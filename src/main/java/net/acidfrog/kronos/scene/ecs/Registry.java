package net.acidfrog.kronos.scene.ecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.util.job.tasks.Task;
import net.acidfrog.kronos.scene.ecs.component.Component;
import net.acidfrog.kronos.scene.ecs.system.EntitySystem;

public final class Registry {

    private static final int DEFAULT_INITIAL_CAPACITY = 48;

    private final List<Entity> entities;
    private final Map<Family, List<Entity>> views;

    private final Deque<Task> tasks;
    private final List<EntitySystem> systems;
    private final List<EntityListener> listeners;
    private final Map<Family, List<EntityListener>> filteredListeners;

    private boolean updating;

    public Registry() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public Registry(final int initialCapacity) {
        this.entities = new ArrayList<Entity>(initialCapacity);
        this.views = new HashMap<Family, List<Entity>>(initialCapacity);
        this.tasks = new LinkedBlockingDeque<Task>();
        this.systems = new ArrayList<EntitySystem>();
        this.listeners = new CopyOnWriteArrayList<EntityListener>();
        this.filteredListeners = new HashMap<Family, List<EntityListener>>();
        this.updating = false;
    }

    public static final Entity create() {
        return new Entity();
    }

    public static final Entity emulate(final Entity entity) {
        return new Entity(entity);
    }

    @SafeVarargs
    public final <T extends Component> Entity emplace(final T... components) {
        Entity entity = new Entity().addAll(components);
        add(entity);
        return entity;
    }
    
    public final void add(final Entity entity) {
        if (updating) tasks.add(() -> { addInternal(entity); });
        else addInternal(entity);
    }

    public final void add(final List<Entity> entities) {
        for (var entity : entities) add(entity);
    }
    
    private void addInternal(Entity entity) {
        if (entity.getRegistry() != null || entities.contains(entity)) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ADDED_TO_ENGINE);
        if (entity.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ENABLED);
        
        entities.add(entity);
        entity.setRegistry(this);
        entity.enable();
        
        for (Family family : views.keySet()) if (family.isMember(entity)) {
            views.get(family).add(entity);
        }

        for (EntityListener l : listeners) l.onEntityAdd(entity);
        
        for (var entry : filteredListeners.entrySet()) if (entry.getKey().isMember(entity)) {
            for (EntityListener l : entry.getValue()) l.onEntityAdd(entity);
        }
    }

    public final void destroy(final Entity entity) {
        if (updating) tasks.add(() -> { removeInternal(entity); });
        else removeInternal(entity);
    }

    public final void destroy(final Entity entity, final boolean immediate) {
        if (immediate) removeInternal(entity);
        else destroy(entity);
    }

    public final void destroy(final List<Entity> entities) {
        for (var entity : entities) destroy(entity);
    }

    private void removeInternal(Entity entity) {
        if (entity.getRegistry() != this) return;
        if (!entity.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ENABLED);
        if (!entities.contains(entity)) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ADDED_TO_THIS_ENGINE);

        // inform listeners as long as the entity is still active
        for (EntityListener l : listeners) l.onEntityRemove(entity);
        
        for (var entry : filteredListeners.entrySet()) {
        	if (entry.getKey().isMember(entity)) {
                for (EntityListener l : entry.getValue()) l.onEntityRemove(entity);
        	}
        }
        
        // actually remove entity
        entity.disable();
        entity.removeRegistry();
        entities.remove(entity);
        entity.flush();
       
        for (List<Entity> view : views.values()) view.remove(entity);
    }

    public final void destroyAll() {
        if (updating) tasks.add(() -> { removeAllInternal(); });
        else removeAllInternal();
    }

    private final void removeAllInternal() {
        while (!entities.isEmpty()) removeInternal(entities.get(0));
    }

    public final Entity release(final Entity entity) {
        if (updating) tasks.add(() -> { releaseInternal(entity); });
        else releaseInternal(entity);
        return entity;
    }

    public final Entity release(final List<Entity> entities) {
        for (var entity : entities) release(entity);
        return entities.get(0);
    }

    private void releaseInternal(Entity entity) {
        if (entity.getRegistry() != this) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_BOUND_TO_ENGINE);
        if (!entity.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ENABLED);
        
        entity.disable();
        entity.removeRegistry();
        entities.remove(entity);
        
        for (Family family : views.keySet()) if (family.isMember(entity)) {
            views.get(family).remove(entity);
        }

        for (EntityListener l : listeners) l.onEntityRemove(entity);
        
        for (var entry : filteredListeners.entrySet()) {
        	if (entry.getKey().isMember(entity)) {
                for (EntityListener l : entry.getValue()) { l.onEntityRemove(entity); }
        	}
        }
    }

    public final void releaseAll() {
        if (updating) tasks.add(() -> { releaseAllInternal(); });
        else releaseAllInternal();
    }

    private void releaseAllInternal() {
        while (!entities.isEmpty()) releaseInternal(entities.get(0));
    }

    public void update(float dt) {
        if (updating) return;
        updating = true;
        
        // update systems
        for (var p : systems) if (p.isEnabled()) {
            p.update(dt);
        }
        
        // execute pending commands
        while (!tasks.isEmpty()) {
            Task t = tasks.poll();
            t.execute();
        }
        
        updating = false;
    }

    public void dispose() {
        if (updating) return;

        for (int i = 0; i < entities.size(); i++) destroy(entities.get(i));

        entities.clear();
        views.clear();

        for (int i = systems.size() - 1; i >= 0; i--) {
            EntitySystem p = systems.get(i);
            p.disable();
            p.onUnbind(this);
            p.unbind();
        }
        
        systems.clear();
    }

    public final Entity get(final int index) {
        return entities.get(index);
    }

    public final boolean has(final Entity entity) {
        return entities.contains(entity);
    }

    public final void bind(final EntitySystem system) {
        if (updating) throw new KronosError(KronosErrorLibrary.ATTEMPTED_SYSTEM_BIND_DURING_UPDATE);
        
        EntitySystem old = getSystem(system.getClass());
        
        if (old != null) unbind(old);
        
        system.bind(this);
        systems.add(system);
        systems.sort(new EntitySystem.SystemComparator());
        system.onBind(this);
    }

    public final void unbind(final EntitySystem system) {
        if (updating) throw new KronosError(KronosErrorLibrary.ATTEMPTED_SYSTEM_UNBIND_DURING_UPDATE);
        if (!systems.contains(system)) throw new KronosError(KronosErrorLibrary.SYSTEM_NOT_BOUND);
        
        system.onUnbind(this);
        systems.remove(system);
        system.unbind();
    }

    public final <T extends EntitySystem> T getSystem(final Class<T> clazz) {
        for (EntitySystem p : systems) if (clazz.isInstance(p)) {
            return clazz.cast(p);
        }
        
        return null;
    }

    public final EntitySystem getSystem(final int index) {
        return systems.get(index);
    }
    
    public final <T extends EntitySystem> boolean hasSystem(final Class<T> clazz) {
        for (EntitySystem p : systems) if (p.getClass() == clazz) {
            return true;
        }

        return false;
    }

    public final boolean hasSystem(final EntitySystem p) {
        return systems.contains(p);
    }

    public final void register(final EntityListener listener, final Family family) {
        List<EntityListener> listeners = filteredListeners.get(family);

        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<EntityListener>();
            filteredListeners.put(family, listeners);
        }

        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void register(final EntityListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void unregister(final EntityListener listener, final Family family) {
        List<EntityListener> listeners = filteredListeners.get(family);

        if (listeners == null) return;
        listeners.remove(listener);
    }
    
    public final void unregister(final EntityListener listener) {
        listeners.remove(listener);
    }

    // TODO: implement sorting
    public final <T extends Component> List<Entity> sort(final Class<T> componentClass) {
        throw new UnsupportedOperationException("Sorting is not yet supported.");
    }

    public final <T extends Component> List<Entity> sort(final Class<T> componentClass, final Comparator<Entity> comparator) {
        throw new UnsupportedOperationException("Sorting is not yet supported.");
    }

    public final List<Entity> view(final Family family) {
        List<Entity> view = views.get(family);

        if (view == null) {
            view = new ArrayList<Entity>();
            views.put(family, view);

            initializeView(family, view);
        }

        return Collections.unmodifiableList(view);
    }

    @SafeVarargs
    public final List<Entity> view(final Class<? extends Component>... components) {
        return view(Family.define(components));
    }

    private void initializeView(Family family, List<Entity> view) {
        if (!view.isEmpty()) return;

        for (var entity : entities) if (family.isMember(entity)) {
            view.add(entity);
        }
    }

    public int size() {
        return entities.size();
    }
    
    public int systemCount() {
        return systems.size();
    }

}

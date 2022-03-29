package net.acidfrog.kronos.scene.ecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.job.tasks.Task;
import net.acidfrog.kronos.scene.ecs.component.Component;
import net.acidfrog.kronos.scene.ecs.system.AbstractEntitySystem;
import net.acidfrog.kronos.scene.ecs.system.EntitySystem;

public class Registry {

    private static final int DEFAULT_INITIAL_CAPACITY = 48;

    private final List<Entity> entities;
    private final Map<Family, List<Entity>> views;

    private final Deque<Task> tasks;
    private final List<AbstractEntitySystem> systems;
    private final List<EntityListener> listeners;
    private final Map<Family, List<EntityListener>> filteredListeners;

    private boolean updating;

    public Registry() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public Registry(int capacity) {
        this.entities = new ArrayList<Entity>(capacity);
        this.views = new HashMap<Family, List<Entity>>(capacity);
        this.tasks = new LinkedBlockingDeque<Task>();
        this.systems = new ArrayList<AbstractEntitySystem>();
        this.listeners = new ArrayList<EntityListener>();
        this.filteredListeners = new HashMap<Family, List<EntityListener>>();
        this.updating = false;
    }

    public final Entity create() {
        Entity e = new Entity();
        add(e);
        return e;
    }
    
    @SafeVarargs
    public final <T extends Component> Entity emplace(final T... components) {
        Entity e = new Entity().addAll(components);
        add(e);
        return e;
    }
    
    public Entity add(Entity e) {
        if (updating) tasks.add(() -> { addInternal(e); });
        else addInternal(e);
        return e;
    }
    
    private void addInternal(Entity e) {
        if (e.getEngine() != null || entities.contains(e)) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ADDED_TO_ENGINE);
        if (e.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ENABLED);
        
        entities.add(e);
        e.setEngine(this);
        e.enable();
        
        for (Family family : views.keySet()) if (family.isMember(e)) {
            views.get(family).add(e);
        }

        for (EntityListener l : listeners) l.onEntityAdd(e);
        
        for (Entry<Family, List<EntityListener>> entry : filteredListeners.entrySet()) {
        	if (entry.getKey().isMember(e)) {
                for (EntityListener l : entry.getValue()) { l.onEntityAdd(e); }
        	}
        }
    }

    public void destroy(Entity e) {
        if (updating) tasks.add(() -> { removeInternal(e); });
        else removeInternal(e);
    }

    public void destroy(List<Entity> entities) {
        for (Entity e : entities) destroy(e);
    }

    private void removeInternal(Entity e) {
        if (e.getEngine() != this) return;
        if (!e.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ENABLED);
        if (!entities.contains(e)) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ADDED_TO_THIS_ENGINE);

        // inform listeners as long as the entity is still active
        for (EntityListener l : listeners) l.onEntityRemove(e);
        
        for (Entry<Family, List<EntityListener>> entry : filteredListeners.entrySet()) {
        	if (entry.getKey().isMember(e)) {
                for (EntityListener l : entry.getValue()) { l.onEntityRemove(e); }
        	}
        }
        
        // actually remove entity
        e.disable();
        e.removeEngine();
        entities.remove(e);
        e.flush();
       
        for (List<Entity> view : views.values()) view.remove(e);
    }

    public void destroyAll() {
        if (updating) tasks.add(() -> { removeAllInternal(); });
        else removeAllInternal();
    }

    private void removeAllInternal() {
        while (!entities.isEmpty()) removeInternal(entities.get(0));
    }

    public Entity release(Entity e) {
        if (updating) tasks.add(() -> { releaseInternal(e); });
        else releaseInternal(e);
        return e;
    }

    public Entity release(List<Entity> entities) {
        for (Entity e : entities) release(e);
        return entities.get(0);
    }

    private void releaseInternal(Entity e) {
        if (e.getEngine() != this) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_BOUND_TO_ENGINE);
        if (!e.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ENABLED);
        
        e.disable();
        e.removeEngine();
        entities.remove(e);
        
        for (Family family : views.keySet()) if (family.isMember(e)) {
            views.get(family).remove(e);
        }

        for (EntityListener l : listeners) l.onEntityRemove(e);
        
        for (Entry<Family, List<EntityListener>> entry : filteredListeners.entrySet()) {
        	if (entry.getKey().isMember(e)) {
                for (EntityListener l : entry.getValue()) { l.onEntityRemove(e); }
        	}
        }
    }

    public void releaseAll() {
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
        for (AbstractEntitySystem p : systems) if (p.isEnabled()) {
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
            AbstractEntitySystem p = systems.get(i);
            p.disable();
            p.onUnbind(this);
            p.unbind();
        }
        
        systems.clear();
    }

    public final List<Entity> view(final Family family) {
        List<Entity> view = views.get(family);

        if (view == null) {
            view = new ArrayList<Entity>();
            views.put(family, view);

            initView(family, view);
        }

        return Collections.unmodifiableList(view);
    }

    @SafeVarargs
    public final List<Entity> view(final Class<? extends Component>... components) {
        return view(Family.define(components));
    }

    public Entity get(int index) {
        return entities.get(index);
    }

    public boolean has(Entity e) {
        return entities.contains(e);
    }
    
    private void initView(Family family, List<Entity> view) {
        if (!view.isEmpty()) return;

        for (Entity e : entities) if (family.isMember(e)) {
            view.add(e);
        }
    }

    public final <T extends Component> List<Entity> sort(final Class<T> clazz) {
        return sort(clazz, new Comparator<Entity>() {

            @Override
            public int compare(Entity e1, Entity e2) {
                return e1.get(clazz).compareTo(e2.get(clazz));
            }
        });
    }

    public final <T extends Component> List<Entity> sort(final Class<T> clazz, final Comparator<Entity> comparator) {
        List<Entity> view = view(clazz);
        Collections.sort(view, comparator);
        return view;
    }

    public void register(EntityListener listener, Family family) {
        List<EntityListener> listeners = filteredListeners.get(family);
        if (listeners == null) {
            listeners = new ArrayList<EntityListener>();
            filteredListeners.put(family, listeners);
        }

        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void register(EntityListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    public void unregister(EntityListener listener, Family family) {
        List<EntityListener> listeners = filteredListeners.get(family);
        if (listeners == null) return;
        listeners.remove(listener);
    }
    
    public void unregister(EntityListener listener) {
        listeners.remove(listener);
    }

    public void bind(AbstractEntitySystem system) throws IllegalStateException, IllegalArgumentException {
        if (updating) throw new IllegalStateException("cannot add system while updating");
        
        EntitySystem old = getSystem(system.getClass());
        
        if (old != null) removeSystem(old);
        
        system.bind(this);
        systems.add(system);
        systems.sort(EntitySystem.getComparator());
        system.onBind(this);
    }

    public void unbind(AbstractEntitySystem system) throws IllegalStateException, IllegalArgumentException {
        if (updating) throw new IllegalStateException("cannot remove system while updating");
        
        if (!systems.contains(system)) throw new IllegalArgumentException("system is unknown");
        
        system.onUnbind(this);
        systems.remove(system);
        system.unbind();        
    }

    public <T extends EntitySystem> T getSystem(Class<T> clazz) throws IllegalArgumentException {
        for (AbstractEntitySystem p : systems) if (clazz.isInstance(p)) {
            return clazz.cast(p);
        }
        
        throw new IllegalArgumentException("system not found " + clazz.getName());
    }
    
    public boolean hasSystem(Class<?> clazz) {
        for (AbstractEntitySystem p : systems) if (p.getClass() == clazz) {
            return true;
        }

        return false;
    }

    public <T extends EntitySystem> T removeSystem(T system) {
        if (updating) throw new IllegalStateException("cannot remove system while updating");
        
        if (!systems.contains(system)) throw new KronosError(KronosErrorLibrary.UNKNOWN_ENTITY_SYSTEM);
        
        systems.remove(system);
        system.unbind();
        return system;
    }

    public AbstractEntitySystem getSystem(int index) {
        return systems.get(index);
    }

    public boolean hasSystem(AbstractEntitySystem p) {
        return systems.contains(p);
    }
    
    public int systemCount() {
        return systems.size();
    }

    public int entityCount() {
        return entities.size();
    }

}

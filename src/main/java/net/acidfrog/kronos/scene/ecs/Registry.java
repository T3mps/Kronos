package net.acidfrog.kronos.scene.ecs;

import java.util.ArrayList;
import java.util.Collection;
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

/**
 * The registry is the central managment system for all {@link Entity entities},
 * their {@link Component components}, and the {@link EntitySystem systems} which
 * process them.
 * 
 * <p>
 * Functionality of the registry include:
 * 
 * <ul>
 *     <li>Creation/insertion/release/removal of {@link Entity entities}</li>
 *     <li>Hashed lookup of {@link Entity entities} by {@link Component component} [via a {@link #view(Family)} | {@link #view(Class...)}]</li>
 *     <li>Binding/unbinding of {@link EntitySystem systems}</li>
 *     <li>Registering/unregistering of {@link EntityListener entity listeners}</li>
 * </ul>
 * 
 * <p>
 * The registry is thread-safe, and can be accessed from any thread.
 * 
 * @apiNote A registry is self-contained, and requires a call to {@link #update(float)} to
 * maintain the systems. An application can have multiple registries, but typically
 * only one is necessary.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public final class Registry {

    /** Holds all entities in the registry */
    private final List<Entity> entities;

    /** {@link Family} to {@link List List&lt;\Entity\&gt; } lookup table */
    private final Map<Family, List<Entity>> views;

    /** A deque of tasks, which are populated with internal tasks attempted during an update, then executed during the next update */
    private final Deque<Task> tasks;

    /** Holds all systems in the registry */
    private final List<EntitySystem> systems;

    /** Holds all entity listeners in the registry */
    private final List<EntityListener> listeners;

    /** Holds all entity listeners that listen to a specific family of entities */
    private final Map<Family, List<EntityListener>> filteredListeners;

    /** Indicates if the registry is in an update cycle */
    private boolean updating;

    /**
     * Default constructor.
     */
    public Registry() {
        this.entities = new ArrayList<Entity>();
        this.views = new HashMap<Family, List<Entity>>();
        this.tasks = new LinkedBlockingDeque<Task>();
        this.systems = new ArrayList<EntitySystem>();
        this.listeners = new CopyOnWriteArrayList<EntityListener>();
        this.filteredListeners = new HashMap<Family, List<EntityListener>>();
        this.updating = false;
    }

    /**
     * Static factory method for creating a new {@link Entity} instance.
     * 
     * <p>
     * The entity is not added to the registry until {@link #add(Entity)} is called.
     * @return A new {@link Entity} instance.
     */
    public static final Entity create() {
        return new Entity();
    }

    /**
     * Static factory method for copying an {@link Entity} instance.
     * 
     * <p>
     * The entity is not added to the registry until {@link #add(Entity)} is called.
     * 
     * @return A new {@link Entity} instance.
     */
    public static final Entity emulate(final Entity entity) {
        return new Entity(entity);
    }

    /**
     * Creates and adds a new {@link Entity} instance to the registry.
     * 
     * <p>
     * Optionally, the entity can be given any number of {@link Component components}.
     * 
     * @param <T> The type(s) of {@link Component component}(s) to add.
     * @param components
     * @return The new {@link Entity} instance.
     */
    @SafeVarargs
    public final <T extends Component> Entity emplace(final T... components) {
        Entity entity = new Entity().addAll(components);
        add(entity);
        return entity;
    }
    
    /**
     * Adds an {@link Entity} to the registry and notifies all {@link EntityListener listeners}
     * of the addition.
     * 
     * <p>
     * The entity is added to the registry, and will be processed by the systems
     * that are currently bound to the registry.
     * 
     * @apiNote An entity will not be added if the registry is currently in an update cycle.
     * A {@link Task} to add the entity will be queued for the next update cycle.
     * 
     * @param entity The entity to add.
     */
    public final void add(final Entity entity) {
        if (updating) tasks.add(() -> { addInternal(entity); });
        else addInternal(entity);
    }

    /**
     * Adds a {@link Collection} {@link Entity entities} to the registry and notifies all
     * {@link EntityListener listeners} of the addition.
     * 
     * <p>
     * The entities are added to the registry, and will be processed by the systems
     * that are currently bound to the registry.
     * 
     * @apiNote An entity will not be added if the registry is currently in an update cycle.
     * A {@link Task} to add the entity will be queued for the next update cycle.
     * 
     * @param entity
     */
    public final void add(final Collection<Entity> entities) {
        for (var entity : entities) add(entity);
    }
    
    /**
     * Internal add method that does not check if the registry is currently in an update cycle.
     * 
     * @param entity
     */
    private void addInternal(Entity entity) {
        if (entity.getRegistry() != null || entities.contains(entity)) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ADDED_TO_ENGINE);
        if (entity.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ENABLED);
        
        entities.add(entity);
        entity.setRegistry(this);
        entity.enable();
        
        for (Family family : views.keySet()) if (family.includes(entity)) {
            views.get(family).add(entity);
        }

        for (EntityListener l : listeners) l.onEntityAdd(entity);
        
        for (var entry : filteredListeners.entrySet()) if (entry.getKey().includes(entity)) {
            for (EntityListener l : entry.getValue()) l.onEntityAdd(entity);
        }
    }

    /**
     * Removes an {@link Entity} from the registry and notifies all {@link EntityListener listeners}
     * of the removal. The entity's components are also removed.
     * 
     * <p>
     * The entity is removed from the registry, and will no longer be processed by any systems.
     * 
     * @apiNote An entity will not be removed if the registry is currently in an update cycle.
     * A {@link Task} to remove the entity will be queued for the next update cycle.
     * 
     * @param entity
     */
    public final void destroy(final Entity entity) {
        if (updating) tasks.add(() -> { removeInternal(entity); });
        else removeInternal(entity);
    }

    /**
     * Removes an {@link Entity} from the registry and notifies all {@link EntityListener listeners}
     * of the removal. The entity's components are also removed.
     * 
     * <p>
     * The entity is removed from the registry, and will no longer be processed by any systems.
     * 
     * @apiNote Flagging the {@code immediate} parameter as true will remove the entity from the registry
     * immediately, without queuing a {@link Task} for the next update cycle.
     * 
     * @param entity
     */
    public final void destroy(final Entity entity, final boolean immediate) {
        if (immediate) removeInternal(entity);
        else destroy(entity);
    }

    /**
     * Removes a {@link Collection} of {@link Entity entities} from the registry and notifies all
     * {@link EntityListener listeners} of the removal. The entity's components are also removed.
     * 
     * <p>
     * The entities are removed from the registry, and will no longer be processed by any systems.
     * 
     * @apiNote An entity will not be removed if the registry is currently in an update cycle.
     * A {@link Task} to remove the entity will be queued for the next update cycle.
     * 
     * @param entities
     */
    public final void destroy(final List<Entity> entities) {
        for (var entity : entities) destroy(entity);
    }

    /**
     * Internal remove method that does not check if the registry is currently in an update cycle.
     * 
     * @param entity
     */
    private void removeInternal(Entity entity) {
        if (entity.getRegistry() != this) return;
        if (!entity.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ENABLED);
        if (!entities.contains(entity)) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ADDED_TO_THIS_ENGINE);

        // inform listeners as long as the entity is still active
        for (EntityListener l : listeners) l.onEntityRemove(entity);
        
        for (var entry : filteredListeners.entrySet()) {
        	if (entry.getKey().includes(entity)) {
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

    /**
     * Removes all {@link Entity entities} from the registry and notifies all {@link EntityListener listeners}
     * of the removal. The entities components are also removed.
     * 
     * <p>
     * The entities are removed from the registry, and will no longer be processed by any systems.
     * 
     * @apiNote An entity will not be removed if the registry is currently in an update cycle.
     * A {@link Task} to remove the entity will be queued for the next update cycle.
     */
    public final void destroyAll() {
        if (updating) tasks.add(() -> { removeAllInternal(); });
        else removeAllInternal();
    }

    /**
     * Internal removeAll method that does not check if the registry is currently in an update cycle.
     */
    private final void removeAllInternal() {
        while (!entities.isEmpty()) removeInternal(entities.get(0));
    }

    /**
     * Removes an {@link Entity} from the registry and notifies all {@link EntityListener listeners}
     * of the removal. The entity's components are not removed.
     * 
     * <p>
     * The entities are removed from the registry, and will no longer be processed by any systems.
     * 
     * @apiNote An entity will not be removed if the registry is currently in an update cycle.
     * A {@link Task} to remove the entity will be queued for the next update cycle.
     * 
     * @param entity
     * @return the entity that was removed
     */
    public final Entity release(final Entity entity) {
        if (updating) tasks.add(() -> { releaseInternal(entity); });
        else releaseInternal(entity);
        return entity;
    }

    /**
     * Removes a {@link Collection} of {@link Entity entities} from the registry and notifies all
     * {@link EntityListener listeners} of the removal. The entity's components are not removed.
     * 
     * <p>
     * The entities are removed from the registry, and will no longer be processed by any systems.
     * 
     * @apiNote An entity will not be removed if the registry is currently in an update cycle.
     * A {@link Task} to remove the entity will be queued for the next update cycle.
     * 
     * @param entities
     */
    public final void release(final List<Entity> entities) {
        for (var entity : entities) release(entity);
    }

    /**
     * Internal release method that does not check if the registry is currently in an update cycle.
     * 
     * @param entity
     */
    private void releaseInternal(Entity entity) {
        if (entity.getRegistry() != this) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_BOUND_TO_ENGINE);
        if (!entity.isEnabled()) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ENABLED);
        
        entity.disable();
        entity.removeRegistry();
        entities.remove(entity);
        
        for (Family family : views.keySet()) if (family.includes(entity)) {
            views.get(family).remove(entity);
        }

        for (EntityListener l : listeners) l.onEntityRemove(entity);
        
        for (var entry : filteredListeners.entrySet()) {
        	if (entry.getKey().includes(entity)) {
                for (EntityListener l : entry.getValue()) { l.onEntityRemove(entity); }
        	}
        }
    }

    /**
     * Removes all {@link Entity entities} from the registry and notifies all {@link EntityListener listeners}
     * of the removal. The entities components are not removed.
     * 
     * <p>
     * The entities are removed from the registry, and will no longer be processed by any systems.
     * 
     * @apiNote An entity will not be removed if the registry is currently in an update cycle.
     * A {@link Task} to remove the entity will be queued for the next update cycle.
     */
    public final void releaseAll() {
        if (updating) tasks.add(() -> { releaseAllInternal(); });
        else releaseAllInternal();
    }

    /**
     * Internal releaseAll method that does not check if the registry is currently in an update cycle.
     */
    private void releaseAllInternal() {
        while (!entities.isEmpty()) releaseInternal(entities.get(0));
    }

    /**
     * Steps each {@link System system} in the registry, and executes the {@link Task tasks}
     * that were queued outside of the last update cycle.
     * 
     * @param dt
     */
    public void update(float dt) {
        if (updating) return;
        updating = true;
        
        for (var s : systems) if (s.isEnabled()) {
            s.update(dt);
        }
        
        while (!tasks.isEmpty()) {
            Task t = tasks.poll();
            t.execute();
        }
        
        updating = false;
    }

    public void uncappedUpdate() {
        if (updating) return;
        updating = true;
        
        for (var s : systems) if (s.isEnabled()) {
            s.update();
        }
        
        updating = false;
    }

    /**
     * Destroys the registry alongside its {@link Entity entities} and {@link System systems}.
     * 
     * @apiNote The registry may not be disposed of if it is currently in an update cycle.
     */
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

    /**
     * Returns the {@link Entity entity} based on its index in the registry.
     * 
     * @param index
     * @return the entity
     */
    public final Entity get(final int index) {
        return entities.get(index);
    }

    /**
     * Determines if the registry contains the specified {@link Entity entity}.
     * 
     * @param entity
     * @return true if the entity is in the registry, false otherwise
     */
    public final boolean contains(final Entity entity) {
        return entities.contains(entity);
    }

    /**
     * Binds the specified {@link EntitySystem system} to the registry. The
     * system will process the appropriate {@link Family families} of {@link Entity entities}
     * automatically.
     * 
     * @param system
     */
    public final void bind(final EntitySystem system) {
        if (updating) throw new KronosError(KronosErrorLibrary.ATTEMPTED_SYSTEM_BIND_DURING_UPDATE);
        
        EntitySystem old = getSystem(system.getClass());
        
        if (old != null) unbind(old);
        
        system.bind(this);
        systems.add(system);
        systems.sort(new EntitySystem.SystemComparator());
        system.onBind(this);
    }

    /**
     * Unbinds the specified {@link EntitySystem system} from the registry.
     * 
     * @param system
     */
    public final void unbind(final EntitySystem system) {
        if (updating) throw new KronosError(KronosErrorLibrary.ATTEMPTED_SYSTEM_UNBIND_DURING_UPDATE);
        if (!systems.contains(system)) throw new KronosError(KronosErrorLibrary.SYSTEM_NOT_BOUND);
        
        system.onUnbind(this);
        systems.remove(system);
        system.unbind();
    }

    /**
     * Returns the {@link EntitySystem system} of the specified type, based on the
     * {@link EntitySystem#getClass() system class}.
     * 
     * @param <T> the system type
     * @param clazz
     * @return the system
     */
    public final <T extends EntitySystem> T getSystem(final Class<T> clazz) {
        for (var s : systems) if (clazz.isInstance(s)) {
            return clazz.cast(s);
        }
        
        return null;
    }

    /**
     * Returns the {@link EntitySystem system} based on its index in the registry.
     * 
     * @param index
     * @return the system
     */
    public final EntitySystem getSystem(final int index) {
        return systems.get(index);
    }
    
    /**
     * Determines if the registry contains the specified {@link EntitySystem system}.
     * 
     * @param <T> the system type
     * @param clazz
     * @return true if the system is in the registry, false otherwise
     */
    public final <T extends EntitySystem> boolean hasSystem(final Class<T> clazz) {
        for (var s : systems) if (s.getClass() == clazz) {
            return true;
        }

        return false;
    }

    /**
     * Determines if the registry contains an instance of a {@link EntitySystem system}.
     * 
     * @param system
     * @return true if the system is in the registry, false otherwise
     */
    public final boolean hasSystem(final EntitySystem system) {
        return systems.contains(system);
    }

    /**
     * Registers a {@link EntityListener listener} to the registry with a {@link Family family}.
     * The listener will only be notified of entities that match the family.
     * 
     * @param listener
     * @param family
     */
    public final void register(final EntityListener listener, final Family family) {
        List<EntityListener> listeners = filteredListeners.get(family);

        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<EntityListener>();
            filteredListeners.put(family, listeners);
        }

        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

     /**
     * Registers a {@link EntityListener listener} to the registry. The listener will be notified
     * of all entities in the registry.
     * 
     * @param listener
     */
    public void register(final EntityListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
    }

    /**
     * Unregisters a {@link EntityListener listener} from the registry that is linked to a {@link Family family}.
     * 
     * @param listener
     */
    public void unregister(final EntityListener listener, final Family family) {
        List<EntityListener> listeners = filteredListeners.get(family);

        if (listeners == null) return;
        listeners.remove(listener);
    }
    
    /**
     * Unregisters a {@link EntityListener listener} from the registry.
     * 
     * @param listener
     */
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

    /**
     * Returns a {@link Collections#unmodifiableList immutable view} of entities that match the specified {@link Family family}.
     * 
     * @param family
     * @return a list of filtered entities
     */
    public final List<Entity> view(final Family family) {
        List<Entity> view = views.get(family);

        if (view == null) {
            view = new ArrayList<Entity>();
            views.put(family, view);

            initializeView(family, view);
        }

        return Collections.unmodifiableList(view);
    }

    /**
     * Returns a {@link Collections#unmodifiableList immutable view} of entities that have the specified {@link Component component(s)}.
     * 
     * @param components
     * @return a list of filtered entities
     */
    @SafeVarargs
    public final List<Entity> view(final Class<? extends Component>... components) {
        return view(Family.define(components));
    }

    private void initializeView(Family family, List<Entity> view) {
        if (!view.isEmpty()) return;

        for (var entity : entities) if (family.includes(entity)) {
            view.add(entity);
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * @return the number of entities in the registry
     */
    public int size() {
        return entities.size();
    }
    
    /**
     * @return the number of systems in the registry
     */
    public int systemCount() {
        return systems.size();
    }

}

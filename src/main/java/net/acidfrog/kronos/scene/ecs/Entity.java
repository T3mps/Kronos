package net.acidfrog.kronos.scene.ecs;

import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.core.datastructure.multiset.Bag;
import net.acidfrog.kronos.core.datastructure.multiset.MultiSet;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.scene.ecs.component.Component;
import net.acidfrog.kronos.scene.ecs.signal.Signal;

/**
 * An entity is unambiguously a collection of {@link Component components}.
 *  
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */

public final class Entity {

    /** Can be used to bitmask an Entity; not used internally */
    public int flags;
    
    /** Reference to the {@link Registry} this entity belongs to */
    private Registry registry;

    /** The {@link Signal} that is dispatched when a component is added to this entity */
    public final Signal<Entity> onComponentAdd;

    /** The {@link Signal} that is dispatched when a component is removed to this entity */
    public final Signal<Entity> onComponentRemove;
    
    /** A {@link MultiSet multiset} which holds the {@link Component components} attached to this entity */
    private MultiSet<Component> components;
    
    /** Internal lookup table mapping {@link Class classes} to {@link Component components} */
    private Map<Class<?>, Component> componentMap;

    /** Indicates if this entity is to be processed */
    private boolean enabled;
    
    /**
     * Default constructor.
     */
    public Entity() {
        this.flags = 0;
        this.registry = null;
        this.onComponentAdd = new Signal<Entity>();
        this.onComponentRemove = new Signal<Entity>();
        this.components = new Bag<Component>();
        this.componentMap = new HashMap<Class<?>, Component>();
        this.enabled = false;
    }

    /**
     * Copy constructor.
     * 
     * @param entity
     */
    public Entity(final Entity entity) {
        this.flags = 0;
        this.registry = entity.registry;
        this.onComponentAdd = entity.onComponentAdd;
        this.onComponentRemove = entity.onComponentRemove;
        this.components = entity.components;
        this.componentMap = entity.componentMap;
        this.enabled = entity.enabled;
    }

    /**
     * Adds a component to this entity.
     * 
     * <p>
     * A {@link Component} may not be added to an entity whilst it is already attached
     * to an entity, or the entity is currently {@link #enabled}.
     * 
     * @param component
     * @return this
     */
    public final Entity add(final Component component) {
        if (component == null) throw new KronosError(KronosErrorLibrary.NULL_COMPONENT);
        if (enabled || registry != null) throw new KronosError(KronosErrorLibrary.COMPONENT_MODIFICATION_WHILE_ENABLED);
        if (component.getParent() != null) throw new KronosError(KronosErrorLibrary.COMPONENT_ALREADY_ATTACHED);
        
        components.add(component);
        component.setParent(this);
        onComponentAdd.dispatch(this);

        if (enabled && !component.isEnabled()) component.enable();

        return this;
    }

    /**
     * Adds all specified {@link Component components} to this entity.
     * 
     * <p>
     * A {@link Component} may not be added to an entity whilst it is already attached
     * to an entity, or the entity is currently {@link #enabled}.
     * 
     * @param components
     * @return this
     */
    public final Entity addAll(final Component... components) {
        for (var c : components) add(c);
        return this;
    }

    /**
     * Adds all of the specified entities' {@link Component components} to this entity.
     * 
     * @param entity
     * @return this
     */
    public final Entity addAll(final Entity entity) {
        for (var c : entity.components) add(c);
        return this;
    }

    /**
     * Queries whether this entity contains the specified {@link Component component}.
     * 
     * @param componentClass
     * @return true if this entity contains the specified component
     */
    public final boolean has(final Class<?> componentClass) {
        if (componentMap.containsKey(componentClass)) return true;

        for (var component : components) if (componentClass.isInstance(component)) {
            return true;
        }

        return false;
    }

    /**
     * Queries whether this entity contains all of the specified {@link Component components}.
     * 
     * @param componentClasses
     * @return true if this entity contains all of the specified components
     */
    public final boolean has(final Class<?>... componentClasses) {
        for (var c : componentClasses) if (!has(c)) return false;
        return true;
    }

    /**
     * Queries whether this entity contains an instance of a {@link Component component}.
     * 
     * @param <T> the subclass of component
     * @param component
     * @return true if this entity contains the instance
     */
    public final <T extends Component> boolean has(final T component) {
        if (component == null) throw new KronosError(KronosErrorLibrary.NULL_COMPONENT);
        if (componentMap.containsKey(component.getClass())) return true;

        for (var c : components) if (component.equals(c)) {
            return true;
        }

        return false;
    }

    /**
     * Returns the {@link Component component} of the specified type if present; null otherwise.
     * 
     * @param <T> the class of the component
     * @param componentClass
     * @return the component of the specified class if it exists, null otherwise
     */
    public final <T extends Component> T get(final Class<T> componentClass) {
        Component com = componentMap.get(componentClass);
        if (com != null) return componentClass.cast(com);
        
        for (var component : components) if (componentClass.isInstance(component)) {
            componentMap.put(componentClass, component);
            return componentClass.cast(component);
        }

        return null;
    }

    /**
     * Removes the and returns specified {@link Component component} from this entity, if present.
     * 
     * @param <T> the class of the component
     * @param componentClass
     * @return the removed component
     */
    public final <T extends Component> T remove(final Class<T> componentClass) {
        if (enabled || registry != null) throw new KronosError(KronosErrorLibrary.COMPONENT_MODIFICATION_WHILE_ENABLED);
        T component = get(componentClass);

        components.remove(component);
        component.setParent(null);
        onComponentRemove.dispatch(this);

        if (enabled && component.isEnabled()) component.disable();

        return componentClass.cast(component);
    }

    /**
     * Removes an instance of a {@link Component component} from this entity, if present.
     * 
     * @param <T> the subclass of component
     * @param component
     * @return the removed component
     */
    public final <T extends Component> T remove(final T component) {
        if (component == null) throw new KronosError(KronosErrorLibrary.NULL_COMPONENT);
        if (enabled || registry != null) throw new KronosError(KronosErrorLibrary.COMPONENT_MODIFICATION_WHILE_ENABLED);
        if (component.getParent() != this || !components.contains(component)) throw new KronosError(KronosErrorLibrary.COMPONENT_NOT_ATTACHED);

        components.remove(component);
        component.setParent(null);
        onComponentRemove.dispatch(this);

        if (enabled && component.isEnabled()) component.disable();

        return component;
    }

    /**
     * Removes all of the specified {@link Component components} from this entity.
     */
    void flush() {
        for (Component component : components) {
            component.setParent(null);
            component.disable();
        }
        
        components.clear();
        componentMap.clear();
    }

    /**
     * @return the {@link Registry} this entity belongs to.
     */
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Sets this entity's {@link Registry}.
     * 
     * @param registry
     * @return this
     */
    public Entity setRegistry(Registry registry) {
        this.registry = registry;
        return this;
    }

    /**
     * Removes this entity's {@link Registry}.
     * 
     * @return this
     */
    public Entity removeRegistry() {
        this.registry = null;
        return this;
    }

    /**
     * @return whether this entity is enabled or not.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables this entity and its {@link Component components}.
     * 
     * @return this
     */
    public Entity enable() {
        if (enabled) return this;
        for (Component component : components) if (!component.isEnabled()) component.enable();
        enabled = true;
        return this;
    }

    /**
     * Disables this entity and its {@link Component components}.
     * 
     * @return this
     */
    public Entity disable() {
        if (!enabled) return this;
        for (Component component : components) if (component.isEnabled()) component.disable();
        enabled = false;
        return this;
    }

    /**
     * @return the {@link Component}s attached to this entity.
     */
    MultiSet<Component> getComponents() {
        return components;
    }

    /**
     * @inheritDoc java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = ((result << 5) - result) + ((componentMap == null) ? 0 : componentMap.hashCode());
        result = ((result << 5) - result) + ((components == null) ? 0 : components.hashCode());
        result = ((result << 5) - result) + (enabled ? 1231 : 1237);
        result = ((result << 5) - result) + flags;
        result = ((result << 5) - result) + ((onComponentAdd == null) ? 0 : onComponentAdd.hashCode());
        result = ((result << 5) - result) + ((onComponentRemove == null) ? 0 : onComponentRemove.hashCode());
        result = ((result << 5) - result) + ((registry == null) ? 0 : registry.hashCode());
        return result;
    }

    /**
     * @inheritDoc java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Entity)) return false;
        Entity other = (Entity) obj;
        if (componentMap == null) {
            if (other.componentMap != null) return false;
        } else if (!componentMap.equals(other.componentMap)) return false;
        if (components == null) {
            if (other.components != null) return false;
        } else if (!components.equals(other.components)) return false;
        if (enabled != other.enabled) return false;
        if (flags != other.flags) return false;
        if (onComponentAdd == null) {
            if (other.onComponentAdd != null) return false;
        } else if (!onComponentAdd.equals(other.onComponentAdd)) return false;
        if (onComponentRemove == null) {
            if (other.onComponentRemove != null) return false;
        } else if (!onComponentRemove.equals(other.onComponentRemove)) return false;
        if (registry == null) {
            if (other.registry != null) return false;
        } else if (!registry.equals(other.registry)) return false;
        return true;
    }

    /**
     * @inheritDoc java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuilder()
        .append("Entity [components=")
        .append(components)
        .append(", flags=")
        .append(flags)
        .append(", enabled=")
        .append(enabled)
        .toString();
    }

}

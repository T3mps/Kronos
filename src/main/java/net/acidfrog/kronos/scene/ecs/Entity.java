package net.acidfrog.kronos.scene.ecs;

import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.core.datastructure.Bag;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.util.UUID;
import net.acidfrog.kronos.scene.ecs.signal.Signal;

public final class Entity {

    public int flags;
    private Engine engine;
    private UUID uuid;
    public final Signal<Entity> onComponentAdd;
    public final Signal<Entity> onComponentRemove;
    
    private Bag<Component> components;
    private Map<Class<?>, Component> componentMap;

    private boolean enabled;
    
    public Entity() {
        this.flags = 0;
        this.engine = null;
        this.uuid = UUID.generate();
        this.onComponentAdd = new Signal<Entity>();
        this.onComponentRemove = new Signal<Entity>();
        this.components = new Bag<Component>();
        this.componentMap = new HashMap<Class<?>, Component>();
        this.enabled = false;
    }

    public Entity addComponent(Component c) {
        if (enabled || engine != null) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ENABLED);
        if (c.getParent() != null) throw new KronosError(KronosErrorLibrary.COMPONENT_ALREADY_ATTACHED);
        
        components.add(c);
        c.setParent(this);
        onComponentAdd();

        if (enabled && !c.isEnabled()) c.enable();

        return this;
    }

    public boolean hasComponent(Class<?> clazz) {
        if (componentMap.containsKey(clazz)) return true;

        for (Component c : components) if (clazz.isInstance(c)) {
            return true;
        }

        return false;
    }

    public <T extends Component> T getComponent(Class<T> clazz) {
        Component com = componentMap.get(clazz);
        if (com != null) return clazz.cast(com);
        
        for (Component c : components) {
            if (clazz.isInstance(c)) {
                componentMap.put(clazz, c);
                return clazz.cast(c);
            }
        }

        throw new KronosError(KronosErrorLibrary.COMPONENT_NOT_FOUND);
    }

    void onComponentAdd() {
        onComponentAdd.dispatch(this);
    }

    public Engine getEngine() {
        return engine;
    }

    public Entity setEngine(Engine engine) {
        this.engine = engine;
        return this;
    }

    public Entity removeEngine() {
        this.engine = null;
        return this;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Entity enable() {
        if (enabled) return this;
        for (Component c : components) if (!c.isEnabled()) c.enable();
        enabled = true;
        return this;
    }

    public Entity disable() {
        if (!enabled) return this;
        for (Component c : components) if (c.isEnabled()) c.disable();
        enabled = false;
        return this;
    }

}

package net.acidfrog.kronos.scene.ecs;
import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.core.util.IDArbiter;
import net.acidfrog.kronos.core.util.UUID;
import net.acidfrog.kronos.core.datastructure.Bag;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.scene.ecs.component.Component;
import net.acidfrog.kronos.scene.ecs.signal.Signal;

public final class Entity {

    private final UUID uuid;
    private final long index;

    private boolean enabled;

    private final Bag<Component> components;
    private final Map<Class<?>, Component> map;

    private final Signal<Entity> onComponentAdd;
    private Engine engine;

    public Entity() {
        this.uuid = UUID.generate();
        this.index = IDArbiter.next();
        this.components = new Bag<Component>();
        this.map = new HashMap<Class<?>, Component>();
        this.onComponentAdd = new Signal<Entity>();
        this.engine = null;
    }

    public Entity addComponent(Component com) {
        if (enabled || engine != null) throw new KronosError(KronosErrorLibrary.ADD_COMPONENT_WHILE_ENABLED);
        if (com.getParent() != null) throw new KronosError(KronosErrorLibrary.COMPONENT_ALREADY_ATTACHED);

        components.add(com);
        com.setParent(this);
        com.enable();
        onComponentAdd.dispatch(this);
        return this;
    }

    public <T extends Component> T getComponent(Class<T> type) {
        Component com = map.get(type);
        if (com != null) return type.cast(com);

        for (Component c : components) if (type.isInstance(c)) {
            map.put(type, c);
            return type.cast(c);
        }

        throw new KronosError(KronosErrorLibrary.COMPONENT_NOT_FOUND);
    }

    public boolean hasComponent(Class<?> type) {
        if (map.containsKey(type)) return true;
        
        for (Component c : components) if (type.isInstance(c)) {
            return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Entity enable() {
        if (enabled) throw new KronosError(KronosErrorLibrary.ENTITY_ALREADY_ENABLED);
        for (Component c : components) if (!c.isEnabled()) c.enable();
        enabled = true;
        return this;
    }

    public Entity disable() {
        if (!enabled) throw new KronosError(KronosErrorLibrary.ENTITY_NOT_ENABLED);
        for (int i = components.size() - 1; i >= 0; --i) {
            Component c = components.get(i);
            if (c.isEnabled()) c.disable();
        }  
        enabled = false;
        return this;
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getIndex() {
        return index;
    }

    public Engine getEngine() {
        return engine;
    }

    public void defineEngine(Engine engine) {
        this.engine = engine;
    }

    public void removeEngine() {
        this.engine = null;
    }
    
}

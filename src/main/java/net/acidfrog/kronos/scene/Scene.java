package net.acidfrog.kronos.scene;

import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.core.util.UUID;
import net.acidfrog.kronos.scene.ecs.Registry;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.EntityListener;

public class Scene {

    private int index;
    private String name;

    private Registry registry;

    private Map<UUID, Entity> entitiesById;

    public Scene(String name, int index) {
        this.name = name;
        this.index = index;
        this.registry = new Registry();
        this.entitiesById = new HashMap<UUID, Entity>();
    }

    public void update(float dt) {
        registry.update(dt);
    }

    public void addEntity(Entity entity) {
        entitiesById.put(entity.getUUID(), entity);
        registry.add(entity);
    }

    public Entity getEntity(UUID uuid) {
        return entitiesById.get(uuid);
    }

    public Entity getEntity(int index) {
        return registry.get(index);
    }

    public void removeEntity(Entity entity) {
        entitiesById.remove(entity.getUUID());
        registry.destroy(entity);
    }

    public void registerListener(EntityListener listener) {
        registry.register(listener);
    }

    public void unregisterListener(EntityListener listener) {
        registry.unregister(listener);
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Registry getEngine() {
        return registry;
    }
    
}

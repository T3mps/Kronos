package net.acidfrog.kronos.scene;

import net.acidfrog.kronos.scene.ecs.Registry;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.EntityListener;

public class Scene {

    private int index;
    private String name;

    private Registry registry;

    public Scene(String name, int index) {
        this.name = name;
        this.index = index;
        this.registry = new Registry();
    }

    public void update(float dt) {
        registry.update(dt);
    }

    public void addEntity(Entity entity) {
        registry.add(entity);
    }

    public Entity getEntity(int index) {
        return registry.get(index);
    }

    public void removeEntity(Entity entity) {
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

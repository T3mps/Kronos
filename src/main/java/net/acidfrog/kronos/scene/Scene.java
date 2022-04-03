package net.acidfrog.kronos.scene;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.acidfrog.kronos.core.lang.Std;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.EntityListener;
import net.acidfrog.kronos.scene.ecs.Registry;
import net.acidfrog.kronos.scene.ecs.component.Component;
import net.acidfrog.kronos.scene.ecs.component.internal.TagComponent;
import net.acidfrog.kronos.scene.ecs.component.internal.TransformComponent;

public class Scene {

    private Registry registry;
    private Map<String, Entity> entities;

    private int index;
    private String name;
    private boolean loaded;

    public Scene(String name, int index) {
        this.registry = new Registry();
        this.entities = new HashMap<String, Entity>();
        this.name = name;
        this.index = index;
        this.loaded = false;
    }

    public void update(float dt) {
        registry.update(dt);
    }

    void close() {
        registry.dispose();
    }

    public RuntimeScene load() {
        if (loaded) return null;
        loaded = true;
        return new RuntimeScene(this);
    }

    @SafeVarargs
    public final <T extends Component> Entity createEntity(String name, T... components) {
        Entity entity = new Entity()
              .add(new TagComponent(name))
              .add(new TransformComponent())
              .addAll(components);
        entities.put(name, entity);
        registry.add(entity);
        return entity;
    }

    public void addEntity(Entity entity) {
        validate(entity);
        entities.put(entity.get(TagComponent.class).getName(), entity);
        registry.add(entity);
    }

    void validate(Entity entity) {
        if (!entity.has(TagComponent.class)) entity.add(new TagComponent(Std.Strings.Generator.randomName()));
        if (!entity.has(TransformComponent.class)) entity.add(new TransformComponent());
    }

    public Entity getEntity(String name) {
        return entities.get(name);
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

    public Registry getRegistry() {
        return registry;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public List<Entity> getEntities() {
        List<Entity> list = registry.getEntities();
        return Collections.unmodifiableList(list);
    }
    
    private class RuntimeScene extends Scene {

        RuntimeScene(Scene scene) {
            super(scene.getName(), scene.getIndex());
            for (var e : scene.getEntities()) addEntity(e);
        }
        
    }
    
}

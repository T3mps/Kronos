package com.starworks.kronos.api.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.starworks.kronos.api.scene.component.IDComponent;
import com.starworks.kronos.api.scene.component.TagComponent;
import com.starworks.kronos.api.scene.component.TransformComponent;
import com.starworks.kronos.core.TimeStep;
import com.starworks.kronos.inferno.Entity;
import com.starworks.kronos.inferno.Registry;
import com.starworks.kronos.inferno.Scheduler;

// TODO: WIP
public final class Scene {
    
    private boolean isRunning;
    private boolean isPaused;
    
    private final Registry registry;
    private final Scheduler scheduler;
    // TODO: private World world; <- Physics

    private final Map<UUID, Entity> entityMap;

    public Scene() {
        this.registry = new Registry();
        this.scheduler = registry.createScheduler();
        this.isRunning = false;
        this.isPaused = false;
        this.entityMap = new HashMap<UUID, Entity>();
    }

    public GameObject createGameObject(String name) {
        return createGameObjectWithUUID(UUID.randomUUID(), name);
    }

    public GameObject createGameObjectWithUUID(UUID uuid, String name) {
        IDComponent idComponent = new IDComponent(uuid);
        TagComponent tagComponent = new TagComponent((name.equals("") || name != null) ? name : "GameObject");
        TransformComponent transformComponent = new TransformComponent();
        Entity entity = registry.emplace(idComponent, tagComponent, transformComponent);
        GameObject gameObject = new GameObject(entity, this);
        entityMap.put(idComponent.uuid(), entity);
        return gameObject;
    }

    public void destroyGameObject(GameObject gameObject) {
        entityMap.remove(gameObject.getUUID().uuid());
        registry.destroy(gameObject.entity);
    }

    public GameObject findGameObjectByUUID(UUID uuid) {
        var entity = entityMap.get(uuid);
        return entity != null ? new GameObject(entity, this) /* TODO: possibly throw an exception instead of returning null */: null;
    }

    public GameObject findGameObjectByName(String name) {
        var entity = registry.view(TagComponent.class).stream().filter(e -> e.component().tag().equals(name)).findFirst().get().entity();
        return entity != null ? new GameObject(entity, this) /* TODO: possibly throw an exception instead of returning null */: null;
    }

    public GameSystem scheduleSystem(GameSystem system) {
        scheduler.schedule(system);
        return system;
    }

    public GameSystem[] scheduleSystems(GameSystem... systems) {
        scheduler.parallelSchedule(systems);
        return systems;
    }

    public void OnUpdate(TimeStep ts) {
    }
}

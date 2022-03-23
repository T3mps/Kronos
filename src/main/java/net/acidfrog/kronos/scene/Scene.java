package net.acidfrog.kronos.scene;

import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;

import net.acidfrog.kronos.core.lang.Std;
import net.acidfrog.kronos.core.lang.UUID;
import net.acidfrog.kronos.core.lang.annotations.Debug;
import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.scene.ecs.Entity;

public class Scene {

    private int width, height;
    private int sceneIndex;
    private final String name;
    private final List<Entity> entities;

    public Scene(String name, int width, int height, boolean useGravity) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.entities = new ArrayList<Entity>();
    }

    public void update(float dt) {
        for (Entity e : entities) e.update(dt);
    }

    public void physicsUpdate(float pdt) {
        for (Entity e : entities) e.physicsUpdate(pdt);
    }

    @Debug
    public void render(Graphics2D g2d) {
    }

    public Entity getEntity(int index) {
        return entities.get(index);
    }
    
    public Entity getEntityByID(UUID id) {
        for (Entity entity : entities) if (entity.getID().equals(id)) return entity;
        Logger.instance.logError("No entity found with id " + id);
        return null;
    }

    public int getEntityIndex(Entity entity) {
        return entities.indexOf(entity);
    }

    public int getEntityIndexByID(String id) {
        for (int i = 0; i < entities.size(); i++) if (entities.get(i).getID().equals(id)) return i;
        Logger.instance.logError("No entity found with id " + id);
        return Std.Arrays.INVALID_INDEX;
    }

    public Scene addEntity(Entity entity) {
        entities.add(entity);
        entity.onEnable();
        return this;
    }

    public void setEntity(int index, Entity entity) {
        entities.set(index, entity);
        entity.onEnable();
    }

    public void removeEntity(Entity entity) {
        entity.onDisable();
        entities.remove(entity);
    }

    public int getEntityCount() {
        return entities.size();
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void close() {
    }

    public String getName() {
        return name;
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getIndex() {
        return sceneIndex;
    }
    
    int setIndex(int index) {
        this.sceneIndex = index;
        return index;
    }

}

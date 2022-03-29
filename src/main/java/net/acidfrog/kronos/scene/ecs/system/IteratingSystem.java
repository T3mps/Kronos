package net.acidfrog.kronos.scene.ecs.system;

import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;
import net.acidfrog.kronos.scene.ecs.Registry;

public abstract class IteratingSystem extends AbstractEntitySystem {

    private Family family;

    private List<Entity> entities;

    public IteratingSystem(Family family) {
        this.family = family;
        this.entities = new ArrayList<Entity>();
    }

    @Override
    public void update(float dt) {
        for (int i = 0; i < entities.size(); i++) process(entities.get(i), dt);
    }

    protected abstract void process(Entity entity, float dt);

    @Override
    public void onBind(Registry registry) {
        entities = registry.view(family);
    }

    @Override
    public void onUnbind(Registry registry) {
        entities = null;
    }

    public List<Entity> getEntities() {
        return entities;
    }
    
}

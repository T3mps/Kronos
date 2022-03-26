package net.acidfrog.kronos.scene.ecs.process;

import java.util.List;

import net.acidfrog.kronos.scene.ecs.Engine;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;

public abstract non-sealed class ComponentProcessor extends EngineProcess {

    private Family family;

    private List<Entity> entities;

    public ComponentProcessor(Family family) {
        this.family = family;
    }
    
    @Override
    public void update(float dt) {
        for (int i = 0; i < entities.size(); i++) {
            process(entities.get(i), dt);
        }
    }

    public abstract void process(Entity entity, float dt);

    @Override
    public void onEngineBind(Engine engine) {
        entities = engine.getMembersOf(family);
    }

    @Override
    public void onEngineUnbind() {
        entities = null;
    }
    
}

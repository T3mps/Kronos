package net.acidfrog.kronos.scene.ecs;

import java.util.List;

public abstract class IteratingSystem extends EngineSystem {

    private Family family;

    private List<Entity> entities;

    public IteratingSystem(Family family) {
        this.family = family;
    }

    @Override
    public void update(float dt) {
        for (int i = 0; i < entities.size(); ++i) {
			processEntity(entities.get(i), dt);
		}
    }

    protected abstract void processEntity(Entity entity, float dt);

    @Override
    public void onEngineBind(Engine e) {
        entities = e.getView(family);
    }

    @Override
    public void onEngineUnbind(Engine e) {
        entities = null;
    }

    public List<Entity> getEntities() {
        return entities;
    }
    
}

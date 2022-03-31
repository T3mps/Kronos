package net.acidfrog.kronos.scene.ecs.system;

import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;
import net.acidfrog.kronos.scene.ecs.Registry;

public abstract class IterativeIntervalSystem extends IntervalSystem {

    private Family family;
    private List<Entity> entities;

    public IterativeIntervalSystem(Family family, float interval) {
        this(family, interval, 0);
    }

    public IterativeIntervalSystem(Family family, float interval, int priority) {
        super(interval, priority);
        this.family = family;
        this.entities = new ArrayList<Entity>();
    }

    @Override
    public void onBind(Registry registry) {
        this.entities = registry.view(family);
    }

    @Override
    protected void intervalUpdate() {
        push();

        for (int i = 0; i < entities.size(); i++) processEntity(entities.get(i));

        pop();
    }

    protected void push() {}

    protected abstract void processEntity(Entity entity);

    protected void pop() {}

    public Family getFamily() {
        return family;
    }

    public List<Entity> getEntities() {
        return entities;
    }
    
}

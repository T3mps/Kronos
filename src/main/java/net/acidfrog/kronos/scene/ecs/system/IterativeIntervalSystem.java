package net.acidfrog.kronos.scene.ecs.system;

import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;
import net.acidfrog.kronos.scene.ecs.Registry;

/**
 * Abstract implementation implementation of {@link EntitySystem}, which operates on
 * filtered list of {@link Entity}s, and updates on a fixed interval.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public abstract class IterativeIntervalSystem extends IntervalSystem {

    /** The family this system operates on */
    private Family family;
    
    /** Holds all entities this system processes */
    private List<Entity> entities;

    /**
     * Creates a new system with the specified {@link Family} and interval.
     * 
     * @param family
     * @param interval
     */
    public IterativeIntervalSystem(Family family, float interval) {
        this(family, interval, 0);
    }

    /**
     * Creates a new system with the specified {@link Family}, interval and
     * priority.
     * 
     * @param family
     * @param interval
     * @param priority
     */
    public IterativeIntervalSystem(Family family, float interval, int priority) {
        super(interval, priority);
        this.family = family;
        this.entities = new ArrayList<Entity>();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onBind(Registry registry) {
        this.entities = registry.view(family);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void intervalUpdate() {
        push();

        for (int i = 0; i < entities.size(); i++) process(entities.get(i));

        pop();
    }

    /**
     * Called directly before the system processes its {@link Entity entities}.
     */
    protected void push() {}

    /**
     * How to handle an {@link Entity} during a step of the system.
     * 
     * @param entity
     */
    protected abstract void process(Entity entity);

    /**
     * Called directly after the system processes its entities.
     */
    protected void pop() {}
 
    /**
     * @return the family associated with this system
     */
    public Family getFamily() {
        return family;
    }

    /**
     * @return the entities contained within this system
     */
    public List<Entity> getEntities() {
        return entities;
    }
    
}

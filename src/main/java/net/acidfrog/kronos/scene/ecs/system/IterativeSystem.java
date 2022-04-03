package net.acidfrog.kronos.scene.ecs.system;

import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;
import net.acidfrog.kronos.scene.ecs.Registry;

/**
 * Abstract implementation implementation of {@link EntitySystem}, which operates on
 * filtered list of {@link Entity}s.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public abstract class IterativeSystem extends AbstractEntitySystem {

    /** The family this system operates on */
    private Family family;

    /** Holds all entities this system processes */
    private List<Entity> entities;

    /**
     * Creates a new system with the specified {@link Family}.
     *  
     * @param family
     */
    public IterativeSystem(Family family) {
        this(family, 0);
    }

    /**
     * Creates a new system with the specified {@link Family} and priority.
     * 
     * @param family
     * @param priority
     */
    public IterativeSystem(Family family, int priority) {
        super(priority);
        this.family = family;
        this.entities = new ArrayList<Entity>();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update(float dt) {
        for (int i = 0; i < entities.size(); i++) process(entities.get(i), dt);
    }

    /**
     * How to handle an {@link Entity} during a step of the system.
     * 
     * @param entity
     * @param dt
     */
    protected abstract void process(Entity entity, float dt);

    /**
     * @inheritDoc
     */
    @Override
    public void onBind(Registry registry) {
        entities = registry.view(family);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onUnbind(Registry registry) {
        entities = null;
    }

    /**
     * @return the entities contained within this system
     */
    public List<Entity> getEntities() {
        return entities;
    }
    
}

package net.acidfrog.kronos.scene.ecs.system;

import java.util.Comparator;
import java.util.List;

import net.acidfrog.kronos.core.datastructure.array.DynamicArray;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.Family;
import net.acidfrog.kronos.scene.ecs.Registry;

/**
 * Abstract implementation implementation of {@link EntitySystem}, which operates on
 * filtered, sorted list of {@link Entity}s.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public abstract class SortedIterativeSystem extends AbstractEntitySystem {

    /** The family this system operates on */
    private Family family;

    /** Holds all entities this system processes */
    private final DynamicArray<Entity> entities;
    
    /** Holds a sorted list of current entities */
    private DynamicArray<Entity> sortedEntities;
    
    /** The method of comparison */
    private Comparator<Entity> entityComparator;
    
    /** Indicates a sort is needed */
    private boolean sort = false;

    /**
     * Creates a new system with the specified {@link Family} and {@link Comparator}.
     * 
     * @param family
     * @param entityComparator
     */
    public SortedIterativeSystem(Family family, Comparator<Entity> entityComparator) {
        this(family, entityComparator, 0);
    }

    /**
     * Creates a new system with the specified {@link Family}, {@link Comparator}, and
     * priority.
     * 
     * @param family
     * @param entityComparator
     * @param priority
     */
    public SortedIterativeSystem(Family family, Comparator<Entity> entityComparator, int priority) {
        super(priority);
        this.family = family;
        this.entities = new DynamicArray<Entity>();
        this.sortedEntities = new DynamicArray<Entity>(16);
        this.sort = false;
        this.entityComparator = entityComparator;
    }

    /**
     * Forces the system to sort the entities.
     */
    public void force() {
        sort = true;
    }

    /**
     * Sorts the entities.
     */
    protected void sort() {
        if (sort) {
            sortedEntities.sort(entityComparator);
            sort = false;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update(float dt) {
        sort();

        push();
        
        for (int i = 0; i < sortedEntities.size(); i++) process(sortedEntities.get(i), dt);

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
    protected abstract void process(Entity entity, float dt);

    /**
     * Called directly after the system processes its entities.
     */
    protected void pop() {}

    /**
     * @inheritDoc
     */
    @Override
    public void onBind(Registry registry) {
        List<Entity> n = registry.view(family);
        sortedEntities.clear();

        if (!n.isEmpty()) {
            for (int i = 0; i < n.size(); i++) sortedEntities.add(n.get(i));
            sortedEntities.sort(entityComparator);
        }

        sort = false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onUnbind(Registry registry) {
        sortedEntities.clear();
        sort = false;
    }

    /**
     * @return the family of entities this system processes.
     */
    public Family getFamily() {
        return family;
    }
    
    /**
     * @return the entities this system processes.
     */
    public DynamicArray<Entity> getEntities() {
        return entities;
    }

}

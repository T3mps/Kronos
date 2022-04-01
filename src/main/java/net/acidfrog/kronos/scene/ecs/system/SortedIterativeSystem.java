package net.acidfrog.kronos.scene.ecs.system;

import java.util.Comparator;
import java.util.List;

import net.acidfrog.kronos.core.datastructure.array.DynamicArray;
import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.EntityListener;
import net.acidfrog.kronos.scene.ecs.Family;
import net.acidfrog.kronos.scene.ecs.Registry;

public abstract class SortedIterativeSystem extends AbstractEntitySystem implements EntityListener {

    private Family family;

    private final DynamicArray<Entity> entities;
    private DynamicArray<Entity> sortedEntities;
    private Comparator<Entity> entityComparator;
    
    private boolean sort = false;

    public SortedIterativeSystem(Family family, Comparator<Entity> entityComparator) {
        this(family, entityComparator, 0);
    }

    public SortedIterativeSystem(Family family, Comparator<Entity> entityComparator, int priority) {
        super(priority);
        this.family = family;
        this.entities = new DynamicArray<Entity>();
        this.sortedEntities = new DynamicArray<Entity>(16);
        this.sort = false;
        this.entityComparator = entityComparator;
    }

    public void force() {
        sort = true;
    }

    protected void sort() {
        if (sort) {
            sortedEntities.sort(entityComparator);
            sort = false;
        }
    }

    @Override
    public void update(float dt) {
        sort();

        push();
        
        for (int i = 0; i < sortedEntities.size(); i++) {
            processEntity(sortedEntities.get(i), dt);
        }

        pop();
    }

    protected void push() {}

    protected abstract void processEntity(Entity entity, float dt);

    protected void pop() {}

    @Override
    public void onBind(Registry registry) {
        List<Entity> n = registry.view(family);
        sortedEntities.clear();

        if (!n.isEmpty()) {
            for (int i = 0; i < n.size(); i++) sortedEntities.add(n.get(i));
            sortedEntities.sort(entityComparator);
        }

        sort = false;
        registry.register(this, family);
    }

    @Override
    public void onUnbind(Registry registry) {
        registry.unregister(this);
        sortedEntities.clear();
        sort = false;
    }

    @Override
    public void onEntityAdd(Entity e) {
        sortedEntities.add(e);
        sort = true;
    }

    @Override
    public void onEntityRemove(Entity e) {
        sortedEntities.remove(e);
        sort = true;
    }

    public Family getFamily() {
        return family;
    }
    
    public DynamicArray<Entity> getEntities() {
        return entities;
    }

}

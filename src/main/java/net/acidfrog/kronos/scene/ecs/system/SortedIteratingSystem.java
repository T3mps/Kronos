package net.acidfrog.kronos.scene.ecs.system;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.acidfrog.kronos.scene.ecs.Entity;
import net.acidfrog.kronos.scene.ecs.EntityListener;
import net.acidfrog.kronos.scene.ecs.Family;
import net.acidfrog.kronos.scene.ecs.Registry;

public abstract class SortedIteratingSystem extends AbstractEntitySystem implements EntityListener {

    private Family family;
    private final List<Entity> entities;
    private List<Entity> sortedEntities;
    private boolean sort = false;
    private Comparator<Entity> entityComparator;

    public SortedIteratingSystem(Family family, Comparator<Entity> entityComparator) {
        this(family, entityComparator, 0);
    }

    public SortedIteratingSystem(Family family, Comparator<Entity> entityComparator, int priority) {
        super(priority);
        this.family = family;
        this.entities = new ArrayList<Entity>();
        this.sortedEntities = new ArrayList<Entity>(16);
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
    
    public List<Entity> getEntities() {
        return entities;
    }

}

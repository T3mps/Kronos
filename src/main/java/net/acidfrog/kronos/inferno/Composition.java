package net.acidfrog.kronos.inferno;

import java.util.Arrays;
import java.util.Iterator;

import net.acidfrog.kronos.crates.pool.ChunkedPool;
import net.acidfrog.kronos.crates.pool.ArrayPool;
import net.acidfrog.kronos.crates.pool.ChunkedPool.IDSchema;

public final class Composition {
    
    public static final int COMPONENT_INDEX_CAPACITY = 1 << 10;

    private final Registry repository;
    private final ChunkedPool.PooledNode<Entity> pooledNode;
    private final ArrayPool arrayPool;
    private final ClassIndex classIndex;
    private final IDSchema idSchema;
    private final Class<?>[] componentTypes;
    private final int[] componentIndices;

    public Composition(Registry repository, ChunkedPool.PooledNode<Entity> pooledNode, ArrayPool arrayPool, ClassIndex classIndex, IDSchema idSchema, Class<?>... componentTypes) {
        this.repository = repository;
        this.pooledNode = pooledNode;
        this.arrayPool = arrayPool;
        this.classIndex = classIndex;
        this.idSchema = idSchema;
        this.componentTypes = componentTypes;
        if (isMultiComponent()) {
            this.componentIndices = new int[COMPONENT_INDEX_CAPACITY];
            Arrays.fill(componentIndices, -1);

            for (int i = 0; i < componentTypes.length; i++) {
                this.componentIndices[classIndex.getIndex(componentTypes[i])] = i;
            }
        } else {
            this.componentIndices = null;
        }
    }
    
    public boolean isMultiComponent() {
        return componentTypes.length > 1;
    }

    public int getComponentIndex(Class<?> componentType) {
        return componentIndices[classIndex.getIndex(componentType)];
    }

    public Object[] sortComponentsByIndex(Object[] components) {
        int newIndex;

        for (int i = 0; i < components.length; i++) {
            newIndex = getComponentIndex(components[i].getClass());

            if (newIndex != i) {
                swapComponents(components, i, newIndex);
            }
        }

        newIndex = getComponentIndex(components[0].getClass());
        
        if (newIndex > 0) {
            swapComponents(components, 0, newIndex);
        }
        return components;
    }

    private void swapComponents(Object[] components, int i, int newIndex) {
        Object temp = components[newIndex];
        components[newIndex] = components[i];
        components[i] = temp;
    }

    protected Entity createEntity(boolean prepared, Object... components) {
        int id = pooledNode.nextID();
        return pooledNode.register(id, new Entity(id, this, !prepared && isMultiComponent() ? sortComponentsByIndex(components) : components));
    }

    protected boolean deleteEntity(Entity entity) {
        detachEntity(entity);
        Object[] components = entity.getComponents();

        if (components != null && entity.isPooledArray()) {
            arrayPool.push(components);
        }

        entity.setData(null);
        return true;
    }

    protected Entity attachEntity(Entity entity, boolean prepared, Object... components) {
        entity = pooledNode.register(entity.setID(pooledNode.nextID()), switch (componentTypes.length) {
            case 00 -> entity.setData(new Entity.ComponentData(this, null));
            case 01 -> entity.setData(new Entity.ComponentData(this, components));
            default -> entity.setData(new Entity.ComponentData(this, prepared ? components : sortComponentsByIndex(components)));
        });

        return entity;
    }

    protected void reattachEntity(Entity entity) {
        pooledNode.register(entity.setID(pooledNode.nextID()), entity);
    }

    protected Entity detachEntity(Entity entity) {
        pooledNode.freeID(entity.getID());
        entity.flagDetachedID();
        return entity;
    }

    public int size() {
        return componentTypes.length;
    }

    public Class<?>[] getComponentTypes() {
        return componentTypes;
    }

    public Registry getRepository() {
        return repository;
    }

    public ChunkedPool.PooledNode<Entity> getNode() {
        return pooledNode;
    }

    public IDSchema getIDSchema() {
        return idSchema;
    }
    
    @Override
    public String toString() {
        int iMax = componentTypes.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0;; i++) {
            sb.append(componentTypes[i].getSimpleName());

            if (i == iMax) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }

    public <T> Iterator<Group.With1<T>> select(Class<T> componentType, Iterator<Entity> iterator) {
        int index = componentIndices == null ? 0 : getComponentIndex(componentType);
        return new IteratorWith1<T>(index, iterator, this);
    }

    public <T1, T2> Iterator<Group.With2<T1, T2>> select(Class<T1> componentType1, Class<T2> componentType2, Iterator<Entity> iterator) {
        return new IteratorWith2<T1, T2>(getComponentIndex(componentType1), getComponentIndex(componentType2), iterator, this);
    }

    public <T1, T2, T3> Iterator<Group.With3<T1, T2, T3>> select(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Iterator<Entity> iterator) {
        return new IteratorWith3<T1, T2, T3>(getComponentIndex(componentType1), getComponentIndex(componentType2), getComponentIndex(componentType3), iterator, this);
    }

    public <T1, T2, T3, T4> Iterator<Group.With4<T1, T2, T3, T4>> select(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Iterator<Entity> iterator) {
        return new IteratorWith4<T1, T2, T3, T4>(getComponentIndex(componentType1), getComponentIndex(componentType2), getComponentIndex(componentType3), getComponentIndex(componentType4), iterator, this);
    }

    public <T1, T2, T3, T4, T5> Iterator<Group.With5<T1, T2, T3, T4, T5>> select(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Iterator<Entity> iterator) {
        return new IteratorWith5<T1, T2, T3, T4, T5>(getComponentIndex(componentType1), getComponentIndex(componentType2), getComponentIndex(componentType3), getComponentIndex(componentType4), getComponentIndex(componentType5), iterator, this);
    }

    public <T1, T2, T3, T4, T5, T6> Iterator<Group.With6<T1, T2, T3, T4, T5, T6>> select(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<T6> componentType6, Iterator<Entity> iterator) {
        return new IteratorWith6<T1, T2, T3, T4, T5, T6>(getComponentIndex(componentType1), getComponentIndex(componentType2), getComponentIndex(componentType3), getComponentIndex(componentType4), getComponentIndex(componentType5), getComponentIndex(componentType6), iterator, this);
    }

    public record IteratorWith1<T>(int index, Iterator<Entity> iterator, Composition composition) implements Iterator<Group.With1<T>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Group.With1<T> next() {
            Entity intEntity;
            Entity.ComponentData data;

            while ((data = (intEntity = iterator.next()).getData()) == null || data.composition() != composition) ;
            
            Object[] components = data.components();
            return new Group.With1<T>((T) components[index], intEntity);
        }
    }

    public record IteratorWith2<T1, T2>(int index1, int index2, Iterator<Entity> iterator, Composition composition) implements Iterator<Group.With2<T1, T2>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Group.With2<T1, T2> next() {
            Entity intEntity;
            Entity.ComponentData data;

            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;

            Object[] components = data.components();
            return new Group.With2<T1, T2>((T1) components[index1], (T2) components[index2], intEntity);
        }
    }

    public record IteratorWith3<T1, T2, T3>(int index1, int index2, int index3, Iterator<Entity> iterator, Composition composition) implements Iterator<Group.With3<T1, T2, T3>> {
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Group.With3<T1, T2, T3> next() {
            Entity intEntity;
            Entity.ComponentData data;

            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;

            Object[] components = data.components();
            return new Group.With3<T1, T2, T3>((T1) components[index1], (T2) components[index2], (T3) components[index3], intEntity);
        }
    }

    public record IteratorWith4<T1, T2, T3, T4>(int index1, int index2, int index3, int index4, Iterator<Entity> iterator, Composition composition) implements Iterator<Group.With4<T1, T2, T3, T4>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Group.With4<T1, T2, T3, T4> next() {
            Entity intEntity;
            Entity.ComponentData data;

            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;

            Object[] components = data.components();
            return new Group.With4<T1, T2, T3, T4>((T1) components[index1], (T2) components[index2], (T3) components[index3], (T4) components[index4], intEntity);
        }
    }

    public record IteratorWith5<T1, T2, T3, T4, T5>(int index1, int index2, int index3, int index4, int index5, Iterator<Entity> iterator, Composition composition) implements Iterator<Group.With5<T1, T2, T3, T4, T5>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Group.With5<T1, T2, T3, T4, T5> next() {
            Entity intEntity;
            Entity.ComponentData data;

            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;
            Object[] components = data.components();

            return new Group.With5<T1, T2, T3, T4, T5>((T1) components[index1], (T2) components[index2], (T3) components[index3], (T4) components[index4], (T5) components[index5], intEntity);
        }
    }

    public record IteratorWith6<T1, T2, T3, T4, T5, T6>(int index1, int index2, int index3, int index4, int index5, int index6, Iterator<Entity> iterator, Composition composition) implements Iterator<Group.With6<T1, T2, T3, T4, T5, T6>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Group.With6<T1, T2, T3, T4, T5, T6> next() {
            Entity intEntity;
            Entity.ComponentData data;

            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;

            Object[] components = data.components();
            return new Group.With6<T1, T2, T3, T4, T5, T6>((T1) components[index1], (T2) components[index2], (T3) components[index3], (T4) components[index4], (T5) components[index5], (T6) components[index6], intEntity);
        }
    }
}

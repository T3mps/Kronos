package net.acidfrog.kronos.inferno;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

import net.acidfrog.kronos.crates.pool.ChunkedPool;
import net.acidfrog.kronos.crates.pool.ObjectArrayPool;
import net.acidfrog.kronos.crates.pool.ChunkedPool.IDSchema;
import net.acidfrog.kronos.inferno.core.ClassIndex;
import net.acidfrog.kronos.inferno.core.IndexKey;

public final class DataComposition {
    
    public static final int COMPONENT_INDEX_CAPACITY = 1 << 10;

    private final CompositionRepository repository;
    private final ChunkedPool.Tenant<IntEntity> tenant;
    private final ObjectArrayPool arrayPool;
    private final ClassIndex classIndex;
    private final IDSchema idSchema;
    private final Class<?>[] componentTypes;
    private final int[] componentIndices;
    private final Map<IndexKey, IntEntity> states;
    private final StampedLock stateLock;

    public DataComposition(CompositionRepository repository, ChunkedPool.Tenant<IntEntity> tenant, ObjectArrayPool arrayPool, ClassIndex classIndex, IDSchema idSchema, Class<?>... componentTypes) {
        this.repository = repository;
        this.tenant = tenant;
        this.arrayPool = arrayPool;
        this.classIndex = classIndex;
        this.idSchema = idSchema;
        this.componentTypes = componentTypes;
        if (isMultiComponent()) {
            this.componentIndices = new int[COMPONENT_INDEX_CAPACITY];
            Arrays.fill(componentIndices, -1);

            for (int i = 0; i < length(); i++) {
                this.componentIndices[classIndex.getIndex(componentTypes[i])] = i;
            }
        } else {
            this.componentIndices = null;
        }
        this.states = new ConcurrentHashMap<IndexKey, IntEntity>();
        this.stateLock = new StampedLock();
    }

    public static <S extends Enum<S>> IndexKey computeIndexKey(S state, ClassIndex classIndex) {
        int index = classIndex.getIndex(state.getClass());
        index = index == 0 ? classIndex.getIndexOrAddClass(state.getClass()) : index;
        return new IndexKey(new int[] { index, state.ordinal() });
    }

    public int length() {
        return componentTypes.length;
    }

    public boolean isMultiComponent() {
        return componentTypes.length > 1;
    }

    public int fetchComponentIndex(Class<?> componentType) {
        return componentIndices[classIndex.getIndex(componentType)];
    }

    public Object[] sortComponentsByIndex(Object[] components) {
        int newIndex;

        for (int i = 0; i < components.length; i++) {
            newIndex = fetchComponentIndex(components[i].getClass());

            if (newIndex != i) {
                swapComponents(components, i, newIndex);
            }
        }

        newIndex = fetchComponentIndex(components[0].getClass());
        
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

    protected IntEntity createEntity(boolean prepared, Object... components) {
        int id = tenant.nextID();
        return tenant.register(id, new IntEntity(id, this, !prepared && isMultiComponent() ? sortComponentsByIndex(components) : components));
    }

    protected boolean deleteEntity(IntEntity entity) {
        detachEntity(entity);
        Object[] components = entity.getComponents();

        if (components != null && entity.isPooledArray()) {
            arrayPool.push(components);
        }
        if (entity.getPrev() != null || entity.getNext() != null) {
            detachEntityState(entity);
        }

        entity.setData(null);
        return true;
    }

    protected IntEntity attachEntity(IntEntity entity, boolean prepared, Object... components) {
        entity = tenant.register(entity.setID(tenant.nextID()), switch (componentTypes.length) {
            case 00 -> entity.setData(new IntEntity.Data(this, null, entity.getData()));
            case 01 -> entity.setData(new IntEntity.Data(this, components, entity.getData()));
            default -> entity.setData(new IntEntity.Data(this, prepared ? components : sortComponentsByIndex(components), entity.getData()));
        });

        return entity;
    }

    protected void reattachEntity(IntEntity entity) {
        tenant.register(entity.setID(tenant.nextID()), entity);
    }

    protected IntEntity detachEntity(IntEntity entity) {
        tenant.freeID(entity.getID());
        entity.flagDetachedID();
        return entity;
    }

    public <S extends Enum<S>> IntEntity setEntityState(IntEntity entity, S state) {
        detachEntityState(entity);
        
        if (state != null) {
            attachEntityState(entity, state);
        }
        return entity;
    }

    private boolean detachEntityState(IntEntity entity) {
        IndexKey key = entity.getData().stateRoot();
        // if entity is root

        if (key != null) {
            // if alone
            if (entity.getPrev() == null) {
                if (states.remove(key) != null) {
                    entity.setData(new IntEntity.Data(this, entity.getComponents(), (IntEntity.Data) null));
                    return true;
                }
            } else {
                IntEntity prev = (IntEntity) entity.getPrev();

                if (states.replace(key, entity, prev)) {
                    prev.setNext(null);
                    prev.setData(new IntEntity.Data(this, prev.getComponents(), entity.getData()));
                    entity.setPrev(null);
                    entity.setData(new IntEntity.Data(this, entity.getComponents(), (IntEntity.Data) null));
                    return true;
                }
            }
        } else if (entity.getNext() != null) {
            long stamp = stateLock.writeLock();
            try {
                IntEntity prev, next;
                if ((next = (IntEntity) entity.getNext()) != null) {
                    if ((prev = (IntEntity) entity.getPrev()) != null) {
                        prev.setNext(next);
                        next.setPrev(prev);
                    } else {
                        next.setPrev(null);
                    }
                }
                entity.setPrev(null);
                entity.setNext(null);
                return true;
            } finally {
                stateLock.unlockWrite(stamp);
            }
        }
        return false;
    }

    private <S extends Enum<S>> void attachEntityState(IntEntity entity, S state) {
        IndexKey indexKey = computeIndexKey(state, classIndex);
        IntEntity.Data entityData = entity.getData();
        IntEntity prev = states.computeIfAbsent(indexKey, k -> entity.setData(new IntEntity.Data(this, entityData.components(), k)));

        if (prev != entity) {
            states.computeIfPresent(indexKey, (k, oldEntity) -> {
                entity.setPrev(oldEntity);
                entity.setData(new IntEntity.Data(this, entityData.components(), k));
                oldEntity.setNext(entity);
                IntEntity.Data oldEntityData = oldEntity.getData();
                oldEntity.setData(new IntEntity.Data(this, oldEntityData.components(), (IntEntity.Data) null));
                return entity;
            });
        }
    }

    public Class<?>[] getComponentTypes() {
        return componentTypes;
    }

    public CompositionRepository getRepository() {
        return repository;
    }

    public ChunkedPool.Tenant<IntEntity> getTenant() {
        return tenant;
    }

    public Map<IndexKey, IntEntity> getStates() {
        return Collections.unmodifiableMap(states);
    }

    public IntEntity getStateRootEntity(IndexKey key) {
        return states.get(key);
    }

    public IDSchema getIDSchema() {
        return idSchema;
    }

    @Override
    public String toString() {
        int iMax = componentTypes.length - 1;
        if (iMax == -1) {
            return "Composition=[]";
        }
        StringBuilder sb = new StringBuilder("Composition=[");
        for (int i = 0;; i++) {
            sb.append(componentTypes[i].getSimpleName());

            if (i == iMax) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
    }

    public <T> Iterator<Group.With1<T>> select(Class<T> componentType, Iterator<IntEntity> iterator) {
        int index = componentIndices == null ? 0 : fetchComponentIndex(componentType);
        return new IteratorWith1<T>(index, iterator, this);
    }

    public <T1, T2> Iterator<Group.With2<T1, T2>> select(Class<T1> componentType1, Class<T2> componentType2, Iterator<IntEntity> iterator) {
        return new IteratorWith2<T1, T2>(fetchComponentIndex(componentType1), fetchComponentIndex(componentType2), iterator, this);
    }

    public <T1, T2, T3> Iterator<Group.With3<T1, T2, T3>> select(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Iterator<IntEntity> iterator) {
        return new IteratorWith3<T1, T2, T3>(fetchComponentIndex(componentType1), fetchComponentIndex(componentType2), fetchComponentIndex(componentType3), iterator, this);
    }

    public <T1, T2, T3, T4> Iterator<Group.With4<T1, T2, T3, T4>> select(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Iterator<IntEntity> iterator) {
        return new IteratorWith4<T1, T2, T3, T4>(fetchComponentIndex(componentType1), fetchComponentIndex(componentType2), fetchComponentIndex(componentType3), fetchComponentIndex(componentType4), iterator, this);
    }

    public <T1, T2, T3, T4, T5> Iterator<Group.With5<T1, T2, T3, T4, T5>> select(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Iterator<IntEntity> iterator) {
        return new IteratorWith5<T1, T2, T3, T4, T5>(fetchComponentIndex(componentType1), fetchComponentIndex(componentType2), fetchComponentIndex(componentType3), fetchComponentIndex(componentType4), fetchComponentIndex(componentType5), iterator, this);
    }

    public <T1, T2, T3, T4, T5, T6> Iterator<Group.With6<T1, T2, T3, T4, T5, T6>> select(Class<T1> componentType1, Class<T2> componentType2, Class<T3> componentType3, Class<T4> componentType4, Class<T5> componentType5, Class<T6> componentType6, Iterator<IntEntity> iterator) {
        return new IteratorWith6<T1, T2, T3, T4, T5, T6>(fetchComponentIndex(componentType1), fetchComponentIndex(componentType2), fetchComponentIndex(componentType3), fetchComponentIndex(componentType4), fetchComponentIndex(componentType5), fetchComponentIndex(componentType6), iterator, this);
    }

    public Iterator<IntEntity> entityStateIterator(IntEntity rootEntity) {
        return new StateIterator(rootEntity);
    }

    private class StateIterator implements Iterator<IntEntity> {

        private IntEntity next;

        private StateIterator(IntEntity rootEntity) {
            next = rootEntity;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public IntEntity next() {
            var current = next;
            next = (IntEntity) next.getPrev();
            return current;
        }
    }

    public record IteratorWith1<T>(int index, Iterator<IntEntity> iterator, DataComposition composition) implements Iterator<Group.With1<T>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public Group.With1<T> next() {
            IntEntity intEntity;
            IntEntity.Data data;
            while ((data = (intEntity = iterator.next()).getData()) == null || data.composition() != composition) ;
            Object[] components = data.components();
            return new Group.With1<T>((T) components[index], intEntity);
        }
    }

    public record IteratorWith2<T1, T2>(int idx1, int idx2, Iterator<IntEntity> iterator, DataComposition composition) implements Iterator<Group.With2<T1, T2>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public Group.With2<T1, T2> next() {
            IntEntity intEntity;
            IntEntity.Data data;
            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;
            Object[] components = data.components();
            return new Group.With2<T1, T2>((T1) components[idx1], (T2) components[idx2], intEntity);
        }
    }

    public record IteratorWith3<T1, T2, T3>(int idx1, int idx2, int idx3, Iterator<IntEntity> iterator, DataComposition composition) implements Iterator<Group.With3<T1, T2, T3>> {
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public Group.With3<T1, T2, T3> next() {
            IntEntity intEntity;
            IntEntity.Data data;
            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;
            Object[] components = data.components();
            return new Group.With3<T1, T2, T3>(
                    (T1) components[idx1],
                    (T2) components[idx2],
                    (T3) components[idx3],
                    intEntity);
        }
    }

    public record IteratorWith4<T1, T2, T3, T4>(int idx1, int idx2, int idx3, int idx4, Iterator<IntEntity> iterator, DataComposition composition) implements Iterator<Group.With4<T1, T2, T3, T4>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public Group.With4<T1, T2, T3, T4> next() {
            IntEntity intEntity;
            IntEntity.Data data;
            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;
            Object[] components = data.components();
            return new Group.With4<T1, T2, T3, T4>(
                    (T1) components[idx1],
                    (T2) components[idx2],
                    (T3) components[idx3],
                    (T4) components[idx4],
                    intEntity);
        }
    }

    public record IteratorWith5<T1, T2, T3, T4, T5>(int idx1, int idx2, int idx3, int idx4, int idx5, Iterator<IntEntity> iterator, DataComposition composition) implements Iterator<Group.With5<T1, T2, T3, T4, T5>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public Group.With5<T1, T2, T3, T4, T5> next() {
            IntEntity intEntity;
            IntEntity.Data data;
            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;
            Object[] components = data.components();
            return new Group.With5<T1, T2, T3, T4, T5>(
                    (T1) components[idx1],
                    (T2) components[idx2],
                    (T3) components[idx3],
                    (T4) components[idx4],
                    (T5) components[idx5],
                    intEntity);
        }
    }

    public record IteratorWith6<T1, T2, T3, T4, T5, T6>(int idx1, int idx2, int idx3, int idx4, int idx5, int idx6, Iterator<IntEntity> iterator, DataComposition composition) implements Iterator<Group.With6<T1, T2, T3, T4, T5, T6>> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public Group.With6<T1, T2, T3, T4, T5, T6> next() {
            IntEntity intEntity;
            IntEntity.Data data;
            while ((data = (intEntity = iterator.next()).getData()).composition() != composition) ;
            Object[] components = data.components();
            return new Group.With6<T1, T2, T3, T4, T5, T6>(
                    (T1) components[idx1],
                    (T2) components[idx2],
                    (T3) components[idx3],
                    (T4) components[idx4],
                    (T5) components[idx5],
                    (T6) components[idx6],
                    intEntity);
        }
    }
}

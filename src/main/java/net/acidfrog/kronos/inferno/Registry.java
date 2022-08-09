package net.acidfrog.kronos.inferno;

import java.util.Arrays;

public final class Registry {

    private static int UNNAMED_COUNT = 0;

    public static final int DEFAULT_SYSTEM_TIMEOUT_SECONDS = 3;

    private final String name;
    private final CompositionRepository compositions;
    private final int systemTimeoutSeconds;

    public Registry() {
        this("registry (" + UNNAMED_COUNT++ + ")");
    }

    public Registry(String name) {
        this(name, CompositionRepository.DEFAULT_CLASS_INDEX_BIT, CompositionRepository.DEFAULT_CHUNK_BIT, CompositionRepository.DEFAULT_CHUNK_COUNT_BIT, DEFAULT_SYSTEM_TIMEOUT_SECONDS);
    }

    public Registry(String name, int classIndexBit, int chunkBit, int chunkCountBit, int systemTimeoutSeconds) {
        this.name = name;
        this.compositions = new CompositionRepository(classIndexBit, chunkBit, chunkCountBit);
        this.systemTimeoutSeconds = systemTimeoutSeconds;
    }

    public String getName() {
        return name;
    }

    public Entity create(Object... components) {
        Object[] componentArray = components.length == 0 ? null : components;
        DataComposition composition = compositions.getOrCreate(componentArray);
        Entity entity = composition.createEntity(false, componentArray);
        return entity;
    }

    public Entity emplace(Object... components) {
        if (components.length == 0) {
            throw new IllegalArgumentException("Cannot emplace an entity with no components");
        }

        DataComposition composition = compositions.getOrCreate(components);
        Entity entity = composition.createEntity(true, components);
        return entity;
    }

    public Entity emplace(Transmute.OfTypes withValues) {
        DataComposition composition = (DataComposition) withValues.getContext();
        return composition.createEntity(true, withValues.getComponents());
    }

    public Entity emulate(Entity prefab, Object... components) {
        IntEntity origin = (IntEntity) prefab;
        Object[] originComponents = origin.getComponents();
        if (originComponents == null || originComponents.length == 0) {
            return create(components);
        }
        Object[] targetComponents = Arrays.copyOf(originComponents, originComponents.length + components.length);
        System.arraycopy(components, 0, targetComponents, originComponents.length, components.length);
        return create(targetComponents);
    }

    public boolean delete(Entity entity) {
        return ((IntEntity) entity).delete();
    }

    public boolean modifyEntity(Transmute.Modifier modifier) {
        var mod = (Composition.NewEntityComposition) modifier.getModifier();
        return mod == null ? false : mod.entity().modify(compositions, mod.dataComposition(), mod.components());
    }

    public Composition composition() {
        return compositions.getPreparedComposition();
    }

    public Scheduler createScheduler() {
        return new Scheduler(systemTimeoutSeconds);
    }

    public <T> Group<Group.With1<T>> view(Class<T> type) {
        var nodes = compositions.find(type);
        return new View.With1<>(compositions, nodes, type);
    }

    public <T1, T2> Group<Group.With2<T1, T2>> view(Class<T1> type1, Class<T2> type2) {
        var nodes = compositions.find(type1, type2);
        return new View.With2<>(compositions, nodes, type1, type2);
    }

    public <T1, T2, T3> Group<Group.With3<T1, T2, T3>> view(Class<T1> type1, Class<T2> type2, Class<T3> type3) {
        var nodes = compositions.find(type1, type2, type3);
        return new View.With3<>(compositions, nodes, type1, type2, type3);
    }

    public <T1, T2, T3, T4> Group<Group.With4<T1, T2, T3, T4>> view(Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        var nodes = compositions.find(type1, type2, type3, type4);
        return new View.With4<>(compositions, nodes, type1, type2, type3, type4);
    }

    public <T1, T2, T3, T4, T5> Group<Group.With5<T1, T2, T3, T4, T5>> view(Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5) {
        var nodes = compositions.find(type1, type2, type3, type4, type5);
        return new View.With5<>(compositions, nodes, type1, type2, type3, type4, type5);
    }

    public <T1, T2, T3, T4, T5, T6> Group<Group.With6<T1, T2, T3, T4, T5, T6>> view(Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
        var nodes = compositions.find(type1, type2, type3, type4, type5, type6);
        return new View.With6<>(compositions, nodes, type1, type2, type3, type4, type5, type6);
    }

    public void close() {
        compositions.close();
    }
}

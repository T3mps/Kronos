package net.acidfrog.kronos.inferno;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.StampedLock;

import net.acidfrog.kronos.crates.pool.ChunkedPool;
import net.acidfrog.kronos.crates.pool.ArrayPool;
import net.acidfrog.kronos.crates.pool.ChunkedPool.IDSchema;
import net.acidfrog.kronos.inferno.Transmute.NewEntityComposition;

public final class Registry implements AutoCloseable {

    private static final int DEFAULT_SYSTEM_TIMEOUT_SECONDS = 3;
    private static final int DEFAULT_CLASS_INDEX_BIT = 20;
    private static final int DEFAULT_CHUNK_COUNT_BIT = 16;
    private static final int DEFAULT_CHUNK_BIT = 14;

    private static int UNNAMED_COUNT = 0;

    private final String name;    
    private final ArrayPool arrayPool;
    private final Map<IndexKey, Node> nodeCache;
    private final ClassIndex classIndex;
    private final IDSchema idSchema;
    private final ChunkedPool<Entity> entityPool;
    private final Map<Class<?>, Transmute.ByAddingAndRemoving<?>> addingTypeModifiers;
    private final Map<Class<?>, Transmute.ByRemoving> removingTypeModifiers;
    private final Node root;
    private final List<Scheduler> schedulers;
    private final int systemTimeoutSeconds;

    public Registry() {
        this("registry (" + (UNNAMED_COUNT++) + ")");
    }

    public Registry(String name) {
        this(name, DEFAULT_CLASS_INDEX_BIT, DEFAULT_CHUNK_BIT, DEFAULT_CHUNK_COUNT_BIT, DEFAULT_SYSTEM_TIMEOUT_SECONDS);
    }

    public Registry(String name, int classIndexBit, int chunkBit, int chunkCountBit, int systemTimeoutSeconds) {
        chunkBit = Math.max(IDSchema.MIN_CHUNK_BIT, Math.min(chunkBit, IDSchema.MAX_CHUNK_BIT));
        int reservedBit = IDSchema.BIT_LENGTH - chunkBit;
        chunkCountBit = Math.max(IDSchema.MIN_CHUNK_COUNT_BIT, Math.min(chunkCountBit, Math.min(reservedBit, IDSchema.MAX_CHUNK_COUNT_BIT)));
        
        this.name = name;
        this.arrayPool = new ArrayPool();
        this.nodeCache = new HashMap<IndexKey, Node>();
        this.classIndex = new ClassIndex(classIndexBit);
        this.idSchema = new IDSchema(chunkBit, chunkCountBit);
        this.entityPool = new ChunkedPool<Entity>(idSchema);
        this.addingTypeModifiers = new ConcurrentHashMap<Class<?>, Transmute.ByAddingAndRemoving<?>>();
        this.removingTypeModifiers = new ConcurrentHashMap<Class<?>, Transmute.ByRemoving>();
        this.root = new Node();
        root.composition = new Composition(this, entityPool.newNode(), arrayPool, classIndex, idSchema);
        this.schedulers = new CopyOnWriteArrayList<Scheduler>();
        this.systemTimeoutSeconds = systemTimeoutSeconds;
    }

    public String getName() {
        return name;
    }

    public Entity create(Object... components) {
        Object[] componentArray = components.length == 0 ? null : components;
        Composition composition = getOrCreate(componentArray);
        Entity entity = composition.createEntity(false, componentArray);
        return entity;
    }

    public Entity emplace(Object... components) {
        if (components.length == 0) {
            throw new IllegalArgumentException("Cannot emplace an entity with no components");
        }

        Composition composition = getOrCreate(components);
        Entity entity = composition.createEntity(true, components);
        return entity;
    }

    public Entity emplace(Transmute.OfTypes withValues) {
        Composition composition = (Composition) withValues.getContext();
        return composition.createEntity(true, withValues.getComponents());
    }

    public Entity emulate(Entity prefab, Object... components) {
        Entity origin = (Entity) prefab;
        Object[] originComponents = origin.getComponents();
        if (originComponents == null || originComponents.length == 0) {
            return create(components);
        }
        Object[] targetComponents = Arrays.copyOf(originComponents, originComponents.length + components.length);
        System.arraycopy(components, 0, targetComponents, originComponents.length, components.length);
        return create(targetComponents);
    }

    public boolean delete(Entity entity) {
        return ((Entity) entity).delete();
    }

    public void update() {
        for (int i = 0; i < schedulers.size(); i++) {
            schedulers.get(i).update();
        }
    }
    
    public void update(int ups) {
        for (int i = 0; i < schedulers.size(); i++) {
            schedulers.get(i).update(ups);
        }
    }

    public Scheduler createScheduler() {
        var scheduler = new Scheduler(systemTimeoutSeconds);
        schedulers.add(scheduler);
        return scheduler;
    }

    public <T> Group<Group.With1<T>> view(Class<T> type) {
        var nodes = find(type);
        return new View.With1<T>(this, nodes, type);
    }

    public <T1, T2> Group<Group.With2<T1, T2>> view(Class<T1> type1, Class<T2> type2) {
        var nodes = find(type1, type2);
        return new View.With2<T1, T2>(this, nodes, type1, type2);
    }

    public <T1, T2, T3> Group<Group.With3<T1, T2, T3>> view(Class<T1> type1, Class<T2> type2, Class<T3> type3) {
        var nodes = find(type1, type2, type3);
        return new View.With3<T1, T2, T3>(this, nodes, type1, type2, type3);
    }

    public <T1, T2, T3, T4> Group<Group.With4<T1, T2, T3, T4>> view(Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4) {
        var nodes = find(type1, type2, type3, type4);
        return new View.With4<T1, T2, T3, T4>(this, nodes, type1, type2, type3, type4);
    }

    public <T1, T2, T3, T4, T5> Group<Group.With5<T1, T2, T3, T4, T5>> view(Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5) {
        var nodes = find(type1, type2, type3, type4, type5);
        return new View.With5<T1, T2, T3, T4, T5>(this, nodes, type1, type2, type3, type4, type5);
    }

    public <T1, T2, T3, T4, T5, T6> Group<Group.With6<T1, T2, T3, T4, T5, T6>> view(Class<T1> type1, Class<T2> type2, Class<T3> type3, Class<T4> type4, Class<T5> type5, Class<T6> type6) {
        var nodes = find(type1, type2, type3, type4, type5, type6);
        return new View.With6<T1, T2, T3, T4, T5, T6>(this, nodes, type1, type2, type3, type4, type5, type6);
    }

    private Map<IndexKey, Node> find(Class<?>... componentTypes) {
        switch (componentTypes.length) {
            case 0:
                return null;
            case 1:
                Node node = nodeCache.get(new IndexKey(classIndex.getIndex(componentTypes[0])));
                return node == null ? null : node.copyOfLinkedNodes();
            default:
                Map<IndexKey, Node> currentCompositions = null;

                for (int i = 0; i < componentTypes.length; i++) {
                    node = nodeCache.get(new IndexKey(classIndex.getIndex(componentTypes[i])));

                    if (node == null) {
                        continue;
                    }
                    currentCompositions = currentCompositions == null ? node.copyOfLinkedNodes() : intersect(currentCompositions, node.linkedNodes);
                }

                return currentCompositions;
        }
    }

    protected void include(Map<IndexKey, Node> nodeMap, Class<?>... componentTypes) {
        if (componentTypes.length == 0) {
            return;
        }
        for (var componentType : componentTypes) {
            Node node = nodeCache.get(new IndexKey(classIndex.getIndex(componentType)));
            
            if (node == null) {
                continue;
            }

            intersect(nodeMap, node.linkedNodes);
        }
    }

    protected void exclude(Map<IndexKey, Node> nodeMap, Class<?>... componentTypes) {
        if (componentTypes.length == 0) {
            return;
        }

        for (var componentType : componentTypes) {
            IndexKey indexKey = new IndexKey(classIndex.getIndex(componentType));
            nodeMap.remove(indexKey);
            Node node = nodeCache.get(indexKey);

            if (node != null) {
                for (var linkedNodeKey : node.linkedNodes.keySet()) {
                    nodeMap.remove(linkedNodeKey);
                }
            }
        }
    }

    private Map<IndexKey, Node> intersect(Map<IndexKey, Node> subject, Map<IndexKey, Node> other) {
        Set<IndexKey> indexKeySet = subject.keySet();
        Iterator<IndexKey> iterator = indexKeySet.iterator();

        while (iterator.hasNext()) {
            if (!other.containsKey(iterator.next())) {
                iterator.remove();
            }
        }

        return subject;
    }
    
    public Node getOrCreateNode(IndexKey key, Class<?>... componentTypes) {
        Node node = nodeCache.computeIfAbsent(key, k -> new Node(componentTypes));

        if (componentTypes.length > 1) {
            for (int i = 0; i < componentTypes.length; i++) {
                Class<?> componentType = componentTypes[i];
                IndexKey typeKey = new IndexKey(classIndex.getIndex(componentType));
                Node singleTypeNode = nodeCache.computeIfAbsent(typeKey, k -> new Node(componentType));
                singleTypeNode.linkNode(key, node);
            }
        } else {
            node.linkNode(key, node);
        }

        return node;
    }

    @SuppressWarnings("unchecked")
    protected Transmute.ByAddingAndRemoving<Object> getAddingTypeModifier(Class<?> componentType) {
        return (Transmute.ByAddingAndRemoving<Object>) addingTypeModifiers.computeIfAbsent(componentType, k -> byAddingAndRemoving(componentType));
    }

    protected Transmute.ByRemoving getRemovingTypeModifier(Class<?> componentType) {
        return removingTypeModifiers.computeIfAbsent(componentType, k -> byRemoving(componentType));
    }

    protected Composition getOrCreate(Object[] components) {
        int componentsLength = components == null ? 0 : components.length;

        switch (componentsLength) {
            case 0: return root.composition;
            case 1: return getSingleTypeComposition(components[0].getClass());
            default:
                IndexKey indexKey = classIndex.getIndexKey(components);
                Node node = nodeCache.get(indexKey);
                
                if (node == null) {
                    node = getOrCreateNode(indexKey, getComponentTypes(components));
                }
                return getNodeComposition(node);
        }
    }

    protected Composition getOrCreateByType(Class<?>[] componentTypes) {
        int length = componentTypes == null ? 0 : componentTypes.length;

        switch (length) {
            case 0: return root.composition;
            case 1: return getSingleTypeComposition(componentTypes[0]);
            default:
                IndexKey indexKey = classIndex.getIndexKey(componentTypes);
                Node node = nodeCache.get(indexKey);

                if (node == null) {
                    node = getOrCreateNode(indexKey, componentTypes);
                }
                return getNodeComposition(node);
        }
    }

    private Composition getSingleTypeComposition(Class<?> componentType) {
        IndexKey key = new IndexKey(classIndex.getIndex(componentType));
        Node node = nodeCache.get(key);
        
        if (node == null) {
            key = new IndexKey(classIndex.getIndexOrAddClass(componentType));
            node = nodeCache.get(key);
            if (node == null) {
                node = getOrCreateNode(key, componentType);
            }
        } else {
            // node may not be yet connected to itself
            node.linkNode(new IndexKey(classIndex.getIndex(componentType)), node);
        }
        return getNodeComposition(node);
    }

    private Class<?>[] getComponentTypes(Object[] components) {
        Class<?>[] componentTypes = new Class<?>[components.length];

        for (int i = 0; i < components.length; i++) {
            componentTypes[i] = components[i].getClass();
        }
        return componentTypes;
    }

    private Composition getNodeComposition(Node node) {
        Composition composition = node.getComposition();
        
        if (composition != null) {
            return composition;
        }
        return node.getOrCreateComposition();
    }

    public void modifyComponents(Entity entity, Composition newDataComposition, Object[] newComponentArray) {
        entity.getComposition().detachEntity(entity);
        newDataComposition.attachEntity(entity, true, newComponentArray);
    }

    public Entity addComponent(Entity entity, Object component) {
        var modifier = getAddingTypeModifier(component.getClass());
        var mod = (NewEntityComposition) modifier.withValue(entity, component).getModifier();
        modifyComponents(mod.entity(), mod.dataComposition(), mod.components());
        return entity;
    }

    public boolean removeComponentType(Entity entity, Class<?> componentType) {
        if (componentType == null) {
            return false;
        }

        var modifier = getRemovingTypeModifier(componentType);
        var mod = (NewEntityComposition) modifier.withValue(entity).getModifier();
        if(mod == null) {
            return false;
        }

        modifyComponents(mod.entity(), mod.dataComposition(), mod.components());
        return true;
    }


    protected Transmute.ByRemoving byRemoving(Class<?>... removedComponentTypes) {
        return new Transmute.PreparedModifier(this, null, removedComponentTypes);
    }

    protected <T> Transmute.ByAddingAndRemoving<T> byAddingAndRemoving(Class<T> componentType, Class<?>... removedComponentTypes) {
        return new Transmute.ByAddingAndRemoving<T>(this, new Class<?>[] { componentType }, removedComponentTypes);
    }

    public ClassIndex getClassIndex() {
        return classIndex;
    }

    public Node getRoot() {
        return root;
    }

    public Map<IndexKey, Node> getNodeCache() {
        return nodeCache;
    }

    public IDSchema getIDSchema() {
        return idSchema;
    }

    public int size() {
        return entityPool.size();
    }

    @Override
    public void close() {
        nodeCache.clear();
        classIndex.close();
        entityPool.close();

        int sSize = schedulers.size();
        for (int i = 0; i < sSize; i++) {
            schedulers.get(i).close();
        }
    }

    protected final class Node {

        private final StampedLock lock;
        private final Map<IndexKey, Node> linkedNodes;
        private final Class<?>[] componentTypes;
        private Composition composition;

        private Node(Class<?>... componentTypes) {
            this.lock = new StampedLock();
            this.linkedNodes = new ConcurrentHashMap<IndexKey, Node>();
            this.componentTypes = componentTypes;
            this.composition = null;
        }

        protected void linkNode(IndexKey key, Node node) {
            linkedNodes.putIfAbsent(key, node);
        }

        protected Composition getOrCreateComposition() {
            Composition value;
            long stamp = lock.tryOptimisticRead();

            try {
                for (;; stamp = lock.writeLock()) {
                    if (stamp == 0L) {
                        continue;
                    }
                    // possibly racy reads
                    value = composition;
                    if (!lock.validate(stamp)) {
                        continue;
                    }
                    if (value != null) {
                        break;
                    }
                    stamp = lock.tryConvertToWriteLock(stamp);
                    if (stamp == 0L) {
                        continue;
                    }
                    // exclusive access
                    value = composition = new Composition(Registry.this, entityPool.newNode(), arrayPool, classIndex, idSchema, componentTypes);
                    break;
                }
                
                return value;
            } finally {
                if (StampedLock.isWriteLockStamp(stamp)) {
                    lock.unlockWrite(stamp);
                }
            }
        }

        public Composition getComposition() {
            return composition;
        }

        public Map<IndexKey, Node> getLinkedNodes() {
            return Collections.unmodifiableMap(linkedNodes);
        }

        public Map<IndexKey, Node> copyOfLinkedNodes() {
            return new ConcurrentHashMap<IndexKey, Node>(linkedNodes);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Node={types=[");
            if (componentTypes != null) {
                for (int i = 0; i < componentTypes.length; i++) {
                    sb.append(componentTypes[i].getSimpleName());
                    sb.append(",");
                }

                sb.setLength(sb.length() - 1);
            }   
            sb.append("]}");
            return sb.toString();
        }
    }
}

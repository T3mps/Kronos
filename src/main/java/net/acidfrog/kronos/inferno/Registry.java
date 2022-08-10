package net.acidfrog.kronos.inferno;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

import net.acidfrog.kronos.crates.pool.ChunkedPool;
import net.acidfrog.kronos.crates.pool.ArrayPool;
import net.acidfrog.kronos.crates.pool.ChunkedPool.IDSchema;
import net.acidfrog.kronos.inferno.core.ClassIndex;
import net.acidfrog.kronos.inferno.core.IndexKey;

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
    private final ChunkedPool<IntEntity> entityPool;
    private final Composition composition;
    private final Map<Class<?>, Transmute.ByAdding1AndRemoving<?>> addingTypeModifiers;
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
        // this.nodeCache = new NodeCache();
        this.nodeCache = new HashMap<IndexKey, Node>();
        this.classIndex = new ClassIndex(classIndexBit);
        this.idSchema = new IDSchema(chunkBit, chunkCountBit);
        this.entityPool = new ChunkedPool<IntEntity>(idSchema);
        this.composition = new Composition(this);
        this.addingTypeModifiers = new ConcurrentHashMap<Class<?>, Transmute.ByAdding1AndRemoving<?>>();
        this.removingTypeModifiers = new ConcurrentHashMap<Class<?>, Transmute.ByRemoving>();
        this.root = new Node();
        root.composition = new DataComposition(this, entityPool.newTenant(), arrayPool, classIndex, idSchema);
        this.schedulers = new CopyOnWriteArrayList<Scheduler>();
        this.systemTimeoutSeconds = systemTimeoutSeconds;
    }

    public String getName() {
        return name;
    }

    public Entity create(Object... components) {
        Object[] componentArray = components.length == 0 ? null : components;
        DataComposition composition = getOrCreate(componentArray);
        Entity entity = composition.createEntity(false, componentArray);
        return entity;
    }

    public Entity emplace(Object... components) {
        if (components.length == 0) {
            throw new IllegalArgumentException("Cannot emplace an entity with no components");
        }

        DataComposition composition = getOrCreate(components);
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

    public boolean modify(Transmute.Modifier modifier) {
        var mod = (Composition.NewEntityComposition) modifier.getModifier();
        return mod == null ? false : mod.entity().modify(this, mod.dataComposition(), mod.components());
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
    public Transmute.ByAdding1AndRemoving<Object> fetchAddingTypeModifier(Class<?> componentType) {
        return (Transmute.ByAdding1AndRemoving<Object>) addingTypeModifiers.computeIfAbsent(componentType, k -> composition.byAdding1AndRemoving(componentType));
    }

    public Transmute.ByRemoving fetchRemovingTypeModifier(Class<?> componentType) {
        return removingTypeModifiers.computeIfAbsent(componentType, k -> composition.byRemoving(componentType));
    }

    public DataComposition getOrCreate(Object[] components) {
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

    public DataComposition getOrCreateByType(Class<?>[] componentTypes) {
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

    private DataComposition getSingleTypeComposition(Class<?> componentType) {
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

    private DataComposition getNodeComposition(Node link) {
        DataComposition composition = link.getComposition();
        
        if (composition != null) {
            return composition;
        }
        return link.getOrCreateComposition();
    }

    public void modifyComponents(IntEntity entity, DataComposition newDataComposition, Object[] newComponentArray) {
        entity.getComposition().detachEntity(entity);
        newDataComposition.attachEntity(entity, true, newComponentArray);
    }

    public Entity addComponent(IntEntity entity, Object component) {
        var modifier = fetchAddingTypeModifier(component.getClass());
        var mod = (Composition.NewEntityComposition) modifier.withValue(entity, component).getModifier();
        modifyComponents(mod.entity(), mod.dataComposition(), mod.components());
        return entity;
    }

    public boolean removeComponentType(IntEntity entity, Class<?> componentType) {
        if (componentType == null) {
            return false;
        }

        var modifier = fetchRemovingTypeModifier(componentType);
        var mod = (Composition.NewEntityComposition) modifier.withValue(entity).getModifier();
        if(mod == null) {
            return false;
        }

        modifyComponents(mod.entity(), mod.dataComposition(), mod.components());
        return true;
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

    public Composition composition() {
        return composition;
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
        private DataComposition composition;

        private Node(Class<?>... componentTypes) {
            this.lock = new StampedLock();
            this.linkedNodes = new ConcurrentHashMap<IndexKey, Node>();
            this.componentTypes = componentTypes;
        }

        protected void linkNode(IndexKey key, Node node) {
            linkedNodes.putIfAbsent(key, node);
        }

        protected DataComposition getOrCreateComposition() {
            DataComposition value;
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
                    value = composition = new DataComposition(Registry.this, entityPool.newTenant(), arrayPool, classIndex, idSchema, componentTypes);
                    break;
                }
                
                return value;
            } finally {
                if (StampedLock.isWriteLockStamp(stamp)) {
                    lock.unlockWrite(stamp);
                }
            }
        }

        public DataComposition getComposition() {
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
            return "Node={types=[" + (componentTypes == null ? "" :
            Arrays.stream(componentTypes).map(Class::getSimpleName).sorted().collect(Collectors.joining(","))) + "]}";
        }
    }

    // private final class NodeCache implements Iterable<Node> {
        
    //     private final Map<IndexKey, Node> data;

    //     private NodeCache() {
    //         this.data = new ConcurrentHashMap<IndexKey, Node>();
    //     }

    //     public Node getOrCreateNode(IndexKey key, Class<?>... componentTypes) {
    //         Node node = data.computeIfAbsent(key, k -> new Node(componentTypes));

    //         if (componentTypes.length > 1) {
    //             for (int i = 0; i < componentTypes.length; i++) {
    //                 Class<?> componentType = componentTypes[i];
    //                 IndexKey typeKey = new IndexKey(classIndex.getIndex(componentType));
    //                 Node singleTypeNode = data.computeIfAbsent(typeKey, k -> new Node(componentType));
    //                 singleTypeNode.linkNode(key, node);
    //             }
    //         } else {
    //             node.linkNode(key, node);
    //         }

    //         return node;
    //     }

    //     public Node get(IndexKey key) {
    //         return data.get(key);
    //     }

    //     public void clear() {
    //         data.clear();
    //     }

    //     @Override
    //     public Iterator<Node> iterator() {
    //         return data.values().iterator();
    //     }
    // }
}

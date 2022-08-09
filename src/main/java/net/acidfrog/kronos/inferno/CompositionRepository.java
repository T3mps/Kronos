package net.acidfrog.kronos.inferno;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

import net.acidfrog.kronos.crates.pool.ChunkedPool;
import net.acidfrog.kronos.crates.pool.ObjectArrayPool;
import net.acidfrog.kronos.crates.pool.ChunkedPool.IDSchema;
import net.acidfrog.kronos.inferno.core.ClassIndex;
import net.acidfrog.kronos.inferno.core.IndexKey;

public final class CompositionRepository implements AutoCloseable {

    public static final int DEFAULT_CLASS_INDEX_BIT = 20;
    public static final int DEFAULT_CHUNK_COUNT_BIT = 16;
    public static final int DEFAULT_CHUNK_BIT = 14;
    
    private final ObjectArrayPool arrayPool;
    private final NodeCache nodeCache = new NodeCache();
    private final ClassIndex classIndex;
    private final ChunkedPool<IntEntity> pool;
    private final IDSchema idSchema;
    private final Composition preparedComposition;
    private final Map<Class<?>, Transmute.ByAdding1AndRemoving<?>> addingTypeModifiers = new ConcurrentHashMap<Class<?>, Transmute.ByAdding1AndRemoving<?>>();
    private final Map<Class<?>, Transmute.ByRemoving> removingTypeModifiers = new ConcurrentHashMap<Class<?>, Transmute.ByRemoving>();
    private final Node root;

    public CompositionRepository() {
        this(DEFAULT_CLASS_INDEX_BIT, DEFAULT_CHUNK_BIT, DEFAULT_CHUNK_COUNT_BIT);
    }

    public CompositionRepository(int classIndexBit, int chunkBit, int chunkCountBit) {
        chunkBit = Math.max(IDSchema.MIN_CHUNK_BIT, Math.min(chunkBit, IDSchema.MAX_CHUNK_BIT));
        int reservedBit = IDSchema.BIT_LENGTH - chunkBit;
        chunkCountBit = Math.max(IDSchema.MIN_CHUNK_COUNT_BIT, Math.min(chunkCountBit, Math.min(reservedBit, IDSchema.MAX_CHUNK_COUNT_BIT)));
        this.classIndex = new ClassIndex(classIndexBit, true);
        this.idSchema = new IDSchema(chunkBit, chunkCountBit);
        this.pool = new ChunkedPool<IntEntity>(idSchema);
        this.preparedComposition = new Composition(this);
        this.root = new Node();
        this.arrayPool = new ObjectArrayPool();
        this.root.composition = new DataComposition(this, pool.newTenant(), arrayPool, classIndex, idSchema);
    }

    public IDSchema getIDSchema() {
        return idSchema;
    }

    public Composition getPreparedComposition() {
        return preparedComposition;
    }

    @SuppressWarnings("unchecked")
    public Transmute.ByAdding1AndRemoving<Object> fetchAddingTypeModifier(Class<?> compType) {
        return (Transmute.ByAdding1AndRemoving<Object>) addingTypeModifiers.computeIfAbsent(compType, k -> preparedComposition.byAdding1AndRemoving(compType));
    }

    public Transmute.ByRemoving fetchRemovingTypeModifier(Class<?> compType) {
        return removingTypeModifiers.computeIfAbsent(compType, k -> preparedComposition.byRemoving(compType));
    }

    public DataComposition getOrCreate(Object[] components) {
        int componentsLength = components == null ? 0 : components.length;

        switch (componentsLength) {
            case 0: return root.composition;
            case 1: return getSingleTypeComposition(components[0].getClass());
            default:
                IndexKey indexKey = classIndex.getIndexKey(components);
                Node node = nodeCache.getNode(indexKey);
                
                if (node == null) {
                    node = nodeCache.getOrCreateNode(indexKey, getComponentTypes(components));
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
                Node node = nodeCache.getNode(indexKey);

                if (node == null) {
                    node = nodeCache.getOrCreateNode(indexKey, componentTypes);
                }
                return getNodeComposition(node);
        }
    }

    private DataComposition getSingleTypeComposition(Class<?> componentType) {
        IndexKey key = new IndexKey(classIndex.getIndex(componentType));
        Node node = nodeCache.getNode(key);
        
        if (node == null) {
            key = new IndexKey(classIndex.getIndexOrAddClass(componentType));
            node = nodeCache.getNode(key);
            if (node == null) {
                node = nodeCache.getOrCreateNode(key, componentType);
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

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public Map<IndexKey, Node> find(Class<?>... componentTypes) {
        switch (componentTypes.length) {
            case 0:
                return null;
            case 1:
                Node node = nodeCache.getNode(new IndexKey(classIndex.getIndex(componentTypes[0])));
                return node == null ? null : node.copyOfLinkedNodes();
            default:
                Map<IndexKey, Node> currentCompositions = null;

                for (int i = 0; i < componentTypes.length; i++) {
                    node = nodeCache.getNode(new IndexKey(classIndex.getIndex(componentTypes[i])));

                    if (node == null) {
                        continue;
                    }
                    currentCompositions = currentCompositions == null ? node.copyOfLinkedNodes() : intersect(currentCompositions, node.linkedNodes);
                }

                return currentCompositions;
        }
    }

    public void include(Map<IndexKey, Node> nodeMap, Class<?>... componentTypes) {
        if (componentTypes.length == 0) {
            return;
        }
        for (var componentType : componentTypes) {
            Node node = nodeCache.getNode(new IndexKey(classIndex.getIndex(componentType)));
            if (node == null) {
                continue;
            }

            intersect(nodeMap, node.linkedNodes);
        }
    }

    public void exclude(Map<IndexKey, Node> nodeMap, Class<?>... componentTypes) {
        if (componentTypes.length == 0) {
            return;
        }
        for (var componentType : componentTypes) {
            IndexKey indexKey = new IndexKey(classIndex.getIndex(componentType));
            nodeMap.remove(indexKey);
            Node node = nodeCache.getNode(indexKey);

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

    public ClassIndex getClassIndex() {
        return classIndex;
    }

    public Node getRoot() {
        return root;
    }

    public NodeCache getNodeCache() {
        return nodeCache;
    }

    @Override
    public void close() {
        nodeCache.clear();
        classIndex.close();
        pool.close();
    }

    public final class NodeCache {
        
        private final Map<IndexKey, Node> data = new ConcurrentHashMap<>();

        public Node getOrCreateNode(IndexKey key, Class<?>... componentTypes) {
            Node node = data.computeIfAbsent(key, k -> new Node(componentTypes));

            if (componentTypes.length > 1) {
                for (int i = 0; i < componentTypes.length; i++) {
                    Class<?> componentType = componentTypes[i];
                    IndexKey typeKey = new IndexKey(classIndex.getIndex(componentType));
                    Node singleTypeNode = data.computeIfAbsent(typeKey, k -> new Node(componentType));
                    singleTypeNode.linkNode(key, node);
                }
            } else {
                node.linkNode(key, node);
            }

            return node;
        }

        public Node getNode(IndexKey key) {
            return data.get(key);
        }

        public boolean contains(IndexKey key) {
            return data.containsKey(key);
        }

        public void clear() {
            data.clear();
        }
    }

    public final class Node {

        private final StampedLock lock = new StampedLock();
        private final Map<IndexKey, Node> linkedNodes = new ConcurrentHashMap<>();
        private final Class<?>[] componentTypes;
        private DataComposition composition;

        public Node(Class<?>... componentTypes) {
            this.componentTypes = componentTypes;
        }

        public void linkNode(IndexKey key, Node node) {
            linkedNodes.putIfAbsent(key, node);
        }

        public DataComposition getOrCreateComposition() {
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
                    value = composition = new DataComposition(CompositionRepository.this, pool.newTenant(), arrayPool, classIndex, idSchema, componentTypes);
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
}

package com.starworks.kronos.structures.pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import com.starworks.kronos.structures.Identifiable;
import com.starworks.kronos.structures.IntStack;

public final class ChunkedPool<T extends Identifiable> implements Pool<T>, AutoCloseable {

    public static final int ID_STACK_CAPACITY = 1 << 16;

    private final IDSchema schema;
    private final AtomicInteger chunkIndex;
    private final AtomicReferenceArray<LinkedChunk<T>> chunks;
    private final List<PooledNode<T>> pooledNodes;

    public ChunkedPool(int chunkBit, int chunkCountBit) {
        this(new IDSchema(chunkBit, chunkCountBit));
    }

    public ChunkedPool(IDSchema schema) {
        this.schema = schema;
        this.chunkIndex = new AtomicInteger(-1);
        this.chunks = new AtomicReferenceArray<LinkedChunk<T>>(schema.chunkCount);
        this.pooledNodes = new ArrayList<PooledNode<T>>();
    }

    private LinkedChunk<T> newChunk(PooledNode<T> owner, int currentChunkIndex) {
        int id;

        if (!chunkIndex.compareAndSet(currentChunkIndex, id = currentChunkIndex + 1)) {
            return null;
        }
        if (id > schema.chunkCount - 1) {
            throw new OutOfMemoryError(ChunkedPool.class.getName() + ": cannot create a new memory chunk");
        }
        
        var currentChunk = owner.currentChunk;
        var newChunk = new LinkedChunk<T>(id, schema, currentChunk);
        
        if (currentChunk != null) {
            currentChunk.setNext(newChunk);
        }
        
        chunks.set(id, newChunk);
        return newChunk;
    }

    private LinkedChunk<T> getChunk(int id) {
        return chunks.getPlain(schema.fetchChunkID(id));
    }

    public T get(int id) {
        return getChunk(id).get(id);
    }

    public PooledNode<T> newNode() {
        PooledNode<T> node = new PooledNode<T>(this, chunks, chunkIndex, schema);
        pooledNodes.add(node);
        return node;
    }

    public void clear() {
        for (int i = 0; i < chunks.length(); i++) {
            chunks.set(i, null);
        }
        pooledNodes.clear();
    }

    public int size() {
        int sum = 0;

        for (int i = 0; i < chunks.length(); i++) {
            var chunk = chunks.getPlain(i);
            sum += chunk != null ? chunk.size() : 0;
        }
        return sum;
    }

    public IDSchema getSchema() {
        return schema;
    }

    @Override
    public void close() {
        pooledNodes.forEach(PooledNode::close);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChunkedPool={");
        sb.append("chunkCount=").append(schema.chunkCount).append("|off-heap");
        sb.append('}');
        return sb.toString();
    }

    public static final class PooledNode<T extends Identifiable> implements AutoCloseable, Iterable<T> {
        
        private final ChunkedPool<T> pool;
        private final AtomicReferenceArray<LinkedChunk<T>> chunks;
        private final AtomicInteger chunkIndex;
        private final IDSchema schema;
        private final IntStack stack;
        private final LinkedChunk<T> firstChunk;
        private LinkedChunk<T> currentChunk;
        private int newID = Integer.MIN_VALUE;

        private PooledNode(ChunkedPool<T> pool, AtomicReferenceArray<LinkedChunk<T>> chunks, AtomicInteger chunkIndex, IDSchema schema) {
            this.pool = pool;
            this.chunks = chunks;
            this.chunkIndex = chunkIndex;
            this.schema = schema;
            this.stack = new IntStack(ID_STACK_CAPACITY);
            while ((currentChunk = pool.newChunk(this, chunkIndex.get())) == null) ;
            this.firstChunk = currentChunk;
            nextID();
        }

        public int nextID() {
            int returnValue = stack.pop();
            if (returnValue != Integer.MIN_VALUE) {
                return returnValue;
            }

            for (;;) {
                returnValue = newID;
                int objectID;
                int currentChunkIndex = chunkIndex.get();
                LinkedChunk<T> chunk;
                while ((chunk = chunks.get(currentChunkIndex)) == null) ;
                
                if (chunk.index.get() < schema.chunkCapacity - 1) {
                    while ((objectID = chunk.index.get()) < schema.chunkCapacity - 1) {
                        if (chunk.index.compareAndSet(objectID, objectID + 1)) {
                            newID = schema.generateID(chunk.id, ++objectID);
                            return returnValue;
                        }
                    }

                    continue;
                }

                if ((chunk = pool.newChunk(this, currentChunkIndex)) != null) {
                    currentChunk = chunk;
                    objectID = chunk.incrementIndex();
                    newID = schema.generateID(chunk.id, objectID);
                    return returnValue;
                }
            }
        }

        public int freeID(int id) {
            LinkedChunk<T> chunk = pool.getChunk(id);

            if (chunk == null) {
                return -1;
            }
            if (chunk.isEmpty()) {
                stack.push(id);
                return id;
            }

            boolean notCurrentChunk = chunk != currentChunk;
            int reusableID = chunk.remove(id, notCurrentChunk);
            
            if (notCurrentChunk) {
                stack.push(reusableID);
            } else {
                newID = reusableID;
            }

            return reusableID;
        }

        public T register(int id, T entry) {
            return pool.getChunk(id).set(id, entry);
        }

        public T get(int id) {
            return pool.get(id);
        }

        public int currentChunkSize() {
            return currentChunk.size();
        }

        public ChunkedPool<T> getPool() {
            return pool;
        }

        @Override
        public void close() {
            stack.close();
        }

        @Override
        public Iterator<T> iterator() {
            return new ChunkedPoolIterator<T>(firstChunk);
        }
    }

    public static class ChunkedPoolIterator<T extends Identifiable> implements Iterator<T> {

        int next = 0;
        private LinkedChunk<T> currentChunk;

        public ChunkedPoolIterator(LinkedChunk<T> currentChunk) {
            this.currentChunk = currentChunk;
        }

        @Override
        public boolean hasNext() {
            return currentChunk.size() > next || ((next = 0) == 0 && (currentChunk = currentChunk.next) != null);
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            return (T) currentChunk.data[next++];
        }
    }

    public static final class LinkedChunk<T extends Identifiable> {

        private final IDSchema schema;
        private final Identifiable[] data;
        private final LinkedChunk<T> previous;
        private final int id;
        private final AtomicInteger index = new AtomicInteger(-1);
        private LinkedChunk<T> next;
        private int sizeOffset;

        public LinkedChunk(int id, IDSchema schema, LinkedChunk<T> previous) {
            this.schema = schema;
            this.data = new Identifiable[schema.chunkCapacity];
            this.previous = previous;
            this.id = id;
        }

        public int incrementIndex() {
            return index.incrementAndGet();
        }

        public int remove(int id, boolean doNotUpdateIndex) {
            int capacity = schema.chunkCapacity;
            int objectIDToBeReused = schema.fetchObjectID(id);
            
            for (;;) {
                int lastIndex = doNotUpdateIndex ? index.get() : index.decrementAndGet();
                
                if (lastIndex >= capacity) {
                    index.compareAndSet(capacity, capacity - 1);
                    continue;
                }
                if (lastIndex < 0) {
                    return 0;
                }
                
                data[objectIDToBeReused] = data[lastIndex];
                data[lastIndex] = null;
                
                if (data[objectIDToBeReused] != null) {
                    data[objectIDToBeReused].setID(objectIDToBeReused);
                }
                
                return schema.mergeID(id, lastIndex);
            }
        }

        @SuppressWarnings("unchecked")
        public T get(int id) {
            return (T) data[schema.fetchObjectID(id)];
        }

        @SuppressWarnings("unchecked")
        public T set(int id, T value) {
            return (T) (data[schema.fetchObjectID(id)] = value);
        }

        public boolean hasCapacity() {
            return index.get() < schema.chunkCapacity - 1;
        }

        public LinkedChunk<T> getPrevious() {
            return previous;
        }

        private void setNext(LinkedChunk<T> next) {
            this.next = next;
            sizeOffset = 1;
        }

        public int size() {
            return index.get() + sizeOffset;
        }

        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("LinkedChunk={");
            sb.append("id=").append(id);
            sb.append(", capacity=").append(schema.chunkCapacity);
            sb.append(", previous=").append(previous == null ? null : previous.id);
            sb.append(", next=").append(next == null ? null : next.id);
            sb.append('}');
            return sb.toString();
        }
    }

    public record IDSchema(int chunkBit, int chunkCountBit, int chunkCount, int chunkIDBitMask, int chunkIDBitMaskShifted, int chunkCapacity, int objectIDBitMask) {
        
        public static final int BIT_LENGTH = 30;
        public static final int MIN_CHUNK_BIT = 10;
        public static final int MIN_CHUNK_COUNT_BIT = 6;
        public static final int MAX_CHUNK_BIT = BIT_LENGTH - MIN_CHUNK_COUNT_BIT;
        public static final int MAX_CHUNK_COUNT_BIT = BIT_LENGTH - MIN_CHUNK_BIT;
        public static final int DETACHED_BIT_IDX = 31;
        public static final int DETACHED_BIT = 1 << DETACHED_BIT_IDX;
        public static final int FLAG_BIT_IDX = 30;
        public static final int FLAG_BIT = 1 << FLAG_BIT_IDX;

        public IDSchema(int chunkBit, int chunkCountBit) {
            this(chunkBit
                    , chunkCountBit
                    , 1 << chunkCountBit
                    , (1 << (BIT_LENGTH - chunkBit)) - 1
                    , ((1 << (BIT_LENGTH - chunkBit)) - 1) << chunkBit
                    , 1 << Math.min(chunkBit, MAX_CHUNK_BIT)
                    , (1 << chunkBit) - 1
            );
        }

        // |--FLAGS--|--CHUNK_ID--|--OBJECT_ID--|
        public String idToString(int id) {
            StringBuilder sb = new StringBuilder();
            sb.append("|").append((id & DETACHED_BIT) >>> DETACHED_BIT_IDX);
            sb.append(":").append((id & FLAG_BIT) >>> FLAG_BIT_IDX);
            sb.append(":").append(fetchChunkID(id));
            sb.append(":").append(fetchObjectID(id));
            sb.append("|");
            return sb.toString();
        }

        public int generateID(int chunkID, int objectID) {
            return (chunkID & chunkIDBitMask) << chunkBit | (objectID & objectIDBitMask);
        }

        public int mergeID(int id, int objectID) {
            return (id & chunkIDBitMaskShifted) | objectID;
        }

        public int fetchChunkID(int id) {
            return (id >>> chunkBit) & chunkIDBitMask;
        }

        public int fetchObjectID(int id) {
            return id & objectIDBitMask;
        }
    }
}
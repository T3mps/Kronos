package net.acidfrog.kronos.crates.pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import net.acidfrog.kronos.crates.IDStack;

public final class ChunkedPool<T extends ChunkedPool.Identifiable> implements AutoCloseable {

    public static final int ID_STACK_CAPACITY = 1 << 16;
    private final AtomicReferenceArray<LinkedChunk<T>> chunks;
    private final AtomicInteger chunkIndex = new AtomicInteger(-1);
    private final List<Tenant<T>> tenants = new ArrayList<Tenant<T>>();
    private final IDSchema idSchema;

    public ChunkedPool(IDSchema idSchema) {
        this.idSchema = idSchema;
        this.chunks = new AtomicReferenceArray<LinkedChunk<T>>(idSchema.chunkCount);
    }

    private LinkedChunk<T> newChunk(Tenant<T> owner, int currentChunkIndex) {
        int id;

        if (!chunkIndex.compareAndSet(currentChunkIndex, id = currentChunkIndex + 1)) {
            return null;
        }
        if (id > idSchema.chunkCount - 1) {
            throw new OutOfMemoryError(ChunkedPool.class.getName() + ": cannot create a new memory chunk");
        }
        
        var currentChunk = owner.currentChunk;
        LinkedChunk<T> newChunk = new LinkedChunk<T>(id, idSchema, currentChunk);
        
        if (currentChunk != null) {
            currentChunk.setNext(newChunk);
        }
        
        chunks.set(id, newChunk);
        return newChunk;
    }

    private LinkedChunk<T> getChunk(int id) {
        return chunks.getPlain(idSchema.fetchChunkId(id));
    }

    public T getEntry(int id) {
        return getChunk(id).get(id);
    }

    public Tenant<T> newTenant() {
        Tenant<T> newTenant = new Tenant<T>(this, chunks, chunkIndex, idSchema);
        tenants.add(newTenant);
        return newTenant;
    }

    public int size() {
        int sum = 0;

        for (int i = 0; i < chunks.length(); i++) {
            var chunk = chunks.getPlain(i);
            sum += chunk != null ? chunk.size() : 0;
        }
        return sum;
    }

    @Override
    public void close() {
        tenants.forEach(Tenant::close);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChunkedPool={");
        sb.append("chunkCount=").append(idSchema.chunkCount).append("|off-heap");
        sb.append('}');
        return sb.toString();
    }

    public interface Identifiable {

        int getID();

        int setID(int id);

        Identifiable getPrevious();

        Identifiable setPrevious(Identifiable prev);

        Identifiable getNext();

        Identifiable setNext(Identifiable next);
    }

    // |--FLAGS--|--CHUNK_ID--|--OBJECT_ID--|
    public record IDSchema(int chunkBit, int chunkCountBit, int chunkCount, int chunkIdBitMask, int chunkIdBitMaskShifted, int chunkCapacity, int objectIdBitMask) {
        
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

        public String idToString(int id) {
            StringBuilder sb = new StringBuilder();
            sb.append("|").append((id & DETACHED_BIT) >>> DETACHED_BIT_IDX);
            sb.append(":").append((id & FLAG_BIT) >>> FLAG_BIT_IDX);
            sb.append(":").append(fetchChunkId(id));
            sb.append(":").append(fetchObjectId(id));
            sb.append("|");
            return sb.toString();
        }

        public int createId(int chunkId, int objectId) {
            return (chunkId & chunkIdBitMask) << chunkBit | (objectId & objectIdBitMask);
        }

        public int mergeId(int id, int objectId) {
            return (id & chunkIdBitMaskShifted) | objectId;
        }

        public int fetchChunkId(int id) {
            return (id >>> chunkBit) & chunkIdBitMask;
        }

        public int fetchObjectId(int id) {
            return id & objectIdBitMask;
        }
    }

    public static final class Tenant<T extends Identifiable> implements AutoCloseable {
        
        private final ChunkedPool<T> pool;
        private final AtomicReferenceArray<LinkedChunk<T>> chunks;
        private final AtomicInteger chunkIndex;
        private final IDSchema idSchema;
        private final IDStack stack;
        private final LinkedChunk<T> firstChunk;
        private LinkedChunk<T> currentChunk;
        private int newId = Integer.MIN_VALUE;

        private Tenant(ChunkedPool<T> pool, AtomicReferenceArray<LinkedChunk<T>> chunks, AtomicInteger chunkIndex, IDSchema idSchema) {
            this.pool = pool;
            this.chunks = chunks;
            this.chunkIndex = chunkIndex;
            this.idSchema = idSchema;
            this.stack = new IDStack(ID_STACK_CAPACITY);
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
                returnValue = newId;
                int objectId;
                int currentChunkIndex = chunkIndex.get();
                LinkedChunk<T> chunk;
                while ((chunk = chunks.get(currentChunkIndex)) == null) {
                }
                if (chunk.index.get() < idSchema.chunkCapacity - 1) {
                    while ((objectId = chunk.index.get()) < idSchema.chunkCapacity - 1) {
                        if (chunk.index.compareAndSet(objectId, objectId + 1)) {
                            newId = idSchema.createId(chunk.id, ++objectId);
                            return returnValue;
                        }
                    }
                } else {
                    if ((chunk = pool.newChunk(this, currentChunkIndex)) != null) {
                        currentChunk = chunk;
                        objectId = chunk.incrementIndex();
                        newId = idSchema.createId(chunk.id, objectId);
                        return returnValue;
                    }
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
            int reusableId = chunk.remove(id, notCurrentChunk);
            
            if (notCurrentChunk) {
                stack.push(reusableId);
            } else {
                newId = reusableId;
            }
            return reusableId;
        }

        public Iterator<T> iterator() {
            return new PoolIterator<T>(firstChunk);
        }

        public T register(int id, T entry) {
            return pool.getChunk(id).set(id, entry);
        }

        public int currentChunkSize() {
            return currentChunk.size();
        }

        public IDStack getStack() {
            return stack;
        }

        public ChunkedPool<T> getPool() {
            return pool;
        }

        @Override
        public void close() {
            stack.close();
        }
    }

    public static class PoolIterator<T extends Identifiable> implements Iterator<T> {

        int next = 0;
        private LinkedChunk<T> currentChunk;

        public PoolIterator(LinkedChunk<T> currentChunk) {
            this.currentChunk = currentChunk;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
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

        private final IDSchema idSchema;
        private final Identifiable[] data;
        private final LinkedChunk<T> previous;
        private final int id;
        private final AtomicInteger index = new AtomicInteger(-1);
        private LinkedChunk<T> next;
        private int sizeOffset;

        public LinkedChunk(int id, IDSchema idSchema, LinkedChunk<T> previous) {
            this.idSchema = idSchema;
            this.data = new Identifiable[idSchema.chunkCapacity];
            this.previous = previous;
            this.id = id;
        }

        public int incrementIndex() {
            return index.incrementAndGet();
        }

        public int remove(int id, boolean doNotUpdateIndex) {
            int capacity = idSchema.chunkCapacity;
            int objectIdToBeReused = idSchema.fetchObjectId(id);
            for (;;) {
                int lastIndex = doNotUpdateIndex ? index.get() : index.decrementAndGet();
                
                if (lastIndex >= capacity) {
                    index.compareAndSet(capacity, capacity - 1);
                    continue;
                }
                if (lastIndex < 0) {
                    return 0;
                }
                
                data[objectIdToBeReused] = data[lastIndex];
                data[lastIndex] = null;
                
                if (data[objectIdToBeReused] != null) {
                    data[objectIdToBeReused].setID(objectIdToBeReused);
                }
                
                return idSchema.mergeId(id, lastIndex);
            }
        }

        @SuppressWarnings("unchecked")
        public T get(int id) {
            return (T) data[idSchema.fetchObjectId(id)];
        }

        @SuppressWarnings("unchecked")
        public T set(int id, T value) {
            return (T) (data[idSchema.fetchObjectId(id)] = value);
        }

        public boolean hasCapacity() {
            return index.get() < idSchema.chunkCapacity - 1;
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
            sb.append(", capacity=").append(idSchema.chunkCapacity);
            sb.append(", previous=").append(previous == null ? null : previous.id);
            sb.append(", next=").append(next == null ? null : next.id);
            sb.append('}');
            return sb.toString();
        }
    }
}
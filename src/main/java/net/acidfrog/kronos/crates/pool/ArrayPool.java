package net.acidfrog.kronos.crates.pool;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

import net.acidfrog.kronos.crates.set.SparseSet;

public class ArrayPool implements Pool<Object[]> {
    
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 16;
    public static final int SOFT_MAX_CAPACITY = Integer.MAX_VALUE - 8;
    
    private final SparseSet<Partition> partitions;

    public ArrayPool() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ArrayPool(int initialCapacity) {
        this.partitions = new SparseSet<Partition>(initialCapacity);
    }

    public Object[] push(Object[] objects) {
        int oSize = objects.length;
        var partition = partitions.get(oSize);
        if (partition == null) {
            partition = partitions.computeIfAbsent(oSize, l -> new Partition(oSize));
        }

        return partition.push(objects);
    }

    public Object[] pop(int size) {
        var partition = partitions.get(size);
        var objects = partition != null ? partition.pop() : null;
        return objects == null ? new Object[size] : objects;
    }

    public Object[] get(int size)  {
        var partition = partitions.get(size);
        var objects = partition != null ? partition.get() : null;
        return objects == null ? new Object[size] : objects;
    }

    public void clear() {
        partitions.clear();
    }

    public int size() {
        return partitions.size();
    }

    public int size(int arrayLength) {
        var partition = partitions.get(arrayLength);
        return partition == null ? -1 : partition.size.get() + 1;
    }

    public static final class Partition {

        private final StampedLock lock;
        private Reference<?>[] data;
        private final AtomicInteger size;
    
        protected Partition(int capacity) {
            this.lock = new StampedLock();
            this.data = new Reference<?>[DEFAULT_INITIAL_CAPACITY];
            this.size = new AtomicInteger(-1);
        }

        public Object[] push(Object[] objects) {
            for (;;) {
                int index = size.get();
                if (index < data.length - 1) {
                    if (size.compareAndSet(index, index + 1)) {
                        data[++index] = new SoftReference<Object>(objects);
                        Arrays.fill(objects, null);
                        return objects;
                    }
                } else {
                    int currentCapacity = data.length;
                    long stamp = lock.writeLock();

                    try {
                        if (data.length == currentCapacity) {
                            ensureCapacity();
                        }
                    } finally {
                        lock.unlockWrite(stamp);
                    }
                }
            }
        }

        public Object[] pop() {
            Object[] objects = null;
            
            int index = 0;
            while (objects == null && (index = size.getAndDecrement()) > -1) {
                objects = (Object[]) data[index].get();
            }

            return objects;
        }

        public Object[] get() {
            Object[] objects = null;
            
            int index = 0;
            while (objects == null && (index = size.get()) > -1) {
                objects = (Object[]) data[index].get();
            }

            return objects;
        }

        private void ensureCapacity() {
            int capacity = data.length + (data.length >> 1);
            
            if (capacity < 0 || capacity > SOFT_MAX_CAPACITY) {
                throw new OutOfMemoryError("Unable to allocate reqested memory for " + capacity + " bytes");
            }

            data = Arrays.copyOf(data, capacity);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Partition [size=").append(size.get()).append(", data=");
            sb.append(Arrays.toString(data)).append("]");
            return sb.toString();
        }
    }
}

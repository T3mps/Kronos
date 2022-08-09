package net.acidfrog.kronos.crates.pool;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

import net.acidfrog.kronos.crates.set.SparseSet;

public class ObjectArrayPool {
    
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 16;
    public static final int SOFT_MAX_CAPACITY = Integer.MAX_VALUE - 8;
    
    private final SparseSet<Layer> layers = new SparseSet<Layer>();

    public Object[] push(Object[] objects) {
        int oSize = objects.length;
        var layer = layers.get(oSize);
        if (layer == null) {
            layer = layers.computeIfAbsent(oSize, k -> new Layer(oSize));
        }
        return layer.push(objects);
    }

    public Object[] pop(int size) {
        var layer = layers.get(size);
        var objects = layer != null ? layer.pop() : null;
        return objects == null ? new Object[size] : objects;
    }

    public int size(int arrayLength) {
        var layer = layers.get(arrayLength);
        return layer == null ? -1 : layer.size.get() + 1;
    }

    public static final class Layer {

        private final AtomicInteger size;
        private final StampedLock lock;
        private Reference<?>[] data;
        final int length;
    
        Layer(int length) {
            this.length = length;
            this.size = new AtomicInteger(-1);
            this.lock = new StampedLock();
            this.data = new Reference<?>[DEFAULT_INITIAL_CAPACITY];
        }

        public Object[] push(Object... objects) {
            for (;;) {
                int index = size.get();

                if (index < data.length - 1) {
                    if (size.compareAndSet(index, index + 1)) {
                        data[++index] = new SoftReference<Object>(objects);
                        Arrays.fill(objects, null);
                        return objects;
                    }
                } else {
                    int cap = data.length;
                    long stamp = lock.writeLock();

                    try {
                        if (data.length == cap) {
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

        private void ensureCapacity() {
            int capacity = data.length + (data.length >> 1);
            
            if (capacity < 0 || capacity > SOFT_MAX_CAPACITY) {
                throw new OutOfMemoryError("Unable to allocate reqested memory for " + capacity + " bytes");
            }

            data = Arrays.copyOf(data, capacity);
        }
    }
}

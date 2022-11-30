package com.starworks.kronos.structures.set;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.starworks.kronos.structures.GenericArrayIterator;

public final class SparseSet<E> implements Iterable<E>{
    
    private final int[] dense;
    private final int[] sparse;
    private final Object[] values;
    private final int capacity;
    private final AtomicInteger size;
    private final StampedLock lock;

    public SparseSet() {
        this(1 << 10);
    }

    public SparseSet(int capacity) {
        this.dense = new int[capacity];
        this.sparse = new int[capacity];
        this.values = new Object[capacity];
        this.capacity = capacity;
        this.size = new AtomicInteger(0);
        this.lock = new StampedLock();
    }

    public E put(int key, E value) {
        E current = get(key);
        int i = current == null ? size.getAndIncrement() : sparse[key];
        dense[i] = key;
        sparse[key] = i;
        values[i] = value;
        return current;
    }

    @SuppressWarnings("unchecked")
    public void putAll(SparseSet<E> map) {
        for (int i = 0; i < map.size(); i++) {
            put(map.dense[i], (E) map.values[i]);
        }
    }

    public E get(int key) {
        int i = sparse[key];
        if (i >= size.get() || dense[i] != key) {
            return null;
        }

        return valueAt(i);
    }

    public boolean contains(int key) {
        int i = sparse[key];
        return i <= size.get() && dense[i] == key;
    }

    public boolean contains(Object value) {
        for (int i = 0; i < size.get(); i++) {
            if (Objects.equals(value, values[i])) {
                return true;
            }
        }

        return false;
    }

    public boolean containsAll(Collection<?> collection) {
        for (var e : collection) {
            if (!contains(e)) {
                return false;
            }
        }

        return true;
    }

    public E computeIfPresent(int key, Function<E, ? extends E> remappingFunction) {
        E value;
        long stamp = lock.tryOptimisticRead();

        try {
            for (;; stamp = lock.writeLock()) {
                if (stamp == 0L) {
                    continue;
                }

                value = get(key);

                if (!lock.validate(stamp)) {
                    continue;
                }
                if (value == null) {
                    return value;
                }

                stamp = lock.tryConvertToWriteLock(stamp);

                if (stamp == 0L) {
                    continue;
                }
                
                put(key, value = remappingFunction.apply(value));
                return value;
            }
        } finally {
            if (StampedLock.isWriteLockStamp(stamp)) {
                lock.unlockWrite(stamp);
            }
        }
    }

    public E computeIfAbsent(int key, Function<Integer, ? extends E> mappingFunction) {
        E value;
        long stamp = lock.tryOptimisticRead();

        try {
            for (;; stamp = lock.writeLock()) {
                if (stamp == 0L) {
                    continue;
                }

                value = get(key);

                if (!lock.validate(stamp)) {
                    continue;
                }
                if (value != null) {
                    return value;
                }

                stamp = lock.tryConvertToWriteLock(stamp);

                if (stamp == 0L) {
                    continue;
                }
                
                put(key, value = mappingFunction.apply(key));
                return value;
            }
        } finally {
            if (StampedLock.isWriteLockStamp(stamp)) {
                lock.unlockWrite(stamp);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public E valueAt(int index) {
        return (E) values[index];
    }

    public E remove(int key) {
        int i = sparse[key];
        if (i > size.get() || dense[i] != key) {
            return null;
        }
        
        E value = valueAt(i);
        dense[i] = dense[size.decrementAndGet()];
        sparse[dense[i]] = i;
        values[i] = values[size.get()];
        sparse[key] = -1;

        return value;
    }

    public void clear() {
        for (int i = 0; i < size.get(); i++) {
            dense[i] = 0;
            sparse[0] = -1;
            values[i] = null;
        }

        size.set(0);
    }

    public int size() {
        return size.get();
    }

    public int capacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return size.get() == 0;
    }

    @SuppressWarnings("unchecked")
    public E[] values() {
        if (isEmpty()) {
            return (E[]) new Object[0];
        }

        int length = size.get();
        E[] target = (E[]) new Object[length];
        
        System.arraycopy(values, 0, target, 0, length);
        
        return target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<E> iterator() {
        return new GenericArrayIterator<E>((E[]) values, size.get());
    }
    
    public Object[] toArray() {
        return values().clone();
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] arr) {
        var values = values();

        if (arr.length < values.length) {
            return (T[]) Arrays.copyOf(values, values.length, arr.getClass());
        }

        System.arraycopy(values, 0, arr, 0, values.length);

        if (arr.length > values.length) {
            arr[values.length] = null;
        }

        return arr;
    }

    public Stream<E> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }
}

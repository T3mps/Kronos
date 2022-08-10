package net.acidfrog.kronos.crates.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class SparseSet<E> implements Set<E> {
    
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

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
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

    @Override
    public boolean contains(Object value) {
        for (int i = 0; i < size.get(); i++) {
            if (Objects.equals(value, values[i])) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (var o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
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
                    break;
                }

                stamp = lock.tryConvertToWriteLock(stamp);

                if (stamp == 0L) {
                    continue;
                }
                
                put(key, value = mappingFunction.apply(key));
                break;
            }
            
            return value;
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

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
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
        return new ValueIterator<E>((E[]) values, size.get());
    }

    @Override
    public Object[] toArray() {
        return values();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    public Stream<E> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    private static class ValueIterator<E> implements Iterator<E> {
        
        private final E[] values;
        private final int size;
        private int index;
        
        public ValueIterator(E[] values, int size) {
            this.values = values;
            this.size = size;
        }
        
        @Override
        public boolean hasNext() {
            return index < size;
        }
        
        @Override
        public E next() {
            return values[index++];
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

package com.starworks.kronos.structures.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A bag is a collection of items that can be added to and removed from.
 * <p>
 * The items are not ordered.
 * 
 * @author Ethan Temprovich
 */
public final class Bag<E> implements MultiSet<E> {

    public E[] data;
    private int size;

    public Bag() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public Bag(int capacity) {
        this.data = (E[]) new Object[capacity];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean add(Object e) {
        if (size == data.length) {
            grow();
        }

        return internalAdd((E) e);
    }

    private boolean internalAdd(E e) {
        data[size++] = e;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        
        for (E e : c) {
            changed |= add(e);
        }

        return changed;
    }


    @Override
    public void set(int index, E e) {
        if (index >= data.length) {
            grow(index * 2);
        }

        size = Math.max(size, index + 1);
        data[index] = e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return data[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object e) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(e)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public E remove(int index) {
        E e = data[index];
        data[index] = data[--size];
        data[size] = null;

        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object e) {
        for (int i = 0; i < size; i++) {
            E current = data[i];

            if (e == current) {
                data[i] = data[--size];
                data[size] = null;
                return true;
            }
        }

        return false;
    }

    @Override
    public E pop() {
        if (size == 0) {
            return null;
        }

        E e = data[--size];
        data[size] = null;

        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }

        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void grow() {
        int newCapacity = (data.length * 3) / 2 + 1;
        grow(newCapacity);
    }

    @SuppressWarnings("unchecked")
    private void grow(int newCapacity) {
        E[] oldData = data;
        data = (E[]) new Object[newCapacity];
        System.arraycopy(oldData, 0, data, 0, oldData.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (var o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (var o : c) {
            changed |= remove(o);
        }

        return changed;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<E> iterator() {
        return new BagIterator();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        System.arraycopy(data, 0, array, 0, size);
        return array;
    }

    private class BagIterator implements Iterator<E> {

        /** Current position. */
        private int pointer;

        /** True if the current position is within bounds. */
        private boolean next;

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return (pointer < size);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public E next() {
            if (pointer == size) {
                throw new NoSuchElementException("No more elements");
            }

            E e = data[pointer++];
            next = true;

            return e;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            if (!next) {
                throw new IllegalStateException("No more elements");
            }

            next = false;
            Bag.this.remove(--pointer);
        }
    }
}

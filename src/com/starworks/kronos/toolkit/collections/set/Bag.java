package com.starworks.kronos.toolkit.collections.set;

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

    public E[] m_data;
    private int m_size;

    public Bag() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public Bag(int capacity) {
        this.m_data = (E[]) new Object[capacity];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean add(Object e) {
        if (m_size == m_data.length) {
            grow();
        }

        return internalAdd((E) e);
    }

    private boolean internalAdd(E e) {
        m_data[m_size++] = e;
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
        if (index >= m_data.length) {
            grow(index * 2);
        }

        m_size = Math.max(m_size, index + 1);
        m_data[index] = e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return m_data[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object e) {
        for (int i = 0; i < m_size; i++) {
            if (m_data[i].equals(e)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public E remove(int index) {
        E e = m_data[index];
        m_data[index] = m_data[--m_size];
        m_data[m_size] = null;

        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object e) {
        for (int i = 0; i < m_size; i++) {
            E current = m_data[i];

            if (e == current) {
                m_data[i] = m_data[--m_size];
                m_data[m_size] = null;
                return true;
            }
        }

        return false;
    }

    @Override
    public E pop() {
        if (m_size == 0) {
            return null;
        }

        E e = m_data[--m_size];
        m_data[m_size] = null;

        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        for (int i = 0; i < m_size; i++) {
            m_data[i] = null;
        }

        m_size = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return m_size;
    }

    public int capacity() {
        return m_data.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return m_size == 0;
    }

    private void grow() {
        int newCapacity = (m_data.length * 3) / 2 + 1;
        grow(newCapacity);
    }

    @SuppressWarnings("unchecked")
    private void grow(int newCapacity) {
        E[] oldData = m_data;
        m_data = (E[]) new Object[newCapacity];
        System.arraycopy(oldData, 0, m_data, 0, oldData.length);
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
        Object[] array = new Object[m_size];
        System.arraycopy(m_data, 0, array, 0, m_size);
        return array;
    }

    private class BagIterator implements Iterator<E> {

        /** Current position. */
        private int m_pointer;

        /** True if the current position is within bounds. */
        private boolean m_next;

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return (m_pointer < m_size);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public E next() {
            if (m_pointer == m_size) {
                throw new NoSuchElementException("No more elements");
            }

            E e = m_data[m_pointer++];
            m_next = true;

            return e;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            if (!m_next) {
                throw new IllegalStateException("No more elements");
            }

            m_next = false;
            Bag.this.remove(--m_pointer);
        }
    }
}

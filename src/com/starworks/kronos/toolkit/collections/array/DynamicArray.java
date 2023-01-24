package com.starworks.kronos.toolkit.collections.array;

import java.util.Arrays;
import java.util.Iterator;

public class DynamicArray<E> implements Array<E> {

    protected static final int DEFAULT_CAPACITY = 16;

    private Object[] m_data;
    private int m_capacity;
    private int m_size;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int capacity) {
        this.m_data = new Object[capacity];
        this.m_capacity = capacity;
        this.m_size = 0;
    }

    public boolean add(E value) {
        if (m_size >= m_capacity) {
            grow();

            if (m_size >= m_capacity) {
                return false;
            }
        }

        m_data[m_size++] = value;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + m_size);
        }

        return (E) m_data[index];
    }

    @Override
    public boolean set(int index, E value) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + m_size);
        }

        m_data[index] = value;
        return true;
    }

    @Override
    public boolean remove(int index) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + m_size);
        }

        for (int i = index; i < m_size - 1; i++) {
            m_data[i] = m_data[i + 1];
        }

        --m_size;
        return true;
    }

    @Override
    public boolean contains(E value) {
        for (int i = 0; i < m_size; i++) {
            if (m_data[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return m_size;
    }

    @Override
    public int capacity() {
        return m_capacity;
    }

    @Override
    public boolean isEmpty() {
        return m_size == 0;
    }

    private Object[] grow() {
        int idealCapacity = m_size << 1;
        if (idealCapacity < 0) {
            throw new OutOfMemoryError("Required array length " + idealCapacity + " is too large");
        }
        int newCapacity = Math.max(idealCapacity, m_capacity);
        if (newCapacity == m_capacity) {
            return m_data;
        }
        if (ensureCapacity(newCapacity)) {
            this.m_capacity = newCapacity;
            return m_data = Arrays.copyOf(m_data, newCapacity);
        }

        return hugeLength(m_data, m_capacity, idealCapacity);
    }

    private static Object[] hugeLength(Object[] items, int oldCapacity, int newCapacity) {
        int minLength = oldCapacity + newCapacity;

        if (minLength < 0) { // integer overflow
            throw new OutOfMemoryError("Required array length " + oldCapacity + " + " + newCapacity + " is too large");
        }
        if (minLength <= SOFT_MAX_ARRAY_LENGTH) {
            return Arrays.copyOf(items, SOFT_MAX_ARRAY_LENGTH);
        }
        return Arrays.copyOf(items, minLength);
    }

    private boolean ensureCapacity(int minCapacity) {
        if (minCapacity > 0 && minCapacity <= SOFT_MAX_ARRAY_LENGTH) {
            return true;
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<E> iterator() {
        return new GenericArrayIterator<E>((E[]) m_data, m_size);
    }
}

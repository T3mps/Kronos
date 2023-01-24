package com.starworks.kronos.toolkit.collections.array;

import java.util.Iterator;

public class GenericArrayIterator<E> implements Iterator<E> {

    private final E[] m_array;
    private int m_size;
    private int m_index;

    public GenericArrayIterator(E[] array, int size) {
        this.m_array = array;
        this.m_size = size;
        this.m_index = 0;
    }

    @Override
    public boolean hasNext() {
        return m_index < m_size;
    }

    @Override
    public E next() {
        return m_array[m_index++];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

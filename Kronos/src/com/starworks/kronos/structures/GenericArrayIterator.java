package com.starworks.kronos.structures;

import java.util.Iterator;

public class GenericArrayIterator<E> implements Iterator<E> {

    private final E[] array;
    private int size;
    private int index;

    public GenericArrayIterator(E[] array, int size) {
        this.array = array;
        this.size = size;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < size;
    }

    @Override
    public E next() {
        return array[index++];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

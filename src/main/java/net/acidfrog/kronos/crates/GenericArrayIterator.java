package net.acidfrog.kronos.crates;

import java.util.Iterator;

public class GenericArrayIterator<E> implements Iterator<E> {

    private final E[] array;
    private int index;

    public GenericArrayIterator(E[] array) {
        this.array = array;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
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

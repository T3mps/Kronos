package net.acidfrog.kronos.core.datastructure.array;

public final class ImmutableDynamicArray<E> extends DynamicArray<E> {

    public ImmutableDynamicArray(int initialCapacity) {
        super(initialCapacity);
    }

    public ImmutableDynamicArray(E[] array) {
        super(array);
    }

    public ImmutableDynamicArray(DynamicArray<E> array) {
        super(array);
    }

    @Override
    public E add(E element) {
        throw new UnsupportedOperationException("This is an immutable array.");
    }
    
    @Override
    public void addAll(Array<E> array) {
        throw new UnsupportedOperationException("This is an immutable array.");
    }

    @Override
    public void addAll(E[] array) {
        throw new UnsupportedOperationException("This is an immutable array.");
    }

    @Override
    public E set(int index, E value) {
        throw new UnsupportedOperationException("This is an immutable array.");
    }

    @Override
    public void remove(int index) {
        throw new UnsupportedOperationException("This is an immutable array.");
    }

    @Override
    public E remove(E element) {
        throw new UnsupportedOperationException("This is an immutable array.");
    }

    @Override
    public void remove(int index, int offset) {
        throw new UnsupportedOperationException("This is an immutable array.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This is an immutable array.");
    }

    @Override
    public void resize(int newSize) {
        throw new UnsupportedOperationException("This is an immutable array.");
    }

}

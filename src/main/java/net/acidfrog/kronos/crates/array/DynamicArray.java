package net.acidfrog.kronos.crates.array;

import java.util.Arrays;
import java.util.Iterator;

import net.acidfrog.kronos.crates.GenericArrayIterator;

public class DynamicArray<E> implements Array<E> {

    protected static final int DEFAULT_CAPACITY = 16;

    private Object[] data;
    private int capacity;
    private int size;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int capacity) {
        this.data = new Object[capacity];
        this.capacity = capacity;
        this.size = 0;
    }

    public boolean add(E value) {
        if (size >= capacity) {
            grow();

            if (size >= capacity) {
                return false;
            }
        }

        data[size++] = value;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        return (E) data[index];
    }

    @Override
    public boolean set(int index, E value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        data[index] = value;
        return true;
    }

    @Override
    public boolean remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }

        --size;
        return true;
    }

    @Override
    public boolean contains(E value) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private Object[] grow() {
        int idealCapacity = size << 1;
        if (idealCapacity < 0) {
            throw new OutOfMemoryError("Required array length " + idealCapacity + " is too large");
        }
        int newCapacity = Math.max(idealCapacity, capacity);
        if (newCapacity == capacity) {
            return data;
        }
        if (ensureCapacity(newCapacity)) {
            this.capacity = newCapacity;
            return data = Arrays.copyOf(data, newCapacity);
        }

        return hugeLength(data, capacity, idealCapacity);
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
        return new GenericArrayIterator<E>((E[]) data, size);
    }
}

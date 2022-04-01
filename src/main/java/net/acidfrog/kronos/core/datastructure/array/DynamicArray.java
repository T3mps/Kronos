package net.acidfrog.kronos.core.datastructure.array;

import java.util.Comparator;

import net.acidfrog.kronos.core.datastructure.algorithm.sort.Sort;
import net.acidfrog.kronos.mathk.Mathk;

public sealed class DynamicArray<E> implements Array<E> permits ImmutableDynamicArray<E> {

    private E[] array;
    private int size;

    public DynamicArray() {
        this(16);
    }

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        this.array = (E[]) new Object[initialCapacity];
    }

    public DynamicArray(E[] array) {
        this.array = array;
        this.size = array.length;
    }

    public DynamicArray(DynamicArray<E> array) {
        this.array = array.array;
        this.size = array.size;
    }

    public E add(E value) {
        if (size == array.length) resize(size << 1);
        array[size++] = value;
        return value;
    }

    public void addAll(Array<E> array) {
        if (size + array.size() > array.toArray().length) resize(size + array.size());
        
        System.arraycopy(array.toArray(), 0, this.array, size, array.size());
        size += array.size();
    }

    public void addAll(E[] array) {
        if (size + array.length > this.array.length) resize(size + array.length);
        
        System.arraycopy(array, 0, this.array, size, array.length);
        size += array.length;
    }

    public E set(int index, E value) {
        array[index] = value;
        return value;
    }

    public E push(E value) {
        return add(value);
    }

    public E get(int index) {
        return array[index];
    }

    public boolean contains(E value) {
        return contains(value, false);
    }

    public boolean contains(E value, boolean identity) {
        if (identity) {
            for (int i = 0; i < size; i++) if (array[i] == value) {
                return true;
            }
        } else {
            for (int i = 0; i < size; i++) if (array[i].equals(value)) {
                return true;
            }
        }

        return false;
    }

    public int indexOf(E value) {
        for (int i = 0; i < size; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(E value) {
        for (int i = size - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void remove(int index) {
        System.arraycopy(array, index + 1, array, index, size - index - 1);
        size--;
    }

    public void remove(int index, int offset) {
        System.arraycopy(array, index + offset, array, index, size - index - offset);
        size -= offset;
    }

    public E remove(E value) {
        for (int i = 0; i < size; i++) if (array[i] == value) {
            remove(i);
            break;
        }

        return value;
    }

    public E pop() {
        int index = size - 1;
        E t = array[index];
        array[--size] = null;
        return t;
    }

    public void clear() {
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public void resize(int newSize) {
        E[] newArray = (E[]) new Object[newSize];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }

    public void sort(Comparator<E> comparator) {
        Sort.tim(array, comparator);
    }

    public void reverse() {
        for (int i = 0; i < size / 2; i++) {
            E tmp = array[i];
            array[i] = array[size - i - 1];
            array[size - i - 1] = tmp;
        }
    }

    public void shuffle() {
        for (int i = 0; i < size; i++) {
            int j = (int) (Mathk.random() * size);
            E tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }

    public void shuffle(int times) {
        for (int i = 0; i < times; i++) {
            shuffle();
        }
    }

    public int size() {
        return size;
    }

    public int length() {
        for (int i = size - 1; i >= 0; i--) if (array[i] != null) {
            return i + 1;
        }

        return 0;
    }

    public E[] toArray() {
        return array;
    }
    
}

package net.acidfrog.kronos.core.datastructure;

import java.util.Comparator;

import net.acidfrog.kronos.core.datastructure.sort.QuickSort;
import net.acidfrog.kronos.mathk.Mathk;

public class Array<T> {

    private T[] array;
    private int size;

    public Array() {
        this(16);
    }

    @SuppressWarnings("unchecked")
    public Array(int initialCapacity) {
        this.array = (T[]) new Object[initialCapacity];
    }

    public Array(T[] array) {
        this.array = array;
        this.size = array.length;
    }

    public Array(Array<T> array) {
        this.array = array.array;
        this.size = array.size;
    }

    public void add(T value) {
        if (size == array.length) {
            resize(size << 1);
        }
        array[size++] = value;
    }

    public void add(int index, T value) {
        if (size == array.length) {
            resize(size << 1);
        }
        System.arraycopy(array, index, array, index + 1, size - index);
        array[index] = value;
        size++;
    }

    public void addAll(Array<T> array) {
        if (size + array.size > array.array.length) {
            resize(size + array.size);
        }
        System.arraycopy(array.array, 0, this.array, size, array.size);
        size += array.size;
    }

    public void addAll(T[] array) {
        if (size + array.length > this.array.length) {
            resize(size + array.length);
        }
        System.arraycopy(array, 0, this.array, size, array.length);
        size += array.length;
    }

    public void addAll(int index, Array<T> array) {
        if (size + array.size > array.array.length) {
            resize(size + array.size);
        }
        System.arraycopy(array.array, 0, this.array, index, array.size);
        size += array.size;
    }

    public void addAll(int index, T[] array) {
        if (size + array.length > this.array.length) {
            resize(size + array.length);
        }
        System.arraycopy(array, 0, this.array, index, array.length);
        size += array.length;
    }

    public void clear() {
        size = 0;
    }

    public T get(int index) {
        return array[index];
    }

    public Array<T> push(T value) {
        add(value);
        return this;
    }

    public T pop() {
        int index = size - 1;
        T t = array[index];
        array[--size] = null;
        return t;
    }

    public void set(int index, T value) {
        array[index] = value;
    }

    public void remove(int index) {
        System.arraycopy(array, index + 1, array, index, size - index - 1);
        size--;
    }

    public void remove(int index, int count) {
        System.arraycopy(array, index + count, array, index, size - index - count);
        size -= count;
    }

    public void remove(T t) {
        for (int i = 0; i < size; i++) {
            if (array[i] == t) {
                remove(i);
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void resize(int newSize) {
        T[] newArray = (T[]) new Object[newSize];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }

    public int size() {
        return size;
    }

    public int length() {
        for (int i = size - 1; i >= 0; i--) {
            if (array[i] != null) {
                return i + 1;
            }
        }
        return 0;
    }

    public T[] toArray() {
        return array;
    }

    public Array<T> clone() {
        return new Array<T>(array);
    }

    public boolean contains(T value) {
        for (int i = 0; i < size; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(T value) {
        for (int i = 0; i < size; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(T value) {
        for (int i = size - 1; i >= 0; i--) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public void sort() {
        // TODO: implement
    }

    public void sort(Comparator<T> comparator) {

    }

    public void reverse() {
        for (int i = 0; i < size / 2; i++) {
            T tmp = array[i];
            array[i] = array[size - i - 1];
            array[size - i - 1] = tmp;
        }
    }

    public void shuffle() {
        for (int i = 0; i < size; i++) {
            int j = (int) (Mathk.random() * size);
            T tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }

    public void shuffle(int times) {
        for (int i = 0; i < times; i++) {
            shuffle();
        }
    }
    
}

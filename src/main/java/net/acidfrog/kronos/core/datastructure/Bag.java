package net.acidfrog.kronos.core.datastructure;

import java.util.Iterator;

import net.acidfrog.kronos.mathk.Mathk;

/**
 * A bag is a collection of items that can be added to and removed from.
 * <p>
 * The items are not ordered.
 * 
 * @author Ethan Temprovich
 */
public class Bag<E> implements Iterable<E> {

    private static final int DEFAULT_INITIAL_CAPACITY = 64;

    private E[] data;
    private int size;

    public Bag() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public Bag(int capacity) {
        data = (E[]) new Object[capacity];
    }

    public void add (E e) {
		if (size == data.length) grow();
		data[size++] = e;
	}
    
    public void set (int index, E e) {
		if (index >= data.length) grow(index * 2);
		size = Mathk.max(size, index + 1);
		data[index] = e;
	}

    public E get(int index) {
        return data[index];
    }

    public boolean contains(E e) {
        for (int i = 0; i < size; i++) if (data[i].equals(e)) {
            return true;
        }
        return false;
    }

    public E remove(int index) {
        E e = data[index];
        data[index] = data[--size];
        data[size] = null;
        return e;
    }

    public boolean remove(E e) {
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

    public E removeLast() {
        if (size == 0) return null;

        E e = data[--size];
        data[size] = null;
        return e;
    }

    public void clear () {
		for (int i = 0; i < size; i++) data[i] = null;

		size = 0;
	}

    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void grow () {
		int newCapacity = (data.length * 3) / 2 + 1;
		grow(newCapacity);
	}

	@SuppressWarnings("unchecked")
	private void grow (int newCapacity) {
		E[] oldData = data;
		data = (E[])new Object[newCapacity];
		System.arraycopy(oldData, 0, data, 0, oldData.length);
	}

    @Override
    public Iterator<E> iterator() {
        return new BagIterator<E>();
    }
    
}

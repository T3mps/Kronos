package net.acidfrog.kronos.core.datastructure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Vector;

/**
 * A Deque Stack, renamed to a Deck to avoid confusion with the Java
 * {@link java.util.Stack} class.
 */
public class Deck<E> extends Vector<E> implements Deque<E>{

    private E[] array;
    private int size;

    public Deck() {
        this(16);
    }

    @SuppressWarnings("unchecked")
    public Deck(int initialCapacity) {
        this.array = (E[]) new Object[initialCapacity];
    }

    public Deck(E[] array) {
        this.array = array;
        this.size = array.length;
    }

    public Deck(Deck<E> array) {
        this.array = array.array;
        this.size = array.size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(array, size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) return (T[]) Arrays.copyOf(array, size, a.getClass());

        System.arraycopy(array, 0, a, 0, size);
        if (a.length > size) a[size] = null;

        return a;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) if (!contains(o)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) if (remove(o)) {
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (int i = 0; i < size; i++) if (!c.contains(array[i])) {
            remove(i);
            changed = true;
        }

        return changed;
    }

    @Override
    public void clear() {
        Arrays.fill(array, null);
        size = 0;
    }

    @Override
    public void addFirst(E e) {
        if (size == array.length) resize(size * 2);

        for (int i = size - 1; i >= 0; i--) {
            array[i + 1] = array[i];
        }

        array[0] = e;
        size++;
    }

    @Override
    public void addLast(E e) {
        if (size == array.length) resize(size * 2);

        array[size++] = e;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newsize) {
        E[] newArray = (E[]) new Object[newsize];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        if (isEmpty()) return null;

        E e = array[0];
        for (int i = 0; i < size - 1; i++) {
            array[i] = array[i + 1];
        }

        array[--size] = null;
        return e;
    }

    @Override
    public E removeLast() {
        if (isEmpty()) return null;

        E e = array[--size];
        array[size] = null;
        return e;
    }

    @Override
    public E pollFirst() {
        if (isEmpty()) return null;

        E e = array[0];
        for (int i = 0; i < size - 1; i++) {
            array[i] = array[i + 1];
        }

        array[--size] = null;
        return e;
    }

    @Override
    public E pollLast() {
        if (isEmpty()) return null;

        E e = array[--size];
        array[size] = null;
        return e;
    }

    @Override
    public E getFirst() {
        if (isEmpty()) return null;

        return array[0];
    }

    @Override
    public E getLast() {
        if (isEmpty()) return null;

        return array[size - 1];
    }

    @Override
    public E peekFirst() {
        if (isEmpty()) return null;

        return array[0];
    }

    @Override
    public E peekLast() {
        if (isEmpty()) return null;

        return array[size - 1];
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        for (int i = 0; i < size; i++) if (array[i].equals(o)) {
            remove(i);
            return true;
        }

        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        for (int i = size - 1; i >= 0; i--) if (array[i].equals(o)) {
            remove(i);
            return true;
        }

        return false;
    }

    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) add(e);

        return true;
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) if (array[i].equals(o)) {
            remove(i);
            return true;
        }

        return false;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) if (array[i].equals(o)) {
            return true;
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int index = 0;

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
                remove(index - 1);
            }

            private void remove(int index) {
                for (int i = index; i < size - 1; i++) {
                    array[i] = array[i + 1];
                }

                array[--size] = null;
            }

        };
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            private int index = size - 1;

            @Override
            public boolean hasNext() {
                return index >= 0;
            }

            @Override
            public E next() {
                return array[index--];
            }

            @Override
            public void remove() {
                remove(index + 1);
            }

            private void remove(int index) {
                for (int i = index; i < size - 1; i++) {
                    array[i] = array[i + 1];
                }

                array[--size] = null;
            }

        };
    }
    
}

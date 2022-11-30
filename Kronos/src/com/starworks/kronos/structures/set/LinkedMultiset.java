package com.starworks.kronos.structures.set;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class LinkedMultiset<E> implements MultiSet<E> {

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public LinkedMultiset() {
        this.head = new Node<E>(null);
        this.tail = new Node<E>(null);
        this.head.next = tail;
        this.tail.prev = head;
        this.size = 0;
    }

    public LinkedMultiset(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        if (head.value.equals(o)) {
            return true;
        }

        for (Node<E> node = head.next; node != tail; node = node.next) {
            if (node.value.equals(o)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedMultisetIterator();
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;
        for (Node<E> node = head.next; node != tail; node = node.next) {
            array[i++] = node.value;
        }
        
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }
        
        int i = 0;
        for (Node<E> node = head.next; node != tail; node = node.next) {
            a[i++] = (T) node.value;
        }

        if (a.length > size) {
            a[size] = null;
        }
        
        return a;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (contains(e)) {
            return false;
        }

        Node<E> node = new Node<E>(e);
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        size++;
        
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        if (head.value.equals(o)) {
            head.next.prev = head.prev;
            head.prev.next = head.next;
            head.value = null;
            head.next = null;
            head.prev = null;
            size--;
            return true;
        }

        for (Node<E> node = head.next; node != tail; node = node.next) {
            if (node.value.equals(o)) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                node.value = null;
                node.next = null;
                node.prev = null;
                size--;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (var o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;

        for (E e : c) {
            if (add(e)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;

        for (var o : c) {
            if (remove(o)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (Node<E> node = head.next; node != tail; node = node.next) {
            if (!c.contains(node.value)) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                node.value = null;
                node.next = null;
                node.prev = null;
                size--;
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        for (Node<E> node = head.next; node != tail; node = node.next) {
            node.value = null;
            node.next = null;
            node.prev = null;
        }

        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    @Override
    public void set(int index, E e) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        int i = 0;

        for (Node<E> node = head.next; node != tail; node = node.next) {
            if (i == index) {
                node.value = e;
                return;
            }

            i++;
        }
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        int i = 0;
        
        for (Node<E> node = head.next; node != tail; node = node.next) {
            if (i == index) {
                return node.value;
            }
        
            i++;
        }
        
        return null;
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        int i = 0;

        for (Node<E> node = head.next; node != tail; node = node.next) {
            if (i == index) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                E value = node.value;
                node.value = null;
                node.next = null;
                node.prev = null;
                size--;
                return value;
            }

            i++;
        }

        return null;
    }

    @Override
    public E pop() {
        if (size == 0) {
            return null;
        }

        Node<E> node = tail.prev;
        node.prev.next = tail;
        node.value = null;
        node.next = null;
        node.prev = null;
        size--;

        return node.value;
    }
    
    private static class Node<E> {

        public E value;
        public Node<E> prev;
        public Node<E> next;

        public Node(E value) {
            this.value = value;
        }
    }

    private final class LinkedMultisetIterator implements Iterator<E> {

        private Node<E> current;
        private Node<E> next;

        public LinkedMultisetIterator() {
            current = head;
            next = head.next;
        }

        @Override
        public boolean hasNext() {
            return next != tail;
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            current = next;
            next = next.next;
            return current.value;
        }

        @Override
        public void remove() {
            if (current == null) throw new IllegalStateException();
            LinkedMultiset.this.remove(current.value);
            current = null;
        }
    }
}

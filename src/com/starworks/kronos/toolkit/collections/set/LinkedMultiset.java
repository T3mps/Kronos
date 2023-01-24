package com.starworks.kronos.toolkit.collections.set;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class LinkedMultiset<E> implements MultiSet<E> {

    private Node<E> m_head;
    private Node<E> m_tail;
    private int m_size;

    public LinkedMultiset() {
        this.m_head = new Node<E>(null);
        this.m_tail = new Node<E>(null);
        this.m_head.m_next = m_tail;
        this.m_tail.m_prev = m_head;
        this.m_size = 0;
    }

    public LinkedMultiset(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    @Override
    public int size() {
        return m_size;
    }

    @Override
    public boolean isEmpty() {
        return m_size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        if (m_head.m_value.equals(o)) {
            return true;
        }

        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            if (node.m_value.equals(o)) {
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
        Object[] array = new Object[m_size];
        int i = 0;
        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            array[i++] = node.m_value;
        }
        
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < m_size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), m_size);
        }
        
        int i = 0;
        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            a[i++] = (T) node.m_value;
        }

        if (a.length > m_size) {
            a[m_size] = null;
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
        node.m_next = m_head.m_next;
        node.m_prev = m_head;
        m_head.m_next.m_prev = node;
        m_head.m_next = node;
        m_size++;
        
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        if (m_head.m_value.equals(o)) {
            m_head.m_next.m_prev = m_head.m_prev;
            m_head.m_prev.m_next = m_head.m_next;
            m_head.m_value = null;
            m_head.m_next = null;
            m_head.m_prev = null;
            m_size--;
            return true;
        }

        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            if (node.m_value.equals(o)) {
                node.m_prev.m_next = node.m_next;
                node.m_next.m_prev = node.m_prev;
                node.m_value = null;
                node.m_next = null;
                node.m_prev = null;
                m_size--;
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
        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            if (!c.contains(node.m_value)) {
                node.m_prev.m_next = node.m_next;
                node.m_next.m_prev = node.m_prev;
                node.m_value = null;
                node.m_next = null;
                node.m_prev = null;
                m_size--;
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            node.m_value = null;
            node.m_next = null;
            node.m_prev = null;
        }

        m_head.m_next = m_tail;
        m_tail.m_prev = m_head;
        m_size = 0;
    }

    @Override
    public void set(int index, E e) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException();
        }

        int i = 0;

        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            if (i == index) {
                node.m_value = e;
                return;
            }

            i++;
        }
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException();
        }
        
        int i = 0;
        
        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            if (i == index) {
                return node.m_value;
            }
        
            i++;
        }
        
        return null;
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException();
        }

        int i = 0;

        for (Node<E> node = m_head.m_next; node != m_tail; node = node.m_next) {
            if (i == index) {
                node.m_prev.m_next = node.m_next;
                node.m_next.m_prev = node.m_prev;
                E value = node.m_value;
                node.m_value = null;
                node.m_next = null;
                node.m_prev = null;
                m_size--;
                return value;
            }

            i++;
        }

        return null;
    }

    @Override
    public E pop() {
        if (m_size == 0) {
            return null;
        }

        Node<E> node = m_tail.m_prev;
        node.m_prev.m_next = m_tail;
        node.m_value = null;
        node.m_next = null;
        node.m_prev = null;
        m_size--;

        return node.m_value;
    }
    
    private static class Node<E> {

        public E m_value;
        public Node<E> m_prev;
        public Node<E> m_next;

        public Node(E value) {
            this.m_value = value;
        }
    }

    private final class LinkedMultisetIterator implements Iterator<E> {

        private Node<E> m_current;
        private Node<E> m_next;

        public LinkedMultisetIterator() {
            m_current = m_head;
            m_next = m_head.m_next;
        }

        @Override
        public boolean hasNext() {
            return m_next != m_tail;
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            m_current = m_next;
            m_next = m_next.m_next;
            return m_current.m_value;
        }

        @Override
        public void remove() {
            if (m_current == null) throw new IllegalStateException();
            LinkedMultiset.this.remove(m_current.m_value);
            m_current = null;
        }
    }
}

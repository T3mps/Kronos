package com.starworks.kronos.toolkit.collections.set;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class HashMultiset<E> implements MultiSet<E> {

    private int m_size;
    private Map<E, Integer> m_table;
    private Set<E> m_set;

    public HashMultiset() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public HashMultiset(int initialCapacity) {
        this.m_size = 0;
        this.m_table = new HashMap<E, Integer>(initialCapacity);
        this.m_set = m_table.keySet();
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
        return m_table.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new HashMultisetIterator();
    }

    @Override
    public Object[] toArray() {
        return m_set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return m_set.toArray(a);
    }

    @Override
    public boolean add(E e) {
        if (m_table.containsKey(e)) {
            m_table.put(e, m_table.get(e) + 1);
        } else {
            m_table.put(e, 1);
        }

        m_size++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        if (m_table.containsKey(o)) {
            int count = m_table.get(o);

            if (count == 1) {
                m_table.remove(o);
            } else {
                m_table.put((E) o, count - 1);
            }
            
            m_size--;
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (var o : c) {
            if (!m_table.containsKey(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (var e : c) {
            add(e);
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (var o : c) {
            remove(o);
        }

        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for (var o : m_set) {
            if (!c.contains(o)) {
                remove(o);
            }
        }

        return true;
    }

    @Override
    public void clear() {
        m_table.clear();
        m_size = 0;
    }

    @Override
    public void set(int index, E e) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException();
        }
        if (m_table.containsKey(e)) {
            m_table.put(e, m_table.get(e) + 1);
        } else {
            m_table.put(e, 1);
        }

        m_size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException();
        }

        return (E) m_set.toArray(new Object[0])[index];
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= m_size) {
            throw new IndexOutOfBoundsException();
        }

        E e = get(index);
        remove(e);

        return e;
    }

    @Override
    public E pop() {
        E e = get(m_size - 1);
        remove(e);

        return e;
    }
    
    public static final class Entry<E> {
    	
    	protected final Map.Entry<E, Integer> m_entry;
    	
    	public Entry(Map.Entry<E, Integer> entry) {
    		this.m_entry = entry;
    	}
    	
    	public E getElement() {
    		return m_entry.getKey();
    	}
    	
    	public int getCount() {
    		return m_entry.getValue();
    	}
    }

    private final class HashMultisetIterator implements Iterator<E> {

        private Iterator<E> m_iterator;
        private E m_next;
        private int m_count;
        private int m_index;

        public HashMultisetIterator() {
            m_iterator = m_set.iterator();
            m_next = m_iterator.next();
            m_count = m_table.get(m_next);
            m_index = 0;
        }

        @Override
        public boolean hasNext() {
            return m_next != null;
        }

        @Override
        public E next() {
            if (m_index == m_count) {
                m_next = m_iterator.next();
                m_count = m_table.get(m_next);
                m_index = 0;
            }
            m_index++;
            return m_next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

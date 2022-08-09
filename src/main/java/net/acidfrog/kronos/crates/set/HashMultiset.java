package net.acidfrog.kronos.crates.set;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class HashMultiset<E> implements MultiSet<E> {

    private int size;
    private Map<E, Integer> table;
    private Set<E> set;

    public HashMultiset() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public HashMultiset(int initialCapacity) {
        this.size = 0;
        this.table = new HashMap<E, Integer>(initialCapacity);
        this.set = table.keySet();
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
        return table.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new HashMultisetIterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean add(E e) {
        if (table.containsKey(e)) {
            table.put(e, table.get(e) + 1);
        } else {
            table.put(e, 1);
        }

        size++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        if (table.containsKey(o)) {
            int count = table.get(o);

            if (count == 1) {
                table.remove(o);
            } else {
                table.put((E) o, count - 1);
            }
            
            size--;
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (var o : c) {
            if (!table.containsKey(o)) {
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
        for (var o : set) {
            if (!c.contains(o)) {
                remove(o);
            }
        }

        return true;
    }

    @Override
    public void clear() {
        table.clear();
        size = 0;
    }

    @Override
    public void set(int index, E e) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (table.containsKey(e)) {
            table.put(e, table.get(e) + 1);
        } else {
            table.put(e, 1);
        }

        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        return (E) set.toArray(new Object[0])[index];
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        E e = get(index);
        remove(e);

        return e;
    }

    @Override
    public E pop() {
        E e = get(size - 1);
        remove(e);

        return e;
    }
    
    private final class HashMultisetIterator implements Iterator<E> {

        private Iterator<E> iterator;
        private E next;
        private int count;
        private int index;

        public HashMultisetIterator() {
            iterator = set.iterator();
            next = iterator.next();
            count = table.get(next);
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            if (index == count) {
                next = iterator.next();
                count = table.get(next);
                index = 0;
            }
            index++;
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }

    public static final class Entry<E> {

        protected final Map.Entry<E, Integer> entry;

        public Entry(Map.Entry<E, Integer> entry) {
            this.entry = entry;
        }

        public E getElement() {
            return entry.getKey();
        }

        public int getCount() {
            return entry.getValue();
        }
        
    }
}

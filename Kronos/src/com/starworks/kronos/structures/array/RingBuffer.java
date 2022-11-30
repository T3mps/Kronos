package com.starworks.kronos.structures.array;

public final class RingBuffer<E> {
    

    private final int capacity;
    private final Object[] buffer;
    private int head;
    private int tail;

    public RingBuffer(int capacity) {
        this.capacity = capacity + 1;
        this.buffer = new Object[this.capacity];
    }

    private int wrapIndex(int index) {
        int idx = index % capacity;
        if (idx < 0) {
            idx += capacity;
        }
        return idx;
    }

    private void shiftBlock(int idx0, int idx1) {
        if (idx0 >= idx1) {
            throw new IllegalArgumentException("idx0 must be less than idx1");
        }
        for (int i = idx1 - 1; idx0 <= i; i--) {
            set(i + 1, get(i));
        }
    }

    public int size() {
        return tail - head + (tail < head ? capacity : 0);
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || size() <= index) {
            throw new IndexOutOfBoundsException();
        }
        return (E) buffer[wrapIndex(head + index)];
    }

    @SuppressWarnings("unchecked")
    public E set(int i, E element) {
        if (i < 0 || size() <= i) {
            throw new IndexOutOfBoundsException();
        }
        int idx = wrapIndex(head + i);
        E old = (E) buffer[idx];
        buffer[idx] = element;
        return old;
    }

    public boolean add(E element) {
        if (size() == capacity - 1) {
            return false;
        }
        buffer[wrapIndex(tail)] = element;
        tail = wrapIndex(tail + 1);
        return true;
    }

    public void add(int index, E element) {
        int size = size();
        if (index < 0 || size < index) {
            throw new IndexOutOfBoundsException();
        }
        if (size == capacity - 1) {
            throw new IllegalStateException("buffer is full");
        }
        tail = wrapIndex(tail + 1);
        if (index < size) {
            shiftBlock(index, size);
        }

        set(index, element);
    }

    public E remove(int index) {
        int size = size();
        if (index < 0 || size <= index) {
            throw new IndexOutOfBoundsException();
        }
        E old = get(index);
        if (index > 0) {
            shiftBlock(0, index);
        }
        head = wrapIndex(head + 1);
        return old;
    }

    public void addOrReplace(E element) {
        if (size() == capacity - 1) {
            remove(0);
        }

        add(element);
    }

    public int indexOf(E element) {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (get(i).equals(element)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        RingBuffer<Integer> rb = new RingBuffer<>(5);
        for (int i = 0; i < 256; i++) {
            rb.addOrReplace(i);
            System.out.println(rb);
        }
    }
}

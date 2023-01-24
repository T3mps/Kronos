package com.starworks.kronos.toolkit.collections.queue;

public final class RingQueue<E> {

	private int m_length;
    private final int m_capacity;
    private final Object[] m_buffer;
    private int m_head;
    private int m_tail;

    public RingQueue(int capacity) {
        this.m_length = 0;
        this.m_capacity = capacity;
        this.m_buffer = new Object[capacity];
        this.m_head = 0;
        this.m_tail = -1;
    }

    public int length() {
        return m_length;
    }

    public int capacity() {
        return m_capacity;
    }
    
    public boolean isEmpty() {
        return m_length == 0;
    }

    public boolean isFull() {
        return m_length == m_capacity;
    }

    public boolean enqueue(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Attempted to enqueue a null value.");
        }
        if (m_length == m_capacity) {
            // throw new IllegalStateException("Attempted to enqueue value " + element + " to a full queue (" + m_length + "/" + m_length + ").");
            return false;
        }
        m_tail = (m_tail + 1) % m_capacity;
        m_buffer[m_tail] = element;
        m_length++;
        return true;
    }

    @SuppressWarnings("unchecked")
    public E dequeue() {
        if (m_length == 0) {
            throw new IllegalStateException("Attempted to dequeue value in an empty queue.");
        }

        E element = (E) m_buffer[m_head];
        m_head = (m_head + 1) % m_capacity;
        m_length--;
        return element;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        if (m_length == 0) {
            throw new IllegalStateException("Attempted to peek at an empty queue.");
        }

        return (E) m_buffer[m_head];
    }

    @SuppressWarnings("unchecked")
    public E peekAt(int index) {
        if (m_length == 0) {
            throw new IllegalStateException("Attempted to peek at an empty queue.");
        }

        if (index < 0 || index >= m_length) {
            throw new IndexOutOfBoundsException("Attempted to peek at index " + index + " in a queue of length " + m_length + ".");
        }

        return (E) m_buffer[(m_head + index) % m_capacity];
    }

    public void clear() {
        m_length = 0;
        m_head = 0;
        m_tail = -1;

        for (int i = 0; i < m_capacity; i++) {
            m_buffer[i] = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RingQueue[");
        for (int i = 0; i < m_length; i++) {
            sb.append(m_buffer[(m_head + i) % m_capacity]);
            sb.append(", ");
        }
        if (m_capacity > 0 && m_length > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }
}

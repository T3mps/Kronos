package com.starworks.kronos.toolkit.collections.stack;

import java.util.concurrent.locks.StampedLock;

public final class Stack<T> {

	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	private T[] m_stack;
	private int m_size;
	private final StampedLock m_lock;

	public Stack() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	@SuppressWarnings("unchecked")
	public Stack(int capacity) {
		this.m_stack = (T[]) new Object[capacity < DEFAULT_INITIAL_CAPACITY ? DEFAULT_INITIAL_CAPACITY : capacity];
		this.m_size = 0;
		this.m_lock = new StampedLock();
	}

	public void push(T item) {
		long stamp = m_lock.writeLock();
		try {
			if (m_size == m_stack.length) {
				resize(m_size + (m_size << 1));
			}

			m_stack[m_size++] = item;
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	public T pop() {
		long stamp = m_lock.writeLock();
		try {
			T item = m_stack[--m_size];
			m_stack[m_size] = null;
			if (m_size > 0 && m_size == m_stack.length / 4) {
				resize(m_stack.length / 2);
			}
			return item;
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	public T peek() {
		long stamp = m_lock.tryOptimisticRead();
		T item = null;
		try {
			item = m_stack[m_size - 1];
			if (!m_lock.validate(stamp)) {
				stamp = m_lock.readLock();
				item = m_stack[m_size - 1];
			}
		} finally {
			m_lock.unlockRead(stamp);
		}
		return item;
	}

	public T get(int index) {
		long stamp = m_lock.tryOptimisticRead();
		T item = m_stack[index];
		if (!m_lock.validate(stamp)) {
			stamp = m_lock.readLock();
			try {
				item = m_stack[index];
			} finally {
				m_lock.unlockRead(stamp);
			}
		}
		return item;
	}

	public void clear() {
		long stamp = m_lock.writeLock();
		try {
			for (int i = 0; i < m_size; i++) {
				m_stack[i] = null;
			}

			m_size = 0;
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	public boolean isEmpty() {
		long stamp = m_lock.tryOptimisticRead();
		boolean empty = m_size == 0;
		if (!m_lock.validate(stamp)) {
			stamp = m_lock.readLock();
			try {
				empty = m_size == 0;
			} finally {
				m_lock.unlockRead(stamp);
			}
		}
		return empty;
	}

	public int size() {
		long stamp = m_lock.tryOptimisticRead();
		int size = this.m_size;
		if (!m_lock.validate(stamp)) {
			stamp = m_lock.readLock();
			try {
				size = this.m_size;
			} finally {
				m_lock.unlockRead(stamp);
			}
		}
		return size;
	}

	@SuppressWarnings("unchecked")
	private void resize(int capacity) {
		T[] copy = m_stack;
		m_stack = (T[]) new Object[capacity];
		System.arraycopy(copy, 0, m_stack, 0, m_size);
	}
}

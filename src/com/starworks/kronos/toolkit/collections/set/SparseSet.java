package com.starworks.kronos.toolkit.collections.set;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class SparseSet<T> {

	public static final int DEFAULT_INITIAL_CAPACITY = 1 << 10;

	private int[] m_dense;
	private int[] m_sparse;
	private Object[] m_values;
	private int m_capacity;
	private final AtomicInteger m_size;

	public SparseSet() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	public SparseSet(int capacity) {
		this(new int[capacity], new int[capacity], new Object[capacity]);
	}

	public SparseSet(int[] dense, int[] sparse, Object[] values) {
		this.m_dense = dense;
		this.m_sparse = sparse;
		this.m_values = values;
		this.m_capacity = values.length;
		this.m_size = new AtomicInteger(0);
	}

	public T put(int key, T value) {
		T current = get(key);
		int index = current == null ? m_size.getAndIncrement() : m_sparse[key];
		if (index >= m_capacity) {
			grow();
		}
		m_dense[index] = key;
		m_sparse[key] = index;
		m_values[index] = value;
		return current;
	}

	public boolean remove(int key) {
		int index = m_sparse[key];
		if (index > m_size.get() || m_dense[index] != key) {
			return false;
		}
		int last = m_size.decrementAndGet();
		m_dense[index] = m_dense[last];
		m_sparse[m_dense[last]] = index;
		m_values[index] = m_values[last];
		m_values[last] = null;
		return true;
	}

	public T get(int key) {
		if (key >= m_capacity) {
			return null;
		}
		int index = m_sparse[key];
		if (index > m_size.get() || m_dense[index] != key) {
			return null;
		}
		return valueAt(index);
	}

	public int getKey(T value) {
		for (int i = 0; i < m_size.get(); i++) {
			if (m_values[i].equals(value)) {
				return m_dense[i];
			}
		}
		return -1;
	}

	public Boolean contains(int key) {
		int index = m_sparse[key];
		return index <= m_size.get() && m_dense[index] == key;
	}

	private void grow() {
		int newCapacity = m_capacity + (m_capacity >> 1);
		int[] newDense = new int[newCapacity];
		int[] newSparse = new int[newCapacity];
		Object[] newValues = new Object[newCapacity];
		System.arraycopy(m_dense, 0, newDense, 0, m_capacity);
		System.arraycopy(m_sparse, 0, newSparse, 0, m_capacity);
		System.arraycopy(m_values, 0, newValues, 0, m_capacity);
		m_dense = newDense;
		m_sparse = newSparse;
		m_values = newValues;
		m_capacity = newCapacity;
	}

	public int size() {
		return m_size.get();
	}

	public boolean isEmpty() {
		return m_size.get() == 0;
	}

	@SuppressWarnings("unchecked")
	private T valueAt(int index) {
		return (T) m_values[index];
	}

	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return new ObjectIterator<>((T[]) m_values, m_size.get());
	}

	public Stream<T> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
	}

	public int[] keys() {
		if (isEmpty()) {
			return null;
		}
		int length = m_size.get();
		int[] target = new int[length];
		System.arraycopy(m_dense, 0, target, 0, length);
		return target;
	}

	@SuppressWarnings("unchecked")
	public T[] values() {
		if (isEmpty()) {
			return null;
		}
		int length = m_size.get();
		T[] target = (T[]) Array.newInstance(m_values[0].getClass(), length);
		System.arraycopy(m_values, 0, target, 0, length);
		return target;
	}

	public int getCapacity() {
		return m_capacity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SparseSet [m_dense=");
		builder.append(Arrays.toString(m_dense));
		builder.append(", m_sparse=");
		builder.append(Arrays.toString(m_sparse));
		builder.append(", m_values=");
		builder.append(Arrays.toString(m_values));
		builder.append(", m_capacity=");
		builder.append(m_capacity);
		builder.append(", m_size=");
		builder.append(m_size);
		builder.append("]");
		return builder.toString();
	}

	public static final class ObjectIterator<V> implements Iterator<V> {

		private final V[] data;
		private final int limit;
		int next = 0;

		ObjectIterator(V[] data, int limit) {
			this.data = data;
			this.limit = limit;
		}

		@Override
		public boolean hasNext() {
			return next < limit;
		}

		@Override
		public V next() {
			return data[next++];
		}
	}
}

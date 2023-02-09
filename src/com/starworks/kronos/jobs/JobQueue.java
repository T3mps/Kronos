package com.starworks.kronos.jobs;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

public final class JobQueue implements Queue<Job<?>> {

	private static final int DEFAULT_CAPACITY = 1 << 4;

	private Job<?>[] m_heap;
	private final AtomicInteger m_size;
	private final StampedLock m_lock;

	public JobQueue() {
		this.m_heap = new Job<?>[DEFAULT_CAPACITY];
		this.m_size = new AtomicInteger(0);
		this.m_lock = new StampedLock();
	}

	@Override
	public boolean add(Job<?> job) {
		long stamp = m_lock.writeLock();
		try {
			int currentSize = m_size.get();
			if (currentSize == m_heap.length) {
				grow();
			}
			m_heap[currentSize] = job;
			siftUp(currentSize);
			if (!m_size.compareAndSet(currentSize, currentSize + 1)) {
				throw new ConcurrentModificationException("TaskQueue size changed while pushing task");
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
		return true;
	}

	@Override
	public boolean offer(Job<?> e) {
		return add(e);
	}

	@Override
	public boolean addAll(Collection<? extends Job<?>> c) {
		long stamp = m_lock.writeLock();
		try {
			for (Job<?> job : c) {
				add(job);
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
		return true;
	}

	public Job<?> poll() {
		long stamp = m_lock.writeLock();
		try {
			int size = m_size.get();
			Job<?> result = m_heap[0];
			if (size == 0 || result == null) {
				return null;
			}
			if (!m_size.compareAndSet(size, size - 1)) {
				throw new ConcurrentModificationException("TaskQueue size changed while popping task");
			}
			size = m_size.get();
			m_heap[0] = m_heap[size];
			m_heap[size] = null;
			siftDown(0);
			return result;
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	public Job<?> peek() {
		long stamp = m_lock.tryOptimisticRead();
		try {
			if (stamp == 0) {
				stamp = m_lock.readLock();
			}
			if (m_size.get() == 0) {
				return null;
			}
			return m_heap[0];
		} finally {
			if (m_lock.validate(stamp)) {
				m_lock.unlockRead(stamp);
			}
		}
	}

	@Override
	public Job<?> element() {
		long stamp = m_lock.tryOptimisticRead();
		try {
			if (stamp == 0) {
				stamp = m_lock.readLock();
			}
			Job<?> result = m_heap[0];
			if (m_size.get() == 0 || result == null) {
				throw new CompletionException("JobQueue is empty", null);
			}
			return result;
		} finally {
			if (m_lock.validate(stamp)) {
				m_lock.unlockRead(stamp);
			}
		}
	}

	@Override
	public boolean contains(Object o) {
		long stamp = m_lock.tryOptimisticRead();
		try {
			if (stamp == 0) {
				stamp = m_lock.readLock();
			}
			for (int i = 0; i < m_size.get(); i++) {
				if (m_heap[i] == o) {
					return true;
				}
			}
			return false;
		} finally {
			if (m_lock.validate(stamp)) {
				m_lock.unlockRead(stamp);
			}
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		long stamp = m_lock.tryOptimisticRead();
		try {
			if (stamp == 0) {
				stamp = m_lock.readLock();
			}
			for (Object o : c) {
				if (!contains(o)) {
					return false;
				}
			}
			return true;
		} finally {
			if (m_lock.validate(stamp)) {
				m_lock.unlockRead(stamp);
			}
		}
	}

	public boolean remove(Job<?> job) {
		long stamp = m_lock.writeLock();
		try {
			int size = m_size.get();
			for (int i = 0; i < size; i++) {
				if (m_heap[i] == job) {
					if (!m_size.compareAndSet(size, size - 1)) {
						throw new ConcurrentModificationException("TaskQueue size changed while removing task");
					}
					size = m_size.get();
					m_heap[i] = m_heap[size];
					m_heap[size] = null;
					siftDown(i);
					return true;
				}
			}
			return false;
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	@Override
	public boolean remove(Object o) {
		try {
			return remove((Job<?>) o);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public Job<?> remove() {
		long stamp = m_lock.writeLock();
		try {
			int size = m_size.get();
			Job<?> result = m_heap[0];
			if (size == 0 || result == null) {
				throw new CompletionException("JobQueue is empty", null);
			}

			if (!m_size.compareAndSet(size, size - 1)) {
				throw new ConcurrentModificationException("TaskQueue size changed while popping task");
			}
			size = m_size.get();
			m_heap[0] = m_heap[size];
			m_heap[size] = null;
			siftDown(0);
			return result;
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o : c) {
			remove(o);
		}
		return true;
	}

	@Override
	public void clear() {
		long stamp = m_lock.writeLock();
		try {
			for (int i = 0; i < m_size.get(); i++) {
				m_heap[i] = null;
			}
			m_size.set(0);
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return m_size.get();
	}

	public boolean isEmpty() {
		return m_size.get() == 0;
	}

	@Override
	public Iterator<Job<?>> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		Object[] result = new Object[m_size.get()];
		long stamp = m_lock.tryOptimisticRead();
		try {
			if (stamp == 0) {
				stamp = m_lock.readLock();
			}
			for (int i = 0; i < m_size.get(); i++) {
				result[i] = m_heap[i];
			}
			return result;
		} finally {
			if (m_lock.validate(stamp)) {
				m_lock.unlockRead(stamp);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < m_size.get()) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), m_size.get());
		}
		long stamp = m_lock.tryOptimisticRead();
		try {
			if (stamp == 0) {
				stamp = m_lock.readLock();
			}
			for (int i = 0; i < m_size.get(); i++) {
				a[i] = (T) m_heap[i];
			}
			return a;
		} finally {
			if (m_lock.validate(stamp)) {
				m_lock.unlockRead(stamp);
			}
		}
	}

	private void siftUp(int index) {
		int childIndex = index;
		int parentIndex = (childIndex - 1) >> 1;
		while (childIndex > 0 && m_heap[parentIndex].getPriority() < m_heap[childIndex].getPriority()) {
			swap(parentIndex, childIndex);
			childIndex = parentIndex;
			parentIndex = (childIndex - 1) >> 1;
		}
	}

	private void siftDown(int index) {
		int size = m_size.get();
		int leftIndex = (index << 1) + 1;
		int rightIndex = (index << 1) + 2;
		while (leftIndex < size) {
			int maxIndex = leftIndex;
			if (rightIndex < size && m_heap[leftIndex].getPriority() < m_heap[rightIndex].getPriority()) {
				maxIndex = rightIndex;
			}
			if (m_heap[index].getPriority() >= m_heap[maxIndex].getPriority()) {
				break;
			}
			swap(index, maxIndex);
			index = maxIndex;
			leftIndex = (index << 1) + 1;
			rightIndex = (index << 1) + 2;
		}
	}

	private void swap(int index1, int index2) {
		Job<?> temp = m_heap[index1];
		m_heap[index1] = m_heap[index2];
		m_heap[index2] = temp;
	}

	private void grow() {
		if (m_size.get() == 0) {
			m_heap = new Job<?>[DEFAULT_CAPACITY];
			return;
		}
		int newCapacity = m_heap.length + (m_heap.length >> 1);
		Job<?>[] newHeap = new Job<?>[newCapacity];
		System.arraycopy(m_heap, 0, newHeap, 0, m_size.get());
		m_heap = newHeap;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("JobQueue[");
		for (int i = 0; i < m_heap.length; i++) {
			Job<?> job = m_heap[i];
			if (job == null) continue;
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(job.toString());
		}
		sb.append(']');
		return sb.toString();
	}
}

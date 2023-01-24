package com.starworks.kronos.toolkit.collections.pool;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* This class represents a pool of objects that can be reused to avoid allocation. When an
* object is requested from the pool, if there are any objects available, one is returned.
* If there are no objects available, a new one is created using the #newInstance() method.
* When an object is returned to the pool, it is added to the pool for later reuse. If the
* pool is full, the object is discarded.
* 
* Any pool needed can be initialized in-line.
* 
* @author Ethan Temprovich
*/
public abstract class ObjectPool<T> {

	private final Deque<T> m_pool;
	private final AtomicInteger m_size;
	private final ReadWriteLock m_lock;

	public ObjectPool(int size) {
		this.m_pool = new ConcurrentLinkedDeque<T>();
		this.m_size = new AtomicInteger(0);
		this.m_lock = new ReentrantReadWriteLock();
	}

	protected abstract T create();

	public T obtain() {
		T object = m_pool.poll();
		if (object == null) {
			return create();
		}
		m_size.decrementAndGet();
		return object;
	}

	public void free(T object) {
		if (object == null) {
			throw new NullPointerException("Cannot free null object");
		}
		release(object);
		m_pool.offer(object);
		m_size.incrementAndGet();
	}

	public void freeAll(T[] objects) {
		if (objects == null) {
			throw new NullPointerException("Cannot free null objects");
		}
		m_lock.writeLock().lock();
		try {
			for (T object : objects) {
				if (object == null) {
					continue;
				}
				release(object);
				m_pool.offer(object);
				m_size.incrementAndGet();
			}
		} finally {
			m_lock.writeLock().unlock();
		}
	}

	public int size() {
		return m_size.get();
	}

	protected void release(T object) {
		if (object instanceof Poolable) {
			((Poolable) object).reset();
		}
	}

	public static interface Poolable {
		void reset();
	}
}
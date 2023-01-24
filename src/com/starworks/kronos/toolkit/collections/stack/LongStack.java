package com.starworks.kronos.toolkit.collections.stack;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.StampedLock;

import com.starworks.kronos.toolkit.memory.UnsafeSupport;

import sun.misc.Unsafe;

public class LongStack implements Closeable {

	private static final Unsafe unsafe = UnsafeSupport.getUnsafe();

	private static final int BYTES = Long.BYTES;
	
	private final AtomicLong m_index;
	private final StampedLock m_lock;
	private long m_capacity;
	private long m_address;

	public LongStack(long initialCapacity) {
		this.m_index = new AtomicLong(-BYTES);
		this.m_lock = new StampedLock();
		this.m_capacity = initialCapacity;
		this.m_address = unsafe.allocateMemory(initialCapacity);
	}

	public boolean push(long value) {
		long offset =  m_index.addAndGet(BYTES);
		if (offset >= m_capacity) {
			grow();
		}
		unsafe.putLong(m_address + offset, value);
		return true;
	}

	public long pop() {
		long index = m_index.get();
		if (index < 0) {
			return Long.MIN_VALUE;
		}
		long returnValue = unsafe.getLong(m_address + index);
		returnValue = m_index.compareAndSet(index, index - BYTES) ? returnValue : Integer.MIN_VALUE;
		return returnValue;
	}
	
	public void clear() {
		m_index.set(-BYTES);
	}

	public long size() {
		return (m_index.get() + BYTES) >>> 3;
	}
	
	public long capacity() {
		return m_capacity;
	}

	@Override
	public void close() {
		unsafe.freeMemory(m_address);
	}

	private void grow() {
		long stamp = m_lock.writeLock();
		try {
			long currentCapacity;
			if (m_index.get() >= (currentCapacity = m_capacity)) {
				long newCapacity = currentCapacity + (currentCapacity >>> 1);
				long newAddress = unsafe.allocateMemory(newCapacity);
				unsafe.copyMemory(m_address, newAddress, currentCapacity);
				m_capacity = newCapacity;
				m_address = newAddress;
			}
		} finally {
			m_lock.unlockWrite(stamp);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LongStack[capacity=");
		builder.append(m_capacity);
		builder.append("(off-heap)");
		builder.append(", size=");
		builder.append(size());
		builder.append(", address=");
		builder.append(m_address);
		builder.append("]");
		return builder.toString();
	}
}

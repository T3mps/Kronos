package com.starworks.kronos.toolkit.collections;

import java.io.Closeable;

import com.starworks.kronos.toolkit.memory.UnsafeSupport;

import sun.misc.Unsafe;

public final class BitField implements Closeable {

	private static final Unsafe unsafe = UnsafeSupport.getUnsafe();

	private static final int SHIFT = 3; // Shift right by 3 is equivalent to divide by 8
	private static final int MASK = 0x07; // ANDing by 7 is equivalent to modulo 8
	private static final long DEFAULT_CAPACITY = 1 << 6;

	private final long m_capacity;
	private long m_address;

	public BitField() {
		this(DEFAULT_CAPACITY);
	}

	public BitField(long capacity) {
		this.m_capacity = capacity;
		this.m_address = unsafe.allocateMemory(capacity >> SHIFT);
		unsafe.setMemory(m_address, capacity >> SHIFT, (byte) 0);
	}

	public BitField(BitField other) {
		this.m_capacity = other.m_capacity;
		this.m_address = unsafe.allocateMemory(m_capacity >> SHIFT);
		unsafe.copyMemory(other.m_address, m_address, m_capacity >> SHIFT);
	}

	public void set(long index) {
		long offset = index >> SHIFT;
		long bit = index & MASK;
		long value = unsafe.getByte(m_address + offset);
		value |= (1 << bit);
		unsafe.putByte(m_address + offset, (byte) value);
	}

	public void clear(long index) {
		long offset = index >> SHIFT;
		long bit = index & MASK;
		long value = unsafe.getByte(m_address + offset);
		value &= ~(1 << bit);
		unsafe.putByte(m_address + offset, (byte) value);
	}

	public boolean flip(long index) {
		long offset = index >> SHIFT;
		long bit = index & MASK;
		long value = unsafe.getByte(m_address + offset);
		value ^= (1 << bit);
		unsafe.putByte(m_address + offset, (byte) value);
		return (value & (1 << bit)) != 0;
	}

	public boolean get(long index) {
		long offset = index >> SHIFT;
		long bit = index & MASK;
		long value = unsafe.getByte(m_address + offset);
		return (value & (1 << bit)) != 0;
	}

	public BitField and(BitField other) {
		if (m_capacity != other.m_capacity) {
			throw new IllegalArgumentException("BitField sizes do not match, therefore bits do not align");
		}
		for (int i = 0; i < m_capacity; i++) {
			if (get(i) && other.get(i)) {
				set(i);
				continue;
			}

			clear(i);
		}
		return this;
	}

	public BitField or(BitField other) {
		if (m_capacity != other.m_capacity) {
			throw new IllegalArgumentException("BitField sizes do not match, therefore bits do not align");
		}
		for (int i = 0; i < m_capacity; i++) {
			if (get(i) || other.get(i)) {
				set(i);
				continue;
			}

			clear(i);
		}
		return this;
	}

	public BitField xor(BitField other) {
		if (m_capacity != other.m_capacity) {
			throw new IllegalArgumentException("BitField sizes do not match, therefore bits do not align");
		}
		for (int i = 0; i < m_capacity; i++) {
			if (get(i) ^ other.get(i)) {
				set(i);
				continue;
			}

			clear(i);
		}
		return this;
	}

	public BitField not() {
		for (int i = 0; i < m_capacity; i++) {
			flip(i);
		}
		return this;
	}

	public BitField andNot(BitField other) {
		if (m_capacity != other.m_capacity) {
			throw new IllegalArgumentException("BitField sizes do not match, therefore bits do not align");
		}
		for (int i = 0; i < m_capacity; i++) {
			if (get(i) && !other.get(i)) {
				set(i);
				continue;
			}

			clear(i);
		}
		return this;
	}

	public boolean intersects(BitField other) {
		if (m_capacity != other.m_capacity) {
			throw new IllegalArgumentException("BitField sizes do not match, therefore bits do not align");
		}
		for (int i = 0; i < m_capacity; i++) {
			if (get(i) && other.get(i)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(BitField other) {
		if (m_capacity != other.m_capacity) {
			throw new IllegalArgumentException("BitField sizes do not match, therefore bits do not align");
		}
		for (int i = 0; i < m_capacity; i++) {
			if (other.get(i) && !get(i)) {
				return false;
			}
		}
		return true;
	}

	public void clear() {
		unsafe.setMemory(m_address, m_capacity >> SHIFT, (byte) 0);
	}

	public boolean isEmpty() {
		for (int i = 0; i < m_capacity; i++) {
			if (get(i)) {
				return false;
			}
		}
		return true;
	}

	public long size() {
		return m_capacity;
	}

	public long cardinality() {
		long count = 0;
		for (int i = 0; i < m_capacity; i++) {
			if (get(i)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void close() {
		unsafe.freeMemory(m_address);
	}

	public long[] toArray() {
		long[] array = new long[(int) cardinality()];
		int index = 0;
		for (int i = 0; i < m_capacity; i++) {
			if (get(i)) {
				array[index++] = i;
			}
		}
		return array;
	}

	public int[] toArray(int[] array) {
		if (array.length < cardinality()) {
			array = new int[(int) cardinality()];
		}
		int index = 0;
		for (int i = 0; i < m_capacity; i++) {
			if (get(i)) {
				array[index++] = i;
			}
		}
		return array;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < m_capacity; i++) {
			sb.append(get(i) ? "1" : "0");
		}
		return sb.toString();
	}
}

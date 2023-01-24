package com.starworks.kronos.toolkit.concurrent;

import java.lang.reflect.Field;

import com.starworks.kronos.toolkit.memory.UnsafeSupport;

import sun.misc.Unsafe;

/**
 * A utility class for atomically updating an objects field using the
 * {@link Unsafe} class, without the need for explicit exception handling.
 *
 * @param <T> the type of the object being updated
 * @param <V> the type of the field being updated
 * @author Ethan Temprovich
 */
public abstract class AtomicUpdater<T, V> {

	private static final Unsafe unsafe = UnsafeSupport.getUnsafe();
	protected final long m_offset;

	private AtomicUpdater(Class<T> type, String fieldName) {
		try {
			Field field = type.getDeclaredField(fieldName);
			m_offset = unsafe.objectFieldOffset(field);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Failed to get field m_offset for field '" + fieldName + "' in class " + type.getName(), e);
		}
	}

	/**
	 * Creates a new {@code UncheckedUpdater} for updating a reference field of the
	 * given type.
	 *
	 * @param type      the class containing the field
	 * @param fieldName the name of the field
	 * @param <T>       the type of the object being updated
	 * @param <V>       the type of the field being updated
	 * @return a new {@code UncheckedUpdater} for updating the field
	 * @throws UncheckedUpdaterException if an error occurs while trying to get the
	 *                                   field m_offset
	 */
	public static <T, V> AtomicUpdater<T, V> forReference(Class<T> type, String fieldName) {
		return new Reference<T, V>(type, fieldName);
	}

	/**
	 * Creates a new {@code UncheckedUpdater} for updating an integer field of the
	 * given type.
	 *
	 * @param type      the class containing the field
	 * @param fieldName the name of the field
	 * @param <T>       the type of the object being updated
	 * @return a new {@code UncheckedUpdater} for updating the field
	 * @throws UncheckedUpdaterException if an error occurs while trying to get the
	 *                                   field m_offset
	 */
	public static <T> AtomicUpdater<T, Integer> forInteger(Class<T> type, String fieldName) {
		return new Int<T>(type, fieldName);
	}

	/**
	 * Creates a new {@code UncheckedUpdater} for updating a long field of the given
	 * type.
	 *
	 * @param type      the class containing the field
	 * @param fieldName the name of the field
	 * @param <T>       the type of the object being updated
	 * @return a new {@code UncheckedUpdater} for updating the field
	 * @throws UncheckedUpdaterException if an error occurs while trying to get the
	 *                                   field m_offset
	 */
	public static <T> AtomicUpdater<T, java.lang.Long> forLong(Class<T> type, String fieldName) {
		return new Long<T>(type, fieldName);
	}

	/**
	 * Atomically updates the field with a new value, if and only if its current
	 * value is equal to a specified expected value.
	 *
	 * @param obj    the object containing the field
	 * @param expect the expected value of the field
	 * @param update the new value to set
	 * @return {@code true} if the update was successful, {@code false} otherwise
	 */
	public abstract boolean compareAndSet(T obj, V expect, V update);

	private static final class Reference<T, V> extends AtomicUpdater<T, V> {

		private Reference(Class<T> type, String fieldName) {
			super(type, fieldName);
		}

		@Override
		public boolean compareAndSet(T obj, V expect, V update) {
			return unsafe.compareAndSwapObject(obj, m_offset, expect, update);
		}
	}

	private static final class Int<T> extends AtomicUpdater<T, Integer> {

		private Int(Class<T> type, String fieldName) {
			super(type, fieldName);
		}

		@Override
		public boolean compareAndSet(T obj, Integer expect, Integer update) {
			return unsafe.compareAndSwapInt(obj, m_offset, expect, update);
		}
	}

	private static final class Long<T> extends AtomicUpdater<T, java.lang.Long> {

		private Long(Class<T> type, String fieldName) {
			super(type, fieldName);
		}

		@Override
		public boolean compareAndSet(T obj, java.lang.Long expect, java.lang.Long update) {
			return unsafe.compareAndSwapLong(obj, m_offset, expect, update);
		}
	}
}
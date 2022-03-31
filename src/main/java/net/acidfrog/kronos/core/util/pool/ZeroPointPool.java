package net.acidfrog.kronos.core.util.pool;

import java.lang.reflect.Constructor;

import javax.management.ReflectionException;

import net.acidfrog.kronos.core.lang.Reflection;
import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.annotations.Null;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;

public final @Internal class ZeroPointPool<T> extends Pool<T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private final Constructor<T> constructor;

    public ZeroPointPool(Class<T> type) {
        this(type, DEFAULT_INITIAL_CAPACITY, Integer.MAX_VALUE);
    }

    public ZeroPointPool(Class<T> type, int initialCapacity) {
		this(type, initialCapacity, Integer.MAX_VALUE);
	}

    public ZeroPointPool(Class<T> type, int initialCapacity, int max) {
		super(initialCapacity, max);
		constructor = findConstructor(type);
		if (constructor == null) throw new KronosError(KronosErrorLibrary.INVALID_ZERO_POINT_CONSTRUCTOR);
	}

	@SuppressWarnings("unchecked")
    private @Null Constructor<T> findConstructor(Class<T> type) {
		try {
			return (Constructor<T>) Reflection.getConstructor(type, (Class[])null);
		} catch (Exception e1) {
			try {
				Constructor<T> constructor = (Constructor<T>) Reflection.getDeclaredConstructor(type, (Class[])null);
				constructor.setAccessible(true);
				return constructor;
			} catch (ReflectionException e2) {
				return null;
			}
		}
	}

    @Override
    protected T create() {
        try {
			return (T) constructor.newInstance((Object[]) null);
		} catch (Exception e) {
			throw new KronosError(KronosErrorLibrary.INVALID_ZERO_POINT_CONSTRUCTOR);
		}
    }
    
}

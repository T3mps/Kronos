package net.acidfrog.kronos.toolkit.internal.memory;

import net.acidfrog.kronos.toolkit.internal.UnsafeSupport;
import sun.misc.Unsafe;

public final class UncheckedReferenceUpdater<T, V> {

    private static final Unsafe UNSAFE = UnsafeSupport.getUnsafe();

    private final long offset;

    public UncheckedReferenceUpdater(final Class<T> tClass, final String fieldName) throws NoSuchFieldException {
        this.offset = UNSAFE.objectFieldOffset(tClass.getDeclaredField(fieldName));
    }

    public boolean compareAndSet(T obj, V expect, V update) {
        return UNSAFE.compareAndSwapObject(obj, offset, expect, update);
    }
}

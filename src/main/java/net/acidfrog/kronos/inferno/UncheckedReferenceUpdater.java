package net.acidfrog.kronos.inferno;

import net.acidfrog.kronos.crates.Crates;
import sun.misc.Unsafe;

public final class UncheckedReferenceUpdater<T, V> {

    private static final Unsafe UNSAFE = Crates.UNSAFE;

    private final long offset;

    public UncheckedReferenceUpdater(final Class<T> tClass, final String fieldName) throws NoSuchFieldException {
        this.offset = UNSAFE.objectFieldOffset(tClass.getDeclaredField(fieldName));
    }

    public boolean compareAndSet(T obj, V expect, V update) {
        return UNSAFE.compareAndSwapObject(obj, offset, expect, update);
    }
}

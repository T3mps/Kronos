package com.starworks.kronos.toolkit.collections.array;

import java.util.Iterator;

public interface Array<E> extends Iterable<E> {
    
    public abstract E get(int index);

    public abstract boolean set(int index, E value);

    public abstract boolean remove(int index);

    public abstract boolean contains(E value);

    public abstract int size();

    public abstract int capacity();

    public abstract boolean isEmpty();

    @Override
    public abstract Iterator<E> iterator();

    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
        if (prefLength > 0 && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
            return prefLength;
        }

        return hugeLength(oldLength, minGrowth);
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;

        if (minLength < 0) { // integer overflow
            throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
        }
        if (minLength <= SOFT_MAX_ARRAY_LENGTH) {
            return SOFT_MAX_ARRAY_LENGTH;
        }
        return minLength;
    }
}

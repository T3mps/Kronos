package com.starworks.kronos.structures.set;

import java.util.Set;

public interface MultiSet<E> extends Set<E> {

    static final int DEFAULT_INITIAL_CAPACITY = 16;

    public abstract void set(int index, E e);

    public abstract E get(int index);

    public abstract E remove(int index);

    public abstract E pop();
}

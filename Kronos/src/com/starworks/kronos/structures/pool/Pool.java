package com.starworks.kronos.structures.pool;

public interface Pool<E> {
    
    public abstract E get(int id);

    public abstract void clear();

    public abstract int size();
}

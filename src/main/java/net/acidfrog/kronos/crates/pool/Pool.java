package net.acidfrog.kronos.crates.pool;

public interface Pool<E> {
    
    public abstract E get(int id);

    public abstract void clear();

    public abstract int size();
}

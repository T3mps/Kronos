package net.acidfrog.kronos.crates.pool;

public interface Pool<E> {
    
    @SuppressWarnings("unchecked")
    public abstract <T> E push(T... objects);

    public abstract <T> E pop(int size);

    public abstract void clear();

    public abstract int size();
}

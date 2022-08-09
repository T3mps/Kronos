package net.acidfrog.kronos.crates;

public interface Ownable<T> {
    
    public abstract T getParent();

    public abstract Ownable<T> setParent(T parent);

}

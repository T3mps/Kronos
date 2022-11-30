package com.starworks.kronos.structures;

public interface Ownable<T> {
    
    public abstract T getParent();

    public abstract Ownable<T> setParent(T parent);

}

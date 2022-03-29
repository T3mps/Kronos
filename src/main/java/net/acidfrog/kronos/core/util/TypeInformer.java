package net.acidfrog.kronos.core.util;

@FunctionalInterface
public interface TypeInformer<T> {

    public T value();
    
}

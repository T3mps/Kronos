package net.acidfrog.kronos.core.util.factory;

@FunctionalInterface
public interface Factory<T> {

    public T create();
    
}

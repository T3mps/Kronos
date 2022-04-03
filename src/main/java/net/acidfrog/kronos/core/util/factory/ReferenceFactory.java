package net.acidfrog.kronos.core.util.factory;

import net.acidfrog.kronos.core.util.Reference;

public final class ReferenceFactory<T> implements Factory<T> {

    private final T value;

    public ReferenceFactory(T value) {
        this.value = value;
    }

    @Override
    public T create() {
        Reference<T> ref = new Reference<T>(value);
        return ref.get();
    }
    
}

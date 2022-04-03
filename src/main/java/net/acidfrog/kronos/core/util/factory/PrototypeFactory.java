package net.acidfrog.kronos.core.util.factory;

import java.lang.reflect.Method;

public final class PrototypeFactory<T> implements Factory<T> {

    private final T prototype;
    
    private transient Method method;
    
    public PrototypeFactory(T prototype, Method method) {
        this.prototype = prototype;
        this.method = method;
    }

    public PrototypeFactory(T prototype) {
        this.prototype = prototype;
        try {
            this.method = prototype.getClass().getMethod("clone", (Class[]) null);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("No clone method found for " + prototype.getClass().getName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T create() {
        try {
            return (T) method.invoke(prototype, (Object[]) null);
        } catch (final Exception e) {
            throw new RuntimeException("PrototypeCloneFactory: Failed to clone object", e);
        }
    }
}

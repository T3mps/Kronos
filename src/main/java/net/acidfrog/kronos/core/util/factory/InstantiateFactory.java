package net.acidfrog.kronos.core.util.factory;

import java.lang.reflect.Constructor;

public final class InstantiateFactory<T> implements Factory<T> {

    private final Class<T> type;
    private final Class<T>[] argTypes;
    private final Object[] args;
    private Constructor<T> constructor;

    public InstantiateFactory(Class<T> type, Class<T>[] argTypes, Object[] args) {
        this.type = type;
        this.argTypes = argTypes;
        this.args = args;
        findConstructor();
    }

    public InstantiateFactory(Class<T> type) {
        this.type = type;
        this.argTypes = null;
        this.args = null;
        findConstructor();
    }

    private void findConstructor() {
        try {
            if (argTypes == null) {
                constructor = type.getConstructor();
            } else {
                constructor = type.getConstructor(argTypes);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No constructor found for " + type.getName(), e);
        }
    }

    @Override
    public T create() {
        try {
            if (args == null) {
                return constructor.newInstance();
            } else {
                return constructor.newInstance(args);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + type.getName(), e);
        }
    }
    
}

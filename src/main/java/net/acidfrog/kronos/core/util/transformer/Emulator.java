package net.acidfrog.kronos.core.util.transformer;

import net.acidfrog.kronos.core.util.factory.PrototypeFactory;

public final class Emulator<T> implements Transformer<T, T> {

    public Emulator() {
    }

    @Override
    public T transform(T input) {
        if (input == null) return null;
        return new PrototypeFactory<T>(input).create();
    }


}

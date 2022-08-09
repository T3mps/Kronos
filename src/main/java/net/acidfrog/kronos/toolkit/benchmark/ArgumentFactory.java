package net.acidfrog.kronos.toolkit.benchmark;

@FunctionalInterface
public interface ArgumentFactory<T> {
    
    public abstract T create();

    @SuppressWarnings("unchecked")
    default Class<T> getArgumentType() {
        return (Class<T>) create().getClass();
    }
}

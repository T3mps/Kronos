package net.acidfrog.kronos.inferno;

import net.acidfrog.kronos.crates.pool.ChunkedPool.Identifiable;

public interface Entity extends Identifiable {
    
    public abstract Entity add(Object component);
    
    public abstract boolean remove(Object component);
    
    public abstract boolean removeType(Class<?> componentType);
    
    public abstract boolean has(Class<?> componentType);
    
    public abstract boolean contains(Object component);
    
    public abstract <S extends Enum<S>> Entity setState(S state);
    
    public abstract boolean isEnabled();
    
    public abstract Entity setEnabled(boolean enabled);
}

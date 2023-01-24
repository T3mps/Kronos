package com.starworks.kronos.serialization.old;

import com.starworks.kronos.toolkit.memory.MemoryBlock;

public interface Serializable {
    
    public abstract void serialize(MemoryBlock block);

    public abstract Serializable deserialize(MemoryBlock block);
}

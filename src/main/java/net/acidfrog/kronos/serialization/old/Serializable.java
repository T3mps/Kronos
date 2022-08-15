package net.acidfrog.kronos.serialization.old;

import org.lwjgl.system.MemoryUtil.MemoryAllocationReport;

import net.acidfrog.kronos.toolkit.internal.memory.MemoryBlock;

public interface Serializable {
    
    public abstract void serialize(MemoryBlock block);

    public abstract Serializable deserialize(MemoryBlock block);
}

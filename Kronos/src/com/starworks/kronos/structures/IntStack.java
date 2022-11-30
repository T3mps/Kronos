package com.starworks.kronos.structures;

import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicInteger;

import com.starworks.kronos.logging.Logger;
import com.starworks.kronos.logging.LoggerFactory;
import com.starworks.kronos.toolkit.internal.UnsafeSupport;

/**
 * A stack of ints whos capacity is determined in bytes at construction time. The stack is
 * thread safe.
 * 
 * TODO: Finish Javadoc
 * 
 * @author Ethan Temprovich
 */
public final class IntStack implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.get(IntStack.class);

    private static final int INT_BYTES = 4;
    private static final Unsafe UNSAFE = UnsafeSupport.getUnsafe();

    private final AtomicInteger pointer;
    private final long address;
    private final int capacity;

    public IntStack(int bytes) {
        this.pointer = new AtomicInteger(-INT_BYTES);
        this.address = UNSAFE.allocateMemory(bytes);
        this.capacity = bytes;
    }

    public boolean push(int n) {
        long offset = pointer.addAndGet(INT_BYTES);
        if (offset < capacity) {
            UNSAFE.putInt(address + offset, n);
            return true;
        }

        LOGGER.warn("IntStack overflow on value " + n + ": " + offset + "/" + capacity + " bytes used.");
        
        pointer.addAndGet(-INT_BYTES);
        return false;
    }

    public int pop() {
        int i = pointer.get();
        if (i < 0) {
            return Integer.MIN_VALUE;
        }
        
        return pointer.compareAndSet(i, i - INT_BYTES) ? UNSAFE.getInt(address + i) : Integer.MIN_VALUE;
    }

    public int size() {
        return (pointer.get() >> 2) + 1;
    }

    @Override
    public void close() {
        UNSAFE.freeMemory(address);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IdStack={");
        sb.append("capacity=").append(capacity).append("|off-heap");
        sb.append('}');
        return sb.toString();
    }
}

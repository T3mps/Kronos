package net.acidfrog.kronos.crates;

import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicInteger;

public final class IDStack implements AutoCloseable {

    private static final int INT_BYTES = 4;
    private static final Unsafe UNSAFE = Crates.UNSAFE;

    private final AtomicInteger index;
    private final long address;
    private final int capacity;

    public IDStack(int capacity) {
        this.index = new AtomicInteger(-INT_BYTES);
        this.address = UNSAFE.allocateMemory(capacity);
        this.capacity = capacity;
    }

    public int pop() {
        int i = index.get();
        if (i < 0) {
            return Integer.MIN_VALUE;
        }
        int returnValue = UNSAFE.getInt(address + i);
        returnValue = index.compareAndSet(i, i - INT_BYTES) ? returnValue : Integer.MIN_VALUE;
        return returnValue;
    }

    public boolean push(int id) {
        long offset = index.addAndGet(INT_BYTES);
        if (offset < capacity) {
            UNSAFE.putInt(address + offset, id);
            return true;
        }
        index.addAndGet(-INT_BYTES);
        return false;
    }

    public int size() {
        return (index.get() >> 2) + 1;
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

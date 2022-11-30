package com.starworks.kronos.inferno;

import sun.misc.Unsafe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.starworks.kronos.toolkit.internal.UnsafeSupport;

final class ClassIndex implements AutoCloseable {

    private static final Unsafe UNSAFE = UnsafeSupport.getUnsafe();

    private final static int INT_BYTES_SHIFT = 2;
    private static final int DEFAULT_HASH_BIT = 20; // 1MB -> about 1K types
    private static final int MIN_HASH_BIT = 14;
    private static final int MAX_HASH_BIT = 24;

    private final Map<Object, Integer> controlMap;
    private final int hashBit;
    private final AtomicBoolean useFallbackMap;
    private int index;
    private final int capacity;
    private final long memoryAddress;
    private final ClassValue<Integer> fallbackMap;

    protected ClassIndex() {
        this(DEFAULT_HASH_BIT);
    }

    protected ClassIndex(final int hashBit) {
        this.controlMap = new ConcurrentHashMap<Object, Integer>(1 << 10);
        this.hashBit = Math.min(Math.max(hashBit, MIN_HASH_BIT), MAX_HASH_BIT);
        this.useFallbackMap = new AtomicBoolean(false);
        this.index = 1;
        this.capacity = (1 << hashBit) << INT_BYTES_SHIFT;
        this.memoryAddress = UNSAFE.allocateMemory(capacity);
        this.fallbackMap = new ClassValue<Integer>() {

            @Override
            protected Integer computeValue(final Class<?> type) {
                return index++;
            }
        };

        ClassIndex.UNSAFE.setMemory(memoryAddress, capacity, (byte) 0b00000000);
    }

    private static final long getIdentityAddress(final long identityHashCode, final long address) {
        return address + (identityHashCode << INT_BYTES_SHIFT);
    }

    protected int addClass(Class<?> clazz) {
        return addObject(clazz);
    }

    protected int addObject(Object object) {
        if (useFallbackMap.get()) {
            return fallbackMap.get((Class<?>) object);
        }
        
        int identityHashCode = capacityHashCode(System.identityHashCode(object), hashBit);
        long identityAddress = getIdentityAddress(identityHashCode, memoryAddress);
        int currentIndex = UNSAFE.getInt(identityAddress);

        if (currentIndex == 0) {
            int index = fallbackMap.get((Class<?>) object);
            UNSAFE.putIntVolatile(null, identityAddress, index);
            controlMap.put(object, index);
            return index;
        }
        
        if (!controlMap.containsKey(object)) {
            int index = fallbackMap.get((Class<?>) object);
            useFallbackMap.set(true);
            return index;
        }

        return currentIndex;
    }

    protected int getHashBit() {
        return hashBit;
    }

    protected int getIndex(Class<?> clazz) {
        return getObjectIndex(clazz);
    }

    protected int getObjectIndex(Object clazz) {
        if (useFallbackMap.get()) {
            return fallbackMap.get((Class<?>) clazz);
        }
        int identityHashCode = capacityHashCode(System.identityHashCode(clazz), hashBit);
        return UNSAFE.getInt(getIdentityAddress(identityHashCode, memoryAddress));
    }

    protected int getObjectIndexVolatile(Object clazz) {
        if (useFallbackMap.get()) {
            return fallbackMap.get((Class<?>) clazz);
        }
        int identityHashCode = capacityHashCode(System.identityHashCode(clazz), hashBit);
        return UNSAFE.getIntVolatile(null, getIdentityAddress(identityHashCode, memoryAddress));
    }

    protected int getIndexOrAddClass(Class<?> clazz) {
        return getIndexOrAddObject(clazz);
    }

    protected int getIndexOrAddObject(Object clazz) {
        int value = getObjectIndexVolatile(clazz);
        if (value != 0) {
            return value;
        }
        return addObject(clazz);
    }

    protected int[] getIndexOrAddClassBatch(Class<?>[] classes) {
        int[] indexes = new int[classes.length];
        for (int i = 0; i < classes.length; i++) {
            indexes[i] = getIndexOrAddClass(classes[i]);
        }
        return indexes;
    }
    
    protected IndexKey getIndexKey(Object[] objects) {
        int length = objects.length;
        boolean[] checkArray = new boolean[index + length + 1];
        int min = Integer.MAX_VALUE, max = 0;

        for (int i = 0; i < length; i++) {
            int value = getIndex(objects[i].getClass());
            value = value == 0 ? getIndexOrAddClass(objects[i].getClass()) : value;
            if (checkArray[value]) {
                throw new IllegalArgumentException("Duplicate object types are not allowed");
            }

            checkArray[value] = true;
            min = Math.min(value, min);
            max = Math.max(value, max);
        }

        return new IndexKey(checkArray, min, max, length);
    }

    protected IndexKey getIndexKeyByType(Class<?>[] classes) {
        int length = classes.length;
        boolean[] checkArray = new boolean[index + length + 1];
        int min = Integer.MAX_VALUE, max = 0;

        for (int i = 0; i < length; i++) {
            int value = getIndex(classes[i]);
            value = value == 0 ? getIndexOrAddClass(classes[i]) : value;
            if (checkArray[value]) {
                throw new IllegalArgumentException("Duplicate object types are not allowed");
            }

            checkArray[value] = true;
            min = Math.min(value, min);
            max = Math.max(value, max);
        }

        return new IndexKey(checkArray, min, max, length);
    }

    private int capacityHashCode(int hashCode, int hashBits) {
        return hashCode >> (32 - hashBits);
    }

    protected int size() {
        return index - 1;
    }

    protected void useUseFallbackMap() {
        useFallbackMap.set(true);
    }

    @Override
    public void close() {
        controlMap.clear();
        UNSAFE.freeMemory(memoryAddress);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassIndex={");
        sb.append("hashBit=").append(hashBit);
        sb.append(", capacity=").append(capacity).append("|off-heap");
        sb.append('}');
        return sb.toString();
    }
}

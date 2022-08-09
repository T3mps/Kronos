package net.acidfrog.kronos.inferno.core;

import sun.misc.Unsafe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.acidfrog.kronos.crates.Crates;

public final class ClassIndex implements AutoCloseable {

    private static final Unsafe UNSAFE = Crates.UNSAFE;

    public final static int INT_BYTES_SHIFT = 2;
    public static final int DEFAULT_HASH_BIT = 20; // 1MB -> about 1K types
    public static final int MIN_HASH_BIT = 14;
    public static final int MAX_HASH_BIT = 24;

    private final Map<Object, Integer> controlMap;
    private final int hashBit;
    private final AtomicBoolean useFallbackMap;
    private final boolean fallbackMapEnabled;
    private int index;
    private final AtomicInteger atomicIndex;
    private final int capacity;
    private final long memoryAddress;
    private final ClassValue<Integer> fallbackMap;

    public ClassIndex() {
        this(DEFAULT_HASH_BIT, true);
    }

    public ClassIndex(final int hashBit, final boolean fallbackMapEnabled) {
        this.controlMap = new ConcurrentHashMap<Object, Integer>(1 << 10);
        this.hashBit = Math.min(Math.max(hashBit, MIN_HASH_BIT), MAX_HASH_BIT);
        this.useFallbackMap = new AtomicBoolean(false);
        this.fallbackMapEnabled = fallbackMapEnabled;
        this.index = 1;
        this.atomicIndex = new AtomicInteger(0);
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

    public int getHashBit() {
        return hashBit;
    }

    public int addClass(Class<?> clazz) {
        return addObject(clazz);
    }

    public int addObject(Object object) {
        if (useFallbackMap.get()) {
            return fallbackMap.get((Class<?>) object);
        }
        
        int identityHashCode = capacityHashCode(System.identityHashCode(object), hashBit);
        long identityAddress = getIdentityAddress(identityHashCode, memoryAddress);
        int currentIndex = UNSAFE.getInt(identityAddress);

        if (currentIndex == 0) {
            int index = fallbackMapEnabled ? fallbackMap.get((Class<?>) object) : atomicIndex.incrementAndGet();
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

    public int getIndex(Class<?> klass) {
        return getObjectIndex(klass);
    }

    public int getObjectIndex(Object klass) {
        if (useFallbackMap.get()) {
            return fallbackMap.get((Class<?>) klass);
        }
        int identityHashCode = capacityHashCode(System.identityHashCode(klass), hashBit);
        return UNSAFE.getInt(getIdentityAddress(identityHashCode, memoryAddress));
    }

    public int getObjectIndexVolatile(Object klass) {
        if (useFallbackMap.get()) {
            return fallbackMap.get((Class<?>) klass);
        }
        int identityHashCode = capacityHashCode(System.identityHashCode(klass), hashBit);
        return UNSAFE.getIntVolatile(null, getIdentityAddress(identityHashCode, memoryAddress));
    }

    public int getIndexOrAddClass(Class<?> klass) {
        return getIndexOrAddObject(klass);
    }

    public int getIndexOrAddObject(Object klass) {
        int value = getObjectIndexVolatile(klass);
        if (value != 0) {
            return value;
        }
        return addObject(klass);
    }

    public int[] getIndexOrAddClassBatch(Class<?>[] classes) {
        int[] indexes = new int[classes.length];
        for (int i = 0; i < classes.length; i++) {
            indexes[i] = getIndexOrAddClass(classes[i]);
        }
        return indexes;
    }
    
    public IndexKey getIndexKey(Object[] objects) {
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

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public IndexKey getIndexKeyByType(Class<?>[] classes) {
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

    public int size() {
        return fallbackMapEnabled ? index - 1 : atomicIndex.get();
    }

    public void useUseFallbackMap() {
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

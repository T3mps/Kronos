package com.starworks.kronos.toolkit.memory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import sun.misc.Unsafe;

public final class UnsafeSupport {
    
    private static final Unsafe unsafe = fetchUnsafe();

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    private static final Unsafe fetchUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException expected) {
            try {
                final Class<Unsafe> type = Unsafe.class;

                try {
                    Field field = type.getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    return type.cast(field.get(type));
                } catch (Exception ignored1) {
                    try {
                        for (var field : type.getDeclaredFields()) {
                            if (type.isAssignableFrom(field.getType())) {
                                field.setAccessible(true);
                                return type.cast(field.get(type));
                            }
                        }
                    } catch (Exception ignored2) {
                        try {
                            Constructor<Unsafe> constructor = type.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            return type.cast(constructor.newInstance());
                        } catch (Exception e) {
                            throw new RuntimeException("Unsafe unavailable", e);
                        }
                    }
                }
            } catch (Exception fatal) {
                throw new RuntimeException("Unsafe unavailable", fatal);
            }
        }

        throw new RuntimeException("Unable to acquire Unsafe instance");
    }

    public static final int ARRAY_BOOLEAN_BASE_OFFSET = unsafe.arrayBaseOffset(boolean[].class);

    public static final int ARRAY_BYTE_BASE_OFFSET = unsafe.arrayBaseOffset(byte[].class);

    public static final int ARRAY_SHORT_BASE_OFFSET = unsafe.arrayBaseOffset(short[].class);

    public static final int ARRAY_CHAR_BASE_OFFSET = unsafe.arrayBaseOffset(char[].class);

    public static final int ARRAY_INT_BASE_OFFSET = unsafe.arrayBaseOffset(int[].class);

    public static final int ARRAY_LONG_BASE_OFFSET = unsafe.arrayBaseOffset(long[].class);

    public static final int ARRAY_FLOAT_BASE_OFFSET = unsafe.arrayBaseOffset(float[].class);

    public static final int ARRAY_DOUBLE_BASE_OFFSET = unsafe.arrayBaseOffset(double[].class);

    public static final int ARRAY_OBJECT_BASE_OFFSET = unsafe.arrayBaseOffset(Object[].class);

    public static final int ARRAY_BOOLEAN_INDEX_SCALE = unsafe.arrayIndexScale(boolean[].class);

    public static final int ARRAY_BYTE_INDEX_SCALE = unsafe.arrayIndexScale(byte[].class);

    public static final int ARRAY_SHORT_INDEX_SCALE = unsafe.arrayIndexScale(short[].class);

    public static final int ARRAY_CHAR_INDEX_SCALE = unsafe.arrayIndexScale(char[].class);

    public static final int ARRAY_INT_INDEX_SCALE = unsafe.arrayIndexScale(int[].class);

    public static final int ARRAY_LONG_INDEX_SCALE = unsafe.arrayIndexScale(long[].class);

    public static final int ARRAY_FLOAT_INDEX_SCALE = unsafe.arrayIndexScale(float[].class);

    public static final int ARRAY_DOUBLE_INDEX_SCALE = unsafe.arrayIndexScale(double[].class);
    
    public static final int ARRAY_OBJECT_INDEX_SCALE = unsafe.arrayIndexScale(Object[].class);

    private UnsafeSupport() {
        throw new SecurityException("UnsafeSupport is a utility class and may NOT be instantiated");
    }

    public static long toAddress(Object obj) {
        Object[] array = new Object[] { obj };
        return normalize(unsafe.getInt(array, ARRAY_OBJECT_BASE_OFFSET));
    }

    public static Object fromAddress(long address) {
        Object[] array = new Object[] { null };
        unsafe.putInt(array, ARRAY_OBJECT_BASE_OFFSET, (int) address);
        return array[0];
    }

    public static long jvm7_32_sizeOf(Object obj) {
        return unsafe.getAddress(normalize(unsafe.getInt(obj, 4L)) + 12L);
    }

    public static long sizeOf(Object obj) {
        Set<Field> fields = new HashSet<Field>();
        Class<?> c = obj.getClass();

        while (c != Object.class) {
            for (var f : c.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    fields.add(f);
                }
            }

            c = c.getSuperclass();
        }
    
        // get offset
        long maxSize = 0;
        for (var field : fields) {
            long offset = unsafe.objectFieldOffset(field);

            if (offset > maxSize) {
                maxSize = offset;
            }
        }
    
        return round8(maxSize);
    }

    public static long sizeOf(Class<?> clazz) {
        long maxSize = headerSize(clazz);

        while (clazz != Object.class) {
            for (var f : clazz.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    long offset = unsafe.objectFieldOffset(f);
                    if (offset > maxSize) {
                        // Assume 1 byte of the field width. This is ok as it gets padded out at the end
                        maxSize = offset + 1;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        // The whole class always pads to a 8 bytes boundary, so we round up to 8 bytes.
        return round8(maxSize);
    }

    public static long headerSize(Object obj) {
        return headerSize(obj.getClass());
    }

    public static long headerSize(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        long len = 12; // JVM_64 has a 12 byte header 8 + 4 (with compressed pointers on)
        if (clazz.isArray()) {
            len += 4;
        }

        return len;
    }

    public static long firstFieldOffset(Object obj) {
        return firstFieldOffset(obj.getClass());
    }

    public static long firstFieldOffset(Class<?> clazz) {
        long minSize = round8(headerSize(clazz));

        while (clazz != Object.class) {
            for (var f : clazz.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    long offset = unsafe.objectFieldOffset(f);

                    if (offset < minSize) {
                        minSize = offset;
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }

        return minSize;
    }

    public static long sizeOfFields(Object obj) {
        return sizeOfFields(obj.getClass());
    }

    public static long sizeOfFields(Class<?> clazz) {
        return sizeOf(clazz) - firstFieldOffset(clazz);
    }

    private static long round8(final long n) {
        return ((n / 8 ) + 1) * 8;
    }

    private static long normalize(int value) {
        if (value >= 0) {
            return value;
        }

        return (~0L >>> 32) & value;
    }

    public static long objectFieldOffset(Field f) {
        return unsafe.objectFieldOffset(f);
    }
}
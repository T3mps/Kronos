package com.starworks.kronos.serialization;

import sun.misc.Unsafe;

import java.nio.charset.StandardCharsets;

import com.starworks.kronos.toolkit.internal.UnsafeSupport;

import static com.starworks.kronos.serialization.Kron.*;

public final class KronWriter {
    
    private static final Unsafe UNSAFE = UnsafeSupport.getUnsafe();

    private KronWriter() {
        throw new IllegalStateException("Cannot instantiate KronWriter");
    }

    // booleans

    public static int writeBytes(byte[] dest, int pointer, boolean value) {
        UNSAFE.putBoolean(dest, UnsafeSupport.ARRAY_BOOLEAN_BASE_OFFSET + pointer, value);
        pointer += BOOLEAN_BYTES;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, boolean[] values) {
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * BOOLEAN_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_BOOLEAN_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }

    public static boolean readBoolean(byte[] src, int pointer) {
        boolean value = UNSAFE.getBoolean(src, UnsafeSupport.ARRAY_BOOLEAN_BASE_OFFSET + pointer);
        return value;
    }

    public static boolean[] readBooleanArray(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        boolean[] values = new boolean[length];
        long bytes = length * BOOLEAN_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_BOOLEAN_BASE_OFFSET, bytes);
        return values;
    }

    // bytes

    public static int writeBytes(byte[] dest, int pointer, byte value) {
        UNSAFE.putByte(dest,  UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, value);
        pointer += BYTE_BYTES;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, byte[] values) {
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * BYTE_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }

    public static byte readByte(byte[] src, int pointer) {
        byte value = UNSAFE.getByte(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer);
        return value;
    }
    public static byte[] readByteArray(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        byte[] values = new byte[length];
        long bytes = length * BYTE_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET, bytes);
        return values;
    }

    // shorts

    public static int writeBytes(byte[] dest, int pointer, short value) {
        UNSAFE.putShort(dest, UnsafeSupport.ARRAY_SHORT_BASE_OFFSET + pointer, value);
        pointer += SHORT_BYTES;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, short[] values) {
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * SHORT_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_SHORT_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }

    public static short readShort(byte[] src, int pointer) {
        short value = UNSAFE.getShort(src, UnsafeSupport.ARRAY_SHORT_BASE_OFFSET + pointer);
        return value;
    }

    public static short[] readShortArray(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        short[] values = new short[length];
        long bytes = length * SHORT_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_SHORT_BASE_OFFSET, bytes);
        return values;
    }

    // chars

    public static int writeBytes(byte[] dest, int pointer, char value) {
        UNSAFE.putChar(dest, UnsafeSupport.ARRAY_CHAR_BASE_OFFSET + pointer, value);
        pointer += CHAR_BYTES;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, char[] values) {
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * CHAR_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_CHAR_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }

    public static char readChar(byte[] src, int pointer) {
        char value = UNSAFE.getChar(src, UnsafeSupport.ARRAY_CHAR_BASE_OFFSET + pointer);
        return value;
    }

    public static char[] readCharArray(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        char[] values = new char[length];
        long bytes = length * CHAR_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_CHAR_BASE_OFFSET, bytes);
        return values;
    }

    // ints

    public static int writeBytes(byte[] dest, int pointer, int value) {
        UNSAFE.putInt(dest, UnsafeSupport.ARRAY_INT_BASE_OFFSET + pointer, value);
        pointer += INT_BYTES;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, int[] values) {
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * INT_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_INT_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }

    public static int readInt(byte[] src, int pointer) {
        int value = UNSAFE.getInt(src, UnsafeSupport.ARRAY_INT_BASE_OFFSET + pointer);
        return value;
    }

    public static int[] readIntArray(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        int[] values = new int[length];
        long bytes = length * INT_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_INT_BASE_OFFSET, bytes);
        return values;
    }

    // longs

    public static int writeBytes(byte[] dest, int pointer, long value) {
        UNSAFE.putLong(dest, UnsafeSupport.ARRAY_LONG_BASE_OFFSET + pointer, value);
        pointer += LONG_BYTES;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, long[] values) {
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * LONG_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_LONG_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }


    public static long readLong(byte[] src, int pointer) {
        long value = UNSAFE.getLong(src, UnsafeSupport.ARRAY_LONG_BASE_OFFSET + pointer);
        return value;
    }

    public static long[] readLongArray(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        long[] values = new long[length];
        long bytes = length * LONG_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_LONG_BASE_OFFSET, bytes);
        return values;
    }

    // floats

    public static int writeBytes(byte[] dest, int pointer, float value) {
        UNSAFE.putFloat(dest, UnsafeSupport.ARRAY_FLOAT_BASE_OFFSET + pointer, value);
        pointer += FLOAT_BYTES;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, float[] values) {
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * FLOAT_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_FLOAT_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }

    public static float readFloat(byte[] src, int pointer) {
        float value = UNSAFE.getFloat(src, UnsafeSupport.ARRAY_FLOAT_BASE_OFFSET + pointer);
        return value;
    }

    public static float[] readFloatArray(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        float[] values = new float[length];
        long bytes = length * FLOAT_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_FLOAT_BASE_OFFSET, bytes);
        return values;
    }

    // doubles

    public static int writeBytes(byte[] dest, int pointer, double value) {
        UNSAFE.putDouble(dest, UnsafeSupport.ARRAY_DOUBLE_BASE_OFFSET + pointer, value);
        pointer += DOUBLE_BYTES;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, double[] values) {
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * DOUBLE_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_DOUBLE_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }

    public static double readDouble(byte[] src, int pointer) {
        double value = UNSAFE.getDouble(src, UnsafeSupport.ARRAY_DOUBLE_BASE_OFFSET + pointer);
        return value;
    }

    public static double[] readDoubleArray(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        double[] values = new double[length];
        long bytes = length * DOUBLE_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_DOUBLE_BASE_OFFSET, bytes);
        return values;
    }

    // strings

    public static int writeBytes(byte[] dest, int pointer, String value) {
        byte[] values = value.getBytes(StandardCharsets.UTF_8);
        pointer = writeBytes(dest, pointer, values.length);
        long bytes = values.length * BYTE_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET, dest, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += (int) bytes;
        return pointer;
    }

    public static String readString(byte[] src, int pointer) {
        int length = readInt(src, pointer);
        byte[] values = new byte[length];
        long bytes = length * BYTE_BYTES;
        UNSAFE.copyMemory(src, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer + INT_BYTES, values, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET, bytes);
        return new String(values, StandardCharsets.UTF_8);
    }
}

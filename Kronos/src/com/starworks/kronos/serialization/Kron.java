package com.starworks.kronos.serialization;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.starworks.kronos.toolkit.internal.UnsafeSupport;
import com.starworks.kronos.toolkit.internal.memory.MemoryBlock;

import static com.starworks.kronos.serialization.KronWriter.*;

public final class Kron {
    
    public static final int BOOLEAN_BYTES = UnsafeSupport.ARRAY_BOOLEAN_INDEX_SCALE; // platform (JVM) dependent
    public static final int BYTE_BYTES = Byte.BYTES;
    public static final int SHORT_BYTES = Short.BYTES;
    public static final int CHAR_BYTES = Character.BYTES;
    public static final int INT_BYTES = Integer.BYTES;
    public static final int LONG_BYTES = Long.BYTES;
    public static final int FLOAT_BYTES = Float.BYTES;
    public static final int DOUBLE_BYTES = Double.BYTES;

    public static final byte[] HEADER = "kron".getBytes();
    public static final short HEADER_SIZE = (short) HEADER.length;
    
    public static final byte MAJOR_VERSION = 0x01;
    public static final byte MINOR_VERSION = 0x00;
    public static final byte PATCH_VERSION = 0x00;

    public static final int MAGIC_NUMBER = 0x4B52524E; // KRON

    private static final int DEFAULT_CAPACITY = 1024;

    private byte[] data;
    private int pointer;
    
    private ClassSchema<?> currentClassSchema;

    public Kron() {
        this.data = new byte[DEFAULT_CAPACITY];
        this.pointer = 0;
        this.currentClassSchema = null;
        
        writeHeader();
    }

    private final void writeHeader() {
        this.pointer = writeBytes(data, pointer, HEADER);
        this.pointer = writeBytes(data, pointer, MAJOR_VERSION);
        this.pointer = writeBytes(data, pointer, MINOR_VERSION);
        this.pointer = writeBytes(data, pointer, PATCH_VERSION);
        this.pointer = writeBytes(data, pointer, MAGIC_NUMBER);
    }

    public static <T> ClassSchema<T> getSchema(Class<T> clazz) {
        return new ClassSchema<T>(clazz);
    }

    public void defineNextMemoryLayout(Class<?> clazz) {
        this.currentClassSchema = getSchema(clazz);
        pointer = currentClassSchema.getMemoryLayout(data, pointer);
        print();
    }

    public static boolean isSerializable(Field field) {
        return !field.isSynthetic() && !Modifier.isTransient(field.getModifiers());
    }

    private void print() {
        int ptr = 0;

        // read header
        String header = readString(data, ptr);
        ptr += INT_BYTES + header.length();

        // read version
        byte major = data[ptr++];
        byte minor = data[ptr++];
        byte patch = data[ptr++];
        
        // read magic number
        int magic = readInt(data, ptr);
        ptr += INT_BYTES;
        
        // read index
        int idx = readInt(data, ptr);
        ptr += INT_BYTES;

        // read field count
        int fieldCt = readInt(data, ptr);
        ptr += INT_BYTES;

        // print
        StringBuilder sb = new StringBuilder();
        sb.append("header: ").append(header).append(" | ");
        sb.append("version: ").append(major).append(".").append(minor).append(".").append(patch).append(" | ");
        sb.append("signature: ").append(magic).append(" | ");
        sb.append("index: ").append(idx).append(" | ");
        sb.append("field count: ").append(fieldCt).append(" | ");
        System.out.println(sb.toString() + "\n");

        MemoryBlock block = new MemoryBlock(data);
        block.setPointer(this.pointer);
        System.out.println(block.toString());
    }
}

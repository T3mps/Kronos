package com.starworks.kronos.serialization;


import static com.starworks.kronos.serialization.KronWriter.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.starworks.kronos.toolkit.memory.MemoryBlock;
import com.starworks.kronos.toolkit.memory.UnsafeSupport;

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

    private byte[] m_data;
    private int m_pointer;
    private ClassSchema<?> m_currentClassSchema;

    public Kron() {
        this.m_data = new byte[DEFAULT_CAPACITY];
        this.m_pointer = 0;
        this.m_currentClassSchema = null;
        
        writeHeader();
    }

    private final void writeHeader() {
        this.m_pointer = writeBytes(m_data, m_pointer, HEADER);
        this.m_pointer = writeBytes(m_data, m_pointer, MAJOR_VERSION);
        this.m_pointer = writeBytes(m_data, m_pointer, MINOR_VERSION);
        this.m_pointer = writeBytes(m_data, m_pointer, PATCH_VERSION);
        this.m_pointer = writeBytes(m_data, m_pointer, MAGIC_NUMBER);
    }

    public static <T> ClassSchema<T> getSchema(Class<T> clazz) {
        return new ClassSchema<T>(clazz);
    }

    public void defineNextMemoryLayout(Class<?> clazz) {
        this.m_currentClassSchema = getSchema(clazz);
        m_pointer = m_currentClassSchema.getMemoryLayout(m_data, m_pointer);
        print();
    }

    public static boolean isSerializable(Field field) {
        return !field.isSynthetic() && !Modifier.isTransient(field.getModifiers());
    }

    private void print() {
        int ptr = 0;

        // read header
        String header = readString(m_data, ptr);
        ptr += INT_BYTES + header.length();

        // read version
        byte major = m_data[ptr++];
        byte minor = m_data[ptr++];
        byte patch = m_data[ptr++];
        
        // read magic number
        int magic = readInt(m_data, ptr);
        ptr += INT_BYTES;
        
        // read index
        int idx = readInt(m_data, ptr);
        ptr += INT_BYTES;

        // read field count
        int fieldCt = readInt(m_data, ptr);
        ptr += INT_BYTES;

        // print
        StringBuilder sb = new StringBuilder();
        sb.append("header: ").append(header).append(" | ");
        sb.append("version: ").append(major).append(".").append(minor).append(".").append(patch).append(" | ");
        sb.append("signature: ").append(magic).append(" | ");
        sb.append("index: ").append(idx).append(" | ");
        sb.append("field count: ").append(fieldCt).append(" | ");
        System.out.println(sb.toString() + "\n");

        MemoryBlock block = new MemoryBlock(m_data);
        block.setPointer(this.m_pointer);
        System.out.println(block.toString());
    }
}

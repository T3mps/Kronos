package com.starworks.kronos.serialization;

import com.starworks.kronos.toolkit.collections.ClassMap;
import com.starworks.kronos.toolkit.memory.UnsafeSupport;

public class Serial {

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
    
    public static final ClassMap s_classMap = new ClassMap();

    private byte[] m_data;
    private int m_pointer;
    private ClassSchema<?> m_currentClassSchema;

    public Serial() {
        this.m_data = new byte[DEFAULT_CAPACITY];
        this.m_pointer = 0;
        this.m_currentClassSchema = null;
    }
}

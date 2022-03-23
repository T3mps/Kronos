package net.acidfrog.kronos.core.io.serialization;

import static net.acidfrog.kronos.core.io.serialization.KronParser.*;

public class KronField extends KronContainer {
    
    public enum Type {
    
        UNKNOWN((byte) 0), BOOLEAN((byte) 1), BYTE((byte) 2), CHAR((byte) 3), SHORT((byte) 4), INT((byte) 5), LONG((byte) 6), FLOAT((byte) 7), DOUBLE((byte) 8);
    
        public final byte value;
        private static final int BYTE_SIZE_SHIFT = 3;

        private Type(byte value) {
            this.value = value;
        }

        public static Type getType(byte value) {
            for (Type type : Type.values()) {
                if (type.value == value) return type;
            }
            return UNKNOWN;
        }

        public static int sizeOf(Type type) {
            switch (type) {
                case BOOLEAN: return 1;
                case BYTE:    return Byte.SIZE      >> BYTE_SIZE_SHIFT;
                case CHAR:    return Character.SIZE >> BYTE_SIZE_SHIFT;
                case SHORT:   return Short.SIZE     >> BYTE_SIZE_SHIFT;
                case INT:     return Integer.SIZE   >> BYTE_SIZE_SHIFT;
                case LONG:    return Long.SIZE      >> BYTE_SIZE_SHIFT;
                case FLOAT:   return Float.SIZE     >> BYTE_SIZE_SHIFT;
                case DOUBLE:  return Double.SIZE    >> BYTE_SIZE_SHIFT;
                default: assert(false);
            }

            return 0;
        }
    }

    public byte type;
    public byte[] data;

    private KronField() {
        super(KronContainer.Type.FIELD);
        this.type = Type.UNKNOWN.value;
        this.data = null;
        this.nameLength = 0;
        this.name = null;
    }

    public static KronField create(String name, Object value) throws KronException {
        if (!(value instanceof Number || value instanceof Boolean)) throw new KronException("Invalid type: " + value.getClass().getName());

        KronField field = new KronField();
        if (value instanceof Boolean) field = create(name, (boolean) value);
        else if (value instanceof Byte) field = create(name, (byte) value);
        else if (value instanceof Character) field = create(name, (char) value);
        else if (value instanceof Short) field = create(name, (short) value);
        else if (value instanceof Integer) field = create(name, (int) value);
        else if (value instanceof Long) field = create(name, (long) value);
        else if (value instanceof Float) field = create(name, (float) value);
        else if (value instanceof Double) field = create(name, (double) value);
        else throw new KronException("Invalid type: " + value.getClass().getName());
        
        return field;
    }

    public static KronField create(String name, boolean value) {
        KronField field = new KronField();
        field.setName(name);
        field.type = Type.BOOLEAN.value;
        field.data = new byte[Type.sizeOf(Type.BOOLEAN)];
        writeBytes(field.data, DEFAULT_POINTER, value);
        return field;
    }

    public static KronField create(String name, byte value) {
        KronField field = new KronField();
        field.setName(name);
        field.type = Type.BYTE.value;
        field.data = new byte[Type.sizeOf(Type.BYTE)];
        writeBytes(field.data, DEFAULT_POINTER, value);
        return field;
    }

    public static KronField create(String name, char value) {
        KronField field = new KronField();
        field.setName(name);
        field.type = Type.CHAR.value;
        field.data = new byte[Type.sizeOf(Type.CHAR)];
        writeBytes(field.data, DEFAULT_POINTER, value);
        return field;
    }

    public static KronField create(String name, short value) {
        KronField field = new KronField();
        field.setName(name);
        field.type = Type.SHORT.value;
        field.data = new byte[Type.sizeOf(Type.SHORT)];
        writeBytes(field.data, DEFAULT_POINTER, value);
        return field;
    }

    public static KronField create(String name, int value) {
        KronField field = new KronField();
        field.setName(name);
        field.type = Type.INT.value;
        field.data = new byte[Type.sizeOf(Type.INT)];
        writeBytes(field.data, DEFAULT_POINTER, value);
        return field;
    }

    public static KronField create(String name, long value) {
        KronField field = new KronField();
        field.setName(name);
        field.type = Type.LONG.value;
        field.data = new byte[Type.sizeOf(Type.LONG)];
        writeBytes(field.data, DEFAULT_POINTER, value);
        return field;
    }

    public static KronField create(String name, float value) {
        KronField field = new KronField();
        field.setName(name);
        field.type = Type.FLOAT.value;
        field.data = new byte[Type.sizeOf(Type.FLOAT)];
        writeBytes(field.data, DEFAULT_POINTER, value);
        return field;
    }

    public static KronField create(String name, double value) {
        KronField field = new KronField();
        field.setName(name);
        field.type = Type.DOUBLE.value;
        field.data = new byte[Type.sizeOf(Type.DOUBLE)];
        writeBytes(field.data, DEFAULT_POINTER, value);
        return field;
    }

    @Override
    public void setName(String name) {
        this.nameLength = (short) name.length();
        this.name = name.getBytes();
    }

    public boolean getBoolean() {
        return readBoolean(data, DEFAULT_POINTER);
    }

    public byte getByte() {
        return readByte(data, DEFAULT_POINTER);
    }

    public char getChar() {
        return readChar(data, DEFAULT_POINTER);
    }

    public short getShort() {
        return readShort(data, DEFAULT_POINTER);
    }

    public int getInt() {
        return readInt(data, DEFAULT_POINTER);
    }

    public long getLong() {
        return readLong(data, DEFAULT_POINTER);
    }

    public float getFloat() {
        return readFloat(data, DEFAULT_POINTER);
    }

    public double getDouble() {
        return readDouble(data, DEFAULT_POINTER);
    }

    public int getBytes(byte[] dest, int pointer) {
        pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
        pointer = writeBytes(dest, pointer, type);
        pointer = writeBytes(dest, pointer, data);
        pointer = writeBytes(dest, pointer, nameLength);
        pointer = writeBytes(dest, pointer, name);
        return pointer;
    }

    @Override
    public int size() {
        return Type.sizeOf(Type.BYTE)  +
               Type.sizeOf(Type.SHORT) +
               name.length             +
               Type.sizeOf(Type.BYTE)  +
               data.length;               
    }

}

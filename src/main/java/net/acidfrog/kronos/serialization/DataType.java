package net.acidfrog.kronos.serialization;

public enum DataType {

    BOOLEAN(Kron.BOOLEAN_BYTES),
    BYTE(Kron.BYTE_BYTES),
    SHORT(Kron.SHORT_BYTES),
    CHAR(Kron.CHAR_BYTES),
    INT(Kron.INT_BYTES),
    LONG(Kron.LONG_BYTES),
    FLOAT(Kron.FLOAT_BYTES),
    DOUBLE(Kron.DOUBLE_BYTES);
    // TODO: add support for other objects

    private final int size;

    private DataType(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }

    public static DataType get(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        if (clazz.equals(Boolean.class)   || clazz.equals(boolean.class) || clazz.equals(Boolean.TYPE) ||
            clazz.equals(boolean[].class) || clazz.equals(Boolean[].class)) {
            return BOOLEAN;
        }

        if (clazz.equals(Byte.class)   || clazz.equals(byte.class) || clazz.equals(Byte.TYPE) ||
            clazz.equals(byte[].class) || clazz.equals(Byte[].class)) {
            return BYTE;
        }

        if (clazz.equals(Short.class)   || clazz.equals(short.class) || clazz.equals(Short.TYPE) ||
            clazz.equals(short[].class) || clazz.equals(Short[].class)) {
            return SHORT;
        }

        if (clazz.equals(Character.class) || clazz.equals(char.class) || clazz.equals(Character.TYPE) ||
            clazz.equals(char[].class)    || clazz.equals(Character[].class)) {
            return CHAR;
        }

        if (clazz.equals(Integer.class) || clazz.equals(int.class) || clazz.equals(Integer.TYPE) ||
            clazz.equals(int[].class)   || clazz.equals(Integer[].class)) {
            return INT;
        }

        if (clazz.equals(Long.class)   || clazz.equals(long.class) || clazz.equals(Long.TYPE) ||
            clazz.equals(long[].class) || clazz.equals(Long[].class)) {
            return LONG;
        }

        if (clazz.equals(Float.class)   || clazz.equals(float.class) || clazz.equals(Float.TYPE) ||
            clazz.equals(float[].class) || clazz.equals(Float[].class)) {
            return FLOAT;
        }

        if (clazz.equals(Double.class)   || clazz.equals(double.class) || clazz.equals(Double.TYPE) ||
            clazz.equals(double[].class) || clazz.equals(Double[].class)) {
            return DOUBLE;
        }

        

        return null;
    }
}

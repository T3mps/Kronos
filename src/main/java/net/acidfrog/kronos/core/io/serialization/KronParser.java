package net.acidfrog.kronos.core.io.serialization;

import net.acidfrog.kronos.core.io.serialization.KronField.Type;

public class KronParser {
   
    public static final int BITMASK         = 0xFF;
    public static final int DEFAULT_POINTER = 0;

    public static boolean readBoolean(byte[] src, int pointer) {
        return src[pointer] != 0;
    }

    public static void readBooleans(byte[] src, int pointer, boolean[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readBoolean(src, pointer);
			pointer += Type.sizeOf(Type.BOOLEAN);
		}
	}

    public static int writeBytes(byte[] dest, int pointer, boolean value) {
        dest[pointer++] = (byte) (value ? 1 : 0);
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, boolean[] src) {
		for (int i = 0; i < src.length; i++) pointer = writeBytes(dest, pointer, src[i]);
		return pointer;
	}

    public static byte readByte(byte[] src, int pointer) {
        return src[pointer];
    }

    public static void readBytes(byte[] src, int pointer, byte[] dest) {
		for (int i = 0; i < dest.length; i++) dest[i] = src[pointer + i];
	}

    public static int writeBytes(byte[] dest, int pointer, byte value) {
        dest[pointer++] = value;
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, byte[] src) {
        for (int i = 0; i < src.length; i++) dest[pointer++] = src[i];
        return pointer;
    }

    public static char readChar(byte[] src, int pointer) {
        return (char) (src[pointer + 0] <<  8 |
                       src[pointer + 1] <<  0);
    }

    public static char[] readChars(byte[] src, int pointer, char[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readChar(src, pointer);
			pointer += Type.sizeOf(Type.CHAR);
		}
        return dest;
	}

    public static int writeBytes(byte[] dest, int pointer, char value) {
        dest[pointer++] = (byte) ((value >> 8) & BITMASK);
        dest[pointer++] = (byte) ((value >> 0) & BITMASK);
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, char[] src) {
		for (int i = 0; i < src.length; i++) pointer = writeBytes(dest, pointer, src[i]);
		return pointer;
	}

    public static short readShort(byte[] src, int pointer) {
        return (short) (src[pointer + 0] <<  8 |
                        src[pointer + 1] <<  0);
    }

    public static short[] readShorts(byte[] src, int pointer, short[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readShort(src, pointer);
			pointer += Type.sizeOf(Type.SHORT);
		}
        return dest;
	}

    public static int writeBytes(byte[] dest, int pointer, short value) {
        dest[pointer++] = (byte) ((value >> 8) & BITMASK);
        dest[pointer++] = (byte) ((value >> 0) & BITMASK);
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, short[] src) {
		for (int i = 0; i < src.length; i++) pointer = writeBytes(dest, pointer, src[i]);
		return pointer;
	}

    public static int readInt(byte[] src, int pointer) {
        return (int) (src[pointer + 0] << 24 |
                      src[pointer + 1] << 16 |
                      src[pointer + 2] <<  8 |
                      src[pointer + 3] <<  0);
    }

    public static int[] readInts(byte[] src, int pointer, int[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readInt(src, pointer);
			pointer += Type.sizeOf(Type.INT);
		}
        return dest;
	}

    public static int writeBytes(byte[] dest, int pointer, int value) {
        dest[pointer++] = (byte) ((value >> 24) & BITMASK);
        dest[pointer++] = (byte) ((value >> 16) & BITMASK);
        dest[pointer++] = (byte) ((value >>  8) & BITMASK);
        dest[pointer++] = (byte) ((value >>  0) & BITMASK);
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, int[] src) {
		for (int i = 0; i < src.length; i++) pointer = writeBytes(dest, pointer, src[i]);
		return pointer;
	}

    public static long readLong(byte[] src, int pointer) {
        return (long) (src[pointer + 0] << 56 |
                       src[pointer + 1] << 48 |
                       src[pointer + 2] << 40 |
                       src[pointer + 3] << 32 |
                       src[pointer + 4] << 24 |
                       src[pointer + 5] << 16 |
                       src[pointer + 6] <<  8 |
                       src[pointer + 7] <<  0);
    }

    public static long[] readLongs(byte[] src, int pointer, long[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readLong(src, pointer);
			pointer += Type.sizeOf(Type.LONG);
		}
        return dest;
	}

    public static int writeBytes(byte[] dest, int pointer, long value) {
        dest[pointer++] = (byte) ((value >> 56) & BITMASK);
        dest[pointer++] = (byte) ((value >> 48) & BITMASK);
        dest[pointer++] = (byte) ((value >> 40) & BITMASK);
        dest[pointer++] = (byte) ((value >> 32) & BITMASK);
        dest[pointer++] = (byte) ((value >> 24) & BITMASK);
        dest[pointer++] = (byte) ((value >> 16) & BITMASK);
        dest[pointer++] = (byte) ((value >>  8) & BITMASK);
        dest[pointer++] = (byte) ((value >>  0) & BITMASK);
        return pointer;
    }

    public static int writeBytes(byte[] dest, int pointer, long[] src) {
		for (int i = 0; i < src.length; i++) pointer = writeBytes(dest, pointer, src[i]);
		return pointer;
	}

    public static float readFloat(byte[] src, int pointer) {
        return Float.intBitsToFloat(readInt(src, pointer));
    }

    public static float[] readFloats(byte[] src, int pointer, float[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readFloat(src, pointer);
			pointer += Type.sizeOf(Type.FLOAT);
		}
        return dest;
	}

    public static int writeBytes(byte[] dest, int pointer, float value) {
        return writeBytes(dest, pointer, Float.floatToIntBits(value));
    }

    public static int writeBytes(byte[] dest, int pointer, float[] src) {
		for (int i = 0; i < src.length; i++) pointer = writeBytes(dest, pointer, src[i]);
		return pointer;
	}

    public static double readDouble(byte[] src, int pointer) {
        return Double.longBitsToDouble(readLong(src, pointer));
    }

    public static double[] readDoubles(byte[] src, int pointer, double[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readDouble(src, pointer);
			pointer += Type.sizeOf(Type.DOUBLE);
		}
        return dest;
	}

    public static int writeBytes(byte[] dest, int pointer, double value) {
        return writeBytes(dest, pointer, Double.doubleToLongBits(value));
    }

    public static int writeBytes(byte[] dest, int pointer, double[] src) {
		for (int i = 0; i < src.length; i++) pointer = writeBytes(dest, pointer, src[i]);
		return pointer;
	}

    public static String readString(byte[] src, int pointer, int length) {
		return new String(src, pointer, length);
	}

    public static int writeBytes(byte[] dest, int pointer, String value) {
        pointer = writeBytes(dest, pointer, (short) value.length());
        return writeBytes(dest, pointer, value.getBytes());
    }

}

package net.acidfrog.kronos.toolkit.internal.memory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

import net.acidfrog.kronos.scribe.ANSI;
import net.acidfrog.kronos.scribe.ANSI.AnsiTrait;
import net.acidfrog.kronos.scribe.ANSI.Traits;
import net.acidfrog.kronos.toolkit.internal.UnsafeSupport;
import sun.misc.Unsafe;

public final class MemoryBlock implements Iterable<Byte> {
    
    private static final Unsafe UNSAFE = UnsafeSupport.getUnsafe();

    public static final int BOOLEAN_BYTES = UnsafeSupport.ARRAY_BOOLEAN_INDEX_SCALE; // platform (JVM) dependent
    public static final int BYTE_BYTES = Byte.BYTES;
    public static final int SHORT_BYTES = Short.BYTES;
    public static final int CHAR_BYTES = Character.BYTES;
    public static final int INT_BYTES = Integer.BYTES;
    public static final int LONG_BYTES = Long.BYTES;
    public static final int FLOAT_BYTES = Float.BYTES;
    public static final int DOUBLE_BYTES = Double.BYTES;

    private int pointer = 0;
    private final byte[] buffer;

    public MemoryBlock(final byte[] buffer) {
        if (buffer == null) {
            throw new NullPointerException("Buffer cannot be null");
        }

        this.buffer = buffer;
    }

    public int writeBoolean(final boolean value) {
        UNSAFE.putBoolean(buffer, UnsafeSupport.ARRAY_BOOLEAN_BASE_OFFSET + pointer, value);
        pointer += BOOLEAN_BYTES;
        return pointer;
    }

    public int writeBooleans(final boolean[] values) {
        writeInt(values.length);
        long bytes = values.length * BOOLEAN_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_BOOLEAN_BASE_OFFSET, buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += bytes;
        return pointer;
    }

    public boolean readBoolean() {
        final boolean value = UNSAFE.getBoolean(buffer, UnsafeSupport.ARRAY_BOOLEAN_BASE_OFFSET + pointer);
        pointer += BOOLEAN_BYTES;
        return value;
    }

    public boolean[] readBooleans() {
        final int length = readInt();
        final boolean[] values = new boolean[length];
        long bytes = length * BOOLEAN_BYTES;
        UNSAFE.copyMemory(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, values, UnsafeSupport.ARRAY_BOOLEAN_BASE_OFFSET, bytes);
        pointer += bytes;
        return values;
    }

    public int writeByte(final byte value) {
        UNSAFE.putByte(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, value);
        pointer += BYTE_BYTES;
        return pointer;
    }

    public int writeBytes(final byte[] values) {
        writeInt(values.length);
        long bytes = values.length * BYTE_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET, buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += bytes;
        return pointer;
    }

    public byte readByte() {
        final byte value = UNSAFE.getByte(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer);
        pointer += BYTE_BYTES;
        return value;
    }

    public byte[] readBytes() {
        final int length = readInt();
        final byte[] values = new byte[length];
        long bytes = length * BYTE_BYTES;
        UNSAFE.copyMemory(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, values, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET, bytes);
        pointer += bytes;
        return values;
    }

    public int writeShort(final short value) {
        UNSAFE.putShort(buffer, UnsafeSupport.ARRAY_SHORT_BASE_OFFSET + pointer, value);
        pointer += SHORT_BYTES;
        return pointer;
    }

    public int writeShorts(final short[] values) {
        writeInt(values.length);
        long bytes = values.length * SHORT_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_SHORT_BASE_OFFSET, buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += bytes;
        return pointer;
    }

    public short readShort() {
        final short value = UNSAFE.getShort(buffer, UnsafeSupport.ARRAY_SHORT_BASE_OFFSET + pointer);
        pointer += SHORT_BYTES;
        return value;
    }

    public short[] readShorts() {
        final int length = readInt();
        final short[] values = new short[length];
        long bytes = length * SHORT_BYTES;
        UNSAFE.copyMemory(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, values, UnsafeSupport.ARRAY_SHORT_BASE_OFFSET, bytes);
        pointer += bytes;
        return values;
    }

    public int writeChar(final char value) {
        UNSAFE.putChar(buffer, UnsafeSupport.ARRAY_CHAR_BASE_OFFSET + pointer, value);
        pointer += CHAR_BYTES;
        return pointer;
    }

    public int writeChars(final char[] values) {
        writeInt(values.length);
        long bytes = values.length * CHAR_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_CHAR_BASE_OFFSET, buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += bytes;
        return pointer;
    }

    public char readChar() {
        final char value = UNSAFE.getChar(buffer, UnsafeSupport.ARRAY_CHAR_BASE_OFFSET + pointer);
        pointer += CHAR_BYTES;
        return value;
    }

    public char[] readChars() {
        final int length = readInt();
        final char[] values = new char[length];
        long bytes = length * CHAR_BYTES;
        UNSAFE.copyMemory(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, values, UnsafeSupport.ARRAY_CHAR_BASE_OFFSET, bytes);
        pointer += bytes;
        return values;
    }

    public int writeInt(final int value) {
        UNSAFE.putInt(buffer, UnsafeSupport.ARRAY_INT_BASE_OFFSET + pointer, value);
        pointer += INT_BYTES;
        return pointer;
    }

    public int writeInts(final int[] values) {
        writeInt(values.length);
        long bytes = values.length * INT_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_INT_BASE_OFFSET, buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += bytes;
        return pointer;
    }

    public int readInt() {
        final int value = UNSAFE.getInt(buffer, UnsafeSupport.ARRAY_INT_BASE_OFFSET + pointer);
        pointer += INT_BYTES;
        return value;
    }

    public int[] readInts() {
        final int length = readInt();
        final int[] values = new int[length];
        long bytes = length * INT_BYTES;
        UNSAFE.copyMemory(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, values, UnsafeSupport.ARRAY_INT_BASE_OFFSET, bytes);
        pointer += bytes;
        return values;
    }

    public int writeLong(final long value) {
        UNSAFE.putLong(buffer, UnsafeSupport.ARRAY_LONG_BASE_OFFSET + pointer, value);
        pointer += LONG_BYTES;
        return pointer;
    }

    public int writeLongs(final long[] values) {
        writeInt(values.length);
        long bytes = values.length * LONG_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_LONG_BASE_OFFSET, buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += bytes;
        return pointer;
    }

    public long readLong() {
        final long value = UNSAFE.getLong(buffer, UnsafeSupport.ARRAY_LONG_BASE_OFFSET + pointer);
        pointer += LONG_BYTES;
        return value;
    }

    public long[] readLongs() {
        int length = readInt();
        long[] values = new long[length];
        long bytes = length * LONG_BYTES;
        UNSAFE.copyMemory(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, values, UnsafeSupport.ARRAY_LONG_BASE_OFFSET, bytes);
        pointer += bytes;
        return values;
    }

    public int writeFloat(final float value) {
        UNSAFE.putFloat(buffer, UnsafeSupport.ARRAY_FLOAT_BASE_OFFSET + pointer, value);
        pointer += FLOAT_BYTES;
        return pointer;
    }

    public int writeFloats(final float[] values) {
        writeInt(values.length);
        long bytes = values.length * FLOAT_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_FLOAT_BASE_OFFSET, buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += bytes;
        return pointer;
    }

    public float readFloat() {
        final float value = UNSAFE.getFloat(buffer, UnsafeSupport.ARRAY_FLOAT_BASE_OFFSET + pointer);
        pointer += FLOAT_BYTES;
        return value;
    }

    public float[] readFloats() {
        final int length = readInt();
        final float[] values = new float[length];
        long bytes = length * FLOAT_BYTES;
        UNSAFE.copyMemory(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, values, UnsafeSupport.ARRAY_FLOAT_BASE_OFFSET, bytes);
        pointer += bytes;
        return values;
    }

    public int writeDouble(final double value) {
        UNSAFE.putDouble(buffer, UnsafeSupport.ARRAY_DOUBLE_BASE_OFFSET + pointer, value);
        pointer += DOUBLE_BYTES;
        return pointer;
    }

    public int writeDoubles(final double[] values) {
        writeInt(values.length);
        long bytes = values.length * DOUBLE_BYTES;
        UNSAFE.copyMemory(values, UnsafeSupport.ARRAY_DOUBLE_BASE_OFFSET, buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, bytes);
        pointer += bytes;
        return pointer;
    }

    public double readDouble() {
        final double value = UNSAFE.getDouble(buffer, UnsafeSupport.ARRAY_DOUBLE_BASE_OFFSET + pointer);
        pointer += DOUBLE_BYTES;
        return value;
    }

    public double[] readDoubles() {
        final int length = readInt();
        final double[] values = new double[length];
        long bytes = length * DOUBLE_BYTES;
        UNSAFE.copyMemory(buffer, UnsafeSupport.ARRAY_BYTE_BASE_OFFSET + pointer, values, UnsafeSupport.ARRAY_DOUBLE_BASE_OFFSET, bytes);
        pointer += bytes;
        return values;
    }

    public int writeString(final String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeBytes(bytes);
        return pointer;
    }

    public String readString() {
        final byte[] bytes = readBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public int pointer() {
        return pointer;
    }

    public int setPointer(final int pointer) {
        this.pointer = pointer;
        return pointer;
    }

    public void resetPointer() {
        pointer = 0;
    }

    public void incrementPointer(final int increment) {
        pointer += increment;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {

            @Override
            public boolean hasNext() {
                return pointer < buffer.length;
            }

            @Override
            public Byte next() {
                return buffer[pointer++];
            }
        };
    }

    public Iterator<Byte> iterator(int stride) {
        return new Iterator<Byte>() {

            @Override
            public boolean hasNext() {
                return pointer < buffer.length;
            }

            @Override
            public Byte next() {
                byte n = buffer[pointer];
                pointer += stride;
                return n;
            }
        };
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(buffer);
        result = prime * result + pointer;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MemoryBlock)) {
            return false;
        }
        MemoryBlock other = (MemoryBlock) obj;
        if (!Arrays.equals(buffer, other.buffer)) {
            return false;
        }
        if (pointer != other.pointer) {
            return false;
        }
        return true;
    }

    public byte[] getBytes() {
        return buffer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String tab = new String(new char[4]).replace("\0", " ");
        long dataAddress = UnsafeSupport.toAddress(buffer);

        sb.append("MemoryBlock@");
        sb.append(dataAddress);
        sb.append(" {\n");
        sb.append(tab);

        for (int i = 0, j = 0; i < buffer.length; i++) {
            if (i > 0 && i % 4 == 0) {
                j++;
            }
            
            AnsiTrait[] color = i == pointer ? new AnsiTrait[] { Traits.REVERSE  } : // pointed to value
                                j % 2 == 0   ? new AnsiTrait[] { Traits.WHITE_FG } : // even lines
                                               new AnsiTrait[] { Traits.GREY_FG  };  // odd lines
            sb.append(ANSI.colorize(String.format("0x%02X", buffer[i]), color));
            
            if (i < buffer.length - 1) {
                sb.append(" ");
            }
            if (i % 16 == 15) {
                sb.append("\n");
                sb.append(tab);
            }
        }

        if (pointer == buffer.length) {
            sb.setLength(sb.length() - tab.length() - 1);
            sb.append(ANSI.colorize(" ", ANSI.Traits.REVERSE));
            sb.append("\n");
        }

        sb.append(ANSI.format(new ANSI.Transmutation[] { ANSI.Transmutation.BACKWARD(8) }, "}" )).toString();
        
        return sb.toString();
    }
}

package test.serialization;

import static net.acidfrog.kronos.serialization.KronWriter.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static net.acidfrog.kronos.serialization.Kron.*;

public class TestKronWriter {
    
    public static void main(String[] args) {
        byte[] dest = new byte[Short.MAX_VALUE];
        boolean boolean1 = true;
        boolean boolean2 = false;
        byte byte1 = 1;
        byte byte2 = 2;
        short short1 = 3;
        short short2 = 4;
        char char1 = 'a';
        char char2 = 'b';
        int int1 = 5;
        int int2 = 6;
        long long1 = 7;
        long long2 = 8;
        float float1 = 9.1f;
        float float2 = 10.2f;
        double double1 = 11.3;
        double double2 = 12.4;
        String string1 = "Hello world!";
        String string2 = "!dlrow olleH";

        boolean[] booleanArray1 = new boolean[] { true, false, true };
        boolean[] booleanArray2 = new boolean[] { false, true, false };
        byte[] byteArray1 = new byte[] { 13, 14, 15 };
        byte[] byteArray2 = new byte[] { 16, 17, 18 };
        short[] shortArray1 = new short[] { 19, 20, 21 };
        short[] shortArray2 = new short[] { 22, 23, 24 };
        char[] charArray1 = new char[] { 'c', 'd', 'e' };
        char[] charArray2 = new char[] { 'f', 'g', 'h' };
        int[] intArray1 = new int[] { 25, 26, 27 };
        int[] intArray2 = new int[] { 28, 29, 30 };
        long[] longArray1 = new long[] { 31, 32, 33 };
        long[] longArray2 = new long[] { 34, 35, 36 };
        float[] floatArray1 = new float[] { 37.1f, 38.2f, 39.3f };
        float[] floatArray2 = new float[] { 40.4f, 41.5f, 42.6f };
        double[] doubleArray1 = new double[] { 43.7, 44.8, 45.9 };
        double[] doubleArray2 = new double[] { 46.0, 47.1, 48.2 };

        int
        pointer = writeBytes(dest, 0, boolean1);
        pointer = writeBytes(dest, pointer, boolean2);
        pointer = writeBytes(dest, pointer, byte1);
        pointer = writeBytes(dest, pointer, byte2);
        pointer = writeBytes(dest, pointer, short1);
        pointer = writeBytes(dest, pointer, short2);
        pointer = writeBytes(dest, pointer, char1);
        pointer = writeBytes(dest, pointer, char2);
        pointer = writeBytes(dest, pointer, int1);
        pointer = writeBytes(dest, pointer, int2);
        pointer = writeBytes(dest, pointer, long1);
        pointer = writeBytes(dest, pointer, long2);
        pointer = writeBytes(dest, pointer, float1);
        pointer = writeBytes(dest, pointer, float2);
        pointer = writeBytes(dest, pointer, double1);
        pointer = writeBytes(dest, pointer, double2);
        pointer = writeBytes(dest, pointer, string1);
        pointer = writeBytes(dest, pointer, string2);
        pointer = writeBytes(dest, pointer, booleanArray1);
        pointer = writeBytes(dest, pointer, booleanArray2);
        pointer = writeBytes(dest, pointer, byteArray1);
        pointer = writeBytes(dest, pointer, byteArray2);
        pointer = writeBytes(dest, pointer, shortArray1);
        pointer = writeBytes(dest, pointer, shortArray2);
        pointer = writeBytes(dest, pointer, charArray1);
        pointer = writeBytes(dest, pointer, charArray2);
        pointer = writeBytes(dest, pointer, intArray1);
        pointer = writeBytes(dest, pointer, intArray2);
        pointer = writeBytes(dest, pointer, longArray1);
        pointer = writeBytes(dest, pointer, longArray2);
        pointer = writeBytes(dest, pointer, floatArray1);
        pointer = writeBytes(dest, pointer, floatArray2);
        pointer = writeBytes(dest, pointer, doubleArray1);
        pointer = writeBytes(dest, pointer, doubleArray2);

        try (var file = new FileOutputStream("test.bin")) {
            file.write(dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        try (var file = new FileInputStream("test.bin")) {
            byte[] src = new byte[file.available()];
            file.read(src);

            int ptr = 0;

            boolean boolean3 = readBoolean(src, ptr);
            ptr += BOOLEAN_BYTES;
            boolean boolean4 = readBoolean(src, ptr);
            ptr += BOOLEAN_BYTES;

            byte byte3 = readByte(src, ptr);
            ptr += BYTE_BYTES;
            byte byte4 = readByte(src, ptr);
            ptr += BYTE_BYTES;

            short short3 = readShort(src, ptr);
            ptr += SHORT_BYTES;
            short short4 = readShort(src, ptr);
            ptr += SHORT_BYTES;

            char char3 = readChar(src, ptr);
            ptr += CHAR_BYTES;
            char char4 = readChar(src, ptr);
            ptr += CHAR_BYTES;

            int int3 = readInt(src, ptr);
            ptr += INT_BYTES;
            int int4 = readInt(src, ptr);
            ptr += INT_BYTES;

            long long3 = readLong(src, ptr);
            ptr += LONG_BYTES;
            long long4 = readLong(src, ptr);
            ptr += LONG_BYTES;

            float float3 = readFloat(src, ptr);
            ptr += FLOAT_BYTES;
            float float4 = readFloat(src, ptr);
            ptr += FLOAT_BYTES;

            double double3 = readDouble(src, ptr);
            ptr += DOUBLE_BYTES;
            double double4 = readDouble(src, ptr);
            ptr += DOUBLE_BYTES;

            String string3 = readString(src, ptr);
            ptr += string3.length() * BYTE_BYTES + INT_BYTES;
            String string4 = readString(src, ptr);
            ptr += string4.length() * BYTE_BYTES + INT_BYTES;

            boolean[] booleanArray3 = readBooleanArray(src, ptr);
            ptr += booleanArray3.length * BOOLEAN_BYTES + INT_BYTES;
            boolean[] booleanArray4 = readBooleanArray(src, ptr);
            ptr += booleanArray4.length * BOOLEAN_BYTES + INT_BYTES;

            byte[] byteArray3 = readByteArray(src, ptr);
            ptr += byteArray3.length * BYTE_BYTES + INT_BYTES;
            byte[] byteArray4 = readByteArray(src, ptr);
            ptr += byteArray4.length * BYTE_BYTES + INT_BYTES;

            short[] shortArray3 = readShortArray(src, ptr);
            ptr += shortArray3.length * SHORT_BYTES + INT_BYTES;
            short[] shortArray4 = readShortArray(src, ptr);
            ptr += shortArray4.length * SHORT_BYTES + INT_BYTES;

            char[] charArray3 = readCharArray(src, ptr);
            ptr += charArray3.length * CHAR_BYTES + INT_BYTES;
            char[] charArray4 = readCharArray(src, ptr);
            ptr += charArray4.length * CHAR_BYTES + INT_BYTES;

            int[] intArray3 = readIntArray(src, ptr);
            ptr += intArray3.length * INT_BYTES + INT_BYTES;
            int[] intArray4 = readIntArray(src, ptr);
            ptr += intArray4.length * INT_BYTES + INT_BYTES;

            long[] longArray3 = readLongArray(src, ptr);
            ptr += longArray3.length * LONG_BYTES + INT_BYTES;
            long[] longArray4 = readLongArray(src, ptr);
            ptr += longArray4.length * LONG_BYTES + INT_BYTES;

            float[] floatArray3 = readFloatArray(src, ptr);
            ptr += floatArray3.length * FLOAT_BYTES + INT_BYTES;
            float[] floatArray4 = readFloatArray(src, ptr);
            ptr += floatArray4.length * FLOAT_BYTES + INT_BYTES;

            double[] doubleArray3 = readDoubleArray(src, ptr);
            ptr += doubleArray3.length * DOUBLE_BYTES + INT_BYTES;
            double[] doubleArray4 = readDoubleArray(src, ptr);
            ptr += doubleArray4.length * DOUBLE_BYTES + INT_BYTES;

            System.out.println("boolean1: " + boolean1);
            System.out.println("boolean2: " + boolean2);
            System.out.println("boolean3: " + boolean3);
            System.out.println("boolean4: " + boolean4 + "\n");

            System.out.println("byte1: " + byte1);
            System.out.println("byte2: " + byte2);
            System.out.println("byte3: " + byte3);
            System.out.println("byte4: " + byte4 + "\n");

            System.out.println("short1: " + short1);
            System.out.println("short2: " + short2);
            System.out.println("short3: " + short3);
            System.out.println("short4: " + short4 + "\n");

            System.out.println("char1: " + char1);
            System.out.println("char2: " + char2);
            System.out.println("char3: " + char3);
            System.out.println("char4: " + char4 + "\n");

            System.out.println("int1: " + int1);
            System.out.println("int2: " + int2);
            System.out.println("int3: " + int3);
            System.out.println("int4: " + int4 + "\n");

            System.out.println("long1: " + long1);
            System.out.println("long2: " + long2);
            System.out.println("long3: " + long3);
            System.out.println("long4: " + long4 + "\n");

            System.out.println("float1: " + float1);
            System.out.println("float2: " + float2);
            System.out.println("float3: " + float3);
            System.out.println("float4: " + float4 + "\n");

            System.out.println("double1: " + double1);
            System.out.println("double2: " + double2);
            System.out.println("double3: " + double3);
            System.out.println("double4: " + double4 + "\n");

            System.out.println("string1: " + string1);
            System.out.println("string2: " + string2);
            System.out.println("string3: " + string3);
            System.out.println("string4: " + string4 + "\n");

            System.out.println("booleanArray1: " + Arrays.toString(booleanArray1));
            System.out.println("booleanArray2: " + Arrays.toString(booleanArray2));
            System.out.println("booleanArray3: " + Arrays.toString(booleanArray3));
            System.out.println("booleanArray4: " + Arrays.toString(booleanArray4) + "\n");

            System.out.println("byteArray1: " + Arrays.toString(byteArray1));
            System.out.println("byteArray2: " + Arrays.toString(byteArray2));
            System.out.println("byteArray3: " + Arrays.toString(byteArray3));
            System.out.println("byteArray4: " + Arrays.toString(byteArray4) + "\n");

            System.out.println("shortArray1: " + Arrays.toString(shortArray1));
            System.out.println("shortArray2: " + Arrays.toString(shortArray2));
            System.out.println("shortArray3: " + Arrays.toString(shortArray3));
            System.out.println("shortArray4: " + Arrays.toString(shortArray4) + "\n");

            System.out.println("charArray1: " + Arrays.toString(charArray1));
            System.out.println("charArray2: " + Arrays.toString(charArray2));
            System.out.println("charArray3: " + Arrays.toString(charArray3));
            System.out.println("charArray4: " + Arrays.toString(charArray4) + "\n");

            System.out.println("intArray1: " + Arrays.toString(intArray1));
            System.out.println("intArray2: " + Arrays.toString(intArray2));
            System.out.println("intArray3: " + Arrays.toString(intArray3));
            System.out.println("intArray4: " + Arrays.toString(intArray4) + "\n");

            System.out.println("longArray1: " + Arrays.toString(longArray1));
            System.out.println("longArray2: " + Arrays.toString(longArray2));
            System.out.println("longArray3: " + Arrays.toString(longArray3));
            System.out.println("longArray4: " + Arrays.toString(longArray4) + "\n");

            System.out.println("floatArray1: " + Arrays.toString(floatArray1));
            System.out.println("floatArray2: " + Arrays.toString(floatArray2));
            System.out.println("floatArray3: " + Arrays.toString(floatArray3));
            System.out.println("floatArray4: " + Arrays.toString(floatArray4) + "\n");

            System.out.println("doubleArray1: " + Arrays.toString(doubleArray1));
            System.out.println("doubleArray2: " + Arrays.toString(doubleArray2));
            System.out.println("doubleArray3: " + Arrays.toString(doubleArray3));
            System.out.println("doubleArray4: " + Arrays.toString(doubleArray4) + "\n");

            System.out.println("All test passed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

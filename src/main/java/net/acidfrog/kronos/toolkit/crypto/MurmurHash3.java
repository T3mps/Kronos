package net.acidfrog.kronos.toolkit.crypto;

import java.nio.charset.Charset;

public final class MurmurHash3 {
    
    public static final int DEFAULT_SEED = 0x19919; // 104729;

    private static final int C1_32 = 0xcc9e2d51;
    private static final int C2_32 = 0x1b873593;
    private static final int R1_32 = 0x0F;
    private static final int R2_32 = 0x0D;
    private static final int M_32  = 0x05;
    private static final int N_32  = 0xe6546b64;

    private static final long C1_128 = 0x87c37b91114253d5L;
    private static final long C2_128 = 0x4cf5ad432745937fL;
    private static final int R1_128  = 0x1F;
    private static final int R2_128  = 0x1B;
    private static final int R3_128  = 0x21;
    private static final int M_128   = 0x05;
    private static final int N1_128  = 0x52dce729;
    private static final int N2_128  = 0x38495ab5;

    private MurmurHash3() {
    }

    public static int hash32(final byte[] data) {
        return hash32(data, 0, data.length, DEFAULT_SEED);
    }

    public static int hash32(final byte[] data, final int seed) {
        return hash32(data, 0, data.length, seed);
    }

    public static int hash32(final byte[] data, final int offset, final int length, final int seed) {
        int hash = seed;
        int nblocks = length >> 0x02;

        for (int i = 0; i < nblocks; i++) {
            int index = offset + (i << 0x02);
            int k  = ((data[index + 0] & 0xff) << 0x00);
                k |= ((data[index + 1] & 0xff) << 0x08);
                k |= ((data[index + 2] & 0xff) << 0x10);
                k |= ((data[index + 3] & 0xff) << 0x18);

            
            k *= C1_32;
            k  = Integer.rotateLeft(k, R1_32);
            k *= C2_32;
            hash ^= k;
            hash  = Integer.rotateLeft(hash, R2_32) * M_32 + N_32;
        }

        int tail = offset + (nblocks << 0x02);
        int k1 = 0;

        switch (offset + length - tail) {
            case 3: k1 ^= (data[tail + 2] & 0xff) << 0x10;
            case 2: k1 ^= (data[tail + 1] & 0xff) << 0x08;
            case 1: k1 ^= (data[tail + 0] & 0xff) << 0x00;
                    k1 *= C1_32;
                    k1 = Integer.rotateLeft(k1, R1_32);
                    k1 *= C2_32;
                    hash ^= k1;
        }

        hash ^= length;
        hash ^= (hash >>> 0x10);
        hash *= 0x85ebca6b;
        hash ^= (hash >>> 0x0D);
        hash *= 0xc2b2ae35;
        hash ^= (hash >>> 0x10);
        return hash;
    }
    
    public static int hash32(final String data) {
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        return hash32(bytes, 0, bytes.length, DEFAULT_SEED);
    }

    public static int hash32(final String data, final int seed) {
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        return hash32(bytes, 0, bytes.length, seed);
    }

    public static long[] hash128(final byte[] data) {
        return hash128(data, 0, data.length, DEFAULT_SEED);
    }

    public static long[] hash128(final byte[] data, final int seed) {
        return hash128(data, 0, data.length, seed);
    }

    public static long[] hash128(final byte[] data, final int offset, final int length, final int seed) {
        long h1 = seed;
        long h2 = seed;
        int nblocks = length >> 0x04;

        for (int i = 0; i < nblocks; i++) {
            int index = offset + (i << 0x04);
            
            long k1  = (((long) data[index +  0] & 0xff) << 0x00);
                 k1 |= (((long) data[index +  1] & 0xff) << 0x08);
                 k1 |= (((long) data[index +  2] & 0xff) << 0x10);
                 k1 |= (((long) data[index +  3] & 0xff) << 0x18);
                 k1 |= (((long) data[index +  4] & 0xff) << 0x20);
                 k1 |= (((long) data[index +  5] & 0xff) << 0x28);
                 k1 |= (((long) data[index +  6] & 0xff) << 0x30);
                 k1 |= (((long) data[index +  7] & 0xff) << 0x38);

            long k2  = (((long) data[index +  8] & 0xff) << 0x00);
                 k2 |= (((long) data[index +  9] & 0xff) << 0x08);
                 k2 |= (((long) data[index + 10] & 0xff) << 0x10);
                 k2 |= (((long) data[index + 11] & 0xff) << 0x18);
                 k2 |= (((long) data[index + 12] & 0xff) << 0x20);
                 k2 |= (((long) data[index + 13] & 0xff) << 0x28);
                 k2 |= (((long) data[index + 14] & 0xff) << 0x30);
                 k2 |= (((long) data[index + 15] & 0xff) << 0x38);

            k1 *= C1_128;
            k1  = Long.rotateLeft(k1, R1_128);
            k1 *= C2_128;
            h1 ^= k1;
            h1  = Long.rotateLeft(h1, R2_128);
            h1 += h2;
            h1  = h1 * M_128 + N1_128;

            k2 *= C2_128;
            k2  = Long.rotateLeft(k2, R3_128);
            k2 *= C1_128;
            h2 ^= k2;
            h2  = Long.rotateLeft(h2, R1_128);
            h2 += h1;
            h2  = h2 * M_128 + N2_128;
        }

        long k1 = 0;
        long k2 = 0;
        int tail = offset + (nblocks << 0x04);

        switch (offset + length - tail) {
            case 15: k2 ^= ((long) data[tail + 14] & 0xff) << 0x30;
            case 14: k2 ^= ((long) data[tail + 13] & 0xff) << 0x28;
            case 13: k2 ^= ((long) data[tail + 12] & 0xff) << 0x20;
            case 12: k2 ^= ((long) data[tail + 11] & 0xff) << 0x18;
            case 11: k2 ^= ((long) data[tail + 10] & 0xff) << 0x10;
            case 10: k2 ^= ((long) data[tail +  9] & 0xff) << 0x08;
            case  9: k2 ^= data[tail + 8] & 0xff;
                     k2 *= C2_128;
                     k2  = Long.rotateLeft(k2, R3_128);
                     k2 *= C1_128;
                     h2 ^= k2;

            case  8: k1 ^= ((long) data[tail +  7] & 0xff) << 0x38;
            case  7: k1 ^= ((long) data[tail +  6] & 0xff) << 0x30;
            case  6: k1 ^= ((long) data[tail +  5] & 0xff) << 0x28;
            case  5: k1 ^= ((long) data[tail +  4] & 0xff) << 0x20;
            case  4: k1 ^= ((long) data[tail +  3] & 0xff) << 0x18;
            case  3: k1 ^= ((long) data[tail +  2] & 0xff) << 0x10;
            case  2: k1 ^= ((long) data[tail +  1] & 0xff) << 0x08;
            case  1: k1 ^= data[tail + 0] & 0xff;
                     k1 *= C1_128;
                     k1  = Long.rotateLeft(k1, R1_128);
                     k1 *= C2_128;
                     h1 ^= k1;
        }

        h1 ^= length;
        h2 ^= length;

        h1 += h2;
        h2 += h1;

        h1 ^= (h1 >>> 0x21);
        h1 *= 0xff51afd7ed558ccdL;
        h1 ^= (h1 >>> 0x21);
        h1 *= 0xc4ceb9fe1a85ec53L;
        h1 ^= (h1 >>> 0x21);
        
        h2 ^= (h2 >>> 0x21);
        h2 *= 0xff51afd7ed558ccdL;
        h2 ^= (h2 >>> 0x21);
        h2 *= 0xc4ceb9fe1a85ec53L;
        h2 ^= (h2 >>> 0x21);

        h1 += h2;
        h2 += h1;

        return new long[] { h1, h2 };
    }

    public static long[] hash128(final String data) {
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        return hash128(bytes, 0, bytes.length, DEFAULT_SEED);
    }

    public static long[] hash128(final String data, final int seed) {
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        return hash128(bytes, 0, bytes.length, seed);
    }
}

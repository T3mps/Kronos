package com.starworks.kronos.toolkit.crypto;

import java.nio.charset.Charset;

public final class MurmurHash2 {
    
    private static final int DEFAULT_32_BIT_SEED = 0x9747b28c;
    private static final int DEFAULT_64_BIT_SEED = 0xe17a1465;

    private static final int M_32 = 0x5bd1e995;
    private static final int R_32 = 0x18;

    private static final long M_64 = 0xc6a4a7935bd1e995L;
    private static final int  R_64 = 0x2F;

    private MurmurHash2() {
    }

    public static int hash32(final byte[] data) {
        return hash32(data, data.length, DEFAULT_32_BIT_SEED);
    }

    public static int hash32(final byte[] data, final int seed) {
        return hash32(data, data.length, seed);
    }

    public static int hash32(final byte[] data, final int length, final int seed) {
        int h = seed ^ length;
        int nblocks = length >> 0x02;

        for (int i = 0; i < nblocks; i++) {
            int index = i << 0x02;
            int k  = ((data[index + 0] & 0xff) << 0x00);
                k |= ((data[index + 1] & 0xff) << 0x08);
                k |= ((data[index + 2] & 0xff) << 0x10);
                k |= ((data[index + 3] & 0xff) << 0x18);

            k *= M_32;
            k ^= k >>> R_32;
            k *= M_32;
            h *= M_32;
            h ^= k;
        }

        int tail = nblocks << 0x02;

        switch (length - tail) {
            case 3: h ^= (data[tail + 2] & 0xff) << 0x10;
            case 2: h ^= (data[tail + 1] & 0xff) << 0x08;
            case 1: h ^= (data[tail + 0] & 0xff) << 0x00;
                    h *= M_32;
        }

        h ^= h >>> 0x0D;
        h *= M_32;
        h ^= h >>> 0x0F;

        return h;
    }
    
    public static int hash32(final String data) {
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        return hash32(bytes, bytes.length, DEFAULT_32_BIT_SEED);
    }

    public static long hash64(final byte[] data) {
        return hash64(data, data.length, DEFAULT_64_BIT_SEED);
    }

    public static long hash64(final byte[] data, final int seed) {
        return hash64(data, data.length, seed);
    }

    public static long hash64(final byte[] data, final int length, final int seed) {
        long h = (seed & 0xffffffffL) ^ (length * M_64);
        int nblocks = length >> 0x03;

        for (int i = 0; i < nblocks; i++) {
            int index = i << 0x03;
            long k  = (((long) data[index + 0] & 0xff) << 0x00);
                 k |= (((long) data[index + 1] & 0xff) << 0x08);
                 k |= (((long) data[index + 2] & 0xff) << 0x10);
                 k |= (((long) data[index + 3] & 0xff) << 0x18);
                 k |= (((long) data[index + 4] & 0xff) << 0x20);
                 k |= (((long) data[index + 5] & 0xff) << 0x28);
                 k |= (((long) data[index + 6] & 0xff) << 0x30);
                 k |= (((long) data[index + 7] & 0xff) << 0x38);
            
            k *= M_64;
            k ^= k >>> R_64;
            k *= M_64;

            h ^= k;
            h *= M_64;
        }

        int tail = nblocks << 0x03;

        switch (length - tail) {
            case 7: h ^= ((long) data[tail + 6] & 0xff) << 0x30;
            case 6: h ^= ((long) data[tail + 5] & 0xff) << 0x28;
            case 5: h ^= ((long) data[tail + 4] & 0xff) << 0x20;
            case 4: h ^= ((long) data[tail + 3] & 0xff) << 0x18;
            case 3: h ^= ((long) data[tail + 2] & 0xff) << 0x10;
            case 2: h ^= ((long) data[tail + 1] & 0xff) << 0x08;
            case 1: h ^= ((long) data[tail + 0] & 0xff) << 0x00;
                    h *= M_64;
        }

        h ^= h >>> R_64;
        h *= M_64;
        h ^= h >>> R_64;

        return h;
    }

    public static long hash64(final String data) {
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        return hash64(bytes, bytes.length, DEFAULT_64_BIT_SEED);
    }

    public static long hash64(final String data, final int seed) {
        byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        return hash64(bytes, bytes.length, seed);
    }
}

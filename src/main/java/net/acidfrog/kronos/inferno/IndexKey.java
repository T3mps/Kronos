package net.acidfrog.kronos.inferno;

import java.util.Arrays;

public final class IndexKey {

    private final int hashCode;
    private final byte[] data;

    public IndexKey(final int value) {
        this.hashCode = value;
        this.data = null;
    }

    public IndexKey(final int[] array) {
        int aSize = array.length;
        this.data = new byte[aSize];
        long result = 1;

        for (int i = 0; i < aSize; i++) {
            int value = array[i];
            result = result * 31 + value;
            data[i] = (byte) (value ^ (value >>> 8));
        }

        this.hashCode = (int) (result ^ (result >>> 32));
    }

    public IndexKey(final boolean[] checkArray, final int min, final int max, final int length) {
        this.data = new byte[length];
        long result = 1;
        int index = 0;

        for (int i = min; i <= max; i++) {
            if (checkArray[i]) {
                result = result * 31 + i;
                data[index++] = (byte) (i ^ (i >>> 8));
            }
        }

        this.hashCode = (int) (result ^ (result >>> 32));
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return Arrays.equals(((IndexKey) o).data, data);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        sb.append(hashCode);
        sb.append(":");
        sb.append(Arrays.toString(data));
        sb.append("|");
        return sb.toString();
    }
}

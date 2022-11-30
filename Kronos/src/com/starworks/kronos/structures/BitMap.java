package com.starworks.kronos.structures;

import java.util.Arrays;

public class BitMap {

    private static final int SHIFT = 6;
    
    private long[] bits;

    public BitMap() {
        this(16);
    }

    public BitMap(int capacity) {
        this.bits = new long[capacity];
    }

    public BitMap(long[] bits) {
        this.bits = bits;
    }

    public BitMap(BitMap other) {
        this.bits = other.bits;
    }

    public void set(int index) {
        int word = index >>> SHIFT;
        checkCapacity(word);
        bits[word] |= 1L << (index);
    }

    public void flip(int index) {
		int word = index >>> SHIFT;
		checkCapacity(word);
		bits[word] ^= 1L << (index);
	}

    public long[] get() {
        return bits;
    }

    public boolean get(int index) {
        int word = index >>> SHIFT;
		
        if (word >= bits.length) {
            return false;
        }

        return (bits[word] & (1L << index)) != 0;
    }

    public BitMap and(BitMap other) {
        int commonWords = Math.min(bits.length, other.bits.length);

        for (int i = 0; i < commonWords; i++) {
            bits[i] &= other.bits[i];
        }

        if (commonWords < bits.length) {
            Arrays.fill(bits, commonWords, bits.length, 0L);
        }

        return this;
    }

    public BitMap or(BitMap other) {
        int commonWords = Math.min(bits.length, other.bits.length);
        
        for (int i = 0; i < commonWords; i++) {
            bits[i] |= other.bits[i];
        }
        
        if (commonWords < other.bits.length) {
            checkCapacity(other.bits.length);
         
            int oSize = other.bits.length;
            for (int i = commonWords; i < oSize; i++) {
                bits[i] = other.bits[i];
            }
        }

        return this;
    }

    public BitMap xor(BitMap other) {
        int commonWords = Math.min(bits.length, other.bits.length);
        
        for (int i = 0; i < commonWords; i++) {
            bits[i] ^= other.bits[i];
        }

        if (commonWords < other.bits.length) {
            checkCapacity(other.bits.length);
         
            int oSize = other.bits.length;
            for (int i = commonWords; i < oSize; i++) {
                bits[i] = other.bits[i];
            }
        }

        return this;
    }

    public BitMap not() {
        for (int i = 0; i < bits.length; i++) {
            bits[i] = ~bits[i];
        }

        return this;
    }

    public int cardinality() {
        int count = 0;
        for (int i = 0; i < bits.length; i++) {
            count += Long.bitCount(bits[i]);
        }

        return count;
    }

    public boolean intersects(BitMap other) {
        return intersects(other.bits);
    }

    public boolean intersects(long[] other) {
        int commonWords = Math.min(bits.length, other.length);
        
        for (int i = 0; i < commonWords; i++) {
            if ((bits[i] & other[i]) != 0) {
                return true;
            }
        }
        
        return false;
    }

    public boolean containsAll(BitMap other) {
        return containsAll(other.bits);
    }

    public boolean containsAll(long[] other) {
        long[] bits = this.bits;
        long[] otherBits = other;
        int bSize = bits.length;
        int oSize = otherBits.length;

        for (int i = bSize; i < oSize; i++) {
            if (otherBits[i] != 0) {
                return false;
            }
        }

        for (int i = 0; i < bSize; i++) {
            if ((bits[i] & otherBits[i]) != otherBits[i]) {
                return false;
            }
        }

        return true;
    }

    public void clear() {
        for (int i = 0; i < bits.length; i++) {
            bits[i] = 0;
        }
    }
    
    public void clear(int index) {
        int word = index >>> SHIFT;
        checkCapacity(word);
        bits[word] &= ~(1L << (index));
    }

    private void checkCapacity(int cap) {
		if (cap >= bits.length) {
			long[] newBits = new long[cap + 1];
			System.arraycopy(bits, 0, newBits, 0, bits.length);
			bits = newBits;
		}
	}

    public boolean isEmpty() {
		long[] bits = this.bits;
		int length = bits.length;
		
        for (int i = 0; i < length; i++) {
            if (bits[i] != 0L) {
                return false;
            }
        }
		
        return true;
    }

    public int size() {
        return bits.length << SHIFT;
    }

    public int length() {
        long[] bits = this.bits;
        
        for (int word = bits.length - 1; word >= 0; word--) {
            long bit = bits[word];

            if (bit != 0) {
                for (int b = 63; b >= 0; --b) {
                    if ((bit & (1L << b)) != 0) {
                        return (word << SHIFT) + b + 1;
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 127;
        int result = 1;
        result = prime * result + Arrays.hashCode(bits);
        return result;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		BitMap other = (BitMap) obj;
		long[] otherBits = other.bits;
		int commonWords = Math.min(bits.length, otherBits.length);
		
        for (int i = 0; commonWords > i; i++) if (bits[i] != otherBits[i]) return false;

		if (bits.length == otherBits.length) return true;

		return length() == other.length();
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Bitset [bits=");
        builder.append(Arrays.toString(bits));
        builder.append("]");
        return builder.toString();
    }
}

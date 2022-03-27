package net.acidfrog.kronos.core.datastructure;

import java.util.Arrays;

/** https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/utils/Bits.java */
public final class Bitset {
    
    long[] bits = new long[64];

    public Bitset() {
        clear();
    }

    public Bitset(int size) {
        checkCapacity(size >>> 6);
    }

    public Bitset(long[] bits) {
        this.bits = bits;
    }

    public Bitset(Bitset bitset) {
        this.bits = bitset.bits;
    }

    public void and(Bitset other) {
		int commonWords = Math.min(bits.length, other.bits.length);
		for (int i = 0; commonWords > i; i++) bits[i] &= other.bits[i];

		if (bits.length > commonWords) for (int i = commonWords, s = bits.length; s > i; i++) {
            bits[i] = 0L;
        }
	}

    public void andNot(Bitset other) {
		for (int i = 0, j = bits.length, k = other.bits.length; i < j && i < k; i++) bits[i] &= ~other.bits[i];
	}

    public void or(Bitset other) {
		int commonWords = Math.min(bits.length, other.bits.length);

		for (int i = 0; commonWords > i; i++) bits[i] |= other.bits[i];

		if (commonWords < other.bits.length) {
			checkCapacity(other.bits.length);

			for (int i = commonWords, s = other.bits.length; s > i; i++) bits[i] = other.bits[i];
		}
	}

    public void xor(Bitset other) {
		int commonWords = Math.min(bits.length, other.bits.length);

		for (int i = 0; commonWords > i; i++) bits[i] ^= other.bits[i];

		if (commonWords < other.bits.length) {
			checkCapacity(other.bits.length);
			for (int i = commonWords, s = other.bits.length; s > i; i++) bits[i] = other.bits[i];
		}
	}

    public boolean convergence(long[] otherBits) {
		long[] bits = this.bits;
		
        for (int i = Math.min(bits.length, otherBits.length) - 1; i >= 0; i--) if ((bits[i] & otherBits[i]) != 0) {
            return true;
        }
		
        return false;
	}

    public boolean convergence(Bitset other) {
        return convergence(other.bits);
    }

    public boolean containsAll(Bitset other) {
		long[] bits = this.bits;
		long[] otherBits = other.bits;
		int otherBitsLength = otherBits.length;
		int bitsLength = bits.length;

		for (int i = bitsLength; i < otherBitsLength; i++) if (otherBits[i] != 0) {
            return false;
		}

		for (int i = Math.min(bitsLength, otherBitsLength) - 1; i >= 0; i--) if ((bits[i] & otherBits[i]) != otherBits[i]) {
            return false;
        }

		return true;
	}

    public long[] get() {
        return bits;
    }

    public boolean get(int index) {
        final int word = index >>> 6;
		if (word >= bits.length) return false;
        return (bits[word] & (1L << index & 0x3f)) != 0;
    }

    public void set(int index) {
		final int word = index >>> 6;
		checkCapacity(word);
		bits[word] |= 1L << (index & 0x3F);
	}

    public void flip(int index) {
		final int word = index >>> 6;
		checkCapacity(word);
		bits[word] ^= 1L << (index & 0x3F);
	}

	private void checkCapacity(int len) {
		if (len >= bits.length) {
			long[] newBits = new long[len + 1];
			System.arraycopy(bits, 0, newBits, 0, bits.length);
			bits = newBits;
		}
	}

    public void clear() {
		for (int i = 0; i < bits.length; i++) bits[i] = 0L;
	}

    public void clear (int index) {
		final int word = index >>> 6;
		if (word >= bits.length) return;
		bits[word] &= ~(1L << (index & 0x3F));
	}

    public int size() {
		return bits.length << 6;
	}

    public int length() {
		long[] bits = this.bits;
		
        for (int word = bits.length - 1; word >= 0; --word) {
			long bitsAtWord = bits[word];
            
			if (bitsAtWord != 0) for (int bit = 63; bit >= 0; --bit) if ((bitsAtWord & (1L << (bit & 0x3F))) != 0L) {
                return (word << 6) + bit + 1;
            }
		}
		
        return 0;
	}

    public boolean isEmpty() {
		long[] bits = this.bits;
		int length = bits.length;
		
        for (int i = 0; i < length; i++) if (bits[i] != 0L) {
            return false;
        }
		
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bits);
        return result;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		Bitset other = (Bitset) obj;
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

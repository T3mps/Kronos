package com.starworks.kronos.toolkit.crypto;

public final class CityHash {

	private static final long k0 = 0xc3a5c85c97cb3127L;
	private static final long k1 = 0xb492b66fbe98f273L;
	private static final long k2 = 0x9ae16a3b2f90404fL;
	private static final long k3 = 0xff51afd7ed558ccdL;
	private static final long k4 = 0xc4ceb9fe1a85ec53L;

	CityHash() {
	}

	private long rotateByAtLeast1(long val, int shift) {
		return (val << shift) | (val >>> (64 - shift));
	}

	private long shiftMix(long val) {
		return val ^ (val >>> 47);
	}

	private long hashLen16(long u, long v, long mul) {
		long a = (u ^ v) * mul;
		a ^= (a >>> 47);
		long b = (v ^ a) * mul;
		b ^= (b >>> 47);
		b *= mul;
		return b;
	}

	private static long hashLen0to16(byte[] input, int len) {
		long mul = (len >= 8 ? Fetch64(input, 0) <<  0 : 0) ^
				   (len >= 4 ? Fetch32(input, 0) << 32 : 0) ^
				   (len >= 2 ? Fetch16(input, 0) << 48 : 0) ^
				   (len >= 1 ?		   input [0] << 56 : 0);

		mul ^= mul >> 33;
		mul *= k3;
		mul ^= mul >> 33;
		mul *= k4;
		mul ^= mul >> 33;

		return mul;
	}

	private long hashLen17to32(byte[] input, int len) {
		long mul = Fetch64(input, 0) ^ Fetch64(input, 8);
		mul ^= mul >> 33;
		mul *= k3;
		mul ^= mul >> 33;
		mul *= k4;
		mul ^= mul >> 33;

		return mul;
	}

	private long hashLen33to64(byte[] input, int len) {
        long mul = Fetch64(input, 0) ^ Fetch64(input, 8) ^ Fetch64(input, len - 8);
        mul ^= mul >> 33;
        mul *= k3;
        mul ^= mul >> 33;
        mul *= k4;
        mul ^= mul >> 33;

        return mul;
    }

	private static long Fetch64(byte[] input, int offset) {
		return ((long) input[offset + 7] << 56) 	   |
			   ((long)(input[offset + 6] & 255) << 48) |
			   ((long)(input[offset + 5] & 255) << 40) |
			   ((long)(input[offset + 4] & 255) << 32) | 
			   ((long)(input[offset + 3] & 255) << 24) |
			  		 ((input[offset + 2] & 255) << 16) |
			  		 ((input[offset + 1] & 255) <<  8) |
			  		 ((input[offset + 0] & 255) << 0);
	}

	private static long Fetch32(byte[] input, int offset) {
		return ((long) (input[offset + 3] & 255) << 24) |
					  ((input[offset + 2] & 255) << 16) |
					  ((input[offset + 1] & 255) <<  8) |
					  ((input[offset + 0] & 255) <<  0);
	}

	private static long Fetch16(byte[] input, int offset) {
		return ((input[offset + 1] & 255) << 8) |
			   ((input[offset + 0] & 255) << 0);
	}

	public long hash64(byte[] input, int len) {
		if (len <= 16) {
			return hashLen0to16(input, len);
		} else if (len <= 32) {
			return hashLen17to32(input, len);
		} else if (len <= 64) {
			return hashLen33to64(input, len);
		}

		long x = Fetch64(input, 0) * k2 + Fetch64(input, 8);
		long y = Fetch64(input, len - 8) * k2 + Fetch64(input, len - 16);

		x = rotateByAtLeast1(x, 37) * k2;
		y = rotateByAtLeast1(y, 37) * k2;
		x ^= y;

		long l = len - 1;
		do {
			x = rotateByAtLeast1(x + y, 39) * k2;
			y = rotateByAtLeast1(y + Fetch64(input, (int) (l -= 8)), 39) * k2;
			x ^= y;
		} while (l != 0);

		return hashLen16(x, shiftMix(y) * k0, k1);
	}

	public long hash64(byte[] input) {
		return hash64(input, input.length);
	}

	public long hash64(String input) {
		return hash64(input.getBytes());
	}

	public long hash64(String input, String charset) {
		try {
			return hash64(input.getBytes(charset));
		} catch (Exception e) {
			return 0;
		}
	}

	public long hash64(String input, String charset, int len) {
		try {
			return hash64(input.getBytes(charset), len);
		} catch (Exception e) {
			return 0;
		}
	}
}

/*
 * The MIT License
 *
 * Copyright (c) 2016-2021 JOML
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.acidfrog.kronos.math;

import java.util.Random;

/**
 * Pseudo-random number generator.
 * 
 * @author Ethan Temprovich
 * @author Kai Burjack
 */
public class Xorshift128 extends Random {
	
	private static final long serialVersionUID = 1L;

	private long seed0;
	private long seed1;
	private static final double NORM_DOUBLE = 1.0 / (1L << 53);
	private static final double NORM_FLOAT  = 1.0 / (1L << 24);

	public Xorshift128() {
		setSeed(new Random().nextLong());
	}

	public Xorshift128(long seed) {
		setSeed(seed);
	}

	@Override
	public long nextLong() {
		long s1 = this.seed0;
		final long s0 = this.seed1;
		this.seed0 = s0;
		s1 ^= s1 << 23;
		return (this.seed1 = (s1 ^ s0 ^ (s1 >>> 17) ^ (s0 >>> 26))) + s0;
	}

	@Override
	protected final int next(int bits) {
		return (int) (nextLong() & ((1L << bits) - 1));
	}

	@Override
	public int nextInt() {
		return (int) nextLong();
	}

	@Override
	public int nextInt(final int n) {
		return (int) nextLong(n);
	}

	public long nextLong(final long n) {
		if (n <= 0) throw new IllegalArgumentException(n + " is not a positive number.");
		for (;;) {
			final long bits = nextLong() >>> 1;
			final long value = bits % n;
			if (bits - value + (n - 1) >= 0) return value;
		}
	}

	@Override
	public double nextDouble() {
		return (nextLong() >>> 11) * NORM_DOUBLE;
	}

	@Override
	public float nextFloat() {
		return (float) ((nextLong() >>> 40) * NORM_FLOAT);
	}

	@Override
	public boolean nextBoolean() {
		return (nextLong() & 1) != 0;
	}

	@Override
	public void nextBytes(final byte[] bytes) {
		int n = 0;
		int i = bytes.length;
		while (i != 0) {
			n = i < 8 ? i : 8; // min(i, 8);
			for (long bits = nextLong(); n-- != 0; bits >>= 8) bytes[--i] = (byte) bits;
		}
	}

	@Override
	public void setSeed(final long seed) {
		long seed0 = murmurHash3(seed == 0 ? Long.MIN_VALUE : seed);
		setState(seed0, murmurHash3(seed0));
	}

	public void setState(final long seed0, final long seed1) {
		this.seed0 = seed0;
		this.seed1 = seed1;
	}

	public long getState(int seed) {
		return seed == 0 ? seed0 : seed1;
	}

	private final static long murmurHash3(long n) {
		n ^= n >>> 33;
		n *= 0xff51afd7ed558ccdL;
		n ^= n >>> 33;
		n *= 0xc4ceb9fe1a85ec53L;
		n ^= n >>> 33;

		return n;
	}

}

package net.acidfrog.kronos.math.random;

import java.util.Random;

/** https://cs.gmu.edu/~sean/research/mersenne/MT19937.java */
public final class MT19937 extends Random implements Cloneable {
    private static final long serialVersionUID = 1L;

    // Period parameters
    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df;
    private static final int UPPER_MASK = 0x80000000; // most significant w-r bits
    private static final int LOWER_MASK = 0x7fffffff; // least significant r bits

    // Tempering parameters
    private static final int TEMPERING_MASK_B = 0x9d2c5680;
    private static final int TEMPERING_MASK_C = 0xefc60000;
    
    private int mt[];
    private int mag01[];
    private int i;

    /**
     * Constructor using the default seed.
     */
    public MT19937() {
        this(System.currentTimeMillis());
    }
    
    /**
     * Constructor using a given seed.  Though you pass this seed in
     * as a long, it's best to make sure it's actually an integer.
     */
    public MT19937(long seed) {
        super(seed);
        setSeed(seed);
    }
    
    /**
     * Constructor using an array of integers as seed.
     * Your array must have a non-zero length.  Only the first 624 integers
     * in the array are used; if the array is shorter than this then
     * integers are repeatedly used in a wrap-around fashion.
     */
    public MT19937(int[] array) {
        super(System.currentTimeMillis()); // fallback
        setSeed(array);
    }

    @Override
    public synchronized void setSeed(long seed) {
        super.setSeed(seed);

        mt = new int[N];
        
        mag01 = new int[2];
        mag01[0] = 0x0;
        mag01[1] = MATRIX_A;

        mt[0]= (int) (seed & 0xffffffff);
        mt[0] = (int) seed;
        for (i = 1; i < N; i++) {
            mt[i] = (1812433253 * (mt[i-1] ^ (mt[i-1] >>> 30)) + i); 
            /* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
            /* In the previous versions, MSBs of the seed affect   */
            /* only MSBs of the array mt[].                        */
            /* 2002/01/09 modified by Makoto Matsumoto             */
            // mt[i] &= 0xffffffff;
            /* for >32 bit machines */
        }
    }


    /**
     * Sets the seed of the MT19937 using an array of integers.
     * Your array must have a non-zero length.  Only the first 624 integers
     * in the array are used; if the array is shorter than this then
     * integers are repeatedly used in a wrap-around fashion.
     */

    public synchronized void setSeed(int[] array) {
        if (array.length == 0) throw new IllegalArgumentException("Array length must be greater than zero");
        int i = 1, j = 0, k;

        setSeed(19650218);
        k = (N>array.length ? N : array.length);

        for (; k!=0; k--) {
            mt[i] = (mt[i] ^ ((mt[i-1] ^ (mt[i-1] >>> 30)) * 1664525)) + array[j] + j; /* non linear */
            i++;
            j++;
            if (i >= N) mt[0] = mt[N - 1]; i = 1;
            if (j >= array.length) j = 0;
        }
        
        for (k = N - 1; k != 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i-1] ^ (mt[i-1] >>> 30)) * 1566083941)) - i; /* non linear */
            i++;

            if (i >= N) mt[0] = mt[N - 1]; i = 1;
        
        }
        mt[0] = 0x80000000; /* MSB is 1; assuring non-zero initial array */ 
    }

    @Override
    public synchronized int next(int bits) {
        int y;
        
        if (i >= N) { // generate N words at one time
            int kk;
            final int[] mt = this.mt; // locals are slightly faster 
            final int[] mag01 = this.mag01; // locals are slightly faster 
            
            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }

            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            i = 0;
        }
  
        y = mt[i++];
        y ^= y >>> 11;                          // TEMPERING_SHIFT_U(y)
        y ^= (y << 7) & TEMPERING_MASK_B;       // TEMPERING_SHIFT_S(y)
        y ^= (y << 15) & TEMPERING_MASK_C;      // TEMPERING_SHIFT_T(y)
        y ^= (y >>> 18);                        // TEMPERING_SHIFT_L(y)

        return y >>> (32 - bits);
    }

    @Override
    public boolean nextBoolean() {
        return !(next(1) == 0);
    }

    /** 
     * This generates a coin flip with a probability <tt>probability</tt>
     * of returning true, else returning false. <tt>probability</tt> must
     * be between 0 and 1, inclusive.
     */
    public boolean coin (double probability) {
        if (probability < 0 || probability > 1) throw new IllegalArgumentException ("probability must be between 0 and 1 inclusive.");
        if (probability==0) return false;
        if (probability==1) return true;
        return nextDouble() <= probability; 
    }
    
    @Override
    public int nextInt(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be positive, got: " + n);
        if ((n & -n) == n) return (int)((n * (long) next(31)) >> 31);
        
        int bits, val;

        do {
            bits = next(31);
            val = bits % n;
        } while(bits - val + (n - 1) < 0);

        return val;
    }

    @Override
    public float nextFloat() {
        return next(24) / ((float) (1 << 24));
    }

    @Override
    public void nextBytes(byte[] bytes) {
        for (int x=0;x<bytes.length;x++) bytes[x] = (byte)next(8);
    }

    /** For completeness' sake, though it's not in java.util.Random.  */
    public char nextChar() {
    return (char) (next(16));
    }

    /** For completeness' sake, though it's not in java.util.Random. */
    public short nextShort() {
        return (short) (next(16));
    }

    /** For completeness' sake, though it's not in java.util.Random.  */
    public byte nextByte() {
        return (byte) (next(8));
    }
    
}

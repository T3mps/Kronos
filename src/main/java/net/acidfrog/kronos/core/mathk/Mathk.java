package net.acidfrog.kronos.core.mathk;

import java.util.Random;

import net.acidfrog.kronos.core.Config;

/**
 * Contains fast math functions using specifically floating point precision. This classes
 * implementations of the trigonometric functions and sqrt are approximated if the FASTMATH flag, 
 * found in {@link Config} is set to true. <br></br>
 * <ul>
 * <li>{@link #sin(float)}</li>
 * <li>{@link #cos(float)}</li>
 * <li>{@link #tan(float)}</li>
 * <li>{@link #atan2(float, float)}</li>
 * <li>{@link #sqrt(float)}</li>
 * </ul>This is reccomended if you are using Kronos
 * for game calculations, as it will be much faster than the default implementations; and
 * it will be quite passable for calculations that get converted to integers anyhow. Contains
 * classic math functions left out of the {@link Math} class. These include: <br></br>
 * <ul>
 * <li>{@link #fma(float, float, float)}</li>
 * <li>{@link #nextPowerOfTwo(int)}</li>
 * <li>{@link #isPowerOfTwo(int)}</li>
 * <li>{@link #clamp(float, float, float)}</li>
 * <li>{@link #map(float, float, float, float, float)}</li>
 * <li>{@link #lerp(float, float, float)}</li>
 * <li>{@link #approach(float, float, float)}</li>
 * <li>{@link #lerpAngle(float, float, float)}</li>
 * <li>{@link #isZero(float)}</li>
 * <li>{@link #isZero(float, float)}</li>
 * <li>{@link #isEqual(float, float)}</li>
 * <li>{@link #isEqual(float, float, float)}</li>
 * <li>{@link #log2(float)}</li>
 * <li>{@link #q_floor(float)}</li>
 * <li>{@link #rotate(Vector2k,float,Vector2k)}</li>
 * <li>{@link #fft(float[])}</li>
 * <li>{@link #tripleProduct(Vector2k,Vector2k,Vector2k)}</li>
 * <li>{@link #compare(float, float, float)}</li>
 * <li>{@link #compare(Vector2k, Vector2k, float)}</li>
 * <li>{@link #compare(float, float)}</li>
 * <li>{@link #compare(Vector2k, Vector2k)}</li>
 * </ul>
 * 
 * @author Ethan Temprovich
 */
public class Mathk {

	public  static final float f1_2 = 0.5f;
	public  static final float f1_3 = 0.333333f;
	public  static final float f1_4 = 0.25f;

	public  static final float PI 					   =   3.14159265359f;
	public  static final float PI2 					   =   PI * 2f;
	public  static final float PIHalf 				   =   PI * f1_2;
	public  static final float PI_SQ 				   =   PI * PI;
	public  static final float PI_4 				   =   PI * f1_4;
	public  static final float PI_INV 				   =   1f / PI;

	public  static final float FLOAT_ROUNDING_ERROR    =   0.000001f; // 32 bits

	public  static final float E 					   =   2.71828182846f;
	
	public  static final float RADIAN_DOMAIN		   =   PI * 2f;
	public  static final float DEGREE_DOMAIN		   =   360f;
	public  static final float TO_RADIANS 			   =   (180f / PI) * PI;  // Multiply an angle in degrees by this constant to convert it to radians.
	public  static final float TO_DEGREES 			   =   (1 	 / PI) * 180; // Multiply an angle in radians by this constant to convert it to degrees.
	
	public  static final float NEGATIVE_INFINITY 	   =   +1f / 0f;
	public  static final float POSITIVE_INFINITY 	   =   -1f / 0f;

	// local vars
	private static final int   LOOKUP_BITS 			      =   Config.SIN_LOOKUP_BITS;
    private static final int   LOOKUP_TABLE_SIZE	      =   1 << LOOKUP_BITS;
    private static final int   LOOKUP_TABLE_SIZE_MASK  	  =   LOOKUP_TABLE_SIZE - 1;
	private static final int   LOOKUP_TABLE_SIZE_MARGIN   =   LOOKUP_TABLE_SIZE + 1;
    private static final float SIN_TABLE[];

	// calculating unit circle essentially 
    static {
        if (Config.FASTMATH) {
            SIN_TABLE = new float[LOOKUP_TABLE_SIZE_MARGIN];
			
            for (int i = 0; i < LOOKUP_TABLE_SIZE_MARGIN; i++) {
                double d = i * PI2 / LOOKUP_TABLE_SIZE;
                SIN_TABLE[i] = (float) java.lang.Math.sin(d);
            }
        } else SIN_TABLE = null;
    }

	private static Random random = new Xorshift128();

	
	public static boolean randomBoolean() {
		return random.nextBoolean();
	}

	public static boolean randomBoolean(float chance) {
		return random() < chance;
	}

	public static int random(int range) {
		return random.nextInt(range + 1);
	}

	public static int random(int start, int end) {
		return start + random.nextInt(end - start + 1);
	}

	public static long random(long range) {
		return (long) (random.nextDouble() * range);
	}

	public static long random(long start, long end) {
		return start + (long) (random.nextDouble() * (end - start));
	}

	public static float random() {
		return random.nextFloat();
	}

	public static float random(float range) {
		return random.nextFloat() * range;
	}

	public static float random(float start, float end) {
		return start + random.nextFloat() * (end - start);
	}

	/** 
	 * let u = n >> 31, is a bitwise operation which shifts n's bits by 31 to
	 * the right, effectivly making n = 0b0..01 | 0b1..01 depending on sign of n.
	 * Performing the bitwise or operator between this number and +1 will result
	 * in either 1 or -1. If u = -1, then the result is -1, otherwise the result
	 * is 1.
	 * 
	 * @return -1 or 1, randomly.
	*/
	public static int randomSign() {
		return 1 | (random.nextInt() >> 31);
	}

	public static float randomRadians() {
		return random(-RADIAN_DOMAIN, RADIAN_DOMAIN);
	}

	public static float randomDegrees() {
		return random(-DEGREE_DOMAIN, DEGREE_DOMAIN);
	}

	public static float randomHex() {
		return random(0xffffff);
	}

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/msg/349515/view.html#msg349515">http://www.java-gaming.org/</a>
     */
    private static float sin_theagentd_lookup(float rad) {
    	float index = rad * LOOKUP_TABLE_SIZE / PI2;
        int i_i = q_floor(index);
        float alpha = index - i_i;
        int i = i_i & LOOKUP_TABLE_SIZE_MASK;
        float sin1 = SIN_TABLE[i];
        float sin2 = SIN_TABLE[i + 1];
        return sin1 + (sin2 - sin1) * alpha;
    }

    public static float sin(float rad) {
        if (Config.FASTMATH) return sin_theagentd_lookup(rad);
		return (float) Math.sin(rad);
    }

    public static float cos(float rad) {
        if (Config.FASTMATH) return sin(rad + PIHalf);
        return (float) Math.cos(rad);
    }

	public static float tan(float rad) {
		return (float) Math.tan(rad);
	}

	public static float atan2(float y, float x) {
		if (Config.FASTMATH) return q_atan2(y, x);
		return (float) Math.atan2(y, x);
	}

	// https://math.stackexchange.com/questions/1098487/atan2-faster-approximation/1105038#answer-1105038
	private static float q_atan2(float y, float x) {
		float ax = x >= 0f ? x : -x, ay = y >= 0f ? y : -y;
		float a = min(ax, ay) / max(ax, ay);
		float s = a * a;
		float r = ((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a;

		if (ay > ax) r = 1.57079637f - r;
		if (x  < 0f) r = 3.14159274f - r;
		
		return y >= 0f ? r : -r;
	}

	// fused multiply add
	public static float fma(float a, float b, float c) {
		return a * b + c;
	}

	public static int nextPowerOfTwo(int value) {
		if (value == 0) return 1;
		value--;
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return   value  + 1;
	}

	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	public static int min(int a, int b) {
		return a < b ? a : b;
	}

	public static float min(float a, float b) {
		return a < b ? a : b;
	}

	public static int max(int a, int b) {
		return a > b ? a : b;
	}

	public static float max(float a, float b) {
		return a > b ? a : b;
	}

	public static float clamp(float value, float min, float max) {
		return max(min, min(max, value));
	}

	public static float map(float inRangeStart, float inRangeEnd, float outRangeStart, float outRangeEnd, float value) {
		return outRangeStart + (value - inRangeStart) * (outRangeEnd - outRangeStart) / (inRangeEnd - inRangeStart);
	}
	
	public static float lerp(float fromValue, float toValue, float delta) {
		return fma(toValue - fromValue, delta, fromValue);
	}

	public static float approach(float fromValue, float toValue, float delta) {
		if (fromValue < toValue) {
			fromValue += delta;
			if (fromValue > toValue) fromValue = toValue;
		} else {
			fromValue -= delta;
			if (fromValue < toValue) fromValue = toValue;
		}

		return fromValue;
	}

	public static float lerpAngle(float fromRadians, float toRadians, float progress) {
		float delta = ((toRadians - fromRadians + PI2 + PI) % PI2) - PI;
		return (fromRadians + delta * progress + PI2) % PI2;
	}

	public static boolean isZero(float value) {
		return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
	}

	public static boolean isZero(float value, float tolerance) {
		return Math.abs(value) <= tolerance;
	}

	public static boolean isEqual(float a, float b) {
		return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
	}

	public static boolean isEqual(float a, float b, float tolerance) {
		return Math.abs(a - b) <= tolerance;
	}

	public static float log(float a, float value) {
		return (float) (Math.log(value) / Math.log(a));
	}

	public static float log2(float value) {
		return log(2, value);
	}

	/**
	 * mu calculated by approximating the square of one, 0x1000 - 0x0001 times:
	 * <br></br>
	 * <code>
	 * Mathf.sqrt(Mathf.sqrt(Mathf.sqrt(Mathf.sqrt(i * i) *
	 * Mathf.sqrt(i * i)) * Mathf.sqrt(Mathf.sqrt(i * i) * Mathf.sqrt(i
	 * * i))) * Mathf.sqrt(Mathf.sqrt(Mathf.sqrt(i * i) * Mathf.sqrt(i
	 * * i)) * Mathf.sqrt(Mathf.sqrt(i * i) * Mathf.sqrt(i * i))))
	 * = 1.0082532
	 * </code>
	 */
	public static float sqrt(float r) {
		if (Config.FASTMATH) {
			if (r == 0) return 0;
			if (r <= 1.0082532f && r >= 1f) return 1f;
			float sqrt = Float.intBitsToFloat(((Float.floatToRawIntBits(r) - (1 << 52)) >> 1) + (1 << 61));
			sqrt = (sqrt + r / sqrt) * f1_2; // first iteration
			if (!Config.IM_FASTMATH) sqrt = (sqrt + r / sqrt) * f1_2; // second iteration, this can be removed for higher performace if necessary
			return sqrt;
		}
		return (float) Math.sqrt(r);
	}

	public static float sqrt(double r) {
		return Mathk.sqrt((float) r);
	}

	public static float invsqrt(float r) {
		return 1f / sqrt(r);
	}

	public static float invsqrt(double r) {
		return 1f / sqrt(r);
	}

	public static float floor(float v) {
		if (Config.FASTMATH) return q_floor(v);
		return (float) Math.floor(v);
	}

	private static int q_floor(float x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}
	
	public static float ceil(float v) {
		return (float) Math.ceil(v);
	}

	public static int round(float v) {
		return Math.round(v);
	}

	public static int abs(int v) {
		return v < 0 ? -v : v;
	}

	public static float abs(float v) {
		return v < 0 ? -v : v;
	}

	public static Vector2k rotate(Vector2k vec, float radians, Vector2k origin) {
		float x = vec.x - origin.x;
		float y = vec.y - origin.y;

		float cos = (float) Math.cos(radians);
		float sin = (float) Math.sin(radians);

		float xPrime = (x * cos) - (y * sin);
		float yPrime = (x * sin) + (y * cos);

		xPrime += origin.x;
		yPrime += origin.y;

		vec.x = xPrime;
		vec.y = yPrime;
		return vec;
	}

	/**
	 * write a function that takes in an array of integers which values
	 * correspond to the coefficents of a polynomial function, and evanluates
	 * the coefficents using the fast fourier transform.
	 *
	 * @author GithubCopilot
	 */
	public static float[] fft(float[] values) {
		int n = values.length;
		if (n == 1) return values;

		float[] even = new float[(int) (n * f1_2)];
		float[] odd  = new float[(int) (n * f1_2)];

		for (int i = 0; i < n * f1_2; i++) {
			even[i] = values[2 * i];
			odd[i]  = values[2 * i + 1];
		}

		float[] even_result = fft(even);
		float[] odd_result  = fft( odd);

		float[] result = new float[n];

		for (int i = 0; i < n * f1_2; i++) {
			float even_val = even_result[i];
			float odd_val  = odd_result [i];

			float cos = (float) Mathk.cos(-2 * Mathk.PI * i / n);
			// float sin = (float) Mathf.sin(-2 * Mathf.PI * i / n);

			result[i] = even_val + (cos * odd_val);
			result[i + (int) (n * f1_2)] = even_val - (cos * odd_val);
		}

		return result;
	}

	// https://github.com/dyn4j/dyn4j/blob/master/src/main/java/org/dyn4j/geometry/Vector2.java (ln. 188)
	public static Vector2k tripleProduct(Vector2k a, Vector2k b, Vector2k c) {
		Vector2k r = new Vector2k();

		/*
		 * In the following we can substitute ac and bc in r.x and r.y and with some
		 * rearrangement get a much more efficient version
		 * 
		 * double ac = a.x * c.x + a.y * c.y; double bc = b.x * c.x + b.y * c.y; r.x =
		 * b.x * ac - a.x * bc; r.y = b.y * ac - a.y * bc;
		 */

		float dot = a.x * b.y - b.x * a.y;
		r.x = -c.y * dot;
		r.y =  c.x * dot;

		return r;
	}

	public static boolean compare(float x, float y, float epsilon) {
		return Math.abs(x - y) <= epsilon * Math.max(1f, Math.max(Math.abs(x), Math.abs(y)));
	}

	public static boolean compare(Vector2k vec1, Vector2k vec2, float epsilon) {
		return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
	}

	public static boolean compare(float x, float y) {
		return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1f, Math.max(Math.abs(x), Math.abs(y)));
	}

	public static boolean compare(Vector2k vec1, Vector2k vec2) {
		return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
	}

	// https://dyn4j.org/
	public final class Epsilon {
		
		public static final double E = compute();

		private Epsilon() { }

		/**
		 * Computes an approximation of machine epsilon.
		 * 
		 * @return double
		 */
		public static final double compute() {
			double e = 0.5;
			while (1.0 + e > 1.0) e *= 0.5;
			return e;
		}

	}

}

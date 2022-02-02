/*
 * The MIT License
 *
 * Copyright (c) 2015-2021 JOML
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
 * Contains fast approximations of some {@link java.lang.Math} operations.
 * <p>
 * By default, {@link java.lang.Math} methods will be used by all other JOML classes. In order to use the approximations in this class, start the JVM with the parameter <code>-Djoml.fastmath</code>.
 * <p>
 * There are two algorithms for approximating sin/cos:
 * <ol>
 * <li>arithmetic <a href="http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815">polynomial approximation</a> contributed by roquendm 
 * <li>theagentd's <a href="http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/msg/346213/view.html#msg346213">linear interpolation</a> variant of Riven's algorithm from
 * <a href="http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/view.html">http://www.java-gaming.org/</a>
 * </ol>
 * By default, the first algorithm is being used. In order to use the second one, start the JVM with <code>-Djoml.sinLookup</code>. The lookup table bit length of the second algorithm can also be adjusted
 * for improved accuracy via <code>-Djoml.sinLookup.bits=&lt;n&gt;</code>, where &lt;n&gt; is the number of bits of the lookup table.
 * 
 * @author Kai Burjack
 * @author Ethan Temprovich
 */
public class Mathk {

    /*
     * The following implementation of an approximation of sine and cosine was
     * thankfully donated by Riven from http://java-gaming.org/.
     * 
     * The code for linear interpolation was gratefully donated by theagentd
     * from the same site.
     */
    public static final double PI = java.lang.Math.PI;
    public static final double PI2 = PI * 2.0;
    public static final double PIHalf = PI * 0.5;
    public static final double PI_4 = PI * 0.25;
    public static final double PI_SQ = PI * PI;
    public static final double PI_INV = 1.0 / PI;
    public static final float PI_f = (float) java.lang.Math.PI;
    public static final float PI2_f = PI_f * 2.0f;
    public static final float PIHalf_f = PI_f * 0.5f;
    public static final float PI_4_f = PI_f * 0.25f;
    public static final float PI_SQ_f = PI_f * PI_f;
    public static final float PI_INV_f = 1.0f / PI_f;

    public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits

    public static final double E = java.lang.Math.E;
    public static final float E_f = (float) E;

    public static final double RADIAN_DOMAIN = PI * 2.0;
	public static final double DEGREE_DOMAIN = 360.0;
    public static final double TO_RADIANS = 0.017453292519943295;
	public static final double TO_DEGREES = 57.29577951308232;
    public static final float RADIAN_DOMAIN_f = PI_f * 2.0f;
	public static final float DEGREE_DOMAIN_f = 360.0f;
	public static final float TO_RADIANS_f = 0.017453292519943295f;
	public static final float TO_DEGREES_f = 57.29577951308232f;

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

    public static double random(double range) {
		return random.nextDouble() * range;
	}

	public static double random(double start, double end) {
		return start + random.nextDouble() * (end - start);
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
		return random(-RADIAN_DOMAIN_f, RADIAN_DOMAIN_f);
	}

	public static float randomDegrees() {
		return random(-DEGREE_DOMAIN_f, DEGREE_DOMAIN_f);
	}

	public static float randomHex() {
		return random(0xffffff);
	}

    private static final int lookupBits = Options.SIN_LOOKUP_BITS;
    private static final int lookupTableSize = 1 << lookupBits;
    private static final int lookupTableSizeMinus1 = lookupTableSize - 1;
    private static final int lookupTableSizeWithMargin = lookupTableSize + 1;
    private static final float pi2OverLookupSize = PI2_f / lookupTableSize;
    private static final float lookupSizeOverPi2 = lookupTableSize / PI2_f;
    private static final float sinTable[];
    
    static {
        if (Options.FASTMATH && Options.SIN_LOOKUP) {
            sinTable = new float[lookupTableSizeWithMargin];
            for (int i = 0; i < lookupTableSizeWithMargin; i++) {
                double d = i * pi2OverLookupSize;
                sinTable[i] = (float) java.lang.Math.sin(d);
            }
        } else sinTable = null;
    }

    private static final double c1 = Double.longBitsToDouble(-4628199217061079772L);
    private static final double c2 = Double.longBitsToDouble(4575957461383582011L);
    private static final double c3 = Double.longBitsToDouble(-4671919876300759001L);
    private static final double c4 = Double.longBitsToDouble(4523617214285661942L);
    private static final double c5 = Double.longBitsToDouble(-4730215272828025532L);
    private static final double c6 = Double.longBitsToDouble(4460272573143870633L);
    private static final double c7 = Double.longBitsToDouble(-4797767418267846529L);

    /**
     * @author theagentd
     */
    public static double sin_theagentd_arith(double x){
        double xi = floor((x + PI_4) * PI_INV);
        double x_ = x - xi * PI;
        double sign = ((int)xi & 1) * -2 + 1;
        double x2 = x_ * x_;
        double sin = x_;
        double tx = x_ * x2;
        sin += tx * c1; tx *= x2;
        sin += tx * c2; tx *= x2;
        sin += tx * c3; tx *= x2;
        sin += tx * c4; tx *= x2;
        sin += tx * c5; tx *= x2;
        sin += tx * c6; tx *= x2;
        sin += tx * c7;
        return sign * sin;
    }

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361718/view.html#msg361718">http://www.java-gaming.org/</a>
     */
    public static double sin_roquen_arith(double x) {
        double xi = Mathk.floor((x + PI_4) * PI_INV);
        double x_ = x - xi * PI;
        double sign = ((int)xi & 1) * -2 + 1;
        double x2 = x_ * x_;

        // code from sin_theagentd_arith:
        // double sin = x_;
        // double tx = x_ * x2;
        // sin += tx * c1; tx *= x2;
        // sin += tx * c2; tx *= x2;
        // sin += tx * c3; tx *= x2;
        // sin += tx * c4; tx *= x2;
        // sin += tx * c5; tx *= x2;
        // sin += tx * c6; tx *= x2;
        // sin += tx * c7;
        // return sign * sin;

        double sin;
        x_  = sign*x_;
        sin =          c7;
        sin = sin*x2 + c6;
        sin = sin*x2 + c5;
        sin = sin*x2 + c4;
        sin = sin*x2 + c3;
        sin = sin*x2 + c2;
        sin = sin*x2 + c1;
        return x_ + x_*x2*sin;
    }

    private static final double s5 = Double.longBitsToDouble(4523227044276562163L);
    private static final double s4 = Double.longBitsToDouble(-4671934770969572232L);
    private static final double s3 = Double.longBitsToDouble(4575957211482072852L);
    private static final double s2 = Double.longBitsToDouble(-4628199223918090387L);
    private static final double s1 = Double.longBitsToDouble(4607182418589157889L);

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815">http://www.java-gaming.org/</a>
     */
    public static double sin_roquen_9(double v) {
      double i  = java.lang.Math.rint(v*PI_INV);
      double x  = v - i * Mathk.PI;
      double qs = 1-2*((int)i & 1);
      double x2 = x*x;
      double r;
      x = qs*x;
      r =        s5;
      r = r*x2 + s4;
      r = r*x2 + s3;
      r = r*x2 + s2;
      r = r*x2 + s1;
      return x*r;
    }

    private static final double k1 = Double.longBitsToDouble(-4628199217061079959L);
    private static final double k2 = Double.longBitsToDouble(4575957461383549981L);
    private static final double k3 = Double.longBitsToDouble(-4671919876307284301L);
    private static final double k4 = Double.longBitsToDouble(4523617213632129738L);
    private static final double k5 = Double.longBitsToDouble(-4730215344060517252L);
    private static final double k6 = Double.longBitsToDouble(4460268259291226124L);
    private static final double k7 = Double.longBitsToDouble(-4798040743777455072L);

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/joml-1-8-0-release/37491/msg/361815/view.html#msg361815">http://www.java-gaming.org/</a>
     */
    public static double sin_roquen_newk(double v) {
      double i  = java.lang.Math.rint(v*PI_INV);
      double x  = v - i * Mathk.PI;
      double qs = 1-2*((int)i & 1);
      double x2 = x*x;
      double r;
      x = qs*x;
      r =        k7;
      r = r*x2 + k6;
      r = r*x2 + k5;
      r = r*x2 + k4;
      r = r*x2 + k3;
      r = r*x2 + k2;
      r = r*x2 + k1;
      return x + x*x2*r;
    }

    /**
     * Reference: <a href="http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/msg/349515/view.html#msg349515">http://www.java-gaming.org/</a>
     */
    public static float sin_theagentd_lookup(float rad) {
        float index = rad * lookupSizeOverPi2;
        int ii = (int)java.lang.Math.floor(index);
        float alpha = index - ii;
        int i = ii & lookupTableSizeMinus1;
        float sin1 = sinTable[i];
        float sin2 = sinTable[i + 1];
        return sin1 + (sin2 - sin1) * alpha;
    }

    public static float sin(float rad) {
        if (Options.FASTMATH) {
            if (Options.SIN_LOOKUP)
                return sin_theagentd_lookup(rad);
            return (float) sin_roquen_newk(rad);
        }
        return (float) java.lang.Math.sin(rad);
    }

    public static double sin(double rad) {
        if (Options.FASTMATH) {
            if (Options.SIN_LOOKUP)
                return sin_theagentd_lookup((float) rad);
            return sin_roquen_newk(rad);
        }
        return java.lang.Math.sin(rad);
    }

    public static float cos(float rad) {
        if (Options.FASTMATH)
            return sin(rad + PIHalf_f);
        return (float) java.lang.Math.cos(rad);
    }

    public static double cos(double rad) {
        if (Options.FASTMATH)
            return sin(rad + PIHalf);
        return java.lang.Math.cos(rad);
    }

    public static float cosFromSin(float sin, float angle) {
        if (Options.FASTMATH)
            return sin(angle + PIHalf_f);
        return cosFromSinInternal(sin, angle);
    }

    private static float cosFromSinInternal(float sin, float angle) {
        // sin(x)^2 + cos(x)^2 = 1
        float cos = sqrt(1.0f - sin * sin);
        float a = angle + PIHalf_f;
        float b = a - (int)(a / PI2_f) * PI2_f;
        if (b < 0.0)
            b = PI2_f + b;
        if (b >= PI_f)
            return -cos;
        return cos;
    }

    public static double cosFromSin(double sin, double angle) {
        if (Options.FASTMATH)
            return sin(angle + PIHalf);
        // sin(x)^2 + cos(x)^2 = 1
        double cos = sqrt(1.0 - sin * sin);
        double a = angle + PIHalf;
        double b = a - (int)(a / PI2) * PI2;
        if (b < 0.0)
            b = PI2 + b;
        if (b >= PI)
            return -cos;
        return cos;
    }

    
    public static float sqrt(float r) {
        if (Options.FASTMATH)
            return sqrtInternal(r);
        return (float) java.lang.Math.sqrt(r);
    }
    
    private static float sqrtInternal(float r) {
        if (r == 0) return 0;
        if (r <= 1.0082532f && r >= 1f) return 1f;
        float sqrt = Float.intBitsToFloat(((Float.floatToRawIntBits(r) - (1 << 52)) >> 1) + (1 << 61));
        sqrt = (sqrt + r / sqrt) * 0.5f; // first iteration
        if (!Options.IMPRECISEMATH) sqrt = (sqrt + r / sqrt) * 0.5f; // second iteration, this can be removed for higher performace if necessary
        return sqrt;
    }

    public static double sqrt(double r) {
        if (Options.FASTMATH) {
            if (r == 0) return 0;
            if (r <= 1.00825 && r >= 1) return 1;
            double sqrt = Double.longBitsToDouble(((Double.doubleToRawLongBits(r) - (1L << 52)) >> 1) + (1L << 61));
            sqrt = (sqrt + r / sqrt) * 0.5; // first iteration
            if (!Options.IMPRECISEMATH) sqrt = (sqrt + r / sqrt) * 0.5; // second iteration, this can be removed for higher performace if necessary
            return sqrt;
        }
        return java.lang.Math.sqrt(r);
    }
    
    public static float invsqrt(float r) {
        return 1.0f / sqrt(r);
    }
    
    public static double invsqrt(double r) {
        return 1.0 / sqrt(r);
    }

    /* Other math functions not yet approximated */

    public static float tan(float r) {
        return (float) java.lang.Math.tan(r);
    }

    public static double tan(double r) {
        return java.lang.Math.tan(r);
    }

    public static float acos(float r) {
        return (float) java.lang.Math.acos(r);
    }

    public static double acos(double r) {
        return java.lang.Math.acos(r);
    }

    public static float safeAcos(float v) {
        if (v < -1.0f)
            return Mathk.PI_f;
        else if (v > +1.0f)
            return 0.0f;
        else
            return acos(v);
    }

    public static double safeAcos(double v) {
        if (v < -1.0)
            return Mathk.PI;
        else if (v > +1.0)
            return 0.0;
        else
            return acos(v);
    }

    /**
     * https://math.stackexchange.com/questions/1098487/atan2-faster-approximation/1105038#answer-1105038
     */
    private static double fastAtan2(double y, double x) {
        double ax = x >= 0.0 ? x : -x, ay = y >= 0.0 ? y : -y;
        double a = min(ax, ay) / max(ax, ay);
        double s = a * a;
        double r = ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a;
        if (ay > ax)
            r = 1.57079637 - r;
        if (x < 0.0)
            r = 3.14159274 - r;
        return y >= 0 ? r : -r;
    }

    public static float atan2(float y, float x) {
        return (float) java.lang.Math.atan2(y, x);
    }

    public static double atan2(double y, double x) {
        if (Options.FASTMATH)
            return fastAtan2(y, x);
        return java.lang.Math.atan2(y, x);
    }

    public static float asin(float r) {
        return (float) java.lang.Math.asin(r);
    }

    public static double asin(double r) {
        return java.lang.Math.asin(r);
    }

    public static float safeAsin(float r) {
        return r <= -1.0f ? -PIHalf_f : r >= 1.0f ? PIHalf_f : asin(r);
    }

    public static double safeAsin(double r) {
        return r <= -1.0 ? -PIHalf : r >= 1.0 ? PIHalf : asin(r);
    }

    public static Vector2fc rotate(Vector2fc vec, float radians, Vector2fc origin) {
		float x = vec.x() - origin.x();
		float y = vec.y() - origin.y();

		float cos = cos(radians);
		float sin = sin(radians);

		float xPrime = (x * cos) - (y * sin);
		float yPrime = (x * sin) + (y * cos);

		xPrime += origin.x();
		yPrime += origin.y();

		return new Vector2f(xPrime, yPrime);
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

    public static float abs(float r) {
        return java.lang.Math.abs(r);
    }

    public static double abs(double r) {
        return java.lang.Math.abs(r);
    }

    static boolean absEqualsOne(float r) {
        return (Float.floatToRawIntBits(r) & 0x7FFFFFFF) == 0x3F800000;
    }

    static boolean absEqualsOne(double r) {
        return (Double.doubleToRawLongBits(r) & 0x7FFFFFFFFFFFFFFFL) == 0x3FF0000000000000L;
    }

    public static int abs(int r) {
        return java.lang.Math.abs(r);
    }

    public static int max(int x, int y) {
        return java.lang.Math.max(x, y);
    }

    public static int min(int x, int y) {
        return java.lang.Math.min(x, y);
    }

    public static double min(double a, double b) {
        return a < b ? a : b;
    }

    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static double max(double a, double b) {
        return a > b ? a : b;
    }

    public static float clamp(float a, float b, float val){
        return max(a,min(b,val));
    }

    public static double clamp(double a, double b, double val) {
        return max(a,min(b,val));
    }

    public static int clamp(int a, int b, int val) {
        return max(a, min(b, val));
    }

    public static float toRadians(float angles) {
        return angles * TO_RADIANS_f;
    }

    public static double toRadians(double angles) {
        return angles * TO_RADIANS;
    }

    public static float toDegrees(float radians) {
        return radians * TO_DEGREES_f;
    }

    public static double toDegrees(double angles) {
        return angles * TO_DEGREES;
    }

    public static double floor(double v) {
        return java.lang.Math.floor(v);
    }

    public static float floor(float v) {
        return (float) java.lang.Math.floor(v);
    }

    public static double ceil(double v) {
        return java.lang.Math.ceil(v);
    }

    public static float ceil(float v) {
        return (float) java.lang.Math.ceil(v);
    }

    public static long round(double v) {
        return java.lang.Math.round(v);
    }

    public static int round(float v) {
        return java.lang.Math.round(v);
    }

    public static double exp(double a) {
        return java.lang.Math.exp(a);
    }

    public static boolean isFinite(double d) {
        return abs(d) <= Double.MAX_VALUE;
    }

    public static boolean isFinite(float f) {
        return abs(f) <= Float.MAX_VALUE;
    }

    public static float fma(float a, float b, float c) {
//#ifdef __HAS_MATH_FMA__
        if (Runtime.HAS_Math_fma)
            return java.lang.Math.fma(a, b, c);
//#endif
        return a * b + c;
    }

    public static double fma(double a, double b, double c) {
//#ifdef __HAS_MATH_FMA__
        if (Runtime.HAS_Math_fma)
            return java.lang.Math.fma(a, b, c);
//#endif
        return a * b + c;
    }

    public static int roundUsing(float v, int mode) {
        switch (mode) {
        case RoundingMode.TRUNCATE:
            return (int) v;
        case RoundingMode.CEILING:
            return (int) java.lang.Math.ceil(v);
        case RoundingMode.FLOOR:
            return (int) java.lang.Math.floor(v);
        case RoundingMode.HALF_DOWN:
            return roundHalfDown(v);
        case RoundingMode.HALF_UP:
            return roundHalfUp(v);
        case RoundingMode.HALF_EVEN:
            return roundHalfEven(v);
        default:
            throw new UnsupportedOperationException();
        }
    }

    public static int roundUsing(double v, int mode) {
        switch (mode) {
        case RoundingMode.TRUNCATE:
            return (int) v;
        case RoundingMode.CEILING:
            return (int) java.lang.Math.ceil(v);
        case RoundingMode.FLOOR:
            return (int) java.lang.Math.floor(v);
        case RoundingMode.HALF_DOWN:
            return roundHalfDown(v);
        case RoundingMode.HALF_UP:
            return roundHalfUp(v);
        case RoundingMode.HALF_EVEN:
            return roundHalfEven(v);
        default:
            throw new UnsupportedOperationException();
        }
    }
    
    public static float map(float inRangeStart, float inRangeEnd, float outRangeStart, float outRangeEnd, float value) {
        return outRangeStart + (outRangeEnd - outRangeStart) * (value - inRangeStart) / (inRangeEnd - inRangeStart);
    }
    
    public static double map(double inRangeStart, double inRangeEnd, double outRangeStart, double outRangeEnd, double value) {
        return outRangeStart + (outRangeEnd - outRangeStart) * (value - inRangeStart) / (inRangeEnd - inRangeStart);
    }

    public static float approach(float fromValue, float toValue, float delta) {
        if (fromValue < toValue) {
            if (fromValue + delta > toValue) {
                return toValue;
            }
            return fromValue + delta;
        } else if (fromValue > toValue) {
            if (fromValue - delta < toValue) {
                return toValue;
            }
            return fromValue - delta;
        }
        return fromValue;
    }

    public static double approach(double fromValue, double toValue, double delta) {
        if (fromValue < toValue) {
            if (fromValue + delta > toValue) {
                return toValue;
            }
            return fromValue + delta;
        } else if (fromValue > toValue) {
            if (fromValue - delta < toValue) {
                return toValue;
            }
            return fromValue - delta;
        }
        return fromValue;
    }

    public static float lerp(float a, float b, float t){
        return Mathk.fma(b - a, t, a);
    }

    public static double lerp(double a, double b, double t) {
        return Mathk.fma(b - a, t, a);
    }

    public static float biLerp(float q00, float q10, float q01, float q11, float tx, float ty) {
        float lerpX1 = lerp(q00, q10, tx);
        float lerpX2 = lerp(q01, q11, tx);
        return lerp(lerpX1, lerpX2, ty);
    }

    public static double biLerp(double q00, double q10, double q01, double q11, double tx, double ty) {
        double lerpX1 = lerp(q00, q10, tx);
        double lerpX2 = lerp(q01, q11, tx);
        return lerp(lerpX1, lerpX2, ty);
    }

    public static float triLerp(float q000, float q100, float q010, float q110, float q001, float q101, float q011, float q111, float tx, float ty, float tz) {
        float x00 = lerp(q000, q100, tx);
        float x10 = lerp(q010, q110, tx);
        float x01 = lerp(q001, q101, tx);
        float x11 = lerp(q011, q111, tx);
        float y0 = lerp(x00, x10, ty);
        float y1 = lerp(x01, x11, ty);
        return lerp(y0, y1, tz);
    }

    public static double triLerp(double q000, double q100, double q010, double q110, double q001, double q101, double q011, double q111, double tx, double ty, double tz) {
        double x00 = lerp(q000, q100, tx);
        double x10 = lerp(q010, q110, tx);
        double x01 = lerp(q001, q101, tx);
        double x11 = lerp(q011, q111, tx);
        double y0 = lerp(x00, x10, ty);
        double y1 = lerp(x01, x11, ty);
        return lerp(y0, y1, tz);
    }

    public static float lerpAngle(float fromRadians, float toRadians, float progress) {
        return lerpAngle(fromRadians, toRadians, progress, -RADIAN_DOMAIN_f, RADIAN_DOMAIN_f);
    }

    public static double lerpAngle(double fromRadians, double toRadians, double progress) {
        return lerpAngle(fromRadians, toRadians, progress, -RADIAN_DOMAIN, RADIAN_DOMAIN);
    }

    public static float lerpAngle(float fromRadians, float toRadians, float progress, float minRadians, float maxRadians) {
        float delta = ((toRadians - fromRadians + PI2_f + PI_f) % PI2_f) - PI_f;
        float result = (fromRadians + delta * progress + PI2_f) % PI2_f;
        if (result < minRadians) {
            result += PI2_f;
        } else if (result > maxRadians) {
            result -= PI2_f;
        }
        return result;
    }

    public static double lerpAngle(double fromRadians, double toRadians, double progress, double minRadians, double maxRadians) {
        double delta = ((toRadians - fromRadians + PI2 + PI) % PI2) - PI;
        double result = (fromRadians + delta * progress + PI2) % PI2;
        if (result < minRadians) {
            result += PI2;
        } else if (result > maxRadians) {
            result -= PI2;
        }
        return result;
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

		float[] even = new float[(int) (n * 0.5f)];
		float[] odd  = new float[(int) (n * 0.5f)];

		for (int i = 0; i < n * 0.5f; i++) {
			even[i] = values[2 * i];
			odd[i]  = values[2 * i + 1];
		}

		float[] even_result = fft(even);
		float[] odd_result  = fft( odd);

		float[] result = new float[n];

		for (int i = 0; i < n * 0.5f; i++) {
			float even_val = even_result[i];
			float odd_val  = odd_result [i];

			float cos = (float) Mathk.cos(-2 * Mathk.PI * i / n);
			// float sin = (float) Mathf.sin(-2 * Mathf.PI * i / n);

			result[i] = even_val + (cos * odd_val);
			result[i + (int) (n * 0.5f)] = even_val - (cos * odd_val);
		}

		return result;
	}

    // https://github.com/dyn4j/dyn4j/blob/master/src/main/java/org/dyn4j/geometry/Vector2.java (ln. 188)
	public static Vector2fc tripleProduct(Vector2fc a, Vector2fc b, Vector2fc c) {
		Vector2f r = new Vector2f();

		/*
		 * In the following we can substitute ac and bc in r.x and r.y and with some
		 * rearrangement get a much more efficient version
		 * 
		 * double ac = a.x * c.x + a.y * c.y; double bc = b.x * c.x + b.y * c.y; r.x =
		 * b.x * ac - a.x * bc; r.y = b.y * ac - a.y * bc;
		 */

		float dot = a.x() * b.y() - b.x() * a.y();
		r.x = -c.y() * dot;
		r.y =  c.x() * dot;

		return r;
	}

    public static boolean isZero(float value) {
        return isZero(value, FLOAT_ROUNDING_ERROR);
    }
    
    public static boolean isZero(float value, float epsilon) {
        return abs(value) <= epsilon;
    }

    public static boolean isZero(double value) {
        return isZero(value, FLOAT_ROUNDING_ERROR);
    }
    
    public static boolean isZero(double value, double epsilon) {
        return abs(value) <= epsilon;
    }

    public static boolean isEqual(float a, float b) {
		return abs(a - b) <= FLOAT_ROUNDING_ERROR;
	}

	public static boolean isEqual(float a, float b, float tolerance) {
		return abs(a - b) <= tolerance;
	}

    public static boolean isEqual(double a, double b) {
        return abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean isEqual(double a, double b, double tolerance) {
        return abs(a - b) <= tolerance;
    }

    public static boolean compare(float x, float y, float epsilon) {
        return abs(x - y) <= epsilon * max(1f, max(abs(x), abs(y)));
    }

    public static boolean compare(double x, double y, double epsilon) {
        return abs(x - y) <= epsilon * max(1d, max(abs(x), abs(y)));
    }

    public static boolean compare(float x, float y) {
        return compare(x, y, FLOAT_ROUNDING_ERROR);
    }

    public static boolean compare(double x, double y) {
        return compare(x, y, FLOAT_ROUNDING_ERROR);
    }

    public static int roundHalfEven(float v) {
        return (int) java.lang.Math.rint(v);
    }

    public static int roundHalfDown(float v) {
        return (v > 0) ? (int) java.lang.Math.ceil(v - 0.5d) : (int) java.lang.Math.floor(v + 0.5d);
    }

    public static int roundHalfUp(float v) {
        return (v > 0) ? (int) java.lang.Math.floor(v + 0.5d) : (int) java.lang.Math.ceil(v - 0.5d);
    }

    public static int roundHalfEven(double v) {
        return (int) java.lang.Math.rint(v);
    }

    public static int roundHalfDown(double v) {
        return (v > 0) ? (int) java.lang.Math.ceil(v - 0.5d) : (int) java.lang.Math.floor(v + 0.5d);
    }

    public static int roundHalfUp(double v) {
        return (v > 0) ? (int) java.lang.Math.floor(v + 0.5d) : (int) java.lang.Math.ceil(v - 0.5d);
    }

    public static double signum(double v) {
        return java.lang.Math.signum(v);
    }

    public static float signum(float v) {
        return java.lang.Math.signum(v);
    }

    public static int signum(int v) {
        int r;
//#ifdef __HAS_INTEGER_SIGNUM__
        r = Integer.signum(v);
//#else
        // code from java.lang.Integer.signum(int)
        r = (v >> 31) | (-v >>> 31);
//#endif
        return r;
    }

    public static int signum(long v) {
        int r;
//#ifdef __HAS_INTEGER_SIGNUM__
        r = Long.signum(v);
//#else
        // code from java.lang.Long.signum(long)
        r = (int) ((v >> 63) | (-v >>> 63));
//#endif
        return r;
    }

    public static float log(float a, float value) {
		return (float) (Math.log(value) / Math.log(a));
	}

	public static float log2(float value) {
		return log(2, value);
	}

    // https://dyn4j.org/
	public static final class Epsilon {
		
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

/*
 * The MIT License
 *
 * Copyright (c) 2022 Ethan Temprovich.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
//#ifdef __HAS_NIO__
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
//#endif

import net.acidfrog.kronos.math.Mathk.Epsilon;

/**
 * Represents a 2D vector with single-precision.
 *
 * @author Ethan Temprovich
 */
public class Vector2k implements Externalizable, Cloneable {

    private static final long serialVersionUID = 1L;

	/**
	 * The X component of this vector.
	 */
    public float x;
	/**
	 * The Y component of this vector.
	 */
    public float y;
    
    public Vector2k() {
        set(0.0f);
    }

    public Vector2k(float n) {
        set(n);
    }

    public Vector2k(float x, float y) {
        set(x, y);
    }

    public Vector2k(double n) {
        set(n);
    }

    public Vector2k(double x, double y) {
        set(x, y);
    }

    public Vector2k(Vector2k v) {
        set(v);
    }

    public Vector2k(Vector2ic v) {
        set(v);
    }

    public Vector2k(Vector2fc v) {
        set(v);
    }

    public Vector2k(Vector2dc v) {
        set(v);
    }

    public Vector2k(float[] n) {
        set(n);
    }

    public Vector2k(float[] n, int offset) {
        set(n, offset);
    }

    public Vector2k(float[] n, int offset, int stride) {
        set(n, offset, stride);
    }

    public Vector2k(double[] n) {
        set(n);
    }

    public Vector2k(double[] n, int offset) {
        set(n, offset);
    }

    public Vector2k(double[] n, int offset, int stride) {
        set(n, offset, stride);
    }

    public float x() {
        return x;
    }


    public float y() {
        return y;
    }

//#ifdef __HAS_NIO__
    public ByteBuffer get(ByteBuffer buffer) {
        MemUtil.INSTANCE.put(new Vector2f(this), buffer.position(), buffer);
        return buffer;
    }

    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put(new Vector2f(this), index, buffer);
        return buffer;
    }

    public FloatBuffer get(FloatBuffer buffer) {
        MemUtil.INSTANCE.put(new Vector2f(this), buffer.position(), buffer);
        return buffer;
    }

    public FloatBuffer get(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.put(new Vector2f(this), index, buffer);
        return buffer;
    }
//#endif

//#ifdef __HAS_UNSAFE__
    public Vector2k getToAddress(long address) {
        if (Options.NO_UNSAFE)
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        MemUtil.MemUtilUnsafe.put(new Vector2f(this), address);
        return this;
    }
//#endif

    public float get(int component) throws IllegalArgumentException {
        switch (component) {
        case 0: return x;
        case 1: return y;
        default: throw new IllegalArgumentException();
        }
    }

    public Vector2i get(int mode, Vector2i dest) {
        dest.x = Mathk.roundUsing(this.x(), mode);
        dest.y = Mathk.roundUsing(this.y(), mode);
        return dest;
    }

    public Vector2f get(Vector2f dest) {
        dest.x = this.x();
        dest.y = this.y();
        return dest;
    }

    public Vector2d get(Vector2d dest) {
        dest.x = this.x();
        dest.y = this.y();
        return dest;
    }

    public Vector2k get(Vector2k dest) {
        dest.x = this.x();
        dest.y = this.y();
        return dest;
    }

//#ifdef __HAS_NIO__
    /**
     * Read this vector from the supplied {@link ByteBuffer} at the current
     * buffer {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which
     * the vector is read, use {@link #set(int, ByteBuffer)}, taking
     * the absolute position as parameter.
     *
     * @param buffer
     *        values will be read in <code>x, y</code> order
     * @return this
     * @see #set(int, ByteBuffer)
     */
    public Vector2k set(ByteBuffer buffer) {
        MemUtil.INSTANCE.get(new Vector2f(this), buffer.position(), buffer);
        return this;
    }

    /**
     * Read this vector from the supplied {@link ByteBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     *
     * @param index
     *        the absolute position into the ByteBuffer
     * @param buffer
     *        values will be read in <code>x, y</code> order
     * @return this
     */
    public Vector2k set(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.get(new Vector2f(this), index, buffer);
        return this;
    }

    /**
     * Read this vector from the supplied {@link FloatBuffer} at the current
     * buffer {@link FloatBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given FloatBuffer.
     * <p>
     * In order to specify the offset into the FloatBuffer at which
     * the vector is read, use {@link #set(int, FloatBuffer)}, taking
     * the absolute position as parameter.
     *
     * @param buffer
     *        values will be read in <code>x, y</code> order
     * @return this
     * @see #set(int, FloatBuffer)
     */
    public Vector2k set(FloatBuffer buffer) {
        MemUtil.INSTANCE.get(new Vector2f(this), buffer.position(), buffer);
        return this;
    }

    /**
     * Read this vector from the supplied {@link FloatBuffer} starting at the specified
     * absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given FloatBuffer.
     *
     * @param index 
     *        the absolute position into the FloatBuffer
     * @param buffer
     *        values will be read in <code>x, y</code> order
     * @return this
     */
    public Vector2k set(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.get(new Vector2f(this), index, buffer);
        return this;
    }
//#endif

//#ifdef __HAS_UNSAFE__
    /**
     * Set the values of this vector by reading 2 float values from off-heap memory,
     * starting at the given address.
     * <p>
     * This method will throw an {@link UnsupportedOperationException} when JOML is used with `-Djoml.nounsafe`.
     * <p>
     * <em>This method is unsafe as it can result in a crash of the JVM process when the specified address range does not belong to this process.</em>
     * 
     * @param address
     *              the off-heap memory address to read the vector values from
     * @return this
     */
    public Vector2k setFromAddress(long address) {
        if (Options.NO_UNSAFE)
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        MemUtil.MemUtilUnsafe.get(new Vector2f(this), address);
        return this;
    }
//#endif

    public Vector2k set(float n) {
        x = y = n;
        return this;
    }

    public Vector2k set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2k set(double n) {
        x = (float)n;
        y = (float)n;
        return this;
    }

    public Vector2k set(double x, double y) {
        this.x = (float)x;
        this.y = (float)y;
        return this;
    }

    public Vector2k set(Vector2k v) {
        x = v.x;
        y = v.y;
        return this;
    }

    public Vector2k set(Vector2ic v) {
        x = v.x();
        y = v.y();
        return this;
    }

    public Vector2k set(Vector2fc v) {
        x = v.x();
        y = v.y();
        return this;
    }

    public Vector2k set(Vector2dc v) {
        x = (float) v.x();
        y = (float) v.y();
        return this;
    }

    public Vector2k set(float[] n) {
        return set(n, 0, 1);
    }

    public Vector2k set(float[] n, int offset) {
        return set(n, offset, 1);
    }

    public Vector2k set(float[] n, int offset, int stride) {
        if (n == null)
            throw new IllegalArgumentException("Array must not be null");
        if (offset < 0 || offset >= n.length)
            throw new IllegalArgumentException("Offset must be >= 0 and < " + n.length);
        if (stride < 1)
            throw new IllegalArgumentException("Stride must be >= 1");
            
        x = n[offset];
        y = n[offset + stride];
        return this;
    }

    public Vector2k set(double[] n) {
        return set(n, 0, 1);
    }

    public Vector2k set(double[] n, int offset) {
        return set(n, offset, 1);
    }

    public Vector2k set(double[] n, int offset, int stride) {
        if (n == null)
            throw new IllegalArgumentException("Array must not be null");
        if (offset < 0 || offset >= n.length)
            throw new IllegalArgumentException("Offset must be >= 0 and < " + n.length);
        if (stride < 1)
            throw new IllegalArgumentException("Stride must be >= 1");
            
        x = (float) n[offset];
        y = (float) n[offset + stride];
        return this;
    }

   	/**
	 * Sets the x and y values of this vector 0.
	 */
	public void zero() {
		this.x = 0;
		this.y = 0;
	}

	/**
	 * Adds s to this vector and returns this.
	 */
	public Vector2k addi(float s) {
		return add(s, this);
	}

	/**
	 * Adds v to this vector and returns this.
	 */
	public Vector2k addi(Vector2k v) {
		return add(v, this);
	}

	/**
	 * Returns a new vector that is the sum between this vector and s.
	 */
	public Vector2k add(float s) {
		return add(s, new Vector2k());
	}
	
	/**
	 * Returns a new vector that is the addition of this vector and v.
	 */
	public Vector2k add(Vector2k v) {
		return add(v, new Vector2k());
	}

	/**
	 * Sets out to the sum of this vector and s and returns out.
	 */
	public Vector2k add(float s, Vector2k out) {
		out.x = x + s;
		out.y = y + s;
		return out;
	}


	/**
	 * Sets out to the addition of this vector and v and returns out.
	 */
	public Vector2k add(Vector2k v, Vector2k out) {
		out.x = x + v.x;
		out.y = y + v.y;
		return out;
	}

	/**
	 * Adds v * s to this vector and returns this.
	 */
	public Vector2k addsi(Vector2k v, float s) {
		this.x = x + v.x * s;
		this.y = y + v.y * s;
		return this;
	}

	/**
	 * Subtracts v from this vector and returns this.
	 */
	public Vector2k subi(float s) {
		return sub(s, this);
	}

	/**
	 * Subtracts v from this vector and returns this.
	 */
	public Vector2k subi(Vector2k v) {
		return sub(v, this);
	}

	/**
	 * Sets out to the subtraction of v from this vector and returns out.
	 */
	public Vector2k sub(float s, Vector2k out) {
		out.x = x - s;
		out.y = y - s;
		return out;
	}

	/**
	 * Sets out to the subtraction of v from this vector and returns out.
	 */
	public Vector2k sub(Vector2k v, Vector2k out) {
		out.x = x - v.x;
		out.y = y - v.y;
		return out;
	}

	/**
	 * Returns a new vector that is the subtraction of v from this vector.
	 */
	public Vector2k sub(float s) {
		return sub(s, new Vector2k());
	}

	/**
	 * Returns a new vector that is the subtraction of v from this vector.
	 */
	public Vector2k sub(Vector2k v) {
		return sub(v, new Vector2k());
	}

	/**
	 * Multiplies this vector by s and returns this.
	 */
	public Vector2k muli(float s) {
		return mul(s, this);
	}

	/**
	 * Multiplies this vector by v and returns this.
	 */
	public Vector2k muli(Vector2k v) {
		return mul(v, this);
	}

	/**
	 * Sets out to this vector multiplied by s and returns out.
	 */
	public Vector2k mul(float s, Vector2k out) {
		out.x = s * x;
		out.y = s * y;
		return out;
	}
	
	/**
	 * Sets out to the product of this vector and v and returns out.
	 */
	public Vector2k mul(Vector2k v, Vector2k out) {
		out.x = x * v.x;
		out.y = y * v.y;
		return out;
	}

	/**
	 * Returns a new vector that is a multiplication of this vector and s.
	 */
	public Vector2k mul(float s) {
		return mul(s, new Vector2k());
	}

	/**
	 * Returns a new vector that is the product of this vector and v.
	 */
	public Vector2k mul(Vector2k v) {
		return mul(v, new Vector2k());
	}

	/**
	 * Divides this vector by s and returns this.
	 */
	public Vector2k divi(float s) {
		return div(s, this);
	}

	/**
	 * Divides this vector by v and returns this.
	 */
	public Vector2k divi(Vector2k v) {
		return div(v, this);
	}

	/**
	 * Sets out to the division of this vector and s and returns out.
	 */
	public Vector2k div(float s, Vector2k out) {
		out.x = x / s;
		out.y = y / s;
		return out;
	}

	/**
	 * Sets out to the division of this vector and v and returns out.
	 */
	public Vector2k div(Vector2k v, Vector2k out) {
		out.x = x / v.x;
		out.y = y / v.y;
		return out;
	}

	/**
	 * Returns a new vector that is a division between this vector and s.
	 */
	public Vector2k div(float s) {
		return div(s, new Vector2k());
	}

	/**
	 * Returns a new vector that is the division of this vector by v.
	 */
	public Vector2k div(Vector2k v) {
		return div(v, new Vector2k());
	}

	/**
	 * Returns the dot product between this vector and v.
	 */
	public float dot(Vector2k v) {
		return dot(this, v);
	}

	/*
	 * Returns the dot product between a and b.
	 */
	public static float dot(Vector2k a, Vector2k b) {
		return a.x * b.x + a.y * b.y;
	}

	/**
	 * Returns the scalar cross between this vector and v. This is essentially the
	 * magnitude of the cross product if this vector were 3d.
	 */
	public float cross(Vector2k v) {
		return cross(this, v);
	}
	
	/**
	 * Sets this vector to the cross between a and v and returns this.
	 */
	public Vector2k cross(float a, Vector2k v) {
		return cross(a, v, this);
	}

	/**
	 * Sets this vector to the cross between v and a and returns this.
	 */
	public Vector2k cross(Vector2k v, float a) {
		return cross(v, a, this);
	}

	/**
	 * Returns the cross product of vector a and b.
	 */
	public static float cross(Vector2k a, Vector2k b) {
		return a.x * b.y - a.y * b.x;
	}

	/**
	 * Sets the x and y components of out to the cross product of a
	 * and b and returns out.
	 */
	public static Vector2k cross(float a, Vector2k v, Vector2k out) {
		out.x = v.y * -a;
		out.y = v.x * a;
		return out;
	}

	/**
	 * Sets the x and y components of out to the cross product of v
	 * and a and returns out.
	 */
	public static Vector2k cross(Vector2k v, float a, Vector2k out) {
		out.x = v.y * a;
		out.y = v.x * -a;
		return out;
	}

	/**
	 * Negates this vector and returns this.
	 */
	public Vector2k negate() {
		return negate(this);
	}

	/**
	 * Sets out to the negation of this vector and returns out.
	 */
	public Vector2k negate(Vector2k out) {
		out.x = -x;
		out.y = -y;
		return out;
	}

	/**
	 * Returns a new vector that is the negation to this vector.
	 */
	public Vector2k negated() {
		return negate(new Vector2k());
	}

	/**
	 * Normalizes this vector, making it a unit vector. A unit vector 
	 * has a magnitude of 1.0.
	 */
	public float normalize() {
		float mag = magnitude();
		float magSq = magnitudeSquared();
		float e = Mathk.FLOAT_ROUNDING_ERROR;

		if (magSq > e * e) {
			float invMag = 1f / Mathk.sqrt(magSq);
			if (invMag == (Float.POSITIVE_INFINITY)) invMag = 0;
			x *= invMag;
			y *= invMag;
		} else {
			zero();
		}
		
		return mag;
	}

	/**
	 * Returns a new vector that is the normalized version of this vector.
	 */
	public Vector2k normalized() {
		float magSq = magnitudeSquared();
		Vector2k out = new Vector2k(this);
		float e = Mathk.FLOAT_ROUNDING_ERROR;

		if (magSq > e * e) {
			float invMag = 1f / Mathk.sqrt(magSq);
			if (invMag == (Float.POSITIVE_INFINITY)) invMag = 0;
			out.x *= invMag;
			out.y *= invMag;
		} else {
			out.zero();
		}

		return out;
	}

	/**
	 * Returns perpendicular vector to this vector. Also known as the
	 * vectors normal.
	 */
	public Vector2k normal() {
		float _y = y;
		this.y = x * -1;
		this.x = _y;
		return this;
	}
	
	/**
	 * Returns a new Vector2 that is left-perpendicular to this vector.
	 */
	public Vector2k getNormalLeft() {
		return new Vector2k(-y, x);
	}

	/**
	 * Sets this vector to the left-perpendicular of this vector and returns this.
	 */
	public Vector2k left() {
		float t = this.x;
		this.x = this.y;
		this.y = -t;
		return this;
	}

	/**
	 * Returns a new Vector2 that is right-perpendicular to this vector.
	 */
	public Vector2k getNormalRight() {
		return new Vector2k(y, -x);
	}

	/**
	 * Sets this vector to the right-perpendicular of this vector and returns this.
	 */
	public Vector2k right() {
		float t = this.x;
		this.x = -this.y;
		this.y = t;
		return this;
	}

	/**
	 * Projects this vector onto the given vector and returns a new Vector2.
	 */
	public Vector2k project(Vector2k vector) {
		double dotProd = this.dot(vector);
		double denominator = vector.dot(vector);
		if (denominator <= Epsilon.E) return new Vector2k();
		denominator = dotProd / denominator;
		return new Vector2k(denominator * vector.x, denominator * vector.y);		
	}

	/**
	 * Rotates this vector by the given radians.
	 */
	public void rotate(float radians) {
		float c = (float) StrictMath.cos(radians);
		float s = (float) StrictMath.sin(radians);

		float xp = x * c - y * s;
		float yp = x * s + y * c;

		x = xp;
		y = yp;
	}

	/**
	 * Returns the angle formed by the vector, counterclockwise from the +x axis.
	 * Angle is in degrees, ranging from 0 to 360.
	 */
	public float angle() {
		// Finds the angle formed by the vector. We do this by finding the arctangent of
		// (y/x). In this case, we pass the y component as the first argument and the x
		// component
		// as the second argument. The returned angle is in radians. We convert it to
		// degrees.
		float angle = Mathk.toDegrees(Mathk.atan2(y, x));

		// The angle returned by the arctangent method is between -180 and 180. Thus, if
		// the angle is negative, we add 360 degrees to the angle. This makes it so that
		// there
		// are no negative angles. The angles will range from 0 to 360, counterclockwise
		// from the +x axis.
		if (angle < 0) angle += 360; // Adds 360 degrees to the negative angle to prevent a negative angle.

		// Returns the angle formed by the vector.
		return angle;
	}

	/**
	 * Returns the squared magnitude of this vector.
	 */
	public float magnitudeSquared() {
		return x * x + y * y;
	}

	/**
	 * Returns the length of this vector.
	 */
	public float magnitude() {
		return Mathk.sqrt(x * x + y * y);
	}
	
	/**
	 * Returns the squared distance between this vector and v.
	 */
	public float distanceSq(Vector2k v) {
		return distanceSq(this, v);
	}
	
	/**
	 * Returns the distance between this vector and v.
	 */
	public float distance(Vector2k v) {
		return distance(this, v);
	}

	/**
	 * Returns the distance squared between vectors a and b.
	 */
	public static float distanceSq(Vector2k a, Vector2k b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;

		return dx * dx + dy * dy;
	}

	/**
	 * Returns the distance between vectors a and b.
	 */
	public static float distance(Vector2k a, Vector2k b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;

		return Mathk.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Sets this vector to the minimum between a and b.
	 */
	public Vector2k mini(Vector2k a, Vector2k b) {
		return min(a, b, this);
	}

	/**
	 * Sets out to the minimum between a and b.
	 */
	public static Vector2k min(Vector2k a, Vector2k b, Vector2k out) {
		out.x = Mathk.min(a.x, b.x);
		out.y = Mathk.min(a.y, b.y);
		return out;
	}

	/**
	 * Sets this vector to the maximum between a and b.
	 */
	public Vector2k maxi(Vector2k a, Vector2k b) {
		return max(a, b, this);
	}

	/**
	 * Sets out to the maximum between a and b.
	 */
	public static Vector2k max(Vector2k a, Vector2k b, Vector2k out) {
		out.x = (float) StrictMath.max(a.x, b.x);
		out.y = (float) StrictMath.max(a.y, b.y);
		return out;
	}

	/**
	 * Allocates an array of zero vectors of specified length.
	 */
	public static Vector2k[] arrayOf(int length) {
		Vector2k[] array = new Vector2k[length];
		for (int i = 0; i < length; i++) array[i] = new Vector2k();
		return array;
	}

	/**
	 * Deternimes if this vectors components are both equal to 0.
	 */
	public boolean isZero() {
		return x == 0 && y == 0;
	}

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
    }


    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        x = in.readFloat();
        y = in.readFloat();
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Vector2k)) return false;
		Vector2k other = (Vector2k) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
		return true;
	}

    @Override
    public Vector2k clone() {
        return new Vector2k(this);
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Vector2 [x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append("]");
		return builder.toString();
	}

}

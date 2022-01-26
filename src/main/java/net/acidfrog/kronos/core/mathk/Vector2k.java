package net.acidfrog.kronos.core.mathk;

import net.acidfrog.kronos.core.mathk.Mathk.Epsilon;

/**
 * A 2D vector.
 * 
 * @author Ethan Temprovich
 */
public class Vector2k {

	public static final Vector2k UP 		= 	new Vector2k(+-0,  +1);
	public static final Vector2k DOWN 	= 	new Vector2k(+-0,  -1);
	public static final Vector2k LEFT 	= 	new Vector2k( -1, +-0);
	public static final Vector2k RIGHT   =   new Vector2k( +1, +-0);
	public static final Vector2k ONE 	= 	new Vector2k( +1,  +1);
	public static final Vector2k ZERO 	= 	new Vector2k(+-0, +-0);

	public float x;
	public float y;

	public Vector2k() {
		this.x = 0.0f;
		this.y = 0.0f;
	}

	public Vector2k(float n) {
		this.x = n;
		this.y = n;
	}

	public Vector2k(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2k(double x, double y) {
		this.x = (float) x;
		this.y = (float) y;
	}

	public Vector2k(Vector2k vector2) {
		this.x = vector2.x;
		this.y = vector2.y;
	}

	public Vector2k(float[] n) {
		this.x = n[0];
		this.y = n[1];
	}

	public Vector2k(float[] n, int offset) {
		this.x = n[offset];
		this.y = n[offset + 1];
	}

	/**
	 * Sets this vectors components to the given values.
	 */
	public Vector2k set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Sets this vectors components to the given value.
	 */
	public Vector2k set(float n) {
		this.x = n;
		this.y = n;
		return this;
	}

	/**
	 * Sets this vectors components to the given vectors components.
	 */
	public Vector2k set(Vector2k vector2) {
		this.x = vector2.x;
		this.y = vector2.y;
		return this;
	}

	/**
	 * Sets this vectors components to the given arrays first indices value and 
	 * second indices value.
	 */
	public Vector2k set(float[] n) {
		this.x = n[0];
		this.y = n[1];
		return this;
	}

	/**
	 * Sets this vectors components to the given arrays indice at given offset, 
	 * and the indice at offsets mask (offset + 1).
	 */
	public Vector2k set(float[] n, int offset) {
		this.x = n[offset];
		this.y = n[offset + 1];
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
		float angle = Mathk.atan2(y, x) * Mathk.TO_DEGREES;

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

package net.acidfrog.kronos.math;

/**
 * A 3D vector.
 * 
 * @author Ethan Temprovich
 */
public class Vector3 {

    public static final Vector3 UP 		    = 	new Vector3(+-0,  +1, +-0);
    public static final Vector3 FORWARD     = 	new Vector3(+-0, +-0,  +1);
	public static final Vector3 DOWN 	    =   new Vector3(+-0,  -1, +-0);
    public static final Vector3 BACKWARD    =   new Vector3(+-0, +-0,  -1);
	public static final Vector3 LEFT 	    = 	new Vector3( -1, +-0, +-0);
	public static final Vector3 RIGHT       =   new Vector3( +1, +-0, +-0);
	public static final Vector3 ONE 	    = 	new Vector3( +1,  +1,  +1);
	public static final Vector3 ZERO 	    = 	new Vector3(+-0, +-0, +-0);

    public float x;
    public float y;
    public float z;

    public Vector3() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }
    
    public Vector3(float n) {
        this.x = n;
        this.y = n;
        this.z = n;
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = 0f;
    }

    public Vector3(Vector2 v, float z) {
        this.x = v.x;
        this.y = v.y;
        this.z = z;
    }

    public Vector3(float[] n) {
        this.x = n[0];
        this.y = n[1];
        this.z = n[2];
    }

    public Vector3(float[] n, int offset) {
        this.x = n[offset];
        this.y = n[offset + 1];
        this.z = n[offset + 2];
    }
    
    /**
	 * Sets this vectors components to the given values.
	 */
    public Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
	 * Sets this vectors components to the given value.
	 */
    public Vector3 set(float n) {
        this.x = n;
        this.y = n;
        this.z = n;
        return this;
    }

    /**
	 * Sets this vectors components to the given vectors components.
	 */
    public Vector3 set(Vector3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        return this;
    }

    /**
	 * Sets this vectors components to the given arrays first indices value, 
	 * second indices value, and third indicies value.
	 */
    public Vector3 set(float[] n) {
        this.x = n[0];
        this.y = n[1];
        this.z = n[2];
        return this;
    }

    /**
	 * Sets this vectors components to the given arrays value corresponding to the 
     * indice at given offset, indice at offset + 1, and indicie at offset + 2.
	 */
    public Vector3 set(float[] n, int offset) {
        this.x = n[offset];
        this.y = n[offset + 1];
        this.z = n[offset + 2];
        return this;
    }

    /**
	 * Sets the x, y and z values of this vector 0.
	 */
    public void zero() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }

    /**
	 * Adds s to this vector and returns this.
	 */
    public Vector3 addi(float s) {
        return add(s, this);
    }

    /**
	 * Adds v to this vector and returns this.
	 */
    public Vector3 addi(Vector3 v) {
        return add(v, this);
    }

    /**
	 * Returns a new vector that is the sum between this vector and s.
	 */
    public Vector3 add(float s) {
        return add(s, new Vector3());
    }

    /**
	 * Returns a new vector that is the addition of this vector and v.
	 */
    public Vector3 add(Vector3 v) {
        return add(v, new Vector3());
    }

    /**
	 * Sets out to the sum of this vector and s and returns out.
	 */
    public Vector3 add(float s, Vector3 out) {
        out.x = x + s;
        out.y = y + s;
        out.z = z + s;
        return out;
    }

    /**
	 * Sets out to the addition of this vector and v and returns out.
	 */
    public Vector3 add(Vector3 v, Vector3 out) {
        out.x = x + v.x;
        out.y = y + v.y;
        out.z = z + v.z;
        return out;
    }

    /**
	 * Adds v * s to this vector and returns this.
	 */
    public Vector3 addsi(Vector3 v, float s) {
        this.x = x + v.x * s;
        this.y = y + v.y * s;
        this.z = z + v.z * s;
        return this;
    }

    /**
	 * Subtracts v from this vector and returns this.
	 */
    public Vector3 subi(float s) {
        return sub(s, this);
    }

    /**
	 * Subtracts v from this vector and returns this.
	 */
    public Vector3 subi(Vector3 v) {
        return sub(v, this);
    }

    /**
	 * Sets out to the subtraction of v from this vector and returns out.
	 */
    public Vector3 sub(float s, Vector3 out) {
        out.x = x - s;
        out.y = y - s;
        out.z = z - s;
        return out;
    }

    /**
	 * Sets out to the subtraction of v from this vector and returns out.
	 */
    public Vector3 sub(Vector3 v, Vector3 out) {
        out.x = x - v.x;
        out.y = y - v.y;
        out.z = z - v.z;
        return out;
    }

    /**
	 * Returns a new vector that is the subtraction of v from this vector.
	 */
    public Vector3 sub(float s) {
        return sub(s, new Vector3());
    }

    /**
	 * Returns a new vector that is the subtraction of v from this vector.
	 */
    public Vector3 sub(Vector3 v) {
        return sub(v, new Vector3());
    }

    /**
	 * Multiplies this vector by s and returns this.
	 */
    public Vector3 muli(float s) {
        return mul(s, this);
    }

    /**
	 * Multiplies this vector by v and returns this.
	 */
    public Vector3 muli(Vector3 v) {
        return mul(v, this);
    }

    /**
	 * Sets out to this vector multiplied by s and returns out.
	 */
    public Vector3 mul(float s, Vector3 out) {
        out.x = x * s;
        out.y = y * s;
        out.z = z * s;
        return out;
    }

    /**
	 * Sets out to the product of this vector and v and returns out.
	 */
    public Vector3 mul(Vector3 v, Vector3 out) {
        out.x = x * v.x;
        out.y = y * v.y;
        out.z = z * v.z;
        return out;
    }

    /**
	 * Returns a new vector that is a multiplication of this vector and s.
	 */
    public Vector3 mul(float s) {
        return mul(s, new Vector3());
    }

    /**
	 * Returns a new vector that is the product of this vector and v.
	 */
    public Vector3 mul(Vector3 v) {
        return mul(v, new Vector3());
    }

    /**
	 * Divides this vector by s and returns this.
	 */
    public Vector3 divi(float s) {
        return div(s, this);
    }

    /**
	 * Divides this vector by v and returns this.
	 */
    public Vector3 divi(Vector3 v) {
        return div(v, this);
    }

    /**
	 * Sets out to the division of this vector and s and returns out.
	 */
    public Vector3 div(float s, Vector3 out) {
        out.x = x / s;
        out.y = y / s;
        out.z = z / s;
        return out;
    }

    /**
	 * Sets out to the division of this vector and v and returns out.
	 */
    public Vector3 div(Vector3 v, Vector3 out) {
        out.x = x / v.x;
        out.y = y / v.y;
        out.z = z / v.z;
        return out;
    }

    /**
	 * Returns a new vector that is a division between this vector and s.
	 */
    public Vector3 div(float s) {
        return div(s, new Vector3());
    }

    /**
	 * Returns a new vector that is the division of this vector by v.
	 */
    public Vector3 div(Vector3 v) {
        return div(v, new Vector3());
    }

    /**
	 * Returns the dot product between this vector and v.
	 */
    public float dot(Vector3 v) {
        return dot(this, v);
    }

    /*
	 * Returns the dot product between a and b.
	 */
    public static float dot(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
	 * Sets this vector to the cross between a and v and returns this.
	 */
    public Vector3 cross(Vector3 v) {
        return cross(this, v, this);
    }

    // https://www.mathsisfun.com/algebra/vectors-cross-product.html
    
    /**
	 * Sets this vector to the cross between v and a and returns this.
	 */
    public Vector3 cross(Vector3 v, Vector3 out) {
        return cross(this, v, out);
    }

    /**
	 * Returns the cross product of vector a and b.
	 */
    public static Vector3 cross(Vector3 a, Vector3 b, Vector3 out) {
        out.x = a.y * b.z - a.z * b.y;
        out.y = a.z * b.x - a.x * b.z;
        out.z = a.x * b.y - a.y * b.x;
        return out;
    }

    /**
	 * Negates this vector and returns this.
	 */
    public Vector3 negate() {
        return negate(this);
    }

    /**
	 * Sets out to the negation of this vector and returns out.
	 */
    public Vector3 negate(Vector3 out) {
        out.x = -x;
        out.y = -y;
        out.z = -z;
        return out;
    }

    /**
	 * Returns a new vector that is the negation to this vector.
	 */
    public Vector3 negated() {
        return negate(new Vector3());
    }

    /**
	 * Normalizes this vector, making it a unit vector. A unit vector 
	 * has a magnitude of 1.0.
	 */
    public void normalize() {
        float magSq = magnitudeSquared();
        float e = Mathf.FLOAT_ROUNDING_ERROR;

        if (magSq > e * e) {
            float invMag = 1f / Mathf.sqrt(magSq);
            if (invMag == (Float.POSITIVE_INFINITY)) invMag = 0f;
            x *= invMag;
            y *= invMag;
            z *= invMag;
        } else {
            zero();
        }
    }

    /**
	 * Returns a new vector that is the normalized version of this vector.
	 */
    public Vector3 normalized() {
        float magSq = magnitudeSquared();
		Vector3 out = new Vector3(this);
		float e = Mathf.FLOAT_ROUNDING_ERROR;

		if (magSq > e * e) {
			float invMag = 1f / Mathf.sqrt(magSq);
			if (invMag == (Float.POSITIVE_INFINITY)) invMag = 0f;
			out.x *= invMag;
            out.y *= invMag;
            out.z *= invMag;
		} else {
			out.zero();
		}

		return out;
    }

    /**
	 * Returns the squared magnitude of this vector.
	 */
    public float magnitudeSquared() {
        return x * x + y * y + z * z;
    }

    /**
	 * Returns the length of this vector.
	 */
    public float magnitude() {
        return Mathf.sqrt(x * x + y * y + z * z);
    }

    /**
	 * Returns the squared distance between this vector and v.
	 */
    public float distanceSq(Vector3 v) {
        return distanceSq(this, v);
    }

    /**
	 * Returns the distance between this vector and v.
	 */
    public float distance(Vector3 v) {
        return distance(this, v);
    }

    /**
	 * Returns the distance squared between vectors a and b.
	 */
    public static float distanceSq(Vector3 a, Vector3 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float dz = a.z - b.z;

        return dx * dx + dy * dy + dz * dz;
    }

    /**
	 * Returns the distance between vectors a and b.
	 */
    public static float distance(Vector3 a, Vector3 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float dz = a.z - b.z;

        return Mathf.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
	 * Allocates an array of zero vectors of specified length.
	 */
    public static Vector3[] arrayOf(int length) {
		Vector3[] array = new Vector3[length];
		for (int i = 0; i < length; i++) array[i] = new Vector3();
		return array;
	}

    /**
	 * Deternimes if this vectors components are both equal to 0.
	 */
    public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

    public int getIntegerValueX() {
        return (int) x;
    }

    public int getIntegerValueY() {
        return (int) y;
    }

    public int getIntegerValueZ() {
        return (int) z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        result = prime * result + Float.floatToIntBits(z);

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector3)) return false;

        Vector3 other = (Vector3) obj;

        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
        if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z)) return false;
       
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vector3 [x=");
        builder.append(x);
        builder.append(", y=");
        builder.append(y);
        builder.append(", z=");
        builder.append(z);
        builder.append("]");
        return builder.toString();
    }

}

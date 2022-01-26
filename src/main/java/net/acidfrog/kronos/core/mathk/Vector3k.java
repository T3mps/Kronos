package net.acidfrog.kronos.core.mathk;
/**
 * A 3D vector.
 * 
 * @author Ethan Temprovich
 */
public class Vector3k {

    public static final Vector3k UP 		    = 	new Vector3k(+-0,  +1, +-0);
    public static final Vector3k FORWARD     = 	new Vector3k(+-0, +-0,  +1);
	public static final Vector3k DOWN 	    =   new Vector3k(+-0,  -1, +-0);
    public static final Vector3k BACKWARD    =   new Vector3k(+-0, +-0,  -1);
	public static final Vector3k LEFT 	    = 	new Vector3k( -1, +-0, +-0);
	public static final Vector3k RIGHT       =   new Vector3k( +1, +-0, +-0);
	public static final Vector3k ONE 	    = 	new Vector3k( +1,  +1,  +1);
	public static final Vector3k ZERO 	    = 	new Vector3k(+-0, +-0, +-0);

    public float x;
    public float y;
    public float z;

    public Vector3k() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }
    
    public Vector3k(float n) {
        this.x = n;
        this.y = n;
        this.z = n;
    }

    public Vector3k(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3k(Vector3k v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3k(Vector2k v) {
        this.x = v.x;
        this.y = v.y;
        this.z = 0f;
    }

    public Vector3k(Vector2k v, float z) {
        this.x = v.x;
        this.y = v.y;
        this.z = z;
    }

    public Vector3k(float[] n) {
        this.x = n[0];
        this.y = n[1];
        this.z = n[2];
    }

    public Vector3k(float[] n, int offset) {
        this.x = n[offset];
        this.y = n[offset + 1];
        this.z = n[offset + 2];
    }
    
    /**
	 * Sets this vectors components to the given values.
	 */
    public Vector3k set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
	 * Sets this vectors components to the given value.
	 */
    public Vector3k set(float n) {
        this.x = n;
        this.y = n;
        this.z = n;
        return this;
    }

    /**
	 * Sets this vectors components to the given vectors components.
	 */
    public Vector3k set(Vector3k v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        return this;
    }

    /**
	 * Sets this vectors components to the given arrays first indices value, 
	 * second indices value, and third indicies value.
	 */
    public Vector3k set(float[] n) {
        this.x = n[0];
        this.y = n[1];
        this.z = n[2];
        return this;
    }

    /**
	 * Sets this vectors components to the given arrays value corresponding to the 
     * indice at given offset, indice at offset + 1, and indicie at offset + 2.
	 */
    public Vector3k set(float[] n, int offset) {
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
    public Vector3k addi(float s) {
        return add(s, this);
    }

    /**
	 * Adds v to this vector and returns this.
	 */
    public Vector3k addi(Vector3k v) {
        return add(v, this);
    }

    /**
	 * Returns a new vector that is the sum between this vector and s.
	 */
    public Vector3k add(float s) {
        return add(s, new Vector3k());
    }

    /**
	 * Returns a new vector that is the addition of this vector and v.
	 */
    public Vector3k add(Vector3k v) {
        return add(v, new Vector3k());
    }

    /**
	 * Sets out to the sum of this vector and s and returns out.
	 */
    public Vector3k add(float s, Vector3k out) {
        out.x = x + s;
        out.y = y + s;
        out.z = z + s;
        return out;
    }

    /**
	 * Sets out to the addition of this vector and v and returns out.
	 */
    public Vector3k add(Vector3k v, Vector3k out) {
        out.x = x + v.x;
        out.y = y + v.y;
        out.z = z + v.z;
        return out;
    }

    /**
	 * Adds v * s to this vector and returns this.
	 */
    public Vector3k addsi(Vector3k v, float s) {
        this.x = x + v.x * s;
        this.y = y + v.y * s;
        this.z = z + v.z * s;
        return this;
    }

    /**
	 * Subtracts v from this vector and returns this.
	 */
    public Vector3k subi(float s) {
        return sub(s, this);
    }

    /**
	 * Subtracts v from this vector and returns this.
	 */
    public Vector3k subi(Vector3k v) {
        return sub(v, this);
    }

    /**
	 * Sets out to the subtraction of v from this vector and returns out.
	 */
    public Vector3k sub(float s, Vector3k out) {
        out.x = x - s;
        out.y = y - s;
        out.z = z - s;
        return out;
    }

    /**
	 * Sets out to the subtraction of v from this vector and returns out.
	 */
    public Vector3k sub(Vector3k v, Vector3k out) {
        out.x = x - v.x;
        out.y = y - v.y;
        out.z = z - v.z;
        return out;
    }

    /**
	 * Returns a new vector that is the subtraction of v from this vector.
	 */
    public Vector3k sub(float s) {
        return sub(s, new Vector3k());
    }

    /**
	 * Returns a new vector that is the subtraction of v from this vector.
	 */
    public Vector3k sub(Vector3k v) {
        return sub(v, new Vector3k());
    }

    /**
	 * Multiplies this vector by s and returns this.
	 */
    public Vector3k muli(float s) {
        return mul(s, this);
    }

    /**
	 * Multiplies this vector by v and returns this.
	 */
    public Vector3k muli(Vector3k v) {
        return mul(v, this);
    }

    /**
	 * Sets out to this vector multiplied by s and returns out.
	 */
    public Vector3k mul(float s, Vector3k out) {
        out.x = x * s;
        out.y = y * s;
        out.z = z * s;
        return out;
    }

    /**
	 * Sets out to the product of this vector and v and returns out.
	 */
    public Vector3k mul(Vector3k v, Vector3k out) {
        out.x = x * v.x;
        out.y = y * v.y;
        out.z = z * v.z;
        return out;
    }

    /**
	 * Returns a new vector that is a multiplication of this vector and s.
	 */
    public Vector3k mul(float s) {
        return mul(s, new Vector3k());
    }

    /**
	 * Returns a new vector that is the product of this vector and v.
	 */
    public Vector3k mul(Vector3k v) {
        return mul(v, new Vector3k());
    }

    /**
	 * Divides this vector by s and returns this.
	 */
    public Vector3k divi(float s) {
        return div(s, this);
    }

    /**
	 * Divides this vector by v and returns this.
	 */
    public Vector3k divi(Vector3k v) {
        return div(v, this);
    }

    /**
	 * Sets out to the division of this vector and s and returns out.
	 */
    public Vector3k div(float s, Vector3k out) {
        out.x = x / s;
        out.y = y / s;
        out.z = z / s;
        return out;
    }

    /**
	 * Sets out to the division of this vector and v and returns out.
	 */
    public Vector3k div(Vector3k v, Vector3k out) {
        out.x = x / v.x;
        out.y = y / v.y;
        out.z = z / v.z;
        return out;
    }

    /**
	 * Returns a new vector that is a division between this vector and s.
	 */
    public Vector3k div(float s) {
        return div(s, new Vector3k());
    }

    /**
	 * Returns a new vector that is the division of this vector by v.
	 */
    public Vector3k div(Vector3k v) {
        return div(v, new Vector3k());
    }

    /**
	 * Returns the dot product between this vector and v.
	 */
    public float dot(Vector3k v) {
        return dot(this, v);
    }

    /*
	 * Returns the dot product between a and b.
	 */
    public static float dot(Vector3k a, Vector3k b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
	 * Sets this vector to the cross between a and v and returns this.
	 */
    public Vector3k cross(Vector3k v) {
        return cross(this, v, this);
    }

    // https://www.mathsisfun.com/algebra/vectors-cross-product.html
    
    /**
	 * Sets this vector to the cross between v and a and returns this.
	 */
    public Vector3k cross(Vector3k v, Vector3k out) {
        return cross(this, v, out);
    }

    /**
	 * Returns the cross product of vector a and b.
	 */
    public static Vector3k cross(Vector3k a, Vector3k b, Vector3k out) {
        out.x = a.y * b.z - a.z * b.y;
        out.y = a.z * b.x - a.x * b.z;
        out.z = a.x * b.y - a.y * b.x;
        return out;
    }

    /**
	 * Negates this vector and returns this.
	 */
    public Vector3k negate() {
        return negate(this);
    }

    /**
	 * Sets out to the negation of this vector and returns out.
	 */
    public Vector3k negate(Vector3k out) {
        out.x = -x;
        out.y = -y;
        out.z = -z;
        return out;
    }

    /**
	 * Returns a new vector that is the negation to this vector.
	 */
    public Vector3k negated() {
        return negate(new Vector3k());
    }

    /**
	 * Normalizes this vector, making it a unit vector. A unit vector 
	 * has a magnitude of 1.0.
	 */
    public void normalize() {
        float magSq = magnitudeSquared();
        float e = Mathk.FLOAT_ROUNDING_ERROR;

        if (magSq > e * e) {
            float invMag = 1f / Mathk.sqrt(magSq);
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
    public Vector3k normalized() {
        float magSq = magnitudeSquared();
		Vector3k out = new Vector3k(this);
		float e = Mathk.FLOAT_ROUNDING_ERROR;

		if (magSq > e * e) {
			float invMag = 1f / Mathk.sqrt(magSq);
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
        return Mathk.sqrt(x * x + y * y + z * z);
    }

    /**
	 * Returns the squared distance between this vector and v.
	 */
    public float distanceSq(Vector3k v) {
        return distanceSq(this, v);
    }

    /**
	 * Returns the distance between this vector and v.
	 */
    public float distance(Vector3k v) {
        return distance(this, v);
    }

    /**
	 * Returns the distance squared between vectors a and b.
	 */
    public static float distanceSq(Vector3k a, Vector3k b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float dz = a.z - b.z;

        return dx * dx + dy * dy + dz * dz;
    }

    /**
	 * Returns the distance between vectors a and b.
	 */
    public static float distance(Vector3k a, Vector3k b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float dz = a.z - b.z;

        return Mathk.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
	 * Allocates an array of zero vectors of specified length.
	 */
    public static Vector3k[] arrayOf(int length) {
		Vector3k[] array = new Vector3k[length];
		for (int i = 0; i < length; i++) array[i] = new Vector3k();
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
        if (!(obj instanceof Vector3k)) return false;

        Vector3k other = (Vector3k) obj;

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

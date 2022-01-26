package net.acidfrog.kronos.core.mathk;

import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.error.KronosGeometryError;

/**
 * Defines a <code>2x2</code> matrix alongside associated functions to transform it. The matrix
 * is column-major to match OpenGL's interpretation:
 * <p>
 *      m00  m10<br></br>
 *      m01  m11<br></br>
 * </p>
 * 
 * @author Ethan Temprovich
 */
public class Matrix2k {

	public float m00, m01;
	public float m10, m11;

	public Matrix2k() {
		set(1, 0,
			0, 1);
	}

	public Matrix2k(float radians) {
		set(radians);
	}

	public Matrix2k(float m00, float m01, float m10, float m11) {
		set(m00, m01,
			m10, m11);
	}

	public Matrix2k(Matrix2k mat2) {
		set(mat2);
	}

	public Matrix2k(float[] mat2) {
		set(mat2);
	}

	public Matrix2k(float[] mat2, int offset) {
		set(mat2, offset);
	}

	/**
	 * Sets this matrix to a rotation matrix with the given radians.
	 */
	public void set(float radians) {
		float c = Mathk.cos(radians);
		float s = Mathk.sin(radians);

		m00 = c; m01 = -s;
		m10 = s; m11 =  c;
	}

	/**
	 * Sets the values of this matrix.
	 */
	public void set(float a, float b, float c, float d) {
		m00 = a; m01 = b;
		m10 = c; m11 = d;
	}

	/**
	 * Sets this matrix to have the same values as the given matrix.
	 */
	public void set(Matrix2k m) {
		m00 = m.m00; m01 = m.m01;
		m10 = m.m10; m11 = m.m11;
	}

	/**
	 * Sets this matrix to have the same values as the given array.
	 */
	public void set(float[] m) {
		if(m.length != 4) throw new KronosGeometryError(KronosErrorLibrary.MATRIX_NOT_2X2);
		set(m, 0);
	}

	public void set(float[] m, int offset) {
		if(offset + 3 >= m.length) throw new KronosGeometryError(KronosErrorLibrary.INVALID_OFFSET);

		m00 = m[offset + 0]; m01 = m[offset + 1];
		m10 = m[offset + 2]; m11 = m[offset + 3];
	}

	// "Cross product" 
	public float determinant() {
		return m00 * m11 - m10 * m01;
	}

	public Matrix2k identity() {
		set(1, 0,
			0, 1);
		return this;
	}

	/**
	 * Inverts this matrix and returns this.
	 */
	public Matrix2k invert() {
		return invert(this);
	}

	/**
	 * Sets out to be the inverse of this matrix and returns out.
	 */
	public Matrix2k invert(Matrix2k out) {
		float det = determinant();
		if (det == 0f) throw new KronosGeometryError(KronosErrorLibrary.ZERO_MATRIX_TRANSFORM_ATTEMPT);
		float invDet = 1f / det;
		out.m00 =  m11 * invDet;
		out.m01 = -m01 * invDet;
		out.m10 = -m10 * invDet;
		out.m11 =  m00 * invDet;
		return out;
	}

	/**
	 * Zeros this matrix.
	 */
	public Matrix2k zero() {
		set(0, 0, 0, 0);
		return this;
	}

	/**
	 * Performs component-wise addition of matrix m and this and return this.
	 */
	public Matrix2k addi(Matrix2k m) {
		return add(m, this);
	}

	/**
	 * Performs component-wise addition of matrix m and this and returns the result.
	 */
	public Matrix2k add(Matrix2k m) {
		return add(m, new Matrix2k());
	}

	/**
	 * Component-wise addition of m and this, return out.
	 */
	public Matrix2k add(Matrix2k m, Matrix2k out) {
		out.m00 = m00 + m.m00;
		out.m01 = m01 + m.m01;
		out.m10 = m10 + m.m10;
		out.m11 = m11 + m.m11;
		return out;
	}

	/**
	 * Perforns component-wise subtraction of matrix m and this and return this.
	 */
	public Matrix2k subi(Matrix2k m) {
		return sub(m, this);
	}

	/**
	 * Performs component-wise subtraction of matrix m and this and returns the result.
	 */
	public Matrix2k sub(Matrix2k m) {
		return sub(m, new Matrix2k());
	}

	/**
	 * Component-wise subtraction of m and this, return out.
	 */
	public Matrix2k sub(Matrix2k m, Matrix2k out) {
		out.m00 = m00 - m.m00;
		out.m01 = m01 - m.m01;
		out.m10 = m10 - m.m10;
		out.m11 = m11 - m.m11;
		return out;
	}

	/**
	 * Transforms v by this matrix.
	 */
	public Vector2k muli(Vector2k v) {
		return mul(v.x, v.y, v);
	}

	/**
	 * Sets out to the transformation of v by this matrix.
	 */
	public Vector2k mul(Vector2k v, Vector2k out) {
		return mul(v.x, v.y, out);
	}

	/**
	 * Returns a new vector that is the transformation of v by this matrix.
	 */
	public Vector2k mul( Vector2k v ) {
		return mul(v.x, v.y, new Vector2k());
	}

	/**
	 * Sets out the to transformation of <x,y> by this matrix.
	 */
	public Vector2k mul(float x, float y, Vector2k out) {
		out.x = m00 * x + m01 * y;
		out.y = m10 * x + m11 * y;
		return out;
	}

	/**
	 * Multiplies this matrix by x.
	 */
	public void muli(Matrix2k x) {
		set(m00 * x.m00 +
			m01 * x.m10,
			m00 * x.m01 +
			m01 * x.m11,
			m10 * x.m00 +
			m11 * x.m10,
			m10 * x.m01 +
			m11 * x.m11);
	}

	/**
	 * Sets out to the multiplication of this matrix and x.
	 */
	public Matrix2k mul(Matrix2k x, Matrix2k out) {
		out.m00 = m00 * x.m00 + m01 * x.m10;
		out.m01 = m00 * x.m01 + m01 * x.m11;
		out.m10 = m10 * x.m00 + m11 * x.m10;
		out.m11 = m10 * x.m01 + m11 * x.m11;
		return out;
	}

	/**
	 * Returns a new matrix that is the multiplication of this and x.
	 */
	public Matrix2k mul(Matrix2k x) {
		return mul(x, new Matrix2k());
	}
	
	/**
	 * Rotates this matrix around the origin by the given radians.
	 */
	public Matrix2k rotate(float radians) {
		float sin = Mathk.sin(radians);
		float cos = Mathk.cos(radians);

		float m00p = m00 * cos + m10 * sin;
		float m01p = m01 * cos + m11 * sin;
		float m10p = m10 * cos - m00 * sin;
		float m11p = m11 * cos - m01 * sin;

		m00 = m00p;
		m01 = m01p;
		m10 = m10p;
		m11 = m11p;

		return this;
	}

	/**
	 * Sets the matrix to it's transpose.
	 */
	public void transpose() {
		float t = m01;
		m01 = m10;
		m10 = t;
	}

	/**
	 * Sets out to the transpose of this matrix.
	 */
	public Matrix2k transpose(Matrix2k out) {
		out.m00 = m00;
		out.m01 = m10;
		out.m10 = m01;
		out.m11 = m11;
		return out;
	}

	/**
	 * Returns a new matrix that is the transpose of this matrix.
	 */
	public Matrix2k transposed() {
		return transpose(new Matrix2k());
	}

	/**
	 * Returns a new float array containing the values of this matrix.
	 */
	public float[] toArray() {
		return new float[] { m00, m01, m10, m11 };
	}

	/**
	 * Sets the values of this matrix from the given float array.
	 */
	public Matrix2k fromArray(float[] array) {
		if (array.length != 4) throw new IllegalArgumentException("array must be of length 9");

		m00 = array[0];
		m01 = array[1];
		m10 = array[2];
		m11 = array[3];
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(m00);
		result = prime * result + Float.floatToIntBits(m01);
		result = prime * result + Float.floatToIntBits(m10);
		result = prime * result + Float.floatToIntBits(m11);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Matrix2k)) return false;

		Matrix2k other = (Matrix2k) obj;
		if (Float.floatToIntBits(m00) != Float.floatToIntBits(other.m00)) return false;
		if (Float.floatToIntBits(m01) != Float.floatToIntBits(other.m01)) return false;
		if (Float.floatToIntBits(m10) != Float.floatToIntBits(other.m10)) return false;
		if (Float.floatToIntBits(m11) != Float.floatToIntBits(other.m11)) return false;
		
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Matrix2 [m00=");
		builder.append(m00);
		builder.append(", m01=");
		builder.append(m01);
		builder.append(", m10=");
		builder.append(m10);
		builder.append(", m11=");
		builder.append(m11);
		builder.append("]");
		return builder.toString();
	}

}

package net.acidfrog.kronos.math;

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
public class Matrix2 {

	public float m00, m01;
	public float m10, m11;

	public Matrix2() {
		set(1, 0,
			0, 1);
	}

	public Matrix2(float radians) {
		set(radians);
	}

	public Matrix2(float m00, float m01, float m10, float m11) {
		set(m00, m01,
			m10, m11);
	}

	public Matrix2(Matrix2 mat2) {
		set(mat2);
	}

	public Matrix2(Matrix3 mat3) {
		set(mat3, 0);
	}

	public Matrix2(Matrix3 mat3, int offset) {
		set(mat3, offset);
	}

	public Matrix2(float[] mat2) {
		set(mat2);
	}

	public Matrix2(float[] mat2, int offset) {
		set(mat2, offset);
	}

	/**
	 * Sets this matrix to a rotation matrix with the given radians.
	 */
	public void set(float radians) {
		float c = Mathf.cos(radians);
		float s = Mathf.sin(radians);

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
	public void set(Matrix2 m) {
		m00 = m.m00; m01 = m.m01;
		m10 = m.m10; m11 = m.m11;
	}

	/**
	 * Sets this matrix to have the same values as the given matrix.
	 */
	public void set(Matrix3 m) {
		set(m, 0);
	}

	/**
	 * Sets this matrix to have the same values as the given matrix.
	 */
	public void set(Matrix3 m, int offset) {
		float[] mat = m.toArray();
		if (offset > mat.length) throw new KronosGeometryError(KronosErrorLibrary.OFFSET_OUT_OF_BOUNDS);
		m00 = mat[offset + 0]; m01 = mat[offset + 1];
		m10 = mat[offset + 2]; m11 = mat[offset + 3];
	}

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

	public Matrix2 identity() {
		set(1, 0,
			0, 1);
		return this;
	}

	/**
	 * Inverts this matrix and returns this.
	 */
	public Matrix2 invert() {
		return invert(this);
	}

	/**
	 * Sets out to be the inverse of this matrix and returns out.
	 */
	public Matrix2 invert(Matrix2 out) {
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
	public Matrix2 zero() {
		set(0, 0, 0, 0);
		return this;
	}

	/**
	 * Performs component-wise addition of matrix m and this and return this.
	 */
	public Matrix2 addi(Matrix2 m) {
		return add(m, this);
	}

	/**
	 * Performs component-wise addition of matrix m and this and returns the result.
	 */
	public Matrix2 add(Matrix2 m) {
		return add(m, new Matrix2());
	}

	/**
	 * Component-wise addition of m and this, return out.
	 */
	public Matrix2 add(Matrix2 m, Matrix2 out) {
		out.m00 = m00 + m.m00;
		out.m01 = m01 + m.m01;
		out.m10 = m10 + m.m10;
		out.m11 = m11 + m.m11;
		return out;
	}

	/**
	 * Perforns component-wise subtraction of matrix m and this and return this.
	 */
	public Matrix2 subi(Matrix2 m) {
		return sub(m, this);
	}

	/**
	 * Performs component-wise subtraction of matrix m and this and returns the result.
	 */
	public Matrix2 sub(Matrix2 m) {
		return sub(m, new Matrix2());
	}

	/**
	 * Component-wise subtraction of m and this, return out.
	 */
	public Matrix2 sub(Matrix2 m, Matrix2 out) {
		out.m00 = m00 - m.m00;
		out.m01 = m01 - m.m01;
		out.m10 = m10 - m.m10;
		out.m11 = m11 - m.m11;
		return out;
	}

	/**
	 * Transforms v by this matrix.
	 */
	public Vector2 muli(Vector2 v) {
		return mul(v.x, v.y, v);
	}

	/**
	 * Sets out to the transformation of v by this matrix.
	 */
	public Vector2 mul(Vector2 v, Vector2 out) {
		return mul(v.x, v.y, out);
	}

	/**
	 * Returns a new vector that is the transformation of v by this matrix.
	 */
	public Vector2 mul( Vector2 v ) {
		return mul(v.x, v.y, new Vector2());
	}

	/**
	 * Sets out the to transformation of <x,y> by this matrix.
	 */
	public Vector2 mul(float x, float y, Vector2 out) {
		out.x = m00 * x + m01 * y;
		out.y = m10 * x + m11 * y;
		return out;
	}

	/**
	 * Multiplies this matrix by x.
	 */
	public void muli(Matrix2 x) {
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
	public Matrix2 mul(Matrix2 x, Matrix2 out) {
		out.m00 = m00 * x.m00 + m01 * x.m10;
		out.m01 = m00 * x.m01 + m01 * x.m11;
		out.m10 = m10 * x.m00 + m11 * x.m10;
		out.m11 = m10 * x.m01 + m11 * x.m11;
		return out;
	}

	/**
	 * Returns a new matrix that is the multiplication of this and x.
	 */
	public Matrix2 mul(Matrix2 x) {
		return mul(x, new Matrix2());
	}
	
	/**
	 * Rotates this matrix around the origin by the given radians.
	 */
	public Matrix2 rotate(float radians) {
		float sin = Mathf.sin(radians);
		float cos = Mathf.cos(radians);

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
	public Matrix2 transpose(Matrix2 out) {
		out.m00 = m00;
		out.m01 = m10;
		out.m10 = m01;
		out.m11 = m11;
		return out;
	}

	/**
	 * Returns a new matrix that is the transpose of this matrix.
	 */
	public Matrix2 transposed() {
		return transpose(new Matrix2());
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
	public Matrix2 fromArray(float[] array) {
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
		if (!(obj instanceof Matrix2)) return false;

		Matrix2 other = (Matrix2) obj;
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

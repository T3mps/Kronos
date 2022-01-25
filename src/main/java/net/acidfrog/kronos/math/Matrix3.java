package net.acidfrog.kronos.math;

/**
 * Defines a <code>3x3</code> matrix alongside associated functions to transform it. The matrix
 * is column-major to match OpenGL's interpretation:
 * <p>
 *      m00  m10 m20<br></br>
 *      m01  m11 m21<br></br>
 *      m02  m12 m22<br></br>
 * </p>
 * 
 * @author Ethan Temprovich
 */
public class Matrix3 {

    public float m00, m01, m02;
    public float m10, m11, m12;
    public float m20, m21, m22;

    public Matrix3() {
        set(1, 0, 0,
            0, 1, 0,
            0, 0, 1);
    }

    public Matrix3(float m00, float m01, float m02,
                   float m10, float m11, float m12,
                   float m20, float m21, float m22) {
        set(m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22);
    }

     public Matrix3(Matrix2 mat2) {
        set(mat2);
    }

    public Matrix3(Matrix3 mat3) {
        set(mat3);
    }

    public Matrix3(Quaternion quat) {
        set(quat);
    }
    
    public Matrix3(float[] mat3) {
        set(mat3);
    }

    public Matrix3(float[] mat3, int offset) {
        set(mat3, offset);
    }

    /**
     * Sets the values of this matrix.
     */
    public Matrix3 set(float a, float b, float c,
                       float d, float e, float f,
                       float g, float h, float i) {
        m00 = a; m01 = b; m02 = c;
        m10 = d; m11 = e; m12 = f;
        m20 = g; m21 = h; m22 = i;
        return this;
    }

    /**
     * Sets this matrix to have the same values as the given matrix.
     */
    public Matrix3 set(Matrix2 m) {
        m00 = m.m00; m01 = m.m01; m02 = 0f;
        m10 = m.m10; m11 = m.m11; m12 = 0f;
        m20 =    0f; m21 =    0f; m22 = 0f;
        return this;
    }

    /**
     * Sets this matrix to have the same values as the given matrix.
     */
    public Matrix3 set(Matrix3 m) {
        m00 = m.m00; m01 = m.m01; m02 = m.m02;
        m10 = m.m10; m11 = m.m11; m12 = m.m12;
        m20 = m.m20; m21 = m.m21; m22 = m.m22;
        return this;
    }

    public Matrix3 set(Quaternion q) {
        return rotate(q);
    }

    public void set(float[] m) {
        set(m, 0);
    }

    public void set(float[] m, int offset) {
        m00 = m[offset + 0]; m01 = m[offset + 1]; m02 = m[offset + 2];
        m10 = m[offset + 3]; m11 = m[offset + 4]; m12 = m[offset + 5];
        m20 = m[offset + 6]; m21 = m[offset + 7]; m22 = m[offset + 8];
    }

    public float determinant() {
		return this.m00 * this.m11 * this.m22 +
		       this.m01 * this.m12 * this.m20 +
		       this.m02 * this.m10 * this.m21 -
		       this.m20 * this.m11 * this.m02 -
		       this.m21 * this.m12 * this.m00 -
		       this.m22 * this.m10 * this.m01;
	}

    public Matrix3 identity() {
		this.m00 = 1; this.m01 = 0; this.m02 = 0;
		this.m10 = 0; this.m11 = 1; this.m12 = 0;
		this.m20 = 0; this.m21 = 0; this.m22 = 1;
		return this;
	}

    /**
	 * Inverts this matrix and returns this.
	 */
    public Matrix3 invert() {
        return invert(this);
    }

    /**
	 * Sets out to be the inverse of this matrix and returns out.
	 */
    public Matrix3 invert(Matrix3 out) {
        float invDet = determinant();

        if(invDet > Mathf.FLOAT_ROUNDING_ERROR) invDet = 1f / invDet;

        float t00 = out.m11 * out.m22 - out.m12 * out.m21;
        float t01 = out.m02 * out.m21 - out.m01 * out.m22;
        float t02 = out.m01 * out.m12 - out.m02 * out.m11;
        float t10 = out.m12 * out.m20 - out.m10 * out.m22;
        float t11 = out.m00 * out.m22 - out.m02 * out.m20;
        float t12 = out.m02 * out.m10 - out.m00 * out.m12;
        float t20 = out.m10 * out.m21 - out.m11 * out.m20;
        float t21 = out.m01 * out.m20 - out.m00 * out.m21;
        float t22 = out.m00 * out.m11 - out.m01 * out.m10;

        out.m00 = t00 * invDet;
        out.m01 = t01 * invDet;
        out.m02 = t02 * invDet;
        out.m10 = t10 * invDet;
        out.m11 = t11 * invDet;
        out.m12 = t12 * invDet;
        out.m20 = t20 * invDet;
        out.m21 = t21 * invDet;
        out.m22 = t22 * invDet;

        return out;
    }

    /**
	 * Zeros this matrix.
	 */
    public Matrix3 zero() {
        set(0, 0, 0,
            0, 0, 0,
            0, 0, 0);
        return this;
    }

    /**
	 * Performs component-wise addition of matrix m and this and return this.
	 */
    public Matrix3 addi(Matrix3 m) {
        return add(m, this);
    }

    /**
	 * Component-wise addition of m and this and return the result
	 */
    public Matrix3 add(Matrix3 m) {
        return add(m, new Matrix3());
    }

    /**
	 * Component-wise addition of m and this, return out.
	 */
    public Matrix3 add(Matrix3 m, Matrix3 out) {
        out.m00 = m00 + m.m00; out.m01 = m01 + m.m01; out.m02 = m02 + m.m02;
        out.m10 = m10 + m.m10; out.m11 = m11 + m.m11; out.m12 = m12 + m.m12;
        out.m20 = m20 + m.m20; out.m21 = m21 + m.m21; out.m22 = m22 + m.m22;
        return out;
    }

    /**
     * Performs component-wise subtraction of matrix m and this and return this.
     */
    public Matrix3 subi(Matrix3 m) {
        return sub(m, this);
    }

    /**
     * Component-wise subtraction of m and this and return the result
     */
    public Matrix3 sub(Matrix3 m) {
        return sub(m, new Matrix3());
    }

    /**
     * Component-wise subtraction of m and this, return out.
     */
    public Matrix3 sub(Matrix3 m, Matrix3 out) {
        out.m00 = m00 - m.m00; out.m01 = m01 - m.m01; out.m02 = m02 - m.m02;
        out.m10 = m10 - m.m10; out.m11 = m11 - m.m11; out.m12 = m12 - m.m12;
        out.m20 = m20 - m.m20; out.m21 = m21 - m.m21; out.m22 = m22 - m.m22;
        return out;
    }

    /**
     * Transforms v by this matrix.
     */
    public Vector3 muli(Vector3 v) {
        return mul(v.x, v.y, v.z, v);
    }

    /**
	 * Sets out to the transformation of v by this matrix.
	 */
    public Vector3 mul(Vector3 v, Vector3 out) {
        return mul(v.x, v.y, v.z, out);
    }

    /**
	 * Returns a new vector that is the transformation of v by this matrix.
	 */
    public Vector3 mul(Vector3 v) {
        return mul(v.x, v.y, v.z, new Vector3());
    }

    /**
	 * Sets out the to transformation of <x, y, z> by this matrix.
	 */
    public Vector3 mul(float x, float y, float z, Vector3 out) {
        out.x = m00 * x + m01 * y + m02 * z;
        out.y = m10 * x + m11 * y + m12 * z;
        out.z = m20 * x + m21 * y + m22 * z;
        return out;
    }

    /**
	 * Multiplies this matrix by x.
	 */
    public void muli(Matrix3 x) {
        set(m00 * x.m00 + m01 * x.m10 + m02 * x.m20,
            m00 * x.m01 + m01 * x.m11 + m02 * x.m21,
            m00 * x.m02 + m01 * x.m12 + m02 * x.m22,
            m10 * x.m00 + m11 * x.m10 + m12 * x.m20,
            m10 * x.m01 + m11 * x.m11 + m12 * x.m21,
            m10 * x.m02 + m11 * x.m12 + m12 * x.m22,
            m20 * x.m00 + m21 * x.m10 + m22 * x.m20,
            m20 * x.m01 + m21 * x.m11 + m22 * x.m21,
            m20 * x.m02 + m21 * x.m12 + m22 * x.m22);
    }

    /**
	 * Sets out to the multiplication of this matrix and x.
	 */
    public Matrix3 mul(Matrix3 x, Matrix3 out) {
        out.set(m00 * x.m00 + m01 * x.m10 + m02 * x.m20,
            m00 * x.m01 + m01 * x.m11 + m02 * x.m21,
            m00 * x.m02 + m01 * x.m12 + m02 * x.m22,
            m10 * x.m00 + m11 * x.m10 + m12 * x.m20,
            m10 * x.m01 + m11 * x.m11 + m12 * x.m21,
            m10 * x.m02 + m11 * x.m12 + m12 * x.m22,
            m20 * x.m00 + m21 * x.m10 + m22 * x.m20,
            m20 * x.m01 + m21 * x.m11 + m22 * x.m21,
            m20 * x.m02 + m21 * x.m12 + m22 * x.m22);
        return out;
    }

    /**
	 * Returns a new matrix that is the multiplication of this and x.
	 */
    public Matrix3 mul(Matrix3 x) {
        return mul(x, new Matrix3());
    }

    public Matrix3 rotate(float radians, Vector3 axis) {
        return rotate(radians, axis.x, axis.y, axis.z);
    }

    // Reference: <a href="http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle">http://en.wikipedia.org</a>
    public Matrix3 rotate(float radians, float x, float y, float z) {
        float sin = Mathf.sin(radians);
        float cos = Mathf.cos(radians);
        float c = 1f - cos;
        float xy = x * y, xz = x * z, yz = y * z;
        
        m00 = cos + x * x * c;
        m10 = xy  * c - z * sin;
        m20 = xz  * c + y * sin;
        m01 = xy  * c + z * sin;
        m11 = cos + y * y * c;
        m21 = yz  * c - x * sin;
        m02 = xz  * c - y * sin;
        m12 = yz  * c + x * sin;
        m22 = cos + z * z * c;

        return this;
    }

    /**
     * Rotates this matrix by a given Quaternion.
     * @param quat
     * @return
     */
    public Matrix3 rotate(Quaternion quat) {
        float w2 = quat.w * quat.w;
        float x2 = quat.x * quat.x;
        float y2 = quat.y * quat.y;
        float z2 = quat.z * quat.z;
        float zw = quat.z * quat.w, dzw = zw + zw;
        float xy = quat.x * quat.y, dxy = xy + xy;
        float xz = quat.x * quat.z, dxz = xz + xz;
        float yw = quat.y * quat.w, dyw = yw + yw;
        float yz = quat.y * quat.z, dyz = yz + yz;
        float xw = quat.x * quat.w, dxw = xw + xw;
        m00 = w2 + x2 - z2 - y2;
        m01 = dxy + dzw;
        m02 = dxz - dyw;
        m10 = -dzw + dxy;
        m11 = y2 - z2 + w2 - x2;
        m12 = dyz + dxw;
        m20 = dyw + dxz;
        m21 = dyz - dxw;
        m22 = z2 - y2 - x2 + w2;
        return this;
    }

    /**
	 * Sets the matrix to it's transpose.
	 */
    public Matrix3 transpose() {
        return transpose(this);
    }

    /**
	 * Sets out to the transpose of this matrix.
	 */
    public Matrix3 transpose(Matrix3 out) {
        out.set(m00, m10, m20,
            m01, m11, m21,
            m02, m12, m22);
        return out;
    }

    /**
	 * Returns a new matrix that is the transpose of this matrix.
	 */
    public Matrix3 transposed() {
        return transpose(new Matrix3());
    }

    public Vector3 getRow(int row) throws IndexOutOfBoundsException {
        switch (row) {
            case 0: return new Vector3(m00, m10, m20);
            case 1: return new Vector3(m01, m11, m21);
            case 2: return new Vector3(m02, m12, m22);
            default: throw new IndexOutOfBoundsException(row + " is out of bounds for " + this.toString());
        }
    }

    public Matrix3 setRow(int index, Vector3 row) {
        return setRow(index, row.x, row.y, row.z);
    }

    public Matrix3 setRow(int row, float x, float y, float z) {
        switch (row) {
            case 0:
                m00 = x;
                m10 = y;
                m20 = z;
                break;
            case 1:
                m01 = x;
                m11 = y;
                m21 = z;
                break;
            case 2:
                m02 = x;
                m12 = y;
                m22 = z;
                break;
            default:
                throw new IndexOutOfBoundsException(row + " is out of bounds for " + this.toString());
        }
        return this;
    }

    public Vector3 getColumn(int column, Vector3 out) throws IndexOutOfBoundsException {
        switch (column) {
            case 0:
                out.set(m00, m01, m02);
                break;
            case 1:
                out.set(m10, m11, m12);
                break;
            case 2:
                out.set(m20, m21, m22);
                break;
            default:
                throw new IndexOutOfBoundsException(column + " is out of bounds for " + this.toString());
        }
        return out;
    }

    public Matrix3 setColumn(int index, Vector3 column) {
        return setColumn(index, column.x, column.y, column.z);
    }

    public Matrix3 setColumn(int column, float x, float y, float z) {
        switch (column) {
            case 0:
                m00 = x;
                m01 = y;
                m02 = z;
                break;
            case 1:
                m10 = x;
                m11 = y;
                m12 = z;
                break;
            case 2:
                m20 = x;
                m21 = y;
                m22 = z;
                break;
            default:
                throw new IndexOutOfBoundsException(column + " is out of bounds for " + this.toString());
        }
        return this;
    }

    // https://github.com/JOML-CI/JOML/blob/main/src/org/joml/Matrix3f.java
    public Vector3 getEulerAngles(Vector3 out) {
        out.x = Mathf.atan2(m12, m22);
        out.y = Mathf.atan2(-m02, Mathf.sqrt(1f - m02 * m02));
        out.z = Mathf.atan2(m01, m00);
        return out;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(m00);
        result = prime * result + Float.floatToIntBits(m01);
        result = prime * result + Float.floatToIntBits(m02);
        result = prime * result + Float.floatToIntBits(m10);
        result = prime * result + Float.floatToIntBits(m11);
        result = prime * result + Float.floatToIntBits(m12);
        result = prime * result + Float.floatToIntBits(m20);
        result = prime * result + Float.floatToIntBits(m21);
        result = prime * result + Float.floatToIntBits(m22);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Matrix3)) return false;

        Matrix3 other = (Matrix3) obj;
        if (Float.floatToIntBits(m00) != Float.floatToIntBits(other.m00)) return false;
        if (Float.floatToIntBits(m01) != Float.floatToIntBits(other.m01)) return false;
        if (Float.floatToIntBits(m02) != Float.floatToIntBits(other.m02)) return false;
        if (Float.floatToIntBits(m10) != Float.floatToIntBits(other.m10)) return false;
        if (Float.floatToIntBits(m11) != Float.floatToIntBits(other.m11)) return false;
        if (Float.floatToIntBits(m12) != Float.floatToIntBits(other.m12)) return false;
        if (Float.floatToIntBits(m20) != Float.floatToIntBits(other.m20)) return false;
        if (Float.floatToIntBits(m21) != Float.floatToIntBits(other.m21)) return false;
        if (Float.floatToIntBits(m22) != Float.floatToIntBits(other.m22)) return false;
        
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Matrix3 [m00=");
        builder.append(m00);
        builder.append(", m01=");
        builder.append(m01);
        builder.append(", m02=");
        builder.append(m02);
        builder.append(", m10=");
        builder.append(m10);
        builder.append(", m11=");
        builder.append(m11);
        builder.append(", m12=");
        builder.append(m12);
        builder.append(", m20=");
        builder.append(m20);
        builder.append(", m21=");
        builder.append(m21);
        builder.append(", m22=");
        builder.append(m22);
        builder.append("]");
        return builder.toString();
    }

    public float[] toArray() {
        return new float[] {m00, m01, m02,
                            m10, m11, m12,
                            m20, m21, m22};
    }

    public Matrix3 fromArray(float[] array) {
        if (array.length != 9) throw new IllegalArgumentException("array must be of length 9");
        
        m00 = array[0]; m01 = array[1]; m02 = array[2];
        m10 = array[3]; m11 = array[4]; m12 = array[5];
        m20 = array[6]; m21 = array[7]; m22 = array[8];
        return this;
    }

}

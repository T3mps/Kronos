package net.acidfrog.kronos.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import net.acidfrog.kronos.core.lang.assertions.Asserts;

/**
 * Defines a <code>4x4</code> matrix alongside associated functions to transform it. The matrix
 * is column-major to match OpenGL's interpretation:
 * <p>
 *    <pre>
 *m00 m10 m20 m30
 *m01 m11 m21 m31
 *m02 m12 m22 m32
 *m03 m13 m23 m33
 *    </pre>
 * </p>
 * 
 * @author Ethan Temprovich
 */
public class Matrix4 {

    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    /**
     * Creates a new <code>Matrix4</code> and sets it to the identity matrix.
     */
    public Matrix4() {
        this.identity();
    }

    public Matrix4(float m00, float m01, float m02, float m03, 
                   float m10, float m11, float m12, float m13, 
                   float m20, float m21, float m22, float m23,
                   float m30, float m31, float m32, float m33) {
        set(m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33);
    }

    public Matrix4(Matrix2 mat) {
        set(mat);
    }

    public Matrix4(Matrix3 mat) {
        set(mat);
    }

    public Matrix4(Matrix4 mat) {
        set(mat);
    }

    public Matrix4(Quaternion quat) {
        set(quat);
    }

    public Matrix4(float[] mat) {
        set(mat);
    }

    public Matrix4(float[] mat, int offset) {
        set(mat, offset);
    }

    public Matrix4(float[] mat, int offset, int stride) {
        set(mat, offset, stride);
    }

    public Matrix4(float[] mat, int offset, int stride, int length) {
        set(mat, offset, stride, length);
    }

    public Matrix4 set(float m00, float m01, float m02, float m03, 
                       float m10, float m11, float m12, float m13, 
                       float m20, float m21, float m22, float m23,
                       float m30, float m31, float m32, float m33) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
        this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
        this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
        this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;

        return this;
    }

    public Matrix4 set(Matrix2 mat) {
        m00 = mat.m00; m01 = mat.m01; m02 = 0; m03 = 0;
        m10 = mat.m10; m11 = mat.m11; m12 = 0; m13 = 0;
        m20 = 0;       m21 = 0;       m22 = 1; m23 = 0;
        m30 = 0;       m31 = 0;       m32 = 0; m33 = 1;

        return this;
    }

    public Matrix4 set(Matrix3 mat) {
        m00 = mat.m00; m01 = mat.m01; m02 = mat.m02; m03 = 0;
        m10 = mat.m10; m11 = mat.m11; m12 = mat.m12; m13 = 0;
        m20 = mat.m20; m21 = mat.m21; m22 = mat.m22; m23 = 0;
        m30 = 0;       m31 = 0;       m32 = 0;       m33 = 1;

        return this;
    }
    
    public Matrix4 set(Matrix4 mat) {
        return set(mat.m00, mat.m01, mat.m02, mat.m03,
                   mat.m10, mat.m11, mat.m12, mat.m13,
                   mat.m20, mat.m21, mat.m22, mat.m23,
                   mat.m30, mat.m31, mat.m32, mat.m33);
    }

    public Matrix4 set(Quaternion quat) {
        return rotate(quat);
    }

    private Matrix4 set(float[] mat) {
        return set(mat, 0, 1);
    }

    private Matrix4 set(float[] mat, int offset) {
        return set(mat, offset, 1);
    }

    private Matrix4 set(float[] mat, int offset, int stride) {
        return set(mat, offset, stride, mat.length);
    }

    private Matrix4 set(float[] mat, int offset, int stride, int length) {
        Asserts.assertFalse(mat.length < offset + stride * length, "Array to small for matrix4.");

        for (int i = 0; i < length; i++) {
            int row = i / stride;
            int col = i % stride;

            switch (row) {
                case 0:
                    switch (col) {
                        case 0: m00 = mat[offset + i]; break;
                        case 1: m01 = mat[offset + i]; break;
                        case 2: m02 = mat[offset + i]; break;
                        case 3: m03 = mat[offset + i]; break;
                    }
                    break;
                case 1:
                    switch (col) {
                        case 0: m10 = mat[offset + i]; break;
                        case 1: m11 = mat[offset + i]; break;
                        case 2: m12 = mat[offset + i]; break;
                        case 3: m13 = mat[offset + i]; break;
                    }
                    break;
                case 2:
                    switch (col) {
                        case 0: m20 = mat[offset + i]; break;
                        case 1: m21 = mat[offset + i]; break;
                        case 2: m22 = mat[offset + i]; break;
                        case 3: m23 = mat[offset + i]; break;
                    }
                    break;
                case 3:
                    switch (col) {
                        case 0: m30 = mat[offset + i]; break;
                        case 1: m31 = mat[offset + i]; break;
                        case 2: m32 = mat[offset + i]; break;
                        case 3: m33 = mat[offset + i]; break;
                    }
                    break;
            }
        }

        return this;
    }

    public float determinant() {
        return m00 * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33) +
               m01 * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33) +
               m02 * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33) +
               m03 * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32);
    }

    public Matrix4 identity() {
        return set(1, 0, 0, 0,
                   0, 1, 0, 0,
                   0, 0, 1, 0,
                   0, 0, 0, 1);
    }

    public Matrix4 transpose() {
        return set(transposed());
    }

    public Matrix4 transposed() {
        Matrix4 mat = new Matrix4();
        return mat.set(m00, m10, m20, m30,
                       m01, m11, m21, m31,
                       m02, m12, m22, m32,
                       m03, m13, m23, m33);
    }

    public Matrix4 invert() {
        return invert(this);
    }

    public Matrix4 invert(Matrix4 out) {
        float invDet = determinant();

        if(invDet > Mathf.FLOAT_ROUNDING_ERROR) invDet = 1f / invDet;

        float m00 = (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32  -
                     m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33) * invDet;
        float m01 = (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32  -
                     m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33) * invDet;
        float m02 = (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31  -
                     m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33) * invDet;
        float m03 = (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31  -
                     m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32) * invDet;

        float m10 = (m01 * m22 * m33 + m02 * m23 * m31 + m03 * m21 * m32  -
                     m03 * m22 * m31 - m01 * m23 * m32 - m02 * m21 * m33) * invDet;
        float m11 = (m00 * m22 * m33 + m02 * m23 * m30 + m03 * m20 * m32  -
                     m03 * m22 * m30 - m00 * m23 * m32 - m02 * m20 * m33) * invDet;
        float m12 = (m00 * m21 * m33 + m01 * m23 * m30 + m03 * m20 * m31  -
                     m03 * m21 * m30 - m00 * m23 * m31 - m01 * m20 * m33) * invDet;
        float m13 = (m00 * m21 * m32 + m01 * m22 * m30 + m02 * m20 * m31  -
                     m02 * m21 * m30 - m00 * m22 * m31 - m01 * m20 * m32) * invDet;

        float m20 = (m01 * m12 * m33 + m02 * m13 * m31 + m03 * m11 * m32  -
                     m03 * m12 * m31 - m01 * m13 * m32 - m02 * m11 * m33) * invDet;
        float m21 = (m00 * m12 * m33 + m02 * m13 * m30 + m03 * m10 * m32  -
                     m03 * m12 * m30 - m00 * m13 * m32 - m02 * m10 * m33) * invDet;
        float m22 = (m00 * m11 * m33 + m01 * m13 * m30 + m03 * m10 * m31  -
                     m03 * m11 * m30 - m00 * m13 * m31 - m01 * m10 * m33) * invDet;
        float m23 = (m00 * m11 * m32 + m01 * m12 * m30 + m02 * m10 * m31  -
                     m02 * m11 * m30 - m00 * m12 * m31 - m01 * m10 * m32) * invDet;
        
        float m30 = (m01 * m12 * m23 + m02 * m13 * m21 + m03 * m11 * m22  -
                     m03 * m12 * m21 - m01 * m13 * m22 - m02 * m11 * m23) * invDet;
        float m31 = (m00 * m12 * m23 + m02 * m13 * m20 + m03 * m10 * m22  -
                     m03 * m12 * m20 - m00 * m13 * m22 - m02 * m10 * m23) * invDet;
        float m32 = (m00 * m11 * m23 + m01 * m13 * m20 + m03 * m10 * m21  -
                     m03 * m11 * m20 - m00 * m13 * m21 - m01 * m10 * m23) * invDet;
        float m33 = (m00 * m11 * m22 + m01 * m12 * m20 + m02 * m10 * m21  -
                     m02 * m11 * m20 - m00 * m12 * m21 - m01 * m10 * m22) * invDet;

        return out.set(m00, m01, m02, m03,
                       m10, m11, m12, m13,
                       m20, m21, m22, m23,
                       m30, m31, m32, m33);
    }

    public Matrix4 zero() {
        return set(0, 0, 0, 0,
                   0, 0, 0, 0,
                   0, 0, 0, 0,
                   0, 0, 0, 0);
    }

    /**
	 * Performs component-wise addition of matrix m and this and return this.
	 */
    public Matrix4 addi(Matrix4 m) {
        return add(m, this);
    }

    /**
	 * Component-wise addition of m and this and return the result
	 */
    public Matrix4 add(Matrix4 m) {
        return add(m, new Matrix4());
    }

    /**
	 * Component-wise addition of m and this, return out.
	 */
    public Matrix4 add(Matrix4 m, Matrix4 out) {
        out.m00 = m00 + m.m00; out.m01 = m01 + m.m01; out.m02 = m02 + m.m02; out.m03 = m03 + m.m03;
        out.m10 = m10 + m.m10; out.m11 = m11 + m.m11; out.m12 = m12 + m.m12; out.m13 = m13 + m.m13;
        out.m20 = m20 + m.m20; out.m21 = m21 + m.m21; out.m22 = m22 + m.m22; out.m23 = m23 + m.m23;
        out.m30 = m30 + m.m30; out.m31 = m31 + m.m31; out.m32 = m32 + m.m32; out.m33 = m33 + m.m33;
        return out;
    }

    /**
     * Performs component-wise subtraction of matrix m and this and return this.
     */
    public Matrix4 subi(Matrix4 m) {
        return sub(m, this);
    }

    /**
     * Component-wise subtraction of m and this and return the result
     */
    public Matrix4 sub(Matrix4 m) {
        return sub(m, new Matrix4());
    }

    /**
     * Component-wise subtraction of m and this, return out.
     */
    public Matrix4 sub(Matrix4 m, Matrix4 out) {
        out.m00 = m00 - m.m00; out.m01 = m01 - m.m01; out.m02 = m02 - m.m02; out.m03 = m03 - m.m03;
        out.m10 = m10 - m.m10; out.m11 = m11 - m.m11; out.m12 = m12 - m.m12; out.m13 = m13 - m.m13;
        out.m20 = m20 - m.m20; out.m21 = m21 - m.m21; out.m22 = m22 - m.m22; out.m23 = m23 - m.m23;
        out.m30 = m30 - m.m30; out.m31 = m31 - m.m31; out.m32 = m32 - m.m32; out.m33 = m33 - m.m33;
        return out;
    }

    public Matrix4 muli(float s) {
        return mul(s, this);
    }

    public Matrix4 mul(float s) {
        return mul(s, new Matrix4());
    }

    public Matrix4 mul(float s, Matrix4 out) {
        out.m00 = m00 * s; out.m01 = m01 * s; out.m02 = m02 * s; out.m03 = m03 * s;
        out.m10 = m10 * s; out.m11 = m11 * s; out.m12 = m12 * s; out.m13 = m13 * s;
        out.m20 = m20 * s; out.m21 = m21 * s; out.m22 = m22 * s; out.m23 = m23 * s;
        out.m30 = m30 * s; out.m31 = m31 * s; out.m32 = m32 * s; out.m33 = m33 * s;
        return out;
    }

    public Matrix4 muli(Matrix4 m) {
        return mul(m, this);
    }

    public Matrix4 mul(Matrix4 m) {
        return mul(m, new Matrix4());
    }
    
    public Matrix4 mul(Matrix4 m, Matrix4 out) {
        out.m00 = this.m00 * m.m00 + this.m01 * m.m10 + this.m02 * m.m20 + this.m03 * m.m30;
        out.m01 = this.m00 * m.m01 + this.m01 * m.m11 + this.m02 * m.m21 + this.m03 * m.m31;
        out.m02 = this.m00 * m.m02 + this.m01 * m.m12 + this.m02 * m.m22 + this.m03 * m.m32;
        out.m03 = this.m00 * m.m03 + this.m01 * m.m13 + this.m02 * m.m23 + this.m03 * m.m33;
        out.m10 = this.m10 * m.m00 + this.m11 * m.m10 + this.m12 * m.m20 + this.m13 * m.m30;
        out.m11 = this.m10 * m.m01 + this.m11 * m.m11 + this.m12 * m.m21 + this.m13 * m.m31;
        out.m12 = this.m10 * m.m02 + this.m11 * m.m12 + this.m12 * m.m22 + this.m13 * m.m32;
        out.m13 = this.m10 * m.m03 + this.m11 * m.m13 + this.m12 * m.m23 + this.m13 * m.m33;
        out.m20 = this.m20 * m.m00 + this.m21 * m.m10 + this.m22 * m.m20 + this.m23 * m.m30;
        out.m21 = this.m20 * m.m01 + this.m21 * m.m11 + this.m22 * m.m21 + this.m23 * m.m31;
        out.m22 = this.m20 * m.m02 + this.m21 * m.m12 + this.m22 * m.m22 + this.m23 * m.m32;
        out.m23 = this.m20 * m.m03 + this.m21 * m.m13 + this.m22 * m.m23 + this.m23 * m.m33;
        out.m30 = this.m30 * m.m00 + this.m31 * m.m10 + this.m32 * m.m20 + this.m33 * m.m30;
        out.m31 = this.m30 * m.m01 + this.m31 * m.m11 + this.m32 * m.m21 + this.m33 * m.m31;
        out.m32 = this.m30 * m.m02 + this.m31 * m.m12 + this.m32 * m.m22 + this.m33 * m.m32;
        out.m33 = this.m30 * m.m03 + this.m31 * m.m13 + this.m32 * m.m23 + this.m33 * m.m33;
        return out;
    }

    public Matrix4 divi(float s) {
        return div(s, this);
    }

    public Matrix4 div(float s) {
        return div(s, new Matrix4());
    }

    public Matrix4 div(float s, Matrix4 out) {
        out.m00 = m00 / s; out.m01 = m01 / s; out.m02 = m02 / s; out.m03 = m03 / s;
        out.m10 = m10 / s; out.m11 = m11 / s; out.m12 = m12 / s; out.m13 = m13 / s;
        out.m20 = m20 / s; out.m21 = m21 / s; out.m22 = m22 / s; out.m23 = m23 / s;
        out.m30 = m30 / s; out.m31 = m31 / s; out.m32 = m32 / s; out.m33 = m33 / s;
        return out;
    }

    public Matrix4 divi(Matrix4 m) {
        return div(m, this);
    }

    public Matrix4 div(Matrix4 m) {
        return div(m, new Matrix4());
    }

    public Matrix4 div(Matrix4 m, Matrix4 out) {
        out.m00 = this.m00 / m.m00; out.m01 = this.m01 / m.m01; out.m02 = this.m02 / m.m02; out.m03 = this.m03 / m.m03;
        out.m10 = this.m10 / m.m10; out.m11 = this.m11 / m.m11; out.m12 = this.m12 / m.m12; out.m13 = this.m13 / m.m13;
        out.m20 = this.m20 / m.m20; out.m21 = this.m21 / m.m21; out.m22 = this.m22 / m.m22; out.m23 = this.m23 / m.m23;
        out.m30 = this.m30 / m.m30; out.m31 = this.m31 / m.m31; out.m32 = this.m32 / m.m32; out.m33 = this.m33 / m.m33;
        return out;
    }


    public Matrix4 rotate(float angle, float x, float y, float z) {
        if (y == 0f && z == 0f && Mathf.abs(x) == 1f) return rotateX(x * angle);
        if (x == 0f && z == 0f && Mathf.abs(y) == 1f) return rotateY(y * angle);
        if (x == 0f && y == 0f && Mathf.abs(z) == 1f) return rotateZ(z * angle);
        
        float sin = Mathf.sin(angle), cos = Mathf.cos(angle);
        float invCos = 1f - cos, xy = x * y, xz = x * z, yz = y * z;

        m00 = x * x * invCos + cos;
        m01 = xy * invCos + z * sin;
        m02 = xz * invCos - y * sin;
        m10 = xy * invCos - z * sin;
        m11 = y * y * invCos + cos;
        m12 = yz * invCos + x * sin;
        m20 = xz * invCos + y * sin;
        m21 = yz * invCos - x * sin;
        m22 = z * z * invCos + cos;
        return this;
    }

    public Matrix4 rotateX(float angle) {
        float sin = Mathf.sin(angle), cos = Mathf.cos(angle);
        m11 = cos; m12 = sin; m21 = -sin; m22 = cos;
        return this;
    }

    public Matrix4 rotateY(float angle) {
        float sin = Mathf.sin(angle), cos = Mathf.cos(angle);
        m00 = cos; m02 = -sin; m20 = sin; m22 = cos;
        return this;
    }

    public Matrix4 rotateZ(float angle) {
        float sin = Mathf.sin(angle), cos = Mathf.cos(angle);
        m00 = cos; m01 = sin; m10 = -sin; m11 = cos;
        return this;
    }

    public Matrix4 rotate(float angle, Vector3 axis) {
        return rotate(angle, axis.x, axis.y, axis.z);
    }

    public Matrix4 rotate(float angle, Vector3 axis, Vector3 pivot) {
        return translate(pivot).rotate(angle, axis).translate(pivot.negated());
    }

    public Matrix4 rotate(Quaternion quat) {
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
        m10 = dxy - dzw;
        m11 = y2 + w2 - x2 - z2;
        m12 = dyz + dxw;
        m20 = dxz + dyw;
        m21 = dyz - dxw;
        m22 = z2 + w2 - x2 - y2;

        return this;
    }

    public Matrix4 orthogonalize(float left, float right, float bottom, float top, float zNear, float zFar, boolean zZeroToOne) {
        float rm00 = 2.0f / (right - left);
        float rm11 = 2.0f / (top - bottom);
        float rm22 = (zZeroToOne ? 1.0f : 2.0f) / (zNear - zFar);
        float rm30 = (left + right) / (left - right);
        float rm31 = (top + bottom) / (bottom - top);
        float rm32 = (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar);

        float nm30 = m00 * rm30 + m10 * rm31 + m20 * rm32 + m30;
        float nm31 = m01 * rm30 + m11 * rm31 + m21 * rm32 + m31;
        float nm32 = m02 * rm30 + m12 * rm31 + m22 * rm32 + m32;
        float nm33 = m03 * rm30 + m13 * rm31 + m23 * rm32 + m33;
        
        float nm00 = m00 * rm00;
        float nm01 = m01 * rm00;
        float nm02 = m02 * rm00;
        float nm03 = m03 * rm00;
        float nm10 = m10 * rm11;
        float nm11 = m11 * rm11;
        float nm12 = m12 * rm11;
        float nm13 = m13 * rm11;
        float nm20 = m20 * rm22;
        float nm21 = m21 * rm22;
        float nm22 = m22 * rm22;
        float nm23 = m23 * rm22;

        m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03;
        m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13;
        m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23;
        m30 = nm30; m31 = nm31; m32 = nm32; m33 = nm33;

        return this;
    }

    public Matrix4 orthogonalize(float left, float right, float bottom, float top, float zNear, float zFar) {
        return orthogonalize(left, right, bottom, top, zNear, zFar, false);
    }


    public Matrix4 lookAt(float camX, float camY, float camZ,
                          float targetX, float targetY, float targetZ,
                          float upX, float upY, float upZ) {
        // Compute direction from position to lookAt
        float dirX, dirY, dirZ;
        dirX = camX - targetX;
        dirY = camY - targetY;
        dirZ = camZ - targetZ;
        // Normalize direction
        float invDirLength = Mathf.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX *= invDirLength;
        dirY *= invDirLength;
        dirZ *= invDirLength;
        // left = up x direction
        float leftX, leftY, leftZ;
        leftX = upY * dirZ - upZ * dirY;
        leftY = upZ * dirX - upX * dirZ;
        leftZ = upX * dirY - upY * dirX;
        // normalize left
        float invLeftLength = Mathf.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        leftX *= invLeftLength;
        leftY *= invLeftLength;
        leftZ *= invLeftLength;
        // up = direction x left
        float upnX = dirY * leftZ - dirZ * leftY;
        float upnY = dirZ * leftX - dirX * leftZ;
        float upnZ = dirX * leftY - dirY * leftX;

        // calculate right matrix elements
        float rm30 = -(leftX * camX + leftY * camY + leftZ * camZ);
        float rm31 = -(upnX * camX + upnY * camY + upnZ * camZ);
        float rm32 = -(dirX * camX + dirY * camY + dirZ * camZ);
        // introduce temporaries for dependent results
        float nm00 = m00 * leftX + m10 * upnX + m20 * dirX;
        float nm01 = m01 * leftX + m11 * upnX + m21 * dirX;
        float nm02 = m02 * leftX + m12 * upnX + m22 * dirX;
        float nm03 = m03 * leftX + m13 * upnX + m23 * dirX;
        float nm10 = m00 * leftY + m10 * upnY + m20 * dirY;
        float nm11 = m01 * leftY + m11 * upnY + m21 * dirY;
        float nm12 = m02 * leftY + m12 * upnY + m22 * dirY;
        float nm13 = m03 * leftY + m13 * upnY + m23 * dirY;

        m30 = m00 * rm30 + m10 * rm31 + m20 * rm32 + m30;
        m31 = m01 * rm30 + m11 * rm31 + m21 * rm32 + m31;
        m32 = m02 * rm30 + m12 * rm31 + m22 * rm32 + m32;
        m33 = m03 * rm30 + m13 * rm31 + m23 * rm32 + m33;
        m20 = m00 * leftZ + m10 * upnZ + m20 * dirZ;
        m21 = m01 * leftZ + m11 * upnZ + m21 * dirZ;
        m22 = m02 * leftZ + m12 * upnZ + m22 * dirZ;
        m23 = m03 * leftZ + m13 * upnZ + m23 * dirZ;
        m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03;
        m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13;

        return this;
    }

    public Matrix4 lookAt(Vector3 cam, Vector3 target, Vector3 up) {
        return lookAt(cam.x, cam.y, cam.z, target.x, target.y, target.z, up.x, up.y, up.z);
    }

    public Matrix4 translate(float x, float y, float z) {
        m03 += m00 * x + m01 * y + m02 * z;
        m13 += m10 * x + m11 * y + m12 * z;
        m23 += m20 * x + m21 * y + m22 * z;
        return this;
    }

    public Matrix4 translate(Vector3 v) {
        return translate(v.x, v.y, v.z);
    }

    public FloatBuffer toFloatBuffer() {
        FloatBuffer result = BufferUtils.createFloatBuffer(capacity());
        result.put(m00).put(m01).put(m02).put(m03);
        result.put(m10).put(m11).put(m12).put(m13);
        result.put(m20).put(m21).put(m22).put(m23);
        result.put(m30).put(m31).put(m32).put(m33);
        // result.flip();
        return result;
    }

    public static int capacity() {
        return 4 * 4;
    }

    public float[] toArray() {
        return new float[] {
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33
        };
    }

    public Matrix4 fromArray(float[] arr) {
        if (arr.length < capacity()) throw new IllegalArgumentException("Array must be of size " + capacity() + " or greater.");

        m00 = arr[0]; m01 = arr[1]; m02 = arr[2];  m03 = arr[3];
        m10 = arr[4]; m11 = arr[5]; m12 = arr[6];  m13 = arr[7];
        m20 = arr[8]; m21 = arr[9]; m22 = arr[10]; m23 = arr[11];
        m30 = arr[8]; m31 = arr[9]; m32 = arr[10]; m33 = arr[11];

        return this;
    }

}

/*
 * The MIT License
 *
 * Copyright (c) 2022 Ethan Temprovich
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
package net.acidfrog.kronos.mathk;

//#ifdef __HAS_NIO__
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
//#endif
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Matrix2k implements Externalizable, Cloneable, Comparable<Matrix2k> {

    private static final long serialVersionUID = 1L;

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

    public Matrix2k(double radians) {
        set(radians);
    }

    public Matrix2k(double m00, double m01, double m10, double m11) {
        set(m00, m01,
            m10, m11);
    }

	public Matrix2k(Matrix2k mat) {
		set(mat);
	}

    public Matrix2k(Matrix2fc mat) {
        set(mat);
    }

    public Matrix2k(Matrix2dc mat) {
        set(mat);
    }

	public Matrix2k(float[] mat2) {
		set(mat2);
	}

	public Matrix2k(float[] mat2, int offset) {
        set(mat2, offset);
	}

    public Matrix2k(float[] mat2, int offset, int stride) {
        set(mat2, offset, stride);
    }

    public float m00() {
        return m00;
    }

    public float m01() {
        return m01;
    }

    public float m10() {
        return m10;
    }

    public float m11() {
        return m11;
    }

//#ifdef __HAS_NIO__
    public FloatBuffer get(FloatBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    public FloatBuffer get(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.put(new Matrix2f(this), index, buffer);
        return buffer;
    }

    public ByteBuffer get(ByteBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.put(new Matrix2f(this), index, buffer);
        return buffer;
    }

    public FloatBuffer getTransposed(FloatBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    public FloatBuffer getTransposed(int index, FloatBuffer buffer) {
        MemUtil.INSTANCE.putTransposed(new Matrix2f(this), index, buffer);
        return buffer;
    }

    public ByteBuffer getTransposed(ByteBuffer buffer) {
        return get(buffer.position(), buffer);
    }

    public ByteBuffer getTransposed(int index, ByteBuffer buffer) {
        MemUtil.INSTANCE.putTransposed(new Matrix2f(this), index, buffer);
        return buffer;
    }
//#endif
//#ifdef __HAS_UNSAFE__
    public Matrix2k getToAddress(long address) {
        if (Options.NO_UNSAFE)
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        MemUtil.MemUtilUnsafe.put(new Matrix2f(this), address);
        return this;
    }
//#endif

    public float[] get(float[] arr, int offset) {
        MemUtil.INSTANCE.copy(new Matrix2f(this), arr, offset);
        return arr;
    }

    public float[] get(float[] arr) {
        return get(arr, 0);
    }

	/**
	 * Sets this matrix to a rotation matrix with the given radians.
	 */
	public Matrix2k set(float radians) {
		float c = Mathk.cos(radians);
		float s = Mathk.sin(radians);

		m00 = c; m01 = -s;
		m10 = s; m11 =  c;
        return this;
	}

	/**
	 * Sets the values of this matrix.
	 */
	public Matrix2k set(float a, float b, float c, float d) {
		m00 = a; m01 = b;
		m10 = c; m11 = d;
        return this;
	}

    public Matrix2k set(double radians) {
        return set((float) radians);
    }

    public Matrix2k set(double a, double b, double c, double d) {
        return set((float) a, (float) b,
                   (float) c, (float) d);
    }

	/**
	 * Sets this matrix to have the same values as the given matrix.
	 */
	public Matrix2k set(Matrix2k m) {
		m00 = m.m00; m01 = m.m01;
		m10 = m.m10; m11 = m.m11;
        return this;
	}

    public Matrix2k set(Matrix2fc m) {
        return set(m.m00(), m.m01(),
                   m.m10(), m.m11());
    }

    public Matrix2k set(Matrix2dc m) {
        return set(m.m00(), m.m01(),
                   m.m10(), m.m11());
    }

	/**
	 * Sets this matrix to have the same values as the given array.
	 */
	public Matrix2k set(float[] m) {
		return set(m, 0, 1);
	}

	public Matrix2k set(float[] m, int offset) {
		return set(m, offset, 1);
	}

    public Matrix2k set(float[] m, int offset, int stride) {
        m00 = m[offset + 0 * stride]; m01 = m[offset + 1 * stride];
        m10 = m[offset + 2 * stride]; m11 = m[offset + 3 * stride];
        return this;
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
		if (det == 0f) throw new IllegalArgumentException("Matrix is not invertible.");
		float invDet = 1f / det;
		out.m00 =  m11 * invDet;
		out.m01 = -m01 * invDet;
		out.m10 = -m10 * invDet;
		out.m11 =  m00 * invDet;
		return out;
	}

	public Matrix2k inverse() {
		Matrix2k out = new Matrix2k();
		return invert(out);
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
	public Vector2k mul(Vector2k v) {
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
	public int compareTo(Matrix2k o) {
		if (m00 != o.m00) return Float.compare(m00, o.m00);
		if (m01 != o.m01) return Float.compare(m01, o.m01);
		if (m10 != o.m10) return Float.compare(m10, o.m10);
		if (m11 != o.m11) return Float.compare(m11, o.m11);
		return 0;
	}
    

    @Override
    public Matrix2k clone() {
        return new Matrix2k(this);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(m00);
        out.writeFloat(m01);
        out.writeFloat(m10);
        out.writeFloat(m11);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        m00 = in.readFloat();
        m01 = in.readFloat();
        m10 = in.readFloat();
        m11 = in.readFloat();
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

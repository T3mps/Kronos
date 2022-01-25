package net.acidfrog.kronos.math;

import net.acidfrog.kronos.core.lang.assertions.Asserts;

public class Quaternion {

    public float x, y, z, w;

    public Quaternion() {
        set(0, 0, 0, 1);
    }

    public Quaternion(float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    public Quaternion(Quaternion q) {
        set(q);
    }
    
    public Quaternion(Matrix2 m) {
        set(m);
    }

    public Quaternion(Matrix3 m) {
        set(m);
    }
    
    public Quaternion(Matrix4 m) {
        set(m);
    }

    public Quaternion(float[] m) {
        set(m);
    }

    public Quaternion(float[] m, int offset) {
        set(m, offset);
    }

    public Quaternion(float[] m, int offset, int stride) {
        set(m, offset, stride);
    }

    public Quaternion(float[] m, int offset, int stride, int length) {
        set(m, offset, stride, length);
    }

    public Quaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Quaternion set(Quaternion q) {
        return set(q.x, q.y, q.z, q.w);
    }

    public Quaternion set(Matrix2 m) {
        float a = m.m00 + m.m11;
        float b = m.m00 - m.m11;
        float c = m.m01 + m.m10;
        float d = m.m01 - m.m10;
        float e = (float) Mathf.sqrt(a * a + b * b + c * c + d * d);
        float s = e != 0 ? 2 / e : 0;
        x = a * s;
        y = b * s;
        z = c * s;
        w = d * s;
        return this;
    }

    public Quaternion set(Matrix3 m) {
        float a = m.m00 + m.m11 + m.m22;
        float b = m.m00 - m.m11 - m.m22;
        float c = m.m01 + m.m10;
        float d = m.m01 - m.m10;
        float e = m.m02 + m.m20;
        float f = m.m02 - m.m20;
        float g = m.m12 - m.m21;
        float h = m.m12 + m.m21;
        float i = (float) Mathf.sqrt(a * a + b * b + c * c + d * d + e * e + f * f + g * g + h * h);
        float s = i != 0 ? 2 / i : 0;
        x = a * s;
        y = b * s;
        z = c * s;
        w = d * s;
        return this;
    }

    public Quaternion set(Matrix4 mat) {
        float a = mat.m00 + mat.m11 + mat.m22 + mat.m33;
        float b = mat.m00 - mat.m11 - mat.m22 + mat.m33;
        float c = mat.m01 + mat.m10;
        float d = mat.m01 - mat.m10;
        float e = mat.m02 + mat.m20;
        float f = mat.m02 - mat.m20;
        float g = mat.m12 - mat.m21;
        float h = mat.m12 + mat.m21;
        float i = mat.m13 + mat.m31;
        float j = mat.m13 - mat.m31;
        float k = mat.m23 - mat.m32;
        float l = mat.m23 + mat.m32;
        float m = (float) Mathf.sqrt(a * a + b * b + c * c + d * d + e * e + f * f + g * g + h * h + i * i + j * j + k * k + l * l);
        float s = m != 0 ? 2 / m : 0;
        x = a * s;
        y = b * s;
        z = c * s;
        w = d * s;
        return this;
    }

    public Quaternion set(float[] m) {
        return set(m, 0, 1, m.length);
    }

    public Quaternion set(float[] m, int offset) {
        return set(m, offset, 1, m.length - offset);
    }

    public Quaternion set(float[] m, int offset, int stride) {
        return set(m, offset, stride, (m.length - offset) / stride);
    }

    public Quaternion set(float[] m, int offset, int stride, int length){
        Asserts.assertFalse(offset < 0 || offset >= m.length || stride <= 0 || length <= 0 || offset + stride * length > m.length, "Invalid array specifiers.");

        float x = 0, y = 0, z = 0, w = 0;
        for (int i = 0; i < length; i++) {
            switch ((i * stride) + offset) {
                case 0:
                    x = m[i * stride + offset];
                    break;
                case 1:
                    y = m[i * stride + offset];
                    break;
                case 2:
                    z = m[i * stride + offset];
                    break;
                case 3:
                    w = m[i * stride + offset];
                    break;
            }
        }
        return set(x, y, z, w);
    }

    public Quaternion normalize() {
        return set(normalized());
    }

    public Quaternion normalized() {
        Quaternion q = new Quaternion(this);
        float l = Mathf.sqrt(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w);
        if (l > Mathf.FLOAT_ROUNDING_ERROR) {
            l = 1 / l;
            q.x *= l;
            q.y *= l;
            q.z *= l;
            q.w *= l;
        }
        return q;
    }

    public Quaternion conjugate() {
        return set(conjugated());
    }

    public Quaternion conjugated() {
        return new Quaternion(-x, -y, -z, w);
    }

    public Quaternion inverse() {
        return set(inversed());
    }

    public Quaternion inversed() {
        float l = x * x + y * y + z * z + w * w;
        if (l > Mathf.FLOAT_ROUNDING_ERROR) {
            l = 1 / l;
            return new Quaternion(-x * l, -y * l, -z * l, w * l);
        }
        return new Quaternion();
    }

    public Quaternion addi(Quaternion q) {
        return add(q, this);
    }

    public Quaternion add(Quaternion q) {
        return add(q, new Quaternion());
    }

    public Quaternion add(Quaternion q, Quaternion out) {
        out.x = x + q.x;
        out.y = y + q.y;
        out.z = z + q.z;
        out.w = w + q.w;
        return out;
    }

    public Quaternion subi(Quaternion q) {
        return sub(q, this);
    }

    public Quaternion sub(Quaternion q) {
        return sub(q, new Quaternion());
    }

    public Quaternion sub(Quaternion q, Quaternion out) {
        out.x = x - q.x;
        out.y = y - q.y;
        out.z = z - q.z;
        out.w = w - q.w;
        return out;
    }
    
    public Quaternion muli(float s) {
        return mul(s, this);
    }

    public Quaternion mul(float s) {
        return mul(s, new Quaternion());
    }

    public Quaternion mul(float s, Quaternion out) {
        out.x = x * s;
        out.y = y * s;
        out.z = z * s;
        out.w = w * s;
        return out;
    }

    public Quaternion muli(float x, float y, float z, float w) {
        return mul(x, y, z, w, this);
    }

    public Quaternion mul(float x, float y, float z, float w) {
        return mul(x, y, z, w, new Quaternion());
    }

    public Quaternion mul(float x, float y, float z, float w, Quaternion out) {
        out.x = this.x * x - this.y * y - this.z * z - this.w * w;
        out.y = this.x * y + this.y * x - this.z * w + this.w * z;
        out.z = this.x * z + this.y * w + this.z * x - this.w * y;
        out.w = this.x * w - this.y * z + this.z * y + this.w * x;
        return out;
    }

    public Quaternion muli(Quaternion q) {
        return mul(q, this);
    }

    public Quaternion mul(Quaternion q) {
        return mul(q, new Quaternion());
    }

    public Quaternion mul(Quaternion q, Quaternion out) {
        return mul(q.x, q.y, q.z, q.w, out);
    }

    public Quaternion divi(float x, float y, float z, float w) {
        return div(x, y, z, w, this);
    }

    public Quaternion div(float x, float y, float z, float w) {
        return div(x, y, z, w, new Quaternion());
    }

    public Quaternion div(float x, float y, float z, float w, Quaternion out) {
        float l = x * x + y * y + z * z + w * w;
        if (l > Mathf.FLOAT_ROUNDING_ERROR) {
            l = 1 / l;
            out.x = (this.x * x + this.y * y + this.z * z + this.w * w) * l;
            out.y = (this.x * y - this.y * x - this.z * w + this.w * z) * l;
            out.z = (this.x * z + this.y * w - this.z * x - this.w * y) * l;
            out.w = (this.x * w - this.y * z + this.z * y - this.w * x) * l;
        } else out.set(0, 0, 0, 1);
        return out;
    }

    public Quaternion divi(Quaternion q) {
        return div(q, this);
    }

    public Quaternion div(Quaternion q) {
        return div(q, new Quaternion());
    }

    public Quaternion div(Quaternion q, Quaternion out) {
        return div(q.x, q.y, q.z, q.w, out);
    }

    public float dot(Quaternion q) {
        return x * q.x + y * q.y + z * q.z + w * q.w;
    }

    public float angle() {
        return 2f * Mathf.safeAcos(w);
    }

    public Quaternion rotate(float angle, float x, float y, float z) {
        float s = Mathf.sin(angle * 0.5f);
        this.x = x * s;
        this.y = y * s;
        this.z = z * s;
        this.w = Mathf.cos(angle * 0.5f);
        return this;
    }

    public Quaternion rotate(float angle, Vector3 axis) {
        return rotate(angle, axis.x, axis.y, axis.z);
    }

    public Quaternion rotateX(float angle) {
        float sin = Mathf.sin(angle * 0.5f);
        float cos = Mathf.cos(angle * 0.5f);
        return set(sin, 0, 0, cos);
    }

    public Quaternion rotateY(float angle) {
        float sin = Mathf.sin(angle * 0.5f);
        float cos = Mathf.cos(angle * 0.5f);
        return set(0, sin, 0, cos);
    }

    public Quaternion rotateZ(float angle) {
        float sin = Mathf.sin(angle * 0.5f);
        float cos = Mathf.cos(angle * 0.5f);
        return set(0, 0, sin, cos);
    }

    public Quaternion rotateXYZ(float angleX, float angleY, float angleZ) {
        float sx = Mathf.sin(angleX * 0.5f);
        float cx = Mathf.cos(angleX * 0.5f);
        float sy = Mathf.sin(angleY * 0.5f);
        float cy = Mathf.cos(angleY * 0.5f);
        float sz = Mathf.sin(angleZ * 0.5f);
        float cz = Mathf.cos(angleZ * 0.5f);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        w = cx*cycz - sx*sysz;
        x = sx*cycz + cx*sysz;
        y = cx*sycz - sx*cysz;
        z = cx*cysz + sx*sycz;

        return this;
    }

    public Quaternion rotateZYX(float angleZ, float angleY, float angleX) {
        float sx = Mathf.sin(angleX * 0.5f);
        float cx = Mathf.cos(angleX * 0.5f);
        float sy = Mathf.sin(angleY * 0.5f);
        float cy = Mathf.cos(angleY * 0.5f);
        float sz = Mathf.sin(angleZ * 0.5f);
        float cz = Mathf.cos(angleZ * 0.5f);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        w = cx*cycz + sx*sysz;
        x = sx*cycz - cx*sysz;
        y = cx*sycz + sx*cysz;
        z = cx*cysz - sx*sycz;

        return this;
    }

    public Quaternion rotateYXZ(float angleY, float angleX, float angleZ) {
        float sx = Mathf.sin(angleX * 0.5f);
        float cx = Mathf.cos(angleX * 0.5f);
        float sy = Mathf.sin(angleY * 0.5f);
        float cy = Mathf.cos(angleY * 0.5f);
        float sz = Mathf.sin(angleZ * 0.5f);
        float cz = Mathf.cos(angleZ * 0.5f);

        float x = cy * sx;
        float y = sy * cx;
        float z = sy * sx;
        float w = cy * cx;
        this.x = x * cz + y * sz;
        this.y = y * cz - x * sz;
        this.z = w * sz - z * cz;
        this.w = w * cz + z * sz;

        return this;
    }

    public Quaternion identity() {
        return set(0, 0, 0, 1);
    }

    public Vector3 getEulerAnglesXYZ(Vector3 eulerAngles) {
        eulerAngles.x = Mathf.atan2(x * w - y * z, 0.5f - x * x - y * y);
        eulerAngles.y = Mathf.safeAsin(2.0f * (x * z + y * w));
        eulerAngles.z = Mathf.atan2(z * w - x * y, 0.5f - y * y - z * z);
        return eulerAngles;
    }

    public Vector3 getEulerAnglesZYX(Vector3 eulerAngles) {
        eulerAngles.x = Mathf.atan2(y * z + w * x, 0.5f - x * x + y * y);
        eulerAngles.y = Mathf.safeAsin(-2.0f * (x * z - w * y));
        eulerAngles.z = Mathf.atan2(x * y + w * z, 0.5f - y * y - z * z);
        return eulerAngles;
    }

    public Vector3 getEulerAnglesZXY(Vector3 eulerAngles) {
        eulerAngles.x = Mathf.safeAsin(2.0f * (w * x + y * z));
        eulerAngles.y = Mathf.atan2(w * y - x * z, 0.5f - y * y - x * x);
        eulerAngles.z = Mathf.atan2(w * z - x * y, 0.5f - z * z - x * x);
        return eulerAngles;
    }

    public Vector3 getEulerAnglesYXZ(Vector3 eulerAngles) {
        eulerAngles.x = Mathf.safeAsin(-2.0f * (y * z - w * x));
        eulerAngles.y = Mathf.atan2(x * z + y * w, 0.5f - y * y - x * x);
        eulerAngles.z = Mathf.atan2(y * x + w * z, 0.5f - x * x - z * z);
        return eulerAngles;
    }

    public Quaternion slerp(Quaternion target, float alpha) {
        float dot = dot(target);
        if (dot < 0.0f) {
            target.x = -target.x;
            target.y = -target.y;
            target.z = -target.z;
            target.w = -target.w;
            dot = -dot;
        }

        if (dot > 0.9995f) {
            return set(x + alpha * (target.x - x), y + alpha * (target.y - y), z + alpha * (target.z - z), w + alpha * (target.w - w));
        }

        float theta0 = (float) Math.acos(dot);
        float theta = theta0 * alpha;
        float sinTheta = Mathf.sin(theta);
        float sinTheta0 = Mathf.sin(theta0);
        float s0 = Mathf.cos(theta) - dot * sinTheta / sinTheta0;
        float s1 = sinTheta / sinTheta0;
        return set(x * s0 + target.x * s1, y * s0 + target.y * s1, z * s0 + target.z * s1, w * s0 + target.w * s1);
    }

    public Quaternion nlerp(Quaternion target, float factor) {
        float cosom = x * target.x + y * target.y + z * target.z + w * target.w;
        float scale0 = 1.0f - factor;
        float scale1 = (cosom > 0.0f) ? factor : -factor;

        x = Mathf.fma(scale0, x, scale1 * target.x);
        y = Mathf.fma(scale0, y, scale1 * target.y);
        z = Mathf.fma(scale0, z, scale1 * target.z);
        w = Mathf.fma(scale0, w, scale1 * target.w);
        float s = 1.0f / Mathf.sqrt(x * x + y * y + z * z + w * w);
        return set(x * s, y * s, z * s, w * s);
    }

    public Vector3 transform(float x, float y, float z) {
        float xx = this.x * this.x, yy = this.y * this.y, zz = this.z * this.z, ww = this.w * this.w;
        float xy = this.x * this.y, xz = this.x * this.z, yz = this.y * this.z, xw = this.x * this.w;
        float zw = this.z * this.w, yw = this.y * this.w, k = 1 / (xx + yy + zz + ww);

        return new Vector3(Math.fma((xx - yy - zz + ww) * k, x, Math.fma(2 * (xy - zw) * k, y, (2 * (xz + yw) * k) * z)),
                           Math.fma(2 * (xy + zw) * k, x, Math.fma((yy - xx - zz + ww) * k, y, (2 * (yz - xw) * k) * z)),
                           Math.fma(2 * (xz - yw) * k, x, Math.fma(2 * (yz + xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
    }

    public Vector3 transform(Vector3 vector) {
        return transform(vector.x, vector.y, vector.z);
    }

    public Vector3 transformInverse(float x, float y, float z) {
        float n = 1.0f / Math.fma(this.x, this.x, Math.fma(this.y, this.y, Math.fma(this.z, this.z, this.w * this.w)));
        float qx = this.x * n, qy = this.y * n, qz = this.z * n, qw = this.w * n;
        float xx = qx * qx, yy = qy * qy, zz = qz * qz, ww = qw * qw;
        float xy = qx * qy, xz = qx * qz, yz = qy * qz, xw = qx * qw;
        float zw = qz * qw, yw = qy * qw, k = 1 / (xx + yy + zz + ww);
        return new Vector3(Math.fma((xx - yy - zz + ww) * k, x, Math.fma(2 * (xy + zw) * k, y, (2 * (xz - yw) * k) * z)),
                           Math.fma(2 * (xy - zw) * k, x, Math.fma((yy - xx - zz + ww) * k, y, (2 * (yz + xw) * k) * z)),
                           Math.fma(2 * (xz + yw) * k, x, Math.fma(2 * (yz - xw) * k, y, ((zz - xx - yy + ww) * k) * z)));
    }

    public Vector3 transformInverse(Vector3 vector) {
        return transformInverse(vector.x, vector.y, vector.z);
    }

    public Vector3 transformPositiveX() {
        float ww = w * w;
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float zw = z * w;
        float xy = x * y;
        float xz = x * z;
        float yw = y * w;
        return new Vector3(ww + xx - zz - yy,
                           xy + zw + zw + xy,
                           xz - yw + xz - yw);
    }

    public Vector3 transformPositiveY() {
        float ww = w * w;
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float zw = z * w;
        float xy = x * y;
        float xz = x * z;
        float yw = y * w;
        return new Vector3(xy + zw + zw + xy,
                           ww - xx + yy - zz,
                           xz - yw + xz - yw);
    }

    public Vector3 transformPositiveZ() {
        float ww = w * w;
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float zw = z * w;
        float xy = x * y;
        float xz = x * z;
        float yw = y * w;
        return new Vector3(xz + yw + xz + yw,
                           xy - zw + xy - zw,
                           ww - xx - yy + zz);
    }
    
    public float lengthSquared() {
        return Mathf.fma(x, x, Mathf.fma(y, y, Mathf.fma(z, z, w * w)));
    }

    public float length() {
        return Mathf.sqrt(lengthSquared());
    }

    public Matrix3 getMatrix3() {
        return new Matrix3(this);
    }

    public Matrix4 getMatrix4() {
        return new Matrix4(this);
    }

}

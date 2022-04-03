package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Matrix2k;
import net.acidfrog.kronos.math.RoundingMode;
import net.acidfrog.kronos.math.Vector2k;

/**
 * Stores the rotation properties of an object. The rotation value
 * is stored in radians, and a {@link Matrix2k 2x2 matrix}. The
 * {@link Rotation#u rotation matrix} can be used to rotate a
 * {@link Vector2k vector} in 2D space.
 * 
 * @author Ethan Temprovich
 */
public final class Rotation implements Comparable<Rotation> {

    /** stores the rotation in radians */
    private float radians;

    /** stores the rotation matrix */
    private final Matrix2k u;

    /** 
     * Default constructor.
     */
    public Rotation() {
        this(0f);
    }

    /**
     * Constructs a {@link Rotation rotation} object with the given
     * {@code radians}.
     * 
     * @param {@code float} radians
     */
    public Rotation(float radians) {
        this.radians = radians;
        this.u = new Matrix2k(radians);
    }

    /**
     * Constructs a {@link Rotation rotation} object with the given
     * {@code degrees}.
     * 
     * @param {@code float} degrees
     */
    public Rotation(double degrees) {
        this(Mathk.roundUsing(Mathk.toRadians(degrees), RoundingMode.HALF_DOWN));
    }
    
    /**
     * Constructs a {@link Rotation rotation} object in the given
     * {@link Vector2k direction}.
     * 
     * @param {@link Vector2k} direction
     */
    public Rotation(Vector2k direction) {
        this(direction.angleRad());
    }

    /**
     * Copy constructor.
     * 
     * @param {@link Rotation} rotation object
     */
    public Rotation(Rotation rotation) {
        this.radians = rotation.radians;
        this.u = rotation.u.clone();
    }

    /**
     * Rotates this {@link Rotation object} by the given {@code radians}.
     * 
     * @param {@code float} radians
     */
    public void rotate(float radians) {
        this.radians += radians;
        this.u.set(this.radians);
    }

    /**
     * Multiplies the given {@link Vector2k point} by this objects
     * {@link Rotation#u rotation matrix}. This method modifies the given
     * {@link Vector2k point}.
     * 
     * @param {@link Vector2k} the point to rotate
     * @return {@link Vector2k} the rotated point
     */
    public Vector2k rotatePoint(Vector2k point) {
        return u.muli(point);
    }

    /**
     * Multiplies the given {@link Vector2k point} by this objects
     * {@link Rotation#u rotation matrix}. This method does not modify
     * the given {@link Vector2k point}.
     * 
     * @param {@link Vector2k} the point to rotate
     * @return {@link Vector2k} the rotated point
     */
    public Vector2k getRotatedPoint(Vector2k point) {
        Vector2k out = new Vector2k();
        u.mul(point, out);
        return out;
    }

    /**
     * Multiplies the given {@link Vector2k point} by this objects
     * inverse {@link Rotation#u rotation matrix}. This method modifies
     * the given {@link Vector2k point}.
     * 
     * @param {@link Vector2k} the point to rotate
     * @return {@link Vector2k} the rotated point
     */
    public Vector2k inverseRotatePoint(Vector2k point) {
        return u.inverse().muli(point);
    }

    /**
     * Multiplies the given {@link Vector2k point} by this objects
     * inverse {@link Rotation#u rotation matrix}. This method does not
     * modify the given {@link Vector2k point}.
     * 
     * @param {@link Vector2k} the point to rotate
     * @return {@link Vector2k} the rotated point
     */
    public Vector2k getInverseRotatedPoint(Vector2k point) {
        Vector2k out = new Vector2k();
        u.inverse().mul(point, out);
        return out;
    }

    public void set(float radians) {
        this.radians = radians;
        this.u.set(radians);
    }

    /**
     * Sets this {@link Rotation object} to the given {@link Rotation object}.
     * 
     * @param {@link Rotation} rotation object
     */
    public void set(Rotation rotation) {
        this.radians = rotation.radians;
        this.u.set(this.radians);
    }

    /**
     * Sets this {@link Rotation object} to the identity. This is
     * equivalent to setting the {@link Rotation#radians radians} to 0
     * and {@link Rotation#u rotation matrix} to the
     * {@link Matrix2k#identity identity matrix}.
     */
    public void identity() {
        this.radians = 0f;
        this.u.identity();
    }

    /**
     * Returns the {@link Rotation#radians radians} of this object.
     * 
     * @return {@code float} radians
     */
    public float getRadians() {
        return radians;
    }

    /**
     * Returns the {@link Rotation#radians radians} of this object,
     * in degrees.
     * 
     * @return {@code float} degrees
     */
    public float getDegrees() {
        return Mathk.toDegrees(radians);
    }

    /**
     * Returns a {@link Vector2k} representing the direction of this
     * {@link Rotation object}.
     * 
     * @return {@link Vector2k} the direction
     */
    public Vector2k getDirection() {
        return new Vector2k(Mathk.cos(radians), Mathk.sin(radians));
    }

    /**
     * Returns the {@link Rotation#u rotation matrix} of this object.
     * 
     * @return {@link Matrix2k} rotation matrix
     */
    public Matrix2k getMatrix() {
        return u;
    }

    /**
     * Compares this {@link Rotation object} to the given
     * {@link Rotation object} by comparing their
     * {@link Rotation#radians radians}.
     * 
     * @param {@link Rotation} rotation object
     * @return {@code int} the comparison result
     */
    @Override
    public int compareTo(Rotation o) {
        return Float.compare(radians, o.radians);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Rotation [rotation=");
        builder.append(radians);
        builder.append(", u=");
        builder.append(u);
        builder.append("]");
        return builder.toString();
    }

}

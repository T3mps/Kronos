package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.scene.component.TransformComponent;

/**
 * Represents a 2D transform object. This is used to represent the position and
 * rotation of an object in the world. The position is held in a {@link Vector2k},
 * and the rotation is held in its own {@link Rotation} object. tl;dr: the rotation
 * object holds the rotation in radians, and the rotation matrix. The matrix is used
 * to rotate a {@link Vector2k vector}.
 * 
 * @author Ethan Temprovich
 * @see TransformComponent
 * @see Rotation
 */
public class Transform implements Cloneable {

    /** The identity instance. */
    public static final Transform IDENTITY = new Transform().identity();
    
    /** The position vector. */
    protected final Vector2k position;

    /** The {@link Rotation} object. */
    protected final Rotation rotation;

    /**
     * Default constructor.
     */
    public Transform() {
        this(new Vector2k(0f), new Rotation());
    }

    /**
     * Constructor with position.
     * 
     * @param position the position vector.
     */
    public Transform(Vector2k position) {
        this(position, new Rotation());
    }

    /**
     * Constructor with position and rotation.
     * 
     * @param x the x position.
     * @param y the y position.
     * @param rotation the radians of rotation.
     */
    public Transform(float x, float y, float rotation) {
        this(new Vector2k(x, y), new Rotation(rotation));
    }

    /**
     * Constructor with position and rotation.
     * 
     * @param position the position vector.
     * @param rotation the radians of rotation.
     */
    public Transform(Vector2k position, float rotation) {
        this(position, new Rotation(rotation));
    }

    /**
     * Constructor with position and rotation.
     * 
     * @param position the position vector.
     * @param rotation the {@link Rotation} object.
     */
    public Transform(Vector2k position, Rotation rotation) {
        super();
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Copy constructor.
     */
    public Transform(Transform transform) {
        this(transform.position, transform.rotation);
    }

    /**
     * Sets this transform to the given {@link Transform}.
     * 
     * @param transform the {@link Transform} to set {@code this} to.
     */
    public void set(Transform transform) {
        this.position.set(transform.position);
        this.rotation.set(transform.rotation);
    }

    /**
     * Sets this transform to the identity.
     * 
     * @return this
     */
    public Transform identity() {
        position.set(0f, 0f);
        rotation.identity();
        return this;
    }

    /**
     * Translates this transform by {@code (x, y)}, and rotates it by
     * the given radians.
     * 
     * @param x the x translation.
     * @param y the y translation.
     * @param r the radians to rotate by.
     */
    public void transform(float x, float y, float r) {
        position.addi(x, y);
        rotation.set(r);
    }

    /**
     * Translates this transform by {@code (x, y)}.
     * 
     * @param x the x translation.
     * @param y the y translation.
     */
    public void translate(float x, float y) {
        position.addi(x, y);
    }

    /**
     * Translates this transform by the given
     * {@link Vector2k translation vector}.
     * 
     * @param translation the {@link Vector2k} to translate by.
     */
    public void translate(Vector2k translation) {
        position.addi(translation);
    }

    /**
     * Rotates this transform by the given radians.
     * 
     * @param radians the radians to rotate by.
     */
    public void rotate(float radians) {
        rotation.rotate(radians);
    }

    public void setRotation(float radians) {
        rotation.set(radians);
    }
    
    /**
     * Rotates this transform by the given radians, around
     * the given {@link Vector2k point}.
     * 
     * @param point the {@link Vector2k} to rotate around.
     * @param radians the radians to rotate by.
     */
    public void rotateAround(Vector2k point, float radians) {
        rotation.rotate(radians);
        position.subi(point);
        rotation.set(radians);
        position.addi(point);
    }

    /**
     * Transforms the given point by this transform, and returns it.
     * The {@link Vector2k point} is modified.
     * 
     * @param point the {@link Vector2k} to transform.
     * @return the transformed {@link Vector2k}.
     */
    public Vector2k transformPoint(Vector2k point) {
        return rotation.rotatePoint(point).addi(position);
    }

    /**
     * Returns the point, transformed by this transform. The
     * {@link Vector2k point} is not modified.
     * 
     * @param point the {@link Vector2k} to transform.
     * @return the transformed {@link Vector2k}.
     */
    public Vector2k getTransformed(Vector2k point) {
        Vector2k out = rotation.getRotatedPoint(point);
        out.addi(position);
        return out;
    }

    /**
     * Transforms the given point by this transforms inverse, and
     * returns it. The {@link Vector2k point} is modified.
     * 
     * @param point the {@link Vector2k} to transform.
     * @return the transformed {@link Vector2k}.
     */
    public Vector2k inverseTransformPoint(Vector2k point) {
        return rotation.inverseRotatePoint(point.subi(position));
    }

    /**
     * Returns the point, transformed by this transforms inverse. The
     * {@link Vector2k point} is not modified.
     * 
     * @param point the {@link Vector2k} to transform.
     * @return the transformed {@link Vector2k}.
     */
    public Vector2k getInverseTransformed(Vector2k point) {
        Vector2k out = rotation.getInverseRotatedPoint(point.subi(position));
        return out;
    }

    /**
     * Translates the given point by this transforms position, and
     * returns it. The {@link Vector2k point} is modified.
     * 
     * @param point the {@link Vector2k} to translate.
     * @return the translated {@link Vector2k}.
     */
    public Vector2k translatePoint(Vector2k point) {
        return point.addi(position);
    }

    /**
     * Returns the point, translated by this transforms position. The
     * {@link Vector2k point} is not modified.
     * 
     * @param point the {@link Vector2k} to translate.
     * @return the translated {@link Vector2k}.
     */
    public Vector2k getTranslated(Vector2k point) {
        Vector2k out = new Vector2k();
        point.add(position, out);
        return out;
    }

    /**
     * Rotates the given point by this transforms rotation, and
     * returns it. The {@link Vector2k point} is modified.
     * 
     * @param point the {@link Vector2k} to rotate.
     * @return the rotated {@link Vector2k}.
     */
    public Vector2k rotatePoint(Vector2k point) {
        return rotation.rotatePoint(point);
    }

    /**
     * Returns the point, rotated by this transforms rotation. The
     * {@link Vector2k point} is not modified.
     * 
     * @param point the {@link Vector2k} to rotate.
     * @return the rotated {@link Vector2k}.
     */
    public Vector2k getRotated(Vector2k point) {
        return rotation.getRotatedPoint(point);
    }

    /**
     * Rotates this point by this transforms inverse rotation, and
     * returns it. The {@link Vector2k point} is modified.
     * 
     * @param point the {@link Vector2k} to rotate.
     * @return the rotated {@link Vector2k}.
     */
    public Vector2k inverseRotate(Vector2k point) {
        return rotation.inverseRotatePoint(point);
    }

    /**
     * Returns the point, rotated by this transforms inverse rotation. The
     * {@link Vector2k point} is not modified.
     * 
     * @param point the {@link Vector2k} to rotate.
     * @return the rotated {@link Vector2k}.
     */
    public Vector2k getInverseRotated(Vector2k point) {
        return rotation.getInverseRotatedPoint(point);
    }

    public void lerp(Vector2k dp, float da, float alpha) {
        rotate(da * alpha);
        translate(dp.x * alpha, dp.y * alpha);
    }

    public void lerp(Transform target, float alpha) {
        float x = (1f - alpha) * position.x + alpha * target.position.x;
        float y = (1f - alpha) * position.y + alpha * target.position.y;
        float r = getRadians();
        float r2 = target.getRadians();
        float diff = r2 - r;

        // clamp it
        if (diff < -Mathk.PI) diff += Mathk.RADIAN_DOMAIN;
        if (diff > Mathk.PI) diff -= Mathk.RADIAN_DOMAIN;

        r += diff * alpha;

        rotation.set(r);
        position.set(x, y);
    }

    public Transform getLerped(Transform target, float alpha) {
        Transform out = new Transform();
        out.position.set(position);
        out.rotation.set(rotation);
        out.lerp(target, alpha);
        return out;
    }

    public Transform getLerped(Vector2k dp, float da, float alpha) {
        Transform out = new Transform();
        out.position.set(position);
        out.rotation.set(rotation);
        out.lerp(dp, da, alpha);
        return out;
    }

    /**
     * @return the {@link Vector2k} position of this transform.
     */
    public Vector2k getPosition() {
        return position;
    }

    public void setPosition(Vector2k position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    /**
     * @return the {@link Rotation} rotation object of this transform.
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * @return the radians of this transforms rotation.
     */
    public float getRadians() {
        return rotation.getRadians();
    }

    /**
     * @return the degrees of this transforms rotation.
     */
    public float getDegrees() {
        return rotation.getDegrees();
    }

    @Override
    public Transform clone() {
        return new Transform(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Transform [position=");
        builder.append(position);
        builder.append(", rotation=");
        builder.append(rotation);
        builder.append("]");
        return builder.toString();
    }

}

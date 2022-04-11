package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.math.Intervalf;
import net.acidfrog.kronos.math.Vector2k;

/**
 * An base implementation of a {@link Shape}. It is recommended
 * that all {@link Shape}s extend this class. All {@link Shape}s
 * have a center point and radius (distance from the center to
 * the farthest vertex). This class also overwrites many of the
 * {@link Shape}s methods, which may not need to be overridden
 * in every {@link Shape} implementation. Of course, if needed,
 * you can {@link Override @Override} the methods in this class.
 * 
 * @author Ethan Temprovich
 */
public sealed abstract class AbstractShape implements Shape permits Circle, Polygon {

    /** The center point. */
    protected Vector2k center;

    /** The distance from the {@link #center} to the farthest vertex. */
    protected float radius;

    /**
     * Default constructor.
     */
    public AbstractShape() {
        this.center = new Vector2k(0f);
        this.radius = 0f;
    }

    /**
     * Constructs this {@link Shape} with the given radius, and a center
     * point of ({@link Vector2k#ZERO 0, 0}).
     * 
     * @param {@code float} the radius
     */
    public AbstractShape(float radius) {
        this.center = new Vector2k(0f);
        this.radius = radius;
    }

    /**
     * Constructs this {@link Shape} with the given center point and radius.
     * 
     * @param {@link Vector2k} the center point
     * @param {@code float} the radius
     */
    public AbstractShape(Vector2k center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    /** default implementation */
    @Override
    public void rotate(float angle) {
        this.rotate(angle, Vector2k.ZERO);
    }

    /** default implementation */
    @Override
    public void rotate(float angle, Vector2k point) {
        if (!center.equals(point)) this.center.rotate(angle, point);
    }

    /** default implementation */
    @Override
    public Intervalf project(Vector2k axis) {
        return this.project(axis, Transform.IDENTITY);
    }

    /** default implementation */
    @Override
    public boolean contains(Vector2k point) {
        return this.contains(point, Transform.IDENTITY);
    }

    /**
     * Returns the {@link Vector2k center point} of this {@link Shape}.
     * 
     * @return {@link Vector2k} the center point
     */
    @Override
    public Vector2k getCenter() {
        return center;
    }

    /**
     * Returns the {@code radius} of this {@link Shape}.
     * 
     * @return {@code float} the radius
     */
    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AbstractShape [center=");
        builder.append(center);
        builder.append(", radius=");
        builder.append(radius);
        builder.append("]");
        return builder.toString();
    }

}

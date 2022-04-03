package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.math.Vector2k;

/**
 * Holds the result of a raycast.
 * 
 * @author Ethan Temprovich
 */
public final class RaycastResult {

    /** The point of intersection. */
    private final Vector2k point;

    /** The {@link #point intersection} normal.*/
    private final Vector2k normal;

    /** Length along the ray at which intersection occured {@code (t | tmin)}. */
    private float distance;

    /**
     * Default constructor.
     */
    public RaycastResult() {
        this(new Vector2k(0f), new Vector2k(0f), 0f);
    }

    /**
     * Constructor with point, normal and distance.
     * 
     * @param point the point of intersection.
     * @param normal the {@link #point intersection} normal.
     * @param distance the length along the ray at which intersection occured
     */
    public RaycastResult(Vector2k point, Vector2k normal, float distance) {
        this.point = point.clone();
        this.normal = normal.clone();
        this.distance = distance;
    }

    /**
     * Sets this raycast result to the given values.
     * 
     * @param point the new point of intersection.
     * @param normal the new {@link #point intersection} normal.
     * @param distance the new length along the ray at which intersection occured
     */
    public void set(Vector2k point, Vector2k normal, float distance) {
        this.point.set(point);
        this.normal.set(normal);
        this.distance = distance;
    }

    /**
     * Clears this raycast result.
     */
    public void clear() {
        this.point.zero();
        this.normal.zero();
        this.distance = 0f;
    }

    /**
     * @return the point of intersection.
     */
    public Vector2k getPoint() {
        return point;
    }

    /**
     * Sets the point of intersection to the given value.
     * 
     * @param point the new point of intersection.
     */
    public void setPoint(Vector2k point) {
        this.point.set(point);
    }

    /**
     * @return the {@link #point intersection} normal.
     */
    public Vector2k getNormal() {
        return normal;
    }

    /**
     * Sets the {@link #point intersection} normal to the given value.
     * 
     * @param normal the new {@link #point intersection} normal.
     */
    public void setNormal(Vector2k normal) {
        this.normal.set(normal);
    }

    /**
     * @return the length along the ray at which intersection occured {@code (t | tmin)}.
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Sets the length along the ray at which intersection occured {@code (t | tmin)}.
     * 
     * @param distance the new length along the ray at which intersection occured
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Raycast [distance=");
        builder.append(distance);
        builder.append(", normal=");
        builder.append(normal);
        builder.append(", point=");
        builder.append(point);
        builder.append("]");
        return builder.toString();
    }
    
}

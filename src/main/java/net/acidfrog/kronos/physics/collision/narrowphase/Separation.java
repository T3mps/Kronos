package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.mathk.Vector2k;
import net.acidfrog.kronos.physics.geometry.Collider;

/**
 * Represents separation between two shapes.
 * 
 * @author Ethan Temprovich
 */
public class Separation {

    /** The closest point on the first {@link Collider}. */
    private final Vector2k pointA;

    /** The closest point on the second {@link Collider}. */
    private final Vector2k pointB;
    
    /** The direction from the first {@link Collider} to the second. */
    private final Vector2k normal;

    /** The distance between the two {@link Collider colliders}. */
    private float distance;

    /**
     * Default constructor.
     */
    public Separation() {
        this( new Vector2k(0f), new Vector2k(0f), new Vector2k(0f), 0f);
    }

    /**
     * Constructor with two points, normal and distance.
     * 
     * @param pointA the closest point on the first {@link Collider}.
     * @param pointB the closest point on the second {@link Collider}.
     * @param normal the direction from the first {@link Collider} to the second.
     * @param distance the distance between the two {@link Collider colliders}.
     */
    public Separation(Vector2k pointA, Vector2k pointB, Vector2k normal, float distance) {
        this.pointA = pointA.clone();
        this.pointB = pointB.clone();
        this.normal = normal.clone();
        this.distance = distance;
    }

    /**
     * Sets this separations values to the given values.
     * 
     * @param pointA the closest point on the first {@link Collider}.
     * @param pointB the closest point on the second {@link Collider}.
     * @param normal the direction from the first {@link Collider} to the second.
     * @param distance the distance between the two {@link Collider colliders}.
     */
    public void set(Vector2k pointA, Vector2k pointB, Vector2k normal, float distance) {
        this.pointA.set(pointA);
        this.pointB.set(pointB);
        this.normal.set(normal);
        this.distance = distance;
    }

    /**
     * Sets this separations values to the given {@link Separation separation} values.
     * 
     * @param separation the separation to copy values from.
     */
    public void set(Separation separation) {
        this.pointA.set(separation.pointA);
        this.pointB.set(separation.pointB);
        this.normal.set(separation.normal);
        this.distance = separation.distance;
    }

    /**
     * Clears this separations values.
     */
    public void clear() {
        this.normal.zero();
        this.distance = 0f;
        this.pointA.zero();
        this.pointB.zero();
    }

    /**
     * @return the closest point on the first {@link Collider}.
     */
    public Vector2k getPointA() {
        return pointA;
    }

    /**
     * Set pointA.
     */
    public void setPointA(Vector2k pointA) {
        this.pointA.set(pointA);
    }
    
    /**
     * @return the closest point on the second {@link Collider}.
     */
    public Vector2k getPointB() {
        return pointB;
    }

    /**
     * Set pointB.
     */
    public void setPointB(Vector2k pointB) {
        this.pointB.set(pointB);
    }

    /**
     * @return the direction from the first {@link Collider} to the second.
     */
    public Vector2k getNormal() {
        return normal;
    }

    /**
     * Set normal.
     */
    public void setNormal(Vector2k normal) {
        this.normal.set(normal);
    }

    /**
     * @return the distance between the two {@link Collider colliders}.
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Set distance.
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Separation [distance=");
        builder.append(distance);
        builder.append(", normal=");
        builder.append(normal);
        builder.append(", pointA=");
        builder.append(pointA);
        builder.append(", pointB=");
        builder.append(pointB);
        builder.append("]");
        return builder.toString();
    }

}

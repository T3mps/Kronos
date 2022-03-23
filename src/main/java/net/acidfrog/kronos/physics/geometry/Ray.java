package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.error.KronosGeometryError;
import net.acidfrog.kronos.mathk.Vector2k;

/**
 * Represents a ray. A ray is a line segment with a {@link Vector2k starting}
 * point and an normalised {@link Vector2k direction vector}.
 * 
 * @author Ethan Temprovich
 */
public class Ray {

    /** The start point */
    private Vector2k start;

    /** The direction which the {@link Ray} is cast */
    private Vector2k direction;
    private Vector2k inverseDirection;

    /**
     * Creates a new ray from the {@link Vector2k#ZERO origin}, with the given
     * {@code radians}.
     * 
     * @param {@code float} the radians of the {@link Vector2k direction}
     */
    public Ray(float radians) {
        this(new Vector2k(radians));
    }
    
    /**
     * Creates a new ray with the given {@link Vector2k start point} and
     * the {@ Vector2k direction} in {@code radians}.
     * 
     * @param {@link Vector2k} the {@link Ray#start start} point
     * @param {@code float} the radians of the {@link Ray#direction direction}
     */
    public Ray(Vector2k start, float radians) {
        this(start, new Vector2k(radians));
    }

    /**
     * Creates a new ray from the {@link Vector2k#ZERO origin}, with the given
     * {@link Vector2k direction}.
     * 
     * @param direction
     */
    public Ray(Vector2k direction) {
        this(Vector2k.ZERO, direction);
    }

    /**
     * Full constructor.
     * Creates a new ray with the given {@link Vector2k start point} and
     * {@link Vector2k direction}.
     * 
     * @param start
     * @param direction
     */
    public Ray(Vector2k start, Vector2k direction) {
        if (direction.isZero()) throw new KronosGeometryError(KronosErrorLibrary.RAY_ZERO_DIRECTION);
        this.start = start;
        this.direction = direction.normalized();
        this.inverseDirection = new Vector2k(1 / this.direction.x, 1 / this.direction.y);
    }

    /**
     * Returns the {@link Vector2k start point} of this {@link Ray}.
     * 
     * @return {@link Ray#start start}
     */
    public Vector2k getStart() {
        return start;
    }
    
    /**
     * Returns the angle of the this {@link Ray}s {@link Vector2k direction}
     * in {@code radians}.
     * 
     * @return {@code float} the angle of the {@link Ray#direction direction}
     */
    public float getDirection() {
        return direction.angleRad();
    }

    /**
     * Returns the {@link Vector2k direction vector} of this {@link Ray}.
     * 
     * @return {@link Ray#direction direction}
     */
    public Vector2k getDirectionVector() {
        return direction;
    }

    /**
     * Returns the {@link Vector2k inverse direction vector} of this {@link Ray}.
     * 
     * @return {@link Ray#inverseDirection inverseDirection}
     */
    public Vector2k getInverseDirectionVector() {
        return inverseDirection;
    }

}

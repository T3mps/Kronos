package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.math.Intervalf;
import net.acidfrog.kronos.math.Vector2k;

/**
 * A 2D, geometric representation for regular forms of different objects.
 * 
 * @author Ethan Temprovich
 */
public sealed interface Shape permits AbstractShape, Collider {

    /**
     * Rotates this {@link Shape} by the given angle.
     * 
     * @param angle the angle to rotate by.
     */
    public abstract void rotate(float angle);

    /**
     * Rotates this {@link Shape} by the given angle, around the given
     * {@link Vector2k point}.
     * 
     * @param angle the angle to rotate by.
     * @param point the point to rotate around.
     */
    public abstract void rotate(float angle, Vector2k point);

    /**
     * Projects this {@link Shape} onto the given {@link Vector2k axis}.
     * 
     * @param axis the {@link Vector2k axis} to project onto.
     * @return the {@link Intervalf} representing the projection.
     */
    public abstract Intervalf project(Vector2k axis);

    /**
     * Projects this {@link Shape} onto the given {@link Vector2k axis},
     * first transforming it by the given {@link Transform}.
     * 
     * @param axis the {@link Vector2k axis} to project onto.
     * @param transform the {@link Transform} to apply before projecting.
     * @return the {@link Intervalf} representing the projection.
     */
    public abstract Intervalf project(Vector2k axis, Transform transform);
    
    /**
     * Determines if the given {@link Shape} contains the given
     * {@link Vector2k point}.
     * 
     * @param point the {@link Vector2k point} to check.
     * @return {@code true} if the {@link Shape} contains the {@link Vector2k point}, {@code false} otherwise.
     */
    public abstract boolean contains(Vector2k point);

    /**
     * Determines if the given {@link Shape} contains the given
     * {@link Vector2k point}, first transformed by the given
     * {@link Transform}.
     * 
     * @param point the {@link Vector2k point} to check.
     * @return {@code true} if the {@link Shape} contains the {@link Vector2k point}, {@code false} otherwise.
     */
    public abstract boolean contains(Vector2k point, Transform transform);

    /**
     * Returns the tightest fitting {@link AABB} for this {@link Shape},
     * transformed by the given {@link Transform}.
     * 
     * @param transform the {@link Transform} to apply before getting the {@link AABB}.
     * @return the {@link AABB} for this {@link Shape}.
     */
    public abstract AABB computeAABB(Transform transform);

    /**
     * @return the {@code area} of this {@link Shape}.
     */
    public abstract float getArea();

    /**
     * @return the {@code perimeter} of this {@link Shape}.
     */
    public abstract float getPerimeter();

    /**
     * @return the {@code center} of the shape in model space.
     */
    public abstract Vector2k getCenter();

    /**
     * @return the {@code radius} of this {@link Shape}.
     */
    public abstract float getRadius();

}

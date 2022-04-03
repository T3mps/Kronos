package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.core.util.Validatable;
import net.acidfrog.kronos.math.Vector2k;

/**
 * Represents a {@link Shape} that can be used for inputs to methods present in
 * {@link net.acidfrog.kronos.physics.collision.narrowphase.NarrowphaseDetector narrowphase detectors},
 * {@link net.acidfrog.kronos.physics.collision.narrowphase.RaycastDetector raycast detectors}, and
 * {@link net.acidfrog.kronos.physics.collision.narrowphase.DistanceDetector distance detectors}.
 * The {@link Shape} is convex, and has methods for calculating the {@link #computeMass  mass, moment of inertia},
 * {@link #getAxes axes}, alongside other properties.
 * 
 * @author Ethan Temprovich
 */
public sealed interface Collider extends Shape, Validatable permits Circle, Polygon, Capsule, Segment {

    /**
     * Computes the axes of this {@link Collider}. The axes are the
     * {@link Vector2k unit vectors} that are perpendicular to the edges of the
     * {@link Collider}. The given {@link Transform} is applied to {@link Collider}
     * before computing the axes.
     * 
     * @param {@link Vector2k}[] the foci of the {@link Collider}
     * @param {@link Transform} the transform to apply
     * @return {@link Vector2k}[] the axes of this {@link Collider}
     */
    public abstract Vector2k[] getAxes(Vector2k[] foci, Transform transform);
    
    /**
     * Calculates the focal points of curved {@link Collider}s. The
     * {@link Transform} is applied to the {@link Collider} before calculating
     * the focal points.
     * 
     * @param {@link Transform} the transform to apply
     * @return {@link Vector2k}[] the focal points of this {@link Collider}
     */
    public abstract Vector2k[] getFoci(Transform transform);

    /**
     * Calculates the {@link Mass} of this {@link Collider} using the specified
     * {@code density}. The {@link Mass#mass mass} and {@link Mass#inertia inertia}
     * are calculated once because a {@link Collider} represents a non-deforming,
     * static object. If the {@link Mass#mass mass} never changes, the
     * {@link Mass#inertia inertia} won't change either for our purposes.
     * 
     * @param {@code float} the density of the {@link Collider}
     * @return {@link Mass} the mass of this {@link Collider}
     */
    public abstract Mass computeMass(float density);

    /**
     * Returns the farthest {@link Feature} of this {@link Collider} along the
     * given {@link Vector2k direction}. The {@link Transform} is applied to the
     * {@link Collider} before calculating the {@link Feature}.
     * 
     * @param {@link Vector2k} the direction to check
     * @param {@link Transform} the transform to apply
     * @return {@link Feature} the farthest feature
     */
    public abstract Feature getFarthestFeature(Vector2k direction, Transform transform);

    /**
     * Returns the farthest {@link Vector2k vertex} of this {@link Collider} along
     * the given {@link Vector2k direction}. The {@link Transform} is applied to
     * the {@link Collider} before calculating the farthest {@link Vector2k point}.
     * 
     * @param {@link Vector2k} the direction to check
     * @param {@link Transform} the transform to apply
     * @return {@link Vector2k} the farthest point
     */
    public abstract Vector2k getFarthestPoint(Vector2k direction, Transform transform);

}

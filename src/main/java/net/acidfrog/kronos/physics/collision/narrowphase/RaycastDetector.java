package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Ray;
import net.acidfrog.kronos.physics.geometry.Transform;

/**
 * A raycast detector is used to detect intersections between a {@link Ray ray} and a {@link Collider collider}.
 * 
 * @author Ethan Temprovich
 */
public sealed interface RaycastDetector permits GJK {
    
    /**
     * Performs a raycast against the given {@link Collider collider}, using the given
     * {@link Ray ray}. The ray can be limited to a certain distance, or 0 for no limit.
     * The {@link Collider colldier} is first transformed by the given {@link Transform transform}.
     * 
     * @param ray the {@link Ray ray} to cast.
     * @param maxDistance the maximum distance to cast the ray.
     * @param shape the {@link Collider collider} to cast against.
     * @param transform the {@link Collider collider}s {@link Transform transform}.
     * @param result the {@link RaycastResult raycast result} object.
     * @return {@code true} if the ray intersects the collider, {@code false} otherwise.
     */
    public abstract boolean raycast(Ray ray, float maxDistance, Collider shape, Transform transform, /** out */ RaycastResult result);

}

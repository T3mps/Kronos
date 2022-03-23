package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Transform;

/**
 * A narrowphase detector is used to detect collisions between two {@link Collider colliders}.
 * 
 * @author Ethan Temprovich
 */
public sealed interface NarrowphaseDetector permits SAT {

    /**
     * Detects the collision between two {@link Collider colliders}. They are first
     * transformed by their respective {@link Transform transforms}.
     * 
     * @param colliderA the first {@link Collider collider}.
     * @param transformA the first colliders {@link Transform transform}.
     * @param colliderB the second {@link Collider collider}.
     * @param transformB the second colliders {@link Transform transform}.
     * @return {@code true} if the two colliders are colliding, {@code false} otherwise.
     */
    public abstract boolean detect(Collider colliderA, Transform transformA, Collider colliderB, Transform transformB);
    
    /**
     * Detects the collision between two {@link Collider colliders}. They are first
     * transformed by their respective {@link Transform transforms}. This method takes
     * in a {@link Penetration penetration} object to store the collision data.
     * 
     * @param colliderA the first {@link Collider collider}.
     * @param transformA the first colliders {@link Transform transform}.
     * @param colliderB the second {@link Collider collider}.
     * @param transformB the second colliders {@link Transform transform}.
     * @param penetration the {@link Penetration penetration} object
     * @return {@code true} if the two colliders are colliding, {@code false} otherwise.
     */
    public abstract boolean detect(Collider colliderA, Transform transformA, Collider colliderB, Transform transformB, /** out */ Penetration penetration);

    /**
     * Determines if the first {@link Collider collider} contains the second.
     * 
     * @param colliderA the first {@link Collider collider}.
     * @param transformA the first colliders {@link Transform transform}.
     * @param colliderB the second {@link Collider collider}.
     * @param transformB the second colliders {@link Transform transform}.
     * @return {@code true} if the first collider contains the second, {@code false} otherwise.
     */
    public abstract boolean contains(Collider colliderA, Transform transformA, Collider colliderB, Transform transformB);
    
}

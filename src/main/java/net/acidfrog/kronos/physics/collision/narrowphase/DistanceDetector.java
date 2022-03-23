package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Transform;

/**
 * A distance detector is used to detect the distance between two colliders.
 * 
 * @author Ethan Temprovich
 */
public sealed interface DistanceDetector permits GJK {

    /**
     * Determines if the two {@link Collider colliders} are separated. Stores the
     * separation data in the given {@link Separation separation} object.
     * 
     * @param shapeA the first {@link Collider collider}.
     * @param transformA the first colliders {@link Transform transform}.
     * @param shapeB the second {@link Collider collider}.
     * @param transformB the second colliders {@link Transform transform}.
     * @param separation the {@link Separation separation} object
     * @return
     */
    public boolean distance(Collider shapeA, Transform transformA, Collider shapeB, Transform transformB, /** out */ Separation separation);
    
}

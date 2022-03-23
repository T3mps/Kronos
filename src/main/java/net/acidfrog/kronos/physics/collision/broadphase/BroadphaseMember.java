package net.acidfrog.kronos.physics.collision.broadphase;

import net.acidfrog.kronos.physics.geometry.AABB;

/**
 * Defines the qualifications for an object to be considered for the
 * {@link BroadphaseDetector broadphase}. The only prerequisite is that the
 * object must have an way to generate an {@link AABB}. {@link #computeAABB()}
 * is the provided method to do just that.
 * 
 * @author Ethan Temprovich
 */
public interface BroadphaseMember {

    /**
     * Returns the {@link AABB} for this object.
     * 
     * @return the {@link AABB} for this object.
     */
    public abstract AABB getBounds();
    
}

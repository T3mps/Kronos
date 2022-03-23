package net.acidfrog.kronos.physics.collision.broadphase;

import net.acidfrog.kronos.physics.geometry.AABB;

/**
 * A policy for actions during the {@link BroadphaseDetector broadphase}.
 * 
 * @author Ethan Temprovich
 */
public sealed interface BroadphasePolicy permits AABBPolicy {
    
    /**
     * Enforces the given rules that pertain to the given {@link AABB}.
     * 
     * @param t the {@link BroadphaseMember parent}.
     * @param aabb the {@link AABB} to expand.
     */
    public abstract void enforce(AABB aabb);

}

package net.acidfrog.kronos.physics.collision.broadphase;

import net.acidfrog.kronos.physics.geometry.AABB;

/**
 * Static implementation of the {@link AABBPolicy} interface for the
 * {@link DynamicAABBTree Dynamic AABB Tree}. This policy is used to inflate the
 * {@link AABB} of a {@link BroadphaseMember member} by a set amount.
 * 
 * @author Ethan Temprovich
 */
public final class StaticAABBPolicy extends AABBPolicy {

    /**
     * Default constructor.
     */
    public StaticAABBPolicy() {
        this.expansion = 0f;
    }

    /**
     * Constructor with expansion amount.
     * 
     * @param amount the amount to expand the {@link AABB} by.
     */
    public StaticAABBPolicy(float amount) {
        this.expansion = amount;
    }

    /**
     * Expands the given {@link AABB} by the expansion amount.
     * 
     * @param aabb the {@link AABB} to expand.
     */
    @Override
    public void enforce(AABB aabb) {
        if (expansion <= DEFAULT_EXPANSION_FACTOR) return;
        
        aabb.expand(expansion);
    }
    
}

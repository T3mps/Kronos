package net.acidfrog.kronos.physics.collision.broadphase;

import net.acidfrog.kronos.math.Mathk;

public abstract non-sealed class AABBPolicy implements BroadphasePolicy {

    /** Default amount to expand an {@link AABB} during the broadphase. */
    protected static final float DEFAULT_EXPANSION_FACTOR = Mathk.log2(Mathk.E) / (Mathk.PI2 * Mathk.sqrt(Mathk.E));

    /** The amount to alter an {@link AABB} during the broadphase. */
    protected float expansion;

    /**
     * Default constructor.
     */
    public AABBPolicy() {}

    /**
     * Constructor with expansion augment factor.
     * 
     * @param expansion the amount to expand the {@link AABB} by.
     */
    public AABBPolicy(float expansion) {
        this.expansion = expansion;
    }

    /**
     * Returns the expansion amount.
     * 
     * @return the expansion amount.
     */
    public float getExpansion() {
        return expansion;
    }

}

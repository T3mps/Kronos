package net.acidfrog.kronos.physics.collision.broadphase;

import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.physics.geometry.AABB;

/**
 * Dynamic implementation of the {@link AABBPolicy} interface for the
 * {@link DynamicAABBTree Dynamic AABB Tree}. This policy is used to inflate the
 * {@link AABB} of a {@link BroadphaseMember member} based on the extends of
 * the {@link AABB}, and an expansion factor.
 * 
 * @author Ethan Temprovich
 */
public final class DynamicAABBPolicy extends AABBPolicy {

    /** Cached value. Optional. */
    private float cache = 0xff;

    public DynamicAABBPolicy() {
        this(DEFAULT_EXPANSION_FACTOR);
    }

    /**
     * Default constructor.
     */
    public DynamicAABBPolicy(float expansion) {
        this.expansion = expansion < -1f ? -1f :
                         expansion >  1f ?  1f :
                         expansion;
    }

    public DynamicAABBPolicy(AABB aabb) {
        this(DEFAULT_EXPANSION_FACTOR);
        this.cache = Mathk.max(aabb.getWidth(), aabb.getHeight()) * expansion;
    }

    public DynamicAABBPolicy(float expansion, AABB aabb) {
        this(expansion);
        this.cache = Mathk.max(aabb.getWidth(), aabb.getHeight()) * expansion;
    }

    /**
     * Expands the given {@link AABB} by the expansion amount.
     * 
     * @param aabb the {@link AABB} to expand.
     */
    @Override
    public void enforce(AABB aabb) {
        if (cache != 0xff) {
            aabb.expand(cache);
            return;
        }
        float r = Mathk.max(aabb.getWidth(), aabb.getHeight()) * expansion;
        aabb.expand(r);
    }
    
}


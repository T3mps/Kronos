package net.acidfrog.kronos.physics.collision.narrowphase.manifold;

import net.acidfrog.kronos.core.lang.annotations.Out;

public interface ManifoldSolver {

    public void solve(@Out Manifold manifold);
    
}

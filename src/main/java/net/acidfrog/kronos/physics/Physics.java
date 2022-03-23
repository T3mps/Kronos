package net.acidfrog.kronos.physics;

import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.physics.collision.narrowphase.DistanceDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.NarrowphaseDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.RaycastDetector;

/**
 * Static class for physics calculations.
 * 
 * @author Ethan Temprovich
 */
public final class Physics {

    /** The max number of allowed iterations for a {@link NarrowphaseDetector narrowphase} algorithm. */
    public static final int MAX_NARROWPHASE_DETECT_ITERATIONS = 32;

    /** The max number of allowed iterations for a {@link RaycastDetector raycast} algorithm. */
    public static final int MAX_RAYCAST_DETECT_ITERATIONS = 32;

    /** The max number of allowed iterations for a {@link ContinuousImpactDetector continuous detection} algorithm. */
    public static final int MAX_CONTINUOUS_DETECT_ITERATIONS = 32;

    /** The precision for {@link DistanceDetector distance} calculations. */
    public static final float DISTANCE_EPSILON = (float) Mathk.sqrt(Mathk.Precision.MACHINE_EPSILON);

    /** The precision for {@link RaycastDetector raycast} calculations. */
    public static final float RAYCAST_EPSILON = (float) Mathk.sqrt(Mathk.Precision.MACHINE_EPSILON);

    public static class Precision {
        
    }
    
}

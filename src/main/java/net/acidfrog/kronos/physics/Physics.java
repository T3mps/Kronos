package net.acidfrog.kronos.physics;

import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
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

    public static final Vector2k GRAVITY 			     = 	 new Vector2k(0f, -981f);

    public static final float    DT 					 =   1f / 60f;
    public static final float    RESTING 			     =   GRAVITY.mul(DT).magnitudeSquared() + (float) Mathk.Precision.MACHINE_EPSILON;
	public static final float    PENETRATION_ALLOWANCE   = 	 0.0055f; // slop
	public static final float    PENETRATION_CORRETION   = 	 0.33f;   // percent

    public static class Precision {
        
    }
    
}

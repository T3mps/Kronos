package net.acidfrog.kronos.physics.collision.broadphase;

import net.acidfrog.kronos.physics.collision.CollisionPair;

/**
 * Holds a pair of objects that are determined to be 
 * colliding during the {@link BroadphaseDetector broadphase}.
 * 
 * @author Ethan Temprovich
 */
public final class BroadphasePair<T extends BroadphaseMember> implements CollisionPair<T> {

    /** The first object. */
    public T a;

    /** The second object. */
    public T b;

    /**
     * Default constructor.
     */
    public BroadphasePair() {}

    /**
     * Constructor with specified objects.
     * 
     * @param a the first object.
     * @param b the second object.
     */
    public BroadphasePair(T a, T b) {
        this.a = a;
        this.b = b;
    }

    /**
     * @return the first object in the pair.
     */
    @Override
    public T getA() {
        return a;
    }

    /**
     * @return the second object in the pair.
     */
    @Override
    public T getB() {
        return b;
    }

}

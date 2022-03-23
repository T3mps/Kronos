package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.physics.world.body.Rigidbody;

/**
 * Represents a mass. A mass is defined, for all intents and purposes,
 * as object that stores a mass, inertia, and pre-computed inverses.
 * The values are calculated once, as the simulated currently handles
 * {@link Rigidbody}s, whos structure is immutable. In turn, we can
 * calculate the inverse values once and cache them.
 * 
 * @author Ethan Temprovich
 */
public final class Mass {
    
    /** The mass and inverse. */
    private final float mass, inverseMass;

    /** The inertia and inverse. */
	private final float inertia, inverseInertia;
    
    /**
     * Default constructor.
     */
    public Mass() {
        this(0f, 0f);
    }

    /**
     * Constructor with mass and inertia.
     * 
     * @param mass the mass.
     * @param inertia the inertia.
     */
    public Mass(float mass, float inertia) {
        this.mass = mass;
        this.inverseMass = mass == 0f ? 0f : 1f / mass;
        this.inertia = inertia;
        this.inverseInertia = inertia == 0f ? 0f : 1f / inertia;
    }

    /**
     * Copy constructor.
     */
    public Mass(Mass mass) {
        this(mass.mass, mass.inertia);
    }

    /**
     * @return if the mass has zero mass and inertia.
     */
    public boolean isZero() {
        return mass == 0f && inertia == 0f;
    }

    /**
     * @return if the mass has infinite mass or inertia.
     */
    public boolean isInfinite() {
        return (mass == Float.POSITIVE_INFINITY
          || inertia == Float.POSITIVE_INFINITY)
          || (mass   == -1
          || inertia == -1);
    }

    /**
     * @return if the mass is finite, or finite and non-zero.
     */
    public boolean isFinite() {
        return !isInfinite() && !isZero();
    }

    /**
     * @return the mass.
     */
    public float getMass() {
        return mass;
    }

    /**
     * @return the precomputed inverse mass.
     */
    public float getInverseMass() {
        return inverseMass;
    }

    /**
     * @return the inertia.
     */
    public float getInertia() {
        return inertia;
    }

    /**
     * @return the precomputed inverse inertia.
     */
    public float getInverseInertia() {
        return inverseInertia;
    }

}

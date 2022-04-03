package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.math.Vector2k;

/**
 * Represents penetration between two shapes.
 * 
 * @author Ethan Temprovich
 */
public final class Penetration {

    /** The collision normal. */
    private final Vector2k normal;

    /** The depth of penetration. */
    private float depth;

    /**
     * Default constructor.
     */
    public Penetration() {
        this(new Vector2k(0f), 0f);
    }

    /**
     * Constructor with normal and depth.
     * 
     * @param normal the collision normal.
     * @param depth the depth of penetration.
     */
    public Penetration(Vector2k normal, float depth) {
        this.normal = normal.clone();
        this.depth = depth;
    }

    /**
     * Sets this penetrations normal and depth to the given values.
     * 
     * @param normal the new collision normal.
     * @param depth the new depth of penetration.
     */
    public void set(Vector2k normal, float depth) {
        this.normal.set(normal);
        this.depth = depth;
    }

    /**
     * Clears this penetrations values.
     */
    public void clear() {
        this.normal.zero();
        this.depth = 0f;
    }

    /**
     * @return the collision normal.
     */
    public Vector2k getNormal() {
        return normal;
    }

    /**
     * Sets the collision normal to the given value.
     * 
     * @param normal the new collision normal.
     */
    public void setNormal(Vector2k normal) {
        this.normal.set(normal);
    }

    /**
     * @return the depth of penetration.
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Sets the depth of penetration to the given value.
     * 
     * @param depth the new depth of penetration.
     */
    public void setDepth(float depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Penetration [depth=");
        builder.append(depth);
        builder.append(", normal=");
        builder.append(normal);
        builder.append("]");
        return builder.toString();
    }

}

package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.math.Vector2k;

/**
 * Represents an indexed vertice of a {@link Collider}.
 * 
 * @author Ethan Temprovich
 */
public final class PointFeature extends Feature {

	/** The vertex */
	final Vector2k point;
	
	/**
	 * Default constructor.
	 */
	PointFeature() {
		this(new Vector2k());
	}

	/**
	 * Exists to allow for curved shapes.
	 * 
	 * @param point
	 */
    @Deprecated
	public PointFeature(Vector2k point) {
		this(point, Feature.NOT_INDEXED);
	}
	
	/**
	 * Creates a new {@link PointFeature} with the given 
	 * {@link Vector2k vertex} and {@code index}.
	 * 
	 * @param point The vertex
	 * @param index The index
	 */
	public PointFeature(Vector2k point, int index) {
		super(index);
		this.point = point;
	}

	/**
	 * Sets this {@link PointFeature}s {@link Vector2k vertex} and
	 * {@code index} to the given values.
	 * 
	 * @param point The vertex
	 * @param index The index
	 */
	public void set(Vector2k point, int index) {
		this.point.set(point);
		this.index = index;
	}
	
	/**
	 * Returns the {@link Vector2k point} represented by this
	 * {@link PointFeature}.
	 * 
	 * @return {@link Vector2k} The point
	 */
	public Vector2k getPoint() {
		return this.point;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PointFeature [point=");
        builder.append(point);
        builder.append("]");
        return builder.toString();
    }

}

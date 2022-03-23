package net.acidfrog.kronos.physics.geometry;

/**
 * Represents an indexed feature of a {@link Collider}.
 * The current implementations are {@link PointFeature}
 * and {@link EdgeFeature}.
 * 
 * @author Ethan Temprovich
 */
public abstract sealed class Feature permits PointFeature, EdgeFeature {

	/** -1 means the shape has no index */
	public static final int NOT_INDEXED = -1;

	/** The index of the feature on the shape */
	int index;
	
	/**
	 * Creates a new {@link Feature} with the given index.
	 * 
	 * @param index The index of the feature on the shape
	 */
	public Feature(int index) {
		this.index = index;
	}

	/**
	 * Returns the index of this feature.
	 * 
	 * @return int The index of this feature
	 */
	public int getIndex() {
		return this.index;
	}
}
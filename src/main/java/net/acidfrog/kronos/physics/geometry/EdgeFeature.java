package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.math.Vector2k;

/**
 * Represents an indexed edge of a {@link Collider}.
 * 
 * @author Ethan Temprovich
 */
public final class EdgeFeature extends Feature {
    
	/** The first vertex of the edge */
	final PointFeature vertex1;
	
	/** The second vertex of the edge */
	final PointFeature vertex2;
	
	/** The max vertex of the edge */
	final PointFeature max;

	/** The {@link Vector2k} representing the edge */
	final Vector2k edge;
	
	/**
	 * Default constructor.
	 */
	EdgeFeature() {
		this(new PointFeature(), new PointFeature(), new PointFeature(), new Vector2k(), Feature.NOT_INDEXED);
	}

	/**
	 * Creates a new {@link EdgeFeature} with the given values.
	 * 
	 * @param vertex1 The first vertex
	 * @param vertex2 The second vertex
	 * @param max The max of the two vertices
	 * @param edge The {@link Vector2k} representing the edge
	 * @param index The index
	 */
	public EdgeFeature(PointFeature vertex1, PointFeature vertex2, PointFeature max, Vector2k edge, int index) {
		super(index);
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.edge = edge;
		this.max = max;
	}

	/**
	 * Sets this {@link EdgeFeature}s values to the given values.
	 * 
	 * @param v1 The first vertex
	 * @param v2 The second vertex
	 * @param m The max of the two vertices
	 * @param e The {@link Vector2k} representing the edge
	 * @param i The index
	 */
	public void set(PointFeature v1, PointFeature v2, PointFeature m, Vector2k e, int i) {
		this.vertex1.set(v1.point, v1.index);
		this.vertex2.set(v2.point, v2.index);
		this.max.set(m.point, m.index);
		this.edge.set(e);
		this.index = i;
	}

	/**
	 * Returns the first vertex of this {@link EdgeFeature}.
	 * 
	 * @return {@link PointFeature} The first vertex
	 */
	public PointFeature getVertex1() {
		return this.vertex1;
	}
	
	/**
	 * Returns the second vertex of this {@link EdgeFeature}.
	 * 
	 * @return {@link PointFeature} The second vertex
	 */
	public PointFeature getVertex2() {
		return this.vertex2;
	}

	/**
	 * Returns the max of the two vertices of this {@link EdgeFeature}.
	 * 
	 * @return {@link PointFeature}
	 */
	public PointFeature getMaximum() {
		return this.max;
	}

	/**
	 * Returns the {@link Vector2k} representing this {@link EdgeFeature}.
	 * 
	 * @return {@link Vector2k}
	 */
	public Vector2k getEdge() {
		return this.edge;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EdgeFeature [edge=");
        builder.append(edge);
        builder.append(", max=");
        builder.append(max);
        builder.append(", vertex1=");
        builder.append(vertex1);
        builder.append(", vertex2=");
        builder.append(vertex2);
        builder.append("]");
        return builder.toString();
    }

}

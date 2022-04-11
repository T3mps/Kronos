package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.math.Vector2k;

/**
 * A wound {@link Shape} is a {@link Shape} that is defined
 * by a set of vertices wound in a consistent direction. The
 * {@link #computeWinding(Vector2k) computeWinding} method
 * will always wind the given {@link Vector2k vertices}[]
 * in a counter-clockwise order.
 * 
 * @author Ethan Temprovich
 */
public sealed interface Wound permits Polygon {

    /**
     * Returns an array of vertex indexes that define the points
     * in a counter-clockwise winding order.
     * 
     * @param vertices the vertices to wind
     * @return int[] the wound vertex indexes
     */
    public abstract int[] computeWinding(Vector2k[] vertices);

    /**
     * Returns the array of vertices that define the {@link Shape}.
     * 
     * @return Vector2k[] the vertices
     */
    public abstract Vector2k[] getVertices();

    /**
     * Returns the array of edge normals that define the {@link Shape}.
     * Since the {@link Shape} is wound counter-clockwise, the normals
     * will always be the on right-hand side.
     * 
     * @return Vector2k[] the vertices
     */     
    public abstract Vector2k[] getNormals();
    
}

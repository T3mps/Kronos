package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.math.Vector2k;

/**
 * Represents a triangle. A triangle is defined by three points, which one
 * is not collinear, and none are consecutive. The points are also defined
 * by a set of vertices, {@link Wound} in a counter-clockwise order.
 * 
 * @author Ethan Temprovich
 */
public final class Triangle extends Polygon {

    /**
     * Constructor with three points.
     * 
     * @param v1 the first point.
     * @param v2 the second point.
     * @param v3 the third point.
     */
    public Triangle(Vector2k v1, Vector2k v2, Vector2k v3) {
        super(v1, v2, v3);
    }

    /**
     * Faster contains method for triangles.
     * 
     * @param point the point to check.
     * @param transform the transform to apply to the triangle.
     * @return {@code true} if the point is contained in the triangle, {@code false} otherwise.
     * @see Polygon#contains(Vector2k, Transform)
     */
    @Override
    public boolean contains(Vector2k point, Transform transform) {
        Vector2k p = transform.getInverseRotated(point);
        Vector2k a = vertices[0], b = vertices[1], c = vertices[2];

        // if p is to the right of the AB, then it is outside the triangle
        if (Vector2k.cross(p.sub(a), b.sub(a)) < 0) return false;

        // if p is to the right of the BC, then it is outside the triangle
        if (Vector2k.cross(p.sub(b), c.sub(b)) < 0) return false;

        // if p is to the right of the CA, then it is outside the triangle
        if (Vector2k.cross(p.sub(c), a.sub(c)) < 0) return false;

        // we know that the triangle is wound counter-clockwise
        // so no sign check is needed
        return true;
    }

    /**
     * @see Polygon#computeAABB(Transform)
     */
    @Override
    public AABB computeAABB(Transform transform) {
        Vector2k b = transform.getTransformed(vertices[1]);
        Vector2k c = transform.getTransformed(vertices[2]);

        Vector2k min = transform.getTransformed(vertices[0]).clone(), max = transform.getTransformed(vertices[0]).clone();

        if (b.x < min.x) min.x = b.x;
        else if (b.x > max.x) max.x = b.x;

        if (c.x < min.x) min.x = c.x;
        else if (c.x > max.x) max.x = c.x;

        if (b.y < min.y) min.y = b.y;
        else if (b.y > max.y) max.y = b.y;

        if (c.y < min.y) min.y = c.y;
        else if (c.y > max.y) max.y = c.y;

        return new AABB(min, max);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Triangle [");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
    
}

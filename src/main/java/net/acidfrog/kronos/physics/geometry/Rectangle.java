package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.mathk.Intervalf;
import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.mathk.Vector2k;
import net.acidfrog.kronos.core.lang.Validatable;

/**
 * Represents a rectangle. A rectangle is defined by a width and height.
 * The rectangle is also defined by a set of vertices, {@link Wound} in
 * a counter-clockwise order.
 * 
 * @author Ethan Temprovich
 * @see Polygon
 */
public sealed class Rectangle extends Polygon permits Square {

    /** The width of the rectangle. */
    private final float width;

    /** The height of the rectangle. */
    private final float height;

    /**
     * Constructor with width and height.
     * 
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     */
    public Rectangle(float width, float height) {
        super(new Vector2k[] {
              new Vector2k(-width / 2, -height / 2), new Vector2k(width / 2, -height / 2),
              new Vector2k(width / 2, height / 2), new Vector2k(-width / 2, height / 2)
        },
              new Vector2k[] {
              new Vector2k( 0.0, -1.0),
              new Vector2k( 1.0,  0.0),
              new Vector2k( 0.0,  1.0),
              new Vector2k(-1.0,  0.0)
        });

        this.width = width;
        this.height = height;
    }

    /**
     * @see Validatable#vaidate()
     */
    @Override
    public boolean validate() {
        return width > 0 && height > 0;
    }

    /**
     * @see Polygon#getAxes()
     */
    @Override
    public Vector2k[] getAxes(Vector2k[] foci, Transform transform) {
        int fociCount = foci == null ? 0 : foci.length;
        Vector2k[] axes = new Vector2k[fociCount + 2]; // 2 axes per shape

        int n = 0;

        // return the normals to the surfaces, since this is a 
		// rectangle we only have two axes to test against
		axes[n++] = transform.getRotated(this.normals[1]);
		axes[n++] = transform.getRotated(this.normals[2]);

        // return the normals to the foci
        for (int i = 0; i < fociCount; i++) {
            Vector2k focus = foci[i];
            
            Vector2k closest = transform.getTransformed(vertices[0]);
            float dstsq = focus.distanceSq(closest);

            for (int j = 1; j < 4; j++) {
                Vector2k v = transform.getTransformed(vertices[j]);
                float d = focus.distanceSq(v);
                
                if (d < dstsq) {
                    closest = v;
                    dstsq = d;
                }
            }

            Vector2k axis = closest.sub(focus);

            axis.normalize();

            axes[n++] = axis;
        }

        return axes;
    }

    /**
     * @see Polygon#project()
     */
    @Override
    public Intervalf project(Vector2k axis, Transform transform) {
        Vector2k center = transform.getTransformed(this.center);

        Vector2k axis0 = transform.getRotated(this.normals[1]);
        Vector2k axis1 = transform.getRotated(this.normals[2]);

        float c = center.dot(axis);
        float e = (width  * 0.5f) * Mathk.abs(axis0.dot(axis)) +
                  (height * 0.5f) * Mathk.abs(axis1.dot(axis));
        return new Intervalf(c - e, c + e);
    }

    /**
	 * Computes the mass properties of this rectangle.
	 * 
	 * @param density the density to use during the computation
	 * @return the mass object containing properties
	 */
    @Override
    public Mass computeMass(float density) {
		// compute the mass
		float mass = density * height * width;
		// compute the inertia tensor
		float inertia = mass * (height * height + width * width) / 12.0f;
		// since we know that a rectangle has only four points that are
		// evenly distributed we can feel safe using the averaging method 
		// for the centroid
		return new Mass(mass, inertia);
	}

    /**
     * @see Polygon#computeAABB()
     */
    @Override
    public AABB computeAABB(Transform transform) {
        AABB aabb = new AABB();
        Vector2k v0 = transform.getTransformed(vertices[0]);
        Vector2k v1 = transform.getTransformed(vertices[1]);
        Vector2k v2 = transform.getTransformed(vertices[2]);
        Vector2k v3 = transform.getTransformed(vertices[3]);

        if (v0.y > v1.y) {
            if (v0.x < v1.x) {
                aabb.min.x = v0.x;
                aabb.min.y = v1.y;
                aabb.max.x = v2.x;
                aabb.max.y = v3.y;
            } else {
                aabb.min.x = v1.x;
                aabb.min.y = v2.y;
                aabb.max.x = v3.x;
                aabb.max.y = v0.y;
            }
        } else {
            if (v0.x < v1.x) {
                aabb.min.x = v3.x;
                aabb.min.y = v0.y;
                aabb.max.x = v1.x;
                aabb.max.y = v2.y;
            } else {
                aabb.min.x = v2.x;
                aabb.min.y = v3.y;
                aabb.max.x = v0.x;
                aabb.max.y = v1.y;
            }
        }

        return aabb;
    }

    /**
     * @return the width of the rectangle.
     */
    public float getWidth() {
        return width;
    }

    /**
     * @return the height of the rectangle.
     */
    public float getHeight() {
        return height;
    }

}

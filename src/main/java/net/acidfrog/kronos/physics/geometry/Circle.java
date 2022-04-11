package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.error.KronosGeometryError;
import net.acidfrog.kronos.math.Intervalf;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;

/**
 * Represents a circle. A circle is defined by a center point and
 * a radius. The variables for those attributes are defined by the
 * parent class {@link AbstractShape}
 * 
 * @author Ethan Temprovich
 */
public final class Circle extends AbstractShape implements Collider {

    /**
     * The only constructor for a circle.
     * 
     * @param {@code float} the radius
     */
    public Circle(float radius) {
        super(radius);
        validate();
    }

    /**
     * Returns a {@code boolean} value indicating if this {@link Circle}
     * is valid. A {@link Circle} is valid if it has a radius greater than
     * zero. In our case {@code 0} is considered to be
     * {@link Mathk#FLOAT_ROUNDING_ERROR a very small number} which avoids
     * insignificant circles.
     * 
     * @return {@code true} if this {@link Circle} is valid, {@code false} otherwise
     * @throws {@link KronosGeometryError} if this {@link Circle} is invalid 
     */
    @Override
    public boolean validate() {
        boolean valid = radius > Mathk.FLOAT_ROUNDING_ERROR;
        if (!valid) throw new KronosGeometryError(KronosErrorLibrary.INVALID_CIRCLE_ERROR);
		return valid;
    }
    
    /**
	 * Computes the mass properties of this circle.
	 * 
	 * @param density the density to use during the computation
	 * @return the mass object containing properties
	 */
    @Override
    public Mass computeMass(float density) {
        float mass = density * getArea();
        float inertia = mass * ((radius * radius) * 0.5f);
        return new Mass(mass, inertia);
    }

    /**
     * Projects this {@link Circle} onto the given axis. It is first
     * rotated and translated by the given {@link Transform} before
     * projecting.
     * 
     * @param {@link Vector2k} the axis to project onto
     * @param {@link Transform} the transform to apply to the circle
     * @return {@link Intervalf} the projection interval
     */
    @Override
    public Intervalf project(Vector2k axis, Transform transform) {
        Vector2k center = transform.getTransformed(this.center);
        float c = center.dot(axis);
        /** radius-c-radius -> diameter */
        return new Intervalf(c - radius, c + radius);
    }

    /**
     * Determines if the given {@link Vector2k} is contained within this
     * {@link Circle}. It is first rotated and translated by the given
     * {@link Transform} before performing the check.
     * 
     * @param {@link Vector2k} the point to check
     * @param {@link Transform} the transform to apply to this circle
     * @return {@code true} if the point is contained within this, {@code false} otherwise
     */
    @Override
    public boolean contains(Vector2k point, Transform transform) {
        Vector2k center = transform.getTransformed(this.center);
        // we compare the squared magnitude and squared radius to avoid the square root
        return center.sub(point).magnitudeSquared() <= radius * radius;
    }

    /**
     * Returns the {@link AABB} that bounds this {@link Circle}. It is
     * first rotated and translated by the given {@link Transform} before
     * computing the bounding volume.
     * 
     * @param {@link Transform} the transform to apply to this circle
     */
    @Override
    public AABB computeAABB(Transform transform) {
        AABB aabb = new AABB();
        Vector2k center = transform.getTransformed(this.center);
        aabb.set(center.x - radius,
                 center.y - radius,
                 center.x + radius,
                 center.y + radius);
        return aabb;
    }

    /**
     * Reteurns a {@link PointFeature} which is farthest from the center
     * of this {@link Circle} in the given {@link Vector2k direction}.
     * The {@link Circle} is first rotated and translated by the given
     * {@link Transform} before computing the indexed point.
     * 
     * @param {@link Vector2k} the direction to find the farthest point
     * @param {@link Transform} the transform to apply to this circle
     * @return {@link PointFeature} the farthest indexed point
     */
    @Override
    @SuppressWarnings("deprecation") // <- info in constructor
    public PointFeature getFarthestFeature(Vector2k vector, Transform transform) {
        return new PointFeature(getFarthestPoint(vector, transform));
    }

    /**
     * Reteurns a {@link Vector2k point} which is farthest from the
     * center of this {@link Circle} in the given {@link Vector2k direction}.
     * The {@link Circle} is first rotated and translated by the given
     * {@link Transform} before computing the point.
     * 
     * @param {@link Vector2k} the direction to find the farthest point
     * @param {@link Transform} the transform to apply to this circle
     * @return {@link PointFeature} the farthest indexed point
     */
    @Override
    public Vector2k getFarthestPoint(Vector2k vector, Transform transform) {
        Vector2k axis = vector.normalized();
        Vector2k center = transform.getTransformed(this.center);
        center.x += axis.x * radius;
        center.y += axis.y * radius;
        return center;
    }

    /**
     * A {@link Circle} has one focal point, which is its center point.
     * The {@link Circle} is first rotated and translated by the given
     * {@link Transform} before computing the focal point.
     * 
     * @param {@link Transform} the transform to apply to this circle
     * @return {@link Vector2k}[] the focal point
     */
    @Override
    public Vector2k[] getFoci(Transform transform) {
        Vector2k[] foci =  { transform.getTransformed(this.center) };
		return foci;
    }

    /**
     * Should return the axes of a {@link Shape}. However, a {@link Circle}
     * has infinite separating axes, so this method always returns null.
     * 
     * @param {@link Vector2k}[] the focal point of the shape
     * @param {@link Transform} the transform to apply to this circle
     * @return {@code null}
     */
    @Override
    public Vector2k[] getAxes(Vector2k[] foci, Transform transform) {
        // circle has infinite axes so we return null
        return null;
    }

    /**
     * Returns the area of this {@link Circle}.
     * 
     * @return {@code float} the area
     */
    @Override
    public float getArea() {
        return Mathk.PI * this.radius * this.radius;
    }

    /**
     * Returns the {@link #getCircumference() circumference} of this {@link Circle}.
     * @return {@code float} the circumference
     */
    @Override
    @Deprecated
    public float getPerimeter() {
        return getCircumference();
    }

    /**
     * Returns the circumference of this {@link Circle}.
     * 
     * @return {@code float} the circumference
     */
    public float getCircumference() {
        return 2 * Mathk.PI * radius;
    }

    @Override
    public ColliderType getType() {
        return ColliderType.CIRCLE;
    }
    
}

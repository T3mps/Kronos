package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.annotations.Out;
import net.acidfrog.kronos.math.Intervalf;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.geometry.Circle;
import net.acidfrog.kronos.physics.geometry.Ray;
import net.acidfrog.kronos.physics.geometry.Transform;

/**
 * A seperate class for performing geometric queries between
 * circles. The class does not implement the
 * {@link NarrowphaseDetector}, {@link RaycastDetector} or
 * {@link DistanceDetector} interfaces due tothe methods requiring a
 * {@link Collider}; we only require a {@link Circle}. Use this class over 
 * others for queries optimized for circles. The methods are static in this
 * class because the other Detectors will automatically use these methods if
 * two circles are present in the query.
 * 
 * @author Ethan Temprovich
 */
@Internal
public final class CircleDetector /* implements NarrowphaseDetector, RaycastDetector, DistanceDetector */ {
    
    /** Ensures this class can not be instantiated */
    private CircleDetector() { }

     /**
     * Determines if the given {@link Circle}s are colliding.
     * 
     * @param circleA The first {@link Circle}
     * @param transformA The {@link Transform} to apply to the first {@link Circle}
     * @param circleB The second {@link Circle}
     * @param transformB The {@link Transform} to apply to the second {@link Circle}
     * @return {@code true} if the circles are colliding, {@code false} otherwise
     */
    public static boolean detect(Circle circleA, Transform transformA, Circle circleB, Transform transformB) {
        Vector2k centerA = transformA.getTransformed(circleA.getCenter());
        Vector2k centerB = transformB.getTransformed(circleB.getCenter());
        Vector2k to = centerB.sub(centerA);
        float radii = circleA.getRadius() + circleB.getRadius();
        
        return to.magnitudeSquared() < radii * radii;
    }

    /**
     * Determines if the given {@link Circle}s are colliding.
     * Populates the {@link Penetration} object with the
     * collision normal and penetration depth.
     * 
     * @param circleA The first {@link Circle}
     * @param transformA The {@link Transform} to apply to the first {@link Circle}
     * @param circleB The second {@link Circle}
     * @param transformB The {@link Transform} to apply to the second {@link Circle}
     * @param penetration The {@link Penetration} object to populate
     * @return {@code true} if the circles are colliding, {@code false} otherwise
     */
    public static boolean detect(Circle circleA, Transform transformA, Circle circleB, Transform transformB, @Out Penetration penetration) {
        penetration.clear();
        Vector2k centerA = transformA.getTransformed(circleA.getCenter());
        Vector2k centerB = transformB.getTransformed(circleB.getCenter());
        Vector2k to = centerB.sub(centerA);
        float radii = circleA.getRadius() + circleB.getRadius();
        float mag2 = to.magnitudeSquared();
        
        if (mag2 < radii * radii) {
            penetration.setNormal(to);
            penetration.setDepth(radii - to.getNormalized());
            return true;
        }

        return false;
    }

    /**
     * Determines if the {@link Circle circleA} fully contains {@link Circle circleB}.
     * 
     * @param cirlceA The containing {@link Circle}
     * @param transformA The {@link Transform} to apply to the containing {@link Circle}
     * @param circleB The contained {@link Circle}
     * @param transformB The {@link Transform} to apply to the contained {@link Circle}
     * @return
     */
    public static boolean contains(Circle circleA, Transform transformA, Circle circleB, Transform transformB) {
        Intervalf ia = circleA.project(Vector2k.RIGHT, transformA); // project onto x-axis
        Intervalf ib = circleB.project(Vector2k.RIGHT, transformB); // project onto x-axis

        // +-project-onto-x-axis-+
        // |                     |
        // |                     |
        // |   ( () )            |
        // |   : :: :            |
        // |   : :: :            |
        // x---A-B-A-------------+
        //
        // we only need to check one axis because cicles have infinitely even distribution

        return ia.containsExclusive(ib);
    }

    /**
     * Determintes if two {@link Circle}s are separated. Returns true if
     * the distance^2 between the centers of the {@link Circle}s is less 
     * than the sum of the radii^2. The {@link Separation} object is
     * used to store the separation data.
     *
     * @param circleA the first {@link Circle}
     * @param transformA the {@link Transform} to be applied to the first {@link Circle}
     * @param circleB the second {@link Circle}
     * @param transformB the {@link Transform} to be applied to the second {@link Circle}
     * @param separation the {@link Separation} object to store the separation data
     * @return {@code true} if the circles are separated, {@code false} otherwise 
     */
    public static boolean distance(Circle circleA, Transform transformA, Circle circleB, Transform transformB, @Out Separation separation) {
        Vector2k centerA = transformA.getTransformed(circleA.getCenter());
        Vector2k centerB = transformB.getTransformed(circleB.getCenter());
        float ra = circleA.getRadius();
        float rb = circleB.getRadius();
        Vector2k to = centerB.sub(centerA);
        float radii = ra + rb;
        float mag2 = to.magnitudeSquared();

        separation.setPointA(centerA.add(to.mul(ra)));
        separation.setPointB(centerB.sub(to.mul(rb)));
        separation.setNormal(to);
        separation.setDistance(to.normalize() - radii);
        
        return mag2 >= radii * radii;
    }

    /**
     * Performs a raycast query on the given {@link Circle}. The raycast
     * is performed from the given {@link Ray}, and the {@link RaycastResult}
     * passed into the method is filled with the result of the query. This
     * method returns {@code true} if the query was successful. The optional
     * maxDistance parameter can be used to limit the distance of the raycast.
     * 
     * @param ray The {@link Ray} to cast
     * @param maxDistance (optional, 0 for infinite) The maximum distance of the raycast
     * @param circle The {@link Circle} to raycast against
     * @param transform The {@link Transform} to apply to the collider
     * @param result The {@link RaycastResult} to fill with the result of the query
     * @return {@code true} if the query was successful, {@code false} otherwise
     */
    public static boolean raycast(Ray ray, float maxDistance, Circle circle, Transform transform, @Out RaycastResult result) {
        Vector2k start = ray.getStart();
        Vector2k direction = ray.getDirectionVector();
        Vector2k center = transform.getTransformed(circle.getCenter());
        float radius = circle.getRadius();

        // start of ray is inside circle
        if (circle.contains(start, transform)) return false;

        // any point on a ray can be found by the parametric equation:
		// P = tD + S
        // where P is the point, S is the start of the ray, D is the
        // direction vector and t is the distance along the ray.
        
        // any point on the circle can be found by the parametric equation:
        // (x - xc)^2 + (y - yc)^2 = r^2
        // where x and y are the coordinates of the point, xc and yc are
        // the coordinates of the circle's center and r is the radius.

        // substituting the first equation into the second yields:
        // |D|^2t^2 + 2D.dot(S - C)t + (S - C)^2 - r^2 = 0
        // where |D| is the magnitude of the direction vector, D.dot(D) is
        // the dot product of the 2 times the direction vector and the start
        // point of the ray minus the center
        // t is the distance along the ray, and (S - C)^2 is the start point
        // of the ray minus the center point squared, minus the radius squared.

        // using the quadratic equation, we can solve for t:
        // a = |D|^2
        // b = 2D.dot(S - C)
        // c = (S - C)^2 - r^2

        Vector2k cToS = start.sub(center);

        // mag(D)^2
        float a = direction.dot(direction);

        // 2D.dot(S - C)
        float b = 2 * direction.dot(cToS);

        // (S - C)^2 - r^2
        float c = cToS.dot(cToS) - radius * radius;

        float inv2a = 1f / (2f * a);
        float disc = b * b - 4 * a * c;
        
        // check for negative discriminant
        // if so, the ray is parallel to the circle
        if (disc < 0) return false;

        float sqrtDisc = Mathk.sqrt(disc);

        // calculate the two possible canidates for t
        float t = 0f;
        float t0 = (-b + sqrtDisc) * inv2a;
        float t1 = (-b - sqrtDisc) * inv2a;

        if (t0 < 0f) {
            if (t1 < 0f) return false;

            t = t1;
        } else {
            if (t1 < 0.0) t = t0;
			else if (t0 < t1) t = t0;
			else t = t1;
        }

        // check if the intersection point is within the max distance
        if (maxDistance > 0f && t > maxDistance) return false;

        // if we get here, we have an intersection
        Vector2k pointOfIntersection = direction.mul(t).add(start);
        Vector2k normal = pointOfIntersection.sub(center);
        
        result.setPoint(pointOfIntersection);
        result.setNormal(normal.normalized());
        result.setDistance(t);

        return true;
    }

}

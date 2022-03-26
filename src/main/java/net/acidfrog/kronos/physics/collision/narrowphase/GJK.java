package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.core.lang.annotations.Out;
import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.mathk.Vector2k;
import net.acidfrog.kronos.physics.Physics;
import net.acidfrog.kronos.physics.geometry.Circle;
import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Geometry;
import net.acidfrog.kronos.physics.geometry.Ray;
import net.acidfrog.kronos.physics.geometry.Transform;

/**
 * The GJK algorithm is a simple algorithm for collision detection.
 * However, it is not the most efficient algorithm for collisions and has
 * since been deprecated. {@link SAT} is a more efficient algorithm and
 * is the default algorithm for collision detection. The GJK algorithm
 * also calculates the {@link Separation} between two convex polygons and 
 * calculates {@link RaycastResult} queries.
 * 
 * @author Ethan Temprovich
 */
public final class GJK implements DistanceDetector, RaycastDetector {

    public GJK() {}

    /**
     * @see DistanceDetector#distance(Collider, Transform, Collider, Transform, Separation)
     */
    @Override
    public boolean distance(Collider shapeA, Transform transformA, Collider shapeB, Transform transformB, @Out Separation separation) {
        if (shapeA instanceof Circle && shapeB instanceof Circle) return CircleDetector.detect((Circle) shapeA, transformA, (Circle) shapeB, transformB);

        final class MinkowskiDiff {
        
            final Collider shapeA, shapeB;
            final Transform transformA, transformB;
    
            MinkowskiDiff(Collider shapeA, Transform transformA, Collider shapeB, Transform transformB) {
                this.shapeA = shapeA;
                this.transformA = transformA;
                this.shapeB = shapeB;
                this.transformB = transformB;
            }
    
            public final Point getSupport(Vector2k direction) {
                Vector2k a = shapeA.getFarthestPoint(direction, transformA);
                Vector2k b = shapeB.getFarthestPoint(direction.negated(), transformB);
                return new Point(a, b);
            }
    
        }

        MinkowskiDiff diff = new MinkowskiDiff(shapeA, transformA, shapeB, transformB);

        Point a = null;
        Point b = null;
        Point c = null;

        Vector2k c1 = transformA.getTransformed(shapeA.getCenter());
        Vector2k c2 = transformB.getTransformed(shapeB.getCenter());

        Vector2k d = c2.sub(c1);

        if (d.isZero()) return false;

        a = diff.getSupport(d);
        d.negate();
        b = diff.getSupport(d);

        d = Geometry.getPointOnSegmentClosestToPoint(Vector2k.ZERO, b.point, a.point);

        for (int i = 0; i < Physics.MAX_NARROWPHASE_DETECT_ITERATIONS; i++) {
            d.negate();
            
            if (d.magnitudeSquared() <= Physics.DISTANCE_EPSILON) return false;

            c = diff.getSupport(d);

            if (containsOrigin(a.point, b.point, c.point)) return false;

            float proj = c.point.dot(d);
            if ((proj - a.point.dot(d)) < Physics.DISTANCE_EPSILON) {
                findClosestPoints(a, b, separation);
                return true;
            }

            Vector2k p1 =Geometry.getPointOnSegmentClosestToPoint(Vector2k.ZERO, a.point, c.point);
            Vector2k p2 =Geometry.getPointOnSegmentClosestToPoint(Vector2k.ZERO, c.point, b.point);

            float p1m2 = p1.magnitudeSquared();
            float p2m2 = p2.magnitudeSquared();
            
            if (p1m2 <= Mathk.FLOAT_ROUNDING_ERROR) {
                findClosestPoints(a, c, separation);
                return true;
            } else if (p2m2 <= Mathk.FLOAT_ROUNDING_ERROR) {
                findClosestPoints(c, b, separation);
                return true;
            }

            if (p1m2 < p2m2) {
                b = c;
                d = p1;
            } else {
                a = c;
                d = p2;
            }
        }

        findClosestPoints(a, b, separation);
        return true;
    }

    void findClosestPoints(Point a, Point b, Separation separation) {
        Vector2k p1 = new Vector2k();
        Vector2k p2 = new Vector2k();
        Vector2k l = b.point.sub(a.point);

        if (l.isZero()) {
            p1.set(a.supportPointA);
            p2.set(b.supportPointB);
        } else {
            float l1 = l.dot(l);
            float l2 = -l.dot(a.point) / l1;

            if (l2 > 1f) {
                p1.set(b.supportPointA);
                p2.set(b.supportPointB);
            } else if (l2 < 0f) {
                p1.set(a.supportPointA);
                p2.set(a.supportPointB);
            } else {
                p1.x = a.supportPointA.x + l2 * (b.supportPointA.x - a.supportPointA.x);
                p1.y = a.supportPointA.y + l2 * (b.supportPointA.y - a.supportPointA.y);
                p2.x = a.supportPointB.x + l2 * (b.supportPointB.x - a.supportPointB.x);
                p2.y = a.supportPointB.y + l2 * (b.supportPointB.y - a.supportPointB.y);
            }
        }

        separation.setPointA(p1);
        separation.setPointB(p2);

        Vector2k n = p2.sub(p1);
        float d = n.normalize();
        separation.setNormal(n);
        separation.setDistance(d);
    }

    boolean containsOrigin(Vector2k a, Vector2k b, Vector2k c) {
        float sa = a.cross(b);
        float sb = b.cross(c);
        float sc = c.cross(a);
        // we don't need to test sb * sc
        return sa * sb > 0 && sa * sc > 0;
    }

    /**
     * Performs a raycast query on the given {@link Collider}. The raycast
     * is performed from the given {@link Ray}, and the {@link RaycastResult}
     * passed into the method is filled with the result of the query. This
     * method returns {@code true} if the query was successful. The optional
     * maxDistance parameter can be used to limit the distance of the raycast.
     * 
     * @param ray The {@link Ray} to cast
     * @param maxDistance (optional, 0 for infinite) The maximum distance of the raycast
     * @param shape The {@link Collider} to raycast against
     * @param transform The {@link Transform} to apply to the collider
     * @param result The {@link RaycastResult} to fill with the result of the query
     * @return {@code true} if the query was successful, {@code false} otherwise
     * @see RaycastDetector#raycast(Ray, float, Collider, Transform, RaycastResult)
     */
    @Override
    public boolean raycast(Ray ray, float maxDistance, Collider shape, Transform transform, @Out RaycastResult result) {
        if (shape instanceof Circle) return CircleDetector.raycast(ray, maxDistance, (Circle) shape, transform, result);
        
        // make sure start isn't inside the shape
        if (shape.contains(ray.getStart())) return false;

        // simplex points
        Vector2k a = null;
        Vector2k b = null;

        // the current closest point, start for now
        Vector2k x = ray.getStart();

        // the direction of the ray
        Vector2k r = ray.getDirectionVector();
        // the normal at point of intersection
        Vector2k n = new Vector2k();
        // distance along ray
        float l = 0;

        // center of shape
        Vector2k c = transform.getTransformed(shape.getCenter());

        // center to start
        Vector2k d = x.sub(c);

        float dstsq = Float.MAX_VALUE;
        int iterations = 0;

        // start the loop
        while (dstsq > Physics.RAYCAST_EPSILON && iterations < Physics.MAX_RAYCAST_DETECT_ITERATIONS) {
            // get the farthest point in the direction of the ray
            Vector2k p = shape.getFarthestPoint(d, transform);
            // closest point on ray -> p
            Vector2k w = x.sub(p);

            // closest point on ray -> w
            float dDotW = d.dot(w);
            // is it point in the direction of d?
            if (dDotW >= 0f) {
                // is d pointing in the direction of the ray?
                float dDotR = d.dot(r);
                if (dDotR >= 0f) {
                    return false;
                } else {
                    l = l - dDotW / dDotR;

                    if (maxDistance > 0 && l > maxDistance) return false;
                   
                    x = r.mul(l).add(ray.getStart());
    
                    n.set(d);
                }
            }

            // create the simplex
            if (a != null) {
                if (b != null) {
                    // find third point

                    Vector2k v1 = Geometry.getPointOnSegmentClosestToPoint(x, a, p);
                    Vector2k v2 = Geometry.getPointOnSegmentClosestToPoint(x, p, b);

                    if (v1.distanceSq(x) > v2.distanceSq(x)) {
                        a.set(p);
                        dstsq = v2.distanceSq(x);
                    } else {
                        b.set(p);
                        dstsq = v1.distanceSq(x);
                    }

                    Vector2k ab = b.sub(a);
                    Vector2k ax = x.sub(a);
                    d = Mathk.tripleProduct(ab, ax, ab);
                } else {
                    // b becomes p
                    b = p;

                    // find new direction
                    Vector2k ab = b.sub(a);
                    Vector2k ax = x.sub(a);
                    d = Mathk.tripleProduct(ab, ax, ab);
                }
            } else {
                // a becomes p
                a = p;
                // reverse direction
                d.negate();
            }

            iterations++;
        }

        result.set(x, n.normalized(), l);

        return true;
    }
 
    private static final class Point {

        final Vector2k supportPointA, supportPointB;
        final Vector2k point;

        Point(Vector2k a, Vector2k b) {
            supportPointA = a;
            supportPointB = b;
            point = a.sub(b);
        }

    }

}

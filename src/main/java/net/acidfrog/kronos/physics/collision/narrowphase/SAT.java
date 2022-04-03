package net.acidfrog.kronos.physics.collision.narrowphase;

import net.acidfrog.kronos.math.Intervalf;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.geometry.Circle;
import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Transform;
import net.acidfrog.kronos.physics.geometry.Polygon;
import net.acidfrog.kronos.physics.geometry.Segment;
import net.acidfrog.kronos.physics.geometry.Wound;
import net.acidfrog.kronos.physics.geometry.Shape;

/**
 * Implementation of the Separating Axis Theorem collision detection algorithm.
 * This is a simple implementation that uses the {@link SAT} to determine if two
 * {@link Collider}s are colliding. {@link Collider} are defined as convex
 * {@link Shape}s, and {@link Polygon}s are {@link Wound}
 * {@link Geometry#COUNTER_CLOCKWISE_WINDING counter-clockwise}.
 * 
 * <p>
 * References:
 * 
 * <p>
 * http://www.metanetsoftware.com/technique/tutorialA.html
 * https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169
 * 
 * @author Ethan Temprovich
 */
public final class SAT implements NarrowphaseDetector {

    public SAT() {}

    /**
     * @see NarrowphaseDetector#detect(Collider, Transform, Collider, Transform)
     */
    @Override
    public boolean detect(Collider colliderA, Transform transformA, Collider colliderB, Transform transformB) {
        if (colliderA instanceof Circle && colliderB instanceof Circle) return CircleDetector.detect((Circle) colliderA, transformA, (Circle) colliderB, transformB);

        Vector2k[] axesA = colliderA.getAxes(colliderA.getFoci(transformA), transformA);
        Vector2k[] axesB = colliderB.getAxes(colliderB.getFoci(transformB), transformB);

        // loop through all the axes of A
        if (axesA != null) {
            for (int i = 0; i < axesA.length; i++) {
                Vector2k axis = axesA[i];

                if (!axis.isZero()) {
                    Intervalf intervalA = colliderA.project(axis, transformA);
                    Intervalf intervalB = colliderB.project(axis, transformB);

                    if (!intervalA.overlaps(intervalB)) return false;
                }
            }
        }

        // loop through all the axes of B
        if (axesB != null) {
            for (int i = 0; i < axesB.length; i++) {
                Vector2k axis = axesB[i];

                if (!axis.isZero()) {
                    Intervalf intervalA = colliderA.project(axis, transformA);
                    Intervalf intervalB = colliderB.project(axis, transformB);

                    if (!intervalA.overlaps(intervalB)) return false;
                }
            }
        }

        return true;
    }
    
    /**
     * @see NarrowphaseDetector#detect(Collider, Transform, Collider, Transform, Penetration)
     */
    @Override
    public boolean detect(Collider colliderA, Transform transformA, Collider colliderB, Transform transformB, /** out */ Penetration penetration) {
        if (colliderA instanceof Circle && colliderB instanceof Circle) return CircleDetector.detect((Circle) colliderA, transformA, (Circle) colliderB, transformB, penetration);

        Vector2k normal = null;
        float depth = Float.MAX_VALUE;

        Vector2k[] axesA = colliderA.getAxes(colliderA.getFoci(transformA), transformA);
        Vector2k[] axesB = colliderB.getAxes(colliderB.getFoci(transformB), transformB);

        // loop through all the axes of A
        if (axesA != null) {
            for (int i = 0; i < axesA.length; i++) {
                Vector2k axis = axesA[i];

                if (!axis.isZero()) {
                    Intervalf intervalA = colliderA.project(axis, transformA);
                    Intervalf intervalB = colliderB.project(axis, transformB);

                    if (!intervalA.overlaps(intervalB)) {
                        return false;
                    } else {
                        float ol = intervalA.getOverlap(intervalB);

                        // check for containment
                        if (intervalA.containsExclusive(intervalB) || intervalB.containsExclusive(intervalA)) {
                            // if there is containment, then aquire the overlap and the distance
                            // between the two closest end points
                            float min = Mathk.abs(intervalA.getMin() - intervalB.getMin());
                            float max = Mathk.abs(intervalA.getMax() - intervalB.getMax());

                            if (min < max) {
                                // if the min is less than the max flip the axis
                                axis.negate();
                                ol += min;
                            } else {
                                ol += max;
                            }
                        }

                        // if the intervals do overlap, then update the overlap and normal
                        if (ol < depth) {
                            depth = ol;
                            normal = axis;
                        }
                    }
                }
            }
        }

        // loop through all the axes of B
        if (axesB != null) {
            for (int i = 0; i < axesB.length; i++) {
                Vector2k axis = axesB[i];

                if (!axis.isZero()) {
                    Intervalf intervalA = colliderA.project(axis, transformA);
                    Intervalf intervalB = colliderB.project(axis, transformB);

                    if (!intervalA.overlaps(intervalB)) {
                        return false;
                    } else {
                        float ol = intervalA.getOverlap(intervalB);

                        // check for containment
                        if (intervalA.containsExclusive(intervalB) || intervalB.containsExclusive(intervalA)) {
                            // if there is containment, then aquire the overlap and the distance
                            // between the two closest end points
                            float min = Mathk.abs(intervalA.getMin() - intervalB.getMin());
                            float max = Mathk.abs(intervalA.getMax() - intervalB.getMax());

                            if (min < max) {
                                // if the min is less than the max flip the axis
                                axis.negate();
                                ol += min;
                            } else {
                                ol += max;
                            }
                        }

                        // if the intervals do overlap, then update the overlap and normal
                        if (ol < depth) {
                            depth = ol;
                            normal = axis;
                        }
                    }
                }
            }
        }

        // if we get here, there are no separating axes, so we have a collision

        Vector2k ca = transformA.getTransformed(colliderA.getCenter());
        Vector2k cb = transformB.getTransformed(colliderB.getCenter());

        // make sure the normal is pointing from A to B
        // if its not, flip it
        if (normal.dot(cb.sub(ca)) < 0) normal.negate();

        penetration.setNormal(normal);
        penetration.setDepth(depth);

        return true;
    }

    /**
     * @see NarrowphaseDetector#contains(Collider, Transform, Collider, Transform)
     */
    @Override
    public boolean contains(Collider colliderA, Transform transformA, Collider colliderB, Transform transformB) {
        if (colliderA instanceof Circle && colliderB instanceof Circle) return CircleDetector.contains((Circle) colliderA, transformA, (Circle) colliderB, transformB);
        
        // overlap is not possible
        if (colliderA instanceof Segment && colliderB instanceof Segment) return false;

        Vector2k[] axesA = colliderA.getAxes(colliderA.getFoci(transformA), transformA);
        Vector2k[] axesB = colliderB.getAxes(colliderB.getFoci(transformB), transformB);

        if (axesA != null) {
            for (int i = 0; i < axesA.length; i++) {
                Vector2k axis = axesA[i];

                if (!axis.isZero()) {
                    Intervalf intervalA = colliderA.project(axis, transformA);
                    Intervalf intervalB = colliderB.project(axis, transformB);

                    if (!intervalA.containsExclusive(intervalB)) return false;
                }
            }
        }

        if (axesB != null) {
            for (int i = 0; i < axesB.length; i++) {
                Vector2k axis = axesB[i];

                if (!axis.isZero()) {
                    Intervalf intervalA = colliderA.project(axis, transformA);
                    Intervalf intervalB = colliderB.project(axis, transformB);

                    if (!intervalA.containsExclusive(intervalB)) return false;
                }
            }
        }

        return true;
    }

}

package net.acidfrog.kronos.physics.geometry;

import java.util.Arrays;
import java.util.List;

import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.error.KronosGeometryError;
import net.acidfrog.kronos.mathk.Intervalf;
import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.mathk.Vector2k;

/**
 * Represents a 2D polygon. A polygon is defined by a set of vertices,
 * {@link Wound} in a counter-clockwise direction for our use.
 * 
 * <p>
 * A polygon must have at least 3 vertices, and no vertex may be symetrical
 * to another. The polygon also must have at least 2 vertices that are not
 * colinear. For our use, a polygon also must be convex.
 * 
 * @author Ethan Temprovich
 */
public sealed class Polygon extends AbstractShape implements Collider, Wound permits Rectangle, Triangle {

	/** The amount of vertices in this polygon. */
	public int vertexCount;

	/** The vertices of this polygon. */
	protected final Vector2k[] vertices;

	/** The normals of this polygon. */
	protected final Vector2k[] normals;

	/**
	 * Constructs a new polygon with the given vertices.
	 * 
	 * @param vertices the vertices of the polygon
	 */
	public Polygon(Vector2k... vertices) {
		super(Geometry.getWeightedCentroid(vertices), Geometry.getRadius(Geometry.getWeightedCentroid(vertices), vertices));
		this.vertexCount = vertices.length;
		this.vertices = new Vector2k[vertexCount];
		this.normals = new Vector2k[vertexCount];
		set(vertices);
        validate();
	}

	/**
	 * Constructs a new polygon with the given vertices.
	 * 
	 * @param vertices the vertices of the polygon
	 */
	public Polygon(List<Vector2k> vertices) {
		// set(vertices.toArray(Vector2k[]::new), null);
		this(vertices.toArray(Vector2k[]::new));
	}

	/**
	 * Constructs a new polygon with the given vertices, and given
	 * normals.
	 * 
	 * @param vertices the vertices of the polygon
	 * @param normals the normals of the polygon
	 */
	public Polygon(Vector2k[] vertices, Vector2k[] normals) {
		super(Geometry.getWeightedCentroid(vertices), Geometry.getRadius(Geometry.getWeightedCentroid(vertices), vertices));
        this.vertices = vertices;
		this.normals  = normals;
		this.vertexCount = vertices.length;
		validate();
	}

	/**
	 * Copy constructor.
	 */
	public Polygon(Polygon polygon) {
		this(polygon.vertices, polygon.normals);
	}

	/**
	 * Internal set method. Computes the appropriate winding and normals, based
	 * on the given vertices.
	 * 
	 * @param vertices the vertices of the polygon
	 */
	void set(Vector2k[] vertices) {
		int[] winding = computeWinding(vertices);
		for (int i = 0; i < vertexCount; i++) this.vertices[i] = vertices[winding[i]];

		for (int i = 0; i < vertexCount; i++) {
			Vector2k face = this.vertices[(i + 1) % vertexCount].sub(this.vertices[i]);

			normals[i] = face.getNormalRight();
			normals[i].normalize();
		}

		// Compute the radius of the shape
        float maxRadius = 0f;

        for (int i = 0; i < this.vertexCount; i++) {
            float radius = vertices[i].distance(vertices[(i + 1) % this.vertexCount]);
            if (radius > maxRadius) {
                maxRadius = radius;
            }
        }

        this.radius = maxRadius;
	}

	/**
	 * Validates this polygon. Tests for the following:
	 * 
	 * <ul>
	 * <li>The polygon has is convex</li>
	 * <li>The polygon has at least 3 vertices</li>
	 * <li>The polygon has at least 2 vertices that are not colinear</li>
	 * <li>The polygon has no duplicate vertices</li>
	 * <li>The polygon has a counter-clockwise winding</li>
	 * </ul>
	 * 
	 * @throws KronosGeometryError if the polygon is invalid
	 * @return {@code true} if the polygon is valid, {@code false} otherwise
	 */
	@Override
    public boolean validate() {
		boolean valid = vertexCount >= 3;
		if (!valid) throw new KronosGeometryError(KronosErrorLibrary.POLYGON_LESS_THAN_THREE_VERTICES_ERROR);

		int n = vertexCount;

		if (vertices[0].x == vertices[n - 1].x && vertices[0].y == vertices[n - 1].y) n--;

		Vector2k current = vertices[n - 2]; // second to last vertex
		Vector2k next = vertices[n - 1]; // last vertex
		float currentDirection = 0;
		float nextDirection = Mathk.atan2(next.y - current.y, next.x - current.x);
		float angleSum = 0f;
		int winding = 0; // 1 is CCW, -1 is CW

		for (int i = 0; i < vertexCount; i++) {
			current = next;
			next = vertices[i];
			currentDirection = nextDirection;
			nextDirection = Mathk.atan2(next.y - current.y, next.x - current.x);

			if (current.x == next.x && current.y == next.y) {
				valid = false;
				throw new KronosGeometryError(KronosErrorLibrary.POLYGON_CONSECUTIVE_VERTICES_ERROR);
			}

			float angle = nextDirection - currentDirection;

			// maps the angles domain to [-PI, PI]
			if (angle < -Mathk.PI) angle += 2 * Mathk.PI;
			else if (angle > Mathk.PI) angle -= 2 * Mathk.PI;

			if (i == 0) { // if first time through, compute winding
				if (angle == 0) {
					valid = false;
					throw new KronosGeometryError(KronosErrorLibrary.POLYGON_IS_LINE_ERROR);
				}

				winding = (angle > 0f) ? 1 : -1;
			} else { // if not first time through, determine if winding is consistent
				if (winding * angle <= 0) { // both winding and angle different signs = not convex
					valid = false;
					throw new KronosGeometryError(KronosErrorLibrary.POLYGON_IS_CONCAVE_ERROR);
				}
			}

			angleSum += angle;
		}
		
		valid = Math.abs(Math.round(angleSum / (2 * Mathk.PI))) == 1 && winding == Geometry.COUNTER_CLOCKWISE_WINDING;
		if (!valid) throw new KronosGeometryError(KronosErrorLibrary.INVALID_POLYGON_ERROR);
		return valid;
	}

	/**
	 * Computes the mass properties of this polygon.
	 * 
	 * @param density the density to use during the computation
	 * @return the mass object containing properties
	 */
	@Override
	public Mass computeMass(float density) {
		Vector2k center = new Vector2k();
		float area = 0f;
		float I = 0f;
		int n = this.vertices.length;
		Vector2k ac = new Vector2k();
		for (int i = 0; i < n; i++) ac.add(this.vertices[i]);
		ac.divi(n);

		for (int i1 = n - 1, i2 = 0; i2 < n; i1 = i2++) {
			Vector2k p1 = this.vertices[i1];
			Vector2k p2 = this.vertices[i2];

			p1.subi(ac);
			p2.subi(ac);

			float D = p1.cross(p2);
			float triangleArea = 0.5f * D;
			area += triangleArea;

			center.x += (p1.x + p2.x) * Geometry.INV_3 * triangleArea;
			center.y += (p1.y + p2.y) * Geometry.INV_3 * triangleArea;

			I += triangleArea * (p2.dot(p2) + p2.dot(p1) + p1.dot(p1));
		}

		// compute the mass
		float m = density * area;

		I *= (density / 6.0f);
		I -= m * center.div(area).magnitudeSquared();
		
		return new Mass(m, I);
	}

	/**
	 * Projects this polygon onto the given axis. The projection
	 * is the minimum and maximum projection of each vertex onto
	 * the axis.
	 * 
	 * @param axis
	 * @param transform
	 * @return {@link Intervalf}
	 */
    @Override
    public Intervalf project(Vector2k axis, Transform transform) {
		float v = 0f;
		Vector2k p = transform.getTransformed(vertices[0]);

		// project first point onto the axis
		float min = axis.dot(p);
		float max = min;

		// project all other points onto the axis
		for (int i = 1; i < vertexCount; i++) {
			p = transform.getTransformed(vertices[i]);

			// project point onto the axis
			v = axis.dot(p);

			if (v < min) min = v;
			else if (v > max) max = v;
		}

		return new Intervalf(min, max);
    }

	/**
	 * Determines if the given {@link Vector2k point} is contained
	 * within this polygon. This polygon is first transformed by the
	 * given {@link Transform transform} before being tested. The polygon
	 * is assumed to be convex.
	 * 
	 * <p>
	 * The following test is performed:
	 * 
	 * <p>
	 * If the the sign of the location of the point on the side of
	 * an edge is always the same and the polygon is convex, then
	 * we know that the point lies inside the polygon. This method
	 * doesn't care about vertex winding inverse transform the point
	 * to put it in local coordinates.
	 * 
	 * @param point the point to test
	 * @param transform the transform to apply to the polygon
	 * @return {@code true} if the point is inside the polygon, {code false} otherwise
	 */
    @Override
    public boolean contains(Vector2k point, Transform transform) {
		Vector2k p = transform.getInverseTransformed(point);

		// start fromthe pair (last, first) so we don't need to wrap in the loop
		Vector2k v1 = vertices[vertexCount - 1];
		Vector2k v2 = vertices[0];

		// get the location of the point relative to the first two vertices
		// A---------D
		//   .	    
		float loc = Geometry.getLocationRelativeToSegment(point, v1, v2);

		for (int i = 0; i < vertexCount - 1; i++) {
			// move to next pair of vertices
			v1 = v2;
			v2 = vertices[i + 1];

			// check if the point is a vertex of the polygon
			if (p.equals(v1) || p.equals(v2)) return true;

			// do side of line test
			float location = Geometry.getLocationRelativeToSegment(point, v1, v2);
			
			// multiply th elast location with this location
			// if they have the same sign, the point is on the same side of the line
			if (loc * location < 0) return false; // (-0.0 < 0.0) evaluates to false and not true
		
			// update the last location, only if its not very close to zero
			// a location of zero means the point lies ON the line through v1 and v2
			if (Mathk.abs(location) > Mathk.FLOAT_ROUNDING_ERROR) loc = location;
		}

		// if we get here, the point is inside the polygon
		return true;
	}

	/**
	 * Returns the tightest fitting {@link AABB} for this polygon.
	 * 
	 * @param transform the transform to apply to the polygon
	 * @return the tightest fitting {@link AABB}
	 */
    @Override
    public AABB computeAABB(Transform transform) {
		Vector2k point = transform.getTransformed(vertices[0]);

		Vector2k min = point.clone();
		Vector2k max = point.clone();

		for (int i = 1; i < vertexCount; i++) {
			Vector2k p = transform.getTransformed(vertices[i]);

			// compare the x values
			if (p.x < min.x) min.x = p.x;
			else if (p.x > max.x) max.x = p.x;

			// compare the y values
			if (p.y < min.y) min.y = p.y;
			else if (p.y > max.y) max.y = p.y;
		}

		return new AABB(min, max);
    }

	/**
	 * @see {@link Wound#computeWinding(Vector2k[])}
	 */
	@Override
    public int[] computeWinding(Vector2k[] vertices) {
        // Find the right most Vector2k on the hull
		int rightMost = 0;
		float highestXCoord = vertices[0].x;

		for (int i = 1; i < vertices.length; i++) {
			float x = vertices[i].x;

			if (x > highestXCoord) {
				highestXCoord = x;
				rightMost = i;
			} else {
				// Check for matching x and y
				if (x == highestXCoord && vertices[i].y < vertices[rightMost].y) rightMost = i;
			}
		}

		int[] windingOrder = new int[Geometry.MAX_POLYGON_VERTICES_COUNT];
		int outCount = 0;
		int indexHull = rightMost;

		for (;;) {
			windingOrder[outCount] = indexHull;

			// Search for next index that wraps around the hull
			// by computing cross products to find the most counter-clockwise
			// vertex in the set, given the previos hull index
			int nextHullIndex = 0;
			for (int i = 1; i < vertices.length; i++) {
				// Skip if same coordinate as we need three unique
				// Vector2s in the set to perform a cross product

				if (nextHullIndex == indexHull) {
					nextHullIndex = i;
					continue;
				}

				// Cross every set of three unique vertices
				// Record each counter clockwise third vertex and add
				// to the output hull
				Vector2k e1 = vertices[nextHullIndex].sub(vertices[windingOrder[outCount]]);
				Vector2k e2 = vertices[i].sub(vertices[windingOrder[outCount]]);
				float c = Vector2k.cross(e1, e2);
				if (c < 0f)  nextHullIndex = i;

				// Cross product is zero then e vectors are on same line
				// therefore want to record vertex farthest along that line
				if (c == 0f && e2.magnitudeSquared() > e1.magnitudeSquared()) nextHullIndex = i;
			}

			outCount++;
			indexHull = nextHullIndex;

			// Conclude algorithm upon wrap-around
			if (nextHullIndex == rightMost) {
				vertexCount = outCount;
				return windingOrder;
			}
		}
    }

	/**
	 * @see {@link Collider#getFarthestFeature(Vector2k, Transform)}
	 */
	@Override
    public Feature getFarthestFeature(Vector2k direction, Transform transform) {
		EdgeFeature edge = new EdgeFeature();
		Vector2k normal = transform.getInverseRotated(direction);

		int index = getFarthestVertexIndex(normal);
		Vector2k max = new Vector2k(vertices[index]);

		// left and right normals
		Vector2k ln = normals[index == 0 ? vertexCount - 1 : index - 1];
		Vector2k rn = normals[index];

		transform.transformPoint(max);
		PointFeature vm = new PointFeature(max, index);

		// check which edge is more perpendicular to the normal
		if (ln.dot(normal) < rn.dot(normal)) {
			// left case
			int l = (index == vertexCount - 1 ? 0 : index + 1);

			Vector2k left = transform.getTransformed(vertices[l]);
			PointFeature vl = new PointFeature(left, l);
			edge.set(vm, vl, vm, max.sub(left), index + 1);
		} else {
			// only other case is right
			int r = (index == 0 ? vertexCount - 1 : index - 1);

			Vector2k right = transform.getTransformed(vertices[r]);
			PointFeature vr = new PointFeature(right, r);
			edge.set(vr, vm, vm, max.sub(right), index);
		}

		return edge;
    }

	public PointFeature getFarthestPointFeature(Vector2k direction, Transform transform) {
		float bestProjection = -Float.MAX_VALUE;
		Vector2k farthest = null;
		int index = -1;

		for (int i = 0; i < vertexCount; i++) {
			Vector2k v = transform.getTransformed(vertices[i]);
			float projection = v.dot(direction);

			if (projection > bestProjection) {
				bestProjection = projection;
				farthest = v;
				index = i;
			}
		}

		return new PointFeature(farthest, index);
	}

	/**
	 * @see {@link Collider#getFarthestPoint(Vector2k, Transform)}
	 */
	@Override
    public Vector2k getFarthestPoint(Vector2k direction, Transform transform) {
		Vector2k localDirection = transform.getInverseRotated(direction);
		return transform.getTransformed(vertices[getFarthestVertexIndex(localDirection)]);
	}

	/**
     * Only applicible to shapes with curved edges. Always returns null
     * for this shape.
     * 
     * @return null
     */
    @Override
    public Vector2k[] getFoci(Transform transform) {
        return null;
    }

	/**
	 * @see {@link Collider#getAxes(Vector2k[], Transform)}
	 */
    @Override
    public Vector2k[] getAxes(Vector2k[] foci, Transform transform) {
        int fociCount = foci == null ? 0 : foci.length;

		// the axes of a polygon are created from the normal of the edges
		// plus the closest point to each focus
		Vector2k[] axes = new Vector2k[fociCount + vertexCount];

		int n = 0;

		// loop over the edge normals and put them into world space
		for (int i = 0; i < vertexCount; i++) axes[n++] = transform.getRotated(normals[i]);

		for (int i = 0; i < fociCount; i++) {
			Vector2k focus = foci[i];

			Vector2k closest = transform.getTransformed(vertices[0]);
			float distance = focus.distanceSq(closest);

			for (int j = 1; j < vertexCount; j++) {
				// get the vertex
				Vector2k v = transform.getTransformed(vertices[j]);

				// get the squared distance to the focus
				float d = focus.distanceSq(v);

				// compare with the last distance
				if (d < distance) {
					closest = v;
					distance = d;
				}
			}

			// once we have found the closest point create 
			// a vector from the focal point to the point
			Vector2k axis = closest.sub(focus);

			axis.normalize();

			axes[n++] = axis;
		}

		return axes;
    }

	/**
	 * @see {@link Shape#getArea()}
	 */
    @Override
    public float getArea() {
        float area = 0f;
		
		// get the average center
		Vector2k ac = new Vector2k();
		for (int i = 0; i < vertexCount; i++) ac.add(this.vertices[i]);
		ac.divi(vertexCount);
		
		// loop through the vertices using two variables to avoid branches in the loop
		for (int i1 = vertexCount - 1, i2 = 0; i2 < vertexCount; i1 = i2++) {
			// get two vertices
			Vector2k p1 = this.vertices[i1];
			Vector2k p2 = this.vertices[i2];

			// get the vector from the center to the point
			p1 = p1.sub(ac);
			p2 = p2.sub(ac);

			// perform the cross product (yi * x(i+1) - y(i+1) * xi) & multiply by half
			float triangleArea = p1.cross(p2) * 0.5f;

			// add it to the total area
			area += triangleArea;
		}
		
		return area;
    }

	/**
	 * @see {@link Shape#getPerimeter()}
	 */
    @Override
    public float getPerimeter() {
        float perimeter = 0f;

        for (int i = 0; i < vertexCount; i++) {
            Vector2k v1 = vertices[i];
            Vector2k v2 = vertices[(i + 1) % vertexCount];
            perimeter += v1.distance(v2);
        }

        return perimeter;
    }

	/**
	 * @see {@link Wound#getVertices()}
	 */
    @Override
    public Vector2k[] getVertices() {
        return vertices;
    }

	/**
	 * @return the number of vertices
	 */
	public int getVertexCount() {
		return vertexCount;
	}

	/**
	 * @see {@link Wound#getNormals()}
	 */
    @Override
    public Vector2k[] getNormals() {
        return normals;
    }

 	// @Deprecated
	// int getFarthestVertexIndex(Vector2k vector) {
	// 	float bestDot = -Float.MAX_VALUE;
	// 	int bestIndex = -1;

	// 	for (int i = 0; i < vertexCount; i++) {
	// 		float dot = vector.dot(normals[i]);

	// 		if (dot > bestDot) {
	// 			bestDot = dot;
	// 			bestIndex = i;
	// 		}
	// 	}

	// 	return bestIndex;
	// }

	/**
	 * Improved method to get the farthest vertex index in a given
	 * direction. This is an internal method.
	 * 
	 * <p>
	 * Reference: <a href="https://github.com/dyn4j/dyn4j/blob/master/src/main/java/org/dyn4j/geometry/Polygon.java">dyn4j</a> (ln. 452)
	 * 
	 * @param {@link Vector2k} the direction
	 * @return {@code int} farthest vertex index
	 */
	@Internal
	int getFarthestVertexIndex(Vector2k vector) {
		/*
		 * The sequence a(n) = vector.dot(vertices[n]) has a maximum, a minimum and is monotonic (though not strictly monotonic) between those extrema.
		 * All indices are considered in modular arithmetic. I choose the initial index to be 0.
		 * 
		 * Based on that I follow this approach:
		 * We start from an initial index n0. We want to an adjacent to n0 index n1 for which a(n1) > a(n0).
		 * If no such index exists then n0 is the maximum. Else we start in direction of n1 (i.e. left or right of n0)
		 * and while a(n) increases we continue to that direction. When the next number of the sequence does not increases anymore
		 * we can stop and we have found max{a(n)}.
		 * 
		 * Although the idea is simple we need to be careful with some edge cases and the correctness of the algorithm in all cases.
		 * Although the sequence is not strictly monotonic the absence of equalities is intentional and wields the correct answer (see below).
		 * 
		 * The correctness of this method relies on some properties:
		 * 1) If n0 and n1 are two adjacent indices and a(n0) = a(n1) then a(n0) and a(n1) are either max{a(n)} or min{a(n)}.
		 *    This holds for all convex polygons. This property can guarantee that if our initial index is n0 or n1 then it does not
		 *    matter to which side (left or right) we start searching.
		 * 2) The polygon has no coincident vertices.
		 *    This guarantees us that there are no adjacent n0, n1, n2 for which a(n0) = a(n1) = a(n2)
		 *    and that only two adjacent n0, n1 can exist with a(n0) = a(n1). This is important because if
		 *    those adjacent n0, n1, n2 existed the code below would always return the initial index, without knowing if
		 *    it's a minimum or maximum. But since only two adjacent indices can exist with a(n0) = a(n1) the code below
		 *    will always start searching in one direction and because of 1) this will give us the correct answer.
		 */
		
		// The initial starting index and the corresponding dot product
		int maxIndex = 0;
		int n = this.vertices.length;
		float max = vector.dot(this.vertices[0]), candidateMax;
		
		if (max < (candidateMax = vector.dot(this.vertices[1]))) {
			// Search to the right
			do {
				max = candidateMax;
				maxIndex++;
			} while ((maxIndex + 1) < n && max < (candidateMax = vector.dot(this.vertices[maxIndex + 1])));
		} else if (max < (candidateMax = vector.dot(this.vertices[n - 1]))) {
			maxIndex = n; // n = 0 (mod n)
			
			// Search to the left
			do {
				max = candidateMax;
				maxIndex--;
			} while (maxIndex > 0 && max <= (candidateMax = vector.dot(this.vertices[maxIndex - 1])));
			//				  ,----------^^
			// The equality here makes this algorithm produce the same results with the old when there exist adjacent vertices
			// with the same a(n).
		}
		// else maxIndex = 0, because if neither of the above conditions is met, then the initial index is the maximum
		
		return maxIndex;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Polygon [normals=");
        builder.append(Arrays.toString(normals));
        builder.append(", vertexCount=");
        builder.append(vertexCount);
        builder.append(", vertices=");
        builder.append(Arrays.toString(vertices));
        builder.append("]");
        return builder.toString();
    }
    
}

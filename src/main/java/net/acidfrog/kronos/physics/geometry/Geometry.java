package net.acidfrog.kronos.physics.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.error.KronosGeometryError;
import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.mathk.Vector2k;

/**
 * Static geometry functions and variables.
 * 
 * @author Ethan Temprovich
 */
public final class Geometry {

	/** The max numbers of vertices suppored in a {@link Polygon}. */
    public static final byte MAX_POLYGON_VERTICES_COUNT = 63;

	/** One third, pre-computed. */
    public static final float INV_3 = 1f / 3f;

	/** Defines the direction of a clockwise winding (of a shape). */
	public static final int CLOCKWISE_WINDING = -1;

	/** Defines the direction of a counter-clockwise winding (of a shape). */
	public static final int COUNTER_CLOCKWISE_WINDING = 1;

	/** 
	 * @return the measure from the origin, or the center of the shape, to
	 * the farthest vertex.
	 * 
	 * @param vertices the vertices of the {@link Wound}
	 * @throws NullPointerException if vertices is null
	 * @return the measure from the origin, or the center of the {@link Wound}, to
	 */
	public static final float getRadius(Vector2k... vertices) {
		return getRadius(Vector2k.ZERO, vertices);
	}

	/** 
	 * @return the measure from the center of the shape, to
	 * the farthest vertex.
	 * 
	 * @param center the center of the {@link Wound}
	 * @param vertices the vertices of the {@link Wound}
	 * @throws NullPointerException if vertices is null
	 * @return the measure from the origin, or the center of the {@link Wound}, to
	 */
	public static final float getRadius(Vector2k center, Vector2k... vertices) {
		if (vertices == null) return 0f;
		if (center == null) center = new Vector2k(0f);

		int length = vertices.length;
		if (length == 0) return 0f;

		float r2 = 0f;

		for (int i = 0; i < length; i++) {
			Vector2k v = vertices[i];
			float dist = center.distanceSq(v);
			r2 = Mathk.max(r2, dist);
		}

		return Mathk.sqrt(r2);
	}

	/**
	 * Calculates the weighted average of the given vertices, which is the
	 * centroid of the {@link Wound}.
	 * 
	 * @param vertices the vertices of the {@link Wound}
	 * @return the weighted average of the given vertices
	 */
	public static final Vector2k getWeightedCentroid(List<Vector2k> vertices) {
		return getWeightedCentroid(vertices.toArray(Vector2k[]::new));
	}

	/**
	 * Calculates the weighted average of the given vertices, which is the
	 * centroid of the {@link Wound}.
	 * 
	 * @param vertices the vertices of the {@link Wound}
	 * @return the weighted average of the given vertices
	 */
	public static final Vector2k getWeightedCentroid(Vector2k... vertices) {
		Vector2k centroid = getCentroid(vertices);
		int length = vertices.length;

		Vector2k c = new Vector2k(0);
		float area = 0f;

		for (int i = 0; i < length; i++) {
			Vector2k v1 = vertices[i].sub(centroid);
			Vector2k v2 = vertices[(i + 1) % length].sub(centroid);

			// cross product
			float a = v1.y * v2.x - v2.y * v1.x;
			
			float triangleArea = a * 0.5f;

			area += triangleArea;

			// area weighted centroid
			// (p1 + p2) * (D / 3)
			// = (x1 + x2) * (yi * x(i+1) - y(i+1) * xi) / 3
			// we will divide by the total area later
			c.addi(v1.addi(v2).muli(INV_3).muli(triangleArea));
		}

		if (Mathk.abs(area) <= Mathk.FLOAT_ROUNDING_ERROR) return vertices[0].clone();
		
		return c.div(area).add(centroid);
	}

	/**
	 * Calculates the average center point of the given vertices.
	 * 
	 * @param vertices the vertices of the {@link Wound}
	 * @return the average center of the given vertices
	 */
	public static final Vector2k getCentroid(List<Vector2k> vertices) {
		return getCentroid(vertices.toArray(Vector2k[]::new));
	}

	/**
	 * Calculates the average center point of the given vertices.
	 * 
	 * @param vertices the vertices of the {@link Wound}
	 * @return the average center of the given vertices
	 */
	public static Vector2k getCentroid(Vector2k... vertices) {
		if (vertices == null) throw new KronosGeometryError(KronosErrorLibrary.NULL_POINTS);
		if (vertices.length == 0) return new Vector2k(0f);

		int length = vertices.length;
		if (length == 1) return vertices[0].clone();

		Vector2k average = new Vector2k(0f);
		for (Vector2k v : vertices) average.add(v);

		return average.div(length);
	}

	/**
	 * Calculates the location of the specified {@link Vector2k point} on the
	 * specified line. The line is represented by the 2 specified
	 * {@link Vector2k points}.
	 * 
	 * @param point the point to find the location of
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return the location of the specified {@link Vector2k point}, relative to the line
	 */
	public static float getLocationRelativeToSegment(Vector2k point, Vector2k linePoint1, Vector2k linePoint2) {
		return (linePoint2.x - linePoint1.x) * (point.y - linePoint1.y) -
			   (linePoint2.y - linePoint1.y) * (point.x - linePoint1.x);
	}

	/**
	 * Calculates the {@link Vector2k point} on the specified line that is
	 * closest to the specified {@link Vector2k point}. The line is represented by
	 * the 2 specified {@link Vector2k points}.
	 * 
	 * @param point the point to query
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return the closest {@link Vector2k point} on the line to the specified point
	 */
	public static final Vector2k getPointOnSegmentClosestToPoint(Vector2k point, Vector2k linePoint1, Vector2k linePoint2) {
		// create a vector from the point to the first line point
		Vector2k p1ToP = point.sub(linePoint1);
		// create a vector representing the line
	    Vector2k line = linePoint2.sub(linePoint1);
	    // get the length squared of the line
	    float ab2 = line.dot(line);
	    // get the projection of AP on AB
	    float ap_ab = p1ToP.dot(line);
	    // check ab2 for zero (linePoint1 == linePoint2)
	    if (ab2 <= Mathk.FLOAT_ROUNDING_ERROR) return linePoint1.clone();
	    // get the position from the first line point to the projection
	    float t = ap_ab / ab2;
	    // make sure t is in between 0.0 and 1.0
	    t = Mathk.clamp(t, 0.0f, 1.0f);
	    // create the point on the line
	    return line.mul(t).add(linePoint1);
	}

	/**
	 * Generates a new {@link Circle}, defined by the specified radius.
	 * 
	 * @param radius the radius of the {@link Circle}
	 * @return the new {@link Circle}
	 */
	public static Circle generateCircle(float radius) {
		return new Circle(radius);
	}

	/**
	 * Generates a new {@link Triangle}, defined by the width and height.
	 * 
	 * @param width the width of the {@link Triangle}
	 * @param height the height of the {@link Triangle}
	 * @return the new {@link Triangle}
	 */
	public static Triangle generateTriangle(float width, float height) {
		Vector2k pointA = new Vector2k(width * 2, height / 2);
		Vector2k pointB = new Vector2k(pointA.x * Mathk.random(), pointA.y * Mathk.random());
		Vector2k pointC = new Vector2k(0, height * 2);
		return new Triangle(pointA, pointB, pointC);
	}

	/**
	 * Generates a new {@link Triangle}, defined by the given {@link Vector2k points}.
	 * 
	 * @param a the first point of the {@link Triangle}
	 * @param b the second point of the {@link Triangle}
	 * @param c the third point of the {@link Triangle}
	 * @return the new {@link Triangle}
	 */
	public static Triangle generateTriangle(Vector2k a, Vector2k b, Vector2k c) {
		return new Triangle(a, b, c);
	}

	/**
	 * Generates a new {@link Rectangle}, defined by the given width and height.
	 * 
	 * @param width the width of the {@link Rectangle}
	 * @param height the height of the {@link Rectangle}
	 * @return the new {@link Rectangle}
	 */
	public static Rectangle generateRectangle(float width, float height) {
		return new Rectangle(width, height);
	}

	public static Square geneateSquare(float length) {
		return new Square(length);
	}

	/**
	 * Generates a new (random) {@link Polygon} with the specified width and height.
	 * The width and height define the extreme verticies.
	 * 
	 * @param width the width of the {@link Polygon}
	 * @param height the height of the {@link Polygon}
	 * @return the new {@link Polygon}
	 */
    public static Polygon generatePolygon(float width, float height) {
		return generatePolygon(width, height, Mathk.random(4, MAX_POLYGON_VERTICES_COUNT));
	}

	/**
	 * Generates a new (random) {@link Polygon} with the specified width, height, and vertex
	 * count. The width and height define the extreme verticies.
	 * 
	 * @param width the width of the {@link Polygon}
	 * @param height the height of the {@link Polygon}
	 * @param n the vertex count
	 * @return the new {@link Polygon}
	 */
    public static Polygon generatePolygon(float width, float height, int n) {
		// Generate two lists of random X and Y coordinates
		List<Float> xPool = new ArrayList<Float>(n);
		List<Float> yPool = new ArrayList<Float>(n);

		for (int i = 0; i < n; i++) {
			xPool.add((float) Mathk.random(-width, width));
			yPool.add((float) Mathk.random(-height, width));
		}

		// Sort them
		Collections.sort(xPool);
		Collections.sort(yPool);

		// Isolate the extreme points
		Float minX = xPool.get(0);
		Float maxX = xPool.get(n - 1);
		Float minY = yPool.get(0);
		Float maxY = yPool.get(n - 1);

		// Divide the interior points into two chains & Extract the vector components
		List<Float> xVec = new ArrayList<Float>(n);
		List<Float> yVec = new ArrayList<Float>(n);

		float lastTop = minX, lastBot = minX;

		for (int i = 1; i < n - 1; i++) {
			float x = xPool.get(i);

			if (Mathk.randomSign() > 0) {
				xVec.add(x - lastTop);
				lastTop = x;
			} else {
				xVec.add(lastBot - x);
				lastBot = x;
			}
		}

		xVec.add(maxX - lastTop);
		xVec.add(lastBot - maxX);

		float lastLeft = minY, lastRight = minY;

		for (int i = 1; i < n - 1; i++) {
			float y = yPool.get(i);

			if (Mathk.randomSign() > 0) {
				yVec.add(y - lastLeft);
				lastLeft = y;
			} else {
				yVec.add(lastRight - y);
				lastRight = y;
			}
		}

		yVec.add(maxY - lastLeft);
		yVec.add(lastRight - maxY);

		// Randomly pair up the X- and Y-components
		Collections.shuffle(yVec);

		// Combine the paired up components into vectors
		List<Vector2k> vec = new ArrayList<Vector2k>(n);

		for (int i = 0; i < n; i++) vec.add(new Vector2k(xVec.get(i), yVec.get(i)));

		// Sort the vectors by angle
		Collections.sort(vec, Comparator.comparingDouble(v -> Math.atan2(v.y, v.x)));

		// Lay them end-to-end
		float x = 0, y = 0;
		float minPolygonX = 0;
		float minPolygonY = 0;
		ArrayList<Vector2k> points = new ArrayList<Vector2k>(n);

		for (int i = 0; i < n; i++) {
			points.add(new Vector2k(x, y));

			x += vec.get(i).x;
			y += vec.get(i).y;

			minPolygonX = Math.min(minPolygonX, x);
			minPolygonY = Math.min(minPolygonY, y);
		}

		// Move the polygon to the original min and max coordinates
		float xShift = minX - minPolygonX;
		float yShift = minY - minPolygonY;

		for (int i = 0; i < n; i++) {
			Vector2k p = points.get(i);
			points.set(i, new Vector2k(p.x + xShift, p.y + yShift));
		}

        return new Polygon(points);
	}

}

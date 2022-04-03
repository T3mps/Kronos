package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.core.util.Validatable;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.error.KronosGeometryError;

/**
 * Represents an axis-aligned bounding box. Common {@link AABB}
 * representations are (a) min-max, (b) min-widths and (c)
 * center-radius. (b) and (c) are the most efficient representations
 * in terms of storage, however, this class is represented in the
 * min-max way, as the tests are more elegant and efficient. The best 
 * feature of the {@link AABB} is its {@link AABB#intersects(AABB) fast overlap check}, which simply
 * involves direct comparison of individual coordinates.
 * 
 * @author Ethan Temprovich
 */
public final class AABB implements Comparable<AABB>, Validatable {

    /** The top left coordinate */
    public final Vector2k min = new Vector2k(0);

    /** The bottom right coordinate */
    public final Vector2k max = new Vector2k(0);

    /**
     * Default constructor.
     */
    public AABB() {
        this(Vector2k.ZERO, Vector2k.ZERO);
    }

    /**
     * Full constructor.
     * 
     * @param min
     * @param max
     */
    public AABB(Vector2k min, Vector2k max) {
        set(min, max);
    }

    /**
     * Sets the min and max coordinates, &lt;\x1, y1\&gt; &lt;\x2, y2\&gt;
     * 
     * @param x1 min x
     * @param y1 min y
     * @param x2 max x
     * @param y2 max y
     */
    public AABB(float x1, float y1, float x2, float y2) {
        set(x1, y1, x2, y2);
    }

    public AABB(Vector2k... points) {
        setFromPoints(points);
    }

    /**
     * Copy constructor.
     * 
     * @param other {@link AABB} to copy
     */
    public AABB(AABB other) {
        set(other);
    }

    /**
     * Sets this {@link AABB} to the specified {@link Vector2k min}
     * and {@link Vector2k max} coordinates.
     * 
     * @param min
     * @param max
     */
    public void set(Vector2k min, Vector2k max) {
        this.min.set(min);
        this.max.set(max);
    }

    /**
     * Sets this {@link AABB} to the specified {@code x1}, {@code y1},
     * {@code x2}, {@code y2} coordinates.
     * 
     * @param x1 min x
     * @param y1 min y
     * @param x2 max x
     * @param y2 max y
     */
    public void set(float x1, float y1, float x2, float y2) {
        this.min.set(x1, y1);
        this.max.set(x2, y2);
    }

    /**
     * Sets this {@link AABB} to the specified {@link AABB}.
     * 
     * @param other {@link AABB} to copy
     */
    public AABB set(AABB other) {
        this.min.set(other.min);
        this.max.set(other.max);
        return this;
    }

    /**
     * Iterates through the given points and sets the min
     * and max values to the min and max values of the set.
     * 
     * @param points {@link Vector2k}[] to use
     */
    public void setFromPoints(Vector2k... points) {
        if (points.length < 2) throw new KronosGeometryError(KronosErrorLibrary.INVALID_AABB_ERROR);

        float minX, minY, maxX, maxY;

        if (points[0].x < points[1].x) {
            minX = points[0].x;
            maxX = points[1].x;
        } else {
            minX = points[1].x;
            maxX = points[0].x;
        }

        if (points[0].y < points[1].y) {
            minY = points[0].y;
            maxY = points[1].y;
        } else {
            minY = points[1].y;
            maxY = points[0].y;
        }

        for (int i = 2; i < points.length; i++) {
            Vector2k point = points[i];
            if (point.x < minX) minX = point.x;
            if (point.y < minY) minY = point.y;
            if (point.x > maxX) maxX = point.x;
            if (point.y > maxY) maxY = point.y;
        }

        this.min.set(minX, minY);
        this.max.set(maxX, maxY);
    }

    /**
     * Determines if this {@link AABB} is valid. A valid {@link AABB}
     * is defined as having a min coordinate less than the max coordinate.
     * 
     * @return {@code true} if valid, {@code false} otherwise
     */
    public boolean validate() {
        return min.x <= max.x && min.y <= max.y;
    }

    public void zero() {
        min.zero();
        max.zero();
    }

    public boolean contains(float x, float y) {
        return x >= min.x &&
               x <= max.x &&
               y >= min.y &&
               y <= max.y;
    }

    public boolean contains(Vector2k point) {
        return contains(point.x, point.y);
    }

    public boolean contains(AABB aabb) {
        return this.min.x <= aabb.min.x &&
               this.max.x >= aabb.max.x &&
               this.min.y <= aabb.min.y &
               this.max.y >= aabb.max.y;
    }

    public boolean intersects(AABB other) {
        return min.x <= other.max.x &&
               max.x >= other.min.x &&
               min.y <= other.max.y &&
               max.y >= other.min.y;
    }

	public void translate(float x, float y) {
        this.min.x += x;
        this.min.y += y;
        this.max.x += x;
        this.max.y += y;
	}

    public void translate(Vector2k v) {
        translate(v.x, v.y);
    }

    public AABB union(AABB aabb) {
        this.min.x = Mathk.min(this.min.x, aabb.min.x);
        this.min.y = Mathk.min(this.min.y, aabb.min.y);
        this.max.x = Mathk.max(this.max.x, aabb.max.x);
        this.max.y = Mathk.max(this.max.y, aabb.max.y);
        return this;
    }

    public AABB union(AABB aabb1, AABB aabb2) {
        this.min.x = Mathk.min(aabb1.min.x, aabb2.min.x);
        this.min.y = Mathk.min(aabb1.min.y, aabb2.min.y);
        this.max.x = Mathk.max(aabb1.max.x, aabb2.max.x);
        this.max.y = Mathk.max(aabb1.max.y, aabb2.max.y);
        return this;
    }

    public AABB expand(float expansion) {
		float e = expansion * 0.5f;
        min.x -= e;
        min.y -= e;
        max.x += e;
        max.y += e;
        
		if (expansion < 0f) {
			// if the aabb is invalid then set the min/max(es) to
			// the middle value of their current values
			if (min.x > max.x) {
                float m = (min.x + max.x) * 0.5f;
                min.x = m;
                max.x = m;
            }

            if (min.y > max.y) {
                float m = (min.y + max.y) * 0.5f;
                min.y = m;
                max.y = m;
            }
		}
		return this;
	}

    public boolean isDegenerate() {
        return min.equals(max);
    }

    public AABB getTranslated(Vector2k v) {
        AABB aabb = new AABB(this);
        aabb.translate(v);
        return aabb;
    }
    
    public AABB getUnion(AABB other) {
        AABB union = new AABB(this);
        union.union(other);
        return union;
    }

    public Vector2k getExtents() {
        return max.sub(min);
    }

    public float getWidth() {
        return max.x - min.x;
    }

    public float getHeight() {
        return max.y - min.y;
    }

    public float getArea() {
        return getExtents().x * getExtents().y;
    }

    public float getPerimeter() {
        return 2 * (getExtents().x + getExtents().y);
    }
    
    public Vector2k getMin() {
        return min;
    }

    public void setMin(Vector2k min) {
        this.min.set(min);
    }

    public Vector2k getMax() {
        return max;
    }

    public void setMax(Vector2k max) {
        this.max.set(max);
    }

    /**
     * Compares this AABB to the given AABB based on area;
     */
    @Override
    public int compareTo(AABB o) {
        return Float.compare(getPerimeter(), o.getPerimeter());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result + ((min == null) ? 0 : min.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AABB)) return false;
        AABB other = (AABB) obj;
        if (max == null) {
            if (other.max != null) return false;
        } else if (!max.equals(other.max)) return false;
        if (min == null) {
            if (other.min != null) return false;
        } else if (!min.equals(other.min)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AABB [max=");
        builder.append(max);
        builder.append(", min=");
        builder.append(min);
        builder.append("]");
        return builder.toString();
    }
    
}

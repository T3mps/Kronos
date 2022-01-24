package net.acidfrog.kronos.core.lang.error;

/**
 * Error Library for Kronos.
 * 
 * @author Ethan Temprovich
 */
public final class KronosErrorLibrary {
    
    public final record KronosError(String message) {   }

    public static final KronosError NOT_IMPLEMENTED = new KronosError("Function not implemented");
    public static final KronosError INVALID_OFFSET = new KronosError("Invalid offset");
    public static final KronosError GEOMETRY_ERROR = new KronosError("Geometry error");
    public static final KronosError GEOMETRY_NULL_POINTS = new KronosError("Points array contains null values");
    public static final KronosError ZERO_MATRIX_TRANSFORM_ATTEMPT = new KronosError("Attempted to transform a zero matrix");
    public static final KronosError WINDING_ERROR = new KronosError("Invalid winding");
    public static final KronosError INVALID_CIRCLE_ERROR = new KronosError("Invalid circle: radius must be greater than zero");
    public static final KronosError INVALID_AABB_ERROR = new KronosError("Invalid AABB: maximum point must be greater than minimum point");
    public static final KronosError INVALID_TRIANGLE_ERROR = new KronosError("Invalid triangle: must have exactly 3 vertices");
    public static final KronosError INVALID_POLYGON_ERROR = new KronosError("Invalid polygon: does not follow a counter-clockwise winding and/or is not convex");
    public static final KronosError POLYGON_LESS_THAN_THREE_VERTICES_ERROR = new KronosError("Invalid polygon: must have at least three vertices");
    public static final KronosError POLYGON_TOO_MANY_VERTICES_ERROR = new KronosError("Invalid polygon: must have no more than 64 vertices");
    public static final KronosError POLYGON_CONSECUTIVE_VERTICES_ERROR = new KronosError("Invalid polygon: consecutive vertices");
    public static final KronosError POLYGON_IS_LINE_ERROR = new KronosError("Invalid polygon: polygon contains more than 2 vertices with unchanging angle");
    public static final KronosError POLYGON_IS_CONCAVE_ERROR = new KronosError("Invalid polygon: polygon is not convex");
    public static final KronosError MATRIX_NOT_2X2 = new KronosError("Invalid Matrix2: must be 2x2");
    public static final KronosError MATRIX_NOT_3X3 = new KronosError("Invalid Matrix3: must be 2x2");
    public static final KronosError MATRIX_NOT_INVERTIBLE = new KronosError("Invalid Matrix: matrix determinant is zero");
    public static final KronosError OFFSET_OUT_OF_BOUNDS = new KronosError("Offset out of bounds");

}

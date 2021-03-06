package net.acidfrog.kronos.core.lang.error;

/**
 * Error Library for Kronos.
 * 
 * @author Ethan Temprovich
 */
public final class KronosErrorLibrary {
    
    public final record KronosErrorMessage(String message) {   }

    // GENERAL ERRORS
    public static final KronosErrorMessage INTERNAL_APPLICATION_STATE_ERROR = new KronosErrorMessage("Internal application state error");
    public static final KronosErrorMessage NOT_IMPLEMENTED = new KronosErrorMessage("Function not implemented");
    public static final KronosErrorMessage INVALID_OFFSET = new KronosErrorMessage("Invalid offset");
    public static final KronosErrorMessage OFFSET_OUT_OF_BOUNDS = new KronosErrorMessage("Offset out of bounds");
    public static final KronosErrorMessage INVALID_COMPARISON_TARGET = new KronosErrorMessage("Invalid comparison target");
    public static final KronosErrorMessage INDEX_OUT_OF_BOUNDS = new KronosErrorMessage("The specified index is out of bounds");
    public static final KronosErrorMessage INVALID_ZERO_POINT_CONSTRUCTOR = new KronosErrorMessage("Invalid zero point constructor, type must contain a constructor with no parameters");

    // IO
    public static final KronosErrorMessage FILE_NOT_FOUND = new KronosErrorMessage("File not found");
    public static final KronosErrorMessage FILE_IS_DIRECTORY = new KronosErrorMessage("File is a directory; can not stream a directory");
    public static final KronosErrorMessage FILE_READ_ERROR = new KronosErrorMessage("File read error");

    // ECS
    public static final KronosErrorMessage INVALID_COMPONENT = new KronosErrorMessage("Invalid component");
    public static final KronosErrorMessage NULL_COMPONENT = new KronosErrorMessage("Null component");
    public static final KronosErrorMessage COMPONENT_NOT_FOUND = new KronosErrorMessage("Component not found");
    public static final KronosErrorMessage COMPONENT_MODIFICATION_WHILE_ENABLED = new KronosErrorMessage("Cannot add/remove/replace component while entity is enabled");
    public static final KronosErrorMessage COMPONENT_NOT_ATTACHED = new KronosErrorMessage("Component is not attached to an entity");
    public static final KronosErrorMessage COMPONENT_ALREADY_ATTACHED = new KronosErrorMessage("Component already attached to an entity");
    public static final KronosErrorMessage COMPONENT_ALREADY_ENABLED = new KronosErrorMessage("Component already enabled");
    public static final KronosErrorMessage COMPONENT_ALREADY_DISABLED = new KronosErrorMessage("Component already disabled");
    public static final KronosErrorMessage ENTITY_ALREADY_ENABLED = new KronosErrorMessage("Entity already enabled");
    public static final KronosErrorMessage ENTITY_NOT_ENABLED = new KronosErrorMessage("Entity is not enabled");
    public static final KronosErrorMessage ENTITY_NOT_ADDED_TO_THIS_ENGINE = new KronosErrorMessage("Entity is not added to this registry");
    public static final KronosErrorMessage ENTITY_ALREADY_ADDED_TO_ENGINE = new KronosErrorMessage("Entity already added to registry");
    public static final KronosErrorMessage ENTITY_NOT_BOUND_TO_ENGINE = new KronosErrorMessage("Entity is not bound to this registry");
    public static final KronosErrorMessage COMPONENT_COUNT_MISMATCH = new KronosErrorMessage("Uneven compairison attempted, component count does not match");
    public static final KronosErrorMessage UNKNOWN_ENTITY_SYSTEM = new KronosErrorMessage("Operation with unknown entity system attempted");
    public static final KronosErrorMessage ATTEMPTED_SYSTEM_BIND_DURING_UPDATE = new KronosErrorMessage("Attempted to bind system during registry update");
    public static final KronosErrorMessage ATTEMPTED_SYSTEM_UNBIND_DURING_UPDATE = new KronosErrorMessage("Attempted to unbind system during registry update");
    public static final KronosErrorMessage SYSTEM_NOT_BOUND = new KronosErrorMessage("System is not bound to this SYSTEM_NOT_BOUND");

    // COLLECTIONS ERRORS
    public static final KronosErrorMessage COLLECTION_IS_EMPTY = new KronosErrorMessage("Collection is empty");
    public static final KronosErrorMessage INVALID_ELEMENT = new KronosErrorMessage("Invalid element");
    public static final KronosErrorMessage NO_SUCH_ELEMENT = new KronosErrorMessage("No such element");
    public static final KronosErrorMessage NULL_ELEMENT = new KronosErrorMessage("Null element");
    public static final KronosErrorMessage NULL_TREE_NODE_VALUE = new KronosErrorMessage("Null tree node value");
    public static final KronosErrorMessage INVALID_NODE_TYPE = new KronosErrorMessage("Invalid node type");

    // PHYSICS / MATH ERRORS
    public static final KronosErrorMessage GEOMETRY_ERROR = new KronosErrorMessage("Geometry error");
    public static final KronosErrorMessage NULL_POINTS = new KronosErrorMessage("Points array contains null values");
    public static final KronosErrorMessage ZERO_MATRIX_TRANSFORM_ATTEMPT = new KronosErrorMessage("Attempted to transform a zero matrix");
    public static final KronosErrorMessage WINDING_ERROR = new KronosErrorMessage("Invalid winding");
    public static final KronosErrorMessage INVALID_CIRCLE_ERROR = new KronosErrorMessage("Invalid circle: radius must be greater than zero");
    public static final KronosErrorMessage INVALID_AABB_ERROR = new KronosErrorMessage("Invalid AABB: maximum point must be greater than minimum point");
    public static final KronosErrorMessage INVALID_TRIANGLE_ERROR = new KronosErrorMessage("Invalid triangle: must have exactly 3 vertices");
    public static final KronosErrorMessage INVALID_POLYGON_ERROR = new KronosErrorMessage("Invalid polygon: does not follow a counter-clockwise winding and/or is not convex");
    public static final KronosErrorMessage POLYGON_LESS_THAN_THREE_VERTICES_ERROR = new KronosErrorMessage("Invalid polygon: must have at least three vertices");
    public static final KronosErrorMessage POLYGON_TOO_MANY_VERTICES_ERROR = new KronosErrorMessage("Invalid polygon: must have no more than 64 vertices");
    public static final KronosErrorMessage POLYGON_CONSECUTIVE_VERTICES_ERROR = new KronosErrorMessage("Invalid polygon: consecutive vertices");
    public static final KronosErrorMessage POLYGON_IS_LINE_ERROR = new KronosErrorMessage("Invalid polygon: polygon contains more than 2 vertices with unchanging angle");
    public static final KronosErrorMessage POLYGON_IS_CONCAVE_ERROR = new KronosErrorMessage("Invalid polygon: polygon is not convex");
    public static final KronosErrorMessage MATRIX_NOT_2X2 = new KronosErrorMessage("Invalid Matrix2: must be 2x2");
    public static final KronosErrorMessage MATRIX_NOT_3X3 = new KronosErrorMessage("Invalid Matrix3: must be 2x2");
    public static final KronosErrorMessage MATRIX_NOT_INVERTIBLE = new KronosErrorMessage("Invalid Matrix: matrix determinant is zero");
    public static final KronosErrorMessage INVALID_INTERVAL = new KronosErrorMessage("Invalid interval: minimum must be less than maximum");
    public static final KronosErrorMessage RAY_ZERO_DIRECTION = new KronosErrorMessage("Invalid ray: direction must be non-zero");
    public static final KronosErrorMessage NULL_BODY = new KronosErrorMessage("Invalid body: body must not be null");

    // RENDERING ERRORS
    public static final KronosErrorMessage GLFW_INIT_FAILED = new KronosErrorMessage("Unable to initialize GLFW");
    public static final KronosErrorMessage GLFW_WINDOW_CREATION_FAILED = new KronosErrorMessage("Unable to create GLFW window");
    public static final KronosErrorMessage SHADER_FILE_NOT_FOUND = new KronosErrorMessage("Shader file not found");
    public static final KronosErrorMessage SHADER_HEADER_NOT_PRESENT = new KronosErrorMessage("Shader header not present; a shader must define its type");
    public static final KronosErrorMessage VERTEX_OR_FRAGMENT_SHADER_NOT_FOUND = new KronosErrorMessage("Vertex or fragment shader not found");
    public static final KronosErrorMessage UNSUPPORTED_SHADER_TYPE = new KronosErrorMessage("Unsupported shader type detected");
    public static final KronosErrorMessage VERTEX_SHADER_COMPILATION_FAILED = new KronosErrorMessage("Vertex shader compilation failed");
    public static final KronosErrorMessage FRAGMENT_SHADER_COMPILATION_FAILED = new KronosErrorMessage("Fragment shader compilation failed");
    public static final KronosErrorMessage SHADER_LINKING_FAILED = new KronosErrorMessage("Shader linking failed");
    public static final KronosErrorMessage ATTEMPTED_DRAW_CALL_LACKING_DATA = new KronosErrorMessage("Attempted a draw call without appropriate data");
    public static final KronosErrorMessage TEXTURE_NOT_FOUND = new KronosErrorMessage("Texture not found");
    public static final KronosErrorMessage SPRITE_COUNT_MISMATCH = new KronosErrorMessage("Expected sprite count does not match actual sprite count");
    public static final KronosErrorMessage DUPLICATE_SPRITESHEET = new KronosErrorMessage("Attempted to load a spritesheet that is already loaded");

}

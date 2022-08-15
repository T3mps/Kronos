package net.acidfrog.kronos.serialization;

public enum ContainerType {
    
    PRIMITIVE,
    ARRAY,
    OBJECT;

    public static ContainerType get(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        if (clazz.isPrimitive()) {
            return PRIMITIVE;
        }

        if (clazz.isArray()) {
            return ARRAY;
        }

        return OBJECT;
    }

    public static ContainerType get(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj.getClass().isPrimitive()) {
            return PRIMITIVE;
        }

        if (obj.getClass().isArray()) {
            return ARRAY;
        }

        return OBJECT;
    }
}

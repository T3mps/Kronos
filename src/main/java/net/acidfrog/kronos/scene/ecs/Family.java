package net.acidfrog.kronos.scene.ecs;

import java.util.HashSet;
import java.util.Set;

import net.acidfrog.kronos.scene.ecs.component.Component;

/**
 * A family is a set of {@link Component components}. It is used to filter {@link Entity entities}.
 * 
 * @since 0.0.2
 * @version 0.0.2
 * @author Ethan Temprovich
 */
public final class Family {

    /** Holds all associated types */
    private final Set<Class<?>> types = new HashSet<Class<?>>();

    /**
     * (<em>Hidden Constructor</em>)
     * 
     * <p>
     * Creates a new family with the specified types.
     * 
     * @param types
     */
    @SafeVarargs
    private Family(final Class<?>... types) {
        if (types.length == 0) throw new IllegalArgumentException("Family must have at least one type");
        for (Class<?> type : types) this.types.add(type);
    }

    /**
     * (<em>Hidden Constructor</em>)
     * 
     * <p>
     * Copy constructor.
     * 
     * @param types
     */
    private Family(final Family family) {
        this.types.addAll(family.types);
    }

    /**
     * Static factory method to create a new family with the specified types.
     * 
     * @param types
     * @return the new instance
     */
    @SafeVarargs
    public static Family define(final Class<?>... types) {
        return new Family(types);
    }

    /**
     * Determines if the given {@link Entity} contains all the {@link Component components}
     * described by this family.
     * 
     * @param entity
     * @return true if the entity has all the types in this family, false otherwise
     */
    public final boolean includes(final Entity entity) {
        for (Class<?> type : types) if (!entity.has(type)) {
            return false;
        }

        return true;
    }

    /**
     * Determines if the given {@link Entity} <strong>does not</strong> contain any of the
     * {@link Component components} described by this family.
     * 
     * @param entity
     * @return true if the entity does not have any of the types in this family, false otherwise
     */
    public final boolean excludes(final Entity entity) {
        for (Class<?> type : types) if (entity.has(type)) {
            return false;
        }

        return true;
    }

    /**
     * Determines if the given {@link Entity} contains <em>any</em> of the {@link Component components}
     * described by this family.
     * 
     * @param entity
     * @return true if the entity has any of the types in this family, false otherwise
     */
    public final boolean isRelated(final Entity entity) {
        boolean related = false;
        for (Class<?> type : types) if (entity.has(type)) {
            related = true;
            break;
        }

        return related;
    }

    /**
     * Determines if the given type is contained in this family.
     * 
     * @param type
     * @return true if the type is contained in this family, false otherwise
     */
    public final boolean has(final Class<?> type) {
        return types.contains(type);
    }

    /**
     * @return All types included in this family.
     */
    public final Class<?>[] getTypes() {
        return types.toArray(new Class<?>[0]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((types == null) ? 0 : types.hashCode());
        return result;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Family)) return false;
        Family other = (Family) obj;
        if (types == null) {
            if (other.types != null) return false;
        } else if (!types.equals(other.types)) return false;
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Family [types=");
        for (Class<?> type : types) {
            builder.append(type.getSimpleName());
            builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }

}

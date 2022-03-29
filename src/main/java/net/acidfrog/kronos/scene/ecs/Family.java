package net.acidfrog.kronos.scene.ecs;

import java.util.HashSet;
import java.util.Set;

import net.acidfrog.kronos.scene.ecs.component.Component;

public final class Family {

    private final Set<Class<?>> types = new HashSet<Class<?>>();

    private Family(Class<?>... types) {
        for (Class<?> type : types) this.types.add(type);
    }

    public static Family define(Class<?>... types) {
        return new Family(types);
    }

    public static Family define(Family family) {
        return new Family(family.types.toArray(new Class<?>[0]));
    }

    public static Family define(Entity entity) {
        Family family = new Family();
        for (Component component : entity.getComponents()) family.types.add(component.getClass());
        return family;
    }

    public static Family define(Family original, Class<?>... types) {
        Family family = new Family(original.types.toArray(new Class<?>[0]));
        for (Class<?> type : types) family.types.add(type);
        return family;
    }

    public boolean isMember(Entity e) {
        for (Class<?> type : types) if (!e.has(type)) {
            return false;
        }
        return true;
    }

    public boolean isRelated(Entity e) {
        for (Class<?> type : types) if (e.has(type)) {
            return true;
        }
        return false;
    }

    public boolean isSubsetOf(Family family) {
        for (Class<?> type : types) if (!family.types.contains(type)) {
            return false;
        }
        return true;
    }

    public boolean isSupersetOf(Family family) {
        for (Class<?> type : family.types) if (!types.contains(type)) {
            return false;
        }
        return true;
    }

    public boolean isDisjointFrom(Family family) {
        for (Class<?> type : types) if (family.types.contains(type)) {
            return false;
        }
        return true;
    }

    public boolean has(Class<?> type) {
        return types.contains(type);
    }

    public Class<?>[] getTypes() {
        return types.toArray(new Class<?>[0]);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((types == null) ? 0 : types.hashCode());
        return result;
    }

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

}

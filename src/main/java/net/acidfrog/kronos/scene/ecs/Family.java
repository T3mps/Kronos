package net.acidfrog.kronos.scene.ecs;

import java.util.HashSet;
import java.util.Set;

public class Family {

    private Set<Class<?>> types = new HashSet<Class<?>>();

    private Family(Class<?>... types) {
        for (Class<?> type : types) this.types.add(type);
    }

    public static Family define(Class<?>... types) {
        return new Family(types);
    }

    public static Family define(Family family) {
        return new Family(family.types.toArray(new Class<?>[0]));
    }

    public static Family define(Family original, Class<?>... types) {
        Family family = new Family(original.types.toArray(new Class<?>[0]));
        for (Class<?> type : types) family.types.add(type);
        return family;
    }

    public boolean isMember(Entity e) {
        for (Class<?> type : types) if (!e.hasComponent(type)) {
            return false;
        }
        return true;
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

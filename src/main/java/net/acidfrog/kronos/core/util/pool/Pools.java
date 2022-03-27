package net.acidfrog.kronos.core.util.pool;

import java.util.HashMap;
import java.util.Map;

import net.acidfrog.kronos.core.datastructure.Array;
import net.acidfrog.kronos.core.lang.annotations.Internal;

public final @Internal class Pools {

    private static final Map<Class<?>, Pool<?>> pools = new HashMap<Class<?>, Pool<?>>();

    private Pools() {}

    @SuppressWarnings("unchecked")
    public static <T> Pool<T> get(Class<T> type, int max) {
        Pool<T> pool = (Pool<T>) pools.get(type);
        if (pool == null) {
            pool = new ZeroPointPool<T>(type, 4, max);
            pools.put(type, pool);
        }
        return pool;
    }

    public static <T> Pool<T> get(Class<T> type) {
        return get(type, Integer.MAX_VALUE);
    }
    
    public static <T> T obtain(Class<T> type) {
        return get(type).get();
    }

    public static <T> void place(Class<T> type, Pool<T> pool) {
        pools.put(type, pool);
    }

    public static void free(Object object) {
        if (object == null) throw new NullPointerException();
        Pool<?> pool = pools.get(object.getClass());
        if (pool == null) return;
        pool.free(object);
    }
    
    static public void freeAll (Array<?> objects) {
		freeAll(objects, false);
	}

	static public void freeAll (Array<?> objects, boolean samePool) {
		if (objects == null) throw new IllegalArgumentException("objects cannot be null.");
		Pool<?> pool = null;

		for (int i = 0, n = objects.size(); i < n; i++) {
			Object object = objects.get(i);
		
            if (object == null) continue;
			if (pool == null) {
				pool = pools.get(object.getClass());
		
                if (pool == null) continue; // Ignore freeing an object that was never retained.
			}
		
            pool.free(object);
			if (!samePool) pool = null;
		}
	}

}

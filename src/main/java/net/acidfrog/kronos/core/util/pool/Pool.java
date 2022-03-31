package net.acidfrog.kronos.core.util.pool;

import net.acidfrog.kronos.core.datastructure.DynamicArray;
import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.mathk.Mathk;

/**
 * A pool of objects that can be reused to avoid allocation.
 * 
 * @author Ethan Temprovich
 */
public abstract @Internal class Pool<T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public final int capacity;

    public int peak;

    private final DynamicArray<T> pool;

    public Pool() {
        this(DEFAULT_INITIAL_CAPACITY, Integer.MAX_VALUE);
    }

    public Pool(int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    public Pool(int initialCapacity, int maxCapacity) {
        this.pool = new DynamicArray<T>(initialCapacity);
        this.capacity = maxCapacity;
    }

    public Pool(DynamicArray<T> pool) {
        this.pool = pool;
        this.capacity = Integer.MAX_VALUE;
    }

    public Pool(Pool<T> pool) {
        this.pool = pool.pool;
        this.capacity = pool.capacity;
    }

    protected abstract T create();

    public T get() {
        return pool.size() == 0 ? create() : pool.pop();
    }

    @SuppressWarnings("unchecked")
    public void free(Object object) {
        if (object == null) throw new NullPointerException();
        if (!pool.get(0).getClass().isAssignableFrom(object.getClass())) throw new IllegalArgumentException("Object is not of the correct type.");
        T t = (T) object;
        if (pool.size() < capacity) {
            pool.add(t);
            peak = Mathk.max(peak, pool.size());
            reset(t);
            return;
        }

        discard(t);
    }

    public void fill(int size) {
		for (int i = 0; i < size; i++) if (pool.size() < capacity) {
            pool.add(create());
        }

        peak = Mathk.max(peak, pool.size());
	}

    protected void reset(T t) {
		if (t instanceof Poolable) ((Poolable) t).reset();
	}

    protected void discard(T object) {
		reset(object);
	}

    public void freeAll(DynamicArray<T> objects) {
        if (objects == null) throw new NullPointerException();
		DynamicArray<T> freeObjects = this.pool;
		int max = this.capacity;

		for (int i = 0, n = objects.size(); i < n; i++) {
			T object = objects.get(i);
			if (object == null) continue;
			if (freeObjects.size() < max) {
				freeObjects.add(object);
				reset(object);
			} else discard(object);
		}
        
        peak = Mathk.max(peak, freeObjects.size());
    }

    public void clear() {
		DynamicArray<T> pool = this.pool;
		for (int i = 0, n = pool.size(); i < n; i++) discard(pool.get(i));
		pool.clear();
	}

	public int getFree() {
		return pool.size();
	}

	public static interface Poolable {

		public void reset();

	}
    
}

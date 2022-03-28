package net.acidfrog.kronos.physics.collision.broadphase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.acidfrog.kronos.mathk.Mathk;
import net.acidfrog.kronos.mathk.Vector2k;
import net.acidfrog.kronos.physics.collision.CollisionPair;
import net.acidfrog.kronos.physics.geometry.AABB;
import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Ray;
import net.acidfrog.kronos.physics.geometry.Transform;

/**
 * Represents the broadphase of the collision detection system.
 * 
 * <p>
 * The responsibility of the broadphase is to determine which objects are
 * potentially colliding. This is typically done by putting all objects into
 * either a bounding volume hierarchy (BVH) or a spatial partitioning structure.
 * The most common bounding volume hierarchy is a {@link DAABB Dynamic AABB Tree}.
 * This is the default implementation of the broadphase.
 * 
 * <p>
 * Our implementation of the broadphase can:
 * <ul>
 * <li>{@link #detect(boolean) Detect} which tracked objects are potentially colliding</li>
 * <li>{@link #detect(AABB) Detect} which objects are potentially intersecting an {@link AABB}</li>
 * <li>{@link #detect(BroadphaseMember, BroadphaseMember) Detect} if two specified objects are potentially colliding</li>
 * <li>{@link #detect(Collider, Transform, Collider, Transform) Detect} if two specified {@link Collider colliders} are potentially intersecting</li>
 * <li>{@link #raycast(Ray, float) Detect} which objects are potentially intersecting a {@link Ray}</li>
 * <li>{@link #raycast(Ray, float, AABB) Detect} if an {@link AABB} intersects a {@link Ray}</li>
 * </ul>
 * 
 * @author Ethan Temprovich
 */
public abstract sealed class BroadphaseDetector<T extends BroadphaseMember> permits DynamicAABBTree<T>, Quadtree<T> {

    /** A multiplier used when determining if we should update an AABB regardless if it fits within the existing AABB */
	protected static final double AABB_REDUCTION_RATIO = 2.0;

    /** The default initial capacity of members */
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 7;

    /** The {@link BroadphasePolicy policy} for this broadphase. */
    protected final BroadphasePolicy policy;

    /**
     * Determines if a seperate collection of members that are determined to have
     * moved a significant distance should be created.
     */
    protected boolean updateTracking;

    /**
     * Super constructor.
     * 
     * @param policy The {@link BroadphasePolicy policy} for this broadphase.
     */
    public BroadphaseDetector(BroadphasePolicy policy) {
        this.policy = (policy == null) ? new StaticAABBPolicy() : policy;
        this.updateTracking = false;
    }
    
    /**
     * Adds a new {@link BroadphaseMember member} to the broadphase.
     * 
     * @param t The {@link BroadphaseMember} to add.
     */
    public abstract void add(T t);

    /**
     * Removes a {@link BroadphaseMember member} from the broadphase.
     * 
     * @param t The {@link BroadphaseMember} to remove.
     */
    public abstract boolean remove(T t);

    /**
     * Updates the entire broadphase.
     */
    public abstract void update();

    /**
     * Updates the broadphase for a specific {@link BroadphaseMember member}.
     * 
     * @param t The {@link BroadphaseMember} to update.
     */
    public abstract void update(T t);

    /**
     * Determines if the broadphase contains a specific {@link BroadphaseMember member}.
     * 
     * @param t The {@link BroadphaseMember} to check for.
     * @return {@value true} if the broadphase contains the member, {@value false} otherwise.
     */
    public abstract boolean contains(T t);

    /**
     * Clears the broadphase.
     */
    public abstract void clear();

    /**
     * @return The count of tracked {@link BroadphaseMember members}.
     */
    public abstract int size();

    /**
     * Returns the broadphase representation of the {@link BroadphaseMember member}. Returns
     * the associated {@link AABB}.
     * 
     * @param t The {@link BroadphaseMember} to query.
     * @return The members {@link AABB}.
     */
    public abstract AABB get(T t);

    /**
     * 
     */
    public abstract void recompute();

    /**
	 * Performs collision detection on all members that have been added to this 
	 * {@link BroadphaseDetector} and returns the list of potential {@link CollisionPair collisions}.
	 * The pairs returned from this method will depend on the parameter {@value all}. if true, query
	 * all members, if false, only query members that have been updated.
     * 
	 * @param all if true, query all members, if false, only query members that have been updated.
     * @return The list of potential {@link CollisionPair collisions}.
	 */
    public List<CollisionPair<T>> detect(boolean all) {
        List<CollisionPair<T>> pairs = new ArrayList<CollisionPair<T>>();
        Iterator<CollisionPair<T>> iterator = detectIterator(all);
        
        while (iterator.hasNext()) {
            CollisionPair<T> pair = iterator.next();
            pairs.add(pair);
        }
        return pairs;
    }

    /**
     * Determines if the given {@link BroadphaseMember members} are considered to be colliding during this
     * broadphase.
     * 
     * @param t1 The first {@link BroadphaseMember} to check.
     * @param t2 The second {@link BroadphaseMember} to check.
     * @return {@value true} if the members are colliding, {@value false} otherwise.
     */
    public boolean detect(T t1, T t2) {
        AABB a = get(t1);
        AABB b = get(t2);

        return a.intersects(b);
    }

    /**
     * Determines if the given {@link Collider colliders} are considered to be colliding during this
     * broadphase.
     * 
     * @param c1 The first {@link Collider} to check.
     * @param t1 The {@link Transform} of the first {@link Collider}.
     * @param c2 The second {@link Collider} to check.
     * @param t2 The {@link Transform} of the second {@link Collider}.
     * @return {@value true} if the colliders are colliding, {@value false} otherwise.
     */
    public boolean detect(Collider c1, Transform tx1, Collider c2, Transform tx2) {
        AABB a = c1.computeAABB(tx1);
        AABB b = c2.computeAABB(tx2);

        return a.intersects(b); 
    }

    /**
     * Determines which {@link BroadphaseMember members} of this broadphase intersect with the given
     * {@Ray ray}.
     * 
     * @param ray The {@link Ray} to query.
     * @param maxDistance The maximum distance to query the ray. (0 for infinite)
     * @return The list of {@link BroadphaseMember members} that intersect with the {@link Ray}.
     */
    public List<T> raycast(Ray ray, float maxDistance) {
        List<T> members = new ArrayList<T>();
        Iterator<T> iterator = raycastIterator(ray, maxDistance);

        while (iterator.hasNext()) {
            T member = iterator.next();
            members.add(member);
        }
        return members;
    }
    
    /**
     * Determines if the given {@link Ray ray} intersects with the given {@link AABB aabb}.
     * 
     * @param ray The {@link Ray} to query.
     * @param maxDistance The maximum distance to query the ray. (0 for infinite)
     * @param aabb The {@link AABB} to query.
     * @return {@value true} if the ray intersects with the aabb, {@value false} otherwise.
     */
    public static boolean raycast(Ray ray, float maxDistance, AABB aabb) {
        Vector2k start = ray.getStart();
        float tx1 = (aabb.getMin().x - start.x) * ray.getInverseDirectionVector().x;
        float tx2 = (aabb.getMax().x - start.x) * ray.getInverseDirectionVector().x;

        float tmin = Mathk.min(tx1, tx2);
        float tmax = Mathk.max(tx1, tx2);

        float ty1 = (aabb.getMin().y - start.y) * ray.getInverseDirectionVector().y;
        float ty2 = (aabb.getMax().y - start.y) * ray.getInverseDirectionVector().y;

        tmin = Mathk.max(tmin, Mathk.min(ty1, ty2));
        tmax = Mathk.min(tmax, Mathk.max(ty1, ty2));

		if (tmax < 0) return false;
		if (maxDistance > 0 && tmin > maxDistance) return false;

		return tmax >= tmin;
    }

    public abstract Iterator<CollisionPair<T>> detectIterator(boolean all);

    public abstract Iterator<T> raycastIterator(Ray ray, float maxDistance);

    /**
     * @return the {@link BroadphasePolicy} used by this {@link BroadphaseDetector}.
     */
    public BroadphasePolicy getPolicy() {
        return policy;
    }

    public boolean isTrackingUpdates() {
        return updateTracking;
    }

    public void shouldTrackUpdates(boolean updateTracking) {
        this.updateTracking = updateTracking;
    }
    
}

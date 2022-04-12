package net.acidfrog.kronos.physics.collision.broadphase;

import java.util.ArrayList;
import java.util.List;

import net.acidfrog.kronos.physics.collision.CollisionPair;
import net.acidfrog.kronos.physics.geometry.AABB;
import net.acidfrog.kronos.physics.geometry.Ray;

public final class BruteForceBroadphase<T extends BroadphaseMember> extends BroadphaseDetector<T> {

    private List<T> members;

    public BruteForceBroadphase() {
        super(null);
        this.members = new ArrayList<T>();
    }

    @Override
    public void add(T t) {
        members.add(t);
    }

    @Override
    public boolean remove(T t) {
        return members.remove(t);
    }

    @Override
    public void update() {
        // do nothing
    }

    @Override
    public void update(T t) {
        // do nothing
    }

    @Override
    public boolean contains(T t) {
        return members.contains(t);
    }

    @Override
    public void clear() {
        members.clear();
    }

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public AABB get(T t) {
        return t.getBounds();
    }

    @Override
    public void recompute() {
        // do nothing
    }

    @Override
    public List<CollisionPair<T>> detect() {
        List<CollisionPair<T>> pairs = new ArrayList<CollisionPair<T>>();
        for (int i = 0; i < members.size(); i++) {
            var a = members.get(i);

            for (int j = i + 1; j < members.size(); j++) {
                var b = members.get(j);
                
                if (a.getBounds().intersects(b.getBounds())) pairs.add(new BroadphasePair<T>(a, b));
            }
        }

        return pairs;
    }

    @Override
    public List<T> raycast(Ray ray, float maxDistance) {
        List<T> bodies = new ArrayList<T>();

        for (var b : members) if (BroadphaseDetector.raycast(ray, maxDistance, b.getBounds())) bodies.add(b);
        
        return bodies;
    }

}

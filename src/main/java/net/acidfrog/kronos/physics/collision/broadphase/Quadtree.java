package net.acidfrog.kronos.physics.collision.broadphase;

import java.util.Iterator;
import java.util.List;

import net.acidfrog.kronos.physics.collision.CollisionPair;
import net.acidfrog.kronos.physics.geometry.AABB;
import net.acidfrog.kronos.physics.geometry.Ray;

public final class Quadtree<T extends BroadphaseMember> extends BroadphaseDetector<T> {

    // TODO: implement

    public Quadtree(BroadphasePolicy policy) {
        super(policy);
    }

    @Override
    public void add(T t) {
    }

    @Override
    public boolean remove(T t) {
        return false;
    }

    @Override
    public void update() {
    }

    @Override
    public void update(T t) {
    }

    @Override
    public boolean contains(T t) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public AABB get(T t) {
        return null;
    }

    @Override
    public void recompute() {
    }

    @Override
    public List<CollisionPair<T>> detect(boolean all) {
        return null;
    }

    @Override
    public List<T> raycast(Ray ray, float maxDistance) {
        return null;
    }

    @Override
    public Iterator<CollisionPair<T>> detectIterator(boolean all) {
        return null;
    }

    @Override
    public Iterator<T> raycastIterator(Ray ray, float maxDistance) {
        return null;
    }
    
}

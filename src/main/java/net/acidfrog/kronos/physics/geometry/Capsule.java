package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.mathk.Intervalf;
import net.acidfrog.kronos.mathk.Vector2k;

public final class Capsule extends AbstractShape implements Collider {

    //TODO: implement

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public Intervalf project(Vector2k axis, Transform transform) {
        return null;
    }

    @Override
    public boolean contains(Vector2k point, Transform transform) {
        return false;
    }

    @Override
    public AABB computeAABB(Transform transform) {
        return null;
    }

    @Override
    public Mass computeMass(float density) {
        return null;
    }

    @Override
    public Feature getFarthestFeature(Vector2k direction, Transform transform) {
        return null;
    }

    @Override
    public Vector2k getFarthestPoint(Vector2k direction, Transform transform) {
        return null;
    }

    @Override
    public Vector2k[] getAxes(Vector2k[] foci, Transform transform) {
        return null;
    }

    @Override
    public Vector2k[] getFoci(Transform transform) {
        return null;
    }

    @Override
    public float getArea() {
        return 0;
    }

    @Override
    public float getPerimeter() {
        return 0;
    }
    
}

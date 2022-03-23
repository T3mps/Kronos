package net.acidfrog.kronos.physics.geometry;

import net.acidfrog.kronos.mathk.Intervalf;
import net.acidfrog.kronos.mathk.Vector2k;

public final class Segment extends AbstractShape implements Collider, Wound {

    // TODO: implement
    
	private final Vector2k[] vertices = new Vector2k[2];
	
	private final Vector2k[] normals = new Vector2k[2];
	
	private float length;

    public Segment(Vector2k v1, Vector2k v2) {
        super(v1.add(v2).div(2), v1.distance(v2) * 0.5f);
        this.vertices[0] = v1;
        this.vertices[1] = v2;
        this.normals[0] = v1.sub(v2).normalized();
        this.normals[1] = v1.sub(v2).right().normalized();
        this.length = v1.distance(v2);
        validate();
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
    public float getArea() {
        return 0;
    }

    @Override
    public float getPerimeter() {
        return 0;
    }

    @Override
    public boolean validate() {
        return !vertices[0].equals(vertices[1]);
    }

    @Override
    public int[] computeWinding(Vector2k[] vertices) {
        return null;
    }

    @Override
    public Vector2k[] getVertices() {
        return null;
    }

    @Override
    public Vector2k[] getNormals() {
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

   
}

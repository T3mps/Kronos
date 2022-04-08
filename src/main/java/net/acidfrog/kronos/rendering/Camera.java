package net.acidfrog.kronos.rendering;

import net.acidfrog.kronos.math.Matrix4f;
import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector3f;

public class Camera {

    public Vector2f position;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    private Viewport viewport;

    private float zDepth = 20;

    public Camera(Vector2f position) {
        this(Viewport.DEFAULT, position);
    }

    public Camera(Viewport viewport) {
        this(viewport, new Vector2f(0f));
    }

    public Camera(Viewport viewport, Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.viewport = viewport;
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(viewport.getLeft(), viewport.getRight(), viewport.getBottom(), viewport.getTop(), viewport.getZNear(), viewport.getZFar());
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        viewMatrix.identity();
        viewMatrix = viewMatrix.lookAt(new Vector3f(position.x, position.y, zDepth),
                                       new Vector3f(position.x, position.y, -1f),
                                       new Vector3f(0, 1, 0));
        return viewMatrix;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Viewport getViewport() {
        return viewport;
    }

}

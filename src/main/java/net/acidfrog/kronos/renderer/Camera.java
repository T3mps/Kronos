package net.acidfrog.kronos.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private Matrix4f projection, view;
    public Vector2f position;

    private final float width = 32f * 40f;
    private final float height = 32f * 21f;
    private final float near = 0f;
    private final float far = 100f;
    private float zdist = 20f;

    public Camera(Vector2f position) {
        this.projection = new Matrix4f();
        this.view = new Matrix4f();
        this.position = position;
        defineProjectionMatrix();
    }

    public void defineProjectionMatrix() {
        projection.identity();
        projection.ortho(0f, width, 0, height, near, far);
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.view.identity();
        view.lookAt(new Vector3f(position.x, position.y, zdist),
                                        cameraFront.add(position.x, position.y, 0.0f),
                                        cameraUp);

        return this.view;
    }

}

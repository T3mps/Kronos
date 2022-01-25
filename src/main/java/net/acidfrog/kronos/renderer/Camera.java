package net.acidfrog.kronos.renderer;

public class Camera {

    private Matrix4f projection, view;
    private Vector2f position;

    private final float width = 32f * 40f;
    private final float height = 32f * 21f;
    private final float near = 0f;
    private final float far = 100f;
    private float zdist = 20f;

    public Camera(Vector2 position) {
        this.projection = new Matrix4f();
        this.view = new Matrix4f();
        this.position = position;
        defineProjectionMatrix();
    }

    public void defineProjectionMatrix() {
        projection.identity();
        projection.ortho(0f, width, 0, height, near, far);
    }

    public Matrix4 getProjectionMatrix() {
        return projection;
    }

    public Matrix4 getViewMatrix() {
        view.identity();
        view.lookAt(position.toVector3(zdist), Vector3.BACKWARD.add(new Vector3(position.x, position.y, 0f)), Vector3.UP);
        return view;
    }

}

package net.acidfrog.kronos.rendering;

public class Viewport {

    public static final int DEFAULT_UNIT_SIZE = 32;

    public static final Viewport DEFAULT = new Viewport(0, DEFAULT_UNIT_SIZE * (40f), 0, DEFAULT_UNIT_SIZE * 21f, 0, 100);

    private final float left;
    private final float bottom;
    private final float right;
    private final float top;

    private float[] clipping = new float[2];

    public Viewport(float left, float right, float bottom, float top, float zNear, float zFar) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.clipping[0] = zNear;
        this.clipping[1] = zFar;
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public float getTop() {
        return top;
    }

    public float[] getClipping() {
        return clipping;
    }

    public float getZNear() {
        return clipping[0];
    }

    public float getZFar() {
        return clipping[1];
    }

}

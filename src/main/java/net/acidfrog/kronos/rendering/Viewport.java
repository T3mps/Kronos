package net.acidfrog.kronos.rendering;

public class Viewport {

    public static final int DEFAULT_UNIT_SIZE = 32;

    public static final Viewport DEFAULT = new Viewport(0, 0, DEFAULT_UNIT_SIZE * 40f, DEFAULT_UNIT_SIZE * 21f, 0, 100);

    private float x;
    private float y;
    private float width;
    private float height;

    private float[] clipping = new float[2];

    public Viewport(float x, float y, float width, float height, float zNear, float zFar) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.clipping[0] = zNear;
        this.clipping[1] = zFar;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float[] getClipping() {
        return clipping;
    }
    
}

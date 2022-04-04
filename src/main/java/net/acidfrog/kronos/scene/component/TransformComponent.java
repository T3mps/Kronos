package net.acidfrog.kronos.scene.component;

import net.acidfrog.kronos.math.Vector2f;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.geometry.Transform;
import net.acidfrog.kronos.scene.ecs.component.AbstractComponent;

public final class TransformComponent extends AbstractComponent {

    private Transform transform;
    private Vector2f scale;

    public TransformComponent() {
        this(new Vector2f(0f), 0f, 0f);
    }

    public TransformComponent(float x, float y) {
        this(new Vector2f(x, y), 0f, 0f);
    }

    public TransformComponent(Vector2f position) {
        this(position, 0f, 0f);
    }

    public TransformComponent(Vector2f position, float rotation) {
        this(position, rotation, 1f);
    }

    public TransformComponent(Vector2f position, float rotation, float scale) {
        this.transform = new Transform(new Vector2k(position), rotation);
        this.scale = new Vector2f(scale);
    }

    public TransformComponent(Vector2f position, float rotation, Vector2f scale) {
        this.transform = new Transform(new Vector2k(position), rotation);
        this.scale = scale;
    }

    public TransformComponent(Transform transform) {
        this.transform = transform;
        this.scale = new Vector2f(1f);
    }

    public TransformComponent(Transform transform, Vector2f scale) {
        this.transform = transform;
        this.scale = scale;
    }
    
    public void rotate(float radians) {
        transform.rotate(radians);
    }

    public void translate(Vector2f translation) {
        transform.translate(new Vector2k(translation));
    }

    public void translate(float x, float y) {
        transform.translate(x, y);
    }

    public Transform getTransform() {
        return transform;
    }

    public Vector2f getPosition() {
        return new Vector2f(transform.getPosition());
    }

    public void setPosition(Vector2f position) {
        transform.setPosition(new Vector2k(position));
    }

    public float getRotation() {
        return transform.getRadians();
    }

    public void setRotation(float radians) {
        transform.setRotation(radians);
    }

    public Vector2f getScale() {
        return scale;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

}

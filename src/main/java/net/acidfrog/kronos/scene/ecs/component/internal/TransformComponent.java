package net.acidfrog.kronos.scene.ecs.component.internal;

import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.geometry.Transform;
import net.acidfrog.kronos.scene.ecs.component.AbstractComponent;

public final class TransformComponent extends AbstractComponent {

    private Transform transform;
    private Vector2k scale;

    public TransformComponent() {
        this(new Vector2k(0f), 0f, 0f);
    }

    public TransformComponent(Vector2k position) {
        this(position, 0f, 0f);
    }

    public TransformComponent(Vector2k position, float rotation) {
        this(position, rotation, 1f);
    }

    public TransformComponent(Vector2k position, float rotation, float scale) {
        this.transform = new Transform(position, rotation);
        this.scale = new Vector2k(scale);
    }

    public TransformComponent(Vector2k position, float rotation, Vector2k scale) {
        this.transform = new Transform(position, rotation);
        this.scale = scale;
    }

    public TransformComponent(Transform transform) {
        this.transform = transform;
        this.scale = new Vector2k(1f);
    }

    public TransformComponent(Transform transform, Vector2k scale) {
        this.transform = transform;
        this.scale = scale;
    }
    
    public void rotate(float radians) {
        transform.rotate(radians);
    }

    public void translate(Vector2k translation) {
        transform.translate(translation);
    }

    public void translate(float x, float y) {
        transform.translate(x, y);
    }

    public Transform getTransform() {
        return transform;
    }

    public Vector2k getPosition() {
        return transform.getPosition();
    }

    public void setPosition(Vector2k position) {
        transform.setPosition(position);
    }

    public float getRotation() {
        return transform.getRadians();
    }

    public void setRotation(float radians) {
        transform.setRotation(radians);
    }

    public Vector2k getScale() {
        return scale;
    }

    public void setScale(Vector2k scale) {
        this.scale = scale;
    }

}

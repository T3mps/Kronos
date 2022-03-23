package net.acidfrog.kronos.scene.ecs.component;

import net.acidfrog.kronos.mathk.Vector2k;
import net.acidfrog.kronos.physics.geometry.Rotation;
import net.acidfrog.kronos.physics.geometry.Transform;

public final class TransformComponent extends Transform {

    public static final Vector2k DEFAULT_SCALE = new Vector2k(1, 1);

    private Vector2k scale;

    public TransformComponent() {
        super();
        this.scale = DEFAULT_SCALE;
    }

    public TransformComponent(Vector2k position) {
        super(position);
        this.scale = DEFAULT_SCALE;
    }

    public TransformComponent(float x, float y, float rotation) {
        super(x, y, rotation);
        this.scale = DEFAULT_SCALE;
    }

    public TransformComponent(float x, float y, float rotation, Vector2k scale) {
        super(x, y, rotation);
        this.scale = scale;
    }

    public TransformComponent(Vector2k position, float rotation) {
        super(position, rotation);
        this.scale = DEFAULT_SCALE;
    }

    public TransformComponent(Vector2k position, float rotation, Vector2k scale) {
        super(position, rotation);
        this.scale = scale;
    }

    public TransformComponent(Vector2k position, Rotation rotation) {
        super(position, rotation);
        this.scale = DEFAULT_SCALE;
    }

    public TransformComponent(Vector2k position, Rotation rotation, Vector2k scale) {
        super(position, rotation);
        this.scale = scale;
    }

    public TransformComponent(Transform transform) {
        super(transform);
        this.scale = DEFAULT_SCALE;
    }

    public TransformComponent(Transform transform, Vector2k scale) {
        super(transform);
        this.scale = scale;
    }

    public TransformComponent(TransformComponent transform) {
        super(transform.position, transform.rotation);
        this.scale = DEFAULT_SCALE;
    }

    @Override
    public void set(Transform transform) {
        this.position.set(transform.getPosition());
        this.rotation.set(transform.getRotation());
    }

    public void set(Transform transform, Vector2k scale) {
        this.position.set(transform.getPosition());
        this.rotation.set(transform.getRotation());
        this.scale.set(scale);
    }

    public void set(TransformComponent transform) {
        this.position.set(transform.position);
        this.rotation.set(transform.rotation);
        this.scale.set(transform.scale);
    }

    @Override
    public Transform identity() {
        position.set(0f, 0f);
        rotation.identity();
        scale.set(DEFAULT_SCALE);
        return this;
    }

    public void scale(Vector2k scale) {
        this.scale.set(scale);
    }

    public void scaleBy(float x, float y) {
        scaleBy(new Vector2k(x, y));
    }

    public void scaleBy(Vector2k scale) {
        scale.muli(scale);
    }

    public void scaleBy(float scale) {
        this.scale.muli(scale);
    }

    public Vector2k getScale() {
        return scale;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Transform [position=");
        builder.append(position);
        builder.append(", rotation=");
        builder.append(rotation);
        builder.append(", scale=");
        builder.append(scale);
        builder.append("]");
        return builder.toString();
    }
    
}

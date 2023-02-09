package com.starworks.kronos.scene.component;

import com.starworks.kronos.maths.Mathk;
import com.starworks.kronos.maths.Vector2f;

public class TransformComponent {

	private static final Vector2f DEFAULT_POSITION = new Vector2f(0.0f, 0.0f);
	private static final float DEFAULT_ROTATION = 0.0f;
	private static final Vector2f DEFAULT_SCALE = new Vector2f(1.0f, 1.0f);

	private final Vector2f m_position;
	private float m_rotation;
	private final Vector2f m_scale;

	public TransformComponent() {
		this(new Vector2f(DEFAULT_POSITION), DEFAULT_ROTATION, new Vector2f(DEFAULT_SCALE));
	}

	public TransformComponent(Vector2f position) {
		this(position, DEFAULT_ROTATION, new Vector2f(DEFAULT_SCALE));
	}

	public TransformComponent(Vector2f position, float rotation, Vector2f scale) {
		this.m_position = new Vector2f(position);
		this.m_rotation = rotation;
		this.m_scale = new Vector2f(scale);
	}

	public TransformComponent(TransformComponent other) {
		this.m_position = new Vector2f(other.m_position);
		this.m_rotation = other.m_rotation;
		this.m_scale = new Vector2f(other.m_scale);
	}

	public void set(float x, float y, float radians) {
		m_position.set(x, y);
		m_rotation = radians;
	}

	public void set(double x, double y, double radians) {
		m_position.set(x, y);
		m_rotation = (float) radians;
	}

	public Vector2f getPosition() {
		return m_position;
	}

	public void setPosition(Vector2f position) {
		this.m_position.set(position);
	}

	public void setPosition(double x, double y) {
		this.m_position.set(x, y);
	}

	public void setPosition(float x, float y) {
		this.m_position.set(x, y);
	}

	public float getRadians() {
		return m_rotation;
	}

	public float getDegrees() {
		return (float) Mathk.toDegrees(m_rotation);
	}

	public void setRotation(float rotation) {
		this.m_rotation = rotation;
	}

	public Vector2f getScale() {
		return m_scale;
	}

	public void setScale(Vector2f scale) {
		if (scale.x() >= 0.0 && scale.y() >= 0.0) this.m_scale.set(scale);
	}

	public void setScale(float scale) {
		if (scale >= 0.0) this.m_scale.set(scale);
	}
}

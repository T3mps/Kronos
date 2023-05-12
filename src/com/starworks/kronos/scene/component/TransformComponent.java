package com.starworks.kronos.scene.component;

import java.util.EnumSet;

import com.starworks.kronos.maths.Mathk;
import com.starworks.kronos.maths.Matrix4f;
import com.starworks.kronos.maths.Quaternionf;
import com.starworks.kronos.maths.Vector2f;

public final class TransformComponent {

	private static final Vector2f DEFAULT_POSITION = new Vector2f(0f, 0f);
	private static final float DEFAULT_ROTATION = 0f;
	private static final Vector2f DEFAULT_SCALE = new Vector2f(1f, 1f);

	private final Vector2f m_position;
	private float m_rotation;
	private final Vector2f m_scale;

	private final Matrix4f m_transformationMatrix;
	private final Quaternionf m_rotationQuaternion;

	private enum DirtyFlag {
		MATRIX, QUATERNION;
	}

	private final EnumSet<DirtyFlag> m_dirtyFlags;

	public TransformComponent() {
		this(DEFAULT_POSITION, DEFAULT_ROTATION, DEFAULT_SCALE);
	}

	public TransformComponent(Vector2f position) {
		this(position, DEFAULT_ROTATION, DEFAULT_SCALE);
	}

	public TransformComponent(Vector2f position, float rotation, Vector2f scale) {
		this.m_position = new Vector2f(position);
		this.m_rotation = rotation;
		this.m_scale = new Vector2f(scale);
		this.m_transformationMatrix = new Matrix4f();
		this.m_rotationQuaternion = new Quaternionf();
		this.m_dirtyFlags = EnumSet.allOf(DirtyFlag.class);
	}

	public TransformComponent translate(Vector2f translation) {
		return translate(translation.x, translation.y);
	}

	public TransformComponent translate(float x, float y) {
		m_position.add(x, y);
		m_dirtyFlags.add(DirtyFlag.MATRIX);
		return this;
	}

	public TransformComponent rotate(float zDegrees) {
		m_rotation += zDegrees;
		m_rotation %= 360; // Ensure rotation is in valid range
		if (m_rotation < 0) {
			m_rotation += 360;
		}
		m_dirtyFlags.add(DirtyFlag.MATRIX);
		m_dirtyFlags.add(DirtyFlag.QUATERNION);
		return this;
	}

	public Vector2f getPosition() {
		return new Vector2f(m_position);
	}
	
	public float getX() {
		return m_position.x;
	}
	
	public float getY() {
		return m_position.y;
	}

	public void setPosition(Vector2f position) {
		setPosition(position.x, position.y);
	}
	
	public void setPosition(float x, float y) {
		this.m_position.set(x, y);
		m_dirtyFlags.add(DirtyFlag.MATRIX);
	}

	public float getRotation() {
		return m_rotation;
	}

	public void setRotation(float rotation) {
		this.m_rotation = rotation;
		m_rotation = m_rotation % 360;
		if (m_rotation < 0) {
			m_rotation += 360;
		}
		m_dirtyFlags.add(DirtyFlag.MATRIX);
		m_dirtyFlags.add(DirtyFlag.QUATERNION);
	}

	public Vector2f getScale() {
		return new Vector2f(m_scale);
	}
	
	public float getScaleX() {
		return m_scale.x;
	}
	
	public float getScaleY() {
		return m_scale.y;
	}
	
	public void setScale(float scale) {
		setScale(scale, scale);
	}

	public void setScale(Vector2f scale) {
		setScale(scale.x, scale.y);
	}

	public void setScale(float x, float y) {
		if (x <= 0 || y <= 0) {
			throw new IllegalArgumentException("Scale must be an unsigned float");
		}
		this.m_scale.set(x, y);
		m_dirtyFlags.add(DirtyFlag.MATRIX);
	}

	public Matrix4f getTransformationMatrix() {
		if (m_dirtyFlags.contains(DirtyFlag.MATRIX)) {
			computeTransformationMatrix();
		}
		return new Matrix4f(m_transformationMatrix);
	}

	public Quaternionf getRotationQuaternion() {
		if (m_dirtyFlags.contains(DirtyFlag.QUATERNION)) {
			computeRotationQuaternion();
		}
		return new Quaternionf(m_rotationQuaternion);
	}

	private void computeTransformationMatrix() {
		m_transformationMatrix.identity();
		m_transformationMatrix.translate(m_position.x, m_position.y, 0);
		m_transformationMatrix.rotateZ(Mathk.toRadians(m_rotation));
		m_transformationMatrix.scale(m_scale.x, m_scale.y, 1.0f);
		m_dirtyFlags.remove(DirtyFlag.MATRIX);
	}

	private void computeRotationQuaternion() {
		m_rotationQuaternion.identity();
		m_rotationQuaternion.rotateZ(Mathk.toRadians(m_rotation));
		m_dirtyFlags.remove(DirtyFlag.QUATERNION);
	}
}

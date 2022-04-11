package net.acidfrog.kronos.physics.world.body;

import java.awt.Graphics2D;
import java.awt.Color;

import net.acidfrog.kronos.core.lang.annotations.Debug;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.geometry.AABB;
import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Mass;
import net.acidfrog.kronos.physics.geometry.Transform;

public non-sealed class Rigidbody implements Body {

    public final Transform transform;
	public final Vector2k velocity;
	public final Vector2k force;
	public final Collider collider;
	public final Mass mass;
    public final Material material;
	public float angularVelocity;
	public float torque;
	private final Type type;
	
    public Rigidbody(Transform transform, Collider collider, Mass mass, Material material, Type type) {
        this.transform = transform;
		this.velocity = new Vector2k(0f);
		this.force = new Vector2k(0f);
        this.collider = collider;
        this.mass = mass;
        this.material = material;
		this.angularVelocity = 0f;
		this.torque = 0f;
        this.type = type;
	}

	public Rigidbody(Transform transform, Collider collider, Material material, Type type) {
        this.transform = transform;
		this.velocity = new Vector2k(0f);
		this.force = new Vector2k(0f);
        this.collider = collider;
        this.mass = (type == Type.STATIC) ? Mass.INFINITE : collider.computeMass(material.density());
        this.material = material;
		this.angularVelocity = 0f;
		this.torque = 0f;
        this.type = type;
    }

    @Override
	public void applyForce(Vector2k f) {
		force.addi(f);
	}

	@Override
	public void applyImpulse(Vector2k impulse) {
		velocity.addsi(impulse, mass.getInverseMass());
	}

	@Override
	public void applyImpulse(Vector2k impulse, Vector2k contactVector) {
		applyImpulse(impulse);
		angularVelocity += mass.getInverseInertia() * Vector2k.cross(contactVector, impulse);
	}

	@Override
	public AABB getBounds() {
		return collider.computeAABB(transform);
	}

	public Transform getTransform() {
		return transform;
	}

	public Vector2k getVelocity() {
		return velocity;
	}

	public Vector2k getForce() {
		return force;
	}

	public float getMass() {
		return mass.getMass();
	}

	public float getInertia() {
		return mass.getInertia();
	}

	public float getInverseMass() {
		return mass.getInverseMass();
	}

	public float getInverseInertia() {
		return mass.getInverseInertia();
	}

	public Material getMaterial() {
		return material;
	}

	public float getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public float getTorque() {
		return torque;
	}

	public void setTorque(float torque) {
		this.torque = torque;
	}

	public Collider getCollider() {
		return collider;
	}

	@Override
    public Type getType() {
        return type;
    }

}

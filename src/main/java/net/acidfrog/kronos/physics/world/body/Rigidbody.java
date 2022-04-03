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
import test.util.G2DRenderer;

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
	
	@Debug
	public boolean colliding;
	public Color color;

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
		
		this.color = new Color(Mathk.random(0, 255), Mathk.random(0, 255), Mathk.random(0, 255));
	}

	public Rigidbody(Transform transform, Collider collider, Material material, Type type) {
        this.transform = transform;
		this.velocity = new Vector2k(0f);
		this.force = new Vector2k(0f);
        this.collider = collider;
        this.mass = collider.computeMass(material.density());
        this.material = material;
		this.angularVelocity = 0f;
		this.torque = 0f;
        this.type = type;

		this.color = new Color(Mathk.random(0, 255), Mathk.random(0, 255), Mathk.random(0, 255));
    }

	@Debug
	private Vector2k direction = new Vector2k(0f, 0f);
    private float speed = 0;
	int counter = 0;

	@Debug
    public void update(float dt) {
        counter++;
        if (counter % 64 == 0) direction = getRandomDirection();
        
		applyImpulse(new Vector2k(speed * direction.x, speed * direction.y));
		if (speed == 0) speed = mass.getMass() * 0.05f;
    }

	@Debug
    private Vector2k getRandomDirection() {
        return new Vector2k(Mathk.random(-1f, 1f), Mathk.random(-1f, 1f));
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

	@Debug
	public void render(Graphics2D g2d) {
		G2DRenderer.render(g2d, collider, transform, colliding ? Color.RED : color);
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

	public Mass getMass() {
		return mass;
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

package net.acidfrog.kronos.physics.collision.narrowphase.manifold;

import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.Physics;
import net.acidfrog.kronos.physics.collision.narrowphase.Penetration;
import net.acidfrog.kronos.physics.world.body.Rigidbody;

public final class Manifold {

    private final Rigidbody bodyA;
    private final Rigidbody bodyB;
    private Penetration penetration;
    private final Vector2k[] contactPoints = new Vector2k[] { new Vector2k(0f), new Vector2k(0f) };
    int contactCount;

    private float averageRestitution;
	private float friction;

    public Manifold(Rigidbody bodyA, Rigidbody bodyB, Penetration penetration) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
        this.penetration = penetration;
        this.contactCount = 0;
    }

    public void initialize() {
		// Calculate average restitution
		averageRestitution =  Mathk.min(bodyA.getMaterial().restitution(), bodyB.getMaterial().restitution());

		// Calculate friction
		friction = (float) Mathk.sqrt(bodyA.material.friction() * bodyA.material.friction() + bodyB.material.friction() * bodyB.material.friction());

		for (int i = 0; i < contactCount; i++) {
			// Calculate radii from COM to contact
			Vector2k ra = contactPoints[i].sub(bodyA.getTransform().getPosition());
			Vector2k rb = contactPoints[i].sub(bodyB.getTransform().getPosition());

			Vector2k rv = bodyB.velocity.add(Vector2k.cross(bodyB.angularVelocity, rb, new Vector2k())).subi(bodyA.velocity).subi(Vector2k.cross(bodyA.angularVelocity, ra, new Vector2k()));

			if (rv.magnitudeSquared() < Physics.RESTING) averageRestitution = 0f;
		}
	}

	public void set(Penetration penetration, Vector2k... contactPoints) {
		setPenetration(penetration);
		setContactPoints(contactPoints);
    }

	public void setPenetration(Penetration penetration) {
		this.penetration = penetration;
	}

	public void setContactPoints(Vector2k... contactPoints) {
		if (contactPoints.length == 0) return;
		if (contactPoints.length > 2)  throw new IllegalArgumentException("Manifold can only handle up to 2 contact points");
		this.contactPoints[0].set(contactPoints[0]);
		if (contactPoints.length == 2) this.contactPoints[1].set(contactPoints[1]);
	}

    public void applyImpulse() {
        // Early out and positional correct if both objects have infinite mass
		if (Mathk.compare(bodyA.getInverseMass() + bodyB.getInverseMass(), 0)) {
			infiniteMassCorrection();
			return;
		}

		for (int i = 0; i < contactCount; i++) {
			Vector2k ra = contactPoints[i].sub(bodyA.getTransform().getPosition());
			Vector2k rb = contactPoints[i].sub(bodyB.getTransform().getPosition());

			Vector2k rv = bodyB.velocity.add(Vector2k.cross(bodyB.angularVelocity, rb, new Vector2k())).subi(bodyA.velocity).subi(Vector2k.cross(bodyA.angularVelocity, ra, new Vector2k()));

			float contactVel = Vector2k.dot(rv, penetration.getNormal());

			if (contactVel > 0f) return;

			float raCrossN = Vector2k.cross(ra, penetration.getNormal());
			float rbCrossN = Vector2k.cross(rb, penetration.getNormal());
			float invMassSum = bodyA.getInverseMass() + bodyB.getInverseMass() + (raCrossN * raCrossN) * bodyA.getInverseInertia() + (rbCrossN * rbCrossN) * bodyB.getInverseInertia();

			float j = -(1f + averageRestitution) * contactVel;
			j /= invMassSum;
			j /= contactCount;

			Vector2k impulse = penetration.getNormal().mul(j);
			bodyA.applyImpulse(impulse.negated(), ra);
			bodyB.applyImpulse(impulse, rb);

			rv = bodyB.velocity.add(Vector2k.cross(bodyB.angularVelocity, rb, new Vector2k())).subi(bodyA.velocity).subi(Vector2k.cross(bodyA.angularVelocity, ra, new Vector2k()));

			Vector2k t = new Vector2k(rv);
			t.addsi(penetration.getNormal(), -Vector2k.dot(rv, penetration.getNormal()));
			t.normalize();

			float jt = -Vector2k.dot(rv, t);
			jt /= invMassSum;
			jt /= contactCount;

			if (Mathk.compare(jt, 0f)) return;

			Vector2k tangentImpulse;
			if (Mathk.abs(jt) < j * friction) tangentImpulse = t.mul(jt);
			else tangentImpulse = t.mul(j).muli(-friction);

			bodyA.applyImpulse(tangentImpulse.negated(), ra);
			bodyB.applyImpulse(tangentImpulse, rb);
		}
    }
    
	public void positionalCorrection() {
		float correction = Mathk.max(penetration.getDepth() - Physics.PENETRATION_ALLOWANCE, 0f) / (bodyA.getInverseMass() + bodyB.getInverseMass()) * Physics.PENETRATION_CORRETION;

		bodyA.getTransform().position.addsi(penetration.getNormal(), -bodyA.getInverseMass() * correction);
		bodyB.getTransform().position.addsi(penetration.getNormal(),  bodyB.getInverseMass() * correction);
	}
	
    public void infiniteMassCorrection() {
		bodyA.velocity.set(0f, 0f);
		bodyB.velocity.set(0f, 0f);
	}

    public Rigidbody getBodyA() {
        return bodyA;
    }

    public Rigidbody getBodyB() {
        return bodyB;
    }

    public Penetration getPenetration() {
        return penetration;
    }

    public Vector2k[] getContactPoints() {
        return contactPoints;
    }

    public int contactCount() {
        return contactCount;
    } 
    
}

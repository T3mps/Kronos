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

	private float friction;

	private float Pn;
	private float Pt;
	private float Pnb;
	private float massNormal, massTangent;
	private float bias;

    public Manifold(Rigidbody bodyA, Rigidbody bodyB, Penetration penetration) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
        this.penetration = penetration;
        this.contactCount = 0;
    }

	public void update(Manifold newManifold) {
		Manifold oldManifold = this;

		for (int i = 0; i < newManifold.contactCount; i++) {
			int k = -1;

			for (int j = 0; j < oldManifold.contactCount; j++) {
				if (newManifold.contactPoints[i].equals(oldManifold.contactPoints[j])) {
					k = j;
					break;
				}
			}

			if (k > -1) {
				newManifold.Pn = oldManifold.Pn;
				newManifold.Pt = oldManifold.Pt;
				newManifold.Pnb = oldManifold.Pnb;
			} else {
				contactPoints[contactCount] = newManifold.contactPoints[i];
			}
		}

		for (int i = 0; i < newManifold.contactCount; i++) {
			contactPoints[i] = newManifold.contactPoints[i];
		}

		penetration = newManifold.penetration;

		contactCount = newManifold.contactCount;
	}

    public void initialize(float dt) {
		float invDt = 1f / dt;

		// Calculate friction
		friction = (float) Mathk.sqrt(bodyA.material.friction() * bodyA.material.friction() + bodyB.material.friction() * bodyB.material.friction());

		for (int i = 0; i < contactCount; i++) {
			// Calculate radii from COM to contact
			Vector2k r1 = contactPoints[i].sub(bodyA.getTransform().getPosition());
			Vector2k r2 = contactPoints[i].sub(bodyB.getTransform().getPosition());

			float rn1 = Vector2k.dot(r1, penetration.getNormal());
			float rn2 = Vector2k.dot(r2, penetration.getNormal());
			float kNormal = bodyA.getInverseMass() + bodyB.getInverseMass();
			kNormal += bodyA.getInverseInertia() * (Vector2k.dot(r1, r1) - rn1 * rn1) + bodyB.getInverseInertia() * (Vector2k.dot(r2, r2) - rn2 * rn2);
			massNormal = 1f / kNormal;

			Vector2k tangent = Vector2k.cross(penetration.getNormal(), 1f);
			float rt1 = Vector2k.dot(r1, tangent);
			float rt2 = Vector2k.dot(r2, tangent);
			float kTangent = bodyA.getInverseMass() + bodyB.getInverseMass();
			kTangent += bodyA.getInverseInertia() * (Vector2k.dot(r1, r1) - rt1 * rt1) + bodyB.getInverseInertia() * (Vector2k.dot(r2, r2) - rt2 * rt2);
			massTangent = 1f / kTangent;

			
			Vector2k P = penetration.getNormal().mul(Pn).add(tangent.mul(Pt));
			
			bodyA.velocity.subi(P.mul(bodyA.getInverseMass()));
			bodyA.angularVelocity -= bodyA.getInverseInertia() * Vector2k.cross(r1, P);
		}
		
		bias  = -Physics.PENETRATION_CORRETION * invDt * Mathk.min(0f, penetration.getDepth() + Physics.PENETRATION_ALLOWANCE);
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
		if (Mathk.compare(bodyA.getInverseMass() + bodyB.getInverseMass(), 0f)) {
			infiniteMassCorrection();
			return;
		}

		for (int i = 0; i < contactCount; i++) {
			Vector2k r1 = contactPoints[i].sub(bodyA.getTransform().getPosition());
			Vector2k r2 = contactPoints[i].sub(bodyB.getTransform().getPosition());

			Vector2k dv = bodyB.getVelocity().add(Vector2k.cross(bodyB.getAngularVelocity(), r2).sub(bodyA.getVelocity()).sub(Vector2k.cross(bodyA.getAngularVelocity(), r1)));
	
			float vn = Vector2k.dot(dv, penetration.getNormal());

			float dPn = massNormal * (-vn + bias);

			float Pn0 = Pn;
			Pn = Mathk.max(Pn0 + dPn, 0f);
			dPn = Pn - Pn0;

			Vector2k Pn = penetration.getNormal().mul(dPn);

			bodyA.velocity.subi(Pn.mul(bodyA.getInverseMass()));
			bodyA.angularVelocity -= bodyA.getInverseInertia() * Vector2k.cross(r1, Pn);

			bodyB.velocity.addi(Pn.mul(bodyB.getInverseMass()));
			bodyB.angularVelocity += bodyB.getInverseInertia() * Vector2k.cross(r2, Pn);

			dv = bodyB.getVelocity().add(Vector2k.cross(bodyB.getAngularVelocity(), r2).sub(bodyA.getVelocity()).sub(Vector2k.cross(bodyA.getAngularVelocity(), r1)));
		
			Vector2k tangent = Vector2k.cross(penetration.getNormal(), 1f);
			float vt = Vector2k.dot(dv, tangent);
			float dPt = massTangent * (-vt);

			float maxPt = friction * dPn;
			float oldTangentImpulse = Pt;
			Pt = Mathk.clamp(oldTangentImpulse + dPt, -maxPt, maxPt);
			dPt = Pt - oldTangentImpulse;

			Vector2k Pt = tangent.mul(dPt);

			bodyA.velocity.subi(Pt.mul(bodyA.getInverseMass()));
			bodyA.angularVelocity -= bodyA.getInverseInertia() * Vector2k.cross(r1, Pt);

			bodyB.velocity.addi(Pt.mul(bodyB.getInverseMass()));
			bodyB.angularVelocity += bodyB.getInverseInertia() * Vector2k.cross(r2, Pt);
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof Manifold)) return false;
		Manifold other = (Manifold) obj;
		return this.bodyA.equals(other.bodyA) && this.bodyB.equals(other.bodyB);
	}
    
}

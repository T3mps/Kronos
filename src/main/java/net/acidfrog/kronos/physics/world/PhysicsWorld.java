package net.acidfrog.kronos.physics.world;

import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.Color;

import net.acidfrog.kronos.core.lang.annotations.Debug;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.Physics;
import net.acidfrog.kronos.physics.collision.CollisionPair;
import net.acidfrog.kronos.physics.collision.broadphase.BroadphaseDetector;
import net.acidfrog.kronos.physics.collision.broadphase.DynamicAABBTree;
import net.acidfrog.kronos.physics.collision.narrowphase.GJK;
import net.acidfrog.kronos.physics.collision.narrowphase.manifold.ClippingManifoldSolver;
import net.acidfrog.kronos.physics.collision.narrowphase.manifold.Manifold;
import net.acidfrog.kronos.physics.collision.narrowphase.manifold.ManifoldSolver;
import net.acidfrog.kronos.physics.geometry.Collider;
import net.acidfrog.kronos.physics.geometry.Ray;
import net.acidfrog.kronos.physics.collision.narrowphase.NarrowphaseDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.Penetration;
import net.acidfrog.kronos.physics.collision.narrowphase.RaycastDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.RaycastResult;
import net.acidfrog.kronos.physics.collision.narrowphase.SAT;
import net.acidfrog.kronos.physics.world.body.Rigidbody;
import test.util.G2DRenderer;

public final class PhysicsWorld implements World<Rigidbody> {

    private final BroadphaseDetector<Rigidbody> broadphaseDetector;
    private final NarrowphaseDetector narrowphaseDetector;
    private final RaycastDetector raycastDetector;
    private final ManifoldSolver manifoldSolver;

    private final Vector2k gravity = Physics.GRAVITY;

    private List<Rigidbody> bodies;
    private List<Manifold> manifolds;

    public PhysicsWorld() {
        this.broadphaseDetector = new DynamicAABBTree<Rigidbody>();
        this.narrowphaseDetector = new SAT();
        this.raycastDetector = new GJK();
        this.manifoldSolver = new ClippingManifoldSolver();
        this.bodies = new ArrayList<Rigidbody>();
        this.manifolds = new ArrayList<Manifold>();
    }

    @Override
    public void update(float dt) {
        manifolds.clear();

        broadphaseDetector.update();
        
        detect();

        // integrate forces
        for (var b : bodies) integrateForces(b, dt);

        // initialize manifolds
        for (var manifold : manifolds) manifold.initialize();

        // resolve manifolds
        for (var manifold : manifolds) manifold.applyImpulse();

        // integrate velocities
        for (var b : bodies) integrateVelocity(b, dt);

        // Correct positions
		for (int i = 0; i < manifolds.size(); i++) manifolds.get(i).positionalCorrection();

        // clear all forces
        for (var b : bodies) {
            b.force.set(0, 0);
			b.torque = 0;
        }
    }

    @Debug
    public void render(Graphics2D g2d) {
        for (var body : bodies) {
			G2DRenderer.render(g2d, body.getCollider(), body.getTransform(), Color.BLUE);
		}

        g2d.setColor(Color.WHITE);
        for (var manifold : manifolds) {
            for (int i = 0; i < manifold.contactCount(); i++) {
                Vector2k p = manifold.getContactPoints()[i];
                g2d.drawOval((int) p.x - 5, (int) -(p.y + 5), 10, 10);
            }
        }

        ((DynamicAABBTree<Rigidbody>) broadphaseDetector).render(g2d);
    }

    @Override
    public void detect() {
        List<CollisionPair<Rigidbody>> broadphasePairs = broadphaseDetector.detect(false);
        Penetration penetration = new Penetration();
        
        for (var pair : broadphasePairs) {
            penetration.clear();

            Rigidbody b1 = pair.getA();
            Rigidbody b2 = pair.getB();
            Collider  c1 = b1.getCollider();
            Collider  c2 = b2.getCollider();

            if (narrowphaseDetector.detect(c1, b1.getTransform(), c2, b2.getTransform(), penetration)) {
                Manifold manifold = new Manifold(b1, b2, penetration);
                manifoldSolver.solve(manifold);
                if (manifold.contactCount() > 0) manifolds.add(manifold);
            }
        }
    }

    private void integrateForces(Rigidbody b, float dt) {
		if (b.getInverseMass() == 0.0f) return;

		float dts = dt * 0.5f;

		b.velocity.addsi(b.force, b.getInverseInertia() * dts);
		b.velocity.addsi(gravity, dts);
		b.angularVelocity += b.torque * b.getInverseInertia() * dts;
	}

	private void integrateVelocity(Rigidbody b, float dt) {
		if (b.getInverseMass() == 0.0f) return;

		b.transform.getPosition().addsi(b.velocity, dt);
		b.transform.rotate(b.angularVelocity * dt);

		integrateForces(b, dt);
	}

    public Rigidbody raycast(Vector2k start, Vector2k direction, float maxDistance, RaycastResult result) {
        return raycast(new Ray(start, direction), maxDistance, result);
    }

    public Rigidbody raycast(Ray ray, float maxDistance, RaycastResult result) {
        List<Rigidbody> bodies = broadphaseDetector.raycast(ray, maxDistance);
        
        for (var b : bodies) {
            if (raycastDetector.raycast(ray, maxDistance, b.getCollider(), b.getTransform(), result)) {
                return b;
            }

            result.clear();
        }

        return null;
    }

    @Override
    public void add(Rigidbody body) {
        bodies.add(body);
        broadphaseDetector.add(body);
    }

    @Override
    public Rigidbody get(int index) {
        return bodies.get(index);
    }

    @Override
    public boolean remove(Rigidbody body) {
        broadphaseDetector.remove(body);
        return bodies.remove(body);
    }

    @Override
    public Rigidbody remove(int index) {
        broadphaseDetector.remove(bodies.get(index));
        return bodies.remove(index);
    }

    @Override
    public void removeAll() {
        broadphaseDetector.clear();
        bodies.clear();
    }

    @Override
    public int size() {
        return bodies.size();
    }

    @Override
    public List<Rigidbody> getBodies() {
        return bodies;
    }

    @Override
    public BroadphaseDetector<Rigidbody> getBroadphaseDetector() {
        return broadphaseDetector;
    }

    @Override
    public NarrowphaseDetector getNarrowphaseDetector() {
        return narrowphaseDetector;
    }

    @Override
    public RaycastDetector getRaycastDetector() {
        return raycastDetector;
    }
    
}

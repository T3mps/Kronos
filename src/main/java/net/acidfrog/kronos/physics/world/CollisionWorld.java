package net.acidfrog.kronos.physics.world;

import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;

import net.acidfrog.kronos.core.lang.annotations.Debug;
import net.acidfrog.kronos.physics.collision.CollisionPair;
import net.acidfrog.kronos.physics.collision.broadphase.BroadphaseDetector;
import net.acidfrog.kronos.physics.collision.broadphase.DynamicAABBTree;
import net.acidfrog.kronos.physics.collision.narrowphase.GJK;
import net.acidfrog.kronos.physics.collision.narrowphase.NarrowphaseDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.RaycastDetector;
import net.acidfrog.kronos.physics.collision.narrowphase.SAT;
import net.acidfrog.kronos.physics.world.body.Rigidbody;

public final class CollisionWorld<T extends Rigidbody> implements World<T> {

    protected BroadphaseDetector<T> broadphaseDetector;
    protected NarrowphaseDetector narrowphaseDetector;
    protected RaycastDetector raycastDetector;

    protected List<T> bodies;

    public CollisionWorld() {
        this(new DynamicAABBTree<T>(true, true), new SAT(), new GJK());
    }

    public CollisionWorld(BroadphaseDetector<T> broadphaseDetector, NarrowphaseDetector narrowphaseDetector, RaycastDetector raycastDetector) {
        this.broadphaseDetector = broadphaseDetector;
        this.narrowphaseDetector = narrowphaseDetector;
        this.raycastDetector = raycastDetector;
        this.bodies = new ArrayList<T>();
    }

    @Override
    public void update(float dt) {
        broadphaseDetector.update();

        detect();

        for (T body : bodies) body.update(dt);

        // Integrate forces
		for (int i = 0; i < bodies.size(); i++) integrateForces(bodies.get(i), dt);

		// Integrate velocities
		for (int i = 0; i < bodies.size(); i++) integrateVelocity(bodies.get(i), dt);
    }

    // Acceleration
	// F = mA
	// => A = F * 1/m
	
	// Explicit Euler
	// x += v * dt
	// v += (1/m * F) * dt
	
	// Semi-Implicit (Symplectic) Euler
	// v += (1/m * F) * dt
	// x += v * dt
	
	// see http://www.niksula.hut.fi/~hkankaan/Homepages/gravity.html
	public void integrateForces(Rigidbody b, float dt) {
		if (b.getMass().getInverseMass() == 0f) return;
		
		float h_dt = dt * 0.5f;
		
		b.velocity.addsi(b.force, b.getMass().getInverseMass() * h_dt);
		// b.angularVelocity += b.torque * b.getMass().getInverseInertia() * h_dt;
	}
	
	public void integrateVelocity(Rigidbody b, float dt) {
		if (b.getMass().getInverseMass() == 0f) return;
		
		b.getTransform().getPosition().addsi(b.velocity, dt);
		// b.rotation += b.angularVelocity * dt;
		// b.setRotation(b.rotation);
		// b.transform.rotate(b.angularVelocity * dt);

		integrateForces(b, dt);
	}

    @Debug
    public void render(Graphics2D g2d) {
        for (T body : bodies) body.render(g2d);
        ((DynamicAABBTree<T>) broadphaseDetector).render(g2d);
    }

    @Override
    public void detect() {
        List<CollisionPair<T>> collisionPairs = broadphaseDetector.detect(true);

        for (int i = 0; i < collisionPairs.size(); i++) {
            CollisionPair<T> pair = collisionPairs.get(i);
            T a = pair.getA();
            T b = pair.getB();

            boolean colliding = narrowphaseDetector.detect(a.getCollider(), a.getTransform(), b.getCollider(), b.getTransform());

            if (colliding) {
                a.colliding = true;
                b.colliding = true;
                continue;
            }

            a.colliding = false;
            b.colliding = false;
        }
    }

    @Override
    public void add(T body) {
        bodies.add(body);
        broadphaseDetector.add(body);
    }

    @Override
    public T get(int index) {
        return bodies.get(index);
    }

    @Override
    public boolean remove(T body) {
        return bodies.remove(body);
    }

    @Override
    public boolean remove(int index) {
        return remove(get(index));
    }

    @Override
    public void removeAll() {
        bodies.clear();
    }

    @Override
    public int size() {
        return bodies.size();
    }

    @Override
    public List<T> getBodies() {
        return bodies;
    }

    @Override
    public BroadphaseDetector<T> getBroadphaseDetector() {
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

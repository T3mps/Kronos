package net.acidfrog.kronos.physics.collision.narrowphase.manifold;

import net.acidfrog.kronos.core.lang.annotations.Out;
import net.acidfrog.kronos.math.Vector2k;
import net.acidfrog.kronos.physics.collision.narrowphase.Penetration;
import net.acidfrog.kronos.physics.geometry.Circle;
import net.acidfrog.kronos.physics.world.body.Rigidbody;

public final class ClippingManifoldSolver implements ManifoldSolver {

    private final CollisionCallback[][] dispatch = { { CollisionCircleCircle.instance,  CollisionCirclePolygon.instance  },
		                                             { CollisionPolygonCircle.instance, CollisionPolygonPolygon.instance } };

    @Override
    public void solve(@Out Manifold manifold) {
        int ia = manifold.getBodyA().getCollider().getType().ordinal();
		int ib = manifold.getBodyB().getCollider().getType().ordinal();

		this.dispatch[ia][ib].handleCollision(manifold, manifold.getBodyA(), manifold.getBodyB());
    }

    private static final class CollisionCircleCircle implements CollisionCallback {

        static final CollisionCircleCircle instance = new CollisionCircleCircle();

        @Override
        public void handleCollision(@Out Manifold manifold, Rigidbody bodyA, Rigidbody bodyB) {
            Circle circleA = (Circle) bodyA.getCollider();
            Circle circleB = (Circle) bodyB.getCollider();
            Vector2k positionA = bodyA.getTransform().getPosition();
            Vector2k positionB = bodyB.getTransform().getPosition();

            Vector2k normal = positionB.sub(positionA);
            float distance = normal.magnitude();
            float radii = circleA.getRadius() + circleB.getRadius();

            manifold.contactCount = 1;

            if (distance == 0f) {
                manifold.setPenetration(new Penetration(Vector2k.RIGHT, circleA.getRadius()));
                manifold.setContactPoints(positionA);
            } else {
                manifold.setPenetration(new Penetration(normal.div(distance), radii - distance));
                manifold.setContactPoints((normal.div(distance)).muli(circleA.getRadius()).addi(positionA));
            }
        }
    }

    private static final class CollisionCirclePolygon implements CollisionCallback {

        static final CollisionCirclePolygon instance = new CollisionCirclePolygon();

        @Override
        public void handleCollision(@Out Manifold manifold, Rigidbody bodyA, Rigidbody bodyB) {
        }

    }

    private static final class CollisionPolygonCircle implements CollisionCallback {

        static final CollisionPolygonCircle instance = new CollisionPolygonCircle();

        @Override
        public void handleCollision(@Out Manifold manifold, Rigidbody bodyA, Rigidbody bodyB) {
        }

    }

    private static final class CollisionPolygonPolygon implements CollisionCallback {

        static final CollisionPolygonPolygon instance = new CollisionPolygonPolygon();

        @Override
        public void handleCollision(@Out Manifold manifold, Rigidbody bodyA, Rigidbody bodyB) {
        }

    }
    
    private sealed interface CollisionCallback permits CollisionCircleCircle, CollisionCirclePolygon, CollisionPolygonCircle, CollisionPolygonPolygon {
        
        public void handleCollision(@Out Manifold manifold, Rigidbody bodyA, Rigidbody bodyB);

    }

}

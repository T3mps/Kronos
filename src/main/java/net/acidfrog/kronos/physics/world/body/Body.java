package net.acidfrog.kronos.physics.world.body;

import net.acidfrog.kronos.mathk.Vector2k;
import net.acidfrog.kronos.physics.collision.broadphase.BroadphaseMember;

public sealed interface Body extends BroadphaseMember permits Rigidbody {
    
    public enum Type { NULL, STATIC, DYNAMIC; }

    public abstract void applyForce(Vector2k f);

	public abstract void applyImpulse(Vector2k impulse);

	public abstract void applyImpulse(Vector2k impulse, Vector2k contactVector);

    public abstract Type getType();

}

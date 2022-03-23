package net.acidfrog.kronos.physics.collision.broadphase;

import net.acidfrog.kronos.mathk.Vector2k;

public class PartitionPoint<T extends BroadphaseMember> extends Vector2k {

    private final T member;

    public PartitionPoint(Vector2k vector, T member) {
        super(vector);
        this.member = member;
    }

    public PartitionPoint(float x, float y, T member) {
        super(x, y);
        this.member = member;
    }

    public PartitionPoint(PartitionPoint<T> point) {
        this.x = point.x;
        this.y = point.y;
        this.member = point.member;
    }

    public T getMember() {
        return member;
    }
    
}

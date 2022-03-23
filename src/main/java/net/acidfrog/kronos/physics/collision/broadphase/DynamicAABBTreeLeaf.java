package net.acidfrog.kronos.physics.collision.broadphase;

/**
 * Represents a leaf {@link DynamicAABBTreeNode node} in a {@link DynamicAABBTree Dynamic AABB Tree}.
 * This needs to be defined in a seperate class because a leaf node contains the
 * {@link BroadphaseMember} that it represents.
 * 
 * <p>
 * Extends {@link DynamicAABBTreeNode}.
 * 
 * @author Ethan Temprovich
 */
public final class DynamicAABBTreeLeaf<T extends BroadphaseMember> extends DynamicAABBTreeNode {

    /** The {@link BroadphaseMember} that this node represents. */
    final T member;

    /**
     * Construtor with the {@link BroadphaseMember} that this node represents.
     * 
     * @param t the {@link BroadphaseMember}.
     */
    public DynamicAABBTreeLeaf(T t) {
        this.member = t;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DAABBTreeNode [data=");
        builder.append(member);
        builder.append(", aabb=");
        builder.append(aabb);
        builder.append(", height=");
        builder.append(height);
        builder.append("]");
        return builder.toString();
    }
    
}

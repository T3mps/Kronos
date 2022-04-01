package net.acidfrog.kronos.physics.collision.broadphase;

import java.awt.Color;
import java.awt.Graphics2D;

import net.acidfrog.kronos.core.lang.annotations.Debug;
import net.acidfrog.kronos.physics.geometry.AABB;
import test.util.G2DRenderer;

/**
 * Represents a node in a {@link DynamicAABBTree Dynamic AABB Tree}.
 * 
 * @author Ethan Temprovich
 */
public sealed class DynamicAABBTreeNode permits DynamicAABBTreeLeaf<? extends BroadphaseMember> {

    /** The left child node. */
    DynamicAABBTreeNode left;

    /** The right child node. */
    DynamicAABBTreeNode right;

    /** The parent node. */
    DynamicAABBTreeNode parent;

    /** The height of this node in the {@link DynamicAABBTree tree}. */
    int height;

    /** The {@link AABB#union(AABB) union} of all {@link AABB AABBs} below this one in the {@link DynamicAABBTree tree}. */
    final AABB aabb;

    /**
     * Default constructor.
     */
    public DynamicAABBTreeNode() {
        this.aabb = new AABB();
    }

    /**
     * @return if this node is a leaf node.
     */
    public boolean isLeaf() {
        return left == null;
    }

    @Debug
    public void render(Graphics2D g2d) {
        G2DRenderer.render(g2d, aabb, Color.WHITE);

        if (left != null) left.render(g2d);
        if (right != null) right.render(g2d);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DAABBTreeNode [aabb=");
        builder.append(aabb);
        builder.append(", height=");
        builder.append(height);
        builder.append("]");
        return builder.toString();
    }
    
}

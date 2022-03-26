package net.acidfrog.kronos.core.lang.datastructure.tree;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;

/**
 * Node for a binary search tree.
 * 
 * @author Ethan Temprovich
 */
public final class BinarySearchTreeNode<E extends Comparable<E>> extends Tree.Node<E> {

    BinarySearchTreeNode<E> left;

    BinarySearchTreeNode<E> right;
    
    public BinarySearchTreeNode(E value) {
        this(value, null, null, null);
    }

    public BinarySearchTreeNode(E value, BinarySearchTreeNode<E> parent, BinarySearchTreeNode<E> left, BinarySearchTreeNode<E> right) {
        super(value, parent);
        this.left = left;
        this.right = right;
    }

    public boolean isLeftChild() {
		if (this.parent == null || (this.parent instanceof BinarySearchTreeNode<E>)) return false;
		return (((BinarySearchTreeNode<E>) this.parent).getLeft() == this);
	}

    public boolean isRightChild() {
        if (this.parent == null || (this.parent instanceof BinarySearchTreeNode<E>)) return false;
        return (((BinarySearchTreeNode<E>) this.parent).getRight() == this);
    }

    public BinarySearchTreeNode<E> getLeft() {
        return this.left;
    }

    public BinarySearchTreeNode<E> getRight() {
        return this.right;
    }

    @Override
	public int compareTo(Tree.Node<E> other) {
        if (!(other instanceof BinarySearchTreeNode<E>)) throw new KronosError(KronosErrorLibrary.INVALID_NODE_TYPE);
		return this.value.compareTo(other.getValue());
	}

}

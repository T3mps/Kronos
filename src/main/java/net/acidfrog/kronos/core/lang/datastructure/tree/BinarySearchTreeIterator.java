package net.acidfrog.kronos.core.lang.datastructure.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;

/*
 * Class for iterating through a binary search tree.
 * 
 * @author Ethan Temprovich
 */
public class BinarySearchTreeIterator <E extends Comparable<E>> implements Iterator<E> {

	/** The node stack for iterative traversal */
	final Deque<BinarySearchTreeNode<E>> stack;
	
	/** The root of the tree */
	final BinarySearchTreeNode<E> root;
	
	/** The value to start iteration from; can be null */
	final E from;
	
	/** The value to end iteration; can be null */
	final E to;

	/** The traversal direction */
	final boolean inOrder;
	
	/**
	 * Default constructor using in-order traversal.
	 * @param root the root node of the subtree to traverse
	 * @throws NullPointerException if node is null
	 */
	public BinarySearchTreeIterator(BinarySearchTreeNode<E> root) {
		this(root, null, null, true);
	}
	
	/**
	 * Full constructor.
	 * @param root the root node of the subtree to traverse
	 * @param inOrder true to iterate in-order, false to iterate reverse order
	 * @throws NullPointerException if node is null
	 */
	public BinarySearchTreeIterator(BinarySearchTreeNode<E> root, boolean inOrder) {
		this(root, null, null, inOrder);
	}

	/**
	 * Full constructor.
	 * @param root the root node of the subtree to traverse
	 * @param from the value to start iterating from (inclusive)
	 * @param to the value to stop iterating after (inclusive)
	 * @throws NullPointerException if node is null
	 * @since 3.2.3
	 */
	public BinarySearchTreeIterator(BinarySearchTreeNode<E> root, E from, E to) {
		this(root, from, to, true);
	}
	
	/**
	 * Full constructor.
	 * @param root the root node of the subtree to traverse
	 * @param from the value to start iterating from (inclusive)
	 * @param to the value to stop iterating after (inclusive)
	 * @param inOrder true to iterate in-order, false to iterate reverse order
	 * @throws NullPointerException if node is null
	 * @since 3.2.3
	 */
	private BinarySearchTreeIterator(BinarySearchTreeNode<E> root, E from, E to, boolean inOrder) {
		// set the direction
		this.inOrder = inOrder;
		// create the node stack and initialize it
		this.stack = new ArrayDeque<BinarySearchTreeNode<E>>();
		this.root = root;
		this.from = from;
		this.to = to;
		// check the direction to determine how to initialize it
		if (inOrder) {
			if (this.from != null) {
				this.pushLeftFrom(from);
			} else {
				this.pushLeft(root);
			}
		} else {
			this.pushRight(root);
		}
	}
	
	/**
	 * Pushes the required nodes onto the stack to begin iterating
	 * nodes in order starting from the given value.
	 * @param from the value to start iterating from
	 * @since 3.2.3
	 */
	protected void pushLeftFrom(E from) {
		BinarySearchTreeNode<E> node = this.root;
		while (node != null) {
			int cmp = from.compareTo(node.value);
			if (cmp < 0) {
				// go left
				this.stack.push(node);
				node = node.left;
			} else if (cmp > 0) {
				// go right
				node = node.right;
			} else {
				this.stack.push(node);
				break;
			}
		}
	}
	
	/**
	 * Pushes the left most nodes of the given subtree onto the stack.
	 * @param node the root node of the subtree
	 */
	protected void pushLeft(BinarySearchTreeNode<E> node) {
		// loop until we don't have any more left nodes
		while (node != null) {
			// if we have a iterate to node, then only push nodes
			// to that are less than or equal to it
			if (this.to == null || this.to.compareTo(node.value) >= 0) {
				this.stack.push(node);
			}
			node = node.left;
		}
	}
	
	/**
	 * Pushes the right most nodes of the given subtree onto the stack.
	 * @param node the root node of the subtree
	 */
	protected void pushRight(BinarySearchTreeNode<E> node) {
		// loop until we don't have any more right nodes
		while (node != null) {
			this.stack.push(node);
			node = node.right;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !this.stack.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public E next() {
		// if the stack is empty throw an exception
		if (this.stack.isEmpty()) throw new KronosError(KronosErrorLibrary.NO_SUCH_ELEMENT);
		// get an element off the stack
		BinarySearchTreeNode<E> node = this.stack.pop();
		if (this.inOrder) {
			// add all the left most nodes of the right subtree of this element 
			this.pushLeft(node.right);
		} else {
			// add all the right most nodes of the left subtree of this element 
			this.pushRight(node.left);
		}
		// return the comparable object
		return node.value;
	}
	
	/**
	 * Currently unsupported.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
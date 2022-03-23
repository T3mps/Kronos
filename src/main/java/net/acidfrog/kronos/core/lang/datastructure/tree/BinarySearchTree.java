package net.acidfrog.kronos.core.lang.datastructure.tree;

import java.util.Iterator;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;

/**
 * A binary search tree. This is a self-balancing binary search tree.
 * 
 * @author Ethan Temprovich
 */
public class BinarySearchTree<E extends Comparable<E>> extends Tree<E> {

    private BinarySearchTreeNode<E> root;

    public BinarySearchTree() {
		this.root = null;
		this.size = 0;
	}

    public BinarySearchTree(BinarySearchTree<E> tree) {
        this.insertSubtree(tree);
    }

    @Override
    public boolean insert(E element) {
        if (element == null) throw new KronosError(KronosErrorLibrary.NULL_ELEMENT);
        BinarySearchTreeNode<E> node = new BinarySearchTreeNode<E>(element);
        return this.insert(node);
    }

    @Override
    public boolean insert(Node<E> element) {
        if (this.root == null) {
			// set the root to the new item
			this.root = (BinarySearchTreeNode<E>) element;
			// increment the size ot the tree
			this.size++;
			// return a success
			return true;
		} else {
            if (element == null) throw new KronosError(KronosErrorLibrary.NULL_ELEMENT);
            return this.insert((BinarySearchTreeNode<E>) element, (BinarySearchTreeNode<E>) this.root);
        }
    }

    public boolean insert(BinarySearchTreeNode<E> element, BinarySearchTreeNode<E> parent) {
        BinarySearchTreeNode<E> node = (BinarySearchTreeNode<E>) root;
		while (node != null) {
			if (element.compareTo(node) < 0) {
				if (node.getLeft() == null) {
					node.right = element;
					element.parent = node;
					break;
				} else node = node.left;
			} else {
				if (node.right == null) {
					node.right = element;
					element.parent = node;
					break;
				} else node = node.right;
			}
		}

        
		// return success
		this.size++;
		return true;
    }

    @Override
    public boolean insertSubtree(Tree<E> tree) {
        // check for null
		if (tree == null) return false;
		// check for empty source tree
		if (((BinarySearchTree<E>) tree).root == null) return true;
		// get an iterator to go through all the nodes
		Iterator<E> iterator = iterator();
		// iterate over the nodes
		while (iterator.hasNext()) {
			// create a copy of the node
			BinarySearchTreeNode<E> newNode = new BinarySearchTreeNode<E>(iterator.next());
			// insert the node
			this.insert(newNode);
		}
		// the inserts were successful
		return true;
    }

    @Override
    public boolean remove(E element) {
        if (element == null) return false;
        if (this.root == null) return false;
        if (!(element instanceof E)) throw new KronosError(KronosErrorLibrary.INVALID_ELEMENT);
        BinarySearchTreeNode<E> node = new BinarySearchTreeNode<E>(element);
        return this.remove(node);
    }

    @Override
    public boolean remove(Node<E> node) {
        if (node == null) return false;
        if (this.root == null) return false;
        if (!(node instanceof BinarySearchTreeNode)) throw new KronosError(KronosErrorLibrary.INVALID_ELEMENT);
        return this.remove((BinarySearchTreeNode<E>) node);
    }

    public boolean remove(BinarySearchTreeNode<E> node) {
        if (node == null) return false;
        if (this.root == null) return false;
        if (!(node instanceof BinarySearchTreeNode)) throw new KronosError(KronosErrorLibrary.INVALID_ELEMENT);
        if (node.getLeft() == null && node.getRight() == null) {
            if (node.parent == null) {
                this.root = null;
            } else {
                if  (((BinarySearchTreeNode<E>) node.parent).getLeft() == node) ((BinarySearchTreeNode<E>) node.parent).left = null;
                else ((BinarySearchTreeNode<E>) node.parent).right = null;
            }
        } else if (node.getLeft() == null) {
            if (node.parent == null) {
                this.root = node.right;
            } else {
                if (((BinarySearchTreeNode<E>) node.parent).getLeft() == node) {
                    ((BinarySearchTreeNode<E>) node.parent).left = node.right;
                } else {
                    ((BinarySearchTreeNode<E>) node.parent).right = node.right;
                }
            }
        } else if (node.getRight() == null) {
            if (node.parent == null) {
                this.root = node.left;
            } else {
                if (((BinarySearchTreeNode<E>) node.parent).getLeft() == node) {
                    ((BinarySearchTreeNode<E>) node.parent).left = node.left;
                } else {
                    ((BinarySearchTreeNode<E>) node.parent).right = node.left;
                }
            }
        } else {
            BinarySearchTreeNode<E> left = node.getLeft();
            while (left.getRight() != null) {
                left = left.getRight();
            }
            left.right = node.right;
            
            this.remove(left);
        }
        this.size--;

        return true;
    }

    @Override
    public boolean contains(E element) {
        if (element == null) return false;
        if (this.root == null) return false;
        if (!(element instanceof E)) throw new KronosError(KronosErrorLibrary.INVALID_ELEMENT);
        BinarySearchTreeNode<E> node = new BinarySearchTreeNode<E>(element);
        return this.contains(node);
    }

    @Override
    public boolean contains(Node<E> node) {
        if (node == null) return false;
        if (this.root == null) return false;
        if (!(node instanceof BinarySearchTreeNode)) throw new KronosError(KronosErrorLibrary.INVALID_ELEMENT);
        BinarySearchTreeNode<E> currentNode = (BinarySearchTreeNode<E>) this.root;
        while (currentNode != null) {
            if (currentNode.compareTo(node) == 0) {
                return true;
            } else if (currentNode.compareTo(node) < 0) {
                currentNode = currentNode.getRight();
            } else {
                currentNode = currentNode.getLeft();
            }
        }
        return false;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public Tree<E> clone() {
        BinarySearchTree<E> tree = new BinarySearchTree<E>();
        tree.root = this.root;
        tree.size = this.size;
        return tree;
    }

    @Override
    public Iterator<E> iterator() {
        return new BinarySearchTreeIterator<E>((BinarySearchTreeNode<E>) this.root);
    }

    @Override
    public int compareTo(Tree<E> o) {
        if (o == null) return 1;
        if (!(o instanceof BinarySearchTree)) throw new KronosError(KronosErrorLibrary.INVALID_COMPARISON_TARGET);
        BinarySearchTree<E> tree = (BinarySearchTree<E>) o;
        if (this.size > tree.size) return 1;
        if (this.size < tree.size) return -1;
        return 0;
    }
    
}
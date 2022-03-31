package net.acidfrog.kronos.core.datastructure.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;

/**
 * A a self-balancing binary search tree.
 * 
 * @author Ethan Temprovich
 */
public class BinarySearchTree<E extends Comparable<E>> extends Tree<E> {

    private BinarySearchTreeNode root;

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
        BinarySearchTreeNode node = new BinarySearchTreeNode(element);
        return this.insert(node);
    }

    @Override
    public boolean insert(Node<E> element) {
        if (this.root == null) {
			// set the root to the new item
			this.root = (BinarySearchTreeNode) element;
			// increment the size ot the tree
			this.size++;
			// return a success
			return true;
		} else {
            if (element == null) throw new KronosError(KronosErrorLibrary.NULL_ELEMENT);
            return this.insert((BinarySearchTreeNode) element, (BinarySearchTreeNode) this.root);
        }
    }

    public boolean insert(BinarySearchTreeNode element, BinarySearchTreeNode parent) {
        BinarySearchTreeNode node = (BinarySearchTreeNode) root;
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
			BinarySearchTreeNode newNode = new BinarySearchTreeNode(iterator.next());
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
        BinarySearchTreeNode node = new BinarySearchTreeNode(element);
        return this.remove(node);
    }

    @Override
    public boolean remove(Node<E> node) {
        if (node == null) return false;
        if (this.root == null) return false;
        if (!(node instanceof BinarySearchTreeNode)) throw new KronosError(KronosErrorLibrary.INVALID_ELEMENT);
        return this.remove((BinarySearchTreeNode) node);
    }

    public boolean remove(BinarySearchTreeNode node) {
        if (node == null) return false;
        if (this.root == null) return false;
        if (!(node instanceof BinarySearchTreeNode)) throw new KronosError(KronosErrorLibrary.INVALID_ELEMENT);
        if (node.getLeft() == null && node.getRight() == null) {
            if (node.parent == null) {
                this.root = null;
            } else {
                if  (((BinarySearchTreeNode) node.parent).getLeft() == node) ((BinarySearchTreeNode) node.parent).left = null;
                else ((BinarySearchTreeNode) node.parent).right = null;
            }
        } else if (node.getLeft() == null) {
            if (node.parent == null) {
                this.root = node.right;
            } else {
                if (((BinarySearchTreeNode) node.parent).getLeft() == node) {
                    ((BinarySearchTreeNode) node.parent).left = node.right;
                } else {
                    ((BinarySearchTreeNode) node.parent).right = node.right;
                }
            }
        } else if (node.getRight() == null) {
            if (node.parent == null) {
                this.root = node.left;
            } else {
                if (((BinarySearchTreeNode) node.parent).getLeft() == node) {
                    ((BinarySearchTreeNode) node.parent).left = node.left;
                } else {
                    ((BinarySearchTreeNode) node.parent).right = node.left;
                }
            }
        } else {
            BinarySearchTreeNode left = node.getLeft();
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
        BinarySearchTreeNode node = new BinarySearchTreeNode(element);
        return this.contains(node);
    }

    @Override
    public boolean contains(Node<E> node) {
        if (node == null) return false;
        if (this.root == null) return false;
        if (!(node instanceof BinarySearchTreeNode)) throw new KronosError(KronosErrorLibrary.INVALID_ELEMENT);
        BinarySearchTreeNode currentNode = (BinarySearchTreeNode) this.root;
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
        return new BinarySearchTreeIterator((BinarySearchTreeNode) this.root);
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

    public final class BinarySearchTreeNode extends Tree.Node<E> {

        BinarySearchTreeNode left;
    
        BinarySearchTreeNode right;
        
        public BinarySearchTreeNode(E value) {
            this(value, null, null, null);
        }
    
        public BinarySearchTreeNode(E value, BinarySearchTreeNode parent, BinarySearchTreeNode left, BinarySearchTreeNode right) {
            super(value, parent);
            this.left = left;
            this.right = right;
        }
    
        public BinarySearchTreeNode getLeft() {
            return this.left;
        }
    
        public BinarySearchTreeNode getRight() {
            return this.right;
        }
    
        @Override
        public int compareTo(Tree.Node<E> other) {
            if (!(other instanceof BinarySearchTreeNode)) throw new KronosError(KronosErrorLibrary.INVALID_NODE_TYPE);
            return this.value.compareTo(other.getValue());
        }
    
    }

    private class BinarySearchTreeIterator implements Iterator<E> {

        /** The node stack for iterative traversal */
        final Deque<BinarySearchTreeNode> stack;
        
        /** The root of the tree */
        final BinarySearchTreeNode root;
        
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
        public BinarySearchTreeIterator(BinarySearchTreeNode root) {
            this(root, null, null, true);
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
        private BinarySearchTreeIterator(BinarySearchTreeNode root, E from, E to, boolean inOrder) {
            // set the direction
            this.inOrder = inOrder;
            // create the node stack and initialize it
            this.stack = new ArrayDeque<BinarySearchTreeNode>();
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
            BinarySearchTreeNode node = this.root;
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
        protected void pushLeft(BinarySearchTreeNode node) {
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
        protected void pushRight(BinarySearchTreeNode node) {
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
            BinarySearchTreeNode node = this.stack.pop();
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
    
}
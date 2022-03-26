package net.acidfrog.kronos.core.lang.datastructure.tree;

import java.util.Iterator;

import net.acidfrog.kronos.core.lang.annotations.Internal;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;

public @Internal abstract class Tree<E extends Comparable<E>> implements Comparable<Tree<E>>, Iterable<E>, Cloneable {

    protected int size;

    public Tree() {
        this.size = 0;
    }

    public Tree(Node<E> root) {
        this.insert(root);
    }

    public Tree(Tree<E> tree) {
        this.insertSubtree(tree);
    }

    public abstract boolean insert(E element);

    public abstract boolean insert(Node<E> node);

    public abstract boolean insertSubtree(Tree<E> tree);

    public abstract boolean remove(E element);

    public abstract boolean remove(Node<E> node);

    public abstract boolean contains(E element);

    public abstract boolean contains(Node<E> node);

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public abstract void clear();

    @Override
    public abstract Tree<E> clone();

    @Override
	public abstract Iterator<E> iterator();

    @Override
    public abstract int compareTo(Tree<E> o);
      
    public static @Internal class Node<F extends Comparable<F>> implements Comparable<Node<F>> {

        Node<F> parent;
    
        final F value;
    
        public Node(F value) {
            this(value, null);
        }
    
        public Node(F value, Node<F> parent) {
            if (value == null) throw new KronosError(KronosErrorLibrary.NULL_TREE_NODE_VALUE);
            this.value = value;
            this.parent = parent;
        }
    
        public Node<F> getParent() {
            return parent;
        }
    
        public F getValue() {
            return value;
        }
    
        @Override
        public int compareTo(Node<F> other) {
            return this.value.compareTo(other.value);
        }
    
        @Override
        public String toString() {
            return value.toString();
        }
    
    }
    
}

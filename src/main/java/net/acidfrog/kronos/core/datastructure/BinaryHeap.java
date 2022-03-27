package net.acidfrog.kronos.core.datastructure;

import java.util.Arrays;

public final class BinaryHeap<T extends BinaryHeap.Node> {

    public int size;

	private Node[] nodes;
	private final boolean isMaxHeap;

	public BinaryHeap() {
		this(16, false);
	}

	public BinaryHeap(int capacity, boolean isMaxHeap) {
		this.isMaxHeap = isMaxHeap;
		nodes = new Node[capacity];
	}

	public T add(T node) {
		// Expand if necessary.
		if (size == nodes.length) {
			Node[] newNodes = new Node[size << 1];
			System.arraycopy(nodes, 0, newNodes, 0, size);
			nodes = newNodes;
		}
		// Insert at end and bubble up.
		node.index = size;
		nodes[size] = node;
		acsend(size++);
		return node;
	}

	public T add(T node, float value) {
		node.value = value;
		return add(node);
	}

	public boolean contains(T node, boolean identity) {
		if (node == null) throw new IllegalArgumentException("node cannot be null.");

		if (identity) for(Node n : nodes) if (n == node) {
			return true;
		} else for(Node other : nodes) if (other.equals(node)) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public T peek() {
		if (size == 0) throw new IllegalStateException("The heap is empty.");
		return(T) nodes[0];
	}

	@SuppressWarnings("unchecked")
	public T pop() {
		Node removed = nodes[0];

		if (--size > 0) {
			nodes[0] = nodes[size];
			nodes[size] = null;
			descend(0);
		} else nodes[0] = null;

		return(T) removed;
	}

	public T remove(T node) {
		if (--size > 0) {
			Node moved = nodes[size];
			nodes[size] = null;
			nodes[node.index] = moved;
			
			if (moved.value < node.value ^ isMaxHeap) acsend(node.index);

			else descend(node.index);
		} else nodes[0] = null;

		return node;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		Arrays.fill(nodes, 0, size, null);
		size = 0;
	}

	public void setValue(T node, float value) {
		float oldValue = node.value;
		node.value = value;

		if (value < oldValue ^ isMaxHeap) acsend(node.index);
		else descend(node.index);
	}

	private void acsend(int index) {
		Node[] nodes = this.nodes;
		Node node = nodes[index];
		float value = node.value;

		while(index > 0) {
			int parentIndex = (index - 1) >> 1;
			Node parent = nodes[parentIndex];

			if (value < parent.value ^ isMaxHeap) {
				nodes[index] = parent;
				parent.index = index;
				index = parentIndex;
			} else break;
		}

		nodes[index] = node;
		node.index = index;
	}

	private void descend(int index) {
		Node[] nodes = this.nodes;
		int size = this.size;

		Node node = nodes[index];
		float value = node.value;

		while(true) {
			int leftIndex = 1 +(index << 1);
			if (leftIndex >= size) break;
			int rightIndex = leftIndex + 1;

			// Always has a left child.
			Node leftNode = nodes[leftIndex];
			float leftValue = leftNode.value;

			// May have a right child.
			Node rightNode;
			float rightValue;
			if (rightIndex >= size) {
				rightNode = null;
				rightValue = isMaxHeap ? -Float.MAX_VALUE : Float.MAX_VALUE;
			} else {
				rightNode = nodes[rightIndex];
				rightValue = rightNode.value;
			}

			// The smallest of the three values is the parent.
			if (leftValue < rightValue ^ isMaxHeap) {
				if (leftValue == value ||(leftValue > value ^ isMaxHeap)) break;
				nodes[index] = leftNode;
				leftNode.index = index;
				index = leftIndex;
			} else {
				if (rightValue == value ||(rightValue > value ^ isMaxHeap)) break;
				nodes[index] = rightNode;
				if (rightNode != null) rightNode.index = index;
				index = rightIndex;
			}
		}

		nodes[index] = node;
		node.index = index;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (!(obj instanceof BinaryHeap)) return false;
		BinaryHeap<T> other = (BinaryHeap<T>) obj;
		if (other.size != size) return false;

		Node[] nodes1 = this.nodes, nodes2 = other.nodes;

		for(int i = 0, n = size; i < n; i++) if (nodes1[i].value != nodes2[i].value) {
			return false;
		}
		
		return true;
	}

	public String toString() {
		if (size == 0) return "[]";

		Node[] nodes = this.nodes;
		StringBuilder buffer = new StringBuilder(32);

		buffer.append('[');
		buffer.append(nodes[0].value);

		for(int i = 1; i < size; i++) {
			buffer.append(", ");
			buffer.append(nodes[i].value);
		}

		buffer.append(']');
		return buffer.toString();
	}

	public static class Node {

		float value;
		int index;

		public Node(float value) {
			this.value = value;
		}

		public float getValue() {
			return value;
		}

		public String toString() {
			return Float.toString(value);
		}
	}
    
}

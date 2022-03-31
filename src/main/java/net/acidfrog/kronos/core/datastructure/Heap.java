package net.acidfrog.kronos.core.datastructure;

public sealed interface Heap<T extends Heap.Node> permits BinaryHeap<T> {

	public abstract T push(T node);

	public abstract T push(T node, float value);

	public abstract boolean contains(T node, boolean identity);

	public abstract T peek();

	public abstract T pop();

	public abstract void clear();

	public abstract int size();

    public static class Node {

		protected float value;
		protected int index;

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

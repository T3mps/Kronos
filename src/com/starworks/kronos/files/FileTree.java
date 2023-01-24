package com.starworks.kronos.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileTree implements Iterable<FileHandle> {

	private final Node m_root;

	public FileTree() {
		this.m_root = new Node("", null);
	}

	public void insert(FileHandle fileHandle) {
		String[] dirs = fileHandle.getFileDirectory().split(FileSystem.separator);

		Node current = m_root;
		for (String dir : dirs) {
			Node child = current.getChild(dir);
			if (child == null) {
				child = current.add(dir);
			}
			current = child;
		}
		current.add(fileHandle.getFileName() + fileHandle.getFileExtension(), fileHandle);
	}

	public FileHandle find(String path) {
		Node target = findNode(path);
		if (target == null) {
			return null;
		}
		if (target instanceof Leaf) {
			return ((Leaf) target).m_fileHandle;
		} else {
			throw new IllegalArgumentException("The path does not correspond to a file: " + path);
		}
	}

	public FileHandle findOrCreate(String path) throws IOException {
		Node target = findNode(path);
		if (target == null) {
			FileHandle fileHandle = new FileHandle(path);
			insert(fileHandle);
			return fileHandle;
		}
		if (target instanceof Leaf) {
			return ((Leaf) target).m_fileHandle;
		} else {
			throw new IllegalArgumentException("The path does not correspond to a file: " + path);
		}
	}

	private Node findNode(String path) {
		String[] dirs = path.split(FileSystem.separator);
		Node current = m_root;
		for (String dir : dirs) {
			Node child = current.getChild(dir);
			if (child == null) {
				return null;
			}
			current = child;
		}
		return current;
	}

	public boolean remove(String path) throws IOException {
		Node target = findNode(path);
		if (target == null) {
			throw new IllegalArgumentException("Invalid path: " + path);
		}

		if (target instanceof Leaf) {
			((Leaf) target).m_fileHandle.delete();
		} else {
			removeAllChildren(target);
			new File(path).delete();
		}
		return target.m_parent.remove(target.m_name);
	}

	private void removeAllChildren(Node node) throws IOException {
		for (Node child : node.m_children.values()) {
			if (child instanceof Leaf) {
				((Leaf) child).m_fileHandle.delete();
			} else {
				removeAllChildren(child);
				new File(child.m_name).delete();
			}
		}
		node.m_children.clear();
	}

	@Override
	public Iterator<FileHandle> iterator() {
		return new FileTreeIterator();
	}

	public Node getRoot() {
		return m_root;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(m_root, "", sb);
		return sb.toString();
	}

	private void toString(Node node, String indent, StringBuilder sb) {
		sb.append(indent).append(node.m_name).append("/\n");
		for (Node child : node.m_children.values()) {
			if (child instanceof Leaf) {
				sb.append(indent).append("  ").append(child.m_name).append("/\n");
			} else {
				toString(child, indent + "  ", sb);
			}
		}
	}

	private static sealed class Node permits Leaf {

		final String m_name;
		final Node m_parent;
		final Map<String, Node> m_children;

		Node(String name, Node parent) {
			this.m_name = name;
			this.m_parent = parent;
			this.m_children = new HashMap<String, Node>();
		}

		Node getChild(String name) {
			return m_children.get(name);
		}

		Node add(String name) {
			Node child = new Node(name, this);
			m_children.put(name, child);
			return child;
		}

		Node add(String name, FileHandle fileHandle) {
			Node child = new Leaf(name, this, fileHandle);
			m_children.put(name, child);
			return child;
		}

		boolean remove(String name) {
			return m_children.remove(name) != null;
		}
	}

	private static final class Leaf extends Node {

		final FileHandle m_fileHandle;

		Leaf(String name, Node parent, FileHandle fileHandle) {
			super(name, parent);
			this.m_fileHandle = fileHandle;
		}
	}

	private final class FileTreeIterator implements Iterator<FileHandle> {

		private final Deque<Node> m_stack;

		private FileTreeIterator() {
			this.m_stack = new ArrayDeque<Node>();
			m_stack.push(m_root);
		}

		@Override
		public boolean hasNext() {
			return !m_stack.isEmpty();
		}

		@Override
		public FileHandle next() {
			Node node = m_stack.pop();
			for (Node child : node.m_children.values()) {
				m_stack.push(child);
			}
			if (node instanceof Leaf) {
				return ((Leaf) node).m_fileHandle;
			} else {
				return next();
			}
		}
	}
}

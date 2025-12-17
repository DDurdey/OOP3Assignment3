package implementations;

import java.io.Serializable;
import java.util.NoSuchElementException;

import utilities.BSTreeADT;
import utilities.Iterator;

public class BSTree<E extends Comparable<? super E>> implements BSTreeADT<E>, Serializable {
	private static final long serialVersionUID = 1L;

	private BSTreeNode<E> root;
	private int size;

	public BSTree() {
		root = null;
		size = 0;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
	}

	@Override
	public BSTreeNode<E> getRoot() throws NullPointerException {
		if (root == null)
			throw new NullPointerException();
		return root;
	}

	@Override
	public int getHeight() {
		return heightRec(root);
	}

	private int heightRec(BSTreeNode<E> node) {
		if (node == null)
			return 0;
		return 1 + Math.max(heightRec(node.getLeft()), heightRec(node.getRight()));
	}

	@Override
	public boolean contains(E entry) throws NullPointerException {
		if (entry == null)
			throw new NullPointerException();
		return containsRec(root, entry);
	}

	private boolean containsRec(BSTreeNode<E> node, E entry) {
		if (node == null)
			return false;
		int cmp = entry.compareTo(node.getData());
		if (cmp == 0)
			return true;
		if (cmp < 0)
			return containsRec(node.getLeft(), entry);
		return containsRec(node.getRight(), entry);
	}

	@Override
	public BSTreeNode<E> search(E entry) throws NullPointerException {
		if (entry == null)
			throw new NullPointerException();
		BSTreeNode<E> current = root;
		while (current != null) {
			int cmp = entry.compareTo(current.getData());
			if (cmp == 0)
				return current;
			current = (cmp < 0) ? current.getLeft() : current.getRight();
		}
		return null;
	}

	@Override
	public boolean add(E newEntry) throws NullPointerException {
		if (newEntry == null)
			throw new NullPointerException();
		if (root == null) {
			root = new BSTreeNode<>(newEntry);
			size++;
			return true;
		}
		boolean added = addRec(root, newEntry);
		if (added)
			size++;
		return added;
	}

	private boolean addRec(BSTreeNode<E> node, E entry) {
		int cmp = entry.compareTo(node.getData());
		if (cmp == 0)
			return false; // no duplicates
		if (cmp < 0) {
			if (node.getLeft() == null) {
				node.setLeft(new BSTreeNode<>(entry));
				return true;
			}
			return addRec(node.getLeft(), entry);
		} else {
			if (node.getRight() == null) {
				node.setRight(new BSTreeNode<>(entry));
				return true;
			}
			return addRec(node.getRight(), entry);
		}
	}

	@Override
	public BSTreeNode<E> removeMin() {
		if (root == null)
			return null;
		BSTreeNode<E> min = findMin(root);
		root = removeMinRec(root);
		size--;
		return min;
	}

	private BSTreeNode<E> removeMinRec(BSTreeNode<E> node) {
		if (node.getLeft() == null)
			return node.getRight();
		node.setLeft(removeMinRec(node.getLeft()));
		return node;
	}

	@Override
	public BSTreeNode<E> removeMax() {
		if (root == null)
			return null;
		BSTreeNode<E> max = findMax(root);
		root = removeMaxRec(root);
		size--;
		return max;
	}

	private BSTreeNode<E> removeMaxRec(BSTreeNode<E> node) {
		if (node.getRight() == null)
			return node.getLeft();
		node.setRight(removeMaxRec(node.getRight()));
		return node;
	}

	private BSTreeNode<E> findMin(BSTreeNode<E> node) {
		while (node.getLeft() != null)
			node = node.getLeft();
		return node;
	}

	private BSTreeNode<E> findMax(BSTreeNode<E> node) {
		while (node.getRight() != null)
			node = node.getRight();
		return node;
	}

	@Override
	public Iterator<E> inorderIterator() {
		return new BSTInorderIterator<>(root);
	}

	@Override
	public Iterator<E> preorderIterator() {
		return new BSTPreorderIterator<>(root);
	}

	@Override
	public Iterator<E> postorderIterator() {
		return new BSTPostorderIterator<>(root);
	}
}

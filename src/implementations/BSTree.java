package implementations;

import java.io.Serializable;
import utilities.BSTreeADT;
import utilities.Iterator;

/**
 * BSTree
 *
 * A simple Binary Search Tree implementation that stores elements which are
 * Comparable. This class provides basic BST operations used by the assignment
 * such as add/search/removeMin/removeMax and produces iterators for common
 * tree traversals (in-order, pre-order, post-order).
 *
 * @param <E> type of elements stored in the tree; must implement Comparable
 */
public class BSTree<E extends Comparable<? super E>> implements BSTreeADT<E>, Serializable {
	private static final long serialVersionUID = 1L;

	private BSTreeNode<E> root;
	private int size;

	public BSTree() {
		root = null;
		size = 0;
	}

	/**
	 * Checks whether the tree contains any elements.
	 *
	 * @return true when the tree has no elements, false otherwise
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the number of elements currently stored in the tree.
	 *
	 * @return element count
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Empties the tree of all elements.
	 */
	@Override
	public void clear() {
		root = null;
		size = 0;
	}

	/**
	 * Returns the root node of the tree.
	 *
	 * @return root node
	 * @throws NullPointerException when the tree is empty
	 */
	@Override
	public BSTreeNode<E> getRoot() throws NullPointerException {
		if (root == null)
			throw new NullPointerException();
		return root;
	}

	/**
	 * Computes the height of the tree (number of levels).
	 *
	 * @return height as an integer (0 for empty tree)
	 */
	@Override
	public int getHeight() {
		return heightRec(root);
	}

	// Recursive helper to compute subtree height
	private int heightRec(BSTreeNode<E> node) {
		if (node == null)
			return 0;
		return 1 + Math.max(heightRec(node.getLeft()), heightRec(node.getRight()));
	}

	/**
	 * Checks if the tree contains an element equal to the provided entry.
	 *
	 * @param entry element to search for; must not be null
	 * @return true if element exists in the tree
	 * @throws NullPointerException when entry is null
	 */
	@Override
	public boolean contains(E entry) throws NullPointerException {
		if (entry == null)
			throw new NullPointerException();
		return containsRec(root, entry);
	}

	// Recursive search helper
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

	/**
	 * Searches for a node containing an element equal to the provided entry.
	 *
	 * @param entry element to search for; must not be null
	 * @return the node containing the element, or null if not found
	 * @throws NullPointerException when entry is null
	 */
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

	/**
	 * Inserts a new element into the tree according to natural ordering. Duplicates
	 * are not inserted.
	 *
	 * @param newEntry element to insert; must not be null
	 * @return true if added, false if a duplicate prevented insertion
	 * @throws NullPointerException when newEntry is null
	 */
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

	// Recursive helper to insert into subtree rooted at `node`.
	// Returns true if insertion occurred, false if the entry equals an existing node.
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

	/**
	 * Removes and returns the node containing the minimum element (left-most).
	 *
	 * @return removed node or null if tree is empty
	 */
	@Override
	public BSTreeNode<E> removeMin() {
		if (root == null)
			return null;
		BSTreeNode<E> min = findMin(root);
		root = removeMinRec(root);
		size--;
		return min;
	}

	// Recursive helper to remove left-most node and re-link subtrees
	private BSTreeNode<E> removeMinRec(BSTreeNode<E> node) {
		if (node.getLeft() == null)
			return node.getRight();
		node.setLeft(removeMinRec(node.getLeft()));
		return node;
	}

	/**
	 * Removes and returns the node containing the maximum element (right-most).
	 *
	 * @return removed node or null if tree is empty
	 */
	@Override
	public BSTreeNode<E> removeMax() {
		if (root == null)
			return null;
		BSTreeNode<E> max = findMax(root);
		root = removeMaxRec(root);
		size--;
		return max;
	}

	// Recursive helper to remove right-most node and re-link subtrees
	private BSTreeNode<E> removeMaxRec(BSTreeNode<E> node) {
		if (node.getRight() == null)
			return node.getLeft();
		node.setRight(removeMaxRec(node.getRight()));
		return node;
	}

	// Iterative helper: find left-most node in subtree
	private BSTreeNode<E> findMin(BSTreeNode<E> node) {
		while (node.getLeft() != null)
			node = node.getLeft();
		return node;
	}

	// Iterative helper: find right-most node in subtree
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

// Inorder Iterator -- Left, Root, Right
class BSTInorderIterator<E> implements utilities.Iterator<E> {
		private java.util.Stack<BSTreeNode<E>> stack;
		
		public BSTInorderIterator(BSTreeNode<E> root) {
			stack = new java.util.Stack<>();
			pushLeft(root);
		}
		
		private void pushLeft(BSTreeNode<E> node) {
			while (node != null) {
				stack.push(node);
				node = node.getLeft();
			}
		}
		
		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public E next() throws java.util.NoSuchElementException {
			if (!hasNext()) {
				throw new java.util.NoSuchElementException();
			}
			
			BSTreeNode<E> node = stack.pop();
			pushLeft(node.getRight());
			return node.getData();
		}
}

// Preorder iterator -- Root, Left, Right
class BSTPreorderIterator<E> implements utilities.Iterator<E> {
	private java.util.Stack<BSTreeNode<E>> stack;

	public BSTPreorderIterator(BSTreeNode<E> root) {
		stack = new java.util.Stack<>();
		if (root != null) {
			stack.push(root);
		}
	}

	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public E next() throws java.util.NoSuchElementException {
		if (!hasNext()) {
			throw new java.util.NoSuchElementException();
		}

		BSTreeNode<E> node = stack.pop();

		if (node.getRight() != null) {
			stack.push(node.getLeft());
		}

		return node.getData();
	}
}


// Postorder Iterator -- Left, Right, Root
class BSTPostorderIterator<E> implements utilities.Iterator<E> {
	private java.util.Stack<BSTreeNode<E>> stack;
	private BSTreeNode<E> lastVisited;

	public BSTPostorderIterator(BSTreeNode<E> root) {
		stack = new java.util.Stack<>();
		lastVisited = null;
		pushLeft(root);
	}

	private void pushLeft(BSTreeNode<E> node) {
		while (node != null) {
			stack.push(node);
			node = node.getLeft();
		}
	}

	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public E next() throws java.util.NoSuchElementException {
		if (!hasNext()) {
			throw new java.util.NoSuchElementException();
		}

		while (true) {
			BSTreeNode<E> node = stack.peek();

			if (node.getRight() != null && lastVisited != node.getRight()) {
				pushLeft(node.getRight());
			} else {
				stack.pop();
				lastVisited = node;
				return node.getData();
			}
		}
	}
}

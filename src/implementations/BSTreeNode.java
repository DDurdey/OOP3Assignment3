package implementations;

import java.io.Serializable;

/**
 * BSTreeNode represents a single node in a binary search tree.
 *
 * Each node holds a data element and references to left and right child
 * nodes. This class is kept minimal (getters/setters) to support the BST
 * implementation in this assignment.
 *
 * @param <E> element type stored in the node
 */
public class BSTreeNode<E> implements Serializable {
	private static final long serialVersionUID = 1L;

	private E data;
	private BSTreeNode<E> left;
	private BSTreeNode<E> right;

	/**
	 * Construct a node containing the given data. Child pointers are initialized
	 * to null.
	 *
	 * @param data the element to store in this node
	 */
	public BSTreeNode(E data) {
		this.data = data;
		this.left = null;
		this.right = null;
	}

	/**
	 * Returns the stored data element.
	 *
	 * @return data element
	 */
	public E getData() {
		return data;
	}

	/**
	 * Replaces the stored data element.
	 *
	 * @param data new data element
	 */
	public void setData(E data) {
		this.data = data;
	}

	/**
	 * Returns the left child or null if none.
	 *
	 * @return left child node
	 */
	public BSTreeNode<E> getLeft() {
		return left;
	}

	/**
	 * Sets the left child reference.
	 *
	 * @param left node to set as left child
	 */
	public void setLeft(BSTreeNode<E> left) {
		this.left = left;
	}

	/**
	 * Returns the right child or null if none.
	 *
	 * @return right child node
	 */
	public BSTreeNode<E> getRight() {
		return right;
	}

	/**
	 * Sets the right child reference.
	 *
	 * @param right node to set as right child
	 */
	public void setRight(BSTreeNode<E> right) {
		this.right = right;
	}
}

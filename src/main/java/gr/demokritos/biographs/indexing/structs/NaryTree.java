/* This file is part of BioGraphs.
 *
 * BioGraphs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BioGraphs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BioGraphs.  If not, see <http://www.gnu.org/licenses/>. */

package gr.demokritos.biographs.indexing.structs;

import java.util.List;
import java.util.ArrayList;

/**
 * A java class that implements an N-ary tree with generic nodes.
 * 
 * TODO: 
 * 1) enforce unique objects (what happens if an item is inserted twice?)
 * 2) enrich API with K-Nearest Neighbour methods.
 */
public class NaryTree<T> {
	/**
	 * The {@link NodeComparator} used internally by the n-ary tree's nodes.
	 */
	final NodeComparator<T> nodeComp;

	/**
	 * The n-ary tree's branching factor (N).
	 */
	final int branching;

	/**
	 * The default branching factor to be used in absence
	 * of a user-provided value.
	 */
	private static int DEFAULT_BRANCHING = 8;

	/**
	 * The tree's root node.
	 */
	protected Node<T> root;

	/**
	 * Builds a new tree from a specified root and sets the tree's node
	 * comparator to be the same as the one used in the root node.
	 *
	 * @param root the root node of the new tree
	 */
	public NaryTree(Node<T> root) {
		this.root = root;
		nodeComp = root.nodeComp;
		branching = root.branching;
	}

	/**
	 * Builds a new tree given the root node's data, using a specified
	 * {@link NodeComparator} and the default branching factor.
	 *
	 * @param rootData the data of the root node
	 * @param ndComp a user-specified node comparator
	 */
	public NaryTree(T rootData, NodeComparator<T> ndComp) {
		this.root = new Node<T>(rootData, ndComp, DEFAULT_BRANCHING);
		this.nodeComp = ndComp;
		this.branching = DEFAULT_BRANCHING;
	}

	/**
	 * Builds a new tree given the root node's data, using a specified
	 * {@link NodeComparator} and a user-provided branching factor.
	 *
	 * @param rootData the data of the root node
	 * @param ndComp a user-specified node comparator
	 * @param n the branching factor of the tree
	 */
	public NaryTree(T rootData, NodeComparator<T> ndComp, int n) {
		this.root = new Node<T>(rootData, ndComp, n);
		this.nodeComp = ndComp;
		this.branching = n;
	}

	/**
	 * Builds a new tree that is initially empty, using a specified
	 * {@link NodeComparator} and the default branching factor.
	 *
	 * @param ndComp a user-specified node comparator
	 */
	public NaryTree(NodeComparator<T> ndComp) {
		this.root = null;
		this.nodeComp = ndComp;
		this.branching = DEFAULT_BRANCHING;
	}

	/**
	 * Checks if the tree is empty.
	 *
	 * @return a boolean indicating if the tree is empty or not.
	 */
	public boolean isEmpty() {
		return (root == null);
	}

	/**
	 * Checks if the tree contains a specified node.
	 *
	 * @param key the node to search for
	 * @return a boolean indicating if the tree contains that node
	 */
	protected boolean containsNode(Node<T> key) {
		return root.hasChild(key);
	}

	/**
	 * Checks if the tree contains a specified data item
	 * in one of its nodes.
	 *
	 * @param key the item to search for
	 * @return a boolean indicating if the tree contains that item
	 */
	public boolean containsItem(T key) {
		return containsNode(new Node<T>(key, nodeComp, branching));
	}

	/**
	 * Adds a new node to the tree. If the tree is empty, the node
	 * becomes the tree's root.
	 *
	 * @param toAdd the new node to add in the tree.
	 */
	protected void addNode(Node<T> toAdd) {
		if (root == null) {
			this.root = toAdd;
		}
		else {
			root.addChild(toAdd);
		}
	}

	/**
	 * Adds a new data item to the tree by creating a node and then
	 * adding the node to it.
	 *
	 * @param toAdd the data item to add in the tree.
	 */
	public void addData(T toAdd) {
		if (null == root) {
			Node<T> _toAdd = new Node<T>(toAdd, nodeComp, branching);
			this.root = _toAdd;
		}
		else {
			root.addChild(toAdd);
		}
	}

	/**
	 * Looks for the node in the tree whose data is the most similar
	 * to a specified query item. 
	 *
	 * @param query the reference data
	 * @return the data from the node in the tree that exhibits
	 * the highest similarity to the query
	 */
	public T getNearestNeighbour(T query) {
		if (isEmpty())
			return null;
		Node<T> toSeek = new Node<T>(query, nodeComp, branching);
		Node<T> result = root.getMostSimilarChild(toSeek);
		return result.getData();
	}

	/**
	 * Traverses the tree in arbitrary order.
	 *
	 * @return a list containing the tree's items
	 */
	public List<T> traverse() {
		ArrayList<T> results = new ArrayList<T>();
		/* run traverse in child to populate list */
		root.traverse(results);
		return results;
	}

	/**
	 * Static function to quickly assess this class's performance.
	 */
	public static void main(String[] args) {
		NodeComparator<Integer> nComp = new NodeComparator<Integer>() {
			@Override
			public int compare(Node<Integer> objA, Node<Integer> objB) {
				return (objA.getData() - objB.getData());
			}

			public double getDistance(Node<Integer> objA, Node<Integer> objB) {
				return Math.abs(objA.getData() - objB.getData());
			}
		};

		// create a new node with branching factor 4
		NaryTree<Integer> nTree = new NaryTree<Integer>(10, nComp, 4);

		nTree.addData(2); nTree.addData(5);
		nTree.addData(9); nTree.addData(6); 
		nTree.addData(15); nTree.addData(10);

		System.out.println(nTree.containsItem(5));
		System.out.println(nTree.containsItem(12));
		System.out.println(nTree.containsItem(6));
		System.out.println(nTree.getNearestNeighbour(7));
	}
}

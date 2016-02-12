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

import java.util.*;

/**
 * A class that implements a generic node to be used in
 * {@link NaryTree}'s implementation.
 * 
 * TODO: 
 * 1) add key/value fields as attributes.
 * 2) add method for nearest keys / neighbours
 * 3) complete {@link #get(Node<T>)} and {@link getMostSimilarChild(Node<T>)}
 *    methods.
 *
 * @author VHarisop
 */
public class Node<T> {
	/**
	 * An enumeration of the available node types. Index nodes contain only
	 * keys, while leaf nodes contain the actual data.
	 */
	public enum NodeType {
		INDEX, LEAF
	}

	/**
	 * The type of this node.
	 */
	protected NodeType nodeType;

	/**
	 * The data this node contains. Once this field is set,
	 * it can't be modified.
	 */
	protected final T data;

	/**
	 * A list containing the children of this nodes, that are also nodes.
	 */
	protected List<Node<T>> children;

	/**
	 * The parent node of this object.
	 */
	protected Node<T> parent = null;

	/**
	 * The comparator used for choosing which child node to expand
	 * when the list of children has reached max capacity.
	 */
	protected NodeComparator<T> nodeComp;

	/**
	 * The number of children per node. Once this field is set, it
	 * cannot be modified.
	 */
	protected final int branching;

	/**
	 * Creates a new node containing a specified data item with an initially
	 * empty list of children, using a specified comparator for choosing which
	 * child node to expand and the default value of N.
	 *
	 * @param data the node's data
	 * @param nodeComp the comparator to be used for nodes
	 */
	public Node(T data, NodeComparator<T> nodeComp) {
		this.data = data;
		this.nodeComp = nodeComp;
		this.branching = 8;
		this.children = new ArrayList<Node<T>>(this.branching);

		/* initialize node as leaf node */
		this.nodeType = NodeType.LEAF;
	}

	/**
	 * Creates a new node containing a specified data item whose
	 * list of children is initially empty, using a specified comparator
	 * for choosing which child node to expand and a specified number
	 * of children per node.
	 *
	 * @param data the node's data
	 * @param nodeComp the comparator to be used for nodes
	 * @param n the max number of children per node
	 */
	public Node(T data, NodeComparator<T> nodeComp, int n) {
		this.data = data;
		this.nodeComp = nodeComp;
		this.children = new ArrayList<Node<T>>(n);
		this.branching = n;

		/* initialize node as leaf node */
		this.nodeType = NodeType.LEAF;
	}

	/**
	 * Checks if this node is a leaf node.
	 *
	 * @return a boolean indicating if this is a leaf node.
	 */
	public boolean isLeaf() {
		return (NodeType.LEAF == this.nodeType);
	}
	/**
	 * Sets the parent of the node.
	 *
	 * @param cameFrom the parent of the node
	 */
	public void setParent(Node<T> cameFrom) {
		this.parent = cameFrom;
	}

	/**
	 * Simple getter for the parent of this node.
	 *
	 * @return this node's parent
	 */
	public Node<T> getParent() {
		return this.parent;
	}

	/**
	 * Simple getter for the data the node encapsulates.
	 *
	 * @return the node's data
	 */
	public T getData() {
		return this.data;
	}

	/**
	 * Returns an unmodifiable view of this node's list of children.
	 *
	 * @return an unmodifiable list containing the children of this node.
	 */
	public List<Node<T>> getChildren() {
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * Adds a new child to the node. If the list of children is not full,
	 * the new child is simply appended to it. Otherwise, an iteration among
	 * the children nodes is performed to find the one that has the minimum
	 * distance from the child node to be added, and that node's own
	 * <tt>addChild</tt> is invoked.
	 *
	 * @param toAdd the new node to add
	 */
	protected void addChild(Node<T> toAdd) {
		/* if this is a leaf node, we probably need to expand
		 * since we are creating a new child. The current node's
		 * type is set to INDEX and its own data is copied to a
		 * new node, which is placed in this node's child list
		 * along with the node that was queued for addition. */
		if (isLeaf()) {
			this.nodeType = NodeType.INDEX;
			children.add(getLeaf());
			children.add(toAdd); toAdd.setParent(this);
			return;
		}
		/* Otherwise, this is already an index node -- no need to expand! */


		/* if child list is not empty, add the new item to it
		 * and update its parent */
		if (children.size() < branching) {
			children.add(toAdd);
			toAdd.setParent(this);
		}
		/* otherwise, pick the closest node to descend into */
		else {
			/* expand the child with the minimum distance */
			int minIndex = findMinIndex(toAdd);
			children.get(minIndex).addChild(toAdd);
		}
	}

	/**
	 * Adds a new child to the node.
	 * @see #addChild(Node<T>)
	 *
	 * @param toAdd the data item to add
	 */
	public void addChild(T toAdd) {
		Node<T> nodeToAdd = new Node<T>(toAdd, nodeComp, branching);
		addChild(nodeToAdd);
	}

	/**
	 * Gets the child that is most similar to a query node, or this node
	 * itself if it is a leaf node.
	 *
	 * @param query the query node
	 * @return the node's most similar descendant to the query node, or
	 * this node itself if it is a leaf.
	 */
	protected Node<T> getMostSimilarChild(Node<T> query) {
		if (this.isLeaf()) {
			return this;
		}
		else {
			int minIndex = findMinIndex(query);
			return children.get(minIndex).getMostSimilarChild(query);
		}
	}

	/**
	 * Gets the data from the child whose data is most similar to a query
	 * item, or the data from this node itself if it is a leaf node.
	 *
	 * @param query the query data
	 * @return the node's most similar descendant's item or this node's data
	 * if it is a leaf.
	 */
	public T getMostSimilarChild(T query) {
		Node<T> result = 
			getMostSimilarChild(new Node<T>(query, nodeComp, branching));

		return result.getData();
	}

	/**
	 * Finds the N most similar nodes to a query node in the tree and
	 * returns them to the caller.
	 *
	 * @param query the query node
	 * @param N the number of results to return
	 * @return a list containing the N most similar nodes to the query
	 */
	protected List<Node<T>> getNMostSimilar(Node<T> query, int N) {
		throw new UnsupportedOperationException("Unimplemented feature!");
	}

	/**
	 * Finds the N most similar items to a query item in the tree and returns
	 * them to the caller.
	 *
	 * @param query the query data
	 * @param N the number of similar results to return
	 * @return a list containing the N most similar items to the query
	 */
	public List<T> getNMostSimilar(T query, int N) {
		List<Node<T>> resultNodes = 
			getNMostSimilar(new Node<T>(query, nodeComp, branching), N);
		List<T> res = new ArrayList<T>(resultNodes.size());
		for (Node<T> nd: resultNodes) {
			res.add(nd.getData());
		}

		return res;

	}

	/**
	 * Utility function that, given a reference node, looks for the child
	 * with the smallest distance from the reference node.
	 *
	 * @param ref the reference node
	 * @return the index of the most similar child
	 */
	private int findMinIndex(Node<T> ref) {
		double minDist = nodeComp.getDistance(ref, children.get(0));
		int minIndex = 0;
		for (int i = 1; i < children.size(); ++i) {
			double tempDist = nodeComp.getDistance(ref, children.get(i));
			if (tempDist < minDist) {
				minDist = tempDist;
				minIndex = i;
			}
		}
		return minIndex;
	}

	/**
	 * Finds the item in the graph that is similar to a query item, if any.
	 *
	 * @param query the query node
	 */
	public Node<T> get(Node<T> query) {
		throw new UnsupportedOperationException("Unimplemented feature!");
	}

	/**
	 * Checks if the node contains a query node in its child list or in any
	 * of those children's child list, or if it is itself equal to the node
	 * query.
	 *
	 * @param key the node to search for
	 * @return a boolean indicating if the query node is contained in this
	 * node's descendants
	 */
	protected boolean hasChild(Node<T> key) {
		if (isLeaf()) {
			return key.equals(this);
		}
		else { /* in this case, this is an index node - pick 
				  most similar child to descend into */

			int minIndex = findMinIndex(key);
			return children.get(minIndex).hasChild(key);
		}
	}

	/**
	 * Checks if the node contains a query key in its child list or in any
	 * of those children's child list, or if it contains itself data equal
	 * to the query.
	 *
	 * @param key the item to search for
	 * @return a boolean indicating if the query node is contained in this
	 * node's descendants
	 */
	public boolean hasChild(T key) {
		return hasChild(new Node<T>(key, nodeComp, branching));
	}

	/**
	 * Traverses the tree in arbitrary ordering.
	 *
	 * @param results a list to be filled with data
	 */
	public void traverse(List<T> results) {
		if (isLeaf()) { 
			results.add(this.data);
		}
		else {
			// add results from all children
			for (Node<T> child: children)
				child.traverse(results);
		}
	}

	/**
	 * Get the node's child from a specified index. Returns null if the
	 * index is not found.
	 *
	 * @param index the index of the child
	 * @return the child located at the specified index
	 */
	public Node<T> getChildAt(int index) {
		return children.get(index);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.data);
	}

	@Override
	public boolean equals(Object other) {
		if (null == other) 
			return false;
		if (!(other instanceof Node<?>))
			return false;
		/* compare the enclosed data */
		Node<?> otherNode = (Node<?>) other;
		if (otherNode.getData().equals(this.getData())) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a shallow copy of this node containg exactly the same data and
	 * using the same {@link NodeComparator} and branching factor. The result
	 * is *always* a leaf node. The resulting node's parent is set to this item,
	 * which is the node that created it, since the expanded node is placed in
	 * this node's child list.
	 *
	 * @return a leaf node with the same data and parameters
	 */
	public Node<T> getLeaf() {
		Node<T> leaf = new Node<T>(this.data, this.nodeComp, this.branching);
		leaf.setParent(this);
		return leaf;
	}
}

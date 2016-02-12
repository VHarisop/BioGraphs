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

/**
 * A java class that implements an N-ary tree with generic nodes.
 * 
 * TODO: 
 * 1) make object insertion simple (autocreation of Node<Item> for Item)
 * 2) enforce unique objects (what happens if an item is inserted twice?)
 * 3) enrich API with K-Nearest Neighbour methods.
 */
public class NaryTree<T> {

	final NodeComparator<T> nodeComp;
	final int branching;

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
	 * @return a boolean indicating if the tree contains that node.
	 */
	public boolean containsNode(Node<T> key) {
		return root.hasChild(key);
	}

	/**
	 * Adds a new node to the tree.
	 *
	 * @param toAdd the new node to add in the tree.
	 */
	public void addNode(Node<T> toAdd) {
		root.addChild(toAdd);
	}

	/**
	 * Adds a new data item to the tree by creating a node and then
	 * adding the node to it.
	 *
	 * @param toAdd the data item to add in the tree.
	 */
	public void addData(T toAdd) {
		Node<T> _toAdd = new Node<T>(toAdd, nodeComp, branching);
		root.addChild(_toAdd);
	}

	/**
	 * Static function that tests the correctness of this class. 
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
		Node<Integer> baseNode = new Node<Integer>(10, nComp, 4);
		NaryTree<Integer> nTree = new NaryTree<Integer>(baseNode); 

		nTree.addData(2); nTree.addData(5);
		nTree.addData(9); nTree.addData(6); 
		nTree.addData(15); nTree.addData(10);

		System.out.println(nTree.containsNode(new Node<Integer>(5, nComp, 4)));
		System.out.println(nTree.containsNode(new Node<Integer>(12, nComp, 4)));
		System.out.println(nTree.containsNode(new Node<Integer>(6, nComp, 4)));
	}
}

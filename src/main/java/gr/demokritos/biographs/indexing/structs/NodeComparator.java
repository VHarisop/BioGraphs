package gr.demokritos.biographs.indexing.structs;

import java.util.Comparator;

/**
 * A simple interface that must be implemented by comparators used
 * from {@link NaryTree} objects to compare nodes.
 *
 * TODO:
 * 1) update interface to extend {@link Comparator<T>} instead of Node<T>.
 *
 * @author VHarisop
 */
public interface NodeComparator<T> extends Comparator<Node<T>> {
	/**
	 * Computes the distance between objA and objB using an arbitrary
	 * distance metric.
	 *
	 * @param objA the first object
	 * @param objB the second object
	 * @return a double containing the distance of the two objects
	 */
	public double getDistance(Node<T> objA, Node<T> objB);
}

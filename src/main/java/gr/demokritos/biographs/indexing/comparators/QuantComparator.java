package gr.demokritos.biographs.indexing.comparators;

import gr.demokritos.biographs.*;
import java.util.Comparator;

public interface QuantComparator extends Comparator<BioGraph> {
	/**
	 * Returns the distance between two {@link BioGraph} objects, which is
	 * the absolute value of their similarity based on some metric dependent
	 * on the implementing comparator.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return the distance of the two graphs
	 */
	public double getDistance(BioGraph bgA, BioGraph bgB);
}

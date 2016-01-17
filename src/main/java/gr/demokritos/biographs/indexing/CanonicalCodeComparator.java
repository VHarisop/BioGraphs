package gr.demokritos.biographs.indexing;

import gr.demokritos.biographs.BioGraph;
import java.util.Comparator;

public class CanonicalCodeComparator 
implements Comparator<BioGraph> 
{
	/**
	 * Compares two BioGraph objects based on the lexicographic ordering
	 * of their canonical codes 
	 */
	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		return bgA.getCanonicalCode().compareTo(bgB.getCanonicalCode());
	}
}

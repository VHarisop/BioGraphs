package gr.demokritos.biographs.indexing;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.iit.jinsect.jutils;
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
		/* use the optimized version in jutils to compare canonical codes */
		return jutils.compareCanonicalCodes(bgA.getGraph(), bgB.getGraph());
	}
}

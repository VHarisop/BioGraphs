package gr.demokritos.biographs;

import java.util.Comparator;
import gr.demokritos.iit.jinsect.jutils;

/**
 * A custom similarity comparator to be used for indexing biographs
 * in s-similarity based graph databases.
 *
 * @author VHarisop
 */
public class SimilarityComparator
implements Comparator<BioGraph> 
{
	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		double sSim = 
			jutils.graphStructuralSimilarity(bgA.getGraph(), bgB.getGraph());

		return Double.compare(sSim, 0.0);
	}
}

package gr.demokritos.biographs.indexing;

import java.util.Comparator;
import gr.demokritos.biographs.*;
import gr.demokritos.iit.jinsect.jutils;

public class TwoLevelSimComparator 
implements Comparator<BioGraph> 
{
	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		double sSim = 
			jutils.graphStructuralSimilarity(bgA.getGraph(), bgB.getGraph());

		/* if s-similarity is zero, order according to the canonical codes */
		int compRes = Double.compare(sSim, 0.0);
		if (compRes != 0) 
			return compRes;

		/* compare the canonical codes */
		String cCodeA = bgA.getCanonicalCode();
		String cCodeB = bgB.getCanonicalCode();

		return cCodeA.compareTo(cCodeB);
	}
}

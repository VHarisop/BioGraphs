package gr.demokritos.biographs.indexing.comparators;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.iit.jinsect.structs.UniqueJVertexGraph;
import java.util.Comparator;

public class VarianceComparator 
implements Comparator<BioGraph>
{
	public enum VarianceType {
		VERTEX, DEGREE, WEIGHT
	}

	private VarianceType choice;

	public VarianceComparator(VarianceType varChoice) {
		super();
		choice = varChoice;
	}
	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {

		double varA, varB;
		switch (choice) {
			case WEIGHT:
				varA = bgA.getGraph().getTotalWeightVariance();
				varB = bgB.getGraph().getTotalWeightVariance();
				break;

			case VERTEX:
				varA = bgA.getGraph().getTotalVarRatios();
				varB = bgB.getGraph().getTotalVarRatios();
				break;

			case DEGREE: /* same as default case */
			default:
				varA = bgA.getGraph().getTotalDegreeVariance();
				varB = bgB.getGraph().getTotalDegreeVariance();
				break;
		}
		return Double.compare(varA, varB);
	}
}

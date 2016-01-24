/* Copyright (C) 2016 VHarisop
 * This file is part of BioGraphs.
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

package gr.demokritos.biographs.indexing.comparators;

import gr.demokritos.biographs.BioGraph;
import java.util.Comparator;

/**
 * A comparator that uses various measures of variance in vertices
 * to compare {@link BioGraph} objects. 
 * The variance type used can be either degree variance, which is the
 * product of indegree and outdegree variance for each vertex, weight
 * variace, which is the product of incoming and outgoing weight variance,
 * or variance ratio, which is the ratio of incoming and outgoing variance.
 *
 * @see gr.demokritos.iit.jinsect.structs.calculators.DegreeVarianceCalculator
 * @author VHarisop
 */
public class VarianceComparator 
implements Comparator<BioGraph>
{
	public enum Type {
		RATIO, DEGREE, WEIGHT
	}

	private Type choice;

	/**
	 * Creates a new VarianceComparator that uses a specified type of
	 * variance for {@link BioGraph} comparison.
	 *
	 * @param varChoice the type of variance to use as a measure
	 */
	public VarianceComparator(Type varChoice) {
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

			case RATIO:
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

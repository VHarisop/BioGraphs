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
import gr.demokritos.iit.jinsect.representations.*;
import gr.demokritos.iit.jinsect.comparators.NGramGraphComparator;

import java.util.List;
import java.util.ArrayList;

/**
 * A comparator that computes a vector of similarities to a set of "model"
 * graphs to compare {@link BioGraph} objects. 
 *
 * @author VHarisop
 */
public class ValuesimVectorComparator 
implements TreeComparator
{
	/**
	 * An array of "model" graphs that are used for extracting the value
	 * similarity vector for each graph to be added. 
	 */
	protected List<NGramJGraph> IndexGraphs;

	/**
	 * Creates a new ValuesimVectorComparator that uses a specified type of
	 * variance for {@link BioGraph} comparison.
	 *
	 * @param varChoice the type of variance to use as a measure
	 */
	public ValuesimVectorComparator(List<BioGraph> indexingGraphs) {
		super();
		initGraphs(indexingGraphs); 
	}

	protected void initGraphs(List<BioGraph> initList) {
		/* initialize IndexGraphs */
		IndexGraphs = new ArrayList<NGramJGraph>();
		for (BioGraph bG: initList) {
			IndexGraphs.add(bG.getSuper());
		}
	}

	/**
	 * @see TreeComparator#getDistance
	 */
	public double getDistance(BioGraph bgA, BioGraph bgB) {
		NGramJGraph ngA = bgA.getSuper(),
					ngB = bgB.getSuper();

		NGramGraphComparator ngc = new NGramGraphComparator();
		double simA = 0.0, simB = 0.0;

		/* calculate the sum of similarity vectors for both graphs */
		for (NGramJGraph ngg: IndexGraphs) {
			simA += ngc.getSimilarityBetween(ngA, ngg).ContainmentSimilarity;
			simB += ngc.getSimilarityBetween(ngB, ngg).ContainmentSimilarity;
		}

		return Math.abs(simA - simB);
	}

	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		NGramJGraph ngA = bgA.getSuper(),
					ngB = bgB.getSuper();

		NGramGraphComparator ngc = new NGramGraphComparator();
		double simA = 0, simB = 0;

		/* calculate the sum of similarity vectors for both graphs */
		for (NGramJGraph ngg: IndexGraphs) {
			simA += ngc.getSimilarityBetween(ngA, ngg).ValueSimilarity;
			simB += ngc.getSimilarityBetween(ngB, ngg).ValueSimilarity;
		}

		return Double.compare(simA, simB);
	}
}

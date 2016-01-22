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
import gr.demokritos.iit.jinsect.structs.VertexCoder;
import gr.demokritos.iit.jinsect.jutils;

public class QuantDegreeComparator
implements QuantComparator
{
	protected VertexCoder vWs;

	/**
	 * Returns a new QuantDegreeComparator that is backed by a given hashmap
	 * of Label - Weight entries.
	 *
	 * @param vWeights the map of weights
	 */
	public QuantDegreeComparator(VertexCoder vWeights) {
		super();
		vWs = vWeights;
	}

	/**
	 * Computes the distance between two biographs based on their
	 * degree range code similarity..
	 *
	 * @see QuantComparator#getDistance(BioGraph, BioGraph)
	 *
	 * @param bgA the first graph 
	 * @param bgB the second graph
	 * @return the distance between the two graphs
	 */
	public double getDistance(BioGraph bgA, BioGraph bgB) {
		double d = 
			jutils.getDegreeRangeSimilarity(bgA.getGraph(), bgB.getGraph(), vWs);

		return Math.abs(d);
	}

	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		double qSim = 
			jutils.getDegreeRangeSimilarity(bgA.getGraph(), bgB.getGraph(), vWs);

		return Double.compare(qSim, 0.0);
	}
}

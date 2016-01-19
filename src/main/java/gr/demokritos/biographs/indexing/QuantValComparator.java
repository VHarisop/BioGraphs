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

package gr.demokritos.biographs.indexing;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.iit.jinsect.structs.VertexCoder;
import gr.demokritos.iit.jinsect.jutils;

import java.util.Comparator;

public class QuantValComparator
implements Comparator<BioGraph>
{
	protected VertexCoder vWs;

	/**
	 * Returns a new QuantValComparator that is backed by a given hashmap
	 * of Label - Weight entries.
	 *
	 * @param vWeights the map of weights
	 */
	public QuantValComparator(VertexCoder vWeights) {
		super();
		vWs = vWeights;
	}

	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		double qSim = 
			jutils.getQuantValSimilarity(bgA.getGraph(), bgB.getGraph(), vWs);

		return Double.compare(qSim, 0.0);
	}
}

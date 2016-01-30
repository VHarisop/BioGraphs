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

import gr.demokritos.biographs.*;

public class TotalEntropyComparator 
implements TreeComparator 
{
	/**
	 * @see TreeComparator#getDistance
	 */
	public double getDistance(BioGraph bgA, BioGraph bgB) {
		double eSim = 
			bgA.getGraph().getTotalVertexEntropy() -
			bgB.getGraph().getTotalVertexEntropy();

		return Math.abs(eSim);
	}

	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		int entropySim = Double.compare(
				bgA.getGraph().getTotalVertexEntropy(),
				bgB.getGraph().getTotalVertexEntropy()
				);

		return entropySim;
	}
}

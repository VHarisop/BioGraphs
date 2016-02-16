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
import gr.demokritos.iit.jinsect.comparators.NGramGraphComparator;
import gr.demokritos.iit.jinsect.comparators.OrderedWeightComparator;

public class OrdWeightComparator 
	implements TreeComparator {
	
	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		return new OrderedWeightComparator().compare(
				bgA.getGraph(), bgB.getGraph());
	}

	/**
	 * @see TreeComparator#getDistance(BioGraph, BioGraph) getDistance
	 * The distance for this class is calculated via the two graph's
	 * value similarity.
	 */
	public double getDistance(BioGraph bgA, BioGraph bgB) {
		return new NGramGraphComparator().getSimilarityBetween(
				bgA.getSuper(), bgB.getSuper()).asDistance();
	}
}

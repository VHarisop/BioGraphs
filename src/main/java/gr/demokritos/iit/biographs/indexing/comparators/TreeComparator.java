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

package gr.demokritos.iit.biographs.indexing.comparators;

import java.util.Comparator;

import gr.demokritos.iit.biographs.*;

public interface TreeComparator extends Comparator<BioGraph> {
	/**
	 * Returns the distance between two {@link BioGraph} objects, which is
	 * the absolute value of their similarity based on some metric dependent
	 * on the implementing comparator.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return the distance of the two graphs
	 */
	public double getDistance(BioGraph bgA, BioGraph bgB);
}

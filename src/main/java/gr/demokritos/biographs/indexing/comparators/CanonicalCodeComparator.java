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
import gr.demokritos.iit.jinsect.JUtils;
import java.util.Comparator;

public class CanonicalCodeComparator 
implements Comparator<BioGraph> 
{
	/**
	 * Compares two BioGraph objects based on the lexicographic ordering
	 * of their canonical codes 
	 */
	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		/* use the optimized version in JUtils to compare canonical codes */
		return JUtils.compareCanonicalCodes(bgA.getGraph(), bgB.getGraph());
	}
}

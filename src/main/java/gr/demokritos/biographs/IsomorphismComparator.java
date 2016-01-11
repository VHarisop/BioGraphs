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

package gr.demokritos.biographs;

import gr.demokritos.iit.jinsect.documentModel.representations.NGramJGraph;
import gr.demokritos.iit.jinsect.structs.NGramVertex;
import gr.demokritos.iit.jinsect.structs.JVertex;
import gr.demokritos.iit.jinsect.structs.Edge;
import gr.demokritos.iit.jinsect.structs.UniqueJVertexGraph;

import java.util.HashMap;
import java.util.Set;
import java.lang.Math;

/**
 * This class is a proxy class for (sub-)graph isomorphism testing
 * for n-gram graphs of documents. 
 * It internally calls {@link IsomorphismTester}'s static methods.'
 *
 * @author VHarisop
 */
public class IsomorphismComparator implements IIsomorphism<BioGraph> {
	/**
	 * Creates a blank IsomorphismComparator object.
	 */
	public IsomorphismComparator() {}

	/**
	 * Checks two {@link BioGraph} objects for graph isomorphism.
	 * Refer to {@link IsomorphismTester} for details.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return true if the two graphs are isomorphic, else false
	 */
	public boolean graphIsomorphic(BioGraph bgA, BioGraph bgB) {
		return IsomorphismTester.graphIsomorphic(bgA, bgB);
	}

	/**
	 * Checks two {@link BioGraph} objects for subgraph isomorphism.
	 * Refer to {@link IsomorphismTester} for details.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return true if bgA is subgraph isomorphic to bgB, else false
	 */
	public boolean subgraphIsomorphic(BioGraph bgA, BioGraph bgB) {
		return IsomorphismTester.subgraphIsomorphic(bgA, bgB);
	}
}

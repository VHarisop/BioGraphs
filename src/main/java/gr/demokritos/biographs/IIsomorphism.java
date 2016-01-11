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

public interface IIsomorphism<E> {
	/**
	 * template for subgraphIsomorphic function
	 * that checks whether graphA is subgraph-isomorphic
	 * to graphB 
	 * @param graphA a graph to be tested against
	 * @param graphB the graph to be tested for isomorphism
	 * @return true if graphA is a subgraph of and isomorphic
	 * 		   to graphB, else false
	 */
	public boolean subgraphIsomorphic(E graphA, E graphB);

	/**
	 * template for graphIsomorphic function that checks
	 * whether graphA is isomorphic to graphB
	 * @param graphA the first graph
	 * @param graphB the second graph
	 * @return true if the 2 graphs are isomorphic, else false
	 */
	public boolean graphIsomorphic(E graphA, E graphB);

}

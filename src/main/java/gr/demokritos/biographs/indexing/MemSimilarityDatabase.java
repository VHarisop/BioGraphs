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

/**
 * A class that implements a graph database using the graphs's similarity measure.
 * Here, the similarity measure used is the graph's structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.structs.jutils}. 
 * The values stored here are the complete {@link BioGraph} objects.
 *
 * @author VHarisop
 */
public class MemSimilarityDatabase extends TreeDatabase<BioGraph> {

	/**
	 * Creates a blank MemSimilarityDatabase object
	 */
	public MemSimilarityDatabase() { 
		super(); 
	}

	/**
	 * Creates a blank MemSimilarityDatabase object operating on
	 * a provided directory 
	 *
	 * @param path a string containing the directory's path
	 */
	public MemSimilarityDatabase(String path) {
		super(path);
	}

	/**
	 * Implements the {@link TreeDatabase.getGraphFeature} method, using the 
	 * biographs themselves as values.
	 */
	@Override
	public BioGraph getGraphFeature(BioGraph bg) {
		return bg;
	}
}

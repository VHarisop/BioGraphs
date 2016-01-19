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
import gr.demokritos.biographs.indexing.comparators.SimilarityComparator;


/**
 * A class that implements a graph database using the graphs's similarity measure.
 * Here, the similarity measure used is the graph's structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.structs.UniqueJVertexGraph}.
 *
 * @author VHarisop
 */
public class SimilarityDatabase extends TreeDatabase<String> {
	public SimilarityDatabase() {
		super(new SimilarityComparator());
	}

	public SimilarityDatabase(String path) {
		super(path, new SimilarityComparator());
	}

	@Override
	public String getGraphFeature(BioGraph bg) {
		return bg.getLabel();
	}
}


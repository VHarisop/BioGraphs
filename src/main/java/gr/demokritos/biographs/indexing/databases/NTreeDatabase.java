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

package gr.demokritos.biographs.indexing.databases;

import java.util.List;
import java.io.File;
import java.io.FileFilter;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.ntree.NodeComparator;
import gr.demokritos.ntree.NTree;
import gr.demokritos.biographs.indexing.GraphDatabase;
import gr.demokritos.biographs.indexing.comparators.DefaultHashComparator;


/**
 * An abstract class that implements a graph database using graph similarity.
 * Here, the similarity measure used is the graphs' structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.jutils}
 *
 * @author VHarisop
 */
public class NTreeDatabase extends GraphDatabase {
	/**
	 * A Red-Black tree map implementation that associates biographs
	 * with lists of value types, which can be any type of feature able
	 * to be extracted from a BioGraph. 
	 * @see #getGraphFeature
	 */
	protected NTree<BioGraph> treeIndex;

	/**
	 * A custom comparator to be used for {@link #treeIndex} that
	 * compares graphs based on their s-similarity.
	 */
	protected NodeComparator<BioGraph> bgComp;

	/**
	 * Creates a blank NTreeDatabase object.
	 */
	public NTreeDatabase() { 
		super();
		initIndex(8, 20);
	}
	
	/**
	 * Creates a blank NTreeDatabase object.
	 */
	public NTreeDatabase(int branching, int numBins) { 
		super();
		initIndex(branching, numBins);
	}

	/**
	 * Initialize the database's comparator with a default hash comparator
	 * that contains 20 bins and an N-Tree with branching factor 8.
	 */
	protected void initIndex(int branching, int numBins) {
		this.bgComp = new DefaultHashComparator(numBins);
		this.treeIndex = new NTree<BioGraph>(bgComp, branching);
	}

	/**
	 * Builds a graph database index from a given file or directory,
	 * representing a specified type of data.
	 *
	 * @param path the file or directory
	 * @param gType the type of the encoded data
	 */
	public void build(File path, GraphType gType) throws Exception {
		this.type = gType;
		buildIndex(path);
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
	@Override
	public void buildIndex(String path) throws Exception {
		File fPath = new File(path);
		buildIndex(fPath);
	}

	/**
	 * Builds a graph database index from a given file or a directory 
	 * of files.
	 *
	 * @param fPath a path containing one or multiple files
	 */
	@Override
	public void buildIndex(File fPath) throws Exception {
		if (!fPath.isDirectory()) {
			if (this.type == GraphType.DNA) {
				for (BioGraph bG: BioGraph.fastaFileToGraphs(fPath)) 
					addGraph(bG);
			}
			else {
				for (BioGraph bG: BioGraph.fromWordFile(fPath))
					addGraph(bG);
			}
		}
		else {
			// get all files in a list
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			// add them all to the database
			for (File f: fileList) {
				if (this.type == GraphType.DNA) {
					for (BioGraph bG: BioGraph.fastaFileToGraphs(f))
						addGraph(bG);
				}
				else {
					for (BioGraph bG: BioGraph.fromWordFile(f))
						addGraph(bG);
				}
			}
		}
	}

	/**
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioGraph object to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		treeIndex.addData(bg);
	}

	/**
	 * Gets the nearest neighbour of a query graph.
	 *
	 * @param bg the query graph
	 * @return the nearest neighbouring graph
	 */
	public BioGraph getNearestNeighbour(BioGraph bg) {
		return treeIndex.getNearestNeighbour(bg);
	}

	/**
	 * Gets the K nearest neighbours to a query {@link BioGraph} object
	 * via the approximation of nearest neighbours offered by {@link NTree}'s
	 * function interface.
	 * 
	 * @param bg the query graph
	 * @param K the number of neighbours to look for
	 * @return an array containing the K nearest neighbours
	 */
	public BioGraph[] getKNearestNeighbours(BioGraph bg, int K) {
		List<BioGraph> res = treeIndex.getKNearestNeighbours(bg, K);
		return res.toArray(new BioGraph[res.size()]);
	}
}

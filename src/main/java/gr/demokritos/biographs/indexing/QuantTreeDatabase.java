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

import java.io.File;
import java.io.FileFilter;
import java.lang.Math;
import java.util.*;
import java.util.Map.Entry;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.iit.jinsect.structs.*;
import gr.demokritos.iit.jinsect.jutils;

public abstract class QuantTreeDatabase<V> extends GraphDatabase {
	/**
	 * A HashMap of String - Double entries, where each entry assigns
	 * a weight to a vertex label to be used in computing a custom 
	 * similarity metric. The {@link VertexCoder} class keeps track
	 * of added labels and associates them with an increasing weight.
	 */
	protected VertexCoder vertexWeights;
	
	/**
	 * A Red-Black tree map that associates biographs with lists of 
	 * type <tt>V</tt>, which can be any type of feature able to be
	 * extracted from a {@link BioGraph}.
	 * @see #getGraphFeature
	 */
	protected TreeMap<BioGraph, List<V>> treeIndex;

	/**
	 * A custom comparator to be used for {@link #treeIndex} that 
	 * compares graphs based on their quantized value similarity.
	 */
	protected Comparator<BioGraph> bgComp;

	/**
	 * Creates a blank QuantTreeDatabase object. 
	 */
	public QuantTreeDatabase() {
		super();
		initIndex();
	}

	/**
	 * Creates a new QuantTreeDatabase for maintaining a database
	 * in a given directory. 
	 *
	 * @param path the directory in which the database resides
	 */
	public QuantTreeDatabase(String path) {
		super(path);
		initIndex();
	}

	/**
	 * Initialize the database's label - weight map, comparator,
	 * and tree index. 
	 */
	protected void initIndex() {
		/* initialize the label - weight map, the custom comparator, as
		 * well as the tree map based on the above comparator */
		this.vertexWeights = new VertexCoder();
		this.bgComp = new QuantValComparator(this.vertexWeights);

		this.treeIndex = new TreeMap<BioGraph, List<V>>(this.bgComp);
	}

	/**
	 * Creates a tree-based index for a set of graphs in a file at a 
	 * given path using their quantized value similarity for indexing.
	 *
	 * @param path a string containing the path
	 */
	@Override
	public void buildIndex(String path) throws Exception {
		File f = new File(path);
		buildIndex(f);
	}

	/**
	 * Creates a tree-based index for a set of graphs in a given file or
	 * directory using their quantized value similarity for indexing.
	 *
	 * @param fPath the file or directory from which to build the index
	 */
	@Override
	public void buildIndex(File fPath) throws Exception {
		/* Set of seen vertices in lexicographic ordering */
		SortedSet<JVertex> seenVertices = 
			new TreeSet<JVertex>(new Comparator<JVertex>() {
				@Override
				public int compare(JVertex vA, JVertex vB) {
					return vA.getLabel().compareTo(vB.getLabel());
				}
			});

		if (!fPath.isDirectory()) {
			BioGraph[] bgs = BioGraph.fastaFileToGraphs(fPath);
			/* Compute the union of all vertices */
			for (BioGraph bG: bgs) {
				seenVertices.addAll(bG.getGraph().vertexSet());
			}
			
			/* populate the label - weight map, create the comparator */
			assignWeights(seenVertices);

			/* add all the graphs to the index */
			for (BioGraph bG: bgs) {
				addGraph(bG);
			}
		}
		else {
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			/* get all biographs into list */
			List<BioGraph> bgList = new ArrayList<BioGraph>();
			for (File f: fileList) {
				BioGraph[] bgs = BioGraph.fastaFileToGraphs(f);

				/* Compute the union of all vertices */
				for (BioGraph bG: bgs) {
					seenVertices.addAll(bG.getGraph().vertexSet());
				}

				/* add the biographs to the list */
				bgList.addAll(Arrays.asList(bgs));
			}
			
			/* populate the label - weight map */
			assignWeights(seenVertices);

			/* add all the graphs to the index */
			for (BioGraph bG: bgList) {
				addGraph(bG);
			}
		}
	}
	/**
	 * Builds a graph database index from a given file or directory 
	 * of files which contain words without extra labels, as in the
	 * case of FASTA files.
	 *
	 * @param fPath a path pointing to a file or directory 
	 */
	public void buildWordIndex(File fPath) throws Exception {
		/* Set of seen vertices in lexicographic ordering */
		SortedSet<JVertex> seenVertices = 
			new TreeSet<JVertex>(new Comparator<JVertex>() {
				@Override
				public int compare(JVertex vA, JVertex vB) {
					return vA.getLabel().compareTo(vB.getLabel());
				}
			});

		if (!fPath.isDirectory()) {
			BioGraph[] bgs = BioGraph.fromWordFile(fPath);
			/* add all the labels to the set of seen labels */
			for (BioGraph bG: bgs) {
				seenVertices.addAll(bG.getGraph().vertexSet());
			}

			/* populate the label - weight map */
			assignWeights(seenVertices);
			
			/* add them to the index */
			for (BioGraph bG: bgs) {
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

			List<BioGraph> bgList = new ArrayList<BioGraph>();
			// add them all to the database
			for (File f: fileList) {
				BioGraph[] bgs = BioGraph.fromWordFile(f);
				/* update the set of seen vertices */
				for (BioGraph bG: bgs) {
					seenVertices.addAll(bG.getGraph().vertexSet());
				}
				
				/* add all the biographs to the list */
				bgList.addAll(Arrays.asList(bgs));
			}

			/* populate the label - weight map */
			assignWeights(seenVertices);

			for (BioGraph bG: bgList) {
				addGraph(bG);
			}
		}
	}

	/**
	 * Assigns incremental weights to vertex labels, starting from an initial
	 * value and using a step, usually 1/10 of the initial value. Labels are 
	 * assigned a value in lexicographic order. The value assignment is handled
	 * by {@link vertexWeights}.
	 *
	 * @param vertexSet the set of vertices
	 */
	protected void assignWeights(SortedSet<JVertex> vertexSet) {
		/* iterate and add seen labels to the coder class. */
		for (JVertex vCurr: vertexSet) {
			vertexWeights.putLabel(vCurr.getLabel());
		}
	}

	/**
	 * Adds a new graph to the database, updating the index as well.
	 *
	 * @param bg the BioGraph object to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		List<V> nodeValues = treeIndex.get(bg);

		/* if key wasn't there, initialize label array */
		if (nodeValues == null) {
			nodeValues = new ArrayList<V>();
		}

		nodeValues.add(getGraphFeature(bg));
		treeIndex.put(bg, nodeValues);
	}

	/**
	 * Gets the keys of the underlying tree map of the database.
	 *
	 * @return a set with all of the map's keys
	 */
	public Set<BioGraph> exposeKeys() {
		return treeIndex.keySet();
	}

	/**
	 * Gets the entries of the underlying tree map of the database.
	 *
	 * @return a set with all of the map's entries
	 */
	public Set<Map.Entry<BioGraph, List<V>>> exposeEntries() {
		return treeIndex.entrySet();
	}

	/**
	 * Gets the nodes corresponding to the biograph query, whose
	 * similarity to the query biograph is 0, according to the 
	 * custom comparator {@link #bgComp}.
	 *
	 * @param bg the biograph key to be searched for
	 * @return a list of node values
	 */
	public List<V> getNodes(BioGraph bQuery) {
		return treeIndex.get(bQuery);
	}

	/**
	 * Gets the nearest neighbours.
	 */
	public List<V> getNearestNeighbours(BioGraph bQuery, boolean include) {
		assert(vertexWeights.size() > 0) : "Weight Map is empty!";

		if (include) {
			/* if an exact match exists, return it */
			List<V> matches = this.getNodes(bQuery);
			if (matches != null) {
				return Collections.unmodifiableList(matches);
			}
		}

		/* get immediately neighboring tree nodes */
		BioGraph lower = treeIndex.lowerKey(bQuery);
		BioGraph higher = treeIndex.higherKey(bQuery);

		/* if both are null, return null */
		if (lower == null && higher == null)
			return null;

		/* handle cases where only one of the neighboring
		 * tree nodes is null, by returning the other */

		if (higher == null) {
			List<V> ans = getNodes(lower);
			return (ans == null) ? null : Collections.unmodifiableList(ans);
		}
		if (lower == null) { 
			List<V> ans = getNodes(higher);
			return (ans == null) ? null : Collections.unmodifiableList(ans);
		}

		/* get the similarities between the lower and higher keys */
		double distLo =
			Math.abs(jutils.getQuantValSimilarity(bQuery.getGraph(),
												  lower.getGraph(),
												  vertexWeights));
		double distHi = 
			Math.abs(jutils.getQuantValSimilarity(bQuery.getGraph(),
												  higher.getGraph(),
												  vertexWeights));

		if (super.compareDouble(distLo, distHi)) {
			List<V> nodes = new ArrayList<V>();
			nodes.addAll(getNodes(lower));
			nodes.addAll(getNodes(higher));
			return Collections.unmodifiableList(nodes);
		}
		else if (distLo < distHi) {
			return Collections.unmodifiableList(getNodes(lower));
		}
		else /* if (distLo > distHi) */ {
			return Collections.unmodifiableList(getNodes(higher));
		}
	}

	public List<V> getKNearestNeighbours(BioGraph bG, boolean include, int K) {
		List<V> results = new ArrayList<V>();
		K = (K > treeIndex.size()) ? treeIndex.size() : K;
		if (include) {
			/* if the inclusive flag is set, check if querying the graph
			 * gives nonempty results; If so, add them */
			List<V> ans = this.getNodes(bG);
			if (ans != null) {
				results.addAll(ans);
				K--;
			}
		}

		/* if K results were already found, return them */
		if (K == 0) {
			return Collections.unmodifiableList(results);
		}

		/* Get tail and head views. The head view should be accessed in reverse
		 * order, since it contains entries with keys "less" than the query */
		NavigableMap<BioGraph, List<V>> tail =
			treeIndex.tailMap(bG, false);
		NavigableMap<BioGraph, List<V>> head =
			treeIndex.headMap(bG, false);

		// lower values must be polled in reverse order
		Entry<BioGraph, List<V>> high = tail.firstEntry();
		Entry<BioGraph, List<V>> low = head.lastEntry();
		double distLo, distHi;

		/* if stuff to return remains, loop */
		while (K > 0) {
			/* handle cases where at least one of the maps has been depleted */
			if (low == null && high == null) {
				return Collections.unmodifiableList(results);
			}
			if (low == null) {
				tail = tail.tailMap(high.getKey(), false);
				results.addAll(high.getValue());
				high = tail.firstEntry();
				K--; continue;
			}
			if (high == null) {
				head = head.headMap(low.getKey(), false);
				results.addAll(low.getValue());
				low = head.lastEntry();
				K--; continue;
			}

			/* if none of the maps has been depleted yet,
			 * we must compare their similarities at each step */
			distLo = Math.abs(jutils.getQuantValSimilarity(
						bG.getGraph(),
						low.getKey().getGraph(),
						this.vertexWeights));
			distHi = Math.abs(jutils.getQuantValSimilarity(
						bG.getGraph(),
						high.getKey().getGraph(),
						this.vertexWeights));

			if (super.compareDouble(distLo, distHi)) {
				results.addAll(low.getValue());
				results.addAll(high.getValue());
				head = head.headMap(low.getKey(), false);
				low = head.lastEntry();
				tail = tail.tailMap(high.getKey(), false);
				high = tail.firstEntry();
				K -= 2;
			}
			else if (distLo < distHi) {
				results.addAll(low.getValue());
				head = head.headMap(low.getKey(), false);
				low = head.lastEntry();
				K--;
			}
			else /* if (distLo > distHi) */ {
				results.addAll(high.getValue());
				tail = tail.tailMap(high.getKey(), false);
				high = tail.firstEntry();
				K--;
			}
		}
		/* finally, return list of neighboring values */
		return Collections.unmodifiableList(results);
	}

	/**
	 * Returns the label - weight map that the database uses. 
	 *
	 * @return the map of label - weight pairs.
	 */
	public VertexCoder getWeightMap() {
		return vertexWeights;
	}


	/**
	 * A method that extracts a feature from a BioGraph. This method
	 * must be overriden in all classes subclassing QuantTreeDatabase.
	 *
	 * @param bg the graph to extract features from
	 * @return a feature of the graph
	 */
	public abstract V getGraphFeature(BioGraph bg);
}

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

import gr.demokritos.iit.jinsect.representations.NGramJGraph;
import gr.demokritos.iit.jinsect.structs.*;
import gr.demokritos.iit.jinsect.encoders.*;
import gr.demokritos.iit.jinsect.jutils;

import org.biojava.nbio.core.sequence.DNASequence;

import java.util.HashMap;
import java.util.Objects;

/**
 * A class for representing N-Gram Graphs for biological sequences.
 * <tt>BioGraph</tt> builds heavily on NGramJGraph.
 *
 * A BioGraph is an N-Gram Graph of single order N.
 *
 * @author VHarisop
 */
public final class BioGraph extends NGramJGraph {

	final static long serialVersionUID = 1L;

	/** 
	 * the label of the dataset this graph represents.
	 */
	protected String bioLabel = null;

	/**
	 * A cached copy of this graph's hash encoding.
	 */
	protected double[] hashEncoding = null;

	/**
	 * A cached copy of this graph's index hash encoding.
	 */
	protected double[] indexEncoding = null;
	
	/**
	 * Creates a BioGraph object to represent a given string.
	 * This method invokes the default NGramJGraph constructor.
	 *
	 * @param data the string to be represented
	 */
	public BioGraph(String data) {
		super(data);
	}

	/**
	 * Creates a BioGraph object to represent a given data string
	 * with an associated label. 
	 *
	 * @param data the string to be represented
	 * @param label the label of the data to be represented
	 */
	public BioGraph(String data, String label) {
		super(data);
		bioLabel = label;
	}

	/**
	 * Creates a BioGraph object to represent a given data string
	 * with an associated label using a given order for the n-grams 
	 * and a given correlation  window for calculating n-gram co-occurences.
	 *
	 * @param data the string to be represented 
	 * @param label the label of the data to be represented
	 * @param order the order of the n-grams
	 * @param correlationWindow the length of the correlation window
	 */
	public BioGraph(String data, String label, int order, int correlationWindow) {
		super(data, order, order, correlationWindow);
		bioLabel = label;
	}

	/**
	 * Creates a BioGraph object to represent a given string, using
	 * the order specified for the N-grams and the given correlation
	 * window for calculating N-gram co-occurences.
	 * @param data the string to be represented
	 * @param order the order of the n-grams
	 * @param correlationWindow the length of the correlation window
	 */
	public BioGraph(String data, int order, int correlationWindow) {
		super(data, order, order, correlationWindow);
	}

	/**
	 * Creates a BioGraph object to represent a given 
	 * {@link org.biojava.nbio.core.sequence.DNASequence}
	 *
	 * @param dnaSeq a <tt>DNASequence</tt> object 
	 * @return a <tt>BioGraph</tt> object to represent the sequence
	 */
	public static BioGraph fromSequence(DNASequence dnaSeq) {
		return new BioGraph(dnaSeq.getSequenceAsString());
	}

	/**
	 * Creates a BioGraph object to represent a given
	 * {@link org.biojava.nbio.core.sequence.DNASequence} 
	 * with an associated label.
	 *
	 * @param dnaSeq a DNASequence object
	 * @param label the associated label
	 * @return a BioGraph object that represents the sequence
	 */
	public static BioGraph fromSequence(DNASequence dnaSeq, String label) {
		return new BioGraph(dnaSeq.getSequenceAsString(), label);
	}

	/**
	 * Returns the underlying {@link UniqueVertexGraph} object that 
	 * implements the N-gram graph representation. By definition, it is
	 * the zero-index graph in the vertex graph array.
	 * @return the underlying UniqueVertexGraph 
	 */
	public final UniqueVertexGraph getGraph() {
		return getGraphLevel(0);
	}

	/**
	 * Return the underlying label-vertex map of the graph implementing
	 * the BioGraph object.
	 * @return a HashMap object of String - JVertex pairs.
	 */
	public HashMap<String, JVertex> getVertexMap() {
		return getGraph().UniqueVertices;
	}

	/**
	 * Simple getter for {@link bioLabel}.
	 *
	 * @return the graph's bioLabel
	 */
	public final String getLabel() {
		return bioLabel;
	}

	/**
	 * Return a String representation of the graph in DOT format. The 
	 * representation is a directed graph.
	 *
	 * @return the string representation of the graph in DOT format
	 */
	public final String toDot() {
		return jutils.graphToDot(getGraphLevel(0), true);
	}

	/**
	 * Returns the string produced by the DFS encoding of the
	 * underlying graph. 
	 * @see gr.demokritos.iit.jinsect.encoders.DepthFirstEncoder#getEncoding()
	 * for label ordering and implementation. The simple encode() method
	 * should be used by default, since indexing N-gram graphs has not
	 * yet been tried using frequent fragments.
	 * @return an array of dfs labels
	 */
	public final String getDfsCode() {
		return (new DepthFirstEncoder(getGraph())).getEncoding();
	}
	
	/**
	 * Returns the label produced by the DFS encoding of the underlying graph.
	 * @see gr.demokritos.iit.jinsect.structs.DepthFirstEncoder#getEncoding()
	 * for label ordering and implementation.
	 *
	 * @param vFrom the starting node
	 * @return the dfs label
	 */
	public final String getDfsCode(JVertex vFrom) {
		return (new DepthFirstEncoder(getGraph(), vFrom)).getEncoding();
	}
	
	/**
	 * Returns the string representation produced by the canonical coding
	 * of the underlying graph. 
	 * @see gr.demokritos.iit.jinsect.encoders.CanonicalCoder 
	 * for implementation and details.
	 *
	 * @return the canonical code of the graph
	 */
	public final String getCanonicalCode() {
		return (new CanonicalCoder(getGraph())).getEncoding();
	}

	/**
	 * Returns a string representation of the list of ordered vertex pairs.
	 * @see UniqueVertexGraph#getOrderedWeightPairs 
	 *
	 * @return the string representation of the weight-ordered vertices
	 */
	public String getOrderedVertexCode() {
		String toRet = "";
		for (Pair<JVertex, Double> p: getGraph().getOrderedWeightPairs()) {
			toRet += "_" +
				p.getFirst().getLabel() +
				String.valueOf(p.getSecond());
		}
		return toRet;
	}

	/**
	 * Creates a {@link NGramJGraph} object that would be this object's
	 * superclass representation.
	 *
	 * @return a {@link NGramJGraph} object from this graph's parameters
	 */
	public NGramJGraph getSuper() {
		return new NGramJGraph(
					this.DataString, 
					this.MinSize, 
					this.MaxSize, 
					this.CorrelationWindow);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getLabel());
	}
}


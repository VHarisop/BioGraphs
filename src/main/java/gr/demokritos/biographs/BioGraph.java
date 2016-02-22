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
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;
import gr.demokritos.biographs.indexing.preprocessing.*;
import gr.demokritos.iit.jinsect.jutils;
import gr.demokritos.iit.jinsect.io.LineReader;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import java.io.File;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.LinkedHashMap;

/**
 * A class for representing N-Gram Graphs for biological sequences.
 * <tt>BioGraph</tt> builds heavily on NGramJGraph.
 *
 * A BioGraph is an N-Gram Graph of single order N.
 *
 * @author VHarisop
 */
public class BioGraph extends NGramJGraph {

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
	 * Creates a BioGraph object to represent a 
	 * {@link org.biojava.nbio.core.sequence.DNASequence} 
	 * that is provided in a given FASTA File.
	 * @param inFile the <tt>File</tt> which contains the sequence 
	 * @return a <tt>BioGraph</tt> object to represent the sequence 
	 * @throws Exception if something is wrong with the file
	 */
	public static BioGraph fromFastaFile(File inFile) 
	throws Exception
	{
		BioGraph bGraph = null;
		LinkedHashMap<String, DNASequence> entries = readFastaFile(inFile);
		/* try reading the first dna sequence from the file */
		for (Entry<String, DNASequence> entry: entries.entrySet()) {
			bGraph = fromSequence(entry.getValue(), entry.getKey());
			break;
		}

		return bGraph;
	}
	
	/**
	 * Creates a BioGraph object to represent a 
	 * {@link org.biojava.nbio.core.sequence.DNASequence} 
	 * that is provided in a FASTA file with a given path.
	 * @param fName a <tt>String</tt> containing the path of the file.
	 * @return a <tt>BioGraph</tt> object to represent the sequence 
	 * @throws Exception if something is wrong with the file
	 */
	public static BioGraph fromFastaFile(String fName) 
	throws Exception 
	{
		BioGraph bGraph = null;
		LinkedHashMap<String, DNASequence> entries = readFastaFile(fName);
		/* try reading the first dna sequence from the file */
		for (Entry<String, DNASequence> entry : entries.entrySet()) {
			bGraph = fromSequence(entry.getValue(), entry.getKey());
			break;
		}

		return bGraph;
	}

	/**
	 * Creates an array of BioGraph objects to represent a series of
	 * {@link org.biojava.nbio.core.sequence.DNASequence} that are provided
	 * in a FASTA file at a given path.
	 *
	 * @param fName the file containing the sequences
	 * @return an array of BioGraph objects to represent the sequences
	 * @throws Exception if something is wrong with the file 
	 */
	public static BioGraph[] fastaFileToGraphs(File fName) 
	throws Exception 
	{
		BioGraph[] bGraphs; 
		LinkedHashMap<String, DNASequence> entries = readFastaFile(fName);

		// allocate space for each entry
		bGraphs = new BioGraph[entries.size()];
		int bCnt = 0;

		for (Entry<String, DNASequence> entry: entries.entrySet()) {
			bGraphs[bCnt++] = fromSequence(entry.getValue(), entry.getKey());
		}

		return bGraphs;
	}

	/**
	 * Returns the underlying {@link UniqueVertexGraph} object that 
	 * implements the N-gram graph representation. By definition, it is
	 * the zero-index graph in the vertex graph array.
	 * @return the underlying UniqueVertexGraph 
	 */
	public UniqueVertexGraph getGraph() {
		return getGraphLevel(0);
	}

	/**
	 * Returns the sum of normalized edge weights of the underlying 
	 * {@link gr.demokritos.iit.jinsect.structs.UniqueVertexGraph} 
	 * object, which is a positive double value.
	 *
	 * @return the sum of normalized edge weights
	 */
	public double getTotalNormWeight() {
		return getGraph().totalNormWeight();
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
	public String getLabel() {
		return bioLabel;
	}

	/**
	 * Return a String representation of the graph in DOT format. The 
	 * representation is a directed graph.
	 *
	 * @return the string representation of the graph in DOT format
	 */
	public String toDot() {
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
	public String getDfsCode() {
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
	public String getDfsCode(JVertex vFrom) {
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
	public String getCanonicalCode() {
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
	 * This method is a proxy to {@link #fromFileLines(File)} 
	 *
	 * @param filePath the string containing the file path
	 * @return an array of {@link BioGraph} objects
	 */
	public static BioGraph[] fromWordFile(String filePath) 
	throws Exception
	{
		return fromWordFile(new File(filePath));
	}

	/**
	 * Creates an array of BioGraph objects, each of which is built 
	 * using a line from a given file as a data string, which also
	 * becomes the graph's bioLabel.
	 *
	 * @param path the file from which to read the lines
	 * @return an array of BioGraph objects
	 */
	public static BioGraph[] fromWordFile(File path) 
	throws Exception 
	{
		/* read lines, allocate array */
		String[] lines = new LineReader().getLines(path);
		BioGraph[] bGs = new BioGraph[lines.length];

		for (int i = 0; i < lines.length; ++i) {
			// the raw data string becomes the label
			bGs[i] = new BioGraph(lines[i], lines[i]);
		}

		return bGs;
	}

	/**
	 * A wrapper method that reads DNA sequences from a file, given its path.
	 *
	 * @param fName a string containing the path of the file
	 * @return a map of string/sequence pairs 
	 */
	public static LinkedHashMap<String, DNASequence> readFastaFile(String fName) 
	throws Exception 
	{
		return readFastaFile(new File(fName));
	}

	/**
	 * A wrapper method around {@link FastaReaderHelper#readFastaDNASequence}
	 * in order to facilicate reading DNA sequences from FASTA files. 
	 *
	 * @param inFile the file from which to read the sequences
	 * @return a hash map of String/Sequence pairs.
	 */
	public static LinkedHashMap<String, DNASequence> readFastaFile(File inFile) 
	throws Exception 
	{
		return FastaReaderHelper.readFastaDNASequence(inFile);
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
	/**
	 * Calculates and the graph's hash encoding using a
	 * {@link DefaultHashVector} encoder and caches it.
	 *
	 * @param usesDna a boolean indicating whether this graph encodes
	 * biological sequences or not
	 * @param nBins the number of bins to use in hashing
	 * @return a double array containing the graph's hash encoding
	 */
	public void computeHashEncoding(boolean usesDna, int nBins) {
		if (hashEncoding == null) {
			DefaultHashVector hVec;
			if (usesDna) {
				hVec = new DefaultHashVector(GraphType.DNA).withBins(nBins);
			}
			else {
				hVec = new DefaultHashVector(GraphType.WORD).withBins(nBins);
			}
			hashEncoding = hVec.encodeGraph(this);
		}
	}
	
	/**
	 * Calculates and the graph's index hash encoding using a specified
	 * {@link DefaultHashVector} encoder and caches it.
	 *
	 * @param hVec the {@link DefaultHashVector} to use
	 */
	public void computeIndexEncoding(DefaultHashVector hVec) {
		if (indexEncoding == null) {
			indexEncoding = hVec.encodeGraph(this);
		}
	}
	
	/**
	 * Calculates and returns the graph's hash encoding using a
	 * {@link DefaultHashVector} encoder.
	 *
	 * @param usesDna a boolean indicating whether this graph encodes
	 * biological sequences or not
	 * @param nBins the number of bins to use in hashing
	 * @return a double array containing the graph's hash encoding
	 */
	public double[] getHashEncoding(boolean usesDna, int nBins) {
		computeHashEncoding(usesDna, nBins);
		return hashEncoding;
	}

	/**
	 * Calculates and returns the graph's index hash encoding using
	 * a specified {@link DefaultHashVector}.
	 *
	 * @param hVec the {@link DefaultHashVector} to use
	 * @return a double array containing the graph's hash encoding
	 */
	public double[] getIndexEncoding(DefaultHashVector hVec) {
		computeIndexEncoding(hVec);
		return indexEncoding;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getLabel());
	}
}


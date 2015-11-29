package gr.demokritos.biographs;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.structs.UniqueVertexGraph;
import gr.demokritos.iit.jinsect.utils;

import salvo.jesus.graph.Vertex;
import salvo.jesus.graph.WeightedEdgeImpl;
import salvo.jesus.graph.algorithm.DepthFirstCoding;

import org.biojava.nbio.core.sequence.DNASequence;

import java.io.File;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

/**
 * A class for representing N-Gram Graphs for biological sequences.
 * <tt>BioGraph</tt> builds heavily on DocumentNGramGraph and defines
 * a few extra methods for exposing the underlying graph and edge sets.
 *
 * A BioGraph is an N-Gram Graph of single order N.
 *
 * @author VHarisop
 */
public class BioGraph extends DocumentNGramGraph {

	/**
	 * Creates a BioGraph object to represent a given string.
	 * This method invokes the default DocumentNGramGraph constructor
	 * and then calls the <code>setDataString</code> method.
	 * @param data the string to be represented
	 */
	public BioGraph(String data) {
		super();
		this.setDataString(data);
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
		super(order, order, correlationWindow);
		this.setDataString(data);
	}

	/**
	 * Creates a BioGraph object to represent a given 
	 * {@link org.biojava.nbio.core.sequence.DNASequence }
	 * @param dnaSeq a <tt>DNASequence</tt> object 
	 * @return a <tt>BioGraph</tt> object to represent the sequence
	 */
	public static BioGraph fromSequence(DNASequence dnaSeq) {
		return new BioGraph(dnaSeq.getSequenceAsString());
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
		LinkedHashMap<String, DNASequence> entries = 
			GraphDatabase.readFastaFile(inFile);
		/* try reading the first dna sequence from the file */
		for (Entry<String, DNASequence> entry: entries.entrySet()) {
			bGraph = fromSequence(entry.getValue());
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
		LinkedHashMap<String, DNASequence> entries = 
			GraphDatabase.readFastaFile(fName);
		/* try reading the first dna sequence from the file */
		for (Entry<String, DNASequence> entry : entries.entrySet()) {
			bGraph = fromSequence(entry.getValue());
			break;
		}

		return bGraph;
	}

	/**
	 * Returns the underlying <tt>UniqueVertexGraph</tt> object that 
	 * implements the N-gram graph representation. By definition, it is
	 * the zero-index graph in the vertex graph array.
	 * @return the underlying UniqueVertexGraph 
	 */
	public UniqueVertexGraph getGraph() {
		return getGraphLevel(0);
	}

	/**
	 * Returns the underlying edge set of the graph implementing the 
	 * n-gram graph. 
	 * @return a Set object containing <tt>WeightedEdgeImpl</tt> instances.
	 */
	public Set<WeightedEdgeImpl> getEdgeSet() {
		return getGraph().getEdgeSet();
	}

	/**
	 * Return the underlying label-vertex map of the graph implementing
	 * the BioGraph object.
	 * @return a HashMap object containing <tt>String</tt> - <tt>Vertex</tt> pairs.
	 */
	public HashMap<String, Vertex> getVertexMap() {
		return getGraph().UniqueVertices;
	}

	/**
	 * Return a String representation of the graph in DOT format. The 
	 * representation is an undirected graph.
	 * @return the string representation of the graph in DOT format
	 */
	public String toDot() {
		return utils.graphToDot(getGraphLevel(0), false);
	}

	/**
	 * Returns an array of labels produced by the DFS encoding of the
	 * underlying graph. 
	 * @see salvo.jesus.graph.algorithm.DepthFirstCoding#encodeByInfo() 
	 * for label ordering and implementation. The simple encode() method
	 * should be used by default, since indexing N-gram graphs has not
	 * yet been tried using frequent fragments.
	 * @return an array of dfs labels
	 */
	public ArrayList<String> dfsCode() {
		return new DepthFirstCoding(getGraph()).encodeByInfo();
	}

	/**
	 * Returns the concatenation of the array of labels returned
	 * by the dfs encoding of the underlying graph.
	 * @return a <tt>String</tt> containing the DFS labels
	 */
	public String getDfsCode() {
		String code = "";

		/* append all the labels into one */
		for (String s: this.dfsCode()) {
			code += s;
			code += "\n";
		}

		return code;
	}

}


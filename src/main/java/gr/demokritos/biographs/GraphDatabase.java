package gr.demokritos.biographs;

import java.io.File;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 * An abstract class that handles a graph database, consisting of BioJGraph
 * objects. Provides auxiliary methods for reading fasta files into the database,
 * checking if the database is empty, etc.
 *
 * @author VHarisop
 */
public abstract class GraphDatabase {

	/* the path of the graph database */
	protected String path;

	/* boolean variable indicating if the database
	 * resides in RAM */
	protected boolean inMem;

	/* an array list of graphs to be kept in memory */
	protected ArrayList<BioJGraph> graphArray;
	protected int arrayIndex;
	
	/**
	 * Creates a blank GraphDatabase object.
	 */
	public GraphDatabase() { 
		path = null;
		inMem = true;

		graphArray = new ArrayList();
		arrayIndex = -1;
	}

	/**
	 * Creates a new GraphDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public GraphDatabase(String path) {
		this.path = path;
		inMem = false;

		graphArray = new ArrayList();
		arrayIndex = -1;
	}

	/**
	 * Gets the BioJGraph located at a given index in the graph array.
	 * If the index is larger than the current array size, returns null instead.
	 *
	 * @param graphIndex the index of the graph we want to retrieve
	 * @return the biograph at the given index, or null if no such graph exists.
	 */
	public BioJGraph getGraph(int graphIndex) {
		if (arrayIndex > graphIndex) 
			return null;

		return graphArray.get(graphIndex);
	}

	/**
	 * Checks if the graph database is empty by checking arrayIndex.
	 * 
	 * @return true if the graph array is empty, otherwise false.
	 */
	public boolean isEmpty() {
		return (arrayIndex > -1);
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
	public abstract void buildIndex(String path) throws Exception;

	/**
	 * Builds a graph database index from a given file or a directory 
	 * of files.
	 *
	 * @param path a path containing one or multiple files
	 */
	public abstract void buildIndex(File path) throws Exception;

	/**
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioJGraph object to be added
	 */
	public abstract void addGraph(BioJGraph bg);

	/**
	 * A wrapper method that reads DNA sequences from a file, given its pathname.
	 *
	 * @param fName a <tt>String</tt> containing the path of the file
	 * @return a <tt>LinkedHashMap</tt> of String/Sequence pairs.
	 */
	public static LinkedHashMap<String, DNASequence> readFastaFile(String fName) 
	throws Exception
	{
		return readFastaFile(new File(fName));
	}

	/**
	 * A wrapper method around BioJava's <tt>readFastaDNASequence</tt> in order
	 * to facilicate reading DNA sequences from FASTA files. 
	 *
	 * @param inFile the <tt>File</tt> from which to read the sequences
	 * @return a <tt>LinkedHashMap</tt> of String/Sequence pairs.
	 */
	public static LinkedHashMap<String, DNASequence> readFastaFile(File inFile) 
	throws Exception 
	{
		return FastaReaderHelper.readFastaDNASequence(inFile);
	}
}

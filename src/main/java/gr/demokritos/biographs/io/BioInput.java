/* This file is part of BioGraphs.
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

package gr.demokritos.biographs.io;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

import gr.demokritos.biographs.*;
import gr.demokritos.iit.jinsect.io.LineReader;

import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.DNASequence;

/**
 * Utility class with static wrapper methods for reading from FASTA
 * files into different data structures.
 *
 * @author VHarisop
 */
public class BioInput {
	private BioInput() {}

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
			bGraph = BioGraph.fromSequence(entry.getValue(), entry.getKey());
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
			bGraph = BioGraph.fromSequence(entry.getValue(), entry.getKey());
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
			bGraphs[bCnt++] =
				BioGraph.fromSequence(entry.getValue(), entry.getKey());
		}

		return bGraphs;
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
	 * A wrapper method around {@link FastaReaderHelper#readFastaDNASequence}
	 * that returns the read DNA sequences as string - string pairs.
	 *
	 * @param inFile the file from which to read the sequences
	 * @return a {@link LinkedHashMap} of String/String pairs
	 * @throws Exception if the file doesn't exist or if something goes wrong
	 * when reading the data
	 */
	public static LinkedHashMap<String, String>
	fromFastaFileToEntries(File inFile) throws Exception
	{
		LinkedHashMap<String, String> res =
			new LinkedHashMap<String, String>();
		for (Entry<String, DNASequence> e: readFastaFile(inFile).entrySet()) 
		{
			res.put(e.getKey(), e.getValue().getSequenceAsString());
		}
		return res;
	}


}

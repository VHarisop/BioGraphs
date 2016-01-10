package gr.demokritos.biographs;

import java.io.File;

public class BioEntry extends BioData<Double> {
	/**
	 * The data label, which can be used for reverse search
	 * in the original file using the FASTA labels. 
	 */
	protected String label;

	/**
	 * Creates a new BioEntry object given a {@link BioGraph}'s total 
	 * normalized edge weight, the file it is contained in, and its label.
	 *
	 * @param graphWeight the total normalized weight of the graph
	 * @param fPath the path of the file
	 */
	public BioEntry(double graphWeight, File fPath, String bLabel) {
		super(graphWeight, fPath);
		label = bLabel;
	}

	/**
	 * {@link BioGraphData#label} 
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Returns a new BioEntry object given a {@link BioGraph} and a 
	 * {@link java.io.File} calling the constructor internally 
	 * after acquiring the needed parameters.
	 *
	 * @param bg the BioGraph the entry will refer to
	 * @param f the path of the containing file
	 * @return a new BioEntry object
	 */
	public static BioEntry fromGraph(BioGraph bg, File f) {
		return new BioEntry(bg.getTotalNormWeight(), f, bg.bioLabel);
	}

	/**
	 * Creates an array of {@link BioEntry} using the 
	 * {@link BioGraph} representations from a given file.
	 *
	 * @param fPath the path of the file
	 * @return an array of {@link BioEntry} entries
	 * @throws Exception if something goes wrong parsing the file
	 */
	public static BioEntry[] fromFile(File fPath)
	throws Exception 
	{
		BioGraph[] bioGraphs = BioGraph.fastaFileToGraphs(fPath);
		BioEntry[] entries = new BioEntry[bioGraphs.length];
		int iCnt = 0;

		// create one entry for each sequence in the file
		for (BioGraph bG: bioGraphs) {
			entries[iCnt++] = 
				new BioEntry(bG.getTotalNormWeight(), fPath, bG.bioLabel);
		}

		return entries;
	}
}

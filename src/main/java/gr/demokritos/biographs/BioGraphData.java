package gr.demokritos.biographs;

import java.io.File;

public class BioGraphData extends BioData<BioGraph> {
	/**
	 * The data label, which can be used for reverse search
	 * in the original file using the FASTA labels. 
	 */
	protected String label;

	public BioGraphData(BioGraph bGraph, File fPath) {
		super(bGraph, fPath);
		label = bGraph.bioLabel;
	}

	/**
	 * {@link BioGraphData#label} 
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Creates an array of {@link BioData} using the 
	 * {@link BioGraph} representations from a given file.
	 *
	 * @param fPath the path of the file
	 * @return an array of {@link BioData} entries
	 * @throws Exception if something goes wrong parsing the file
	 */
	public static BioGraphData[] fromFile(File fPath)
	throws Exception 
	{
		BioGraph[] bioGraphs = BioGraph.fastaFileToGraphs(fPath);
		BioGraphData[] entries = new BioGraphData[bioGraphs.length];
		int iCnt = 0;

		// create one entry for each sequence in the file
		for (BioGraph bG: bioGraphs) {
			entries[iCnt++] = new BioGraphData(bG, fPath);
		}

		return entries;
	}
}

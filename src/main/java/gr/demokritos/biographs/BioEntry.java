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

package gr.demokritos.biographs;

public interface IIsomorphism<E> {

	/**
	 * template for subgraphIsomorphic function
	 * that checks whether graphA is subgraph-isomorphic
	 * to graphB 
	 * @param graphA a graph to be tested against
	 * @param graphB the graph to be tested for isomorphism
	 * @return true if graphA is a subgraph of and isomorphic
	 * 		   to graphB, else false
	 */
	public boolean subgraphIsomorphic(E graphA, E graphB);


	/**
	 * template for graphIsomorphic function that checks
	 * whether graphA is isomorphic to graphB
	 * @param graphA the first graph
	 * @param graphB the second graph
	 * @return true if the 2 graphs are isomorphic, else false
	 */
	public boolean graphIsomorphic(E graphA, E graphB);

}

package assignment.flooding;

import java.util.Random;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;

//import peersim.core.Linkable;

public class Topology extends WireGraph {

	private static final String PAR_COORDINATES_PROT = "coord_protocol";

	/** Coordinate protocol pid. */
	private final int coordPid;

	public Topology(String prefix) {
		super(prefix);
		coordPid = Configuration.getPid(prefix + "." + PAR_COORDINATES_PROT);
	}

	// ------------------------------------------------------------------------
	/**
	 * Performs the actual wiring. It is a static factory method. Being static,
	 * it can also be invoked by a {@link peersim.dynamics.WireByMethod}
	 * initializer control.
	 * 
	 * @param g
	 *            * a {@link peersim.graph.Graph} interface object to work on.
	 */
	public void wire(Graph g) {
		/** Contains the distance in hops from the root node for each node. */
		
		NetworkInfo net_in = NetworkInfo.getInstance();
		g = net_in.getGraph();
		int net_size = net_in.getNetwork_size();
		int neighbor_size = net_in.getNeighbor_size();
		int rand_neighbor_k = 0;
		Node current_node;
		
		for (int i = 0; i < net_size;++i)
		{
			Random rand = new Random();
			rand_neighbor_k = rand.nextInt(neighbor_size) + 1;
			current_node = Network.get(i);
			//setting neighbor of nodes
			for (int j = 0; j < rand_neighbor_k;++j)
			{
				int k = rand.nextInt(net_size);
				while (k == i)
				{
					k = rand.nextInt(net_size);
				}
				g.setEdge(i, k);
			}
		}
		net_in.printGraph();
	}

	// ------------------------------------------------------------------------
	private static double distance(Node node1, Node node2, int coordPid) {
		Coordinates c1 = (Coordinates) node1.getProtocol(coordPid);
		Coordinates c2 = (Coordinates) node2.getProtocol(coordPid);
		if (c1.getX() == -1 || c2.getX() == -1 || c1.getY() == -1 || c2.getY() == -1)
			throw new RuntimeException("Un-initialized coordinate");
		return c1.distance(c2);
	}
	// ------------------------------------------------------------------------
}

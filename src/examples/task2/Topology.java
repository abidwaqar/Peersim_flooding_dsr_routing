package examples.task2;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;
import peersim.transport.Transport;

//import peersim.core.Linkable;

public class Topology extends WireGraph {

	private static final String PAR_COORDINATES_PROT = "coord_protocol";

	/** Coordinate protocol pid. */
	//TODO not public
	public final int coordPid;

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
		net_in.setGraph(g);
		int total_vlr = net_in.getTotal_vlr();
		int total_hlr = net_in.getTotal_hlr();
		
		//all VLRs get HLR as neighbor
		for (int i = 1; i <= total_vlr; i++) {
			g.setEdge(i, 0);
			g.setEdge(0, i);
		}
		
		//VLR and Node
		for (int i = total_vlr + total_hlr; i < Network.size(); ++i) {
			Coordinates n_coor = (Coordinates) Network.get(i).getProtocol(net_in.getNode_coord_pid());
			int x = (int) n_coor.getX();
			int y = (int) n_coor.getY();
			int vlr_idx = (x/net_in.getLa_width_height()) + (y/net_in.getLa_width_height())*net_in.getTotal_col_la() + net_in.getTotal_hlr();
			g.setEdge(i, vlr_idx);
			g.setEdge(vlr_idx, i);
//			System.out.println("Connecting Node:" + i + "With vlr:" + vlr_idx);
			Node m_node = Network.get(i);
			((Transport)m_node.getProtocol(FastConfig.getTransport(5))).send(m_node,
					Network.get(vlr_idx), new Message(m_node, Message.MessageCode.add), 5);
//			System.out.println();
//			System.out.println();
//			System.out.println("i: "+ i + " vlr_idx: "+ vlr_idx + " " + x + " "+ y);
		}
//		System.exit(0);
		// TODO test of distance() function; to be removed
//		int test = 1;
//		Node parent = (Node) (g.getNode(test));
//		Node n = (Node) (g.getNode(test + 1));
//		double distance = distance(n, parent, coordPid);
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

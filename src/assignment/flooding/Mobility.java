package assignment.flooding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.GeneralNode;
import peersim.core.Network;
import peersim.core.Node;
import peersim.graph.NeighbourListGraph;
import sun.util.resources.cldr.ur.CurrencyNames_ur;;

public class Mobility implements Control
{
	public int mobility;
	
	public Mobility(String prefix) {
//		super(prefix);
		mobility = Configuration.getInt("MOBILITY");
	}
	
	@Override
	public boolean execute() {
		
		System.out.println("Mobility Running");
		NetworkInfo net_in = NetworkInfo.getInstance();
		NeighbourListGraph g = net_in.getGraph();
		int net_size = net_in.getNetwork_size();
		int nodes_to_change = (net_size*mobility)/100;
//		System.out.println(mobility);
//		System.out.println(net_size);
//		System.out.println((net_size*mobility)/100);
		int neighbor_size = net_in.getNeighbor_size();
		int rand_neighbor_k = 0;
		int rand_node = 0;
		Random rand = new Random();

		for (int i = 0; i < nodes_to_change; ++i)
		{
			rand_node = rand.nextInt(net_size);
			Object[] curr_node_neighbor = g.getNeighbours(rand_node).toArray();
			for (int j = 0; j< curr_node_neighbor.length; ++j)
			{
				g.clearEdge(rand_node, (Integer)curr_node_neighbor[j]);
			}
			rand_neighbor_k = rand.nextInt(neighbor_size) + 1;
			//setting neighbor of nodes
			for (int j = 0; j < rand_neighbor_k;++j)
			{
				int k = rand.nextInt(net_size);
				while (k == rand_node)
				{
					k = rand.nextInt(net_size);
				}
				g.setEdge(rand_node, k);
			}
		}
		net_in.printGraph();
		
		// TODO Auto-generated method stub
		return false;
	}

}

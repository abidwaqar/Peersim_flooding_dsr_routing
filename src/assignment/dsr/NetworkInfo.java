package assignment.dsr;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.sun.swing.internal.plaf.metal.resources.metal_zh_CN;

import javafx.util.Pair;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.graph.Graph;
import peersim.graph.NeighbourListGraph;

public class NetworkInfo {
	
	public static NetworkInfo net_in;
	
	public static NetworkInfo getInstance()
	{
		if (net_in == null)
		{
			net_in = new NetworkInfo();
		}
		return net_in;
	}
	
	private int network_size;
//	private String trace_file_name;
//	private String lookup_file_name;
//	private BufferedReader FileBufferedReader;
	private int node_coord_pid;
	private int unreliable_transport_pid;
	private NeighbourListGraph graph;
	private int neighbor_size;
	private int sending_rate;

	public void printInfo()
	{
		System.out.println("Network Size: " + network_size);
		System.out.println("Neighbor Size: " + neighbor_size);
//		System.out.println("Trace File Name: " + trace_file_name);
//		System.out.println("Lookup file name: " + lookup_file_name);
	}

	private NetworkInfo()
	{
		network_size = Network.size();
		neighbor_size = Configuration.getInt("NEIGHBOR_SIZE");
//		trace_file_name = Configuration.getString("TRACEFILE", "mobisim_trace.txt");
		node_coord_pid = Configuration.getPid("init.0.protocol");
//		lookup_file_name = Configuration.getString("LOOKUPFILE", "lookup_trace.txt");
		unreliable_transport_pid = 4;
		graph = new NeighbourListGraph(network_size, false);
		sending_rate = Configuration.getInt("SENDING_RATE");
		
//		try {
//        	
//    		// FileReader reads text files in the default encoding.
//            FileReader fileReader = new FileReader(trace_file_name);
//        	// Always wrap FileReader in BufferedReader.
//            FileBufferedReader = new BufferedReader(fileReader);
//            //ignoring first two lines
//			FileBufferedReader.readLine();
//	        FileBufferedReader.readLine();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
//	private double distance(Node node1, Node node2, int coordPid) 
//	{
//		Coordinates c1 = (Coordinates) node1.getProtocol(coordPid);
//		Coordinates c2 = (Coordinates) node2.getProtocol(coordPid);
//		if (c1.getX() == -1 || c2.getX() == -1 || c1.getY() == -1 || c2.getY() == -1)
//			throw new RuntimeException("Un-initialized coordinate");
//		return c1.distance(c2);
//	}
	
//	void setTopology(double neighborRange)
//	{
//		for (int i = 0; i < this.network_size; ++i)
//		{
//			for (int j = 0; j<this.network_size; ++j)
//			{
//				if (this.distance(Network.get(i), Network.get(j), this.node_coord_pid) <= neighborRange)
//				{
//					
//				}
//			}
//		}
//	}

	public void printGraph()
	{
		for(int i = 0; i< network_size; ++i)
		{
			System.out.printf("Node: " + i + " --> ");
			Collection<Integer> neighbor = graph.getNeighbours(i);
			Iterator<Integer> itr = neighbor.iterator();
			while (itr.hasNext())
			{
				System.out.print(itr.next() + ", ");
			}
			System.out.println();
		}
	}
	
	public int getSending_rate() {
		return sending_rate;
	}

	public int getNeighbor_size() {
		return neighbor_size;
	}

	public int getUnreliable_transport_pid() {
		return unreliable_transport_pid;
	}

	public int getNode_coord_pid() {
		return node_coord_pid;
	}

//	public String getTrace_file_name() {
//		return trace_file_name;
//	}

	public int getNetwork_size() {
		return network_size;
	}

	public NeighbourListGraph getGraph() {
		return graph;
	}
	
//	public BufferedReader getFileBufferedReader()
//	{
//		return FileBufferedReader;
//	}

//	@Override
//	protected void finalize() throws Throwable {
//		FileBufferedReader.close();
//	}
	
	
}

package examples.task2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.swing.internal.plaf.metal.resources.metal_zh_CN;

import javafx.util.Pair;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.graph.Graph;

public class NetworkInfo {
	
	private int network_size;
	private int graph_size;
	private int i_param;
	private int cells_per_vlr;
	private int total_hlr;
	private int total_vlr;
	private int total_MN;
	private int total_col_la;
	private int total_cells;
	private String trace_file_name;
	private String lookup_file_name;
	private int node_coord_pid;
	private int la_width_height;
	private BufferedReader FileBufferedReader;
	private BufferedReader lookupFileBR;
	private Graph graph;
	private ArrayList<Pair<Integer, Integer>> lookupArr;
	private int updateCount;
	private int searchCount;
	private ArrayList<Integer> meanCostPerSecond;
	private int MCPSi;
	
	public void printInfo()
	{
		System.out.println("Network Size: " + network_size);
		System.out.println("Graph Size: " + graph_size);
		System.out.println("IParam: " + i_param);
		System.out.println("Cells Per VLR: " + cells_per_vlr);
		System.out.println("Total HLR: " + total_hlr);
		System.out.println("Total VLR: " + total_vlr);
		System.out.println("Total Mobile Nodes: " + total_MN);
		System.out.println("Total column LA: " + total_col_la);
		System.out.println("Total Cells: " + total_cells);
		System.out.println("Trace File Name: " + trace_file_name);
		System.out.println("Lookup file name: " + lookup_file_name);
		System.out.println("LA width height : " + la_width_height);
		int sum = 0;
		for (int i = 0;i< meanCostPerSecond.size();++i)
		{
			sum += meanCostPerSecond.get(i);
//			System.out.print(meanCostPerSecond.get(i) + " ");
		}
		System.out.println("Total size of array: " + meanCostPerSecond.size());
		System.out.println("Total Sum of Mean cost: " + sum);
		System.out.println("Mean Cost: " +  (float)sum/meanCostPerSecond.size());
		
	}
	
	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public static NetworkInfo net_in;
	public static NetworkInfo getInstance()
	{
		if (net_in == null)
		{
			net_in = new NetworkInfo();
		}
		return net_in;
	}
	
	public int getNode_coord_pid() {
		return node_coord_pid;
	}

	private NetworkInfo()
	{
		network_size = Network.size();
		graph_size = Configuration.getInt("GRAPHSIZE", 400);
		i_param = Configuration.getInt("IPARAM", 1);
		total_cells = Configuration.getInt("TOTALCELLS", 64);
		total_hlr = Configuration.getInt("TOTALHLR", 1);
		cells_per_vlr = (int) Math.pow(4, i_param);
		total_vlr = total_cells/cells_per_vlr;
		total_MN = network_size - total_hlr - total_vlr;
		total_col_la = (int) (Math.sqrt(total_cells)/Math.sqrt(cells_per_vlr));
		trace_file_name = Configuration.getString("TRACEFILE", "mobisim_trace.txt");
		node_coord_pid = Configuration.getPid("init.0.protocol");
		la_width_height = graph_size/total_col_la;
		lookup_file_name = Configuration.getString("LOOKUPFILE", "lookup_trace.txt");
		lookupArr = new ArrayList<Pair<Integer, Integer>>();	//node, time
		updateCount = 0;
		searchCount = 0;
		MCPSi = 0;
		meanCostPerSecond = new ArrayList<>();
		
        try {
        	
    		// FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(trace_file_name);
        	// Always wrap FileReader in BufferedReader.
            FileBufferedReader = new BufferedReader(fileReader);
            //ignoring first two lines
			FileBufferedReader.readLine();
	        FileBufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
        	
    		// FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(lookup_file_name);
        	// Always wrap FileReader in BufferedReader.
            lookupFileBR = new BufferedReader(fileReader);
            //ignoring first two lines
			lookupFileBR.readLine();
			
			String line = lookupFileBR.readLine();
			
			while(line != null)
			{
				String[] val = line.split("\t");   	
				lookupArr.add(new Pair<Integer, Integer>(Integer.parseInt(val[0]),  Integer.parseInt(val[1])));
				line = lookupFileBR.readLine();
			}
			
			lookupFileBR.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
        
	}
	
	public ArrayList<Integer> getMeanCostPerSecond() {
		return meanCostPerSecond;
	}

	public int getMCPSi() {
		return MCPSi;
	}

	public ArrayList<Pair<Integer, Integer>> getLookupArr() {
		return lookupArr;
	}

	public boolean is_in_same_la(int oldX, int oldY, int newX, int newY)
	{
		if (cal_la(oldX, oldY) == cal_la(newX, newY))
			return true;
		return false;
	}
	
	public int cal_la(int x, int y)
	{
		return (x/this.la_width_height)+(y/this.la_width_height)*this.total_col_la;
	}

	public int getLa_width_height() {
		return la_width_height;
	}

	public void setTotal_hlr(int total_hlr) {
		this.total_hlr = total_hlr;
	}

	public String getTrace_file_name() {
		return trace_file_name;
	}

	public int getNetwork_size() {
		return network_size;
	}

	public int getGraph_size() {
		return graph_size;
	}

	public int getI_param() {
		return i_param;
	}

	public int getCells_per_vlr() {
		return cells_per_vlr;
	}

	public int getTotal_hlr() {
		return total_hlr;
	}

	public int getTotal_vlr() {
		return total_vlr;
	}

	public int getTotal_MN() {
		return total_MN;
	}

	public int getTotal_col_la() {
		return total_col_la;
	}

	public int getTotal_cells() {
		return total_cells;
	}
	
	public BufferedReader getFileBufferedReader()
	{
		return FileBufferedReader;
	}
	
	public void incrementUpdate()
	{
		++updateCount;
		++MCPSi;
	}
	
	public int getUpdateCount() {
		return updateCount;
	}
	
	public void incrementSearch()
	{
		++searchCount;
		++MCPSi;
	}
	
	public int getSearchCount() {
		return searchCount;
	}
	
	public void setTOMeanCost()
	{
		meanCostPerSecond.add(MCPSi);
		MCPSi = 0;
	}

	@Override
	protected void finalize() throws Throwable {
		FileBufferedReader.close();
	}
}

package examples.task2;

import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;

import java.util.ArrayList;
import java.util.Collection;

import examples.task2.Message.MessageCode;
import peersim.cdsim.CDProtocol;
import peersim.edsim.EDProtocol;
import peersim.edsim.Network_Info;
import peersim.graph.Graph;

/**
 * Event driven version of epidemic averaging.
 */
public class ProtocolNode implements CDProtocol, EDProtocol, Linkable {

	/** Neighbors currently in the cache */
	private Node[] cache;

	/** Time stamps currently in the cache */
	private int[] tstamps;

	/** * @param prefix string prefix for config properties */
	public ProtocolNode(String prefix) {
	}

	public Object clone() {
		return this;
	}

	// --------------------------------------------------------------------------
	/**
	 * * This is the standard method the define periodic activity. The frequency
	 * of execution of this method is defined by a
	 * {@link peersim.edsim.CDScheduler} component in the configuration.
	 */

	public void nextCycle(Node node, int pid) {
		System.out.println(" nextCycle done ");
		
		Linkable linkable = (Linkable) node.getProtocol(FastConfig
				.getLinkable(pid));
		// TODO think
		if (linkable.degree() > 0) {
			Node peern = linkable.getNeighbor(CommonState.r.nextInt(linkable
					.degree()));

			// quick handling of failures (message would be lost anyway, we save
			// time)
			if (!peern.isUp())
				return;

			((Transport) node.getProtocol(FastConfig.getTransport(pid))).send(
					node, peern, new Message(node), pid);
		}
	}

	// --------------------------------------------------------------------------
	public ProtocolNode() {

	}

	// --------------------------------------------------------------------------
	/*** This is the standard method to define to process incoming messages. */
	public void processEvent(Node node, int pid, Object event) {
		NetworkInfo net_in = NetworkInfo.getInstance();
		Message aem = (Message) event;
		
		
		if (aem.getType() != null && aem.getType() == Message.MessageType.search)
		{
			if (node.getIndex() == aem.getDestID())
			{
				Object[] temp = NetworkInfo.getInstance().getGraph().getNeighbours(node.getIndex()).toArray();
				int vlr_idx = (int) temp[temp.length-1];
//				System.out.println("Node: "+ node.getIndex() +"Connected to vlr_idx: " + vlr_idx);
				
				((Transport) node.getProtocol(FastConfig.getTransport(pid + 1))).send(node,
						Network.get(vlr_idx), new Message(node, aem.sender, Message.MessageType.searchReply), pid + 1);
				NetworkInfo.getInstance().incrementSearch();
//				System.out.println("Dest found on Node:" + node.getIndex()  + ". sending message to vlr:" + vlr_idx);
			}
			return;
		}
		else if (aem.getType() != null && aem.getType() == Message.MessageType.searchReply)
		{
//			System.out.println("Node:"+ aem.sender.getIndex() +" found");
			return;
		}

		
		int newX = (int) aem.getCoor().getX();
		int newY = (int) aem.getCoor().getY();
		int new_vlr_idx = net_in.getTotal_hlr() + net_in.cal_la(newX, newY);
		Coordinates node_coord = (Coordinates)node.getProtocol(net_in.getNode_coord_pid());
		int oldX = (int) node_coord.getX();
		int oldY = (int) node_coord.getY();
		int old_vlr_idx = net_in.getTotal_hlr() + net_in.cal_la(oldX, oldY);
		node_coord.setX(newX);
		node_coord.setY(newY);
		if (net_in.is_in_same_la(oldX, oldY, newX, newY))
		{
			return;
		}
		//else
		Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		Node peern = linkable.getNeighbor(0);
		
//		System.out.println("XXX Node:" + node.getIndex() + "HAS VLR: " + peern.getIndex());
		
		if (peern == null)
			return;
		
		if (peern.getID() == 0)
			return;

		if (aem.sender != null) {
			{
				System.out.println("Node " + node.getID()+ " sends message to its VLR " + peern.getID()
						+ "; pid = " + pid + "; neighbours# = "+ linkable.degree());
			
				
				//sending message to old vlr
				((Transport) aem.sender.getProtocol(FastConfig.getTransport(pid + 1))).send(aem.sender,
						peern, new Message(node, Message.MessageCode.remove), pid + 1);
				
				//sending message to new vlr
				((Transport) aem.sender.getProtocol(FastConfig.getTransport(pid + 1))).send(aem.sender,
						Network.get(new_vlr_idx), new Message(node, Message.MessageCode.add), pid + 1);
				int node_idx = node.getIndex();

				//adding new VLR id as neighbor, remove the old VLR id
				Graph net_graph = net_in.getGraph();
				
				net_graph.setEdge(new_vlr_idx, node_idx);
				net_graph.setEdge(node_idx, new_vlr_idx);
				
			}
			
		}
		
	}

	// ====================== Linkable implementation =====================
	/***
	 * Does not check if the index is out of bound (larger than
	 * {@link #degree()})
	 */
	public Node getNeighbor(int i) {
		return cache[i];
	}

	// --------------------------------------------------------------------
	/** Might be less than cache size. */
	public int degree() {
		int len = cache.length - 1;
		while (len >= 0 && cache[len] == null)
			len--;
		return len + 1;
	}

	// --------------------------------------------------------------------
	public boolean addNeighbor(Node node) {
		int i;
		for (i = 0; i < cache.length && cache[i] != null; i++) {
			if (cache[i] == node)
				return false;
		}
		if (i < cache.length) {
			if (i > 0 && tstamps[i - 1] < CommonState.getIntTime()) {
				// we need to insert to the first position
				for (int j = cache.length - 2; j >= 0; --j) {
					cache[j + 1] = cache[j];
					tstamps[j + 1] = tstamps[j];
				}
				i = 0;
			}
			cache[i] = node;
			tstamps[i] = CommonState.getIntTime();
			return true;
		} else
			throw new IndexOutOfBoundsException();
	}

	// --------------------------------------------------------------------
	public void pack() {
	}

	// --------------------------------------------------------------------
	public boolean contains(Node n) {
		for (int i = 0; i < cache.length; i++) {
			if (cache[i] == n)
				return true;
		}
		return false;
	}

	// --------------------------------------------------------------------
	public void onKill() {
		cache = null;
		tstamps = null;
	}
}
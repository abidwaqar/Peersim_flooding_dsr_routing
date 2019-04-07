package assignment.dsr;

import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import peersim.cdsim.CDProtocol;
import peersim.edsim.EDProtocol;

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
	}

	// --------------------------------------------------------------------------
	public ProtocolNode() {

	}

	// --------------------------------------------------------------------------
	/*** This is the standard method to define to process incoming messages. */
	public void processEvent(Node node, int pid, Object event) {
		
		NetworkInfo net_in = NetworkInfo.getInstance();
		Message aem = (Message) event;
		GeneralNode current_node = (GeneralNode) node;
		int msg_seq_no = aem.msg_seq_no;
		int node_pid = net_in.getInstance().getUnreliable_transport_pid();
		
		if (aem.type == Message.message_type.route_request)
		{
			if(current_node.getID() == aem.dest_id)
			{
//				CommonState.setTime(CommonState.getTime() + 1);
				System.out.println("Route REQ-> Dest " + aem.dest_id + " received msg " + aem.msg_seq_no + " from node " + aem.sender.getID());
				aem.path.add((int)current_node.getID());
				System.out.println("Route REQ-> Node "+node.getID()+" dest Path: " + aem.path);
				
				int message_sender = (int)aem.sender.getID();
				//for route reply
				aem.type = Message.message_type.route_reply;
				aem.dest_id = (int) aem.sender.getID();
				aem.sender = current_node;
				
//				System.out.println("HOLA" + current_node.getIndex());
//				((Transport) node.getProtocol(FastConfig.getTransport(node_pid))).send(current_node, current_node, new Message(aem), node_pid);
				
				if (current_node.sender_msgs.containsKey(message_sender))
				{
					current_node.sender_msgs.get(message_sender).add(aem);
				}
				else
				{
					ArrayList<Message> msgs = new ArrayList<Message>();
					msgs.add(aem);
					current_node.sender_msgs.put(message_sender, msgs);
				}
			}
			else if (current_node.msgs_seq_no.contains(msg_seq_no))
			{
				System.out.println("Route REQ-> Node " + current_node.getID() + " has already sent msg " + msg_seq_no);
			}
			else
			{
//				System.out.println("Node " + node.getID());
				current_node.msgs_seq_no.add(msg_seq_no);
				aem.path.add((int)current_node.getID());
				Collection<Integer> neighbor = net_in.getGraph().getNeighbours((int)node.getID());
				if (aem.sender != null) {
					{
//						System.out.println("Node: " + current_node.getID() + " array size: " + current_node.msgs_seq_no.size());
						Iterator<Integer> itr = neighbor.iterator();
						while(itr.hasNext())
						{
							int node_id = itr.next();
							System.out.println("Route REQ-> Node " + node.getID() + " sending message " + msg_seq_no + " to Node " + node_id);
							((Transport) node.getProtocol(FastConfig.getTransport(node_pid))).send(node, Network.get(node_id), new Message(aem), node_pid);
//							System.out.println("Cycle time: " + CommonState.getTime());
						}
					}
				}
			}
		}
		else if (aem.type == Message.message_type.route_reply)
		{
//			System.out.println("Current Node "+ current_node.getID());
			System.out.println("Route REP-> route reply Path:  " + aem.path);
			
			if(current_node.getID() == aem.dest_id)
			{
				System.out.println("Route REP-> Source Path: " + aem.path);
				int message_sender = (int) aem.sender.getID();
				//for route reply
				aem.type = Message.message_type.data;
				aem.dest_id = (int) aem.sender.getID();
				aem.sender = current_node;
				
//				System.out.println("HOLA" + current_node.getIndex());
//				((Transport) node.getProtocol(FastConfig.getTransport(node_pid))).send(current_node, current_node, new Message(aem), node_pid);
				
				if (current_node.sender_msgs.containsKey(message_sender))
				{
					current_node.sender_msgs.get(message_sender).add(aem);
				}
				else
				{
					ArrayList<Message> msgs = new ArrayList<Message>();
					msgs.add(aem);
					current_node.sender_msgs.put(message_sender, msgs);
				}
			
			}
			else
			{
				//TODO route error
				int next_idx = aem.path.indexOf((int) current_node.getID()) -1;
				System.out.println("Route REP-> Current Node " + current_node.getID() +
						" Sending msg " + aem.msg_seq_no + " to node " + aem.path.get(next_idx));
				((Transport) node.getProtocol(FastConfig.getTransport(node_pid))).send(node, Network.get(aem.path.get(next_idx)), aem, node_pid);
			}
		}
		else if (aem.type == Message.message_type.data)
		{
//			System.out.println("WE ARE IN DATA");
//			System.out.println("Current Node "+ current_node.getID());
//			System.out.println("Path:  " + aem.path);
			if(current_node.getID() == aem.dest_id)
			{
				System.out.println("Data reached destination");
				System.out.println("Source Path: " + aem.path);
				//for route reply
			}
			else
			{
				int next_idx = aem.path.indexOf((int) current_node.getID()) + 1;
				
				if (net_in.getInstance().getGraph().getNeighbours((int)current_node.getID()).contains(aem.path.get(next_idx)))
				{
					System.out.println("DATA, current node:" + current_node.getID() + " sending to node: " + aem.path.get(next_idx));
					((Transport) node.getProtocol(FastConfig.getTransport(node_pid))).send(node, Network.get(aem.path.get(next_idx)), new Message(aem), node_pid);
				}
				else
				{
					//for route reply
					aem.type = Message.message_type.route_error;
					aem.dest_id = (int) aem.sender.getID();
					aem.sender = current_node;
					
//					System.out.println("HOLA" + current_node.getIndex());
					((Transport) node.getProtocol(FastConfig.getTransport(node_pid))).send(current_node, current_node, new Message(aem), node_pid);
				}
				
			}
		}
		else if (aem.type == Message.message_type.route_error)
		{
			if(current_node.getID() == aem.dest_id)
			{
				System.out.println("Route ERR-> Route Error reached destination");
				System.out.println("Source Path: " + aem.path);
//				System.out.println(aem.path.indexOf((int) aem.sender.getID()));
//				System.out.println(aem.path.get(aem.path.indexOf((int )aem.sender.getID()) + 1));
				System.out.println("Route ERR-> Error occured from node: "+ aem.sender.getID() + " to node " + aem.path.get(aem.path.indexOf((int)aem.sender.getID()) + 1));
				//for route reply
				
				// TODO dest
				((Transport) current_node.getProtocol(FastConfig.getTransport(node_pid))).send(
						current_node, current_node, new Message(current_node, aem.path.get(aem.path.size()-1), Message.message_type.route_request), node_pid);
			}
			else
			{
				int next_idx = aem.path.indexOf((int) current_node.getID()) - 1;
				System.out.println("Route ERR-> route error, current node:" + current_node.getID() + " sending to node: " + aem.path.get(next_idx));
				((Transport) node.getProtocol(FastConfig.getTransport(node_pid))).send(node, Network.get(aem.path.get(next_idx)), new Message(aem), node_pid);
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
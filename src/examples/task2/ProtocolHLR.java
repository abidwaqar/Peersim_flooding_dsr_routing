package examples.task2;

import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;

import java.util.HashMap;
import java.util.Map.Entry;

import examples.task2.Message.MessageType;
import peersim.cdsim.CDProtocol;
import peersim.edsim.EDProtocol;

public class ProtocolHLR implements CDProtocol, EDProtocol, Linkable {

	/** Neighbors currently in the cache */
//	private Node[] cache;
	//mobilenode, vlr
	private HashMap<Integer, Integer> cache;
	/** Time stamps currently in the cache */
	private int[] tstamps;

	/** * @param prefix string prefix for config properties */
	public ProtocolHLR(String prefix) {
		cache = new HashMap<Integer, Integer>();
	}

	public Object clone() {
		return this;
	}

	/**
	 * * This is the standard method the define periodic activity. The frequency
	 * of execution of this method is defined by a
	 * {@link peersim.edsim.CDScheduler} component in the configuration.
	 */

	public void nextCycle(Node node, int pid) {
		System.out.println(" nextCycle done ");

		Linkable linkable = (Linkable) node.getProtocol(FastConfig
				.getLinkable(pid));
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
	public ProtocolHLR() {
		cache = new HashMap<Integer, Integer>();
	}

	// --------------------------------------------------------------------------
	/*** This is the standard method to define to process incoming messages. */
	public void processEvent(Node node, int pid, Object event) {
		Message aem = (Message) event;
		Node vlr_node = aem.getSenderNode();
		Node m_node = aem.getMobileNode();
		
		if (aem.getType() != null && aem.getType() == Message.MessageType.search)
		{
			if (aem.getDestID() >= (NetworkInfo.getInstance().getTotal_hlr() + NetworkInfo.getInstance().getTotal_vlr())  )
			{
				if (cache.containsKey(aem.getDestID()))
				{
					int vlr_idx = cache.get(aem.getDestID());
					((Transport) aem.sender.getProtocol(FastConfig.getTransport(pid - 1))).send(node,
							Network.get(vlr_idx), aem, pid - 1);
					System.out.println("Search message sent to vlr:" + vlr_idx);
					NetworkInfo.getInstance().incrementSearch();
				}
				return;
			}
			else 
			{
				System.out.println("Node: " + aem.getDestID() + " is a VLRNode");
				return;
			}
		}
		else if (aem.getType() != null && aem.getType() == Message.MessageType.searchReply)
		{
			((Transport) node.getProtocol(FastConfig.getTransport(pid - 2))).send(node,
					aem.destNode, aem, pid - 2);
			System.out.println("Send Search reply from hlr to src_node:" + aem.destNode.getIndex());
			return;
		}

		
		if (aem.sender != null) {
			System.out.println("      HLR " + node.getID()
					+ " receives massage from " + aem.sender.getID()
					+ "; pid = " + pid);
			//Updating the HLR location database
			cache.put(m_node.getIndex(), vlr_node.getIndex());
			NetworkInfo.getInstance().incrementUpdate();
//			System.out.println("VLR: " + vlr_node.getIndex() + " updated in hlr." );
//			System.out.println(cache.values());
//			System.out.println();
//			System.out.println();
//			for (Entry<Integer, Integer> entry : cache.entrySet()) {
//			    System.out.print(entry.getKey() + ":" + entry.getValue().toString() + "  ");
//			}
			
		}
//		System.out.println("HELLSNIUFUGSIBOSIU");
	}

	// ====================== Linkable implementation =====================
	/***
	 * Does not check if the index is out of bound (larger than
	 * {@link #degree()})
	 */
	public Node getNeighbor(int key) {
		return (Node) NetworkInfo.getInstance().getGraph().getNode(key);
	}

	// --------------------------------------------------------------------
	/** Might be less than cache size. */
	public int degree() {
		return cache.size();
	}

	// --------------------------------------------------------------------
	public boolean addNeighbor(Node node) {
		//TODO check this
		return true;
//		int i;
//		for (i = 0; i < cache.length && cache[i] != null; i++) {
//			if (cache[i] == node)
//				return false;
//		}
//		if (i < cache.length) {
//			if (i > 0 && tstamps[i - 1] < CommonState.getIntTime()) {
//				// we need to insert to the first position
//				for (int j = cache.length - 2; j >= 0; --j) {
//					cache[j + 1] = cache[j];
//					tstamps[j + 1] = tstamps[j];
//				}
//				i = 0;
//			}
//			cache[i] = node;
//			tstamps[i] = CommonState.getIntTime();
//			return true;
//		} else
//			throw new IndexOutOfBoundsException();
	}

	// --------------------------------------------------------------------
	public void pack() {
	}

	// --------------------------------------------------------------------
	public boolean contains(Node n) {
		if(cache.containsKey(n.getIndex()))
			return true;
		return false;
	}

	// --------------------------------------------------------------------
	public void onKill() {
		cache.clear();
		tstamps = null;
	}
}
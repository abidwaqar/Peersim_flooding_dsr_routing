package examples.task2;

import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;

import java.util.ArrayList;
import java.util.HashMap;
import examples.task2.Message.MessageCode;
import peersim.cdsim.CDProtocol;
import peersim.edsim.EDProtocol;
import peersim.edsim.Network_Info;


public class ProtocolVLR implements CDProtocol, EDProtocol, Linkable {

	/** Neighbors currently in the cache */
//	private Node[] cache;
	public ArrayList<ArrayList<Node>> cache;		//zero miss hai
	int cacheS = 0;
	
	/** Time stamps currently in the cache */
	private int[] tstamps;

	/** * @param prefix string prefix for config properties */
	public ProtocolVLR(String prefix) {
		cacheS = NetworkInfo.getInstance().getTotal_hlr() + NetworkInfo.getInstance().getTotal_vlr();
		cache = new ArrayList<ArrayList<Node>>();
		for (int i = 0;i< cacheS;++i)
		{
			cache.add(new ArrayList<Node>());
		}
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
	public ProtocolVLR() {
		cacheS = NetworkInfo.getInstance().getTotal_hlr() + NetworkInfo.getInstance().getTotal_vlr();
		cache = new ArrayList<ArrayList<Node>>();
		for (int i = 0;i< cacheS;++i)
		{
			cache.add(new ArrayList<Node>());
		}
	}

	// --------------------------------------------------------------------------
	/*** This is the standard method to define to process incoming messages. */
	public void processEvent(Node vlr_node, int pid, Object event) {
//		System.out.println("cache size: " + cache.size()+ " vlr index: " + vlr_node.getIndex());
		ArrayList<Node> realCache = cache.get(vlr_node.getIndex());
		
		Message aem = (Message) event;
		Linkable linkable = (Linkable) vlr_node.getProtocol(FastConfig.getLinkable(pid));
		Node peern = linkable.getNeighbor(0);
		Node m_node = aem.getSenderNode();
		
		
//		System.out.println("IM VLRNODE: " + vlr_node.getIndex() + " AND THIS IS MY CACHE");
//		for (int i = 0;i<realCache.size();++i)
//		{
//			System.out.print(realCache.get(i).getIndex() + " ");
//		}
//		System.out.println();
		
		
		if (aem.getType() != null && aem.getType() == Message.MessageType.search)
		{
			for (int i = 0; i < realCache.size();++i)
			{
				((Transport) vlr_node.getProtocol(FastConfig.getTransport(pid-1))).send(
						vlr_node, Network.get(realCache.get(i).getIndex()), aem, pid-1);	
				NetworkInfo.getInstance().incrementSearch();
//				System.out.println("Search message sent node:" + realCache.get(i).getIndex());
			}
			System.out.println("Paging in vlr:" + vlr_node.getIndex());
			return;
		}
		else if (aem.getType() != null && aem.getType() == Message.MessageType.searchReply)
		{
			((Transport) vlr_node.getProtocol(FastConfig.getTransport(pid+1))).send(
					vlr_node, Network.get(0), aem, pid+1);	
			System.out.println("Send Search reply from vlr:" + vlr_node.getIndex() + "  to hlr");
			NetworkInfo.getInstance().incrementSearch();
			return;
		}

		
		if (aem.operation == Message.MessageCode.remove)
		{
			NetworkInfo.getInstance().incrementUpdate();
			if (realCache.contains(m_node))
			{
//				System.out.println("Node: " + m_node.getIndex() + " removing VLR: " + vlr_node.getIndex());
				realCache.remove(m_node);
			}
//				System.out.println("Node: " + m_node.getIndex() + " not in VLR: " + vlr_node.getIndex());
		}
		else if (aem.operation == Message.MessageCode.add)
		{
			NetworkInfo.getInstance().incrementUpdate();
			if (realCache.contains(m_node)) {
//				System.out.println("Node: " + m_node.getIndex() +" already contained in VLR: " + vlr_node.getIndex());
				return;
			}
			else 
			{
//				System.out.println("Node: " + m_node.getIndex() +" adding in VLR: " + vlr_node.getIndex());
				realCache.add(m_node);
			}
				
			if (aem.sender != null) {
				System.out.println("   VLR " + vlr_node.getID()
						+ " sends massage to HLR " + peern.getID() + "; pid = "+ pid);
				((Transport) aem.sender.getProtocol(FastConfig
						.getTransport(pid + 1))).send(aem.sender, peern,
						new Message(vlr_node, m_node), pid + 1);
//				System.out.println(peern.getIndex() + "hifygadv");

			}
		
		}
		
	}

	// ====================== Linkable implementation =====================
	/***
	 * Does not check if the index is out of bound (larger than
	 * {@link #degree()})
	 */
	public Node getNeighbor(int i) {
		return Network.get(0);
	}

	// --------------------------------------------------------------------
	/** Might be less than cache size. */
	public int degree() {
		return cache.size();
	}

	// --------------------------------------------------------------------
	public boolean addNeighbor(Node node) {
		if (cache.contains(node))
		{
			return true;
		}
//		cache.add(0, node);
		return false;
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
		return cache.contains(n); 
	}

	// --------------------------------------------------------------------
	public void onKill() {
		cache.clear();
		tstamps = null;
	}
}
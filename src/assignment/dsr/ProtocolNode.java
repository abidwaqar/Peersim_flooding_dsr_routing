package examples.task2;

import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;
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
		Message aem = (Message) event;

		Linkable linkable = (Linkable) node.getProtocol(FastConfig
				.getLinkable(pid));

		Node peern = linkable.getNeighbor(0);
		if (peern == null)
			return;
		if (peern.getID() == 0)
			return;

		if (aem.sender != null) {
			{
				System.out.println("Node " + node.getID()
						+ " sends message to its VLR " + peern.getID()
						+ "; pid = " + pid + "; neighbours# = "
						+ linkable.degree());
				((Transport) aem.sender.getProtocol(FastConfig
						.getTransport(pid + 1))).send(aem.sender, peern,
						new Message(node), pid + 1);
				// TODO "pid+1" could be done more graceful...
			}
			// TODO: add new VLR id as neighbor, remove the old VLR id
			// (if the area of responsibility has changed)
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
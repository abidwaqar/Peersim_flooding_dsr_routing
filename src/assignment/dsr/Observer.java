package assignment.dsr;

import examples.task2.Message;
import peersim.config.FastConfig;
import peersim.core.Network;
import peersim.core.Node;
import peersim.reports.GraphObserver;
import peersim.transport.Transport;

public class Observer extends GraphObserver {

	public Observer(String prefix) {
		super(prefix);
	}

	public boolean execute() {
		
		//TODO Read sender and dest from file 
		
		int senderIdx = 1;
		int destIdx = 2;
		
		NetworkInfo net_in = NetworkInfo.getInstance();
		
		Node sender_node = Network.get(senderIdx);
		
		((Transport) sender_node.getProtocol(FastConfig.getTransport(net_in.getUnreliable_transport_pid()))).send(
				sender_node, peern, new Message(node), net_in.getUnreliable_transport_pid());
		
		// TODO: note that "4" is a constant here! this can be improved
		for (int i = 2; i < Network.size(); i++) {
			Node current = (Node) Network.get(i);
			((Transport) current.getProtocol(FastConfig.getTransport(4))).send(
					current, current, new Message(current), 4);
			// System.out.println("Observer execute; i = "+i);

			// TODO: check location change here (coordinates) and perform
			// location update

			// TODO: search queries also can be implemented here
		}

		return false;
	}

}

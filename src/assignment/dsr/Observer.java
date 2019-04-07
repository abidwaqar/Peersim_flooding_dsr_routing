package assignment.dsr;

import assignment.dsr.Message;
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
				
		NetworkInfo net_in = NetworkInfo.getInstance();
		Node current_node = Network.get(0);
		((Transport) current_node.getProtocol(FastConfig.getTransport(net_in.getUnreliable_transport_pid()))).send(
				current_node, current_node, new Message(current_node, 4, Message.message_type.route_request), net_in.getUnreliable_transport_pid());

		
	//TODO remove from sender msgs
		return false;
	}

}

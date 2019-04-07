package assignment.dsr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import assignment.dsr.Message;
import peersim.config.FastConfig;
import peersim.core.GeneralNode;
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
		
		//send msgs to sender 
		int net_size = Network.size();
		for (int i = 0; i < net_size; ++i)
		{
			GeneralNode current_node = (GeneralNode) Network.get(i);
			HashMap<Integer, ArrayList<Message>> curr_node_sender_msgs = current_node.sender_msgs;
			for(Map.Entry<Integer, ArrayList<Message>> entry: curr_node_sender_msgs.entrySet()) 
			{
				int min_size = 9999;
				int min_idx = 0;
				System.out.println("key, paths");
			    System.out.print(entry.getKey() + " : ");
			    for (int j = 0; j < entry.getValue().size(); ++j)
			    {
			    	if (entry.getValue().get(j).path.size() < min_size)
			    	{
			    		min_size = entry.getValue().get(j).path.size();
			    		min_idx = j;
			    	}
			    	System.out.print(entry.getValue().get(j).path);
			    }
			    System.out.println();
			}
			
		}
		
		
		Node current_node = Network.get(0);
		((Transport) current_node.getProtocol(FastConfig.getTransport(net_in.getUnreliable_transport_pid()))).send(
				current_node, current_node, new Message(current_node, 4, Message.message_type.route_request), net_in.getUnreliable_transport_pid());

		
		//TODO remove from sender msgs
		return false;
	}

}

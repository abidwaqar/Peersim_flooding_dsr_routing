package assignment.dsr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import assignment.dsr.Message;
import peersim.config.FastConfig;
import peersim.core.CommonState;
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
		int unreliable_protocol_pid = net_in.getUnreliable_transport_pid();
		
		//send msgs to sender 
		int net_size = Network.size();
		for (int i = 0; i < net_size; ++i)
		{
			GeneralNode current_node = (GeneralNode) Network.get(i);
			HashMap<Integer, ArrayList<Message>> curr_node_sender_msgs = current_node.sender_msgs;
			for(Map.Entry<Integer, ArrayList<Message>> entry: curr_node_sender_msgs.entrySet()) 
			{
				int min_size = entry.getValue().get(0).path.size();
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
			    System.out.println("Min path is " + entry.getValue().get(min_idx).path.size());
			    ((Transport) current_node.getProtocol(FastConfig.getTransport(unreliable_protocol_pid)))
				.send(current_node, current_node, new Message(entry.getValue().get(min_idx)), unreliable_protocol_pid);
				//msg send so removing entry
//				current_node.sender_msgs.remove(entry.getKey());
			}
			current_node.sender_msgs = new HashMap<Integer, ArrayList<Message>>();
		}
		
		//node 0 is sending message to node 4
		if(CommonState.getTime() == 0)
		{
		Node current_node = Network.get(0);
		((Transport) current_node.getProtocol(FastConfig.getTransport(net_in.getUnreliable_transport_pid()))).send(
				current_node, current_node, new Message(current_node, 4, Message.message_type.route_request), net_in.getUnreliable_transport_pid());

		}
		
//		//random node is sending route request to random node
//		int network_size = net_in.getNetwork_size();
//		GeneralNode current_node;
//		int rand_i = 0;
//		int rand_j = 0;
//		
//		System.out.println(net_in.getSending_rate());
//		for (int i = 0; i < net_in.getSending_rate(); ++i)
//		{
//			Random rand = new Random();
//			rand_i = rand.nextInt(network_size);
//			rand_j = rand.nextInt(network_size);
//			current_node = (GeneralNode) Network.get(rand_i);
//			((Transport) current_node.getProtocol(FastConfig.getTransport(net_in.getUnreliable_transport_pid()))).send(
//					current_node, current_node, new Message(current_node, rand_j, Message.message_type.route_request), net_in.getUnreliable_transport_pid());
//		}
		
		return false;
	}

}

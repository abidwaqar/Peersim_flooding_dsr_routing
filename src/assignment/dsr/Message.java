package assignment.dsr;

import java.util.ArrayList;
import java.util.Enumeration;

import peersim.core.Node;

/**
 * The type of a message. It contains the sender node
 * of type {@link peersim.core.Node}.
 */
public class Message {
	
	public enum message_type{
		route_request, route_reply, route_error, data
	}

	/**
	 * If not null, this has to be answered, otherwise this is the answer.
	 */
	public Node sender;
	private static int class_seq_no = 0;
	public int msg_seq_no;
	public int dest_id;
	public message_type type;
	public ArrayList<Integer> path;

	public Message(Node sender, int dest_ID, message_type type) {
		this.sender = sender;
		this.dest_id= dest_ID;
		this.type = type;
		this.path = new ArrayList<>();
		msg_seq_no = class_seq_no;
		++class_seq_no;	
	}
	
	public Message(Message msg)
	{
		this.sender = msg.sender;
		this.dest_id = msg.dest_id;
		this.type = msg.type;
		this.msg_seq_no = msg.msg_seq_no;
		this.path = new ArrayList<>(msg.path);
	}
}

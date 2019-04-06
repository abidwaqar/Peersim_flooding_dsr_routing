package assignment.flooding;

import java.util.Enumeration;

import peersim.core.Node;

/**
 * The type of a message. It contains the sender node
 * of type {@link peersim.core.Node}.
 */
class Message {
	
	public enum message_type{
		route_request, route_reply, route_err, data
	}

	/**
	 * If not null, this has to be answered, otherwise this is the answer.
	 */
	final Node sender;
	private static int class_seq_no = 0;
	public int msg_seq_no;
	public int dest_id;
	public message_type type;

	public Message(Node sender, int dest_ID) {
		this.sender = sender;
		this.dest_id= dest_ID;
//		this.type = type;
		msg_seq_no = class_seq_no;
		++class_seq_no;
		
	}
}

package examples.task2;

import peersim.core.Node;

/**
 * The type of a message. It contains the sender node
 * of type {@link peersim.core.Node}.
 */
public class Message {

	/**
	 * If not null, this has to be answered, otherwise this is the answer.
	 */
	
	public enum MessageCode 
	{
		nothing, remove, add 
	}
	
	public enum MessageType
	{
		search, update, searchReply
	}
	
	final Node sender;
	private Coordinates coor;
	public MessageCode operation;
	private Node mobile_node = null;
	private int destID;
	public Node destNode;
	private MessageType type = null;
	
	
	//TODO theek karni hai
	public Message(Node sender) {
		this.sender = sender;
		coor = new Coordinates(null);
		operation = MessageCode.nothing;
	}

	public Message(Node sender, int x, int y) {
		coor = new Coordinates(null);
		coor.setX(x);
		coor.setY(y);
		this.sender = sender;
	}
	
	public Message(Node sender, MessageCode operation) {
		coor = new Coordinates(null);
		this.sender = sender;
		this.operation = operation;
	}
	
	public int getDestID() {
		return destID;
	}

	public MessageType getType() {
		return type;
	}
	
	public Message(Node sender, Node m_node) {
		this.sender = sender;
		mobile_node = m_node;
	}
	
	public Message(Node sender, int dest, MessageType type) {
		this.sender = sender;
		destID = dest;
		this.type = type;
	}
	
	public Message(Node sender, Node dest, MessageType type) {
		this.sender = sender;
		destNode = dest;
		this.type = type;
	}
	
	public Coordinates getCoor() {
		return coor;
	}
	
	public Node getSenderNode()
	{
		return sender;
	}
	
	public Node getMobileNode()
	{
		return mobile_node;
	}
}

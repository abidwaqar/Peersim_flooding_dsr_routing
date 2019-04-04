package examples.test;

import peersim.core.Node;

/**
* The type of a message. It contains a value of type double and the
* sender node of type {@link peersim.core.Node}.
*/
class Message {

	/** If not null,
	this has to be answered, otherwise this is the answer. */
	final Node sender;
	public Message(Node sender )
	{
		this.sender = sender;
	}
}


package com.simplelife.renhai.server.business.device;

import com.simplelife.renhai.server.util.IDeviceWrapper;

public class PingLink
{
	private PingNode head;
	private PingNode tail;
	private volatile int count;
	
	/**
	 * @return the head
	 */
	public PingNode getHead()
	{
		return head;
	}
	/**
	 * @return the tail
	 */
	public PingNode getTail()
	{
		return tail;
	}
	
	public void append(IDeviceWrapper deviceWrapper)
	{
		PingNode node = new PingNode(deviceWrapper);
		addToTail(node);
		if (head == null)
		{
			addToHead(node);
		}
		count++;
	}
	
	public void deleteNode(PingNode node)
	{
		if (node == null)
		{
			return;
		}
		
		PingNode tmpNode = node.getPrevNode();
		if (tmpNode != null)
		{
			tmpNode.setNextNode(node.getNextNode());
		}
		
		tmpNode = node.getNextNode();
		if (tmpNode != null)
		{
			tmpNode.setPrevNode(node.getPrevNode());
		}
	}
	
	public void moveToTail(PingNode node)
	{
		deleteNode(node);
		addToTail(node);
	}
	
	private void addToHead(PingNode node)
	{
		node.setNextNode(head);
		head = node;
		node.setPrevNode(null);
	}
	
	private void addToTail(PingNode node)
	{
		node.setPrevNode(tail);
		tail = node;
		node.setNextNode(null);
	}
}

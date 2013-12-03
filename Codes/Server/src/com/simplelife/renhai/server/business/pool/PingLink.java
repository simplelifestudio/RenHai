package com.simplelife.renhai.server.business.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.business.device.PingNode;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.IDeviceWrapper;

public class PingLink
{
	public final static PingLink instance = new PingLink();
	
	private PingNode head;
	private PingNode tail;
	private volatile int count;
	private Logger logger = LoggerFactory.getLogger("Ping");
	
	
	private PingLink()
	{
		
	}
	
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
	
	public int size()
	{
		return count;
	}
	
	public void append(IDeviceWrapper deviceWrapper)
	{
		PingNode node = deviceWrapper.getPingNode();
		addToTail(node);
		if (head == null)
		{
			addToHead(node);
		}
		count++;
	}
	
	
	public void checkInactivity()
	{
		logger.debug("Start to check inactivity of Ping");
		int count = 0;
		PingNode node = head;
		PingNode nextNode = null;
		IDeviceWrapper deviceWrapper;
		while(node != null && node.isPingTimeout())
		{
			deviceWrapper = node.getDeviceWrapper();
			deviceWrapper.changeBusinessStatus(DeviceStatus.Disconnected, StatusChangeReason.TimeoutOfPing);
			nextNode = node.getNextNode();
			this.removeNode(node);
			node = nextNode;
			count++;
		}
		
		if (count > 0)
		{
			logger.debug("Check inactivity of Ping finished, removed {} devices", count);
		}
	}
	
	private void removeNode(PingNode node, boolean printLog)
	{
		if (node == null)
		{
			return;
		}
		
		if (printLog)
		{
			logger.debug("Remove Ping node of device <{}>", node.getDeviceWrapper().getDeviceSn());
		}
		
		PingNode preNode = node.getPrevNode();
		PingNode nextNode = node.getNextNode();
		
		if (preNode == null && nextNode == null & head != node)
		{
			// node is not in the link
			return;
		}
		
		if (preNode != null)
		{
			preNode.setNextNode(node.getNextNode());
		}
		else
		{
			head = node.getNextNode();
		}
		
		if (nextNode != null)
		{
			nextNode.setPrevNode(node.getPrevNode());
		}
		else
		{
			tail = node.getPrevNode();
		}
		node.setNextNode(null);
		node.setPrevNode(null);
		count--;
	}
	public void removeNode(PingNode node)
	{
		removeNode(node, true);
	}
	
	public void moveToTail(PingNode node)
	{
		logger.debug("Move Ping node of device <{}> to tail", node.getDeviceWrapper().getDeviceSn());
		removeNode(node, false);
		addToTail(node);
	}
	
	private void addToHead(PingNode node)
	{
		if (head != null)
		{
			head.setPrevNode(node);
		}
		node.setNextNode(head);

		node.setPrevNode(null);
		head = node;
	}
	
	private void addToTail(PingNode node)
	{
		if (tail != null)
		{
			tail.setNextNode(node);
		}
		
		node.setPrevNode(tail);
		
		node.setNextNode(null);
		tail = node;
	}
}

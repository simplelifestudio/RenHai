package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.atomic.AtomicInteger;

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
	private AtomicInteger linkSize = new AtomicInteger(0);
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
		return linkSize.get();
	}
	
	public void append(IDeviceWrapper deviceWrapper)
	{
		PingNode node = deviceWrapper.getPingNode();
		addToTail(node);
	}
	
	
	public void checkInactivity()
	{
		if (head == null)
		{
			return;
		}
		
		long duration = System.currentTimeMillis() - head.getLastPingTime();
		logger.debug("Start to check inactivity of Ping, link size: {}, duration of first node: " + duration, linkSize.get());
		
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
			logger.debug("Remove Ping node of device <{}>", node.getDeviceWrapper().getDeviceIdentification());
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
			preNode.setNextNode(nextNode);
		}
		else
		{
			head = nextNode;
		}
		
		if (nextNode != null)
		{
			nextNode.setPrevNode(preNode);
		}
		else
		{
			tail = preNode;
		}
		node.setNextNode(null);
		node.setPrevNode(null);
		linkSize.decrementAndGet();
	}
	public void removeNode(PingNode node)
	{
		//logger.debug("Remove Ping node of device <{}>", node.getDeviceWrapper().getDeviceIdentification());
		removeNode(node, true);
	}
	
	public void moveToTail(PingNode node)
	{
		logger.debug("Move Ping node of device <{}> to tail", node.getDeviceWrapper().getDeviceIdentification());
		if (tail == node)
		{
			return;
		}
		
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
		
		if (head == null)
		{
			addToHead(node);
		}
		
		linkSize.incrementAndGet();
	}
}

package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.business.device.AbstractTimeoutNode;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.IDeviceWrapper;

public class TimeoutLink
{
	private AbstractTimeoutNode head;
	private AbstractTimeoutNode tail;
	private AtomicInteger linkSize = new AtomicInteger(0);
	private Logger logger = LoggerFactory.getLogger("Ping");
	
	public TimeoutLink()
	{
		
	}
	
	/**
	 * @return the head
	 */
	public AbstractTimeoutNode getHead()
	{
		return head;
	}
	/**
	 * @return the tail
	 */
	public AbstractTimeoutNode getTail()
	{
		return tail;
	}
	
	public int size()
	{
		return linkSize.get();
	}
	
	public void append(AbstractTimeoutNode node)
	{
		addToTail(node);
	}
	
	
	public void checkTimeout()
	{
		if (head == null)
		{
			linkSize.set(0);
			return;
		}
		
		long duration = System.currentTimeMillis() - head.getLastTime();
		logger.debug("Start to check timeout nodes, link size: {}, last update of first node: " + duration, linkSize.get());
		
		int count = 0;
		AbstractTimeoutNode node = head;
		AbstractTimeoutNode nextNode = null;
		while(node != null && node.isTimeout())
		{
			node.onTimeout();
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
	
	private void removeNode(AbstractTimeoutNode node, boolean printLog)
	{
		if (node == null)
		{
			return;
		}
		
		AbstractTimeoutNode preNode = node.getPrevNode();
		AbstractTimeoutNode nextNode = node.getNextNode();
		
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
	public void removeNode(AbstractTimeoutNode node)
	{
		//logger.debug("Remove Ping node of device <{}>", node.getDeviceWrapper().getDeviceIdentification());
		removeNode(node, true);
	}
	
	public void moveToTail(AbstractTimeoutNode node)
	{
		if (tail == node)
		{
			return;
		}
		
		removeNode(node, false);
		addToTail(node);
	}
	
	private void addToHead(AbstractTimeoutNode node)
	{
		if (head != null)
		{
			head.setPrevNode(node);
		}
		node.setNextNode(head);
		node.setPrevNode(null);
		head = node;
	}
	
	private void addToTail(AbstractTimeoutNode node)
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

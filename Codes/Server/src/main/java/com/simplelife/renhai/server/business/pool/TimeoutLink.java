package com.simplelife.renhai.server.business.pool;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.business.device.AbstractTimeoutNode;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts.TimeoutActionType;
import com.simplelife.renhai.server.util.IProductor;
import com.simplelife.renhai.server.util.Worker;

public class TimeoutLink
{
	private AbstractTimeoutNode head;
	private AbstractTimeoutNode tail;
	private AtomicInteger linkSize = new AtomicInteger(0);
	private Logger logger = LoggerFactory.getLogger("Ping");
	private TimeoutActionQueue actionQueue = new TimeoutActionQueue();
	private Timer timeoutCheckTimer = new Timer();
	private int timeoutCheckInterval;
	
	public TimeoutLink(int timeoutCheckInterval)
	{
		this.timeoutCheckInterval = timeoutCheckInterval;
	}
	
	public void startService()
	{
		timeoutCheckTimer.scheduleAtFixedRate(new TimeoutCheckTask(), timeoutCheckInterval, timeoutCheckInterval);
		actionQueue.startService();
	}
	
	public void stopService()
	{
		timeoutCheckTimer.cancel();
		actionQueue.stopService();
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
		actionQueue.newAction(TimeoutActionType.AppendToTail, node);
	}
	
	
	public void executeCheckTimeout()
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
	
	private void executeRemoveNode(AbstractTimeoutNode node)
	{
		removeNode(node, true);
	}
	
	public void removeNode(AbstractTimeoutNode node)
	{
		actionQueue.newAction(TimeoutActionType.RemoveNode, node);
	}
	
	public void checkTimeout()
	{
		actionQueue.newAction(TimeoutActionType.CheckTimeout, null);
	}
	
	public void moveToTail(AbstractTimeoutNode node)
	{
		if (node == null)
		{
			return;
		}
		
		actionQueue.newAction(TimeoutActionType.MoveToTail, node);
	}
	
	private void executeMoveToTail(AbstractTimeoutNode node)
	{
		if (node == null)
		{
			return;
		}
		
		if (tail == node)
		{
			return;
		}
		
		removeNode(node, false);
		executeAddToTail(node);
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
	
	private void executeAddToTail(AbstractTimeoutNode node)
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
	
	private class TimeoutAction implements Runnable
	{
		private TimeoutActionType actionType;
		private AbstractTimeoutNode node;
		
		public TimeoutAction(TimeoutActionType actionType, AbstractTimeoutNode node)
		{
			this.actionType = actionType;
			this.node = node;
		}
		
		@Override
		public void run()
		{
			try
			{
				switch(actionType)
				{
					case Invalid:
						break;
					case MoveToTail:
						executeMoveToTail(node);
						break;
					case CheckTimeout:
						executeCheckTimeout();
						break;
					case RemoveNode:
						executeRemoveNode(node);
						break;
					case AppendToTail:
						executeAddToTail(node);
						break;
				}
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	
	private class TimeoutCheckTask extends TimerTask
	{
		@Override
		public void run()
		{
			Thread.currentThread().setName("TimeoutCheckTask");
			try
			{
				checkTimeout();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
		
	}
	
	private class TimeoutActionQueue implements IProductor
	{
		//public final static PingActionQueue instance = new PingActionQueue();
		private ConcurrentLinkedQueue<TimeoutAction> queue = new ConcurrentLinkedQueue<>();
		private Worker worker = new Worker(this);
		
		public TimeoutActionQueue()
		{
			worker.setName("Ping");
		}
		
		public void newAction(TimeoutActionType actionType, AbstractTimeoutNode node)
		{
			TimeoutAction action = new TimeoutAction(actionType, node);
			queue.add(action);
			worker.resumeExecution();
		}
		
		public void startService()
		{
			worker.startExecution();
		}
		
		public void stopService()
		{
			worker.stopExecution();
		}
			
		

		@Override
		public boolean hasWork()
		{
			return !queue.isEmpty();
		}
		
		@Override
		public Runnable getWork()
		{
			return queue.remove();
		}
	}
}

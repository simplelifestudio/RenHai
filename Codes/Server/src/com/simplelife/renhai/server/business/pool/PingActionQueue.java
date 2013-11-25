/**
 * PingActionQueue.java
 * 
 * History:
 *     2013-11-24: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.simplelife.renhai.server.business.device.PingNode;
import com.simplelife.renhai.server.util.Consts.PingActionType;
import com.simplelife.renhai.server.util.IProductor;
import com.simplelife.renhai.server.util.Worker;

/**
 * 
 */
public class PingActionQueue implements IProductor
{
	public final static PingActionQueue instance = new PingActionQueue();
	private ConcurrentLinkedQueue<PingAction> queue = new ConcurrentLinkedQueue<>();
	private Worker worker = new Worker(this);
	
	private PingActionQueue()
	{
		
	}
	public void newAction(PingActionType actionType, PingNode node)
	{
		PingAction action = new PingAction(actionType, node);
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
		
	private class PingAction implements Runnable
	{
		private PingActionType actionType;
		private PingNode node;
		
		public PingAction(PingActionType actionType, PingNode node)
		{
			this.actionType = actionType;
			this.node = node;
		}
		
		@Override
		public void run()
		{
			switch(actionType)
			{
				case Invalid:
					break;
				case OnPing:
					PingLink.instance.moveToTail(node);
					break;
				case CheckInactivity:
					PingLink.instance.checkInactivity();
					break;
				case DeviceRemoved:
					PingLink.instance.removeNode(node);
					break;
			}
		}
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

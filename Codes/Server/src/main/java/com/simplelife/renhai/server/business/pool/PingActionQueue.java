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

import com.simplelife.renhai.server.business.device.AbstractTimeoutNode;
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
		worker.setName("Ping");
	}
	public void newAction(PingActionType actionType, AbstractTimeoutNode node)
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
		private AbstractTimeoutNode node;
		
		public PingAction(PingActionType actionType, AbstractTimeoutNode node)
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
					OnlineDevicePool.pingLink.moveToTail(node);
					break;
				case CheckInactivity:
					OnlineDevicePool.pingLink.checkTimeout();
					break;
				case DeviceRemoved:
					OnlineDevicePool.pingLink.removeNode(node);
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

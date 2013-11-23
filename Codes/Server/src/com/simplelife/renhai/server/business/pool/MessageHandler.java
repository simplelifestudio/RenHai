/**
 * MessageHandler.java
 * 
 * History:
 *     2013-11-8: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.json.AbstractJSONMessage;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.Consts.MessageId;

/**
 * 
 */
public class MessageHandler implements Runnable
{
	private IDeviceWrapper deviceWrapper;
	private Logger logger = BusinessModule.instance.getLogger();
	private AbstractMsgExecutorPool executor;
	
	private boolean executeFlag = false;
	private Lock executeLock = new ReentrantLock();
	
	private ConcurrentLinkedQueue<AbstractJSONMessage> queue = new ConcurrentLinkedQueue<>();
	private Lock queueLock = new ReentrantLock();
	
	
	public MessageHandler(IDeviceWrapper deviceWrapper, AbstractMsgExecutorPool executor)
	{
		this.deviceWrapper = deviceWrapper;
		this.executor = executor;
	}
	
	public void clearMessage()
	{
		logger.debug("{} messages in queue of Deivce <"+ deviceWrapper.getDeviceSn() +"> is released.", queue.size());
		queueLock.lock();
		queue.clear();
		queueLock.unlock();
	}
	
	public void addMessage(AbstractJSONMessage message)
	{
		if (message == null)
		{
			return;
		}
		
		message.setQueueTime(System.currentTimeMillis());
		logger.debug("Queue message " + message.getMessageId().name() 
				+ " with MessageSN: " + message.getMessageSn());
		
		if (message.getMessageId() == MessageId.AppDataSyncRequest)
		{
			// Put AppDataSyncRequest in higher priority
			InputMsgExecutorPool.instance.execute(message);
			return;
		}
		
		queueLock.lock();
		queue.add(message);
		
		executeLock.lock();
		if (!executeFlag)
		{
			executeFlag = true;
			executor.execute(this);
		}
		executeLock.unlock();
		queueLock.unlock();
	}

	@Override
	public void run()
	{
		if (queue.isEmpty())
		{
			return;
		}
		
		logger.debug("Message handler of device <{}> started", deviceWrapper.getDeviceSn());
		queueLock.lock();
		
		AbstractJSONMessage message;
		do
		{
			message = queue.remove();
			queueLock.unlock();
			executeMessage(message);
			queueLock.lock();
		}while(!queue.isEmpty());
		
		executeLock.lock();
		executeFlag = false;
		executeLock.unlock();
		queueLock.unlock();
		
		logger.debug("Message handler of device <{}> ended", deviceWrapper.getDeviceSn());
	}
	
	private void executeMessage(AbstractJSONMessage message)
	{
		int duration = message.getQueueDuration();
		String temp = "Start to handle " + message.getMessageId().name() + " related to device <"
				+ message.getDeviceWrapper().getDeviceSn() + "> which was queued " + duration
				+ "ms ago, message Sn: " + message.getMessageSn();
		logger.debug(temp);
		
		int delay = message.getDelayOfHandle();
		if (delay > 0)
		{
			if (duration < delay)
			{
				// To ensure message is delayed for given time
				// Currently it's designed for delay of SessionBound
				delay = delay - duration;
				logger.debug("Delay " + delay + "ms for handle of " + message.getMessageId().name(), message
						.getDeviceWrapper().getDeviceSn());
				try
				{
					Thread.sleep(delay - duration);
				}
				catch (InterruptedException e)
				{
					FileLogger.printStackTrace(e);
				}
			}
		}
		message.run();
	}
}

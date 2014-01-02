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
import com.simplelife.renhai.server.util.IRunnableMessage;

/**
 * 
 */
public class MessageHandler implements Runnable
{
	//private IDeviceWrapper deviceWrapper;
	private String msgOwnerInfo;
	private Logger logger = BusinessModule.instance.getLogger();
	private AbstractMsgExecutorPool executor;
	
	private boolean executeFlag = false;
	private Lock executeLock = new ReentrantLock();
	
	private ConcurrentLinkedQueue<IRunnableMessage> messageQueue = new ConcurrentLinkedQueue<>();
	private Lock queueLock = new ReentrantLock();
	
	private boolean syncPauseFlag = false;
	
	public MessageHandler(String msgOwnerInfo, AbstractMsgExecutorPool executorPool)
	{
		this.msgOwnerInfo = msgOwnerInfo;
		this.executor = executorPool;
	}

	public void setMsgOwnerInfo(String msgOwnerInfo)
	{
		this.msgOwnerInfo = msgOwnerInfo;
	}
	
	public void clearMessage()
	{
		if (messageQueue.isEmpty())
		{
			return;
		}
		
		logger.debug("{} messages in queue of Deivce <"+ msgOwnerInfo +"> is released.", messageQueue.size());
		queueLock.lock();
		messageQueue.clear();
		queueLock.unlock();
	}
	
	public void syncResponseReceived(String deviceSn)
	{
		syncPauseFlag = false;
		
		//logger.debug("==================1 before lock in syncResponseReceived() of device <{}>", deviceSn);
		queueLock.lock();
		//logger.debug("==================2 after lock in syncResponseReceived() of device <{}>", deviceSn);
		if (!messageQueue.isEmpty())
		{
			executeLock.lock();
			if (!executeFlag)
			{
				executeFlag = true;
				executor.execute(this);
			}
			executeLock.unlock();
		}
		//logger.debug("==================3 before unlock in syncResponseReceived() of device <{}>", deviceSn);
		queueLock.unlock();
		//logger.debug("==================4 after unlock in syncResponseReceived() of device <{}>", deviceSn);
	}
	
	public void addMessage(IRunnableMessage message)
	{
		if (message == null)
		{
			return;
		}
		
		message.setQueueTime(System.currentTimeMillis());
		String messageName = message.getMessageName();
		
		if (logger.isDebugEnabled())
		{
			logger.debug("Queue message " +  messageName
				+ " with MessageSN: " + message.getMessageSn());
		}
		
		if (messageName.equals(MessageId.AppDataSyncRequest.name()))
		{
			// Put AppDataSyncRequest in higher priority
			InputMsgExecutorPool.instance.execute(message);
			return;
		}
		
		queueLock.lock();
		messageQueue.add(message);

		// Execution of message will resume after response of synchronized message was received 
		if (!syncPauseFlag)
		{
			executeLock.lock();
			if (!executeFlag)
			{
				executeFlag = true;
				executor.execute(this);
			}
			executeLock.unlock();
		}
		queueLock.unlock();
	}

	@Override
	public void run()
	{
		try
		{
			logger.debug("Message handler of device <{}> started", msgOwnerInfo);
			queueLock.lock();
			
			IRunnableMessage message;
			while (!messageQueue.isEmpty())
			{
				message = messageQueue.remove();
				queueLock.unlock();
				try
				{
					executeMessage(message);
					if (message.isSyncMessage())
					{
						// Pause execution of message until response of
						// synchronized notification received
						// logger.debug("Interrupt message sending of device <{}> due to synchronized waiting",
						// message.getMsgOwnerInfo());
						// syncPauseFlag = true;
						// Thread.sleep(15);
					}
				}
				catch (Exception e)
				{
					FileLogger.printStackTrace(e);
				}
				queueLock.lock();
			}
			
			executeLock.lock();
			executeFlag = false;
			executeLock.unlock();
			queueLock.unlock();
			
			logger.debug("Message handler of device <{}> ended", msgOwnerInfo);
		}
		catch(Exception e)
		{
			FileLogger.printStackTrace(e);
		}
		
	}
	
	private void executeMessage(IRunnableMessage message)
	{
		int duration = message.getQueueDuration();
		
		if (logger.isDebugEnabled())
		{
			String temp = "Start to handle " + message.getMessageName() + " related to session <"
					+ message.getMsgOwnerInfo() + "> which was queued " + duration
					+ "ms ago, message Sn: " + message.getMessageSn();
			logger.debug(temp);
		}
		
		int delay = message.getDelayOfHandle();
		if (delay > 0)
		{
			if (duration < delay)
			{
				// To ensure message is delayed for given time
				// Currently it's designed for delay of SessionBound
				delay = delay - duration;
				logger.debug("Delay " + delay + "ms for handle of " + message.getMessageName(), message.getMsgOwnerInfo());
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
					FileLogger.printStackTrace(e);
				}
			}
		}
		message.run();
	}
	
	public boolean isEmpty()
	{
		return messageQueue.isEmpty();
	}
}

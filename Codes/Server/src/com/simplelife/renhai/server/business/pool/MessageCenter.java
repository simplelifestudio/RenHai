/**
 * MessageCenter.java
 * 
 * History:
 *     2013-10-25: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerErrorResponse;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.JSONKey;

/**
 * 
 */
public class MessageCenter
{
	private class MessageExecuteThread extends Thread
	{
		private ConcurrentLinkedQueue<AppJSONMessage> messageQueue;
		private int index;
		private final Lock lock = new ReentrantLock();
		private final Condition condition = lock.newCondition();
		protected boolean runFlag = true;
		protected Logger logger = BusinessModule.instance.getLogger();
	    
		public MessageExecuteThread(int index, ConcurrentLinkedQueue<AppJSONMessage> messageQueue)
		{
			this.index = index;
			this.messageQueue = messageQueue;
		}
		
		public void resumeExecute()
		{
			lock.lock();
	    	condition.signal();
	    	lock.unlock();
	    }
	    
		public void stopThread()
	    {
	    	runFlag = false;
	    	resumeExecute();
	    }
		
		
	    @Override
		public void run()
		{
			lock.lock();
			try
			{
				while (runFlag)
				{
					AppJSONMessage command = null;
					if (!messageQueue.isEmpty())
					{
						try
						{
							command = messageQueue.remove();
						}
						catch(Exception e)
						{
							
						}
						
						try
						{
							if (command == null)
							{
								continue;
							}
							String messageId = command.getMessageId().name();
							this.setName(index + "-" + messageId + DateUtil.getCurrentMiliseconds());
							logger.debug("Start to execute " + messageId 
									+ " with MessageSN: " + command.getMessageSn() 
									+ ", which was queued " + (System.currentTimeMillis() - command.getQueueTime()) 
									+ "ms ago.");
							command.run();
						}
						catch(Exception e)
				    	{
				    		ServerErrorResponse response = new ServerErrorResponse(command);
				        	response.addToBody(JSONKey.ReceivedMessage, command.getJSONObject());
				        	response.addToBody(JSONKey.ErrorCode, Consts.GlobalErrorCode.UnknownException_1104.getValue());
				        	response.addToBody(JSONKey.ErrorDescription, "Server internal error");
				        	response.addToHeader(JSONKey.MessageSn, command.getMessageSn());
				        	
				        	response.asyncResponse();
				    		FileLogger.printStackTrace(e);
				    	}
					}
					else
					{
						logger.debug("Queue thread-" + index + " await due to empty message queue");
						condition.await();
						logger.debug("Queue thread-" + index + " resume to work");
					}
				}
			}
			catch (InterruptedException e)
			{
				FileLogger.printStackTrace(e);
			}
			finally
			{
				lock.unlock();
			}
		}
	}
	
	
	public final static MessageCenter instance = new MessageCenter();
	private ConcurrentLinkedQueue<AppJSONMessage> messageQueue = new ConcurrentLinkedQueue<AppJSONMessage>();
	private List<MessageExecuteThread> threadList = new ArrayList<MessageExecuteThread>();
	private Logger logger = BusinessModule.instance.getLogger();
	private ExecutorService executeThreadPool;
	
	private MessageCenter()
	{
		
	}
	
	public void addMessage(AppJSONMessage message)
	{
		if (message == null)
		{
			return;
		}
		
		message.setQueueTime(System.currentTimeMillis());
		logger.debug("Queue message " + message.getMessageId().name() 
				+ " with MessageSN: " + message.getMessageSn());
		executeThreadPool.execute(message);
		//messageQueue.add(message);
		//notifyThreads();
	}
	
	private void notifyThreads()
	{
		for (MessageExecuteThread thread : threadList)
		{
			thread.resumeExecute();
		}
	}

	public void startThreads()
	{
		executeThreadPool = Executors.newFixedThreadPool(GlobalSetting.BusinessSetting.MessageHandleThreads);
		
		/*
		MessageExecuteThread thread;
		for (int i = 0; i < GlobalSetting.BusinessSetting.MessageHandleThreads; i++)
		{
			thread = new MessageExecuteThread(i, messageQueue);
			threadList.add(thread);
			thread.start();
		}
		*/
	}
	
	public void stopThreads()
	{
		executeThreadPool.shutdown();
		/*
		for (MessageExecuteThread thread : threadList)
		{
			thread.stopThread();
		}
		*/
	}
}

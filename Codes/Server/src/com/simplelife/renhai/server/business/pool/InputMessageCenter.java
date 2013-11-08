/**
 * MessageCenter.java
 * 
 * History:
 *     2013-10-25: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.util.GlobalSetting;

/**
 * 
 */
public class InputMessageCenter
{
	public final static InputMessageCenter instance = new InputMessageCenter();
	//private ConcurrentLinkedQueue<AppJSONMessage> messageQueue = new ConcurrentLinkedQueue<AppJSONMessage>();
	//private List<MessageExecuteThread> threadList = new ArrayList<MessageExecuteThread>();
	private Logger logger = BusinessModule.instance.getLogger();
	private ExecutorService executeThreadPool;
	
	private InputMessageCenter()
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
	}
	
	public void startThreads()
	{
		executeThreadPool = Executors.newFixedThreadPool(GlobalSetting.BusinessSetting.InputMessageHandleThreads);
	}
	
	public void stopThreads()
	{
		executeThreadPool.shutdown();
	}
}

/**
 * MessageCenter.java
 * 
 * History:
 *     2013-10-25: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IMessageCenter;
import com.simplelife.renhai.server.util.Consts.MessageId;

/**
 * 
 */
public class InputMessageCenter implements IMessageCenter
{
	public final static InputMessageCenter instance = new InputMessageCenter();
	//private ConcurrentLinkedQueue<AppJSONMessage> messageQueue = new ConcurrentLinkedQueue<AppJSONMessage>();
	//private List<MessageExecuteThread> threadList = new ArrayList<MessageExecuteThread>();
	private Logger logger = BusinessModule.instance.getLogger();
	private ExecutorService executeThreadPool;
	private ConcurrentHashMap<String, MessageHandler> messageMap = new ConcurrentHashMap<String, MessageHandler>();
	
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
		
		if (message.getMessageId() == MessageId.AppDataSyncRequest)
		{
			// Put AppDataSyncRequest in higher priority
			executeThreadPool.execute(message);
			return;
		}
		
		String deviceSn = message.getDeviceWrapper().getDeviceSn();
		if (messageMap.containsKey(deviceSn))
		{
			MessageHandler handler = messageMap.get(deviceSn);
			handler.add(message);
			return;
		}
		
		MessageHandler handler = new MessageHandler(deviceSn, this);
		handler.add(message);
		
		messageMap.put(deviceSn, handler);
		//logger.debug("Launch new input message handler for device <{}>", deviceSn);
		executeThreadPool.execute(handler);
	}
	
	public void startService()
	{
		executeThreadPool = Executors.newFixedThreadPool(GlobalSetting.BusinessSetting.InputMessageHandleThreads);
	}
	
	public void stopService()
	{
		executeThreadPool.shutdown();
	}

	@Override
	public void removeMessageHandler(String deviceSn)
	{
		messageMap.remove(deviceSn);
	}
}

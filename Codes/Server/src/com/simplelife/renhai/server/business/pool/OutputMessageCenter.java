/**
 * OutputMessageCenter.java
 * 
 * History:
 *     2013-10-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IMessageCenter;

/**
 * 
 */
public class OutputMessageCenter implements IMessageCenter
{
	private ConcurrentHashMap<String, MessageHandler> messageMap = new ConcurrentHashMap<String, MessageHandler>();
	public final static OutputMessageCenter instance = new OutputMessageCenter();
	private ExecutorService executeThreadPool;
	
	private OutputMessageCenter()
	{
		
	}
	
	public void addMessage(ServerJSONMessage message)
	{
		String deviceSn = message.getDeviceWrapper().getDeviceSn();
		message.setQueueTime(System.currentTimeMillis());
		if (messageMap.containsKey(deviceSn))
		{
			MessageHandler sender = messageMap.get(deviceSn);
			sender.add(message);
			return;
		}
		
		MessageHandler sender = new MessageHandler(deviceSn, this);
		sender.add(message);
		
		messageMap.put(deviceSn, sender);
		executeThreadPool.execute(sender);
	}
	
	public void removeMessageHandler(String deviceSn)
	{
		messageMap.remove(deviceSn);
	}
	
	public void startThreads()
	{
		executeThreadPool = Executors.newFixedThreadPool(GlobalSetting.BusinessSetting.OutputMessageSendThreads);
	}
	
	public void stopThreads()
	{
		executeThreadPool.shutdown();
	}
}

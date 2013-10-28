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

/**
 * 
 */
public class OutputMessageCenter
{
	private class OutputMessageSender implements Runnable
	{
		private String deviceSn;
		public OutputMessageSender(String deviceSn)
		{
			this.deviceSn = deviceSn;
		}
		
		private ConcurrentLinkedQueue<ServerJSONMessage> queue = new ConcurrentLinkedQueue<ServerJSONMessage>();
		
		public void add(ServerJSONMessage message)
		{
			queue.add(message);
		}

		@Override
		public void run()
		{
			ServerJSONMessage message;
			while (!queue.isEmpty())
			{
				message = queue.remove();
				String temp = "send " + message.getMessageId().name() + " to App <" + message.getDeviceWrapper().getDeviceSn() + ">"; 
				message.response();
			}
			
			OutputMessageCenter.instance.removeMessageSender(deviceSn);
		}
	}
	
	private ConcurrentHashMap<String, OutputMessageSender> messageMap = new ConcurrentHashMap<String, OutputMessageSender>();
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
			OutputMessageSender sender = messageMap.get(deviceSn);
			sender.add(message);
			return;
		}
		
		OutputMessageSender sender = new OutputMessageSender(deviceSn);
		sender.add(message);
		
		messageMap.put(deviceSn, sender);
		executeThreadPool.execute(sender);
	}
	
	public void removeMessageSender(String deviceSn)
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

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

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.json.AbstractJSONMessage;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.IMessageCenter;

/**
 * 
 */
public class MessageHandler implements Runnable
{
	private String deviceSn;
	private IMessageCenter messageCenter;
	private Logger logger = BusinessModule.instance.getLogger();
	
	public MessageHandler(String deviceSn, IMessageCenter messageCenter)
	{
		this.deviceSn = deviceSn;
		this.messageCenter = messageCenter;
	}
	
	private ConcurrentLinkedQueue<AbstractJSONMessage> queue = new ConcurrentLinkedQueue<>();
	
	public void add(AbstractJSONMessage message)
	{
		queue.add(message);
	}

	@Override
	public void run()
	{
		logger.debug("Message handler of device <{}> started", deviceSn);
		AbstractJSONMessage message;
		int duration;
		int delay;
		while (!queue.isEmpty())
		{
			message = queue.remove();
			duration = message.getQueueDuration();;
			String temp = "Start to handle " + message.getMessageId().name() + " related to device <" 
				+ message.getDeviceWrapper().getDeviceSn() + "> which was queued " 
				+ duration + "ms ago, message Sn: " + message.getMessageSn();
			BusinessModule.instance.getLogger().debug(temp);
			
			delay = message.getDelayOfHandle(); 
			if (delay > 0)
			{
				if (duration < delay)
				{
					// To ensure message is delayed for given time
					// Currently it's designed for delay of SessionBound
					delay = delay - duration;
					logger.debug("Delay " + delay + "ms for handle of " + message.getMessageId().name(), message.getDeviceWrapper().getDeviceSn());
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
		
		messageCenter.removeMessageHandler(deviceSn);
		logger.debug("Message handler of device <{}> ended", deviceSn);
	}
}

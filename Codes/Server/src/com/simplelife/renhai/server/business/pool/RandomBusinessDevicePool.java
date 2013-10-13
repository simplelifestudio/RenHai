/**
 * RandomBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class RandomBusinessDevicePool extends AbstractBusinessDevicePool
{
	public RandomBusinessDevicePool()
	{
		businessType = Consts.BusinessType.Random;
		
		businessScheduler = new RandomBusinessScheduler();
		businessScheduler.bind(this);
		businessScheduler.setName("RandomScheduler");
		businessScheduler.startScheduler();
		
		setCapacity(GlobalSetting.BusinessSetting.RandomBusinessPoolCapacity);
	}

	@Override
	public void startChat(IDeviceWrapper device)
	{
		String deviceSn = device.getDeviceSn();
		synchronized(deviceMap)
		{
			deviceMap.remove(deviceSn);
		}
		synchronized(chatDeviceMap)
		{
			chatDeviceMap.put(deviceSn, device);
		}
	}

	@Override
	public void endChat(IDeviceWrapper device)
	{
		String sn = device.getDeviceSn();
		boolean existFlag = false;
		synchronized(chatDeviceMap)
		{
			if (chatDeviceMap.containsKey(sn))
			{
				existFlag = true;
				chatDeviceMap.remove(sn);
			}
		}
		
		if (existFlag)
		{
			// Maybe device has been removed from business device pool by another thread
			synchronized(deviceMap)
			{
				deviceMap.put(sn, device);
			}
		}
	}

}

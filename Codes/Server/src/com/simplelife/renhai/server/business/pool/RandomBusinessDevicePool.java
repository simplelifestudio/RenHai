/**
 * RandomBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import com.simplelife.renhai.server.util.GlobalSetting;


/** */
public class RandomBusinessDevicePool extends AbstractBusinessDevicePool
{
	public RandomBusinessDevicePool()
	{
		businessScheduler = new RandomBusinessScheduler();
		businessScheduler.bind(this);
		
		setCapacity(GlobalSetting.BusinessSetting.RandomBusinessPoolCapacity);
	}

}

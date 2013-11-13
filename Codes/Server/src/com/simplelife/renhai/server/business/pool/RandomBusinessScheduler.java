/**
 * RandomBusinessScheduler.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.util.IBusinessSession;


/** */
public class RandomBusinessScheduler extends AbstractBusinessScheduler
{
	@Override
	public void bind(AbstractBusinessDevicePool pool)
    {
    	this.ownerBusinessPool = pool;
    	deviceMap = pool.getDeviceMap();
    }
	
	/** */
	public void schedule()
	{
		if (deviceMap.size() < deviceCountPerSession)
		{
			return;
		}
		
		List<String> selectedDevice = new ArrayList<String>();
		
		if (deviceCountPerSession == deviceMap.size())
		{
			selectedDevice.addAll(deviceMap.keySet());
		}
		else
		{
			Set<String> keySet = deviceMap.keySet();
			Object[] keyArray = keySet.toArray();
			
			Random random = new Random();
			String key;
			for (int i = 0; i < deviceCountPerSession; i++)
			{
				do
				{
					key = (String) keyArray[random.nextInt(keyArray.length)];
				} while (selectedDevice.contains(key));
				
				selectedDevice.add(key);
			}
		}
		
		IBusinessSession session = BusinessSessionPool.instance.getBusinessSession();
		if (session == null)
		{
			logger.debug("No availabel business session.");
			return;
		}
		
		session.bindBusinessDevicePool(this.ownerBusinessPool);
		if (!session.startSession(selectedDevice, null))
		{
			recycleDevice(selectedDevice);
		}
	}

	@Override
	public boolean meetScheduleCondition()
	{
		return (deviceMap.size() >= deviceCountPerSession);
	}
}

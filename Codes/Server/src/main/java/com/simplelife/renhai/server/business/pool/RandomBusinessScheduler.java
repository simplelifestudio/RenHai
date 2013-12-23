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
import java.util.Collection;
import java.util.List;
import java.util.Random;
import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class RandomBusinessScheduler extends AbstractBusinessScheduler
{
	@Override
	public void bind(AbstractBusinessDevicePool pool)
    {
    	this.ownerBusinessPool = pool;
    	matchStartedDeviceMap = pool.getDeviceMap();
    }
	
	/** */
	public void schedule()
	{
		if (matchStartedDeviceMap.size() < deviceCountPerSession)
		{
			return;
		}
		
		List<IDeviceWrapper> selectedDevice = new ArrayList<>();
		
		if (deviceCountPerSession == matchStartedDeviceMap.size())
		{
			selectedDevice.addAll(matchStartedDeviceMap.values());
		}
		else
		{
			Collection<IDeviceWrapper> deviceSet = matchStartedDeviceMap.values();
			Object[] deviceArray = deviceSet.toArray();
			
			Random random = new Random();
			IDeviceWrapper device;
			for (int i = 0; i < deviceCountPerSession; i++)
			{
				do
				{
					device = (IDeviceWrapper) deviceArray[random.nextInt(deviceArray.length)];
				} while (selectedDevice.contains(device));
				
				selectedDevice.add(device);
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
		return (matchStartedDeviceMap.size() >= deviceCountPerSession);
	}
}

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
	private final int deviceCountPerSession = 2; 
	
	/** */
	public void schedule()
	{
		if (deviceMap.size() < deviceCountPerSession)
		{
			return;
		}
		
		List<String> selectedDevice = new ArrayList<String>();
		
		synchronized (deviceMap)
		{
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
		}
		
		IBusinessSession session = BusinessSessionPool.instance.getBusinessSession();
		if (session == null)
		{
			return;
		}
		session.bind(this.ownerBusinessPool);
		session.startSession(selectedDevice);
	}
	
	@Override
	public void run()
	{
		lock.lock();
		try
		{
			while (runFlag)
			{
				if (deviceMap.size() >= deviceCountPerSession)
				{
					schedule();
				}
				else
				{
					logger.debug("Await due to there is no enough device in devicemap");
					condition.await();
					logger.debug("Recover from await");
				}
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
	}
}

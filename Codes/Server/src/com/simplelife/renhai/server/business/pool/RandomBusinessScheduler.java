/**
 * RandomBusinessScheduler.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;


/** */
public class RandomBusinessScheduler extends AbstractBusinessScheduler
{
	private final int deviceCountPerSession = 2; 
	
	/** */
	public void startScheduler()
	{
		
	}
	
	/** */
	public void stopScheduler()
	{
		lock.unlock();
	}
	
	/** */
	public void schedule()
	{
		if (deviceMap.size() < deviceCountPerSession)
		{
			
		}
	}
	
	@Override
	public void run()
	{
		lock.lock();
		try
		{
			//while ()
			condition.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void signal()
	{
		condition.signal();
	}
}

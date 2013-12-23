/**
 * ExecuteThread.java
 * 
 * History:
 *     2013-11-24: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.log.FileLogger;

/**
 * 
 */
public class Worker extends Thread
{
	private boolean continueFlag = true;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private volatile boolean isRunning = false;
	private IProductor productor;
	private Logger logger = BusinessModule.instance.getLogger();

	public Worker(IProductor productor)
	{
		this.productor = productor;
	}
	
	public void resumeExecution()
	{
		if (isRunning)
		{
			return;
		}
		
		lock.lock();
		condition.signal();
		lock.unlock();
	}
	
	public void startExecution()
	{
		continueFlag = true;
		this.start();
	}
	
	public void stopExecution()
	{
		continueFlag = false;
		resumeExecution();		// Wakeup from await
	}
	
	@Override
	public void run()
	{
		isRunning = true;
		while(continueFlag)
		{
			if (productor.hasWork())
			{
				Runnable work = productor.getWork();
				if (work != null)
				{
					work.run();
				}
			}
			else
			{
				try
				{
					lock.lock();
					isRunning = false;
					condition.await();
					isRunning = true;
				}
				catch (InterruptedException e)
				{
					FileLogger.printStackTrace(e);
				}
				finally
				{
					lock.unlock();
				}
			}
		}
	}
}

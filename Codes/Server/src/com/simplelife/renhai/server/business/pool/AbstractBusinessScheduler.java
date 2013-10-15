/**
 * AbstractBusinessScheduler.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.IBusinessScheduler;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractBusinessScheduler extends Thread implements IBusinessScheduler
{
    /** */
    protected AbstractBusinessDevicePool ownerBusinessPool;
    protected final Lock lock = new ReentrantLock();
    protected final Condition condition = lock.newCondition();
	protected boolean runFlag = false;
    protected Logger logger = BusinessModule.instance.getLogger();
    protected ConcurrentHashMap<String, IDeviceWrapper> deviceMap;
    
    protected final int deviceCountPerSession = 2;

    public Lock getLock()
    {
    	return lock;
    }
    
    
    /** */
    public void startScheduler()
    {
    	runFlag = true;
    	this.start();
    }
    
    /** */
    public void stopScheduler()
    {
    	runFlag = false;
    }

    public void signal()
    {
    	condition.signal();
    }
    
    /** */
    public abstract void bind(AbstractBusinessDevicePool pool);
    
    public abstract boolean meetScheduleCondition();
    
    @Override
	public void run()
	{
		lock.lock();
		try
		{
			while (runFlag)
			{
				if (meetScheduleCondition())
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
			FileLogger.printStackTrace(e);
		}
		finally
		{
			lock.unlock();
		}
	}
}

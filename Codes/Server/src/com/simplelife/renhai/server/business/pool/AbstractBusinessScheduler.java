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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.util.IBusinessScheduler;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractBusinessScheduler extends Thread implements IBusinessScheduler
{
    /** */
    protected AbstractBusinessDevicePool ownerBusinessPool;
    protected HashMap<String, IDeviceWrapper> deviceMap;
    protected final Lock lock = new ReentrantLock();
    protected final Condition condition = lock.newCondition();
	protected boolean runFlag = false;
    protected Logger logger = BusinessModule.instance.getLogger();

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
    public void bind(AbstractBusinessDevicePool pool)
    {
    	this.ownerBusinessPool = pool;
    	deviceMap = pool.getDeviceMap();
    }
}

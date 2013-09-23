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

import com.simplelife.renhai.server.util.IBusinessScheduler;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractBusinessScheduler extends Thread implements IBusinessScheduler
{
    /** */
    protected AbstractBusinessDevicePool ownerBusinessPool;
    protected HashMap<String, IDeviceWrapper> deviceMap;
    public final Lock lock = new ReentrantLock();
	public final Condition condition = lock.newCondition(); 
    
    /** */
    public abstract void startScheduler();
    
    /** */
    public abstract void stopScheduler();
    
    /** */
    public abstract void schedule();
    
    public abstract void signal();
    
    /** */
    public void bind(AbstractBusinessDevicePool pool)
    {
    	this.ownerBusinessPool = pool;
    	deviceMap = pool.getDeviceMap();
    }
}

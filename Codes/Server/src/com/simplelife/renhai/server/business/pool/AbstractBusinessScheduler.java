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
import com.simplelife.renhai.server.util.IBusinessScheduler;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractBusinessScheduler extends Thread implements IBusinessScheduler
{
    /** */
    protected AbstractBusinessDevicePool ownerBusinessPool;
    protected HashMap<String, IDeviceWrapper> deviceMap;
    
    /** */
    public abstract void startScheduler();
    
    /** */
    public abstract void stopScheduler();
    
    /** */
    public abstract void schedule();
    
    /** */
    public void bind(AbstractBusinessDevicePool pool)
    {
    	this.ownerBusinessPool = pool;
    	deviceMap = pool.getDeviceMap();
    }
}

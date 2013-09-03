/**
 * AbstractBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.HashMap;

import com.simplelife.renhai.server.util.IBusinessPool;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class AbstractBusinessDevicePool extends AbstractDevicePool implements IBusinessPool
{
    /** */
    protected AbstractBusinessScheduler businessScheduler;
    
    /** */
    protected HashMap chatDeviceMap;
    
    /** */
    public boolean isPoolFull()
    {
        return false;
    }
    
    /** */
    public void updateCount()
    {
    }
    
    public AbstractBusinessScheduler getBusinessScheduler()
    {
    	return businessScheduler;
    }
    
    /** */
    public int getElementCount()
    {
        return chatDeviceMap.size();
    }
    
    /** */
    public boolean deviceEnter(IDeviceWrapper device)
    {
        return false;
    }
    
    /** */
    public void deviceLeave(IDeviceWrapper device)
    {
    }
}

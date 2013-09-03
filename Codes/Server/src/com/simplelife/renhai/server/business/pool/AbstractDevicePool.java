/**
 * AbstractDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.HashMap;

import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractDevicePool extends AbstractPool
{
    /** */
    protected HashMap deviceMap;
    
    /** */
    public IDeviceWrapper getDevice(int deviceId)
    {
        return null;
    
    }
    
    /** */
    public IDeviceWrapper getDevice(String deviceSn)
    {
        return null;
    
    }
    
    /** */
    public boolean isPoolFull()
    {
        return false;
    }
    
    /** */
    public void updateCount()
    {
    }
    
    /** */
    public int getElementCount()
    {
        return 0;
    }
    
    public void clearPool()
    {
    	deviceMap.clear();
    }
}

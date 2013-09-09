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
    protected HashMap<String, IDeviceWrapper> deviceMap = new HashMap<String, IDeviceWrapper>();
    
    /** */
    public IDeviceWrapper getDevice(String deviceSn)
    {
        return deviceMap.get(deviceSn);
    }
    
    /** */
    public abstract boolean isPoolFull();
    /** */
    public abstract int getElementCount();
    
    public abstract void clearPool();
}

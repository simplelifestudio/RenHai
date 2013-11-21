/**
 * AbstractDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.ConcurrentHashMap;

import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractDevicePool extends AbstractPool
{
	public abstract IDeviceWrapper getDevice(String deviceSn);
    
    /** */
    public abstract boolean isPoolFull();
    /** */
    public abstract int getElementCount();
    
    public abstract void clearPool();
}

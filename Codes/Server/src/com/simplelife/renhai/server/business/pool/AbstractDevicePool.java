/**
 * AbstractDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractDevicePool extends AbstractPool
{
	protected volatile int elementCount;
	public int getElementCount()
    {
    	return elementCount;
    }
	
	public abstract IDeviceWrapper getDevice(String deviceSn);
    public abstract boolean isPoolFull();
    public abstract void clearPool();
}

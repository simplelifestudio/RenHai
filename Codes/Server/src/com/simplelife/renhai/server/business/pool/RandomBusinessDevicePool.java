/**
 * RandomBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class RandomBusinessDevicePool extends AbstractBusinessDevicePool
{
    /** */
    public boolean deviceEnter(IDeviceWrapper device)
    {
        return false;
    }
    
    /** */
    public void deviceLeave(IDeviceWrapper device)
    {
    }

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.business.pool.AbstractDevicePool#clearPool()
	 */
	@Override
	public void clearPool()
	{
		deviceMap.clear();
	}
}

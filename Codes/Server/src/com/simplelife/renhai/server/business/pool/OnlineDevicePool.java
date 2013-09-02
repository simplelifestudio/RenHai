/**
 * OnlineDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.List;
import java.util.Timer;

import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBusinessPool;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class OnlineDevicePool extends AbstractDevicePool
{
    /** */
    private Timer timer;
    
    /** */
    private List businessPoolList;
    
    private static OnlineDevicePool poolInstance;
    
    /** */
    private void checkInactiveDevice()
    {
    
    }
    
    /** */
    public static OnlineDevicePool getInstance()
    {
    	if (poolInstance != null)
    	{
    		return poolInstance;
    	}
    	
    	synchronized (poolInstance)
		{
    		// Check again after get lock
    		if (poolInstance != null)
        	{
        		return poolInstance;
        	}
    		
    		poolInstance = new OnlineDevicePool();
    		return poolInstance;
		}
    }
    
    /** */
    public void addBusinessPool(IBusinessPool pool)
    {
    
    }
    
    /** */
    public List getBusinessPoolList()
    {
        return businessPoolList;
    
    }
    
    /** */
    public boolean isDeviceInPool(int deviceId)
    {
        return false;
    
    }
    
    /** */
    public IDeviceWrapper newDevice(IBaseConnection connection)
    {
        return null;
    
    }
    
    /** */
    public void releaseDevice(IDeviceWrapper device)
    {
    
    }
    
    /** */
    public void removeBusinessPool(IBusinessPool pool)
    {
    
    }
}

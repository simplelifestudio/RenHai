/**
 * OnlineDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.Timer;
import java.util.Vector;

import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBusinessPool;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class OnlineDevicePool extends AbstractDevicePool
{
    /** */
    private Timer timer;
    
    /** */
    private Vector<AbstractBusinessDevicePool> businessPoolList = new Vector<AbstractBusinessDevicePool>();
    
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
    public void addBusinessPool(int type, AbstractBusinessDevicePool pool)
    {
    	businessPoolList.set(type, pool);
    }
    
    public AbstractBusinessDevicePool getBusinessPool(Consts.BusinessType type)
    {
    	int index = type.ordinal();
    	if (index < 0 || index >= businessPoolList.size())
    	{
    		return null;
    	}
    	
    	return businessPoolList.get(index);
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

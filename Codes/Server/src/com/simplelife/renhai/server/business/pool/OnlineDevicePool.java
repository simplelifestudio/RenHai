/**
 * OnlineDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBusinessPool;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class OnlineDevicePool extends AbstractDevicePool
{
    /** */
    private Timer timer;
    private HashMap<String, DeviceWrapper> tmpDeviceLink = new HashMap<String, DeviceWrapper>(); 
    
    /** */
    private Vector<AbstractBusinessDevicePool> businessPoolList = new Vector<AbstractBusinessDevicePool>();
    
    public final static OnlineDevicePool instance = new OnlineDevicePool();
    
    
    private OnlineDevicePool()
    {
    	
    }
    
    /** */
    private void checkInactiveDevice()
    {
    
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
    	Logger logger = BusinessModule.instance.getLogger();
    	logger.debug("newDevice");
    	DeviceWrapper deviceWrapper = new DeviceWrapper(connection);
    	deviceWrapper.bindOnlineDevicePool(this);
    	connection.bind(deviceWrapper);
    	tmpDeviceLink.put(connection.getConnectionId(), deviceWrapper);
        return deviceWrapper;
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

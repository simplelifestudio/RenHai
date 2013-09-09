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
import java.util.LinkedList;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IBusinessPool;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractBusinessDevicePool extends AbstractDevicePool implements IBusinessPool
{
    // Scheduler of current BusinessDevicePool
    protected AbstractBusinessScheduler businessScheduler;
    
    // Map for saving devices in chat
    protected HashMap<String, IDeviceWrapper> chatDeviceMap;
    
    /**
     * Return if current BusinessDevicePool reaches its capacity
     */
    public boolean isPoolFull()
    {
    	return (getElementCount() >= this.capacity);
    }
    
    /**
     * Return scheduler of current BusinessDevicePool
     * @return
     */
    public AbstractBusinessScheduler getBusinessScheduler()
    {
    	return businessScheduler;
    }
    
    /**
     * Return count of elements in current BusinessDevicePool
     */
    public int getElementCount()
    {
        return chatDeviceMap.size() + deviceMap.size();
    }
    
    /**
     * Device enters BusinessDevicePool, it's triggered by entering business 
     */
    public boolean deviceEnter(IDeviceWrapper device)
    {
    	if (device == null)
    	{
    		return false;
    	}
    	
    	Logger logger = BusinessModule.instance.getLogger();
    	if (isPoolFull())
    	{
    		return false;
    	}
    	
    	String sn = device.getDeviceSn();
    	if (deviceMap.containsKey(sn))
    	{
    		logger.warn("Device ({}) has been in BusinessDevicePool", sn);
    		return true;
    	}
    	
    	if (chatDeviceMap.containsKey(sn))
    	{
    		logger.error("Device ({}) has been in BusinessDevicePool and is in chat", sn);
    		return false;
    	}
    	
    	deviceMap.put(sn, device);
    	return true;
    }

    /**
     * Device leaves BusinessDevicePool, it may be caused by exit business or device is released
     */
    public void deviceLeave(IDeviceWrapper device)
    {
    	Logger logger = BusinessModule.instance.getLogger();
    	if (device == null)
    	{
    		return;
    	}
    	
    	String sn = device.getDeviceSn();
    	if (!(deviceMap.containsKey(sn) || chatDeviceMap.containsKey(sn)))
    	{
    		logger.warn("Device ({}) was not in BusinessDevicePool", sn);
    		return;
    	}
    	
    	synchronized(deviceMap)
    	{
    		deviceMap.remove(sn);
    	}
    	
    	synchronized(chatDeviceMap)
    	{
    		chatDeviceMap.remove(sn);
    	}
    	
    	if (device.getBusinessStatus() == Consts.DeviceBusinessStatus.SessionBound)
    	{
    		IBusinessSession session = device.getOwnerBusinessSession();
    		session.onDeviceLeave(device);
    	}
    }
    

    /**
     * Starts chat, move device from deviceMap to chatDeviceMap
     */
	@Override
	public void startChat(IBusinessSession session)
	{
		LinkedList<IDeviceWrapper> deviceList =  session.getDeviceList();
		String sn;
		for (IDeviceWrapper device : deviceList)
		{
			sn = device.getDeviceSn();
			
			synchronized(deviceMap)
			{
				deviceMap.remove(sn);
			}
			synchronized(chatDeviceMap)
			{
				chatDeviceMap.put(sn, device);
			}
		}
	}

	/**
     * Chat ends, move device from chatDeviceMap to deviceMap
     */
	@Override
	public void endChat(IBusinessSession session)
	{
		LinkedList<IDeviceWrapper> deviceList =  session.getDeviceList();
		String sn;
		for (IDeviceWrapper device : deviceList)
		{
			sn = device.getDeviceSn();
			
			synchronized(chatDeviceMap)
			{
				chatDeviceMap.remove(sn);
			}
			synchronized(deviceMap)
			{
				deviceMap.put(sn, device);
			}
		}
	}
}

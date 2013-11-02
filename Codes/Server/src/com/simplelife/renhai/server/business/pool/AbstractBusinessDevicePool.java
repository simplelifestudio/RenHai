/**
 * AbstractBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IBusinessPool;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public abstract class AbstractBusinessDevicePool extends AbstractDevicePool implements IBusinessPool
{
    // Scheduler of current BusinessDevicePool
    protected AbstractBusinessScheduler businessScheduler;
    protected Consts.BusinessType businessType;
    protected Logger logger = BusinessModule.instance.getLogger();
    
    // Map for saving devices in chat
    protected ConcurrentHashMap<String, IDeviceWrapper> chatDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
    protected ConcurrentHashMap<String, IDeviceWrapper> cacheDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
    
    
    public Consts.BusinessType getBusinessType()
    {
    	return businessType;
    }
    
    public ConcurrentHashMap<String, IDeviceWrapper> getDeviceMap()
    {
    	return deviceMap;
    }
    
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
        return chatDeviceMap.size() + deviceMap.size() + cacheDeviceMap.size();
    }
    
    public String checkDeviceEnter(IDeviceWrapper device)
    {
    	if (device == null)
    	{
    		return "Device is null";
    	}
    	
    	if (isPoolFull())
    	{
    		String temp = "Pool is full and request of entering pool is rejected"; 
    		logger.warn(temp);
    		return temp;
    	}
    	
    	String sn = device.getDeviceSn();
    	if (deviceMap.containsKey(sn) || cacheDeviceMap.containsKey(sn))
    	{
    		logger.warn("Device <{}> has been in BusinessDevicePool", sn);
    		return null;
    	}
    	
    	if (chatDeviceMap.containsKey(sn))
    	{
    		String temp = "Device ("+ sn +") has been in BusinessDevicePool and is in chat";
    		logger.error(temp);
    		return temp;
    	}
    	return null;
    }
    
    /**
     * Device enters BusinessDevicePool, it's triggered by entering business 
     */
    public void onDeviceEnter(IDeviceWrapper device)
    {
    	cacheDeviceMap.put(device.getDeviceSn(), device);
    	logger.debug("Device <{}> has entered " + businessType.name() + " pool, device count after enter: " + this.getElementCount(), device.getDeviceSn());
    }
    
    /**
     * Device leaves BusinessDevicePool, it may be caused by exit business or device is released
     */
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason)
    {
    	Logger logger = BusinessModule.instance.getLogger();
    	if (device == null)
    	{
    		return;
    	}
    	
    	String sn = device.getDeviceSn();
    	if (!(deviceMap.containsKey(sn) || chatDeviceMap.containsKey(sn) || cacheDeviceMap.containsKey(sn)))
    	{
    		logger.debug("Device <{}> was not in " + this.businessType.name() + " Device Pool", sn);
    		return;
    	}
    	
    	if (deviceMap.containsKey(sn))
    	{
	    	deviceMap.remove(sn);
	    	logger.debug("Device <{}> was removed from deviceMap of " + this.businessType.name() + " Device Pool", sn);
	    	return;
    	}
    	
    	if (chatDeviceMap.containsKey(sn))
    	{
	    	chatDeviceMap.remove(sn);
	    	logger.debug("Device <{}> was removed from chatDeviceMap of " + this.businessType.name() + " Device Pool", sn);
	    	return;
    	}
    	
    	if (cacheDeviceMap.containsKey(sn))
    	{
	    	cacheDeviceMap.remove(sn);
	    	logger.debug("Device <{}> was removed from cacheDeviceMap of " + this.businessType.name() + " Device Pool", sn);
	    	return;
    	}

    	/*
    	if (device.getBusinessType() == this.getBusinessType())
    	{
	    	if (device.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
	    	{
	    		IBusinessSession session = device.getOwnerBusinessSession();
	    		if (session != null)
				{
					synchronized (session)
					{
						logger.debug("Device <{}> has bound session, notify session to notify other devices.", sn);
						session.onDeviceLeave(device, reason);
					}
				}
	    	}
    	}
    	*/
    }
    
    
    /**
     * Starts chat, move device from deviceMap to chatDeviceMap
     */
	@Override
	public abstract void startChat(IDeviceWrapper device);
	
	public abstract void startMatch(IDeviceWrapper device);
	
	@Override
	public IDeviceWrapper getDevice(String deviceSn)
    {
		if (deviceMap.containsKey(deviceSn))
		{
			return deviceMap.get(deviceSn);
		}
		if (chatDeviceMap.containsKey(deviceSn))
		{
			return chatDeviceMap.get(deviceSn);
		}
		if (cacheDeviceMap.containsKey(deviceSn))
		{
			return cacheDeviceMap.get(deviceSn);
		}
   		return null;
    }
	
	/**
     * Chat ends, move device from chatDeviceMap to deviceMap
     */
	@Override
	public abstract void endChat(IDeviceWrapper device);
	
	@Override
	public void clearPool()
	{
		deviceMap.clear();
		chatDeviceMap.clear();
		cacheDeviceMap.clear();
	}
	
	public int getDeviceCountInChat()
	{
		return chatDeviceMap.size();
	}
}

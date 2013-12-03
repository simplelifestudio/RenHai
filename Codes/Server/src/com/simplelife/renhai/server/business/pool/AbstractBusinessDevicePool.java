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
    protected ConcurrentHashMap<String, IDeviceWrapper> sessionBoundDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
    protected ConcurrentHashMap<String, IDeviceWrapper> businessChoosedDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
    protected ConcurrentHashMap<String, IDeviceWrapper> matchStartedDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
    
    protected final int deviceCountPerSession = 2;

    
    public abstract void startService();
    public abstract void stopService();
    
    public int getDeviceCountPerSession()
    {
    	return deviceCountPerSession;
    }
    
    public Consts.BusinessType getBusinessType()
    {
    	return businessType;
    }
    
    public ConcurrentHashMap<String, IDeviceWrapper> getDeviceMap()
    {
    	return matchStartedDeviceMap;
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
    public int getElementCount()
    {
        return sessionBoundDeviceMap.size() + matchStartedDeviceMap.size() + businessChoosedDeviceMap.size();
    }
    */
    
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
    	if (matchStartedDeviceMap.containsKey(sn) || businessChoosedDeviceMap.containsKey(sn))
    	{
    		logger.warn("Device <{}> has been in BusinessDevicePool", sn);
    		return null;
    	}
    	
    	if (sessionBoundDeviceMap.containsKey(sn))
    	{
    		String temp = "Device <"+ sn +"> has been in BusinessDevicePool and is in chat";
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
    	businessChoosedDeviceMap.put(device.getDeviceSn(), device);
    	elementCount++;
    	logger.debug("Device <{}> has entered " + businessType.name() + " pool, device count after enter: " + this.getElementCount(), device.getDeviceSn());
    }
    
    /**
     * Device leaves BusinessDevicePool, it may be caused by exit business or device is released
     */
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason)
    {
    	if (device == null)
    	{
    		return;
    	}
    	
    	String sn = device.getDeviceSn();
    	if (!(matchStartedDeviceMap.containsKey(sn) || sessionBoundDeviceMap.containsKey(sn) || businessChoosedDeviceMap.containsKey(sn)))
    	{
    		logger.debug("Device <{}> was not in " + this.businessType.name() + " Device Pool when try to release it by reason of " + reason.name(), sn);
    		return;
    	}
    	
    	elementCount--;
    	if (matchStartedDeviceMap.containsKey(sn))
    	{
	    	matchStartedDeviceMap.remove(sn);
	    	logger.debug("Device <{}> was removed from matchStartedDeviceMap of " + this.businessType.name() + " Device Pool caused by " + reason.name(), sn);
	    	return;
    	}
    	
    	if (sessionBoundDeviceMap.containsKey(sn))
    	{
	    	sessionBoundDeviceMap.remove(sn);
	    	logger.debug("Device <{}> was removed from sessionBoundDeviceMap of " + this.businessType.name() + " Device Pool caused by " + reason.name(), sn);
	    	return;
    	}
    	
    	if (businessChoosedDeviceMap.containsKey(sn))
    	{
	    	businessChoosedDeviceMap.remove(sn);
	    	logger.debug("Device <{}> was removed from businessChoosedDeviceMap of " + this.businessType.name() + " Device Pool caused by " + reason.name(), sn);
	    	return;
    	}
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
		if (matchStartedDeviceMap.containsKey(deviceSn))
		{
			return matchStartedDeviceMap.get(deviceSn);
		}
		if (sessionBoundDeviceMap.containsKey(deviceSn))
		{
			return sessionBoundDeviceMap.get(deviceSn);
		}
		if (businessChoosedDeviceMap.containsKey(deviceSn))
		{
			return businessChoosedDeviceMap.get(deviceSn);
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
		matchStartedDeviceMap.clear();
		sessionBoundDeviceMap.clear();
		businessChoosedDeviceMap.clear();
	}
	
	public int getDeviceCountInChat()
	{
		return sessionBoundDeviceMap.size();
	}
}

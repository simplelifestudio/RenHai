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
    protected Consts.BusinessType businessType;
    
    // Map for saving devices in chat
    protected HashMap<String, IDeviceWrapper> chatDeviceMap = new HashMap<String, IDeviceWrapper>();
    
    
    public Consts.BusinessType getBusinessType()
    {
    	return businessType;
    }
    
    public HashMap<String, IDeviceWrapper> getDeviceMap()
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
        return chatDeviceMap.size() + deviceMap.size();
    }
    
    /**
     * Device enters BusinessDevicePool, it's triggered by entering business 
     */
    public boolean onDeviceEnter(IDeviceWrapper device)
    {
    	if (device == null)
    	{
    		return false;
    	}
    	
    	Logger logger = BusinessModule.instance.getLogger();
    	if (isPoolFull())
    	{
    		logger.warn("Pool is full and request of entering pool is rejected");
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
    	logger.debug("Device <{}> has entered " + businessType.name() + " pool", device.getDeviceSn());
    	
    	businessScheduler.getLock().lock();
    	businessScheduler.signal();
    	businessScheduler.getLock().unlock();
    	return true;
    }

    /**
     * Device leaves BusinessDevicePool, it may be caused by exit business or device is released
     */
    public void onDeviceLeave(IDeviceWrapper device)
    {
    	Logger logger = BusinessModule.instance.getLogger();
    	if (device == null)
    	{
    		return;
    	}
    	
    	String sn = device.getDeviceSn();
    	if (!(deviceMap.containsKey(sn) || chatDeviceMap.containsKey(sn)))
    	{
    		logger.debug("Device <{}> was not in BusinessDevicePool", sn);
    		return;
    	}
    	
    	if (deviceMap.containsKey(sn))
    	{
	    	synchronized(deviceMap)
	    	{
	    		deviceMap.remove(sn);
	    	}
	    	logger.debug("Device <{}> was removed from deviceMap of BusinessDevicePool", sn);
    	}
    	
    	if (chatDeviceMap.containsKey(sn))
    	{
	    	synchronized(chatDeviceMap)
	    	{
	    		chatDeviceMap.remove(sn);
	    	}
	    	logger.debug("Device <{}> was removed from chatDeviceMap of BusinessDevicePool", sn);
    	}
    	
    	if (device.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
    	{
    		IBusinessSession session = device.getOwnerBusinessSession();
    		session.onDeviceLeave(device);
    	}
    }
    
    
    /**
     * Starts chat, move device from deviceMap to chatDeviceMap
     */
	@Override
	public void startChat(IDeviceWrapper device)
	{
		String sn = device.getDeviceSn();
		
		synchronized(deviceMap)
		{
			deviceMap.remove(sn);
		}
		synchronized(chatDeviceMap)
		{
			chatDeviceMap.put(sn, device);
		}
	}
	
	@Override
	public IDeviceWrapper getDevice(String deviceSn)
    {
		if (deviceMap.containsKey(deviceSn))
		{
			return deviceMap.get(deviceSn);
		}
		else if (chatDeviceMap.containsKey(deviceSn))
		{
			return chatDeviceMap.get(deviceSn);
		}
   		return null;
    }
	
	/**
     * Chat ends, move device from chatDeviceMap to deviceMap
     */
	@Override
	public void endChat(IDeviceWrapper device)
	{
		String sn = device.getDeviceSn();
		
		synchronized(chatDeviceMap)
		{
			chatDeviceMap.remove(sn);
		}
		synchronized(deviceMap)
		{
			deviceMap.put(sn, device);
		}
	}
	
	@Override
	public void clearPool()
	{
		deviceMap.clear();
		chatDeviceMap.clear();
	}
	
	public int getDeviceCountInChat()
	{
		return chatDeviceMap.size();
	}
}

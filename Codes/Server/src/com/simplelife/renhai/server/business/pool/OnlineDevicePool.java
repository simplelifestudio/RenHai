/**
 * OnlineDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IBusinessPool;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class OnlineDevicePool extends AbstractDevicePool
{
	private Logger logger = BusinessModule.instance.getLogger();
	
	private class InactiveCheckTask extends TimerTask
    {
		@Override
		public void run()
		{
			OnlineDevicePool.instance.checkInactiveDevice();
		}
    }
	
    private Timer inactiveTimer = new Timer();
    private HashMap<String, IDeviceWrapper> tmpDeviceMap = new HashMap<String, IDeviceWrapper>(); 
    private Vector<AbstractBusinessDevicePool> businessPoolList = new Vector<AbstractBusinessDevicePool>();
    
    public final static OnlineDevicePool instance = new OnlineDevicePool();
    
    
    private OnlineDevicePool()
    {
    	startTimers();
    }
    
    private void checkDeviceMap(HashMap<String, IDeviceWrapper> deviceMap)
    {
    	logger.debug("Start to check inactive connections.");
    	Iterator<Entry<String, IDeviceWrapper>> entryKeyIterator = deviceMap.entrySet().iterator();
        IDeviceWrapper deviceWrapper;
        long lastTime;
        long now = System.currentTimeMillis();
		while (entryKeyIterator.hasNext())
		{
			Entry<String, IDeviceWrapper> e = entryKeyIterator.next();
			deviceWrapper = e.getValue();
			
			lastTime = deviceWrapper.getLastPingTime().getTime();
			if ((now - lastTime) > GlobalSetting.TimeOut.OnlineDeviceConnection)
			{
				releaseDevice(deviceWrapper);
				continue;
			}
			
			lastTime = deviceWrapper.getLastActivityTime().getTime();
			if ((now - lastTime) > GlobalSetting.TimeOut.DeviceInIdel)
			{
				releaseDevice(deviceWrapper);
				continue;
			}
		}
    }
    /** */
    private void checkInactiveDevice()
    {
    	checkDeviceMap(this.deviceMap);
    	checkDeviceMap(this.tmpDeviceMap);
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
    public boolean isDeviceInPool(String deviceSn)
    {
        return deviceMap.containsKey(deviceSn);
    }
    
    /** */
    public IDeviceWrapper newDevice(IBaseConnection connection)
    {
    	logger.debug("Create device bases on connection with id: {}", connection.getConnectionId());
    	DeviceWrapper deviceWrapper = new DeviceWrapper(connection);
    	deviceWrapper.bindOnlineDevicePool(this);
    	Date now = DateUtil.getNowDate();
    	deviceWrapper.setLastActivityTime(now);
    	deviceWrapper.setLastPingTime(now);
    	
    	connection.bind(deviceWrapper);
    	tmpDeviceMap.put(connection.getConnectionId(), deviceWrapper);
        return deviceWrapper;
    }
    
    
    /** */
    public void releaseDevice(IDeviceWrapper deviceWrapper)
    {
    	if (deviceWrapper == null)
    	{
    		return;
    	}
    	
    	String sn = deviceWrapper.getDeviceSn();
    	
    	if (sn != null && sn.length() > 0)
    	{
    		if (deviceMap.containsKey(sn))
    		{
	    		deviceMap.remove(sn);
	    		deviceWrapper.unbindOnlineDevicePool();
	    		logger.debug("Device <{}> was removed from online device pool.", sn);
    		}
    		else
    		{
    			logger.error("The device <{}> to be removed is not in online device pool.");
    		}
    	}
    	else
    	{
	    	// Check if device exists in tmpDeviceMap
	    	String id = ((IBaseConnectionOwner)deviceWrapper).getConnection().getConnectionId();
	    	if (tmpDeviceMap.containsKey(id))
	    	{
	    		tmpDeviceMap.remove(id);
	    		logger.debug("Device with connection id {} was removed from online device pool.", id);
	    	}
	    	else
	    	{
	    		logger.error("The device with connection id {} to be removed is not in online device pool.");
	    	}
    	}    			
    	
    	if ((deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.WaitMatch)
    			|| (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound))
    	{
    		for (AbstractBusinessDevicePool pool : businessPoolList)
    		{
    			pool.deviceLeave(deviceWrapper);
    		}
    	}
    }
    
    /** */
    public void removeBusinessPool(IBusinessPool businessPool)
    {
    	for (AbstractBusinessDevicePool pool : businessPoolList)
		{
			if (pool == businessPool)
			{
				businessPoolList.remove(businessPool);
			}
		}
    }
    
    public void startTimers()
    {
    	inactiveTimer.scheduleAtFixedRate(new InactiveCheckTask(), DateUtil.getNowDate(), GlobalSetting.TimeOut.OnlineDeviceConnection);
    	logger.debug("Timers of online device pool started.");
    }
    
    public void stopTimers()
    {
    	inactiveTimer.cancel();
    	logger.debug("Timers of online device pool stopped.");
    }
    
    /**
     * DeviceWrapper finished sync device successfully, 
     * and will be moved from tempDeviceMap to deviceMap
     * 
     * @param deviceWrapper: device which finished synchronize device
     */
    public void synchronizeDevice(DeviceWrapper deviceWrapper)
    {
    	IBaseConnection connection = deviceWrapper.getConnection();
    	if (!tmpDeviceMap.containsKey(connection.getConnectionId()))
    	{
    		return;
    	}
    	
    	if (deviceWrapper.getDevice() == null)
    	{
    		return;
    	}
    	
    	if (deviceMap.containsKey(deviceWrapper.getDeviceSn()))
    	{
    		return;
    	}
    	
    	synchronized(tmpDeviceMap)
    	{
    		tmpDeviceMap.remove(connection.getConnectionId());
    	}
    	
    	synchronized(deviceMap)
    	{
    		deviceMap.put(deviceWrapper.getDeviceSn(), deviceWrapper);
    	}
    }

	@Override
	public int getElementCount()
	{
		return deviceMap.size() + tmpDeviceMap.size();
	}

	@Override
	public void clearPool()
	{
		deviceMap.clear();
		tmpDeviceMap.clear();
	}

	@Override
	public boolean isPoolFull()
	{
		return ((tmpDeviceMap.size() + deviceMap.size()) >= capacity);
	}
	
	@Override
	public IDeviceWrapper getDevice(String deviceSnOrConnectionId)
    {
		if (deviceMap.containsKey(deviceSnOrConnectionId))
		{
			return deviceMap.get(deviceSnOrConnectionId);
		}
		else if (tmpDeviceMap.containsKey(deviceSnOrConnectionId))
		{
			return tmpDeviceMap.get(deviceSnOrConnectionId);
		}
		else
		{
			return null;
		}
    }
}

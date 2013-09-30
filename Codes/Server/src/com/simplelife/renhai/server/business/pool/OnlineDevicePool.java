/**
 * OnlineDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.business.session.BusinessSession;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBusinessSession;
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
	
	private class BannedCheckTask extends TimerTask
    {
		@Override
		public void run()
		{
			OnlineDevicePool.instance.deleteBannedDevice();
		}
    }
	
	private Timer inactiveTimer = new Timer();
	private Timer bannedTimer = new Timer();
    private HashMap<String, IDeviceWrapper> queueDeviceMap = new HashMap<String, IDeviceWrapper>();
    private HashMap<Consts.BusinessType, AbstractBusinessDevicePool> businessPoolMap = new HashMap<Consts.BusinessType, AbstractBusinessDevicePool>();
    private List<IDeviceWrapper> bannedDeviceList = new ArrayList<IDeviceWrapper> ();
    
    public final static OnlineDevicePool instance = new OnlineDevicePool();
    
    
    private OnlineDevicePool()
    {
    	startTimers();
    	this.addBusinessPool(Consts.BusinessType.Random, new RandomBusinessDevicePool());
    	this.addBusinessPool(Consts.BusinessType.Interest, new InterestBusinessDevicePool());
    	
    	setCapacity(GlobalSetting.BusinessSetting.OnlinePoolCapacity);
    }
    
    private void checkDeviceMap(HashMap<String, IDeviceWrapper> deviceMap)
    {
    	logger.debug("Start to check inactive connections.");
    	Iterator<Entry<String, IDeviceWrapper>> entryKeyIterator = deviceMap.entrySet().iterator();
        IDeviceWrapper deviceWrapper;
        long lastPingTime;
        long lastActivityTime;
        long now = System.currentTimeMillis();
		while (entryKeyIterator.hasNext())
		{
			Entry<String, IDeviceWrapper> e = entryKeyIterator.next();
			deviceWrapper = e.getValue();
			
			
			lastActivityTime = deviceWrapper.getLastActivityTime().getTime();
			if ((now - lastActivityTime) > GlobalSetting.TimeOut.DeviceInIdle)
			{
				logger.debug("Device with connection id {} was removed from online device pool due to last activity time is: " + DateUtil.getDateStringByLongValue(deviceWrapper.getLastActivityTime().getTime())
						, deviceWrapper.getConnection().getConnectionId());
				deleteDevice(deviceWrapper);
				continue;
			}
			
			lastPingTime = deviceWrapper.getLastPingTime().getTime();
			//String temp = "last ping time: " + lastTime + ", now: " + now + ", diff: " + (now - lastTime) + ", setting: " + GlobalSetting.TimeOut.OnlineDeviceConnection;
			//logger.debug(temp);
			if ((now - lastPingTime) > GlobalSetting.TimeOut.OnlineDeviceConnection)
			{
				if ((now - lastActivityTime) < GlobalSetting.TimeOut.OnlineDeviceConnection)
				{
					// The extreme case of there is activity but no ping 
					continue;
				}
				logger.debug("Device with connection id {} was removed from online device pool due to last ping time is: " + DateUtil.getDateStringByLongValue(deviceWrapper.getLastPingTime().getTime()),
						deviceWrapper.getConnection().getConnectionId());
				deleteDevice(deviceWrapper);
				continue;
			}
		}
    }
    /** */
    private void checkInactiveDevice()
    {
    	synchronized(deviceMap)
    	{
    		checkDeviceMap(this.deviceMap);
    	}
    	
    	synchronized(queueDeviceMap)
    	{
    		checkDeviceMap(this.queueDeviceMap);
    	}
	}
    
    /** */
    public void addBusinessPool(Consts.BusinessType type, AbstractBusinessDevicePool pool)
    {
    	businessPoolMap.put(type, pool);
    }
    
    /** */
    public void removeBusinessPool(Consts.BusinessType type)
    {
    	businessPoolMap.remove(type);
    }
    
    public AbstractBusinessDevicePool getBusinessPool(Consts.BusinessType type)
    {
    	return businessPoolMap.get(type);
    }
    
    /** */
    public boolean isDeviceInPool(String deviceSn)
    {
        return deviceMap.containsKey(deviceSn);
    }
    
    /** */
    public IDeviceWrapper newDevice(IBaseConnection connection)
    {
    	if (this.isPoolFull())
    	{
    		logger.warn("Online device pool is full and request of new device was rejected.");
    		return null;
    	}
    	
    	logger.debug("Create device bases on connection with id: {}", connection.getConnectionId());
    	DeviceWrapper deviceWrapper = new DeviceWrapper(connection);
    	deviceWrapper.bindOnlineDevicePool(this);
    	Date now = DateUtil.getNowDate();
    	deviceWrapper.updateActivityTime();
    	deviceWrapper.updatePingTime();
    	
    	connection.bind(deviceWrapper);
    	queueDeviceMap.put(connection.getConnectionId(), deviceWrapper);
        return deviceWrapper;
    }
    
    public int getDeviceCountInChat()
    {
    	int count = 0;
    	for (AbstractBusinessDevicePool pool : this.businessPoolMap.values())
    	{
    		count += pool.getDeviceCountInChat();
    	}
        return count;
    }
    
    /** */
    public void deleteDevice(IDeviceWrapper deviceWrapper)
    {
    	if (deviceWrapper == null)
    	{
    		return;
    	}
    	
    	deviceWrapper.unbindOnlineDevicePool();
    	
    	Consts.BusinessStatus status = deviceWrapper.getBusinessStatus();
    	IBusinessSession session = deviceWrapper.getOwnerBusinessSession();
    	
    	deviceWrapper.unbindOnlineDevicePool();
    	if (status == Consts.BusinessStatus.Init)
    	{
    		String id = deviceWrapper.getConnection().getConnectionId();
    		if (queueDeviceMap.containsKey(id))
    		{
	    		synchronized(queueDeviceMap)
	    		{
	    			queueDeviceMap.remove(id);
	    		}
	    		logger.debug("Device <{}> was removed from queueDeviceMap of online device pool.", id);
    		}
    	}
    	else
    	{
    		String sn = deviceWrapper.getDeviceSn();
    		if (deviceMap.containsKey(sn))
    		{
	    		synchronized(deviceMap)
				{
					deviceMap.remove(sn);
				}
	    		logger.debug("Device <{}> was removed from deviceMap of online device pool.", sn);
    		}
    		
    		if ((status == Consts.BusinessStatus.WaitMatch)
        			|| (status == Consts.BusinessStatus.SessionBound))
        	{
    			for (Consts.BusinessType type: Consts.BusinessType.values())
        		{
        			AbstractBusinessDevicePool pool = this.getBusinessPool(type);
        			if (pool != null)
        			{
        				pool.onDeviceLeave(deviceWrapper);
        			}
        		}
    			
    			if (session != null)
    			{
    				session.notifyDevices(deviceWrapper, Consts.NotificationType.OthersideLost);
    			}
        	}
    	}
    }
    
    
    
    public void startTimers()
    {
    	inactiveTimer.scheduleAtFixedRate(new InactiveCheckTask(), DateUtil.getNowDate(), GlobalSetting.TimeOut.OnlineDeviceConnection);
    	bannedTimer.scheduleAtFixedRate(new BannedCheckTask(), DateUtil.getNowDate(), GlobalSetting.TimeOut.OnlineDeviceConnection);
    	logger.debug("Timers of online device pool started.");
    }
    
    public void stopTimers()
    {
    	inactiveTimer.cancel();
    	bannedTimer.cancel();
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
    	if (!queueDeviceMap.containsKey(connection.getConnectionId()))
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
    	
    	synchronized(queueDeviceMap)
    	{
    		queueDeviceMap.remove(connection.getConnectionId());
    	}
    	
    	synchronized(deviceMap)
    	{
    		deviceMap.put(deviceWrapper.getDeviceSn(), deviceWrapper);
    	}
    }

	@Override
	public int getElementCount()
	{
		return deviceMap.size() + queueDeviceMap.size();
	}

	@Override
	public void clearPool()
	{
		deviceMap.clear();
		queueDeviceMap.clear();
	}

	@Override
	public boolean isPoolFull()
	{
		return ((queueDeviceMap.size() + deviceMap.size()) >= capacity);
	}
	
	@Override
	public IDeviceWrapper getDevice(String deviceSnOrConnectionId)
    {
		if (deviceMap.containsKey(deviceSnOrConnectionId))
		{
			return deviceMap.get(deviceSnOrConnectionId);
		}
		else if (queueDeviceMap.containsKey(deviceSnOrConnectionId))
		{
			return queueDeviceMap.get(deviceSnOrConnectionId);
		}
		else
		{
			return null;
		}
    }
	
	/**
	 * Identify banned device for delete after a while
	 * @param device
	 */
	public void IdentifyBannedDevice(IDeviceWrapper device)
	{
		logger.debug("Device <{}> was identified as banned device", device.getDeviceSn());
		synchronized(queueDeviceMap)
		{
			queueDeviceMap.remove(device.getConnection().getConnectionId());
		}
		synchronized(bannedDeviceList)
		{
			bannedDeviceList.add(device);
		}
	}
	
	public void deleteBannedDevice()
	{
		if (bannedDeviceList.size() > 0)
		{
			logger.debug("Start to delete {} banned devices", bannedDeviceList.size());
		}
		
		IDeviceWrapper device;
		while (bannedDeviceList.size() > 0)
		{
			device = bannedDeviceList.remove(0);
			device.unbindOnlineDevicePool();
		}
	}
}

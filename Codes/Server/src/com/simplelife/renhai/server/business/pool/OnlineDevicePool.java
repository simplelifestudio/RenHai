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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.Systemstatistics;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessStatus;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class OnlineDevicePool extends AbstractDevicePool
{
	private Logger logger = BusinessModule.instance.getLogger();
	
	private class InactiveCheckTask extends TimerTask
    {
		@Override
		public void run()
		{
			try
			{
				//Session hibernateSesion = HibernateSessionFactory.getSession();
				Thread.currentThread().setName("InactiveCheck");
				OnlineDevicePool.instance.checkInactiveDevice();
				//HibernateSessionFactory.closeSession();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
    }
	
	private class BannedCheckTask extends TimerTask
    {
		@Override
		public void run()
		{
			try
			{
				//Session hibernateSesion = HibernateSessionFactory.getSession();
				Thread.currentThread().setName("BannedCheck");
				OnlineDevicePool.instance.deleteBannedDevice();
				//HibernateSessionFactory.closeSession();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
    }
	
	private class StatSaveTask extends TimerTask
    {
		@Override
		public void run()
		{
			try
			{
				Thread.currentThread().setName("StatSave");
				OnlineDevicePool.instance.saveStatistics();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
    }
	
	private Timer inactiveTimer = new Timer();
	private Timer bannedTimer = new Timer();
	private Timer statSaveTimer = new Timer();
    private ConcurrentHashMap<String, IDeviceWrapper> queueDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
    private HashMap<Consts.BusinessType, AbstractBusinessDevicePool> businessPoolMap = new HashMap<Consts.BusinessType, AbstractBusinessDevicePool>();
    private ConcurrentLinkedQueue<IDeviceWrapper> bannedDeviceList = new ConcurrentLinkedQueue<IDeviceWrapper> ();
    
    public final static OnlineDevicePool instance = new OnlineDevicePool();
    
    private OnlineDevicePool()
    {
    	this.addBusinessPool(Consts.BusinessType.Random, new RandomBusinessDevicePool());
    	this.addBusinessPool(Consts.BusinessType.Interest, new InterestBusinessDevicePool());
    	setCapacity(GlobalSetting.BusinessSetting.OnlinePoolCapacity);
    }
    
    private void checkDeviceMap(ConcurrentHashMap<String, IDeviceWrapper> deviceMap)
    {
    	Iterator<Entry<String, IDeviceWrapper>> entryKeyIterator = deviceMap.entrySet().iterator();
        IDeviceWrapper deviceWrapper;
        long lastPingTime;
        long lastActivityTime;
        long now = System.currentTimeMillis();
		while (entryKeyIterator.hasNext())
		{
			Entry<String, IDeviceWrapper> e = entryKeyIterator.next();
			deviceWrapper = e.getValue();
			
			lastActivityTime = deviceWrapper.getLastActivityTime();
			if ((lastActivityTime > 0) && ((now - lastActivityTime) > GlobalSetting.TimeOut.DeviceInIdle))
			{
				logger.debug("Device with connection id {} will be removed from online device pool due to last activity time is: " + DateUtil.getDateStringByLongValue(deviceWrapper.getLastActivityTime())
						, deviceWrapper.getConnection().getConnectionId());
				
				deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.Offline, Consts.StatusChangeReason.TimeoutOfActivity);
				//deleteDevice(deviceWrapper, Consts.StatusChangeReason.TimeoutOfActivity);
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
				logger.debug("Device with connection id {} will be removed from online device pool due to last ping time is: " 
						+ DateUtil.getDateStringByLongValue(deviceWrapper.getLastPingTime().getTime()) + ", id in map: " + e.getKey(),
						deviceWrapper.getConnection().getConnectionId());
				//deleteDevice(deviceWrapper, Consts.StatusChangeReason.TimeoutOfPing);
				deviceWrapper.changeBusinessStatus(Consts.BusinessStatus.Offline, Consts.StatusChangeReason.TimeoutOfPing);
				continue;
			}
		}
    }
    /** */
    private void checkInactiveDevice()
    {
    	logger.debug("Start to check inactive connections.");
    	checkDeviceMap(this.deviceMap);
    	checkDeviceMap(this.queueDeviceMap);
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
    	
    	DeviceWrapper deviceWrapper = new DeviceWrapper(connection);
    	deviceWrapper.bindOnlineDevicePool(this);
    	deviceWrapper.updateActivityTime();
    	deviceWrapper.updatePingTime();
    	
    	String id = connection.getConnectionId();
    	queueDeviceMap.put(id, deviceWrapper);
    	
    	logger.debug("Save connection {} in OnlineDevicePool", id);
    	/*
    	DbLogger.saveSystemLog(Consts.OperationCode.SetupWebScoket_1001
    			, Consts.SystemModule.business
    			, id);
    	*/
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
    public void deleteDevice(IDeviceWrapper deviceWrapper, Consts.StatusChangeReason reason)
    {
    	if (deviceWrapper == null)
    	{
    		logger.error("Fatal error, device to be deleted is null");
    		return;
    	}
    	
    	Consts.BusinessStatus status = deviceWrapper.getBusinessStatus();
    	
    	logger.debug("Start to remove device <{}> from OnlineDevicePool, device status: " + status.name(), deviceWrapper.getDeviceSn());
    	
    	if (status == Consts.BusinessStatus.Init)
    	{
    		String id = deviceWrapper.getConnection().getConnectionId();
    		if (queueDeviceMap.containsKey(id))
    		{
	    		queueDeviceMap.remove(id);
	    		logger.debug("Device <{}> was removed from queueDeviceMap of online device pool, device count after remove: " + getElementCount(), id);
    		}
    		else
    		{
    			logger.error("Device with id <> shall be in queueDeviceMap but it can't be found in queueDeviceMap!", id);
    		}
    	}
    	else
    	{
    		String sn = deviceWrapper.getDeviceSn();
    		if (deviceMap.containsKey(sn))
    		{
	    		deviceMap.remove(sn);
	    		logger.debug("Device <{}> was removed from deviceMap of online device pool, device count after remove: " + getElementCount(), sn);
    		}
    		else
    		{
    			logger.error("Device <{}> shall be in deviceMap but it doesn't!", sn);
    		}
    		
    		/*
    		if ((status == Consts.BusinessStatus.WaitMatch)
        			|| (status == Consts.BusinessStatus.SessionBound))
        	{
    			logger.debug("Notify business device pools to delete device <{}> as it's also in business pool", sn);
    			for (Consts.BusinessType type: Consts.BusinessType.values())
        		{
        			AbstractBusinessDevicePool pool = this.getBusinessPool(type);
        			if (pool != null)
        			{
        				pool.onDeviceLeave(deviceWrapper, reason);
        			}
        		}
        	}
        	*/
    	}
    }
    
    public void startTimers()
    {
    	inactiveTimer.scheduleAtFixedRate(new InactiveCheckTask(), DateUtil.getNowDate(), GlobalSetting.TimeOut.OnlineDeviceConnection);
    	bannedTimer.scheduleAtFixedRate(new BannedCheckTask(), DateUtil.getNowDate(), GlobalSetting.TimeOut.OnlineDeviceConnection);
    	statSaveTimer.scheduleAtFixedRate(new StatSaveTask(), DateUtil.getNowDate(), GlobalSetting.TimeOut.SaveStatistics);
    	logger.debug("Timers of online device pool started.");
    }
    
    public void stopTimers()
    {
    	inactiveTimer.cancel();
    	bannedTimer.cancel();
    	statSaveTimer.cancel();
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
    	String connectionId = connection.getConnectionId();
    	if (!queueDeviceMap.containsKey(connectionId))
    	{
    		return;
    	}
    	
    	if (deviceWrapper.getDevice() == null)
    	{
    		return;
    	}
    	
    	String deviceSn = deviceWrapper.getDeviceSn();
    	if (deviceMap.containsKey(deviceSn))
    	{
    		logger.debug("Device <{}> has been in deviceMap", deviceSn);
    		IDeviceWrapper preDevice = deviceMap.get(deviceSn);
    		String previousId = preDevice.getConnection().getConnectionId(); 
    		if (!previousId.equals(connectionId))
    		{
    			// If receive AppDataSyncRequest from new WebsocketConnection, close the previous one
    			logger.debug("Found same deviceSn <{}> on different Websocket connection, close the previous one: " + previousId, deviceSn);
    			//preDevice.getConnection().closeConnection();
    			// 2013-10-15, delete device due to it's hard for app to recover to status before connection loss
    			//deleteDevice(preDevice, StatusChangeReason.WebsocketClosedByServer);
    			preDevice.changeBusinessStatus(Consts.BusinessStatus.Offline, Consts.StatusChangeReason.WebSocketReconnect);
    		}
    	}
    	
    	queueDeviceMap.remove(connectionId);
    	if (deviceSn == null || deviceSn.length() == 0)
    	{
    		logger.error("Fatal error that device on connection {} has empty deviceSn", connectionId);
    		return;
    	}
    	deviceMap.put(deviceWrapper.getDeviceSn(), deviceWrapper);
    	logger.debug("Create device <{}> bases on connection " + connection, deviceWrapper.getDeviceSn());
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
		int count = this.getElementCount();
		return (count >= capacity);
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
		queueDeviceMap.remove(device.getConnection().getConnectionId());
		
		bannedDeviceList.add(device);
	}
	
	public void deleteBannedDevice()
	{
		if (!bannedDeviceList.isEmpty())
		{
			logger.debug("Start to delete {} banned devices", bannedDeviceList.size());
		}
		
		IDeviceWrapper device;
		while (!bannedDeviceList.isEmpty())
		{
			device = bannedDeviceList.remove();
			device.changeBusinessStatus(BusinessStatus.Offline, StatusChangeReason.BannedDevice);
		}
	}

	private void reportDeviceInMap(ConcurrentHashMap<String, IDeviceWrapper> deviceMap, JSONObject response)
	{
		Iterator<Entry<String, IDeviceWrapper>> entryKeyIterator = deviceMap.entrySet().iterator();
        IDeviceWrapper deviceWrapper;
        while (entryKeyIterator.hasNext())
		{
        	Entry<String, IDeviceWrapper> e = entryKeyIterator.next();
			deviceWrapper = e.getValue();
			
			JSONObject deviceObj = new JSONObject();
        	response.put(deviceWrapper.getDeviceSn(), deviceObj);
        	
        	deviceObj.put(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
        	deviceObj.put(JSONKey.DeviceSn, deviceWrapper.getDeviceSn());
        	
        	if (deviceWrapper.getBusinessType() != null)
        	{
        		deviceObj.put(JSONKey.BusinessType, deviceWrapper.getBusinessType().name());
        	}
        	deviceObj.put(JSONKey.BusinessStatus, deviceWrapper.getBusinessStatus().name());
        	
        	if (deviceWrapper.getBusinessStatus() == Consts.BusinessStatus.SessionBound)
        	{
        		deviceObj.put(JSONKey.SessionId, deviceWrapper.getOwnerBusinessSession().getSessionId());
        		deviceObj.put("businessSessionStatus", deviceWrapper.getOwnerBusinessSession().getStatus().name());
        	}
		}
	}
	public void reportDeviceDetails(JSONObject response)
	{
		reportDeviceInMap(deviceMap, response);
		reportDeviceInMap(queueDeviceMap, response);
	}
	
	public void saveStatistics()
	{
		long now = System.currentTimeMillis();
		
		AbstractBusinessDevicePool randomPool = OnlineDevicePool.instance.getBusinessPool(Consts.BusinessType.Random);
		InterestBusinessDevicePool interestPool = (InterestBusinessDevicePool) OnlineDevicePool.instance.getBusinessPool(Consts.BusinessType.Interest);
		
		Systemstatistics statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.OnlineDeviceCount.getValue());
		statItem.setCount(OnlineDevicePool.instance.getElementCount());
		DAOWrapper.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.RandomDeviceCount.getValue());
		statItem.setCount(randomPool.getElementCount());
		DAOWrapper.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.InterestDeviceCount.getValue());
		statItem.setCount(interestPool.getElementCount());
		DAOWrapper.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.ChatDeviceCount.getValue());
		statItem.setCount(OnlineDevicePool.instance.getDeviceCountInChat());
		DAOWrapper.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.RandomChatDeviceCount.getValue());
		statItem.setCount(randomPool.getDeviceCountInChat());
		DAOWrapper.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.InterestChatDeviceCount.getValue());
		statItem.setCount(interestPool.getDeviceCountInChat());
		DAOWrapper.cache(statItem);
	}
}

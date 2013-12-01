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
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.PingActionType;
import com.simplelife.renhai.server.util.Consts.StatusChangeReason;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class OnlineDevicePool extends AbstractDevicePool
{
	private Logger logger = BusinessModule.instance.getLogger();
	protected ConcurrentHashMap<String, IDeviceWrapper> appDataSyncedDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
	
	private class InactiveCheckTask extends TimerTask
    {
		@Override
		public void run()
		{
			try
			{
				//Session hibernateSesion = HibernateSessionFactory.getSession();
				//Thread.currentThread().setName("InactiveCheck");
				//OnlineDevicePool.instance.checkInactiveDevice();
				//HibernateSessionFactory.closeSession();
				PingActionQueue.instance.newAction(PingActionType.CheckInactivity, null);
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
    private ConcurrentHashMap<String, IDeviceWrapper> connectedDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
    private HashMap<Consts.BusinessType, AbstractBusinessDevicePool> businessPoolMap = new HashMap<Consts.BusinessType, AbstractBusinessDevicePool>();
    private ConcurrentLinkedQueue<IDeviceWrapper> bannedDeviceList = new ConcurrentLinkedQueue<IDeviceWrapper> ();
    
    public final static OnlineDevicePool instance = new OnlineDevicePool();
    
    private OnlineDevicePool()
    {
    	this.addBusinessPool(Consts.BusinessType.Random, new RandomBusinessDevicePool());
    	this.addBusinessPool(Consts.BusinessType.Interest, new InterestBusinessDevicePool());
    	setCapacity(GlobalSetting.BusinessSetting.OnlinePoolCapacity);
    }
    
    /*
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
				
				deviceWrapper.changeBusinessStatus(Consts.DeviceStatus.Disconnected, Consts.StatusChangeReason.TimeoutOfActivity);
				//deleteDevice(deviceWrapper, Consts.StatusChangeReason.TimeoutOfActivity);
				continue;
			}
			
			lastPingTime = deviceWrapper.getLastPingTime();
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
						+ DateUtil.getDateStringByLongValue(deviceWrapper.getLastPingTime()) + ", id in map: " + e.getKey(),
						deviceWrapper.getConnection().getConnectionId());
				//deleteDevice(deviceWrapper, Consts.StatusChangeReason.TimeoutOfPing);
				deviceWrapper.changeBusinessStatus(Consts.DeviceStatus.Disconnected, Consts.StatusChangeReason.TimeoutOfPing);
				continue;
			}
		}
    }
    */
    
    /*
    private void checkInactiveDevice()
    {
    	logger.debug("Start to check inactive connections.");
    	checkDeviceMap(this.appDataSyncedDeviceMap);
    	checkDeviceMap(this.connectedDeviceMap);
	}
	*/
    
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
        return appDataSyncedDeviceMap.containsKey(deviceSn);
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
    	connectedDeviceMap.put(id, deviceWrapper);
    	elementCount++;
    	
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
    	
    	Consts.DeviceStatus status = deviceWrapper.getBusinessStatus();
    	
    	logger.debug("Start to remove device <{}> from OnlineDevicePool, device status: " + status.name(), deviceWrapper.getDeviceSn());
    	
    	if (status == Consts.DeviceStatus.Connected)
    	{
    		String id = deviceWrapper.getConnection().getConnectionId();
    		if (connectedDeviceMap.containsKey(id))
    		{
	    		connectedDeviceMap.remove(id);
	    		elementCount--;
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
    		if (appDataSyncedDeviceMap.containsKey(sn))
    		{
	    		appDataSyncedDeviceMap.remove(sn);
	    		elementCount--;
	    		logger.debug("Device <{}> was removed from deviceMap of online device pool, device count after remove: " + getElementCount(), sn);
    		}
    		else
    		{
    			logger.warn("Device <{}> may have been removed from OnlineDevicePool when trying to remove it", sn);
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
    
    public void startService()
    {
    	inactiveTimer.scheduleAtFixedRate(new InactiveCheckTask(), GlobalSetting.TimeOut.CheckPingInterval, GlobalSetting.TimeOut.CheckPingInterval);
    	bannedTimer.scheduleAtFixedRate(new BannedCheckTask(), GlobalSetting.TimeOut.OnlineDeviceConnection, GlobalSetting.TimeOut.OnlineDeviceConnection);
    	statSaveTimer.scheduleAtFixedRate(new StatSaveTask(), GlobalSetting.TimeOut.SaveStatistics, GlobalSetting.TimeOut.SaveStatistics);
    	businessPoolMap.get(Consts.BusinessType.Interest).startService();
    	logger.debug("Timers of online device pool started.");
    }
    
    public void stopService()
    {
    	inactiveTimer.cancel();
    	bannedTimer.cancel();
    	statSaveTimer.cancel();
    	businessPoolMap.get(Consts.BusinessType.Interest).stopService();
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
    	if (!connectedDeviceMap.containsKey(connectionId))
    	{
    		return;
    	}
    	
    	if (deviceWrapper.getDevice() == null)
    	{
    		return;
    	}
    	
    	String deviceSn = deviceWrapper.getDeviceSn();
    	if (appDataSyncedDeviceMap.containsKey(deviceSn))
    	{
    		logger.debug("Device <{}> has been in deviceMap", deviceSn);
    		IDeviceWrapper preDevice = appDataSyncedDeviceMap.get(deviceSn);
    		String previousId = preDevice.getConnection().getConnectionId(); 
    		if (!previousId.equals(connectionId))
    		{
    			// If receive AppDataSyncRequest from new WebsocketConnection, close the previous one
    			logger.debug("Found same deviceSn <{}> on different Websocket connection, close the previous one: " + previousId, deviceSn);
    			//preDevice.getConnection().closeConnection();
    			// 2013-10-15, delete device due to it's hard for app to recover to status before connection loss
    			//deleteDevice(preDevice, StatusChangeReason.WebsocketClosedByServer);
    			preDevice.changeBusinessStatus(Consts.DeviceStatus.Disconnected, Consts.StatusChangeReason.WebSocketReconnect);
    		}
    	}
    	
    	connectedDeviceMap.remove(connectionId);
    	if (deviceSn == null || deviceSn.length() == 0)
    	{
    		logger.error("Fatal error that device on connection {} has empty deviceSn", connectionId);
    		return;
    	}
    	appDataSyncedDeviceMap.put(deviceWrapper.getDeviceSn(), deviceWrapper);
    	logger.debug("Create device <{}> bases on connection " + connection, deviceWrapper.getDeviceSn());
    }

    /*
	@Override
	public int getElementCount()
	{
		return appDataSyncedDeviceMap.size() + connectedDeviceMap.size();
	}
	*/

	@Override
	public void clearPool()
	{
		appDataSyncedDeviceMap.clear();
		connectedDeviceMap.clear();
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
		if (appDataSyncedDeviceMap.containsKey(deviceSnOrConnectionId))
		{
			return appDataSyncedDeviceMap.get(deviceSnOrConnectionId);
		}
		else if (connectedDeviceMap.containsKey(deviceSnOrConnectionId))
		{
			return connectedDeviceMap.get(deviceSnOrConnectionId);
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
		connectedDeviceMap.remove(device.getConnection().getConnectionId());
		elementCount--;
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
			device.changeBusinessStatus(DeviceStatus.Disconnected, StatusChangeReason.BannedDevice);
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
        	
        	if (deviceWrapper.getBusinessStatus() == Consts.DeviceStatus.SessionBound)
        	{
        		deviceObj.put(JSONKey.SessionId, deviceWrapper.getOwnerBusinessSession().getSessionId());
        		deviceObj.put("businessSessionStatus", deviceWrapper.getOwnerBusinessSession().getStatus().name());
        	}
		}
	}
	public void reportDeviceDetails(JSONObject response)
	{
		reportDeviceInMap(appDataSyncedDeviceMap, response);
		reportDeviceInMap(connectedDeviceMap, response);
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

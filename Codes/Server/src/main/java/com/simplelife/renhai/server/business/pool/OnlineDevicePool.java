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
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.Consts.DeviceStatus;
import com.simplelife.renhai.server.util.Consts.TimeoutActionType;
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
	private Timer bannedTimer = new Timer();
	private Timer statSaveTimer = new Timer();
	private Timer adjustCountTimer = new Timer();
	private ConcurrentHashMap<String, IDeviceWrapper> connectedDeviceMap = new ConcurrentHashMap<String, IDeviceWrapper>();
    private HashMap<Consts.BusinessType, AbstractBusinessDevicePool> businessPoolMap = new HashMap<Consts.BusinessType, AbstractBusinessDevicePool>();
    private ConcurrentLinkedQueue<IDeviceWrapper> bannedDeviceList = new ConcurrentLinkedQueue<IDeviceWrapper> ();
    
    public final static OnlineDevicePool instance = new OnlineDevicePool();
    public final static TimeoutLink pingLink = new TimeoutLink(GlobalSetting.TimeOut.CheckPingInterval);
    public final static TimeoutLink syncSendingTimeoutLink = new TimeoutLink(GlobalSetting.TimeOut.CheckPingInterval);
    public final static TimeoutLink chatConfirmTimeoutLink = new TimeoutLink(GlobalSetting.TimeOut.CheckPingInterval);
    
    private OnlineDevicePool()
    {
    	this.addBusinessPool(Consts.BusinessType.Random, new RandomBusinessDevicePool());
    	this.addBusinessPool(Consts.BusinessType.Interest, new InterestBusinessDevicePool());
    	setCapacity(GlobalSetting.BusinessSetting.OnlinePoolCapacity);
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
        return appDataSyncedDeviceMap.containsKey(deviceSn);
    }
    
    /** */
    public IDeviceWrapper newDevice(IBaseConnection connection)
    {
    	if (this.isPoolFull())
    	{
    		logger.error("Online device pool is full and request of new device was rejected.");
    		return null;
    	}
    	
    	DeviceWrapper deviceWrapper = new DeviceWrapper(connection);
    	deviceWrapper.bindOnlineDevicePool(this);
    	deviceWrapper.updateActivityTime();
    	deviceWrapper.updatePingTime();
    	
    	String id = connection.getConnectionId();
    	connectedDeviceMap.put(id, deviceWrapper);
    	deviceCount.incrementAndGet();
    	
    	logger.debug("Save connection {} in OnlineDevicePool", id);
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
    	logger.debug("Start to remove device <{}> from OnlineDevicePool, device status: " + status.name(), deviceWrapper.getDeviceIdentification());
    	
    	if (status == Consts.DeviceStatus.Connected)
    	{
    		String id = deviceWrapper.getConnection().getConnectionId();
    		if (connectedDeviceMap.containsKey(id))
    		{
	    		connectedDeviceMap.remove(id);
	    		deviceCount.decrementAndGet();
	    		logger.debug("Device <{}> was removed from queueDeviceMap of online device pool, device count after remove: " + getDeviceCount(), id);
    		}
    		else
    		{
    			logger.error("Device with id <{}> shall be in queueDeviceMap but it can't be found in queueDeviceMap!", id);
    		}
    	}
    	else
    	{
    		String sn = deviceWrapper.getDeviceIdentification();
    		if (appDataSyncedDeviceMap.containsKey(sn))
    		{
	    		appDataSyncedDeviceMap.remove(sn);
	    		deviceCount.addAndGet(-1);
	    		logger.debug("Device <{}> was removed from deviceMap of online device pool, device count after remove: " + getDeviceCount(), sn);
    		}
    		else
    		{
    			logger.warn("Device <{}> may have been removed from OnlineDevicePool when trying to remove it", sn);
    		}
    	}
    }
    
    public void startService()
    {
    	//inactiveTimer.scheduleAtFixedRate(new InactiveCheckTask(), GlobalSetting.TimeOut.CheckPingInterval, GlobalSetting.TimeOut.CheckPingInterval);
    	bannedTimer.scheduleAtFixedRate(new BannedCheckTask(), GlobalSetting.TimeOut.OnlineDeviceConnection, GlobalSetting.TimeOut.OnlineDeviceConnection);
    	statSaveTimer.scheduleAtFixedRate(new StatSaveTask(), GlobalSetting.TimeOut.SaveStatistics, GlobalSetting.TimeOut.SaveStatistics);
    	adjustCountTimer.scheduleAtFixedRate(new AdjustDeviceCountTask(), GlobalSetting.TimeOut.AdjustDeviceCount, GlobalSetting.TimeOut.AdjustDeviceCount);
    	//syncSendingCheckTimer.scheduleAtFixedRate(new SyncSendingCheckTask(), GlobalSetting.TimeOut.CheckPingInterval, GlobalSetting.TimeOut.CheckPingInterval);
    	businessPoolMap.get(Consts.BusinessType.Interest).startService();
    	pingLink.startService();
    	syncSendingTimeoutLink.startService();
    	logger.debug("Timers of online device pool started.");
    }
    
    public void stopService()
    {
    	pingLink.stopService();
    	syncSendingTimeoutLink.stopService();
    	bannedTimer.cancel();
    	statSaveTimer.cancel();
    	adjustCountTimer.cancel();
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
    	
    	String deviceSn = deviceWrapper.getDeviceIdentification();
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
    		deviceCount.decrementAndGet();
    		return;
    	}
    	appDataSyncedDeviceMap.put(deviceWrapper.getDeviceIdentification(), deviceWrapper);
    	logger.debug("Create device <{}> bases on connection " + connection, deviceWrapper.getDeviceIdentification());
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
		int count = this.getDeviceCount();
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
		logger.debug("Device <{}> was identified as banned device", device.getDeviceIdentification());
		connectedDeviceMap.remove(device.getConnection().getConnectionId());
		deviceCount.decrementAndGet();
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
        	response.put(deviceWrapper.getDeviceIdentification(), deviceObj);
        	
        	deviceObj.put(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
        	deviceObj.put(JSONKey.DeviceSn, deviceWrapper.getDeviceIdentification());
        	
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
		statItem.setCount(OnlineDevicePool.instance.getDeviceCount());
		DAOWrapper.instance.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.RandomDeviceCount.getValue());
		statItem.setCount(randomPool.getDeviceCount());
		DAOWrapper.instance.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.InterestDeviceCount.getValue());
		statItem.setCount(interestPool.getDeviceCount());
		DAOWrapper.instance.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.ChatDeviceCount.getValue());
		statItem.setCount(OnlineDevicePool.instance.getDeviceCountInChat());
		DAOWrapper.instance.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.RandomChatDeviceCount.getValue());
		statItem.setCount(randomPool.getDeviceCountInChat());
		DAOWrapper.instance.cache(statItem);
		
		statItem = new Systemstatistics();
		statItem.setSaveTime(now);
		statItem.setStatisticsItemId(Consts.StatisticsItem.InterestChatDeviceCount.getValue());
		statItem.setCount(interestPool.getDeviceCountInChat());
		DAOWrapper.instance.cache(statItem);
	}
	
	@Override
	public void adjustDeviceCount()
	{
		deviceCount.set(connectedDeviceMap.size() + appDataSyncedDeviceMap.size());
	}
	
	private class AdjustDeviceCountTask  extends TimerTask
	{
		public AdjustDeviceCountTask()
		{
			Thread.currentThread().setName("AdjustDeviceCountTask");
		}

		@Override
		public void run()
		{
			try
			{
				logger.debug("Adjust device count of pools");
				OnlineDevicePool.instance.adjustDeviceCount();
				AbstractBusinessDevicePool pool = OnlineDevicePool.instance.getBusinessPool(BusinessType.Interest);
				pool.adjustDeviceCount();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	private class InactiveCheckTask extends TimerTask
    {
		public InactiveCheckTask()
		{
			
		}
		
		@Override
		public void run()
		{
			try
			{
				Thread.currentThread().setName("PingCheckTimer");
				pingLink.checkTimeout();
				//PingActionQueue.instance.newAction(TimeoutActionType.CheckTimeout, null);
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
    }
	
	private class BannedCheckTask extends TimerTask
    {
		public BannedCheckTask()
		{
			
		}
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("BanCheckTimer");
			try
			{
				//Session hibernateSesion = HibernateSessionFactory.getSession();
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
		public StatSaveTask()
		{
			
		}
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("StatSaveTimer");
			try
			{
				OnlineDevicePool.instance.saveStatistics();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
    }
}

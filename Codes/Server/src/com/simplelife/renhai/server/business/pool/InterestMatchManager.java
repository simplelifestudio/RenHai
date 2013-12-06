/**
 * MatchProductor.java
 * 
 * History:
 *     2013-11-30: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IProductor;
import com.simplelife.renhai.server.util.Worker;


/**
 * 
 */
public class InterestMatchManager implements IProductor 
{
	private ConcurrentLinkedQueue<IDeviceWrapper> queue = new ConcurrentLinkedQueue<>();
	protected Worker worker = new Worker(this);
	private SessionManager sessionManager = new SessionManager();
	private Logger logger = BusinessModule.instance.getLogger();
	private InterestBusinessDevicePool pool;
    
	
	public InterestMatchManager()
	{
		worker.setName("InterestMatch");
	}
	public void startService()
	{
		sessionManager.startService();
		worker.startExecution();
	}
	
	public void stopService()
	{
		sessionManager.stopService();
		worker.stopExecution();
	}
	
	public void addDevice(IDeviceWrapper device, InterestBusinessDevicePool pool)
	{
		logger.debug("Add device <{}> to InterestMatchProductor", device.getDeviceIdentification());
		this.pool = pool;
		queue.add(device);
		worker.resumeExecution();
	}
	
	@Override
	public boolean hasWork()
	{
		return !queue.isEmpty();
	}

	@Override
	public Runnable getWork()
	{
		IDeviceWrapper device = queue.remove();
		MatchTask coor = new MatchTask(device, pool);
		return coor;
	}
	
	
	private class MatchTask implements Runnable
	{
		private IDeviceWrapper device;
		private InterestBusinessDevicePool pool;
		private Logger logger = BusinessModule.instance.getLogger();
		public MatchTask(IDeviceWrapper device, InterestBusinessDevicePool pool)
		{
			this.device = device;
			this.pool = pool;
		}
		
		@Override
		public void run()
		{
			logger.debug("Run of MatchCoordinator for device <{}>", device.getDeviceIdentification());
			//logger.debug("====================start to run match manager for device <{}>", device.getDeviceIdentification());
			Collection<Interestlabelmap> maps = device
					.getDevice()
					.getProfile()
					.getInterestCard()
					.getInterestLabelMapSet();
			
			String strLabel;
			for (Interestlabelmap map : maps)
			{
				strLabel = map.getGlobalLabel().getInterestLabelName();
				if (match(strLabel))
				{
					logger.debug("Success to match devices bases on label: {}", strLabel);
					break;
				}
			}
		}
		
		private boolean match(String interestLabel)
		{
			//logger.debug("====================device <{}>, enter match", device.getDeviceIdentification());
			ConcurrentHashMap<String, IDeviceWrapper> interestLabelDeviceMap = pool.getInterestLabelMap();
			if (logger.isDebugEnabled())
	    	{
	    		logger.debug("Start to match device bases on interest label " + interestLabel +" from device <{}>", device.getDeviceIdentification());
	    	}
	    	
	    	if (!interestLabelDeviceMap.containsKey(interestLabel))
	    	{
	    		//logger.debug("====================device <{}>, device list is null, return", device.getDeviceIdentification());
	    		// It may be removed by other devices if all of devices with this label were removed 
	    		logger.debug("Add device <{}> to device interestLabelDeviceMap", device.getDeviceIdentification());
    			interestLabelDeviceMap.put(interestLabel, device);
	    		return false;
	    	}
	    	
	    	IDeviceWrapper tmpDevice = interestLabelDeviceMap.get(interestLabel);
	    	//logger.debug("====================add device <{}> to device list of " + interestLabel, device.getDeviceIdentification());
			if (tmpDevice == device)
			{
				logger.error("Fatal error: device <{}> has been saved by interest label: " + interestLabel, device.getDeviceIdentification());
				return false;
			}
		
			pool.removeInterestIndex(tmpDevice);
			List<IDeviceWrapper> tmpList = new ArrayList<>();
			tmpList.add(tmpDevice);
			tmpList.add(device);
			sessionManager.addDeviceList(tmpList, pool, interestLabel);
			return true;
		}
		
		/*
	    private boolean selectDevicePair(String interestLabel, List<IDeviceWrapper> deviceList)
	    {
	    	logger.debug("====================device <{}>, Enter selectDevicePair", device.getDeviceIdentification());
	    	int deviceCountPerSession = pool.getDeviceCountPerSession();
	    	
	    	int size = deviceList.size();
	    	
	    	if (logger.isDebugEnabled())
	    	{
	    		logger.debug("Select device pair bases on interest label: {}", interestLabel);
	    	}
	    	
	    	if (size < deviceCountPerSession)
	    	{
	    		logger.debug("But there is no enough devices with label {}, it may be caused by concurrent operation");
	    		return false;
	    	}
	    	
	    	logger.debug("====================device <{}>, before create tmpList", device.getDeviceIdentification());
	    	List<IDeviceWrapper> tmpList = new ArrayList<>();
	    	logger.debug("====================device <{}>, after create tmpList", device.getDeviceIdentification());
	    	if (size == deviceCountPerSession)
	    	{
	    		tmpList.addAll(deviceList);
	    		deviceList.clear();
	    	}
	    	else
	    	{
	    		for (int i = 0; i < deviceCountPerSession; i++)
	    		{
	    			tmpList.add(deviceList.remove(0));
	    		}
	    	}
	    	
	    	logger.debug("====================device <{}>, will removeInterestIndex for devices", device.getDeviceIdentification());
	    	for (int i = tmpList.size(); i > 0; i--)
	    	//for (IDeviceWrapper device : tmpList)
	    	{
	    		logger.debug("====================device <{}>, after removeInterestIndex", device.getDeviceIdentification());
	    		pool.removeInterestIndex(device);
	    	}
	    	logger.debug("====================device <{}>, finished removeInterestIndex", device.getDeviceIdentification());
	    	
	    	logger.debug("====================device <{}>, will add tmpList to sessionManager", device.getDeviceIdentification());
	    	sessionManager.addDeviceList(tmpList, pool, interestLabel);
	    	logger.debug("====================device <{}>, tmpList added to sessionManager", device.getDeviceIdentification());
	    	return true;
	    }
	    */
	}
}

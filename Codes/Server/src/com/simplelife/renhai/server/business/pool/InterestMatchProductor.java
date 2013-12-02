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

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IProductor;
import com.simplelife.renhai.server.util.Worker;


/**
 * 
 */
public class InterestMatchProductor implements IProductor 
{
	private ConcurrentLinkedQueue<MatchCoordinator> queue = new ConcurrentLinkedQueue<>();
	protected Worker worker = new Worker(this);
	private SessionProductor sessionProductor = new SessionProductor();
	private Logger logger = BusinessModule.instance.getLogger();
    
	
	public InterestMatchProductor()
	{
		worker.setName("InterestMatch");
	}
	public void startService()
	{
		sessionProductor.startService();
		worker.startExecution();
	}
	
	public void stopService()
	{
		sessionProductor.stopService();
		worker.stopExecution();
	}
	
	public void addDevice(IDeviceWrapper device, InterestBusinessDevicePool pool)
	{
		logger.debug("Add device <{}> to InterestMatchProductor", device.getDeviceSn());
		MatchCoordinator coor = new MatchCoordinator(device, pool);
		queue.add(coor);
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
		return queue.remove();
	}
	
	
	private class MatchCoordinator implements Runnable
	{
		private IDeviceWrapper device;
		private InterestBusinessDevicePool pool;
		private Logger logger = BusinessModule.instance.getLogger();
		public MatchCoordinator(IDeviceWrapper device, InterestBusinessDevicePool pool)
		{
			this.device = device;
			this.pool = pool;
		}
		
		@Override
		public void run()
		{
			logger.debug("Run of MatchCoordinator for device <{}>", device.getDeviceSn());
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
			ConcurrentHashMap<String, List<IDeviceWrapper>> interestLabelDeviceMap = pool.getInterestLabelMap();
			if (logger.isDebugEnabled())
	    	{
	    		logger.debug("Start to match device bases on interest label " + interestLabel +" from device <{}>", device.getDeviceSn());
	    	}
	    	
	    	List<IDeviceWrapper> deviceList = interestLabelDeviceMap.get(interestLabel);
	    	if (deviceList == null)
	    	{
	    		// It may be removed by other devices if all of devices with this label were removed 
	    		logger.debug("Add device <{}> to device list of label " + interestLabel);
	    		deviceList = new ArrayList<IDeviceWrapper>();
    			interestLabelDeviceMap.put(interestLabel, deviceList);
    			deviceList.add(device);
	    		return false;
	    	}
	    	
			if (deviceList.contains(device))
			{
				// It's impossible to match here, because previous match should be successful if devices in list can be matched
				logger.warn("Device <{}> has been in list of interest queue: " + interestLabel, device.getDeviceSn());
				return false;
			}
			
			deviceList.add(device);
		
			if (selectDevicePair(interestLabel, deviceList))
			{
				return true;
			}
			return false;
		}
		
	    private boolean selectDevicePair(String interestLabel, List<IDeviceWrapper> deviceList)
	    {
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
	    	
	    	List<IDeviceWrapper> tmpList = new ArrayList<>();
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
	    	
	    	logger.debug("============devicelist size after append: {}", tmpList.size());
	    	for (IDeviceWrapper device : tmpList)
	    	{
	    		pool.removeInterestIndex(device);
	    	}
	    	
	    	logger.debug("============devicelist size before create SessionCoordinator: {}", tmpList.size());
	    	sessionProductor.addDeviceList(tmpList, pool, interestLabel);
	    	return true;
	    }
	}
}

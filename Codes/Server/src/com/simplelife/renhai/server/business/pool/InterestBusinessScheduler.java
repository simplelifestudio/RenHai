/**
 * InterestBusinessScheduler.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class InterestBusinessScheduler extends AbstractBusinessScheduler
{
	private ConcurrentHashMap<String, ConcurrentSkipListSet<IDeviceWrapper>> interestLabelMap;
	private boolean deadMatchFlag = false; 
	
	@Override
	public void bind(AbstractBusinessDevicePool pool)
    {
    	this.ownerBusinessPool = pool;
    	deviceMap = pool.getDeviceMap();
    	
    	if (pool instanceof InterestBusinessDevicePool)
    	{
    		interestLabelMap = ((InterestBusinessDevicePool)pool).getInterestLabelMap(); 
    	}
    	else
    	{
    		logger.error("Fatal error found that the pool which InterestBusinessScheduler binds is not InterestBusinessDevicePool");
    	}
    }
	
	@Override
	public boolean meetScheduleCondition()
	{
		return ((deviceMap.size() >= deviceCountPerSession));
	}
	
	private LinkedList<String> getRandomLabelSet(Set<Interestlabelmap> labelSet)
	{
		LinkedList<String> orgLabels = new LinkedList<>();
		for (Interestlabelmap map : labelSet)
		{
			orgLabels.add(map.getGlobalLabel().getInterestLabelName());
		}
		
		Random random = new Random();
		LinkedList<String> labels = new LinkedList<>();
		while (orgLabels.size() > 0)
		{
			labels.add(orgLabels.remove(random.nextInt(orgLabels.size())));
		}
		return labels;
	}
	
    /** */
    public void schedule()
    {
    	int deviceMapsize = deviceMap.size();
    	logger.debug("Start to schedule devices in InterestBusinessDevicePool, devices count: " + deviceMapsize);
    	if (deviceMapsize < deviceCountPerSession)
		{
    		logger.debug("There is no enough devices, return directly");
			return;
		}
    	
    	List<String> selectedDevice = new ArrayList<String>();
    	boolean deviceFoundFlag = false;
    	String deviceFoundInterest = null;
    	
    	// Loop all device from deviceMap
		Set<Entry<String, IDeviceWrapper>> entrySet = deviceMap.entrySet();
		Set<Interestlabelmap> labelSet;
		for (Entry<String, IDeviceWrapper> entry : entrySet)
		{
			if (deviceFoundFlag)
			{
				break;
			}
			
			IDeviceWrapper device = entry.getValue();
			labelSet = device.getDevice()
					.getProfile()
					.getInterestCard()
					.getInterestLabelMapSet();
			ConcurrentSkipListSet<IDeviceWrapper> deviceList;
			
			int deviceListSize = 0;
			
			// Loop all interest labels of device 
			LinkedList<String> labels = getRandomLabelSet(labelSet);
			
			for (String strLabel : labels)
			{
				if (deviceFoundFlag)
    			{
    				break;
    			}

				selectedDevice.clear();
				// Try to find device with same interest label
				deviceList = interestLabelMap.get(strLabel);
				deviceListSize = deviceList.size();
				logger.debug("Find {} devices by interest label: " + strLabel, deviceListSize);
				
				// The selected device shall be considered
				//int expectedDeviceCount = deviceCountPerSession - selectedDevice.size() + 1;
				
				// We don't have enough devices have same interest label
				if (deviceListSize < deviceCountPerSession)
				{
					logger.debug("Not enough device count, try next interest label");
					continue;
				}
				
				if ((deviceListSize == deviceCountPerSession))
				{
					String tempSn;
					// All of devices found can be added to this business session
					for (IDeviceWrapper tmpDevice : deviceList)
					{
						tempSn = tmpDevice.getDeviceSn();
						selectedDevice.add(tempSn);
					}
					deviceFoundFlag = true;
				}
				else
				{
					// More devices than expected 
					deviceFoundFlag = selectDevice(deviceList, selectedDevice);
				}
				
				deviceFoundInterest = strLabel;
			}
			
			if (!deviceFoundFlag)
			{
				// Devices in pool can't be matched until new device entered 
				logger.debug("Device <{}> can't be scheduled by its interest labels", device.getDeviceSn());
				continue;
			}
		}
    	
		if (!deviceFoundFlag)
		{
			// Devices in pool can't be matched until new device entered 
			logger.debug("Devices in pool can't be matched by interest labels");
			deadMatchFlag = true;
			return;
		}
		startSession(selectedDevice, deviceFoundInterest);
    }
    
    private boolean selectDevice(
    		ConcurrentSkipListSet<IDeviceWrapper> deviceList,
    		List<String> selectedDevice)
    {
    	Random random = new Random();
    	Object[] deviceArray = deviceList.toArray();
		IDeviceWrapper tempDevice;
		String deviceSn;
		for (int i = 0; i < deviceCountPerSession; i++)
		{
			int tempCount = 0;
			
			do
			{
				tempCount ++;
				tempDevice = (IDeviceWrapper) deviceArray[random.nextInt(deviceArray.length)]; 
				deviceSn = tempDevice.getDeviceSn();
			} while (selectedDevice.contains(deviceSn) && tempCount < 100);
			
			if (tempCount >= 100)
			{
				logger.error("Fatal error: failed to matching device for 100 times, given up");
				deadMatchFlag = true;
				return false;
			}
			selectedDevice.add(deviceSn);
		}
		return true;
    }
    
    private void increaseMatchCount(List<String> selectedDevice, String label)
    {
    	IDeviceWrapper device;
    	for (String deviceSn : selectedDevice)
    	{
    		device = ownerBusinessPool.getDevice(deviceSn); 
    		if (device != null)
    		{
    			device.increaseMatchCount(label);
    		}
    	}
    }
    
    private void startSession(List<String> selectedDevice, String deviceFoundInterest)
    {
    	IBusinessSession session = BusinessSessionPool.instance.getBusinessSession();
		if (session == null)
		{
			logger.debug("No availabel business session.");
			return;
		}
		
		session.bindBusinessDevicePool(this.ownerBusinessPool);
		JSONObject obj = new JSONObject();
		obj.put(JSONKey.InterestLabel, deviceFoundInterest);
		if (session.startSession(selectedDevice, obj))
		{
			DbLogger.increaseInterestMatchCount(deviceFoundInterest);
			increaseMatchCount(selectedDevice, deviceFoundInterest);
		}
		else
		{
			recycleDevice(selectedDevice);
		}
    }
    
    @Override
    public void resumeSchedule()
    {
    	super.resumeSchedule();
    	deadMatchFlag = false;
    }
}

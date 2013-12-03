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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class InterestBusinessScheduler extends AbstractBusinessScheduler
{
	private ConcurrentHashMap<String, List<IDeviceWrapper>> interestLabelDeviceMap;
	private boolean deadMatchFlag = false; 
	
	@Override
	public void bind(AbstractBusinessDevicePool pool)
    {
    	this.ownerBusinessPool = pool;
    	matchStartedDeviceMap = pool.getDeviceMap();
    	
    	if (pool instanceof InterestBusinessDevicePool)
    	{
    		interestLabelDeviceMap = ((InterestBusinessDevicePool)pool).getInterestLabelMap(); 
    	}
    	else
    	{
    		logger.error("Fatal error found that the pool which InterestBusinessScheduler binds is not InterestBusinessDevicePool");
    	}
    }
	
	@Override
	public boolean meetScheduleCondition()
	{
		return ((matchStartedDeviceMap.size() >= deviceCountPerSession));
	}
	
	private LinkedList<String> getRandomLabelSet(Collection<Interestlabelmap> labelSet)
	{
		LinkedList<String> orgLabels = new LinkedList<>();
		for (Interestlabelmap map : labelSet)
		{
			orgLabels.add(map.getGlobalLabel().getInterestLabelName());
		}
		
		Random random = new Random();
		LinkedList<String> labels = new LinkedList<>();
		int tmpSize = orgLabels.size(); 
		while (tmpSize > 0)
		{
			labels.add(orgLabels.remove(random.nextInt(tmpSize)));
			tmpSize--;
		}
		return labels;
	}
	
    /** */
    public void schedule()
    {
    	int deviceMapsize = matchStartedDeviceMap.size();
    	logger.debug("Start to schedule devices in InterestBusinessDevicePool, devices count: " + deviceMapsize);
    	if (deviceMapsize < deviceCountPerSession)
		{
    		logger.debug("There is no enough devices, return directly");
			return;
		}
    	
    	List<IDeviceWrapper> selectedDeviceList = new ArrayList<>();
    	boolean isAllDeviceFound = false;
    	String deviceFoundInterest = null;
    	
    	// Loop all device from deviceMap
		Set<Entry<String, IDeviceWrapper>> entrySet = matchStartedDeviceMap.entrySet();
		Collection<Interestlabelmap> labelSet;
		for (Entry<String, IDeviceWrapper> entry : entrySet)
		{
			if (isAllDeviceFound)
			{
				break;
			}
			
			IDeviceWrapper device = entry.getValue();
			labelSet = device.getDevice()
					.getProfile()
					.getInterestCard()
					.getInterestLabelMapSet();
			
			Collection<IDeviceWrapper> candidateDeviceList;
			
			int deviceListSize = 0;
			
			// Loop all interest labels of device 
			LinkedList<String> labels = getRandomLabelSet(labelSet);
			
			for (String strLabel : labels)
			{
				if (isAllDeviceFound)
    			{
    				break;
    			}

				selectedDeviceList.clear();
				// Try to find device with same interest label
				candidateDeviceList = interestLabelDeviceMap.get(strLabel);
				deviceListSize = candidateDeviceList.size();
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
					// All of devices found can be added to this business session
					for (IDeviceWrapper tmpDevice : candidateDeviceList)
					{
						selectedDeviceList.add(tmpDevice);
					}
					isAllDeviceFound = true;
				}
				else
				{
					// More devices than expected 
					isAllDeviceFound = selectDevice(candidateDeviceList, selectedDeviceList);
				}
				
				deviceFoundInterest = strLabel;
			}
			
			if (!isAllDeviceFound)
			{
				// Devices in pool can't be matched until new device entered 
				logger.debug("Device <{}> can't be scheduled by its interest labels", device.getDeviceSn());
				continue;
			}
		}
    	
		if (!isAllDeviceFound)
		{
			// Devices in pool can't be matched until new device entered 
			logger.debug("Devices in pool can't be matched by interest labels");
			deadMatchFlag = true;
			return;
		}
		startSession(selectedDeviceList, deviceFoundInterest);
    }
    
    private boolean selectDevice(
    		Collection<IDeviceWrapper> deviceList,
    		Collection<IDeviceWrapper> selectedDevice)
    {
    	Random random = new Random();
    	Object[] deviceArray = deviceList.toArray();
		IDeviceWrapper tempDevice;
		for (int i = 0; i < deviceCountPerSession; i++)
		{
			int tempCount = 0;
			
			do
			{
				tempCount ++;
				tempDevice = (IDeviceWrapper) deviceArray[random.nextInt(deviceArray.length)]; 
			} while (selectedDevice.contains(tempDevice) && tempCount < 100);
			
			if (tempCount >= 100)
			{
				logger.error("Fatal error: failed to matching device for 100 times, given up");
				deadMatchFlag = true;
				return false;
			}
			selectedDevice.add(tempDevice);
		}
		return true;
    }
    
    private void increaseMatchCount(Collection<IDeviceWrapper> selectedDevice, String label)
    {
    	for (IDeviceWrapper device : selectedDevice)
    	{
    		if (device != null)
    		{
    			device.increaseMatchCount(label);
    		}
    	}
    }
    
    private void startSession(List<IDeviceWrapper> selectedDevice, String deviceFoundInterest)
    {
    	IBusinessSession session = BusinessSessionPool.instance.getBusinessSession();
		if (session == null)
		{
			logger.debug("No availabel business session.");
			return;
		}
		
		session.bindBusinessDevicePool(this.ownerBusinessPool);
		
		JSONObject obj = null;
		Globalinterestlabel label = DBModule.instance.interestLabelCache.getObject(deviceFoundInterest);
		if (label != null)
		{
			obj = new JSONObject();
			obj.put(JSONKey.GlobalInterestLabelId, label.getGlobalInterestLabelId());
			obj.put(JSONKey.InterestLabelName, label.getInterestLabelName());
			obj.put(JSONKey.GlobalMatchCount, label.getGlobalMatchCount());
		}
		else
		{
			logger.error("Fatal error, global interest label {} can not be found when trying to start session", deviceFoundInterest);
			return;
		}
		
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

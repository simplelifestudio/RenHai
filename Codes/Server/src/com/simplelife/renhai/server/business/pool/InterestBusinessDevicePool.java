/**
 * InterestBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.AbstractLabel;
import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IProductor;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class InterestBusinessDevicePool extends AbstractBusinessDevicePool implements IProductor
{
    /** */
    private ConcurrentHashMap<String, List<IDeviceWrapper>> interestLabelDeviceMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> labelDeviceCountMap = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<SessionCoordinator> sessionCoordicatorQueue = new ConcurrentLinkedQueue<>();
    
    public ConcurrentHashMap<String, List<IDeviceWrapper>> getInterestLabelMap()
    {
    	return interestLabelDeviceMap;
    }
    
    public InterestBusinessDevicePool()
    {
    	businessType = Consts.BusinessType.Interest;
    	
		businessScheduler = new InterestBusinessScheduler();
		businessScheduler.bind(this);
		businessScheduler.setName("InterestScheduler");
		//businessScheduler.startScheduler();
		matchWorker.start();
		
		setCapacity(GlobalSetting.BusinessSetting.InterestBusinessPoolCapacity);
    }
    
    /**
     * Get hot interest labels in InterestBusinessDevicePool
     * @param count: count of hot interest label to be returned
     * @return: link of hot interest label
     */
    public LinkedList<HotLabel> getHotInterestLabel(int count)
    {
    	LinkedList<HotLabel> labelList = new LinkedList<HotLabel>();
    	if (count <= 0)
    	{
    		return labelList;
    	}
    	
    	Set<Entry<String, Integer>> entrySet = labelDeviceCountMap.entrySet();
    	for (Entry<String, Integer> entry : entrySet)
    	{
    		HotLabel hotLabel = new HotLabel();
    		hotLabel.setLabelName(entry.getKey());
    		hotLabel.setProfileCount(entry.getValue());
    		
    		labelList.add(hotLabel);
    	}
    	
    	Collections.sort(labelList);
    	int removeCount = labelList.size() - count;
    	while(removeCount > 0)
    	{
    		labelList.removeLast();
    		removeCount--;
    	}
        return labelList;
    }
    
    /** */
    public LinkedList<AbstractLabel> getHistoryHotInterestLabel(int count)
    {
    	LinkedList<AbstractLabel> labelList = new LinkedList<AbstractLabel>();
    	if (count <= 0)
    	{
    		return labelList;
    	}
    	
    	// TODO: 
        return labelList;
    }
    
    @Override
    public String checkDeviceEnter(IDeviceWrapper device)
    {
    	Collection<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
    	if (labelSet.isEmpty())
    	{
    		String temp = "Device <" + device.getDeviceSn() + "> has no interest label and is not allowed to enter interest business device pool.";
    		logger.error(temp);
    		return temp;
    	}
    	
    	String result = super.checkDeviceEnter(device);
    	if (result != null)
    	{
    		return result;
    	}
    	return null;
    }
    
    /** */
    @Override
    public void onDeviceEnter(IDeviceWrapper device)
    {
    	super.onDeviceEnter(device);
    	increaseLabelDeviceCount(device);
    }
    
    private void decreaseLabelDeviceCount(IDeviceWrapper device)
    {
    	Collection<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
    	if (labelSet.isEmpty())
    	{
    		return;
    	}
    	
    	for (Interestlabelmap label : labelSet)
    	{
    		String strLabel = label.getGlobalLabel().getInterestLabelName();
    		if (this.labelDeviceCountMap.containsKey(strLabel))
    		{
    			Integer count = labelDeviceCountMap.get(strLabel);
    			synchronized (count)
				{
    				if (count == null)
    				{
    					// Protect of multi-accessing
    					continue;
    				}
    				
					count--;
					if (count <= 0)
	    			{
						labelDeviceCountMap.remove(strLabel);
	    			}
					else
					{
						labelDeviceCountMap.put(strLabel, count);
					}
				}
    			
    			
    		}
    		else
    		{
    			labelDeviceCountMap.put(strLabel, 0);
    		}
    	}
    }
    
    private void increaseLabelDeviceCount(IDeviceWrapper device)
    {
    	Collection<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
    	if (labelSet.isEmpty())
    	{
    		return;
    	}
    	
    	for (Interestlabelmap label : labelSet)
    	{
    		String strLabel = label.getGlobalLabel().getInterestLabelName();
    		if (this.labelDeviceCountMap.containsKey(strLabel))
    		{
    			labelDeviceCountMap.put(strLabel, labelDeviceCountMap.get(strLabel) + 1);
    		}
    		else
    		{
    			labelDeviceCountMap.put(strLabel, 1);
    		}
    	}
    }
    
    private void addInterestIndex(IDeviceWrapper device)
    {
    	logger.debug("Add interest index for device <{}>", device.getDeviceSn());
    	Collection<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
    	if (labelSet.isEmpty())
    	{
    		return;
    	}
    	
    	String strLabel;
    	for (Interestlabelmap label : labelSet)
    	{
    		strLabel = label.getGlobalLabel().getInterestLabelName();
    		if (!this.interestLabelDeviceMap.containsKey(strLabel))
    		{
    			//List<IDeviceWrapper> deviceList = Collections.synchronizedList(new ArrayList<IDeviceWrapper>());
    			List<IDeviceWrapper> deviceList = new ArrayList<IDeviceWrapper>();
    			interestLabelDeviceMap.put(strLabel, deviceList);
    			deviceList.add(device);
    		}
    		else
    		{
    			List<IDeviceWrapper> deviceList = interestLabelDeviceMap.get(strLabel);
        		synchronized(deviceList)
        		{
        			if (deviceList.contains(device))
            		{
            			logger.warn("Device <{}> has been in list of interest queue: " + strLabel, device.getDeviceSn());
            			continue;
            		}
        			
        			//logger.debug("==========before add {}", device.getDeviceSn());
	        		deviceList.add(device);
	        		
	        		if (deviceList.size() >= deviceCountPerSession)
	        		{
	        			//logger.debug("==========match devices bases on {}", strLabel);
	        			if (matchDevice(strLabel, deviceList))
	        			{
	        				break;
	        			}
	        		}
        		}
    		}
    	}
    }
    
    private boolean matchDevice(String interestLabel, List<IDeviceWrapper> deviceList)
    {
    	int size = deviceList.size();
    	if (size < deviceCountPerSession)
    	{
    		logger.warn("But there is no enough devices with label {}, it may be caused by concurrent operation");
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
    		removeInterestIndex(device);
    	}
    	
    	logger.debug("============devicelist size before create SessionCoordinator: {}", tmpList.size());
    	SessionCoordinator coor = new SessionCoordinator(tmpList, this, interestLabel);
    	sessionCoordicatorQueue.add(coor);
    	matchWorker.resumeExecution();
    	return true;
    }
    
    private void removeInterestIndex(IDeviceWrapper device)
    {
    	logger.debug("Start to remove interest label index for device <{}>", device.getDeviceSn());
    	
    	if (device.getDevice() == null)
    	{
    		logger.error("Fatal error, DeviceWrapper <{}> has empty device object!");
    		return;
    	}
    	
    	Collection<Interestlabelmap> labelSet = device
    			.getDevice()
    			.getProfile()
    			.getInterestCard()
    			.getInterestLabelMapSet();
    	
    	if (labelSet.isEmpty())
    	{
    		logger.warn("Interest label set of Device <{}> is empty when trying to remove it from interest device pool.", device.getDeviceSn());
    		return;
    	}
    	
    	for (Interestlabelmap label : labelSet)
    	{
    		String strLabel = label.getGlobalLabel().getInterestLabelName();
    		
    		if (!this.interestLabelDeviceMap.containsKey(strLabel))
    		{
    			//logger.error("Fatal error: Interest device list for label <{}> is non-existent", strLabel);
    			continue;
    		}
    		
    		if (logger.isDebugEnabled())
    		{
    			logger.debug("Remove interest label <" + strLabel + "> for device <{}>", device.getDeviceSn());
    		}
    		
    		List<IDeviceWrapper> deviceList = interestLabelDeviceMap.get(strLabel);
    		if (deviceList == null || !deviceList.contains(device))
    		{
    			//logger.warn("Device <{}> is not in list of interest queue: " + strLabel, device.getDeviceSn());
    			continue;
    		}
    		
    		synchronized(deviceList)
    		{
    			deviceList.remove(device);
    		}
    		
    		if (deviceList.isEmpty())
    		{
    			interestLabelDeviceMap.remove(strLabel);
    		}
    	}
    }
    
    /** */
    @Override
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason)
    {
    	logger.debug("Enter onDeviceLeave of InterBusinessDevicePool, Device <{}> to be removed", device.getDeviceSn());
    	String deviceSn = device.getDeviceSn();
    	if (null != this.getDevice(deviceSn))
    	{
    		if (matchStartedDeviceMap.containsKey(deviceSn))
    		{
    			removeInterestIndex(device);
    		}
    		super.onDeviceLeave(device, reason);
    	}
    	
    	decreaseLabelDeviceCount(device);
    }

	@Override
	public void clearPool()
	{
		interestLabelDeviceMap.clear();
		super.clearPool();
	}

	@Override
	public void startChat(IDeviceWrapper device)
	{
		String deviceSn = device.getDeviceSn();
		matchStartedDeviceMap.remove(deviceSn);
		sessionBoundDeviceMap.put(deviceSn, device);
		
		//device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
		//this.removeInterestIndex(device);
	}
	
	@Override
	public void startMatch(IDeviceWrapper device)
    {
		String deviceSn = device.getDeviceSn();
		businessChoosedDeviceMap.remove(deviceSn);
		matchStartedDeviceMap.put(deviceSn, device);
		addInterestIndex(device);
		//businessScheduler.resumeSchedule();
    }

	@Override
	public void endChat(IDeviceWrapper device)
	{
		String sn = device.getDeviceSn();
		boolean existFlag = false;
		if (sessionBoundDeviceMap.containsKey(sn))
		{
			existFlag = true;
			sessionBoundDeviceMap.remove(sn);
		}
		
		if (existFlag)
		{
			// Maybe device has been removed from business device pool by another thread
			//deviceMap.put(sn, device);
			businessChoosedDeviceMap.put(sn, device);
			//device.changeBusinessStatus(Consts.BusinessStatus.WaitMatch);
			//device.unbindBusinessSession();
			//addInterestIndex(device);
		}
	}

	@Override
	public boolean hasWork()
	{
		return !sessionCoordicatorQueue.isEmpty();
	}

	@Override
	public Runnable getWork()
	{
		return sessionCoordicatorQueue.remove();
	}
	
	private class SessionCoordinator implements Runnable
	{
		private List<IDeviceWrapper> selectedDevice;
		private InterestBusinessDevicePool pool;
		private String deviceFoundInterest;
		
		public SessionCoordinator(
				List<IDeviceWrapper> selectedDevice, 
				InterestBusinessDevicePool pool,
				String deviceFoundInterest)
		{
			this.selectedDevice = selectedDevice;
			this.pool = pool;
			this.deviceFoundInterest = deviceFoundInterest;
		}
		
		@Override
		public void run()
		{
			//logger.debug("=============begin of SessionCoordinator.run(), size of deviceList: {}", selectedDevice.size());
			IBusinessSession session = BusinessSessionPool.instance.getBusinessSession();
			if (session == null)
			{
				logger.debug("No availabel business session.");
				return;
			}
			
			session.bindBusinessDevicePool(pool);
			
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
		
		private void recycleDevice(Collection<IDeviceWrapper> selectedDevice)
	    {
	    	logger.debug("Recycle devices due to failure of starting session");
	    	for (IDeviceWrapper device : selectedDevice)
	    	{
	    		if (device != null)
	    		{
	    			pool.endChat(device);
	    		}
	    	}
	    }
	}
}

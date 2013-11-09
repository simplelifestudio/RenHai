/**
 * InterestBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.simplelife.renhai.server.business.device.AbstractLabel;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class InterestBusinessDevicePool extends AbstractBusinessDevicePool
{
    /** */
    private ConcurrentHashMap<String, ConcurrentSkipListSet<IDeviceWrapper>> interestLabelMap = new ConcurrentHashMap<String, ConcurrentSkipListSet<IDeviceWrapper>>();
    public ConcurrentHashMap<String, ConcurrentSkipListSet<IDeviceWrapper>> getInterestLabelMap()
    {
    	return interestLabelMap;
    }
    
    public InterestBusinessDevicePool()
    {
    	businessType = Consts.BusinessType.Interest;
    	
		businessScheduler = new InterestBusinessScheduler();
		businessScheduler.bind(this);
		businessScheduler.setName("InterestScheduler");
		businessScheduler.startScheduler();
		
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
    	
    	Set<Entry<String, ConcurrentSkipListSet<IDeviceWrapper>>> entrySet = interestLabelMap.entrySet();
    	for (Entry<String, ConcurrentSkipListSet<IDeviceWrapper>> entry : entrySet)
    	{
    		HotLabel hotLabel = new HotLabel();
    		hotLabel.setLabelName(entry.getKey());
    		hotLabel.setLabelCount(entry.getValue().size());
    		
    		addToSortedLink(labelList, hotLabel, count);
    	}
        return labelList;
    }
    
    /**
     * Add hotLabel to labelList with order
     * @param labelList: list of hot interest labels
     * @param hotLabel: interest label to be added
     * @param count: max length of @labelList
     */
    private void addToSortedLink(LinkedList<HotLabel> labelList, HotLabel hotLabel, int count)
    {
    	boolean addFlag = false;
    	for (int i = 0; i < labelList.size(); i++)
    	{
    		if (hotLabel.compareTo(labelList.get(i)) > 0)
    		{
    			labelList.add(i, hotLabel);
    			addFlag = true;
    			break;
    		}
    	}
    	
    	if (!addFlag)
    	{
    		labelList.add(hotLabel);
    	}
    	
    	if (labelList.size() > count)
    	{
    		labelList.removeLast();
    	}
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
    	Set<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
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
    }
    
    private void addInterestIndex(IDeviceWrapper device)
    {
    	Set<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
    	if (labelSet.isEmpty())
    	{
    		return;
    	}
    	
    	for (Interestlabelmap label : labelSet)
    	{
    		String strLabel = label.getGlobalLabel().getInterestLabelName();
    		if (!this.interestLabelMap.containsKey(strLabel))
    		{
    			interestLabelMap.put(strLabel, new ConcurrentSkipListSet<IDeviceWrapper>());
    		}
    		
    		ConcurrentSkipListSet<IDeviceWrapper> deviceList = interestLabelMap.get(strLabel);
    		if (deviceList.contains(device))
    		{
    			logger.warn("Device <{}> has been in list of interest queue: " + strLabel, device.getDeviceSn());
    			continue;
    		}
    		
    		deviceList.add(device);
    	}
    }
    
    private void removeInterestIndex(IDeviceWrapper device)
    {
    	logger.debug("Start to remove interest label index for device <{}>", device.getDeviceSn());
    	
    	if (device.getDevice() == null)
    	{
    		logger.error("Fatal error, DeviceWrapper <{}> has empty device object!");
    		return;
    	}
    	
    	Set<Interestlabelmap> labelSet = device
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
    		if (!this.interestLabelMap.containsKey(strLabel))
    		{
    			logger.error("Fatal error: Interest device list for label <{}> is non-existent", strLabel);
    			continue;
    		}
    		
    		ConcurrentSkipListSet<IDeviceWrapper> deviceList = interestLabelMap.get(strLabel);
    		if (!deviceList.contains(device))
    		{
    			logger.warn("Device <{}> is not in list of interest queue: " + strLabel, device.getDeviceSn());
    			continue;
    		}
    		
    		deviceList.remove(device);
    		
    		if (deviceList.isEmpty())
    		{
    			interestLabelMap.remove(strLabel);
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
    		if (deviceMap.containsKey(deviceSn))
    		{
    			removeInterestIndex(device);
    		}
    		super.onDeviceLeave(device, reason);
    	}
    }

	@Override
	public void clearPool()
	{
		interestLabelMap.clear();
		super.clearPool();
	}

	@Override
	public void startChat(IDeviceWrapper device)
	{
		String deviceSn = device.getDeviceSn();
		deviceMap.remove(deviceSn);
		chatDeviceMap.put(deviceSn, device);
		
		device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
		this.removeInterestIndex(device);
	}
	
	@Override
	public void startMatch(IDeviceWrapper device)
    {
		String deviceSn = device.getDeviceSn();
		cacheDeviceMap.remove(deviceSn);
		deviceMap.put(deviceSn, device);
		addInterestIndex(device);
		businessScheduler.resumeSchedule();
    }

	@Override
	public void endChat(IDeviceWrapper device)
	{
		String sn = device.getDeviceSn();
		boolean existFlag = false;
		if (chatDeviceMap.containsKey(sn))
		{
			existFlag = true;
			chatDeviceMap.remove(sn);
		}
		
		if (existFlag)
		{
			// Maybe device has been removed from business device pool by another thread
			//deviceMap.put(sn, device);
			cacheDeviceMap.put(sn, device);
			//device.changeBusinessStatus(Consts.BusinessStatus.WaitMatch);
			//device.unbindBusinessSession();
			//addInterestIndex(device);
		}
	}
}

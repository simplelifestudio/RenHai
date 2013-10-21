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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.device.AbstractLabel;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class InterestBusinessDevicePool extends AbstractBusinessDevicePool
{
    /** */
    private HashMap<String, List<IDeviceWrapper>> interestLabelMap = new HashMap<String, List<IDeviceWrapper>>();
    public HashMap<String, List<IDeviceWrapper>> getInterestLabelMap()
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
    	
    	Set<Entry<String, List<IDeviceWrapper>>> entrySet = interestLabelMap.entrySet();
    	for (Entry<String, List<IDeviceWrapper>> entry : entrySet)
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
    	Set<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestcard().getInterestlabelmaps();
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
    	addInterestIndex(device);
    }
    
    private void addInterestIndex(IDeviceWrapper device)
    {
    	Set<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestcard().getInterestlabelmaps();
    	if (labelSet.isEmpty())
    	{
    		return;
    	}
    	
    	for (Interestlabelmap label : labelSet)
    	{
    		String strLabel = label.getGlobalinterestlabel().getInterestLabelName();
    		if (!this.interestLabelMap.containsKey(strLabel))
    		{
    			interestLabelMap.put(strLabel, new ArrayList<IDeviceWrapper>());
    		}
    		
    		List<IDeviceWrapper> deviceList = interestLabelMap.get(strLabel);
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
    	Set<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestcard().getInterestlabelmaps();
    	if (labelSet.isEmpty())
    	{
    		return;
    	}
    	
    	for (Interestlabelmap label : labelSet)
    	{
    		String strLabel = label.getGlobalinterestlabel().getInterestLabelName();
    		if (!this.interestLabelMap.containsKey(strLabel))
    		{
    			logger.error("Fatal error: Interest device list for label <{}> is non-existent", strLabel);
    			continue;
    		}
    		
    		List<IDeviceWrapper> deviceList = interestLabelMap.get(strLabel);
    		if (!deviceList.contains(device))
    		{
    			logger.warn("Device <{}> is not in list of interest queue: " + strLabel, device.getDeviceSn());
    			continue;
    		}
    		
    		deviceList.remove(device);
    	}
    }
    
    /** */
    @Override
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason)
    {
    	if (null != this.getDevice(device.getDeviceSn()))
    	{
    		if (deviceMap.contains(device.getDeviceSn()))
    		{
    			removeInterestIndex(device);
    		}
    		super.onDeviceLeave(device, reason);
    	}
    }

	@Override
	public void clearPool()
	{
		deviceMap.clear();
		interestLabelMap.clear();
	}

	@Override
	public void startChat(IDeviceWrapper device)
	{
		String deviceSn = device.getDeviceSn();
		deviceMap.remove(deviceSn);
		chatDeviceMap.put(deviceSn, device);
		
		Set<Interestlabelmap> labelSet = device.getDevice().getProfile().getInterestcard().getInterestlabelmaps();
		this.removeInterestIndex(device);
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
			deviceMap.put(sn, device);
			//device.changeBusinessStatus(Consts.BusinessStatus.WaitMatch);
			//device.unbindBusinessSession();
			addInterestIndex(device);
		}
	}
}

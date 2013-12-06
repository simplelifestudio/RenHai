/**
 * InterestBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.simplelife.renhai.server.business.device.AbstractLabel;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class InterestBusinessDevicePool extends AbstractBusinessDevicePool
{
    /** */
    private ConcurrentHashMap<String, IDeviceWrapper> interestLabelDeviceMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> labelDeviceCountMap = new ConcurrentHashMap<>();
    private InterestMatchManager matchManager = new InterestMatchManager();
    
    public ConcurrentHashMap<String, IDeviceWrapper> getInterestLabelMap()
    {
    	return interestLabelDeviceMap;
    }
    
    public void startService()
    {
    	matchManager.startService();
    }
    
    public void stopService()
    {
    	matchManager.stopService();
    }
    
    public InterestBusinessDevicePool()
    {
    	businessType = Consts.BusinessType.Interest;
    	
		//businessScheduler = new InterestBusinessScheduler();
		//businessScheduler.bind(this);
		//businessScheduler.setName("InterestScheduler");
		//businessScheduler.startScheduler();
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
    		String temp = "Device <" + device.getDeviceIdentification() + "> has no interest label and is not allowed to enter interest business device pool.";
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
    
    public void removeInterestIndex(IDeviceWrapper device)
    {
    	logger.debug("Start to remove interest label index for device <{}>", device.getDeviceIdentification());
    	
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
    		logger.warn("Interest label set of Device <{}> is empty when trying to remove it from interest device pool.", device.getDeviceIdentification());
    		return;
    	}
    	
    	for (Interestlabelmap label : labelSet)
    	{
    		String strLabel = label.getGlobalLabel().getInterestLabelName();
    		
    		//logger.debug("====================before check interestLabelDeviceMap for "+strLabel+", device <{}>", device.getDeviceIdentification());
    		if (!this.interestLabelDeviceMap.containsKey(strLabel))
    		{
    			//logger.error("Fatal error: Interest device list for label <{}> is non-existent", strLabel);
    			//logger.debug("====================nonexistent, for "+strLabel+", device <{}>", device.getDeviceIdentification());
    			continue;
    		}
    		//logger.debug("====================after check interestLabelDeviceMap for "+strLabel+", device <{}>", device.getDeviceIdentification());
    		
    		
    		if (logger.isDebugEnabled())
    		{
    			logger.debug("Remove interest label <" + strLabel + "> for device <{}>", device.getDeviceIdentification());
    		}
    		
    		//logger.debug("====================before get deviceList for "+strLabel+", device <{}>", device.getDeviceIdentification());
    		if (!interestLabelDeviceMap.containsKey(strLabel))
    		{
    			//logger.warn("Device <{}> is not in list of interest queue: " + strLabel, device.getDeviceSn());
    			//logger.debug("====================nonexistent for "+strLabel+", device <{}>", device.getDeviceIdentification());
    			continue;
    		}
    		//logger.debug("====================after get deviceList for "+strLabel+", device <{}>", device.getDeviceIdentification());
    		
    		//logger.debug("====================before remove interest label for device <{}>,", device.getDeviceIdentification());
    		
    		if (interestLabelDeviceMap.get(strLabel) == device)
    		{
    			interestLabelDeviceMap.remove(strLabel);
    		}
    	}
    }
    
    /** */
    @Override
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason)
    {
    	logger.debug("Enter onDeviceLeave of InterBusinessDevicePool, Device <{}> to be removed", device.getDeviceIdentification());
    	String deviceSn = device.getDeviceIdentification();
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
		String deviceSn = device.getDeviceIdentification();
		matchStartedDeviceMap.remove(deviceSn);
		sessionBoundDeviceMap.put(deviceSn, device);
		
		//device.getDevice().getProfile().getInterestCard().getInterestLabelMapSet();
		//this.removeInterestIndex(device);
	}
	
	@Override
	public void startMatch(IDeviceWrapper device)
    {
		String deviceSn = device.getDeviceIdentification();
		businessChoosedDeviceMap.remove(deviceSn);
		matchStartedDeviceMap.put(deviceSn, device);
		matchManager.addDevice(device, this);
    }

	@Override
	public void endChat(IDeviceWrapper device)
	{
		String sn = device.getDeviceIdentification();
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
}

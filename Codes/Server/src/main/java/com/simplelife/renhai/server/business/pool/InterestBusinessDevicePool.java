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
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.session.AutoMappingBehavior;

import com.simplelife.renhai.server.business.KeywordFilter;
import com.simplelife.renhai.server.business.device.AbstractLabel;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class InterestBusinessDevicePool extends AbstractBusinessDevicePool
{
    /** */
    private ConcurrentHashMap<String, AtomicInteger> labelDeviceCountMap = new ConcurrentHashMap<>();
    private InterestMatchManager matchManager = new InterestMatchManager(this);
    
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
    	
    	Set<Entry<String, AtomicInteger>> entrySet = labelDeviceCountMap.entrySet();
    	for (Entry<String, AtomicInteger> entry : entrySet)
    	{
    		HotLabel hotLabel = new HotLabel();
    		hotLabel.setLabelName(entry.getKey());
    		hotLabel.setProfileCount(entry.getValue().get());
    		
    		labelList.add(hotLabel);
    	}
    	
    	Collections.sort(labelList);
    	int removeCount = labelList.size() - count;
    	while(removeCount > 0)
    	{
    		labelList.removeLast();
    		removeCount--;
    	}
    	KeywordFilter.filterHotLabel(labelList);
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
    			AtomicInteger count = labelDeviceCountMap.get(strLabel);
    			synchronized (count)
				{
    				if (count == null)
    				{
    					// Protect of multi-accessing
    					continue;
    				}
    				
					count.decrementAndGet();
					if (count.get() <= 0)
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
    			labelDeviceCountMap.get(strLabel).incrementAndGet();
    		}
    		else
    		{
    			labelDeviceCountMap.put(strLabel, new AtomicInteger(1));
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
    			matchManager.removeDevice(device);
    		}
    		super.onDeviceLeave(device, reason);
    	}
    	
    	decreaseLabelDeviceCount(device);
    }

	@Override
	public void clearPool()
	{
		matchManager.clear();
		super.clearPool();
	}

	@Override
	public void startChat(IDeviceWrapper device)
	{
		String deviceSn = device.getDeviceIdentification();
		matchStartedDeviceMap.remove(deviceSn);
		sessionBoundDeviceMap.put(deviceSn, device);
		chatCount.incrementAndGet();
	}
	
	@Override
	public void startMatch(IDeviceWrapper device)
    {
		String deviceSn = device.getDeviceIdentification();
		businessChoosedDeviceMap.remove(deviceSn);
		matchStartedDeviceMap.put(deviceSn, device);
		addDeviceToMatchManager(device);
    }
	
	private void addDeviceToMatchManager(IDeviceWrapper device)
	{
		matchManager.addDevice(device);
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
			chatCount.decrementAndGet();
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

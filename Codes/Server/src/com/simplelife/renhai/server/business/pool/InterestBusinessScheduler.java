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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class InterestBusinessScheduler extends AbstractBusinessScheduler
{
	private HashMap<String, List<IDeviceWrapper>> interestLabelMap;
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
		return ((deviceMap.size() >= deviceCountPerSession)
				&& !deadMatchFlag);
	}
	
    /** */
    public void schedule()
    {
    	logger.debug("Start to schedule devices in InterestBusinessDevicePool.");
    	if (deviceMap.size() < deviceCountPerSession)
		{
    		logger.debug("There is no enough devices, return directly");
			return;
		}
    	
    	List<String> selectedDevice = new ArrayList<String>();
    	boolean deviceFoundFlag = false;
    	
    	Random random = new Random();
		synchronized (deviceMap)
		{
    		// Loop all device from deviceMap
    		Set<Entry<String, IDeviceWrapper>> entrySet = deviceMap.entrySet();
    		Set<Interestlabelmap> labelSet;
    		for (Entry<String, IDeviceWrapper> entry : entrySet)
    		{
    			if (deviceFoundFlag)
    			{
    				break;
    			}
    			
    			selectedDevice.clear();
    			
    			IDeviceWrapper device = entry.getValue();
    			selectedDevice.add(device.getDeviceSn());
    			
    			labelSet = device.getDevice().getProfile().getInterestcard().getInterestlabelmaps();
    			String strLabel; 
    			List<IDeviceWrapper> deviceList;
    			
    			// Loop all interest labels of device 
    			for (Interestlabelmap label : labelSet)
    			{
    				if (deviceFoundFlag)
        			{
        				break;
        			}

    				// Try to find device with same interest label
    				strLabel = label.getGlobalinterestlabel().getInterestLabelName();
    				deviceList = interestLabelMap.get(strLabel);
    				
    				// The selected device shall be considered
    				int expectedDeviceCount = deviceCountPerSession - selectedDevice.size() + 1;
    				
    				// We don't have enough devices have same interest label
    				if (deviceList.size() < expectedDeviceCount)
    				{
    					continue;
    				}
    				
    				if ((deviceList.size() == expectedDeviceCount))
					{
    					String tempSn;
    					// All of devices found can be added to this business session
    					for (IDeviceWrapper tmpDevice : deviceList)
    					{
    						tempSn = tmpDevice.getDeviceSn();
    						if (!selectedDevice.contains(tempSn))
    						{
    							selectedDevice.add(tempSn);
    						}
    					}
					}
    				else
    				{
    					Object[] deviceArray = deviceList.toArray();
    					IDeviceWrapper tempDevice;
        				String deviceSn;
        				for (int i = 0; i < expectedDeviceCount; i++)
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
        						return;
        					}
        					selectedDevice.add(deviceSn);
        				}
    				}
    				deviceFoundFlag = true;
    			}
    			
    			if (!deviceFoundFlag)
    			{
    				// Devices in pool can't be matched by same interest label 
    				deadMatchFlag = true;
    				return;
    			}
    		}
		}
    	
    	IBusinessSession session = BusinessSessionPool.instance.getBusinessSession();
		if (session == null)
		{
			return;
		}
		session.bindBusinessDevicePool(this.ownerBusinessPool);
		session.startSession(selectedDevice);
    }
    
    @Override
    public void signal()
    {
    	super.signal();
    	deadMatchFlag = false;
    }
}

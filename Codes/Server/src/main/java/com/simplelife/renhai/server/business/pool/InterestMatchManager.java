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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.Interestlabelmap;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IProductor;
import com.simplelife.renhai.server.util.Worker;


/**
 * 
 */
public class InterestMatchManager implements IProductor 
{
	private ConcurrentLinkedQueue<Runnable> actionQueue = new ConcurrentLinkedQueue<>();
	protected Worker worker = new Worker(this);
	private SessionManager sessionManager = new SessionManager();
	private Logger logger = BusinessModule.instance.getLogger();
	private InterestBusinessDevicePool pool;
	private HashMap<String, IDeviceWrapper> interestLabelDeviceMap = new HashMap<>();
	
	public InterestMatchManager(InterestBusinessDevicePool pool)
	{
		worker.setName("InterestMatch");
		this.pool = pool;
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
	
	public void addDevice(IDeviceWrapper device)
	{
		logger.debug("Add device <{}> to InterestMatchProductor", device.getDeviceIdentification());
		MatchTask task = new MatchTask(device, pool);
		actionQueue.add(task);
		worker.resumeExecution();
	}
	
	public void removeDevice(IDeviceWrapper device)
	{
		logger.debug("Remove device <{}> from InterestMatchProductor", device.getDeviceIdentification());
		RemoveDeviceTask task = new RemoveDeviceTask(device);
		actionQueue.add(task);
		worker.resumeExecution();
	}
	
	public void clear()
	{
		actionQueue.clear();
		interestLabelDeviceMap.clear();
	}
	
	@Override
	public boolean hasWork()
	{
		return !actionQueue.isEmpty();
	}

	@Override
	public Runnable getWork()
	{
		logger.debug("Count of devices waiting for match: {}", actionQueue.size());
		return actionQueue.remove();
	}
	
	private void removeInterestIndex(IDeviceWrapper device)
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
    		
    		if (!interestLabelDeviceMap.containsKey(strLabel))
    		{
    			//logger.error("Fatal error: Interest device list for label <{}> is non-existent", strLabel);
    			continue;
    		}
    		
    		if (logger.isDebugEnabled())
    		{
    			logger.debug("Remove interest label <" + strLabel + "> for device <{}>", device.getDeviceIdentification());
    		}
    		
    		if (!interestLabelDeviceMap.containsKey(strLabel))
    		{
    			continue;
    		}
    		
    		if (interestLabelDeviceMap.get(strLabel) == device)
    		{
    			interestLabelDeviceMap.remove(strLabel);
    		}
    	}
    }
	
	private class RemoveDeviceTask implements Runnable
	{
		IDeviceWrapper device;
		public RemoveDeviceTask(IDeviceWrapper device)
		{
			this.device = device;
		}
		
		@Override
		public void run()
		{
			try
			{
				removeInterestIndex(device);
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
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
			try
			{
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				logger.debug("Run of MatchCoordinator for device <{}>", device.getDeviceIdentification());
				Collection<Interestlabelmap> maps = device.getDevice().getProfile().getInterestCard()
						.getInterestLabelMapSet();
				
				String strLabel;
				boolean matchFlag = false;
				for (Interestlabelmap map : maps)
				{
					strLabel = map.getGlobalLabel().getInterestLabelName();
					if (match(strLabel))
					{
						logger.debug("Success to match devices bases on label: {}", strLabel);
						matchFlag = true;
						break;
					}
				}
				
				if (!matchFlag)
				{
					logger.debug(
							"Can't find devices with same interest labels, add interest labels of device <{}> into interestLabelDeviceMap",
							device.getDeviceIdentification());
					for (Interestlabelmap map : maps)
					{
						strLabel = map.getGlobalLabel().getInterestLabelName();
						interestLabelDeviceMap.put(strLabel, device);
					}
				}
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
		
		private boolean match(String interestLabel)
		{
			if (logger.isDebugEnabled())
	    	{
	    		logger.debug("Start to match device bases on interest label " + interestLabel +" from device <{}>", device.getDeviceIdentification());
	    	}
	    	
			IDeviceWrapper tmpDevice = interestLabelDeviceMap.get(interestLabel);
			
	    	if (tmpDevice == null)
	    	{
	    		return false;
	    	}
	    	
	    	if (tmpDevice == device)
			{
				logger.error("Fatal error: device <{}> has been saved by interest label: " + interestLabel, device.getDeviceIdentification());
				return false;
			}
			
			removeInterestIndex(tmpDevice);
			List<IDeviceWrapper> tmpList = new ArrayList<>();
			tmpList.add(tmpDevice);
			tmpList.add(device);
			sessionManager.addDeviceList(tmpList, pool, interestLabel);
			return true;
		}
	}
}

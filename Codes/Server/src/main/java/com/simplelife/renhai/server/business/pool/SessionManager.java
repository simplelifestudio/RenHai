/**
 * SessionProductor.java
 * 
 * History:
 *     2013-11-30: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.pool;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.session.BusinessSessionPool;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.log.DbLogger;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IProductor;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.util.Worker;

/**
 * 
 */
public class SessionManager implements IProductor
{
	private Logger logger = BusinessModule.instance.getLogger();
	private ConcurrentLinkedQueue<SessionStartTask> sessionStartTaskQueue = new ConcurrentLinkedQueue<>();
	protected Worker worker = new Worker(this);
	
	public SessionManager()
	{
		worker.setName("SessionStarter");
	}
	
	public void startService()
	{
		worker.startExecution();
	}
	
	public void stopService()
	{
		worker.stopExecution();
	}
	
	public void addDeviceList(List<IDeviceWrapper> selectedDeviceList, InterestBusinessDevicePool pool, String deviceFoundInterest)
	{
		logger.debug("Create SessionCoordinator with device list of label {}", deviceFoundInterest);
		SessionStartTask startSessionTask = new SessionStartTask(selectedDeviceList, pool, deviceFoundInterest);
    	sessionStartTaskQueue.add(startSessionTask);
    	worker.resumeExecution();
	}
	
	@Override
	public boolean hasWork()
	{
		return !sessionStartTaskQueue.isEmpty();
	}

	@Override
	public Runnable getWork()
	{
		return sessionStartTaskQueue.remove();
	}
	
	private class SessionStartTask implements Runnable
	{
		private List<IDeviceWrapper> selectedDeviceList;
		private InterestBusinessDevicePool pool;
		private String deviceFoundInterest;
		
		public SessionStartTask(
				List<IDeviceWrapper> selectedDevice, 
				InterestBusinessDevicePool pool,
				String deviceFoundInterest)
		{
			this.selectedDeviceList = selectedDevice;
			this.pool = pool;
			this.deviceFoundInterest = deviceFoundInterest;
		}
		
		@Override
		public void run()
		{
			try
			{
				logger.debug("Run of SessionCoordinator, deviceFoundInterest: {}", deviceFoundInterest);
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
				
				if (session.startSession(selectedDeviceList, obj))
				{
					DbLogger.increaseInterestMatchCount(deviceFoundInterest);
					increaseMatchCount(selectedDeviceList, deviceFoundInterest);
				}
				else
				{
					recycleDevice(selectedDeviceList);
				}
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
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

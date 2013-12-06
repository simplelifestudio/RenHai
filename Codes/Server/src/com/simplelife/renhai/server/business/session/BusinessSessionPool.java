/**
 * BusinessSessionPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.business.session;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.AbstractPool;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBusinessSession;


/** */
public class BusinessSessionPool extends AbstractPool
{
	// Timer designed for checking expiration of token
	private Timer timer;
	
	private List<IBusinessSession> sessionList = new ArrayList<IBusinessSession>();
	
	public final static BusinessSessionPool instance = new BusinessSessionPool();
	
	private Logger logger = BusinessModule.instance.getLogger();
    
	private BusinessSessionPool()
	{
		capacity = GlobalSetting.BusinessSetting.OnlinePoolCapacity / 2;
		init();
	}
    
	/** */
	public IBusinessSession getBusinessSession()
	{
		if (sessionList.isEmpty())
		{
			logger.error("All business sessions are used.");
			return null;
		}
		logger.debug("Dispatch session, session count before dispatch: {}", sessionList.size());
		return sessionList.remove(0);
	}
	
	/** */
	public void recycleBusinessSession(IBusinessSession session)
	{
		sessionList.add(session);
		logger.debug("Recycle session, session count after recycle: {}", sessionList.size());
	}
	
	/** */
	public void init()
	{
		clearPool();
		for (int i = 0; i < this.capacity; i++)
		{
			sessionList.add(new BusinessSession());
		}
	}
	
	/** */
	public void checkWebRTCToken()
	{
		
	}
	
	/** */
	public boolean isPoolFull()
	{
		return (sessionList.size() >= capacity);
	}
	
	/** */
	public int getElementCount()
	{
		return sessionList.size();
	}
	
	@Override
	public void clearPool()
	{
		sessionList.clear();
	}
}

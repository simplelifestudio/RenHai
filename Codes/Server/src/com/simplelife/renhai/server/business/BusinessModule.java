/**
 * BusinessModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.business.pool.InputMessageCenter;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.business.pool.OutputMessageCenter;
import com.simplelife.renhai.server.business.session.WebRTCSessionPool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.util.AbstractModule;


/** */
public class BusinessModule extends AbstractModule
{
	private BusinessModule()
	{
		logger = LoggerFactory.getLogger(BusinessModule.class);
	}

	public final static BusinessModule instance = new BusinessModule();
	
	@Override
	public void startService()
    {
		// To initialize DB connection
    	SqlSession session = DAOWrapper.getSession();
    	session.close();
    	
    	OnlineDevicePool.instance.startService();
    	InputMessageCenter.instance.startService();
    	OutputMessageCenter.instance.startService();
    	WebRTCSessionPool.instance.startService();
    	moduleAvailable = true;
    }
	
	@Override
	public void stopService()
	{
		OnlineDevicePool.instance.stopService();
		InputMessageCenter.instance.stopService();
		OutputMessageCenter.instance.stopService();
		WebRTCSessionPool.instance.stopService();
		moduleAvailable = false;
	}
}

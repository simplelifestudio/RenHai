/**
 * DbModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.db;

import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.AbstractModule;


/** */
public class DBModule extends AbstractModule
{
	private DBModule()
	{
		logger = LoggerFactory.getLogger(DBModule.class);
	}
	
	
	public void cache(Object obj)
	{
		DAOWrapper.asyncSave(obj);
	}
	public final static DBModule instance = new DBModule();
	
	
	@Override
	public void startService()
    {
		super.startService();
		DAOWrapper.startTimers();
    }
	
	@Override
	public void stopService()
	{
		DAOWrapper.flushToDB();
		super.stopService();
		DAOWrapper.stopTimers();
	}
	
}

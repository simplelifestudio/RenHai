/**
 * DbModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.db;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.util.AbstractModule;
import com.simplelife.renhai.server.util.DbObjectCache;
import com.simplelife.renhai.server.util.GlobalSetting;


/** */
public class DBModule extends AbstractModule
{
	public final static DBModule instance = new DBModule();
	public final DbObjectCache<Globalimpresslabel> impressLabelCache = new DbObjectCache<Globalimpresslabel>(GlobalimpresslabelMapper.class);
	public final DbObjectCache<Globalinterestlabel> interestLabelCache = new DbObjectCache<Globalinterestlabel>(GlobalinterestlabelMapper.class);
	public final DbObjectCache<Device> deviceCache = new DbObjectCache<Device>(DeviceMapper.class);
	
	private DBModule()
	{
		logger = LoggerFactory.getLogger(DBModule.class);
		impressLabelCache.setCapacity(GlobalSetting.DBSetting.GlobalImpressLabelCacheCount);
		interestLabelCache.setCapacity(GlobalSetting.DBSetting.GlobalInterestLabelCacheCount);
		deviceCache.setCapacity(GlobalSetting.DBSetting.DeviceCacheCount);
	}

	@Override
	public void startService()
    {
		super.startService();
		DAOWrapper.instance.startService();
		GlobalSetting.startService();
		
		// To initialize DB connection
    	SqlSession session = DAOWrapper.instance.getSession();
    	session.close();
    }
	
	@Override
	public void stopService()
	{
		DAOWrapper.instance.flushToDB();
		super.stopService();
		DAOWrapper.instance.stopService();
		GlobalSetting.stopService();
	}
	
}

/**
 * DbLogger.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.log;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.Profileoperationlog;
import com.simplelife.renhai.server.db.Systemoperationlog;
import com.simplelife.renhai.server.util.Consts;


/** */
public class DbLogger
{
	//public final static DbLogger instance = new DbLogger();
	//private static Object increaseInterestFlag = new Object();
	//private static Object increaseImpressFlag = new Object();
	
	public static void increaseImpressAssessCount(String labelName)
	{
		Globalimpresslabel label = DBModule.instance.impressLabelCache.getObject(labelName);
		if (label == null)
		{
			DBModule.instance.getLogger().error("Target label can't be found in DB: ", labelName);
			return;
		}
	
		synchronized(label)
		{
			int count = label.getGlobalAssessCount();
			label.setGlobalAssessCount(label.getGlobalAssessCount() + 1);
			BusinessModule.instance.getLogger().debug("Assess count of global impress label <" 
				+ labelName + "> was increased from " + count 
				+ " to " + label.getGlobalAssessCount());
		}
		DAOWrapper.cache(label);
		
	}
	
	public static void increaseInterestMatchCount(String labelName)
	{
		if (labelName == null || labelName.length() == 0)
		{
			return;
		}
		
		Globalinterestlabel label = DBModule.instance.interestLabelCache.getObject(labelName);
		if (label == null)
		{
			DBModule.instance.getLogger().error("Fatal error: target label {} can't be found in DB: ", labelName);
			return;
		}
		
		synchronized(label)
		{
			int count = label.getGlobalMatchCount();
			label.setGlobalMatchCount(label.getGlobalMatchCount()+1);
			BusinessModule.instance.getLogger().debug("Match count of global interest label <" 
				+ labelName + "> was increased from " + count 
				+ " to " + label.getGlobalMatchCount());
		}
		DAOWrapper.cache(label);
		
	}
	
	public static void saveProfileLog(Consts.OperationCode code, Profile profile, String logInfo)
	{
		long now = System.currentTimeMillis();
		
		Profileoperationlog log = new Profileoperationlog();
		log.setOperationCodeId(code.getValue());
		log.setLogInfo(logInfo);
		log.setLogTime(now);
		log.setProfile(profile);
		
		DAOWrapper.cache(log);
	}
	
	public static void saveSystemLog(Consts.OperationCode code
			, Consts.SystemModule module
			, String logInfo)
	
	{
		long now = System.currentTimeMillis();
		
		Systemoperationlog log = new Systemoperationlog();
		log.setLogTime(now);
		log.setLogInfo(logInfo);
		log.setOperationCodeId(code.getValue());
		log.setModuleId(module.getValue());
		DAOWrapper.cache(log);
	}
}

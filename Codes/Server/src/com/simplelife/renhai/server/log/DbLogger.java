/**
 * DbLogger.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.log;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.DBQueryUtil;
import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.db.GlobalimpresslabelDAO;
import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.db.GlobalinterestlabelDAO;
import com.simplelife.renhai.server.db.HibernateSessionFactory;
import com.simplelife.renhai.server.db.Operationcode;
import com.simplelife.renhai.server.db.OperationcodeDAO;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.db.Profileoperationlog;
import com.simplelife.renhai.server.db.Systemmodule;
import com.simplelife.renhai.server.db.SystemmoduleDAO;
import com.simplelife.renhai.server.db.Systemoperationlog;
import com.simplelife.renhai.server.util.Consts;


/** */
public class DbLogger
{
	//public final static DbLogger instance = new DbLogger();
	private static Object increaseInterestFlag = new Object();
	private static Object increaseImpressFlag = new Object();
	
	public static void increaseImpressMatchCount(String labelName)
	{
		synchronized (increaseImpressFlag)
		{
			Globalimpresslabel label = DBQueryUtil.getGlobalimpresslabel(labelName);
			if (label == null)
			{
				DBModule.instance.getLogger().error("Target label can't be found in DB: ", labelName);
				return;
			}
			
		
			label.setGlobalAssessCount(label.getGlobalAssessCount() + 1);
			DAOWrapper.asyncSave(label);
		}
	}
	
	public static void increaseInterestMatchCount(String labelName)
	{
		if (labelName == null || labelName.length() == 0)
		{
			return;
		}
		
		synchronized (increaseInterestFlag)
		{
			Globalinterestlabel label = DBQueryUtil.getGlobalinterestlabel(labelName);
			if (label == null)
			{
				DBModule.instance.getLogger().error("Target label can't be found in DB: ", labelName);
				return;
			}
			label.setGlobalMatchCount(label.getGlobalMatchCount()+1);
			DAOWrapper.asyncSave(label);
		}
	}
	
	public static void saveProfileLog(Consts.OperationCode code, Profile profile, String logInfo)
	{
		OperationcodeDAO dao = new OperationcodeDAO();
		Operationcode codeObj = dao.findByOperationCode(code.getValue()).get(0);
		
		long now = System.currentTimeMillis();
		
		Profileoperationlog log = new Profileoperationlog();
		log.setOperationcode(codeObj);
		log.setLogInfo(logInfo);
		log.setLogTime(now);
		log.setProfile(profile);
		
		DAOWrapper.asyncSave(log);
	}
	
	public static void saveSystemLog(Consts.OperationCode code
			, Consts.SystemModule module
			, String logInfo)
	{
		OperationcodeDAO dao = new OperationcodeDAO();
		Operationcode codeObj = dao.findByOperationCode(code.getValue()).get(0);
		
		SystemmoduleDAO moduleDao = new SystemmoduleDAO();
		Systemmodule moduleObj = moduleDao.findByModuleNo(module.getValue()).get(0); 
		
		long now = System.currentTimeMillis();
		
		Systemoperationlog log = new Systemoperationlog();
		log.setLogTime(now);
		log.setLogInfo(logInfo);
		log.setOperationcode(codeObj);
		log.setSystemmodule(moduleObj);
		DAOWrapper.asyncSave(log);
	}
}

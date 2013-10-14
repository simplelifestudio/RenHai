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

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.DBModule;
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
	private class SaveThread extends Thread
	{
		@Override
		public void run()
		{
			
		}
	}
	
	private static class IncreaseImpressThread extends Thread
	{
		private String labelName;
		public IncreaseImpressThread(String labelName)
		{
			this.labelName = labelName;
		}
		@Override
		public void run()
		{
			Thread.currentThread().setName("IncreaseImpress");
			Session hibernateSesion = HibernateSessionFactory.getSession();
			synchronized (increaseImpressFlag)
			{
				GlobalimpresslabelDAO dao = new GlobalimpresslabelDAO();
				List<Globalimpresslabel> list = dao.findByImpressLabelName(labelName);
				if (list.size() == 0)
				{
					DBModule.instance.getLogger().error("Target label can't be found in DB: ", labelName);
					return;
				}
				
				Globalimpresslabel label = list.get(0);
				Session session = HibernateSessionFactory.getSession();
				Transaction t = null;
				
				try
				{
					t = session.beginTransaction();
					label.setGlobalAssessCount(label.getGlobalAssessCount() + 1);
					t.commit();
				}
				catch(Exception e)
				{
					FileLogger.printStackTrace(e);
					if (t != null)
					{
						t.rollback();
					}
				}
			}
			hibernateSesion.close();
		}
	}
	
	private static class IncreaseInterestThread extends Thread
	{
		private String labelName;
		public IncreaseInterestThread(String labelName)
		{
			this.labelName = labelName;
		}
		@Override
		public void run()
		{
			if (labelName == null || labelName.length() == 0)
			{
				return;
			}
			
			Thread.currentThread().setName("IncreaseInterest");
			Session hibernateSesion = HibernateSessionFactory.getSession();
			synchronized (increaseInterestFlag)
			{
				GlobalinterestlabelDAO dao = new GlobalinterestlabelDAO();
				List<Globalinterestlabel> list = dao.findByInterestLabelName(labelName);
				if (list.size() == 0)
				{
					DBModule.instance.getLogger().error("Target label can't be found in DB: ", labelName);
					return;
				}
				
				Globalinterestlabel label = list.get(0);
				Session session = HibernateSessionFactory.getSession();
				Transaction t = null;
				
				try
				{
					t = session.beginTransaction();
					label.setGlobalMatchCount(label.getGlobalMatchCount()+1);
					t.commit();
				}
				catch(Exception e)
				{
					FileLogger.printStackTrace(e);
					if (t != null)
					{
						t.rollback();
					}
				}
			}
			hibernateSesion.close();
		}
	}
	
	private static class SaveProfileLogThread extends Thread
	{
		private Consts.OperationCode code;
		private Profile profile;
		private String logInfo;
		
		public SaveProfileLogThread(Consts.OperationCode code, Profile profile, String logInfo)
		{
			this.code = code;
			this.profile = profile;
			this.logInfo = logInfo;
		}
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("SaveProfileLog");
			Session hibernateSesion = HibernateSessionFactory.getSession();
			
			OperationcodeDAO dao = new OperationcodeDAO();
			Operationcode codeObj = dao.findByOperationCode(code.getValue()).get(0);
			
			Session session = HibernateSessionFactory.getSession();
			Transaction t = null;
			long now = System.currentTimeMillis();
			
			try
			{
				t = session.beginTransaction();
				Profileoperationlog log = new Profileoperationlog();
				log.setOperationcode(codeObj);
				log.setLogInfo(logInfo);
				log.setLogTime(now);
				log.setProfile(profile);
				
				BusinessModule.instance.getLogger().debug("profile ID: " + profile.getProfileId());
				session.save(log);
				t.commit();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
				if (t != null)
				{
					t.rollback();
				}
			}
			finally
			{
				hibernateSesion.close();
			}
		}
	}
	
	
	private static class SaveSystemLogThread extends Thread
	{
		private Consts.OperationCode code;
		private Consts.SystemModule module;
		private String logInfo;
		
		public SaveSystemLogThread(Consts.OperationCode code
				, Consts.SystemModule module
				, String logInfo)
		{
			this.code = code;
			this.module = module;
			this.logInfo = logInfo;
		}
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("IncreaseImpress");
			Session hibernateSesion = HibernateSessionFactory.getSession();
			
			OperationcodeDAO dao = new OperationcodeDAO();
			Operationcode codeObj = dao.findByOperationCode(code.getValue()).get(0);
			
			SystemmoduleDAO moduleDao = new SystemmoduleDAO();
			Systemmodule moduleObj = moduleDao.findByModuleNo(module.getValue()).get(0); 
			
			Session session = HibernateSessionFactory.getSession();
			Transaction t = null;
			long now = System.currentTimeMillis();
			
			try
			{
				t = session.beginTransaction();
				Systemoperationlog log = new Systemoperationlog();
				log.setLogTime(now);
				log.setLogInfo(logInfo);
				log.setOperationcode(codeObj);
				log.setSystemmodule(moduleObj);
				session.save(log);
				t.commit();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
				if (t != null)
				{
					t.rollback();
				}
			}
			
			hibernateSesion.close();
		}
	}
	
	//public final static DbLogger instance = new DbLogger();
	private static Object increaseInterestFlag = new Object();
	private static Object increaseImpressFlag = new Object();
	
	private void DbLogger()
	{
		
	}
	
	public static void increaseImpressMatchCount(String impressLabelName)
	{
		(new IncreaseImpressThread(impressLabelName)).start();
	}
	
	public static void increaseInterestMatchCount(String interestLabelName)
	{
		(new IncreaseInterestThread(interestLabelName)).start();
	}
	
	public static void saveProfileLog(Consts.OperationCode code, Profile profile, String logInfo)
	{
		(new SaveProfileLogThread(code, profile, logInfo)).start();
	}
	
	public static void saveSystemLog(Consts.OperationCode code
			, Consts.SystemModule module
			, String logInfo)
	{
		(new SaveSystemLogThread(code, module, logInfo)).start();
	}
}

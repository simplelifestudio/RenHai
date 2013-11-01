/**
 * DAOWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.db;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hamcrest.core.IsInstanceOf;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;


/** */
public class DAOWrapper
{
	private static class FlushTimer extends TimerTask
	{
		FlushTask flushTask;
		
		public FlushTimer(FlushTask flushTask)
		{
			this.flushTask = flushTask; 
		}
		
		@Override
		public void run()
		{
			flushTask.getLock().lock();
	    	flushTask.signal();
	    	flushTask.getLock().unlock();
		}
	}
	
	private static class FlushTask extends Thread
	{
		private final ConcurrentLinkedQueue<Object> linkToBeSaved;
		private final Lock lock = new ReentrantLock();
		private final Condition condition = lock.newCondition();
		private boolean continueFlag = true;
		private Session session = null;
		
		public FlushTask(ConcurrentLinkedQueue<Object> linkToBeSaved)
		{
			this.linkToBeSaved = linkToBeSaved;
		}
		
		public Lock getLock()
	    {
	    	return lock;
	    }
		
		public void signal()
	    {
	    	condition.signal();
	    }
		
		private boolean saveObjectToDB(Object obj)
		{
			/*
			if (obj instanceof Globalimpresslabel)
			{
				Globalimpresslabel label = (Globalimpresslabel) obj;
				logger.debug("===========Save Globalimpresslabel: " + label.getImpressLabelName());
			}
			else if (obj instanceof Globalinterestlabel)
			{
				Globalinterestlabel label = (Globalinterestlabel) obj;
				logger.debug("===========Save Globalinterestlabel: " + label.getInterestLabelName());
			} 
			else if (obj instanceof Device)
			{
				Device device = (Device) obj;
				logger.debug("===========Save device:" + device.getDeviceSn() + ", associated labels:");
				
				for (Impresslabelmap labelMap : device.getProfile().getImpresscard().getImpresslabelmaps())
				{
					logger.debug("===========Impress label:" + labelMap.getGlobalimpresslabel().getImpressLabelName() + ", global id: " + labelMap.getGlobalimpresslabel().getGlobalImpressLabelId());
				}
				
				for (Interestlabelmap labelMap : device.getProfile().getInterestcard().getInterestlabelmaps())
				{
					logger.debug("===========Interest label:" + labelMap.getGlobalinterestlabel().getInterestLabelName() + ", global id: " + labelMap.getGlobalinterestlabel().getGlobalInterestLabelId());
				}
			}
			else
			{
				logger.debug("===========Save " + obj.toString());
			}
			*/
			
			//Session session = HibernateSessionFactory.getSession();
			if (session == null)
			{
				logger.error("Fatal error: null hibernate session, check DB parameters");
				return false;
			}
			
			Transaction t = null;
			try
			{
				t =  session.beginTransaction();
				
				
				/*
				if (obj instanceof Device)
				{
					Device device = (Device) obj;
					Set<Impresslabelmap> impressLabelSet = device.getProfile().getImpresscard().getImpresslabelmaps();
					GlobalimpresslabelDAO impressDao = new GlobalimpresslabelDAO();
					for (Impresslabelmap labelMap : impressLabelSet)
					{
						String labelName = labelMap.getGlobalimpresslabel().getImpressLabelName();
						if (labelName == null)
						{
							logger.error("Fatal error, labelname == null in device <{}>", device.getDeviceSn());
							continue;
						}
						Globalimpresslabel globalLabel = impressDao.findByImpressLabelName(labelName).get(0);
						labelMap.setGlobalimpresslabel(globalLabel);
					}
					
					GlobalinterestlabelDAO interestDao = new GlobalinterestlabelDAO();
					Set<Interestlabelmap> interestLabelSet = device.getProfile().getInterestcard().getInterestlabelmaps();
					for (Interestlabelmap labelMap : interestLabelSet)
					{
						String labelName = labelMap.getGlobalinterestlabel().getInterestLabelName();
						if (labelName == null)
						{
							logger.error("Fatal error, labelname == null in device <{}>", device.getDeviceSn());
							continue;
						}
						Globalinterestlabel globalLabel = interestDao.findByInterestLabelName(labelName).get(0);
						labelMap.setGlobalinterestlabel(globalLabel);
					}
					/*
					String sql = SqlUtil.getDeviceSaveSql(device);
					SQLQuery query = session.createSQLQuery(sql);
					logger.debug("Executing sql:\n{}", sql);
					query.executeUpdate();
				}
				*/
				
				Object mergedObj = session.merge(obj);
				if (mergedObj != null)
				{
					session.save(mergedObj);
				}
				else
				{
					session.save(obj);
				}
				t.commit();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
				if (t != null)
				{
					t.rollback();
				}
				return false;
			}
			finally
			{
				//session.close();
			}
			return true;
		}
		
		
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("DBCacheTask");
			Object obj = null;
			while (continueFlag)
			{
				if (linkToBeSaved.isEmpty())
				{
					try
					{
						lock.lock();
						if (session != null && session.isOpen())
						{
							HibernateSessionFactory.closeSession();
							session = null;
							//session.close();
						}
						logger.debug("Await due to no data in cache queue");
						condition.await();
						
						if (!linkToBeSaved.isEmpty())
						{
							logger.debug("Resume saving data in cache queue, cache queue size: {}", linkToBeSaved.size());
							session = HibernateSessionFactory.getSession();
						}
					}
					catch (InterruptedException e)
					{
						FileLogger.printStackTrace(e);
					}
					finally
					{
						lock.unlock();
					}
				}
				else
				{
					obj = linkToBeSaved.remove();
					if (!saveObjectToDB(obj))
					{
						// Try again
						saveObjectToDB(obj);
					}
				}
			}
		}
	}

	protected static Logger logger = BusinessModule.instance.getLogger();
    protected static ConcurrentLinkedQueue<Object> linkToBeSaved = new ConcurrentLinkedQueue<Object>();
    protected static int objectCountInCache = 0;
    protected static Timer timer = new Timer();
    protected static FlushTask flushTask = new FlushTask(linkToBeSaved);
    
    
    public static Device getDeviceInCache(String deviceSn, boolean removeFlag)
    {
    	Iterator<Object> it = linkToBeSaved.iterator();
    	Object obj;
    	while (it.hasNext())
    	{
    		obj = it.next();
    		if (obj instanceof Device)
    		{
    			Device device = (Device) obj;
    			if (deviceSn.equals(device.getDeviceSn()))
    			{
    				if (removeFlag)
    				{
    					linkToBeSaved.remove(obj);
    				}
    				return device;
    			}
    		}
    	}
    	return null;
    }
    
    public static Globalimpresslabel getImpressLabelInCache(String lableName)
    {
    	Iterator<Object> it = linkToBeSaved.iterator();
    	Object obj;
    	while (it.hasNext())
    	{
    		obj = it.next();
    		if (obj instanceof Globalimpresslabel)
    		{
    			Globalimpresslabel label = (Globalimpresslabel) obj;
    			if (lableName.equals(label.getImpressLabelName()))
    			{
    				return label;
    			}
    		}
    	}
    	return null;
    }
    
    public static Globalinterestlabel getInterestLabelInCache(String lableName)
    {
    	Iterator<Object> it = linkToBeSaved.iterator();
    	Object obj;
    	while (it.hasNext())
    	{
    		obj = it.next();
    		if (obj instanceof Globalinterestlabel)
    		{
    			Globalinterestlabel label = (Globalinterestlabel) obj;
    			if (lableName.equals(label.getInterestLabelName()))
    			{
    				return label;
    			}
    		}
    	}
    	return null;
    }
    
    public static void startTimers()
    {
    	flushTask.start();
    	timer.scheduleAtFixedRate(new FlushTimer(flushTask), DateUtil.getNowDate(), GlobalSetting.TimeOut.FlushCacheToDB);
    }
    
    public static void stopTimers()
    {
    	timer.cancel();
    }
    
    public static void signalForFlush()
    {
    	flushTask.getLock().lock();
    	flushTask.signal();
    	flushTask.getLock().unlock();
    }
    
    /**
     * Save object in cache or to DB 
     * @param obj: object to be saved
     */
    public static void cache(Object obj)
    {
    	if(obj == null)
    	{
    		return;
    	}
    	
    	if (linkToBeSaved.contains(obj))
    	{
    		return;
    	}
    	
    	linkToBeSaved.add(obj);
    	if (linkToBeSaved.size() >= GlobalSetting.DBSetting.MaxRecordCountForFlush)
		{
    		signalForFlush();
		}
    }
    
    /**
	 * Execute given SQL string
	 * @param sql: SQL string
	 * @return true normally, else false if any error occurred or invalid SQL string
	 */
	public static boolean executeSql(String sql)
	{
		if (sql == null || sql.length() == 0)
		{
			logger.error("Invalid SQL string: " + sql);
			return false;
		}
		
		Session session = HibernateSessionFactory.getSession();
		if (session == null)
		{
			logger.error("Null hibernate session, check DB parameters");
			return false;
		}
		
		boolean result = false;
		Transaction t = null;
		try
		{
			t =  session.beginTransaction();
			SQLQuery query = session.createSQLQuery(sql);
			query.executeUpdate();
			t.commit();
			result = true;
		}
		catch(Exception e)
		{
			if (t != null)
			{
				t.rollback();
			}
			result = false;
			FileLogger.printStackTrace(e);
		}
		return result;
	}
    
	/**
	 * Check if records existent by query of given SQL string
	 * @param sql: SQL string
	 * @return nonExistent, existent or errorOccurred
	 */
	public static Consts.DBExistResult exists(String sql)
	{
		logger.debug("start of exists()");
		if (sql == null || sql.length() == 0)
		{
			logger.error("Invalid SQL string: " + sql);
			return Consts.DBExistResult.NonExistent;
		}
		
		Session session = HibernateSessionFactory.getSession();
		if (session == null)
		{
			logger.error("Null hibernate session, check DB parameters");
			return Consts.DBExistResult.ErrorOccurred;
		}
		
		try
		{
			logger.debug("before createSQLQuery()");
			SQLQuery query = session.createSQLQuery(sql);
			logger.debug("after createSQLQuery()");
			if (query.list().size() > 0)
			{
				return Consts.DBExistResult.Existent;
			}
			else
			{
				return Consts.DBExistResult.NonExistent;
			}
		}
		catch(Exception e)
		{
			logger.error(sql);
			FileLogger.printStackTrace(e);
			return Consts.DBExistResult.ErrorOccurred;
		}
	}
    
	/**
	 * Make query by given SQL string
	 * @param sql: SQL string
	 * @param objClass: class definition for mapping of return records
	 * @return List of objects
	 */
	public static List query(String sql, Class objClass)
	{
		if (sql == null || sql.length() == 0)
		{
			logger.error("Invalid SQL string: " + sql);
			return null;
		}
		
		Session session = HibernateSessionFactory.getSession();
	    if (session == null)
		{
	    	logger.error("Null hibernate session, check DB parameters");
			return null;
		}
	    
	    try
	    {
		    SQLQuery query = session.createSQLQuery(sql).addEntity(objClass);
	    	return query.list();
		}
		catch(Exception e)
		{
			logger.error(sql);
			logger.error(e.getMessage());
			return null;
		}
	    finally
		{
			//HibernateSessionFactory.closeSession();
		}
	}
    
	/**
	 * Delete record in DB by given object
	 * @param obj: object to be deleted
	 */
	public static void delete(Object obj)
	{
		Session session = HibernateSessionFactory.getSession();
		if (session == null)
		{
			logger.error("Null hibernate session, check DB parameters");
			return;
		}
		
		Transaction t = null;
		try
		{
			t =  session.beginTransaction();
			session.delete(obj);
			t.commit();
		}
		catch(Exception e)
		{
			if (t != null)
			{
				t.rollback();
			}
			FileLogger.printStackTrace(e);
		}
	}
    
	/**
	 * Save object to DB
	 * @param obj: object to be saved
	 */
	public static void save(Object obj)
	{
		Session session = HibernateSessionFactory.getSession();
		if (session == null)
		{
			logger.error("Null hibernate session, check DB parameters");
			return;
		}
		
		Transaction t = null;
		try
		{
			logger.debug("Start to save data to DB");
			t = session.beginTransaction();
			session.save(obj);
			t.commit();
			logger.debug("Save data succeed");
		}
		catch(Exception e)
		{
			if (t != null)
			{
				t.rollback();
			}
			FileLogger.printStackTrace(e);
		}
	}

	/**
	 * Flush session to db, or stop flush timer if necessary
	 * */
    public static void flushToDB()
    {
    	signalForFlush();
    }
}

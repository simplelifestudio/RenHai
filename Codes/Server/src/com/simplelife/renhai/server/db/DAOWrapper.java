/**
 * DAOWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.db;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
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
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("DBCacheTask");
			Session session = HibernateSessionFactory.getSession();
			if (session == null)
			{
				logger.error("Fatal error: null hibernate session, check DB parameters");
				return;
			}
			
			Object obj = null;
			while (continueFlag)
			{
				if (linkToBeSaved.size() == 0)
				{
					try
					{
						lock.lock();
						if (session.isOpen())
						{
							session.close();
						}
						logger.debug("Await due to no data in cache queue");
						condition.await();
						
						if (linkToBeSaved.size() > 0)
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
				
				if (!session.isOpen())
				{
					continue;
				}
				
				Transaction t = null;
				try
				{
					t =  session.beginTransaction();
					Object mergedObj = session.merge(linkToBeSaved.remove());
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
				}
			}
		}
		
	}
	
	protected static Logger logger = BusinessModule.instance.getLogger();
    protected static ConcurrentLinkedQueue<Object> linkToBeSaved = new ConcurrentLinkedQueue<Object>();
    protected static int objectCountInCache = 0;
    protected static Timer timer = new Timer();
    protected static FlushTask flushTask = new FlushTask(linkToBeSaved);
    
    
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
    public static void asyncSave(Object obj)
    {
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

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

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDbCache;


/** */
public class DAOWrapper
{
	private static class FlushTask extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				Thread.currentThread().setName("DBFlush");
				logger.debug("Start to flush DB cache");
				DAOWrapper.flushToDB();
				logger.debug("Finished flush DB cache");
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	private static Logger logger = BusinessModule.instance.getLogger();
    /** */
    protected static LinkedList<Object> linkToBeSaved = new LinkedList<Object>();
    protected static int objectCountInCache = 0;
    
    /** */
    protected static Timer timer = new Timer();
    
    
    public static void startTimers()
    {
    	timer.scheduleAtFixedRate(new FlushTask(), DateUtil.getNowDate(), GlobalSetting.TimeOut.FlushCacheToDB);
    }
    
    public static void stopTimers()
    {
    	timer.cancel();
    }
    
    /**
     * Save object in cache or to DB 
     * @param obj: object to be saved
     */
    public static void cache(Object obj)
    {
    	if (GlobalSetting.DBSetting.CacheEnabled)
    	{
    		/*
    		synchronized(linkToBeSaved)
        	{
	    		if (linkToBeSaved.size() >= GlobalSetting.DBSetting.MaxRecordCountForDiscard)
	    		{
	    			logger.error("The first records was discarded due to DB module is unavailable and cache is full!!!");
	        		linkToBeSaved.removeFirst();
	    		}
    		
        		linkToBeSaved.add(obj);
        	}
        	*/
        	
    		try
    		{
	    		Session session = HibernateSessionFactory.getSession();
	    		session.saveOrUpdate(session.merge(obj));
	    		objectCountInCache++;
    		}
    		catch(Exception e)
    		{
    			DBModule.instance.getLogger().error(e.getMessage());
    			FileLogger.printStackTrace(e);
    		}
    		
    		if (objectCountInCache >= GlobalSetting.DBSetting.MaxRecordCountForFlush)
    		{
    			flushToDB();
    		}
    	}
    	else
    	{
    		try
    		{
	    		//Session session = HibernateSessionFactory.getSessionFactory().openSession();
    			Session session = HibernateSessionFactory.getSession();
    			
    			Transaction trans = session.beginTransaction();
    			Object mergedObj = session.merge(obj);
	    		session.saveOrUpdate(mergedObj);
    			//session.saveOrUpdate(obj);
	        	session.flush();
	        	//session.clear();
	        	trans.commit();
	        	//session.beginTransaction().commit();
	    		//session.close();
    		}
    		catch(Exception e)
    		{
    			DBModule.instance.getLogger().error(e.getMessage());
    			FileLogger.printStackTrace(e);
    		}
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
		
		try
		{
			session.beginTransaction();
			SQLQuery query = session.createSQLQuery(sql);
			query.executeUpdate();
			session.getTransaction().commit();
			session.clear();
			return true;
		}
		catch(Exception e)
		{
			logger.error(sql);
			logger.error(e.getMessage());
			//logger.printStackTrace(e);
			if (session.getTransaction() != null)
			{
				session.getTransaction().rollback();
			}
			return false;
		}
		finally
		{
			//HibernateSessionFactory.closeSession();
		}
	}
    
	/**
	 * Check if records existent by query of given SQL string
	 * @param sql: SQL string
	 * @return nonExistent, existent or errorOccurred
	 */
	public static Consts.DBExistResult exists(String sql)
	{
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
			SQLQuery query = session.createSQLQuery(sql); 
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
			logger.error(e.getMessage());
			return Consts.DBExistResult.ErrorOccurred;
		}
		finally
		{
			//HibernateSessionFactory.closeSession();
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
		
		try
		{
			session.beginTransaction();
			session.delete(obj);
			session.getTransaction().commit();
		}
		catch(Exception e)
		{
			if (session.getTransaction() != null)
			{
				session.getTransaction().rollback();
			}
			logger.error(e.getMessage());
		}
		finally
		{
			//HibernateSessionFactory.closeSession();
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
		
		try
		{
			logger.debug("Start to save data to DB");
			session.beginTransaction();
			session.save(obj);
			session.getTransaction().commit();
			logger.debug("Save data succeed");
		}
		catch(Exception e)
		{
			if (session.getTransaction() != null)
			{
				session.getTransaction().rollback();
			}
			logger.error(e.getMessage());
		}
		finally
		{
			//HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * Flush session to db, or stop flush timer if necessary
	 * */
    public static void flushToDB()
    {
    	if (objectCountInCache == 0)
    	{
    		if (!GlobalSetting.DBSetting.CacheEnabled)
        	{
        		DAOWrapper.stopTimers();
        	}
    		return;
    	}
    	
    	if (!DBModule.instance.isAvailable())
    	{
    		return;
    	}
    	
    	try
    	{
    		Session session = HibernateSessionFactory.getSession();
    		//TODO 这个时候如果曾经获取过多个session，怎么办？考虑保存所有获取过的session？
    		session.beginTransaction().commit();		// Commit会隐含调用flush
    		session.clear();
        }
    	catch(Exception e)
    	{
    		logger.error("Error occurred when trying to flush: " + e.getMessage());
    		FileLogger.printStackTrace(e);
    	}
    }
}

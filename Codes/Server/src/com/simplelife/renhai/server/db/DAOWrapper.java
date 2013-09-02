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

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.DBExistResult;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDbOperation;


/** */
public class DAOWrapper
{
    /** */
    protected static LinkedList<IDbOperation> linkToBeSaved;
    
    /** */
    protected static Timer timer;
    
    
    /**
	 * Execute given SQL string
	 * @param sql: SQL string
	 * @return true normally, else false if any error occurred or invalid SQL string
	 */
	public static boolean executeSql(String sql)
	{
		if (sql == null || sql.length() == 0)
		{
			FileLogger.severe("Invalid SQL string: " + sql);
			return false;
		}
		
		Session session = HibernateSessionFactory.getCurrentSession();
		if (session == null)
		{
			FileLogger.severe("Null hibernate session, check DB parameters");
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
		    FileLogger.severe(sql);
			FileLogger.printStackTrace(e);
			if (session.getTransaction() != null)
			{
				session.getTransaction().rollback();
			}
			return false;
		}
		finally
		{
			HibernateSessionFactory.closeCurrentSession();
		}
	}
    
	/**
	 * Check if records existent by query of given SQL string
	 * @param sql: SQL string
	 * @return nonExistent, existent or errorOccurred
	 */
	public static DBExistResult exists(String sql)
	{
		if (sql == null || sql.length() == 0)
		{
			FileLogger.severe("Invalid SQL string: " + sql);
			return DBExistResult.NonExistent;
		}
		
		Session session = HibernateSessionFactory.getCurrentSession();
		if (session == null)
		{
			FileLogger.severe("Null hibernate session, check DB parameters");
			return DBExistResult.ErrorOccurred;
		}
		
		try
		{
			SQLQuery query = session.createSQLQuery(sql); 
			if (query.list().size() > 0)
			{
				return DBExistResult.Existent;
			}
			else
			{
				return DBExistResult.NonExistent;
			}
		}
		catch(Exception e)
		{
		    FileLogger.severe(sql);
			FileLogger.printStackTrace(e);
			return DBExistResult.ErrorOccurred;
		}
		finally
		{
			HibernateSessionFactory.closeCurrentSession();
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
			FileLogger.severe("Invalid SQL string: " + sql);
			return null;
		}
		
		Session session = HibernateSessionFactory.getCurrentSession();
	    if (session == null)
		{
			FileLogger.severe("Null hibernate session, check DB parameters");
			return null;
		}
	    
	    try
	    {
		    SQLQuery query = session.createSQLQuery(sql).addEntity(objClass); 
	    	return query.list();
		}
		catch(Exception e)
		{
		    FileLogger.severe(sql);
			FileLogger.printStackTrace(e);
			return null;
		}
	    finally
		{
			HibernateSessionFactory.closeCurrentSession();
		}
	}
    
	/**
	 * Delete record in DB by given object
	 * @param obj: object to be deleted
	 */
	public static void delete(Object obj)
	{
		Session session = HibernateSessionFactory.getCurrentSession();
		if (session == null)
		{
			FileLogger.severe("Null hibernate session, check DB parameters");
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
			FileLogger.printStackTrace(e);
		}
		finally
		{
			HibernateSessionFactory.closeCurrentSession();
		}
	}
    
	/**
	 * Save object to DB
	 * @param obj: object to be saved
	 */
	public static void save(Object obj)
	{
		Session session = HibernateSessionFactory.getCurrentSession();
		if (session == null)
		{
			FileLogger.severe("Null hibernate session, check DB parameters");
			return;
		}
		
		try
		{
			FileLogger.info("Start to save data to DB");
			session.beginTransaction();
			session.save(obj);
			session.getTransaction().commit();
			FileLogger.info("Save data succeed");
		}
		catch(Exception e)
		{
			if (session.getTransaction() != null)
			{
				session.getTransaction().rollback();
			}
			FileLogger.printStackTrace(e);
		}
		finally
		{
			HibernateSessionFactory.closeCurrentSession();
		}
	}

	/** */
    public static void toBeSaved(IDbOperation object)
    {
    	linkToBeSaved.addLast(object);
    	
    	if (linkToBeSaved.size() >= GlobalSetting.DBSetting.MaxRecordCountForFlush)
    	{
    		flushToDB();
    	}
    }
    
    /** */
    public static void flushToDB()
    {
    	if (linkToBeSaved.size() == 0)
    	{
    		return;
    	}
    	
    	IDbOperation obj;
    	while (linkToBeSaved.size() > 0)
    	{
    		obj = linkToBeSaved.removeFirst();
    		obj.saveToDb();
    	}
    }
}

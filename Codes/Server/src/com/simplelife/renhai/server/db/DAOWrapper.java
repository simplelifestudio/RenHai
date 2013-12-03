/**
 * DAOWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.db;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDbObject;


/** */
public class DAOWrapper
{
	private static SqlSessionFactory factory = null;
	
	public static SqlSession getSession()
	{
		if (factory == null)
		{
			synchronized(SqlSession.class)
			{
				if (factory == null)
				{
					try
					{
						Reader reader = Resources.getResourceAsReader("mybatis.xml");
						SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
						factory = builder.build(reader);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						FileLogger.printStackTrace(e);
						return null;
					}
				}
			}
		}
		
		if (factory == null)
		{
			return null;
		}
		return factory.openSession();
	}
	
	private static class FlushTimer extends TimerTask
	{
		//FlushTask flushTask;
		
		public FlushTimer(FlushTask flushTask)
		{
			Thread.currentThread().setName("DBFlushTimer");
		}
		
		@Override
		public void run()
		{
			DAOWrapper.signalForFlush();
		}
	}
	
	private static class FlushTask extends Thread
	{
		private final ConcurrentLinkedQueue<IDbObject> linkToBeSaved;
		private final Lock lock = new ReentrantLock();
		private final Condition condition = lock.newCondition();
		private boolean continueFlag = true;
		private SqlSession session = null;
		private boolean runningFlag = false;
		
		public FlushTask(ConcurrentLinkedQueue<IDbObject> linkToBeSaved)
		{
			this.linkToBeSaved = linkToBeSaved;
		}
		
		
		public boolean isRunning()
		{
			return runningFlag;
		}
		
		public Lock getLock()
	    {
	    	return lock;
	    }
		
		public void signal()
	    {
	    	condition.signal();
	    }

		private boolean saveObjectToDB(IDbObject obj)
		{
			if (session == null)
			{
				logger.error("Fatal error: null SQL session, check DB parameters");
				return false;
			}
			
			try
			{
				//printLog(obj);
				obj.save(session);
				session.commit();
			}
			catch(Exception e)
			{
				session.rollback();
				FileLogger.printStackTrace(e);
				return false;
			}
			return true;
		}
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("DBCacheTask");
			IDbObject obj = null;
			session = getSession();
			while (continueFlag)
			{
				if (linkToBeSaved.isEmpty())
				{
					try
					{
						lock.lock();
						if (session != null)
						{
							session.close();
							session = null;
						}
						logger.debug("Await due to no data in cache queue");
						runningFlag = false;
						condition.await();
						
						if (!linkToBeSaved.isEmpty())
						{
							runningFlag = true;
							logger.debug("Resume saving data in cache queue, cache queue size: {}", linkToBeSaved.size());
							session = getSession();
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
    protected static ConcurrentLinkedQueue<IDbObject> linkToBeSaved = new ConcurrentLinkedQueue<IDbObject>();
    protected static int objectCountInCache = 0;
    protected static Timer timer = new Timer();
    protected static FlushTask flushTask = new FlushTask(linkToBeSaved);
    
    /*
    public static Device getDeviceInCache(String deviceSn, boolean removeFlag)
    {
    	Iterator<IDbObject> it = linkToBeSaved.iterator();
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
    	Iterator<IDbObject> it = linkToBeSaved.iterator();
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
    
    public static Globalimpresslabel getImpressLabelInCache(Integer labelId)
    {
    	Iterator<IDbObject> it = linkToBeSaved.iterator();
    	Object obj;
    	while (it.hasNext())
    	{
    		obj = it.next();
    		if (obj instanceof Globalimpresslabel)
    		{
    			Globalimpresslabel label = (Globalimpresslabel) obj;
    			if (label.getGlobalImpressLabelId() == labelId)
    			{
    				return label;
    			}
    		}
    	}
    	return null;
    }
    
    public static Globalinterestlabel getInterestLabelInCache(String lableName)
    {
    	Iterator<IDbObject> it = linkToBeSaved.iterator();
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
    
    public static Globalinterestlabel getInterestLabelInCache(Integer labelId)
    {
    	Iterator<IDbObject> it = linkToBeSaved.iterator();
    	Object obj;
    	while (it.hasNext())
    	{
    		obj = it.next();
    		if (obj instanceof Globalinterestlabel)
    		{
    			Globalinterestlabel label = (Globalinterestlabel) obj;
    			if (label.getGlobalInterestLabelId() == labelId)
    			{
    				return label;
    			}
    		}
    	}
    	return null;
    }
    */
    
    public static void startTimers()
    {
    	flushTask.start();
    	timer.scheduleAtFixedRate(new FlushTimer(flushTask), GlobalSetting.TimeOut.FlushCacheToDB, GlobalSetting.TimeOut.FlushCacheToDB);
    }
    
    public static void stopTimers()
    {
    	timer.cancel();
    }
    
    public static void signalForFlush()
    {
    	if (flushTask.isRunning())
    	{
    		return;
    	}
    	
    	flushTask.getLock().lock();
    	flushTask.signal();
    	flushTask.getLock().unlock();
    }
    
    
    /**
     * Check if an object is in DB saving cache
     * @param obj: object to be saved
     * @return Return true if obj is existent in cache, else return false;
     */
    private boolean isObjectInCache(Object obj)
    {
    	// Use string field to check existence of object, as ID of objects may be null before save
    	if (obj instanceof Globalinterestlabel)
    	{
    		Globalinterestlabel newLabel = (Globalinterestlabel) obj;
    		Iterator<IDbObject> it = linkToBeSaved.iterator();
        	while (it.hasNext())
        	{
        		Globalinterestlabel label = (Globalinterestlabel) it.next();
    			if (label.getInterestLabelName().equals(newLabel.getInterestLabelName()))
    			{
    				return true;
    			}
        	}
        	return false;
    	}
    	
    	if (obj instanceof Globalimpresslabel)
    	{
    		Globalimpresslabel newLabel = (Globalimpresslabel) obj;
    		Iterator<IDbObject> it = linkToBeSaved.iterator();
        	while (it.hasNext())
        	{
        		Globalimpresslabel label = (Globalimpresslabel) it.next();
    			if (label.getImpressLabelName().equals(newLabel.getImpressLabelName()))
    			{
    				return true;
    			}
        	}
        	return false;
    	}
    	
    	if (obj instanceof Device)
    	{
    		Device newDevice = (Device) obj;
    		Iterator<IDbObject> it = linkToBeSaved.iterator();
        	while (it.hasNext())
        	{
        		Device device = (Device) it.next();
    			if (device.getDeviceSn().equals(newDevice.getDeviceSn()))
    			{
    				return true;
    			}
        	}
        	return false;
    	}
    	return false;
    }
    
    public static void removeDeviceForAssess(Device device)
    {
    	if (!linkToBeSaved.contains(device))
    	{
    		String deviceSn = device.getDeviceSn();
    		logger.debug("Object of given Device object with DeviceSn {} is not in linkToBeSaved.", deviceSn);
    		
    		for (Object obj : linkToBeSaved)
    		{
    			if (obj instanceof Device)
    			{
    				if (((Device)obj).getDeviceSn().equals(deviceSn))
    				{
    					logger.error("But device with same DeviceSn exists!");
    					break;
    				}
    			}
    		}
    		return;
    	}
    	
    	logger.debug("Remove device <{}> from cache of DAOWrapper", device.getDeviceSn());
    	linkToBeSaved.remove(device);
    }
    
    private static void printLog(Object obj)
	{
		if (obj instanceof Globalinterestlabel)
		{
			Globalinterestlabel label = (Globalinterestlabel) obj;
			logger.debug("Cache Globalinterestlabel: " + label.getInterestLabelName());
		}
		else if (obj instanceof Globalimpresslabel)
		{
			Globalimpresslabel label = (Globalimpresslabel) obj;
			logger.debug("Cache Globalimpresslabel: " + label.getImpressLabelName());
		}
		else if (obj instanceof Device)
		{
			Device device = (Device) obj;
			logger.debug("Cache Device: " + device.getDeviceSn());
		}
		else
		{
			logger.debug("Cache " + obj.getClass().getName());
		}
	}
    
    public static void save(IDbObject obj)
    {
    	SqlSession session = getSession();
    	try
		{
			obj.save(session);
			session.commit();
		}
		catch(Exception e)
		{
			session.rollback();
			FileLogger.printStackTrace(e);
		}
    }
    
    /**
     * Save object in cache 
     * @param obj: object to be saved
     */
    public static void cache(IDbObject obj)
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
    	printLog(obj);
    	if (linkToBeSaved.size() >= GlobalSetting.DBSetting.MaxRecordCountForFlush)
		{
    		signalForFlush();
		}
    }
    
	/**
	 * Flush session to db, or stop flush timer if necessary
	 * */
    public static void flushToDB()
    {
    	Thread.currentThread().setName("DBCacheTask");
		IDbObject obj = null;
		SqlSession session = getSession();
		while (!linkToBeSaved.isEmpty())
		{
			obj = linkToBeSaved.remove();
			try
			{
				obj.save(session);
				session.commit();
			}
			catch(Exception e)
			{
				session.rollback();
				FileLogger.printStackTrace(e);
				try
				{
					obj.save(session);
					session.commit();
				}
				catch(Exception e2)
				{
					session.rollback();
					FileLogger.printStackTrace(e2);
				}
			}
		}
    }
}

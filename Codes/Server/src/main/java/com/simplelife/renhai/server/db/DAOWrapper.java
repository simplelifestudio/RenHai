/**
 * DAOWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.db;

import java.io.Reader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDbObject;
import com.simplelife.renhai.server.util.IProductor;
import com.simplelife.renhai.server.util.Worker;


/** */
public class DAOWrapper implements IProductor
{
	public final static DAOWrapper instance = new DAOWrapper(); 
	private SqlSessionFactory factory = null;
	protected Logger logger = BusinessModule.instance.getLogger();
    protected ConcurrentLinkedQueue<IDbObject> linkToBeSaved = new ConcurrentLinkedQueue<>();
    protected volatile int objectCountInCache = 0;
    protected Timer timer = new Timer();
    protected Worker worker;
	
    private DAOWrapper()
    {
    	worker = new Worker(this);
    }
    
	public SqlSession getSession()
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
	
	private class FlushTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				DAOWrapper.instance.flushToDB();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	
	private class FlushTimer extends TimerTask
	{
		public FlushTimer(Worker worker)
		{
			Thread.currentThread().setName("DBFlushTimer");
		}
		
		@Override
		public void run()
		{
			try
			{
				worker.resumeExecution();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	
	public void startService()
    {
    	worker.startExecution();
    	timer.scheduleAtFixedRate(new FlushTimer(worker), GlobalSetting.TimeOut.FlushCacheToDB, GlobalSetting.TimeOut.FlushCacheToDB);
    }
    
    public void stopService()
    {
    	worker.stopExecution();
    	timer.cancel();
    }
    
    public void signalForFlush()
    {
    	worker.resumeExecution();
    }
    
    public void removeDeviceForAssess(Device device)
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
    	removeFromLink(device);
    }
    
    private void printLog(Object obj)
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
    
    public void save(IDbObject obj)
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
    	finally
    	{
    		session.close();
    	}
    }

    private void addToLink(IDbObject obj)
    {
    	linkToBeSaved.add(obj);
    	objectCountInCache++;
    }
    
    private void removeFromLink(IDbObject obj)
    {
    	objectCountInCache--;
    	linkToBeSaved.remove(obj);
    }
    
    private IDbObject removeFromLink()
    {
    	objectCountInCache--;
    	return linkToBeSaved.remove();
    }
    
    /**
     * Save object in cache 
     * @param obj: object to be saved
     */
    public void cache(IDbObject obj)
    {
    	if(obj == null)
    	{
    		return;
    	}
    	
    	if (linkToBeSaved.contains(obj))
    	{
    		return;
    	}
    	
    	addToLink(obj);
    	printLog(obj);
    	if (objectCountInCache >= GlobalSetting.DBSetting.MaxRecordCountForFlush)
		{
    		signalForFlush();
		}
    }
    
	/**
	 * Flush session to db, or stop flush timer if necessary
	 * */
    public void flushToDB()
    {
    	Thread.currentThread().setName("DBCacheTask");
		IDbObject obj = null;
		SqlSession session = getSession();
		while (!linkToBeSaved.isEmpty())
		{
			obj = removeFromLink();
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
		session.close();
    }

	@Override
	public boolean hasWork()
	{
		return !linkToBeSaved.isEmpty();
	}

	@Override
	public Runnable getWork()
	{
		return new FlushTask();
	}
}

/**
 * HibernateSessionFactory.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.db;


import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.simplelife.renhai.server.log.FileLogger;


/** */
public class HibernateSessionFactory
{
	private static String CONFIG_FILE_LOCATION = "/com/simplelife/seeds/server/db/hibernate.cfg.xml";
	private static final ThreadLocal threadLocal = new ThreadLocal();
	private static Configuration configuration;
	private static SessionFactory sessionFactory;
	private static String configFile = CONFIG_FILE_LOCATION;
	private final static Lock l = new ReentrantLock();
	
	/**
	 * Returns the ThreadLocal Session instance. Lazy initialize the
	 * <code>SessionFactory</code> if needed.
	 * 
	 * @return Session
	 * @throws HibernateException
	 */
	public static Session getCurrentSession() throws HibernateException
	{
		Session session = (Session) threadLocal.get();
		
		try
		{
			if (session == null || !session.isOpen())
			{
				if (sessionFactory == null)
				{
					l.lock();
					rebuildSessionFactory();
					l.unlock();
				}
				session = (sessionFactory != null) ? sessionFactory.openSession() : null;
				threadLocal.set(session);
			}
		}
		catch (Exception e)
		{
			FileLogger.printStackTrace(e);
		}
		return session;
	}
	
	/**
	 * Rebuild hibernate session factory
	 * 
	 */
	public static void rebuildSessionFactory()
	{
		try
		{
			if (configuration == null)
			{
				configuration = new Configuration();
				configuration.configure(configFile);
				FileLogger.info("Load configuration file: " + configFile);
			}
			
			Properties properties = configuration.getProperties();
			// properties.list(System.out);
			// <property name=></property>
			// properties.put("connection.password", "Simplelife123");
			ServiceRegistryBuilder build = new ServiceRegistryBuilder().applySettings(properties);
			ServiceRegistry sr = build.buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(sr);
		}
		catch (Exception e)
		{
			FileLogger.printStackTrace(e);
		}
	}
	
	/**
	 * Close the single hibernate session instance.
	 * 
	 * @throws HibernateException
	 */
	public static void closeCurrentSession() throws HibernateException
	{
		Session session = (Session) threadLocal.get();
		threadLocal.set(null);
		
		if (session != null)
		{
			session.close();
		}
	}
	
	/**
	 * return session factory
	 * 
	 */
	public static SessionFactory getSessionFactory()
	{
		if (sessionFactory == null)
		{
			rebuildSessionFactory();
		}
		return sessionFactory;
	}
	
	/**
	 * return session factory
	 * 
	 * session factory will be rebuilt in the next call
	 */
	public static void setConfigFile(String configFile)
	{
		HibernateSessionFactory.configFile = configFile;
		sessionFactory = null;
	}
	
	/**
	 * return hibernate configuration
	 * 
	 */
	public static Configuration getConfiguration()
	{
		return configuration;
	}
}

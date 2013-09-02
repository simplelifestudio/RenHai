/**
 * DbModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.util.AbstractModule;


/** */
public class DBModule extends AbstractModule
{
	private static DBModule instance;
	
	public static DBModule instance()
	{
		if (instance != null)
		{
			return instance;
		}
		
		synchronized (instance)
		{
			if (instance != null)
			{
				return instance;
			}
			instance = new DBModule();
			return instance;
		}
	}
}

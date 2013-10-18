/**
 * RenHaiListener.java
 * 
 * History:
 *     2013-10-18: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.json.JSONModule;
import com.simplelife.renhai.server.websocket.WebSocketModule;

/**
 * 
 */
public class RenHaiListener implements ServletContextListener
{
	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		BusinessModule.instance.stopService();
		DBModule.instance.stopService();
		JSONModule.instance.stopService();
		WebSocketModule.instance.stopService();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0)
	{
		BusinessModule.instance.startService();
		DBModule.instance.startService();
		JSONModule.instance.startService();
		WebSocketModule.instance.startService();
		
	}

	
}

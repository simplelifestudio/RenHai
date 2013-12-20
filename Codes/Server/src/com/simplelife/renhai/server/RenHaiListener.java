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

import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.json.JSONModule;
import com.simplelife.renhai.server.util.GlobalSetting;
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
		System.out.println("+----------------------------------------------------------+");
		System.out.println("|               Try to launch RenHai Server                |");
		System.out.println("+----------------------------------------------------------+");
		try
		{
			GlobalSetting.checkSettingFile();
			
			System.out.println("+----------------------------------------------------------+");
			System.out.println("|                      Load DB Module                      |");
			System.out.println("+----------------------------------------------------------+");
			DBModule.instance.startService();
			System.out.println("+----------------------------------------------------------+");
			System.out.println("|                      DB Module Ready                     |");
			System.out.println("+----------------------------------------------------------+");
			
			System.out.println("+----------------------------------------------------------+");
			System.out.println("|                  Load Business Module                    |");
			System.out.println("+----------------------------------------------------------+");
			BusinessModule.instance.startService();
			System.out.println("+----------------------------------------------------------+");
			System.out.println("|                  Business Module Ready                   |");
			System.out.println("+----------------------------------------------------------+");
			
			System.out.println("+----------------------------------------------------------+");
			System.out.println("|                     Load JSON Module                     |");
			System.out.println("+----------------------------------------------------------+");
			JSONModule.instance.startService();
			System.out.println("+----------------------------------------------------------+");
			System.out.println("|                     JSON Module Ready                    |");
			System.out.println("+----------------------------------------------------------+");
			
			System.out.println("+----------------------------------------------------------+");
			System.out.println("|                   Load WebSocket Module                  |");
			System.out.println("+----------------------------------------------------------+");
			WebSocketModule.instance.startService();
			System.out.println("+----------------------------------------------------------+");
			System.out.println("|                   WebSocket Module Ready                 |");
			System.out.println("+----------------------------------------------------------+");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
}

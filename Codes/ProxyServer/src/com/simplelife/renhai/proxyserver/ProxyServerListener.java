/**
 * RenHaiListener.java
 * 
 * History:
 *     2013-10-18: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ProxyServerListener implements ServletContextListener
{
	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0)
	{
		GlobalSetting.instance.checkSettingFile();
	}

	
}

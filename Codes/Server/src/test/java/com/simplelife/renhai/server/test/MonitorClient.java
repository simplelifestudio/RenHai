/**
 * MonitorClient.java
 * 
 * History:
 *     2014-1-22: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import com.simplelife.renhai.server.test.MockAppConsts.MockAppBehaviorMode;

/**
 * 
 */
public class MonitorClient
{
	public static void main(String[] args)
	{
		if (args.length != 2)
        {
            printUsage();
            return;
        }
		
		try
        {
        	MockApp.mockAppExecutePool.startService();
	        String arg = args[0];
	        int interval = Integer.parseInt(arg);
	        String serverLink = args[1];
	        
	        MockApp app = new MockApp("Monitor", MockAppBehaviorMode.Monitor.name(), serverLink, "Monitor");
	        app.startMonitorTimer(interval);
	        while (true)
	        {
	        	Thread.sleep(10000);
	        }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	printUsage();
        }
        finally
        {
        	MockApp.mockAppExecutePool.stopService();
        }

	}

	private static void printUsage()
    {
    	System.out.println("Wrong parameters, usage of this tool:");
        System.out.println("+---------------------------------------------------------------------+");
        System.out.println("| Java -jar monitor.jar {MonitorInterval} {WebsocketLink}             |");
        System.out.println("| where:                                                              |");
        System.out.println("|   MonitorInterval: interval of querying monitor data                   |");
        System.out.println("|   WebsocketLink: websocket link of server for communication         |");
        System.out.println("+---------------------------------------------------------------------+");
    }
}

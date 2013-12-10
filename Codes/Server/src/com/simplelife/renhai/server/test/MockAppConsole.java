/**
 * MockAppConsole.java
 * 
 * History:
 *     2013-12-4: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;

/**
 * 
 */
public class MockAppConsole
{
    public static void main(String[] args)
    {
        if (args.length != 4)
        {
            printUsage();
            return;
        }
        
        try
        {
        	MockApp.mockAppExecutePool.startService();
	        String arg = args[0];
	        int threadCount = Integer.parseInt(arg);
	        
	        arg = args[1];
	        int threadInterval = Integer.parseInt(arg);
	        
	        String serverLink = args[2];
	        String behaviorMode = args[3];
	        //ExecutorService executeThreadPool = Executors.newFixedThreadPool(threadCount);

	        ExecuteThread thread = new ExecuteThread("MA-Monitor", "Monitor", serverLink);
	        thread.start();
	        for (int i = 0; i < threadCount; i++)
	        {
	        	System.out.println("Start to launch MockApp-" + i);
	        	thread = new ExecuteThread("MA-" + i, behaviorMode, serverLink);
	        	thread.start();
	        	System.out.println("MockApp-" + i + " launched successfully.");
	        	Thread.sleep(threadInterval);
	        }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	printUsage();
        }
    }
    
    private static void printUsage()
    {
    	System.out.println("Wrong parameters, usage of this tool:");
        System.out.println("+---------------------------------------------------------------------+");
        System.out.println("| Java -jar renhai.jar {mockAppCount} {mockAppInterval}               |");
        System.out.println("|          {websocketLink} {appBehaviorMode}                          |");
        System.out.println("| where:                                                              |");
        System.out.println("|   mockAppCount: number of Mock App                                  |");
        System.out.println("|   mockAppInterval: interval of launching mockApps                   |");
        System.out.println("|   websocketLink: websocket link of server for communication         |");
        System.out.println("|   appBehaviorMode: behaveMode of mockApp, shall be one of:          |");
        System.out.println("|     - SendInvalidJSONCommand                                        |");
        System.out.println("|     - NoAppSyncRequest                                              |");
        System.out.println("|     - NoEnterPoolRequest                                            |");
        System.out.println("|     - NoResponseForSessionBound                                     |");
        System.out.println("|     - NoRequestOfAgreeChat                                          |");
        System.out.println("|     - ConnectLossDuringChatConfirm                                  |");
        System.out.println("|     - RejectChat                                                    |");
        System.out.println("|     - ConnectLossDuringChat                                         |");
        System.out.println("|     - NoRequestOfAgreeChat                                          |");
        System.out.println("|     - NormalAndContinue                                             |");
        System.out.println("|     - NormalAndQuit                                                 |");
        System.out.println("+---------------------------------------------------------------------+");
    }
    
    private static class ExecuteThread extends Thread
    {
        private String name;
        private String behaviorMode;
        private String websocketLink;
        private Logger logger = BusinessModule.instance.getLogger();
        
        public ExecuteThread(String name, String behaviorMode, String websocketLink)
        {
            this.name = name;
            this.behaviorMode = behaviorMode;
            this.websocketLink = websocketLink;
        }
        
        @Override
        public void run()
        {
            MockApp app = new MockApp(name, behaviorMode, websocketLink);
            while (app.getBusinessStatus() != MockAppConsts.MockAppBusinessStatus.Ended)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    logger.error("Thread of app {} is interrupted", app.getDeviceSn());
                    app.stopTimer();
                    app.disconnect();
                    return;
                }
            }
            logger.debug("MockApp: " + name + " ended");
            return;
        }
        
    }
    
}

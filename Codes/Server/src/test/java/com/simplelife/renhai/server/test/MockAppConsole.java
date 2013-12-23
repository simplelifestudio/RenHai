/**
 * MockAppConsole.java
 * 
 * History:
 *     2013-12-4: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.business.BusinessModule;

/**
 * 
 */
public class MockAppConsole
{
    public static void main(String[] args)
    {
        if (args.length != 6)
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
	        
	        arg = args[4];
	        int startIndex = Integer.parseInt(arg);
	        
	        arg = args[5];
	        int chatCount = Integer.parseInt(arg);
	        //ExecutorService executeThreadPool = Executors.newFixedThreadPool(threadCount);

	        CopyOnWriteArrayList<MockApp> mockApps = new CopyOnWriteArrayList<>();
	        ExecuteThread thread;
	        //thread = new ExecuteThread("MA-Monitor-" + startIndex, "Monitor", serverLink, 0, 0, mockApps);
	        //thread.start();
	        
	        int indexInGroup = 1;
	        int countPerGroup = 2;
	        
	        int modNum = threadCount % countPerGroup;
	        if (modNum != 0)
	        {
	        	threadCount += (countPerGroup - modNum); 
	        }
	        
	        int groupCount = threadCount / countPerGroup;
	        
	        int endIndex = startIndex + threadCount;
	        for (int i = startIndex; i < endIndex; i++)
	        {
	        	indexInGroup = i % groupCount + 1 + startIndex;
	        	System.out.println("Start to launch MockApp-" + i);
	        	thread = new ExecuteThread("MA-" + i, behaviorMode, serverLink, i, indexInGroup, mockApps, chatCount);
	        	thread.start();
	        	//System.out.println("MockApp-" + i + " launched successfully.");
	        	//interestLabel = "MA_" + i + "," + "GI_" + indexInGroup;
	        	//app = new MockApp("MA-" + i, behaviorMode, serverLink, interestLabel);
	        	//mockApps.add(app);
	        	Thread.sleep(threadInterval);
	        }
	        
	        Thread.sleep(3000);
	        MockApp app;
	        while (mockApps.size() > 0)
	        {
	        	Thread.sleep(1000);
	        	//System.out.println("Number of mockApps before check: "+ mockApps.size());
	        	
	        	for (int i = mockApps.size()-1; i >= 0; i--)
	        	{
	        		app = mockApps.get(i); 
	        		if (app.getBusinessStatus() == MockAppConsts.MockAppBusinessStatus.Ended)
	        		{
	        			System.out.println(app.getDeviceSn() + " is ended.");
	        			mockApps.remove(i);
	        		}
	        	}
	        	//System.out.println("Number of mockApps after check: "+ mockApps.size());
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
        private int appIndex;
        private int labelIndex;
        private CopyOnWriteArrayList<MockApp> mockApps;
        private int chatCount;
        
        private Logger logger = LoggerFactory.getLogger("MockAppConsole");
        
        public ExecuteThread(String name, String behaviorMode, String websocketLink, int appIndex, int labelIndex, CopyOnWriteArrayList<MockApp> mockApps, int chatCount)
        {
            this.name = name;
            this.behaviorMode = behaviorMode;
            this.websocketLink = websocketLink;
            this.appIndex = appIndex;
            this.labelIndex = labelIndex;
            this.mockApps = mockApps;
            this.chatCount = chatCount;
        }
        
        @Override
        public void run()
        {
        	String interestLabel = "Ä£Äâ," + "MA_" + appIndex + "," + "GI_" + labelIndex;
        	MockApp app = new MockApp(name, behaviorMode, websocketLink, interestLabel, chatCount);
        	mockApps.add(app);
        	/*
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
            */
        }
        
    }
    
}

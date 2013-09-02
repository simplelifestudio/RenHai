/**
 * FileLogger.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.log;

import java.util.logging.Level;
import java.util.logging.Logger;


/** */
public class FileLogger
{
	public static String LogLevelFine = "Fine";
    public static String LogLevelInfo = "Info";
    public static String LogLevelWarning = "Warning";
    public static String LogLevelSevere = "Severe";
    
    
	private static Logger logger = Logger.getLogger("Seeds");
	
	public static Level getLevel()
	{
	    return logger.getLevel();
	}
	
	public static void setLevel(Level level)
	{
	    logger.setLevel(level);
	}
	
	public static void info(String logInfo)
	{
		log(Level.INFO, logInfo);
	}
	
	public static void severe(String logInfo)
	{
		log(Level.SEVERE, logInfo);
	}
	
	public static void warning(String logInfo)
	{
		log(Level.WARNING, logInfo);
	}
	
	public static void fine(String logInfo)
	{
		log(Level.FINE, logInfo);
	}
	
	public static void printStackTrace(Exception e)
	{
	    logger.logp(Level.SEVERE, "", "", e.getMessage());
	    
		StackTraceElement[] messages = e.getStackTrace();
		int length=messages.length;
		for(int i=0;i<length;i++)
		{
		    if (messages[i].toString().contains("com.simplelife.seeds"))
		    {
		        logger.logp(Level.SEVERE, "", "", messages[i].toString());
		    }
		}
	}
	
	private static void log(Level level, String logInfo)
	{
		if (!logger.isLoggable(level))
		{
			return;
		}
		
		StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3]; 
		String methodName = traceElement.getMethodName();
		String fileName = traceElement.getFileName()+ ":" + Integer.toString(traceElement.getLineNumber());
		logger.logp(level, fileName, methodName, logInfo);
	}
}

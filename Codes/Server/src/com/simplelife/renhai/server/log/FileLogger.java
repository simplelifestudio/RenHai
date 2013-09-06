/**
 * FileLogger.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.simplelife.renhai.server.db.SessionRecordDAO;


/** */
public class FileLogger
{
	private static final Logger logger = LoggerFactory.getLogger("CommonLogger");
	
	public static String LogLevelFine = "Fine";
    public static String LogLevelInfo = "Info";
    public static String LogLevelWarning = "Warning";
    public static String LogLevelSevere = "Severe";
    
    public boolean isDebugEnabled()
    {
    	return logger.isDebugEnabled();
    }
    
    public boolean isWarnEnabled()
    {
    	return logger.isWarnEnabled();
    }
    
    public boolean isInfoEnabled()
    {
    	return logger.isInfoEnabled();
    }
    
    public boolean isErrorEnabled()
    {
    	return logger.isErrorEnabled();
    }
    
	public static void info(String logInfo)
	{
		logger.info(logInfo);
	}
	
	public static void severe(String logInfo)
	{
		logger.error(logInfo);
	}
	
	public static void warning(String logInfo)
	{
		logger.warn(logInfo);
	}
	
	public static void fine(String logInfo)
	{
		logger.debug(logInfo);
	}
	
	public static void printStackTrace(Exception e)
	{
	    logger.error(e.getMessage());
	    
		StackTraceElement[] messages = e.getStackTrace();
		int length=messages.length;
		for(int i=0;i<length;i++)
		{
		    if (messages[i].toString().contains("com.simplelife.seeds"))
		    {
		    	logger.error(messages[i].toString());
		    }
		}
	}
}

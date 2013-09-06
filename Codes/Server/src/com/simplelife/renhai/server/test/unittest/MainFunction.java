/**
 * Main.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test.unittest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.db.SystemOperationLog;
import com.simplelife.renhai.server.util.DateUtil;


/**
 * 
 */
public class MainFunction
{
	private static void testFileLogger()
	{
		Logger log = LoggerFactory.getLogger("RenHai");
		log.debug("This is debug log: {}", DateUtil.getNow());
		log.info("This is info log: {}", DateUtil.getNow());
		log.warn("This is warn log: {}", DateUtil.getNow());
		log.error("This is error log: {}", DateUtil.getNow());
	}

	private static void testDbOperations()
	{
		SystemOperationLog log = new SystemOperationLog();
		long time = DateUtil.getNowDate().getTime();
		log.setLogTime(time);
		log.setLogInfo("This is log info");
		log.setModuledefinition("Moduledefinition");
		log.setOperationcodedefinition("operationcodedefinition")
		
	}
	
	public static void main(String[] args)
	{
		testFileLogger();
	}
}

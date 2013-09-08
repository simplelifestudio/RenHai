/**
 * UtilModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import org.slf4j.LoggerFactory;


/** */
public class UtilModule extends AbstractModule
{
	private UtilModule()
	{
		logger = LoggerFactory.getLogger(UtilModule.class);
	}

	public final static UtilModule instance = new UtilModule();
}

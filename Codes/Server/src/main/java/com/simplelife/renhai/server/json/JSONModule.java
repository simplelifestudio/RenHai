/**
 * JSONModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;

import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.util.AbstractModule;


/** */
public class JSONModule extends AbstractModule
{
	private JSONModule()
	{
		logger = LoggerFactory.getLogger(JSONModule.class);
	}

	public final static JSONModule instance = new JSONModule();
}

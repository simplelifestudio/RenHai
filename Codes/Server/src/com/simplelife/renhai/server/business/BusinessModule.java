/**
 * BusinessModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business;

import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.util.AbstractModule;
import com.simplelife.renhai.server.websocket.WebSocketModule;


/** */
public class BusinessModule extends AbstractModule
{
	private BusinessModule()
	{
		logger = LoggerFactory.getLogger(BusinessModule.class);
	}

	public final static BusinessModule instance = new BusinessModule();

}

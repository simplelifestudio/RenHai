/**
 * WebSocketModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.websocket;
import org.slf4j.LoggerFactory;

import com.simplelife.renhai.server.util.AbstractModule;


/** */
public class WebSocketModule extends AbstractModule
{
	private WebSocketModule()
	{
		logger = LoggerFactory.getLogger(WebSocketModule.class);
	}

	public final static WebSocketModule instance = new WebSocketModule();

}

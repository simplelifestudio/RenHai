/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiSplashActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android;

public class RenHaiDefinitions {
	
	// Definitions for the app utilities
	public static final String RENHAI_APP_NAME = "RenHai";
	public static final String RENHAI_APP_FOLDER = "RenHai";
	
	// Log positions
	public static final String RENHAI_LOG_FILENAME = "renhai";
	
	// Key for the message encoding
	public static final String RENHAI_ENCODE_KEY = "20130801";
	
	// Server Address
	public static final String RENHAI_SERVER_WEBSOCKETADDR = "ws://192.81.135.31:80/renhai/websocket";
	public static final String RENHAI_SERVER_PROXYADDR = "http://192.81.135.31/renhaiproxy/";
	
	// ===============================================================================
	// C-S message type definitions
	// ===============================================================================	
	// Message types
	public static final int RENHAI_MSGID_ALOHAREQUEST  = 100;
	public static final int RENHAI_MSGID_ALOHARESPONSE = 402;
	
	// ===============================================================================
	// Network process result definitions
	// ===============================================================================		
	// Network return type
	public static final int RENHAI_NETWORK_CREATE_SUCCESS = 1100;
	public static final int RENHAI_NETWORK_CREATE_ERROR   = 1101;
	public static final int RENHAI_NETWORK_RECEIVE_MSG    = 1102;
	
	// ===============================================================================
	// Broadcast receiver definitions
	// ===============================================================================	
	// Broadcast regsiter type
	public static final String RENHAI_BROADCAST_WEBSOCKETMSG = "android.intent.action.WEBSOCKETMSG";
	
	// Broad cast message type
	public static final String RENHAI_BROADCASTMSG_DEF = "renhai.broadcast.msgtype";
	public static final String RENHAI_BROADCASTMSG_SOCKETERROR = "renhai.broadcast.websocketerrtype";
	
	

}

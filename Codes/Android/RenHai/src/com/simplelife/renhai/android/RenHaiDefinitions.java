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
	public static final String RENHAI_APP_NAME    = "RenHai";
	public static final String RENHAI_APP_FOLDER  = "RenHai";
	public static final String RENHAI_APP_VERSION = "1.0";
	
	// Log positions
	public static final String RENHAI_LOG_FILENAME = "renhai";
	
	// Key for the message encoding
	public static final String RENHAI_ENCODE_KEY = "20130801";
	
	// Server Address
	public static final String RENHAI_SERVER_WEBSOCKETADDR = "ws://192.81.135.31:80/renhai/websocket";
	public static final String RENHAI_SERVER_PROXYADDR = "http://192.81.135.31/renhaiproxy/request";
	
	// ===============================================================================
	// C-S message type definitions
	// ===============================================================================	
	// Message Type
	public static final int RENHAI_MSGTYPE_UNKNOW = 0;
	public static final int RENHAI_MSGTYPE_APPREQUEST  = 1;
	public static final int RENHAI_MSGTYPE_APPRESPONSE = 2;
	public static final int RENHAI_MSGTYPE_SERVERNOTIFICATION = 3;
	public static final int RENHAI_MSGTYPE_SERVERRESPONSE = 4;
	public static final int RENHAI_MSGTYPE_PROXYREQUEST = 5;
	
	// Message ID
	public static final int RENHAI_MSGID_ALOHAREQUEST  = 100;
	public static final int RENHAI_MSGID_ALOHARESPONSE = 402;
	public static final int RENHAI_MSGID_PROXYSYNCREQUEST  = 500; 
	public static final int RENHAI_MSGID_PROXYSYNCRESPONSE = 600; 
	
	// ===============================================================================
	// Network process result definitions
	// ===============================================================================		
	// Network return type
	public static final int RENHAI_NETWORK_HTTP_RECEIVE_MSG = 1100;
	public static final int RENHAI_NETWORK_HTTP_COMM_ERROR  = 1101;
	public static final int RENHAI_NETWORK_WEBSOCKET_CREATE_SUCCESS = 1102;
	public static final int RENHAI_NETWORK_WEBSOCKET_CREATE_ERROR   = 1103;
	public static final int RENHAI_NETWORK_WEBSOCKET_RECEIVE_MSG    = 1104;
	
	// ===============================================================================
	// Broadcast receiver definitions
	// ===============================================================================	
	// Broadcast regsiter type
	public static final String RENHAI_BROADCAST_WEBSOCKETMSG = "android.intent.action.WEBSOCKETMSG";
	
	// Broad cast message type
	public static final String RENHAI_BROADCASTMSG_DEF = "renhai.broadcast.msgtype";
	public static final String RENHAI_BROADCASTMSG_HTTPRESPSTATUS = "renhai.broadcast.httprespstatuscode";
	public static final String RENHAI_BROADCASTMSG_SOCKETERROR = "renhai.broadcast.websocketerrtype";
	
	

}

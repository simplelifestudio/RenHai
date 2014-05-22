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
	public static final int RENHAI_APP_BUILD = 1000;
	
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
	public static final int RENHAI_MSGTYPE_PROXYREQUEST   = 5;
	public static final int RENHAI_MSGTYPE_PROXYRESPONSE  = 6;
	
	// Message ID
	public static final int RENHAI_MSGID_ALOHAREQUEST  = 100;
	public static final int RENHAI_MSGID_APPDATASYNCREQUEST = 101;
	public static final int RENHAI_MSGID_SERVERDATASYNCREQUEST = 102;
	public static final int RENHAI_MSGID_ALOHARESPONSE = 402;
	public static final int RENHAI_MSGID_APPDATASYNCRESPONSE = 403;
	public static final int RENHAI_MSGID_SERVERDATASYNCRESPONSE = 404;
	public static final int RENHAI_MSGID_PROXYSYNCREQUEST  = 500; 
	public static final int RENHAI_MSGID_PROXYSYNCRESPONSE = 600; 
	
	// ===============================================================================
	// Network process result definitions
	// ===============================================================================		
	// Network return type
	public static final int RENHAI_NETWORK_HTTP_COMM_SUCESS = 1100;
	public static final int RENHAI_NETWORK_HTTP_COMM_ERROR  = 1101;
	public static final int RENHAI_NETWORK_WEBSOCKET_CREATE_SUCCESS = 1102;
	public static final int RENHAI_NETWORK_WEBSOCKET_CREATE_ERROR   = 1103;
	public static final int RENHAI_NETWORK_WEBSOCKET_RECEIVE_MSG    = 1104;
	public static final int RENHAI_NETWORK_WEBSOCKET_RECEIVE_ALOHARESP = 1105;
	public static final int RENHAI_NETWORK_WEBSOCKET_RECEIVE_APPSYNCRESP = 1106;
	public static final int RENHAI_NETWORK_WEBSOCKET_RECEIVE_SERVERSYNCRESP = 1107;
	public static final int RENHAI_NETWORK_MSS_UNMATCHMSGSN = 1108;
	public static final int RENHAI_NETWORK_MSS_UNMATCHDEVICESN = 1109;
	
	// ===============================================================================
	// Broadcast receiver definitions
	// ===============================================================================	
	// Broadcast regsiter type
	public static final String RENHAI_BROADCAST_WEBSOCKETMSG = "android.intent.action.WEBSOCKETMSG";
	
	// Broad cast message type
	public static final String RENHAI_BROADCASTMSG_DEF = "renhai.broadcast.msgtype";
	public static final String RENHAI_BROADCASTMSG_HTTPRESPSTATUS = "renhai.broadcast.httprespstatuscode";
	public static final String RENHAI_BROADCASTMSG_SOCKETERROR = "renhai.broadcast.websocketerrtype";
	
	// ===============================================================================
	// Database definitions
	// ===============================================================================	
	// The name of the sql file
	public static final String RENHAI_DB_SQLFILE = "CreateTables.sql";
	
	// ===============================================================================
	// Function return value definitions
	// ===============================================================================
	public static final int RENHAI_FUNC_STATUS_OK    = 0;
	public static final int RENHAI_FUNC_STATUS_ERROR = 1;
	
	
	

}

/**
 * Consts.java
 * 
 * History:
 *     2013-9-3: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;


/**
 * 
 */
public class Consts
{
	public enum DeviceBusinessStatus
	{
		Init, Idle, WaitMatch, SessionBound
	}
	
	public enum DeviceServiceStatus
	{
		Normal, Forbidden
	}
	
	public enum BusinessSessionStatus
	{
		Idle, ChatConfirm, VideoChat, Assess
	}
	
	public enum BusinessType
	{
		Random, Interest;
	}
	
	public enum DBExistResult
	{
		Existent, NonExistent, ErrorOccurred
	}
	
	public class MessageType
	{
		public final static int AppRequest = 1; 
		public final static int ServerResponse = 2;
		public final static int ServerNotification = 3;
		public final static int AppResponse = 4;
	}
	
	public class MessageId
	{
		public final static int Invalid						= 0;
		public final static int AppErrorResposne			= 100;
		public final static int ServerErrorResponse			= 101;
		public final static int AlohaRequest				= 102;
		public final static int AlohaResponse				= 103;
		public final static int AppDataSyncRequest			= 104;

		public final static int AppDataSyncResponse			= 105;
		public final static int ServerDataSyncRequest		= 106;
		public final static int ServerDataSyncResponse		= 107;
		public final static int BusinessSessionNotification	= 108;
		public final static int BusinessSessionNotificationResponse= 109;

		public final static int BusinessSessionRequest		= 110;
		public final static int BusinessSessionResponse		= 111;
		public final static int BroadcastNotification		= 112;
	}
	
	/*
	public enum MessageId
	{
		Invalid(0),
		AppErrorResposne(100), 
		ServerErrorResponse(101), 
		AlohaRequest(102), 
		AlohaResponse(103), 
		AppDataSyncRequest(104), 

		AppDataSyncResponse(105), 
		ServerDataSyncRequest(106), 
		ServerDataSyncResponse(107), 
		BusinessSessionNotification(108), 
		BusinessSessionNotificationResponse(109), 

		BusinessSessionRequest(110), 
		BusinessSessionResponse(111), 
		BroadcastNotification(112);
		
		private int messageId ;
		
		private MessageId(int messageId)
		{
			this.messageId = messageId;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(this.messageId);
		}
	}
	*/
	
	public class GlobalErrorCode
	{
		public final static int WebSocketSetupFailed_1000		= 1000;
		public final static int DBException_1001				= 1001;
		public final static int CapacityReached_1002			= 1002;
		public final static int ConnectionTimeout_1003			= 1003;
		public final static int EchoTimeout_1004				= 1004;
		                                                    
		public final static int InvalidJSONRequest_1100			= 1100;
		public final static int InvalidBusinessRequest_1101		= 1101;
		public final static int NoPermission_1102				= 1102;
		public final static int ParameterError_1103				= 1103;
		public final static int UnknownException_1104			= 1104;
		
		public final static int SyncFailureUnkownReason_1200	= 1200;
		public final static int StatFailureUnkownReason_1201	= 1201;
		public final static int CapacityReached_1202			= 1202;
		public final static int QuitBusiDevicePoolUnkown_1203	= 1203;
	}
}

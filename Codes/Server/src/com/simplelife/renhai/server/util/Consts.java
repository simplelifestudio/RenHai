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
	
	public enum MessageType
	{
		Invalid(0),
		AppRequest(1), 
		ServerResponse(2),
		ServerNotification(3),
		AppResponse(4);
		
		private int value;
		private MessageType(int value)
		{
			this.value = value;
		}
		
		public static MessageType getEnumItemByValue(int value)
		{
			for (MessageType item : values())
			{
				if (item.value == value)
				{
					return item;
				}
			}
			return null;
		}
	}
	
	/*
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
	*/
	
	public enum MessageId
	{
		InvalidRequest(0),
		UnkownRequest(1),
		TimeoutRequest(2),
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
		public static MessageId getEnumItemByValue(int value)
		{
			for (MessageId item : values())
			{
				if (item.messageId == value)
				{
					return item;
				}
			}
			return null;
		}
		
		public static MessageId getEnumItemByValue(String value)
		{
			return getEnumItemByValue(Integer.parseInt(value));
		}
	}
	
	public enum GlobalErrorCode
	{
		WebSocketSetupFailed_1000(1000),
		DBException_1001(1001),
		CapacityReached_1002(1002),
		ConnectionTimeout_1003(1003),
		EchoTimeout_1004(1004),
		                                                    
		InvalidJSONRequest_1100(1100),
		InvalidBusinessRequest_1101(1101),
		NoPermission_1102(1102),
		ParameterError_1103(1103),
		UnknownException_1104(1104),

		SyncFailureUnkownReason_1200(1200),
		StatFailureUnkownReason_1201(1201),
		CapacityReached_1202(1202),
		QuitBusiDevicePoolUnkown_1203(1203);
		
		private int code;
    	
    	private GlobalErrorCode(int code)
    	{
    		this.code = code; 
    	}
	}
	
	public enum OperationType
    {
    	EnterPool(1),
    	LeavePool(2),
    	AgreeChat(3),
    	RejectChat(4),
    	EndChat(5),

    	Assess(6),
    	AssessAndQuit(7),
    	Received(8);

    	private int type;
    	
    	private OperationType(int type)
    	{
    		this.type = type; 
    	}
    }
	
	public enum ConnectionSetting
	{
		ByteBufferMaxSize (2097152),
		CharBufferMaxSize (2097152);
		
		private int value;
		
		private ConnectionSetting(int value)
		{
			this.value = value;
		}
	}
}

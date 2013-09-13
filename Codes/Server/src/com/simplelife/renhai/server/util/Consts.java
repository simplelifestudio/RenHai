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
	public enum ServiceStatus
	{
		Normal, Banned;
		
		public static boolean isValidStatus(String status)
		{
			for (ServiceStatus item : values())
			{
				if (item.name().equals(status))
				{
					return true;
				}
			}
			return false;
		}
	}
	public enum BusinessStatus
	{
		Init, Idle, WaitMatch, SessionBound;
		
		public boolean isValidStatus(String status)
		{
			for (BusinessStatus item : values())
			{
				if (item.name().equals(status))
				{
					return true;
				}
			}
			return false;
		}
	}

	public enum YesNo
	{
		Yes(1),
		No(0);
		
		private int value;
		private YesNo(int value)
		{
			this.value = value;
		}
	
		public static YesNo parseValue(String value)
		{
			int intValue = Integer.valueOf(value);
			for (YesNo item : values())
			{
				if (item.value == intValue)
				{
					return item;
				}
			}
			return null;
		}
	}
	
	public enum JSONExecuteResult
	{
		Success(1),
		Fail(0);
		
		private int value;
		private JSONExecuteResult(int value)
		{
			this.value = value;
		}
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
		AppResponse(2),
		ServerNotification(3),
		ServerResponse(4);
		
		private int value;
		private MessageType(int value)
		{
			this.value = value;
		}
		
		public static MessageType parseValue(int value)
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
		
		@Override
		public String toString()
		{
			return String.valueOf(value);
		}
	}
	
	public enum MessageId
	{
		InvalidRequest(0),
		UnkownRequest(1),
		TimeoutRequest(2),
		
		AlohaRequest(100),
		AppDataSyncRequest(101),
		ServerDataSyncRequest(102),
		BusinessSessionRequest(103),
		
		AppErrorResposne(201),
		BusinessSessionNotificationResponse(202),
		
		BusinessSessionNotification(300),
		BroadcastNotification(301),
		
		ServerErrorResponse(401), 
		AlohaResponse(402),
		AppDataSyncResponse(403), 
		ServerDataSyncResponse(404), 
		BusinessSessionResponse(405); 
		
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
		public static MessageId parseValue(int value)
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
			return parseValue(Integer.parseInt(value));
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
    	
    	@Override
    	public String toString()
    	{
    		return String.valueOf(code);
    	}
	}
	
	public enum OperationType
    {
    	EnterPool(1),
    	LeavePool(2),
    	AgreeChat(3),
    	RejectChat(4),
    	EndChat(5),

    	AssessAndContinue(6),
    	AssessAndQuit(7),
    	Received(8);

    	private int value;
    	
    	private OperationType(int value)
    	{
    		this.value = value; 
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

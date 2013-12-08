/**
 * Consts.java
 * 
 * History:
 *     2013-9-3: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;


/**
 * 
 */
public class Consts
{
	public enum ServiceStatus
	{
		Invalid(-1),
		Maintenance(0),
		Normal(1),
		ToBeMaintained(2);
		
		private int value;
		private ServiceStatus(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static ServiceStatus parseValue(int value)
		{
			for (ServiceStatus item : values())
			{
				if (item.value == value)
				{
					return item;
				}
			}
			return Invalid;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(value);
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
		
		private int value;
    	
    	private GlobalErrorCode(int code)
    	{
    		this.value = code; 
    	}
    	
    	public int getValue()
		{
			return value;
		}
    	
    	@Override
    	public String toString()
    	{
    		return String.valueOf(value);
    	}
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
		
		public int getValue()
		{
			return value;
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
			return Invalid;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(value);
		}
	}
	
	public enum MessageId
	{
		Invalid(0),
		UnkownRequest(1),
		TimeoutRequest(2),
		ConnectionErrorEvent(3),
		
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
		BusinessSessionResponse(405),
		ProxyDataSyncRequest(500),
		ProxyDataSyncResponse(600);
		
		private int value ;
		
		private MessageId(int messageId)
		{
			this.value = messageId;
		}
		
		public int getValue()
		{
			return value;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(this.value);
		}
		
		public static MessageId parseValue(int value)
		{
			for (MessageId item : values())
			{
				if (item.value == value)
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
}

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
	public enum SolidAssessLabel
	{
		Invalid(""), Good("Ï²»¶"), Normal("Ò»°ã"), Bad("·´¸Ð");
		private String value;
		private SolidAssessLabel(String value)
		{
			this.value = value;
		}
		
		public String getValue()
		{
			return value;
		}
		
		@Override
		public String toString()
		{
			return value;
		}
		
		public static boolean isSolidAssessLabel(String label)
		{
			for (SolidAssessLabel tmpValue : values())
			{
				if (tmpValue.value.equals(label))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	public enum ServiceStatus
	{
		Invalid(0), Normal(1), Banned(2);
		
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
			int intValue = Integer.valueOf(value);
			for (ServiceStatus item : values())
			{
				if (item.value == intValue)
				{
					return item;
				}
			}
			return null;
		}
		
		public static ServiceStatus parseFromStringValue(String value)
		{
			for (ServiceStatus item : values())
			{
				if (item.name().equals(value))
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
	

	
	public enum NotificationType
	{
		Invalid(0),
		SessionBinded(1), 
		OthersideRejected(2),
		OthersideAgreed(3), 
		OthersideLost(4),
		OthersideEndChat(5);
		
		private int value;
		private NotificationType(int value)
		{
			this.value = value;
		}
	
		public int getValue()
		{
			return value;
		}
		
		public static NotificationType parseValue(int value)
		{
			int intValue = Integer.valueOf(value);
			for (NotificationType item : values())
			{
				if (item.value == intValue)
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
	
	public enum BusinessStatus
	{
		Invalid(0), Init(1), Idle(2), WaitMatch(3), SessionBound(4);
		
		private int value;
		private BusinessStatus(int value)
		{
			this.value = value;
		}
	
		public int getValue()
		{
			return value;
		}
		
		public static BusinessStatus parseValue(int value)
		{
			int intValue = Integer.valueOf(value);
			for (BusinessStatus item : values())
			{
				if (item.value == intValue)
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
	
	public enum SessionEndReason
	{
		Invalid(0), Reject(1), ConnectionLoss(2), NormalEnd(3), CheckFailed(4);
		
		private int value;
		private SessionEndReason(int value)
		{
			this.value = value;
		}
	
		public int getValue()
		{
			return value;
		}
		
		public static SessionEndReason parseValue(int value)
		{
			int intValue = Integer.valueOf(value);
			for (SessionEndReason item : values())
			{
				if (item.value == intValue)
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

	public enum YesNo
	{
		Yes(1),
		No(0);
		
		private int value;
		private YesNo(int value)
		{
			this.value = value;
		}
	
		public int getValue()
		{
			return value;
		}
		
		public static YesNo parseValue(int value)
		{
			for (YesNo item : values())
			{
				if (item.value == value)
				{
					return item;
				}
			}
			return null;
		}
		
		public static YesNo parseValue(String value)
		{
			for (YesNo item : values())
			{
				if (item.name().equals(value))
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
	
	public enum SuccessOrFail
	{
		Success(1),
		Fail(0);
		
		private int value;
		private SuccessOrFail(int value)
		{
			this.value = value;
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
	
	public enum BusinessSessionStatus
	{
		Invalid(0), Idle(1), ChatConfirm(2), VideoChat(3), Assess(4);
		
		private int value;
		private BusinessSessionStatus(int value)
		{
			this.value = value;
		}
	
		public int getValue()
		{
			return value;
		}
		
		public static BusinessSessionStatus parseValue(String value)
		{
			int intValue = Integer.valueOf(value);
			for (BusinessSessionStatus item : values())
			{
				if (item.value == intValue)
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
	
	public enum BusinessType
	{
		Invalid(0), Random(1), Interest(2);
		
		private int value;
		private BusinessType(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static BusinessType parseValue(int value)
		{
			for (BusinessType item : values())
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
	
	public enum OperationType
    {
    	Invalid(0),
		EnterPool(1),
    	LeavePool(2),
    	AgreeChat(3),
    	RejectChat(4),
    	EndChat(5),

    	AssessAndContinue(6),
    	AssessAndQuit(7);

    	private int value;
    	
    	private OperationType(int value)
    	{
    		this.value = value; 
    	}
    	
    	public int getValue()
		{
			return value;
		}
    	
		public static OperationType parseValue(int value)
		{
			for (OperationType item : values())
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
	
	public enum ConnectionSetting
	{
		ByteBufferMaxSize (2097152),
		CharBufferMaxSize (2097152);
		
		private int value;
		
		public int getValue()
		{
			return value;
		}
		
		private ConnectionSetting(int value)
		{
			this.value = value;
		}
	}
}

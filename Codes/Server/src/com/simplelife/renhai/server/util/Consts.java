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
		Invalid(""), Good("^#Happy#^"), Normal("^#SoSo#^"), Bad("^#Disgusting#^");
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
	
	public enum StatusChangeReason
	{
		Invalid(0), 
		WebsocketClosedByApp(101),
		WebsocketClosedByServer(102),
		ExceptionOnWebsocket(103),
		TimeoutOnSyncSending(104),
		TimeoutOfPing(105),
		TimeoutOfActivity(106),
		BannedDevice(107),
		AppLeaveBusiness(108),
		AssessAndQuit(109),
		AssessAndContinue(110),
		SessionEnded(111),
		UnknownBusinessException(112),
		BusinessSessionStarted(113),
		AppDataSynchronize(114),
		AppEnterBusiness(115),
		AppRejectChat(116),
		AppUnbindSession(117);
		
		private int value;
		private StatusChangeReason(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static StatusChangeReason parseValue(int value)
		{
			for (StatusChangeReason item : values())
			{
				if (item.value == value)
				{
					return item;
				}
			}
			return null;
		}
	}
	
	public enum ValidInvalid
	{
		Valid(1), Invalid(0);
		private int value;
		private ValidInvalid(int value)
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
		
		public static boolean isValidFlag(String label)
		{
			for (ValidInvalid tmpValue : values())
			{
				if (tmpValue.name().equals(label))
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
			for (ServiceStatus item : values())
			{
				if (item.value == value)
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
	
	public enum StatisticsItem
	{
		Invalid(0),
		OnlineDeviceCount(1),
		RandomDeviceCount(2),
		InterestDeviceCount(3),
		ChatDeviceCount(4),
		RandomChatDeviceCount(5),
		InterestChatDeviceCount(6),
		HotInterestLabel(7);
		
		private int value;
		private StatisticsItem(int value)
		{
			this.value = value;
		}
	
		public int getValue()
		{
			return value;
		}
		
		public static StatisticsItem parseValue(int value)
		{
			for (StatisticsItem item : values())
			{
				if (item.value == value)
				{
					return item;
				}
			}
			return Invalid;
		}
	}
	
	public enum NotificationType
	{
		Invalid(0),
		SessionBound(1), 
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
			for (NotificationType item : values())
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
	
	public enum BusinessStatus
	{
		Invalid(0), Offline(1), Init(2), Idle(3), WaitMatch(4), SessionBound(5);
		
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
			for (BusinessStatus item : values())
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
	
	public enum BusinessProgress
	{
		Invalid(0), 
		Init(1), 
		SessionBoundConfirmed(2), 
		ChatConfirmed(3), 
		ChatEnded(4), 
		AssessFinished(5);
		
		private int value;
		private BusinessProgress(int value)
		{
			this.value = value;
		}
	
		public int getValue()
		{
			return value;
			
		}
		
		public static BusinessProgress parseValue(int value)
		{
			for (BusinessProgress item : values())
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
	
	public enum SessionEndReason
	{
		Invalid(0), Reject(1), ConnectionLoss(2), NormalEnd(3), CheckFailed(4), AllDeviceRemoved(5);
		
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
			for (SessionEndReason item : values())
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
    	AssessAndQuit(7),
    	SessionUnbind(8);

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
	
	public enum SystemModule
	{
		business(1),
		db(2),
		json(3),
		log(4),
		servlets(5),
		util(6),
		websocket(7);
		
		private int value;
    	
    	private SystemModule(int value)
    	{
    		this.value = value;
    	}
    	
    	public int getValue()
		{
			return value;
		}
	}
	
	public enum OperationCode
	{
		SetupWebScoket_1001(1001),
		AlohaRequest_1002(1002),
		AlohaResponse_1003(1003),
		AppDataSyncRequest_1004(1004),
		AppDataSyncResponse_1007(1007),
		ServerDataSyncRequest_1008(1008),
		ServerDataSyncResponse_1009(1009),
		NotificationSessionBound_1010(1010),
		NotificationOthersideRejected_1011(1011),
		NotificationOthersideAgreed_1012(1012),
		NotificationResponse_1013(1013),
		BusinessRequestEnterPool_1014(1014),
		BusinessRequestLeavePool_1015(1015),
		BusinessRequestAgreeChat_1016(1016),
		BusinessRequestRejectChat_1017(1017),
		BusinessRequestEndChat_1018(1018),
		BusinessRequestAssessContinue_1019(1019),
		BusinessRequestAssessQuit_1020(1020),
		BusinessResponse_1021(1021),
		BroadcastNotification_1022(1022),
		WebsocketTimeout_1100(1100),
		DeviceInIdleTimeout_1101(1101),
		SyncMessageTimeout_1102(1102),
		FlushToDB_1103(1103),
		UpdateWebRTCSession_1104(1104);

    	private int value;
    	
    	private OperationCode(int value)
    	{
    		this.value = value;
    	}
    	
    	public int getValue()
		{
			return value;
		}
	}
}

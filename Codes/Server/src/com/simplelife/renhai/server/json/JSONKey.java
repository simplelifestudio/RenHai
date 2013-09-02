/**
 * JSONKey.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;


/** */
public class JSONKey
{
	public class Command
	{
		public final static String ErrorResponse				=  "ErrorResponse";
		public final static String AlohaRequest					=  "AlohaRequest";
		public final static String AlohaResponse				=  "AlohaResponse";
		public final static String AppDataSyncRequest			=  "AppDataSyncRequest";
		public final static String AppDataSyncResponse			=  "AppDataSyncResponse";

		public final static String ServerDataSyncRequest		=  "ServerDataSyncRequest";
		public final static String ServerDataSyncResponse		=  "ServerDataSyncResponse";
		public final static String BusinessSessionNotification	=  "BusinessSessionNotification";
		public final static String BusinessSessionNotificationResponse=  "BusinessSessionNotificationResponse";
		public final static String BusinessSessionRequest		=  "BusinessSessionRequest";

		public final static String BusinessSessionResponse		=  "BusinessSessionResponse";
		public final static String BroadcastNotification		=  "BroadcastNotification";
	}
	
    public class OperationType
    {
    	public final static String EnterPool					=  "EnterPool";
    	public final static String LeavePool					=  "LeavePool";
    	public final static String AgreeChat					=  "AgreeChat";
    	public final static String RejectChat					=  "RejectChat";
    	public final static String EndChat						=  "EndChat";

    	public final static String Assess						=  "Assess";
    	public final static String AssessAndQuit				=  "AssessAndQuit";
    	public final static String Received						=  "Received";
    }
    
    public class BusinessType
    {
    	public final static String Interest 					= "Interest";
        public final static String Random 						= "Random";
    }
    
    public class FieldName
    {
    	public final static String AppRequestMessage			= "appRequestMessage";
    	public final static String AppVersion					= "appVersion";
    	public final static String Body							= "body";
    	public final static String BusinessSessionId			= "businessSessionId";
    	public final static String BusinessType					= "businessType";
    	
    	public final static String ChatDeviceCount				= "chatDeviceCount";
    	public final static String ChatLossCount				= "chatLossCount";
    	public final static String ChatTotalCount				= "chatTotalCount";
    	public final static String Content						= "content";
    	public final static String Count						= "count";
    	
    	public final static String CurrentHotInterestLabels		= "currentHotInterestLabels";
    	public final static String DeviceId						= "deviceId";
    	public final static String DeviceSn						= "deviceSn";
    	public final static String ErrorCode					= "errorCode";
    	public final static String ErrorDescription				= "errorDescription";
    	
    	public final static String ForbiddenExpiredDate			= "forbiddenExpiredDate";
    	public final static String Head							= "head";
    	public final static String HistoryHotInterestLabels		= "historyHotInterestLabels";
    	public final static String ImpressCardId				= "impressCardId";
    	public final static String ImpressId					= "impressId";
    	
    	public final static String ImpressName					= "impressName";
    	public final static String InterestChatDeviceCount		= "interestChatDeviceCount";
    	public final static String InterestDeviceCount			= "interestDeviceCount";
    	public final static String IsJailed						= "isJailed";
    	public final static String LabelListCount				= "labelListCount";
    	
    	public final static String Location						= "location";
    	public final static String MatchCount					= "matchCount";
    	public final static String MessageId					= "messageId";
    	public final static String OnlineDeviceCount			= "onlineDeviceCount";
    	public final static String OperationType				= "operationType";
    	
    	public final static String OperationValue				= "operationValue";
    	public final static String Order						= "order";
    	public final static String OsVersion					= "osVersion";
    	public final static String RegisterTime					= "registerTime";
    	public final static String ServiceStatus				= "serviceStatus";
    	
    	public final static String SessionId					= "sessionId";
    	public final static String TimeStamp					= "timeStamp";
    	public final static String TotalChatDuration			= "totalChatDuration";
    	public final static String DataQuery					= "dataQuery";
    	public final static String DeviceCard					= "deviceCard";
    	
    	public final static String ImpressCard					= "impressCard";
    	public final static String InterestCard					= "interestCard";
    	public final static String DataUpdate					= "dataUpdate";
    	public final static String DeviceModel					= "deviceModel";
    }
}

/**
 * JSONKey.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;


/** */
public class JSONKey
{
	public class MessageId
	{
		public final static String ServerErrorResponse			=  "ServerErrorResponse";
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
    	public final static String Header						= "header";
    	public final static String HistoryHotInterestLabels		= "historyHotInterestLabels";
    	public final static String ImpresscardId				= "impressCardId";
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
    	public final static String Devicecard					= "deviceCard";
    	
    	public final static String Impresscard					= "impressCard";
    	public final static String Interestcard					= "interestCard";
    	public final static String DataUpdate					= "dataUpdate";
    	public final static String DeviceModel					= "deviceModel";
    	public final static String MessageType					= "messageType";
    	
    	public final static String MessageSn					= "messageSn";
    	public final static String JsonEnvelope					= "jsonEnvelope";
    	public final static String ReceivedMessage				= "receivedMessage";
    }
}

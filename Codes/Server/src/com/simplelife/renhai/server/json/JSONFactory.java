/**
 * JSONFactory.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class JSONFactory
{
	/** */
	public static AppJSONMessage createAppJSONMessage(JSONObject obj)
	{
		if (!obj.containsKey(JSONKey.FieldName.JsonEnvelope))
		{
			return null;
		}
		
		JSONObject messageObject = obj.getJSONObject(JSONKey.FieldName.JsonEnvelope);
		
		AppJSONMessage message = new AppJSONMessage(messageObject); 
		
		if (!message.checkJsonCommand())
		{
			return message;
		}
		
		int messageId = message.getMessageId();
		switch (messageId)
		{
			case Consts.MessageId.AlohaRequest:
				message = new AlohaRequest(messageObject);
				break;
				
			case Consts.MessageId.AppDataSyncRequest:
				message = new AppDataSyncRequest(messageObject);
				break;
				
			case Consts.MessageId.AppErrorResposne:
				message = new AppErrorResposne(messageObject);
				break;
				
			case Consts.MessageId.BusinessSessionNotificationResponse:
				message = new BusinessSessionNotificationResponse(messageObject);
				break;
				
			case Consts.MessageId.BusinessSessionRequest:
				message = new BusinessSessionRequest(messageObject);
				break;
				
			case Consts.MessageId.ServerDataSyncRequest:
				message = new ServerDataSyncRequest(messageObject);
				break;
		}
		
		return message;
	}
	
	/** */
	public static ServerJSONMessage createServerJSONMessage(int messageId, IDeviceWrapper deviceWrapper)
	{
		ServerJSONMessage message = null;
		switch (messageId)
		{
			case Consts.MessageId.AlohaResponse:
				message = new AlohaResponse(deviceWrapper);
				break;
				
			case Consts.MessageId.AppDataSyncResponse:
				message = new AppDataSyncResponse(deviceWrapper);
				break;
				
			case Consts.MessageId.BroadcastNotification:
				message = new BroadcastNotification();
				break;
				
			case Consts.MessageId.BusinessSessionNotification:
				message = new BusinessSessionNotification(deviceWrapper);
				break;
				
			case Consts.MessageId.BusinessSessionResponse:
				message = new BusinessSessionResponse(deviceWrapper);
				break;
				
			case Consts.MessageId.ServerDataSyncResponse:
				message = new ServerDataSyncResponse(deviceWrapper);
				break;
				
			case Consts.MessageId.ServerErrorResponse:
				message = new ServerErrorResponse(deviceWrapper);
				break;
		}
		return message;
	}
}

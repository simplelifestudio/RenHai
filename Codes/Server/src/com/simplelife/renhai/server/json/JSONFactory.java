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
		AppJSONMessage message;
		if (obj == null)
		{
			message = new InvalidRequest(null);
			message.setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
			message.setErrorDescription("Invalid JSON string.");
			return message;
		}
		
		if (!obj.containsKey(JSONKey.FieldName.JsonEnvelope))
		{
			message = new InvalidRequest(obj);
			message.setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
			message.setErrorDescription("Invalid JSON request: " + JSONKey.FieldName.JsonEnvelope + " was not found.");
			return message;
		}
		
		JSONObject messageObject = obj.getJSONObject(JSONKey.FieldName.JsonEnvelope);
		message = new InvalidRequest(messageObject); 
		if (!message.checkJsonCommand())
		{
			return message;
		}
		
		Consts.MessageId messageId = message.getMessageId();
		switch (messageId)
		{
			case AlohaRequest:
				message = new AlohaRequest(messageObject);
				break;
				
			case AppDataSyncRequest:
				message = new AppDataSyncRequest(messageObject);
				break;
				
			case AppErrorResposne:
				message = new AppErrorResposne(messageObject);
				break;
				
			case BusinessSessionNotificationResponse:
				message = new BusinessSessionNotificationResponse(messageObject);
				break;
				
			case BusinessSessionRequest:
				message = new BusinessSessionRequest(messageObject);
				break;
				
			case ServerDataSyncRequest:
				message = new ServerDataSyncRequest(messageObject);
				break;
				
			default:
				break;
		}
		
		return message;
	}
	
	/** */
	public static ServerJSONMessage createServerJSONMessage(Consts.MessageId messageId, IDeviceWrapper deviceWrapper)
	{
		ServerJSONMessage message = null;
		switch (messageId)
		{
			case AlohaResponse:
				message = new AlohaResponse(deviceWrapper);
				break;
				
			case AppDataSyncResponse:
				message = new AppDataSyncResponse(deviceWrapper);
				break;
				
			case BroadcastNotification:
				message = new BroadcastNotification();
				break;
				
			case BusinessSessionNotification:
				message = new BusinessSessionNotification(deviceWrapper);
				break;
				
			case BusinessSessionResponse:
				message = new BusinessSessionResponse(deviceWrapper);
				break;
				
			case ServerDataSyncResponse:
				message = new ServerDataSyncResponse(deviceWrapper);
				break;
				
			case ServerErrorResponse:
				message = new ServerErrorResponse(deviceWrapper);
				break;
				
			default:
				break;
		}
		return message;
	}
}

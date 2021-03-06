/**
 * JSONFactory.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class JSONFactory
{
	private static Logger logger = BusinessModule.instance.getLogger();
	private static Consts.MessageId parseRequestMessageId(JSONObject messageObject)
	{
		try
		{
			JSONObject header = messageObject.getJSONObject(JSONKey.Header);
			String strMessageId = header.getString(JSONKey.MessageId);
			if (strMessageId == null || strMessageId.length() == 0)
			{
				logger.debug("Message ID is missed");
				return Consts.MessageId.Invalid;
			}
			return Consts.MessageId.getEnumItemByValue(strMessageId);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		return Consts.MessageId.Invalid;
	}
	
	
	/** */
	public static AppJSONMessage createAppJSONMessage(JSONObject messageObject)
	{
		logger.debug("Enter createAppJSONMessage()");
		
		AppJSONMessage message = new UnkownRequest(messageObject); 
		if (!message.checkJSONRequest())
		{
			if (messageObject == null)
			{
				logger.debug("Failed to validate request");
			}
			else
			{
				logger.debug("Failed to validate request: " + messageObject.toJSONString());
			}
			return message;
		}
		
		Consts.MessageId messageId = parseRequestMessageId(messageObject);
		logger.debug("Enclosed message ID: " + messageId.name());
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
	public static ServerJSONMessage createServerJSONMessage(AppJSONMessage request, Consts.MessageId responseMessageId)
	{
		ServerJSONMessage message = null;
		switch (responseMessageId)
		{
			case AlohaResponse:
				message = new AlohaResponse(request);
				break;
				
			case AppDataSyncResponse:
				message = new AppDataSyncResponse(request);
				break;
				
			case BroadcastNotification:
				message = new BroadcastNotification();
				break;
				
			case BusinessSessionNotification:
				message = new BusinessSessionNotification(request);
				break;
				
			case BusinessSessionResponse:
				message = new BusinessSessionResponse(request);
				break;
				
			case ServerDataSyncResponse:
				message = new ServerDataSyncResponse(request);
				break;
				
			case ServerErrorResponse:
				message = new ServerErrorResponse(request);
				break;
				
			default:
				break;
		}
		
		if (message != null)
		{
			message.setMessageId(responseMessageId);
		}
		
		return message;
	}
}

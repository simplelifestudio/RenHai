/**
 * JSONFactory.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.proxyserver;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/** */
public class JSONFactory
{
	private static Logger logger = LoggerFactory.getLogger(JSONFactory.class);
	
	
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
	public static AbstractJSONMessage createAppJSONMessage(JSONObject messageObject, PrintWriter out)
	{
		logger.debug("Enter createAppJSONMessage()");
		
		AppJSONMessage message = new AppJSONMessage(messageObject, out);
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
			return null;
		}
		
		message = null;
		Consts.MessageId messageId = parseRequestMessageId(messageObject);
		logger.debug("Enclosed message ID: " + messageId.name());
		switch (messageId)
		{
			case ProxyDataSyncRequest:
				message = new ProxyDataSyncRequest(messageObject, out);
				break;
				
			default:
				break;
		}
		
		return message;
	}
}

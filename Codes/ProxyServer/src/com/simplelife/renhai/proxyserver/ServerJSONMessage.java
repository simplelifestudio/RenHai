/**
 * ServerJSONMessage.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.proxyserver;

import java.io.PrintWriter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/** */
public abstract class ServerJSONMessage extends AbstractJSONMessage
{
    private PrintWriter out;
    
    public boolean isSyncMessage()
    {
    	return false;
    }
    
	public JSONObject getJsonObject()
	{
		return jsonObject;
	}

	public JSONObject getHeader()
	{
		return header;
	}

	public JSONObject getBody()
	{
		return body;
	}

    
    public ServerJSONMessage(AppJSONMessage request, PrintWriter out)
    {
    	this.out = out;
    	jsonObject = new JSONObject();
    	header = new JSONObject();
    	body = new JSONObject();
    	
    	init(request);
    }

	protected void init(AppJSONMessage request)
    {
    	jsonObject.clear();
    	jsonObject.put(JSONKey.Header, header);
    	jsonObject.put(JSONKey.Body, body);
    	
    	if (request == null)
    	{
    		addToHeader(JSONKey.MessageSn, CommonFunctions.getRandomString(16));
    	}
    	else
    	{
    		//logger.debug("request.getMessageSn():" + request.getMessageSn());
    		addToHeader(JSONKey.MessageSn, request.getMessageSn());
    	}
    }
    
    /** */
    public JSONObject toJSONObject()
    {
    	return jsonObject; 
    }
    
    public void addToHeader(String key, Object object)
    {
    	header.put(key, object);
    }
    
    public void addToBody(String key, Object object)
    {
    	body.put(key, object);
    }
    
    protected void setMessageType(Consts.MessageType messageType)
    {
    	addToHeader(JSONKey.MessageType, messageType.getValue());
    }
    
    protected void setMessageSn(String messageSn)
    {
    	addToHeader(JSONKey.MessageSn, messageSn);
    }
    
    protected void setMessageId(Consts.MessageId messageId)
    {
    	addToHeader(JSONKey.MessageId, messageId.getValue());
    }
    
    public Consts.MessageType getMessageType()
    {
    	return Consts.MessageType.valueOf(header.getString(JSONKey.MessageType));
    }

    public String getMessageSn()
    {
    	return header.getString(JSONKey.MessageSn);
    }
    
    public abstract Consts.MessageId getMessageId();
    
    @Override
    public void run()
    {
    	JSONObject whole = new JSONObject();
    	whole.put(JSONKey.JsonEnvelope, jsonObject);
    	String response = JSON.toJSONString(whole, true);
    	logger.debug("Send to client:\n" + response);
    	out.write(response);
    }

    @Override
    public String toString()
    {
    	return JSON.toJSONString(jsonObject, true); 
    }
}

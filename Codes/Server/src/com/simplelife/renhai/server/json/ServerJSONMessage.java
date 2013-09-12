/**
 * ServerJSONMessage.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;

import java.util.HashMap;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IJSONObject;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public abstract class ServerJSONMessage extends AbstractJSONMessage implements IJSONObject
{
	protected JSONObject jsonObject = new JSONObject();
    protected JSONObject header = new JSONObject();
    protected JSONObject body = new JSONObject();
    
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

    
    protected ServerJSONMessage(AppJSONMessage request)
    {
    	this.deviceWrapper = request.getDeviceWrapper();
    	init(request);
    }
    
    protected void init(AppJSONMessage request)
    {
    	Logger logger = JSONModule.instance.getLogger();
    	jsonObject.clear();
    	jsonObject.put(JSONKey.Header, header);
    	jsonObject.put(JSONKey.Body, body);
    	
    	addToHeader(JSONKey.MessageSn, request.getMessageSn());
		addToHeader(JSONKey.TimeStamp, DateUtil.getNow());
    	
    	Device device = request.getDeviceWrapper().getDevice();
    	if (device == null)
    	{
    		//addToHeader(JSONKey.MessageType, 2);
    		addToHeader(JSONKey.DeviceId, "");
    		addToHeader(JSONKey.DeviceSn, "");
    	}
    	else
    	{
    		addToHeader(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
    		addToHeader(JSONKey.DeviceSn, deviceWrapper.getDevice().getDeviceSn());
    	}
    }
    
    /** */
    public JSONObject toJSONObject()
    {
    	return jsonObject; 
    }
    
    protected void addToHeader(String key, Object object)
    {
    	header.put(key, object);
    }
    
    protected void addToBody(String key, Object object)
    {
    	body.put(key, object);
    }
    
    protected void setMessageType(Consts.MessageType messageType)
    {
    	addToHeader(JSONKey.MessageType, String.valueOf(messageType.ordinal()));
    }
    
    protected void setMessageSn(String messageSn)
    {
    	addToHeader(JSONKey.MessageSn, messageSn);
    }
    
    protected void setMessageId(Consts.MessageId messageId)
    {
    	addToHeader(JSONKey.MessageId, messageId.toString());
    }
    
    public Consts.MessageType getMessageType()
    {
    	return Consts.MessageType.valueOf(header.get(JSONKey.MessageType).toString());
    }

    public String getMessageSn()
    {
    	return header.get(JSONKey.MessageSn).toString();
    }
    
    public abstract Consts.MessageId getMessageId();
    
    
    protected void asyncResponse()
    {
    	Logger logger = JSONModule.instance.getLogger();
    	if (deviceWrapper == null)
    	{
    		logger.error("deviceWrapper is null in response");
    		return;
    	}
    	
    	deviceWrapper.asyncSendMessage(this);
    }
    
    protected void syncResponse()
    {
    	if (deviceWrapper == null)
    	{
    		// TODO: log error here
    		return;
    	}
    	
    	deviceWrapper.syncSendMessage(this);
    }

    @Override
    public String toString()
    {
    	return toJSONObject().toJSONString(); 
    }
}

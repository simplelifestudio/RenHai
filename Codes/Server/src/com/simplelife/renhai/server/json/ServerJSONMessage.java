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
import com.simplelife.renhai.server.business.device.Device;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IJSONObject;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public abstract class ServerJSONMessage extends AbstractJSONMessage implements IJSONObject
{
	protected HashMap<String, Object> jsonMap = new HashMap<String, Object>();
    protected HashMap<String, Object> jsonMapHeader = new HashMap<String, Object>();
    protected HashMap<String, Object> jsonMapBody = new HashMap<String, Object>();
    
    protected ServerJSONMessage(AppJSONMessage request)
    {
    	this.deviceWrapper = request.getDeviceWrapper();
    	init(request);
    }
    
    protected void init(AppJSONMessage request)
    {
    	Logger logger = JSONModule.instance.getLogger();
    	jsonMap.clear();
    	jsonMap.put(JSONKey.FieldName.Header, jsonMapHeader);
    	jsonMap.put(JSONKey.FieldName.Body, jsonMapBody);
    	
    	addToHeader(JSONKey.FieldName.MessageSn, request.getMessageSn());
		addToHeader(JSONKey.FieldName.TimeStamp, DateUtil.getNow());
    	
    	Device device = request.getDeviceWrapper().getDevice();
    	if (device == null)
    	{
    		//addToHeader(JSONKey.FieldName.MessageType, 2);
    		addToHeader(JSONKey.FieldName.DeviceId, "");
    		addToHeader(JSONKey.FieldName.DeviceSn, "");
    	}
    	else
    	{
    		Devicecard card = deviceWrapper.getDevice().getDevicecard();
    		addToHeader(JSONKey.FieldName.DeviceId, card.getDeviceId());
    		addToHeader(JSONKey.FieldName.DeviceSn, card.getDeviceSn());
    	}
    }
    
    /** */
    public JSONObject toJSONObject()
    {
    	return new JSONObject(jsonMap); 
    }
    
    protected void addToHeader(String key, Object object)
    {
    	jsonMapHeader.put(key, object);
    }
    
    protected void addToBody(String key, Object object)
    {
    	jsonMapBody.put(key, object);
    }
    
    protected void setMessageType(Consts.MessageType messageType)
    {
    	addToHeader(JSONKey.FieldName.MessageType, String.valueOf(messageType.ordinal()));
    }
    
    protected void setMessageSn(String messageSn)
    {
    	addToHeader(JSONKey.FieldName.MessageSn, messageSn);
    }
    
    protected void setMessageId(Consts.MessageId messageId)
    {
    	addToHeader(JSONKey.FieldName.MessageId, messageId.toString());
    }
    
    public Consts.MessageType getMessageType()
    {
    	return Consts.MessageType.valueOf(jsonMapHeader.get(JSONKey.FieldName.MessageType).toString());
    }

    public String getMessageSn()
    {
    	return jsonMapHeader.get(JSONKey.FieldName.MessageSn).toString();
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

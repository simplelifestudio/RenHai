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

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.DeviceCard;
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
    
    protected ServerJSONMessage(IDeviceWrapper deviceWrapper)
    {
    	init();
    	this.deviceWrapper = deviceWrapper;
    }
    
    protected void init()
    {
    	jsonMap.clear();
    	jsonMap.put(JSONKey.FieldName.Head, jsonMapHeader);
    	jsonMap.put(JSONKey.FieldName.Body, jsonMapBody);
    	
    	jsonMapHeader.put(JSONKey.FieldName.TimeStamp, DateUtil.getNow());
    	
    	if (deviceWrapper != null)
    	{
    		DeviceCard card = deviceWrapper.getDevice().getDeviceCard();
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
    
    protected void setMessageId(String messageId)
    {
    	addToHeader(JSONKey.FieldName.MessageId, messageId);
    }
    
    public Consts.MessageType getMessageType()
    {
    	return Consts.MessageType.valueOf(jsonMapHeader.get(JSONKey.FieldName.MessageType).toString());
    }

    public String getMessageSn()
    {
    	return jsonMapHeader.get(JSONKey.FieldName.MessageSn).toString();
    }
    
    public Consts.MessageId getMessageId()
    {
    	return Consts.MessageId.valueOf(jsonMapHeader.get(JSONKey.FieldName.MessageId).toString());
    }
    
    protected void asyncResponse()
    {
    	if (deviceWrapper == null)
    	{
    		// TODO: log error here
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

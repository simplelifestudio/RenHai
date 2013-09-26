/**
 * ServerJSONMessage.java
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
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IJSONObject;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public abstract class ServerJSONMessage extends AbstractJSONMessage implements IJSONObject
{
	protected JSONObject jsonObject = new JSONObject();
    protected JSONObject header = new JSONObject();
    protected JSONObject body = new JSONObject();
    private Logger logger = BusinessModule.instance.getLogger();
    
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
    	if (request != null)
    	{
    		this.deviceWrapper = request.getDeviceWrapper();
    	}
    	init(request);
    }
    
    protected void init(AppJSONMessage request)
    {
    	jsonObject.clear();
    	jsonObject.put(JSONKey.Header, header);
    	jsonObject.put(JSONKey.Body, body);
    	
    	if (request == null)
    	{
    		addToHeader(JSONKey.MessageSn, CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfMessageSn));
    		addToHeader(JSONKey.DeviceId, null);
    		addToHeader(JSONKey.DeviceSn, null);
    	}
    	else
    	{
    		addToHeader(JSONKey.MessageSn, request.getMessageSn());
    		
    		Device device = request.getDeviceWrapper().getDevice();
        	if (device == null)
        	{
        		//addToHeader(JSONKey.MessageType, 2);
        		addToHeader(JSONKey.DeviceId, null);
        		addToHeader(JSONKey.DeviceSn, null);
        	}
        	else
        	{
        		addToHeader(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
        		addToHeader(JSONKey.DeviceSn, deviceWrapper.getDevice().getDeviceSn());
        	}
    	}
		
    	addToHeader(JSONKey.TimeStamp, DateUtil.getNow());
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
    
    
    public void asyncResponse()
    {
    	Logger logger = JSONModule.instance.getLogger();
    	if (deviceWrapper == null)
    	{
    		logger.error("deviceWrapper is null in response");
    		return;
    	}
    	
    	deviceWrapper.asyncSendMessage(this);
    }
    
    public void syncResponse()
    {
    	if (deviceWrapper == null)
    	{
    		logger.error("deviceWrapper of ServerJSONMessage is null! message Id: {}", this.header.getString(JSONKey.MessageId));
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

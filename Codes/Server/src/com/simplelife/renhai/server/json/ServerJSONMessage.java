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
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IJSONObject;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public abstract class ServerJSONMessage extends AbstractJSONMessage implements IJSONObject
{
    private Logger logger = BusinessModule.instance.getLogger();

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

    
    protected ServerJSONMessage(AppJSONMessage request)
    {
    	jsonObject = new JSONObject();
    	header = new JSONObject();
    	body = new JSONObject();
    	
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
        		if (request.getHeader() != null)
        		{
	        		addToHeader(JSONKey.DeviceId, request.getHeader().getString(JSONKey.DeviceId));
	        		addToHeader(JSONKey.DeviceSn, request.getHeader().getString(JSONKey.DeviceSn));
        		}
        		else
        		{
        			addToHeader(JSONKey.DeviceId, null);
	        		addToHeader(JSONKey.DeviceSn, null);
        		}
        	}
        	else
        	{
        		addToHeader(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
        		addToHeader(JSONKey.DeviceSn, deviceWrapper.getDevice().getDeviceSn());
        	}
    	}
		
    	//addToHeader(JSONKey.TimeStamp, DateUtil.getNow());
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
    	if (deviceWrapper == null)
    	{
    		logger.error("DeviceWrapper of ServerJSONMessage is null! message Id: {}", this.header.getString(JSONKey.MessageId));
    		return;
    	}
    	
    	// Update current time again before sending
    	//addToHeader(JSONKey.TimeStamp, DateUtil.getNow());
    	int duration = this.getQueueDuration();
    	if (duration > GlobalSetting.BusinessSetting.MessageQueueTime)
    	{
    		logger.warn("Response with SN {} was queued " + duration + "ms ago, consider increasing size of output message execution thread pool.", this.getMessageSn());
    	}
    	
    	if (this.isSyncMessage())
    	{
    		deviceWrapper.syncSendMessage(this);
    	}
    	else
    	{
    		deviceWrapper.asyncSendMessage(this);
    	}
    	
    }

    @Override
    public String toString()
    {
    	return toJSONObject().toJSONString(); 
    }
}

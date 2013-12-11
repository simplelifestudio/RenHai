/**
 * AbstractJSONMessage.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IRunnableMessage;

/**
 * 
 */
public abstract class AbstractJSONMessage implements IRunnableMessage
{
	protected Logger logger = BusinessModule.instance.getLogger();
	
    protected JSONObject jsonObject;
    protected JSONObject header;
    protected JSONObject body;
	
	protected IDeviceWrapper deviceWrapper;
	
    public abstract Consts.MessageType getMessageType();

    public abstract String getMessageSn();
    
    public abstract Consts.MessageId getMessageId();
    
    protected long queueTime;
    protected int delayOfHandle;
    
    
    public String getMessageName()
    {
    	return this.getMessageId().name();
    }
    
    public String getMsgOwnerInfo()
    {
    	return deviceWrapper.getDeviceIdentification();
    }
    
	public int getDelayOfHandle()
	{
		return delayOfHandle;
	}

	public void setDelayOfHandle(int delayOfHandle)
	{
		this.delayOfHandle = delayOfHandle;
	}

	public long getQueueTime()
	{
		return queueTime;
	}
	
	public int getQueueDuration()
	{
		return (int) (System.currentTimeMillis() - queueTime);
	}
	
	public abstract void run();
	
	public void setQueueTime(long queueTime)
	{
		this.queueTime = queueTime;
	}
    
    public IDeviceWrapper getDeviceWrapper()
    {
    	return deviceWrapper;
    }
    
    public void setDeviceWrapper(IDeviceWrapper deviceWrapper)
    {
    	this.deviceWrapper = deviceWrapper;
    }
    public JSONArray getJSONArray(JSONObject obj, String key)
    {
    	if (!obj.containsKey(key))
    	{
    		return null;
    	}
    	
    	String temp = obj.getString(key);
    	if (temp == null)
    	{
    		return null;
    	}
    	
    	if (temp.trim().length() == 0)
    	{
    		return null;
    	}
    	
    	JSONArray returnObj = obj.getJSONArray(key);
    	
    	if (returnObj == null)
    	{
    		return null;
    	}
    	
    	if (returnObj.isEmpty())
    	{
    		return null;
    	}
    	
    	return returnObj;
    }
    
    public JSONObject getJSONObject(JSONObject obj, String key)
    {
    	if (obj == null)
    	{
    		return null;
    	}
    	
    	if (!obj.containsKey(key))
    	{
    		return null;
    	}
    	
    	String temp = obj.getString(key);
    	if (temp == null)
    	{
    		return null;
    	}
    	
    	if (temp.trim().length() == 0)
    	{
    		return null;
    	}
    	
    	JSONObject returnObj = obj.getJSONObject(key);
    	
    	if (returnObj == null)
    	{
    		return null;
    	}
    	
    	if (returnObj.isEmpty())
    	{
    		return null;
    	}
    	
    	return returnObj;
    }
    
    public String toReadableString()
    {
    	return JSON.toJSONString(this.jsonObject, true);
    }
}

/**
 * AbstractJSONMessage.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 */
public abstract class AbstractJSONMessage implements Runnable
{
	protected Logger logger = LoggerFactory.getLogger(AbstractJSONMessage.class);
	
    protected JSONObject jsonObject;
    protected JSONObject header;
    protected JSONObject body;
	
	public abstract Consts.MessageType getMessageType();

    public abstract String getMessageSn();
    
    public abstract Consts.MessageId getMessageId();
    
    protected long queueTime;
    protected int delayOfHandle;
    
    
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

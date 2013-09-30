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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public abstract class AbstractJSONMessage
{
	protected Logger logger = JSONModule.instance.getLogger();
	
	protected IDeviceWrapper deviceWrapper;
	
    public abstract Consts.MessageType getMessageType();

    public abstract String getMessageSn();
    
    public abstract Consts.MessageId getMessageId();
    
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
}

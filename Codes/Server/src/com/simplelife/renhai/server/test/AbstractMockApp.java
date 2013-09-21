
/**
 * AbstractMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import java.util.HashMap;
import java.util.Timer;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.JSONKey;

/** */
public abstract class AbstractMockApp implements IMockApp
{
    /** */
    protected int sessionId;
    protected Timer pingTimer;
    
    /** */
    protected IDeviceWrapper deviceWrapper;
    protected String peerDeviceId;
    protected String businessSessionId;
    protected String businessType;
    protected JSONObject lastReceivedCommand;
    
    protected HashMap<String, Object> jsonMap = new HashMap<String, Object>();
    protected HashMap<String, Object> jsonMapHeader = new HashMap<String, Object>();
    protected HashMap<String, Object> jsonMapBody = new HashMap<String, Object>();
    protected HashMap<String, Object> jsonMapDevice = new HashMap<String, Object>();
    
    public AbstractMockApp()
    {
    	 
    }
    
    /**
     * Clear jsonMap and add default fields
     */
    protected void init()
    {
    	jsonMap.clear();
    	jsonMap.put(JSONKey.Header, jsonMapHeader);
    	jsonMap.put(JSONKey.Body, jsonMapBody);
    	
    	jsonMapHeader.put(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
    	jsonMapHeader.put(JSONKey.DeviceSn, deviceWrapper.getDeviceSn());
    	jsonMapHeader.put(JSONKey.TimeStamp, DateUtil.getNow());
    }
    
    protected HashMap<String, Object> getDeviceJSONMap()
    {
    	if (jsonMapDevice.size() > 0)
    	{
    		return jsonMapDevice;
    	}
    	
    	Devicecard card = (Devicecard) deviceWrapper.getDevice().getDevicecard();
    	jsonMapDevice.put(JSONKey.OsVersion, card.getOsVersion());
    	jsonMapDevice.put(JSONKey.AppVersion, card.getAppVersion());
    	jsonMapDevice.put(JSONKey.IsJailed, card.getIsJailed());
    	jsonMapDevice.put(JSONKey.Location, card.getLocation());
    	
    	return jsonMapDevice;
    }
    
    public void bindDeviceWrapper(IDeviceWrapper deviceWrapper)
    {
    	this.deviceWrapper = deviceWrapper;
    }
    
    public IDeviceWrapper getDeviceWrapper()
    {
    	return deviceWrapper;
    }
}

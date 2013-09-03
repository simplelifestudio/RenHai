
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

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.device.DeviceCard;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.JSONKey;

/** */
public abstract class AbstractMockApp implements IMockApp
{
    /** */
    protected int sessionId;
    
    /** */
    protected IDeviceWrapper deviceWrapper;
    protected String peerDeviceId;
    protected String businessSessionId;
    protected String businessSessionType;
    protected JSONObject lastReceivedCommand;
    
    protected HashMap<String, Object> jsonMap = new HashMap<String, Object>();
    protected HashMap<String, Object> jsonMapHeader = new HashMap<String, Object>();
    protected HashMap<String, Object> jsonMapBody = new HashMap<String, Object>();
    protected HashMap<String, Object> jsonMapDevice = new HashMap<String, Object>();
    
    /**
     * Clear jsonMap and add default fields
     */
    protected void init()
    {
    	jsonMap.clear();
    	jsonMap.put(JSONKey.FieldName.Head, jsonMapHeader);
    	jsonMap.put(JSONKey.FieldName.Body, jsonMapBody);
    	
    	jsonMapHeader.put(JSONKey.FieldName.DeviceId, deviceWrapper.getDevice().getDeviceCard().getDeviceId());
    	jsonMapHeader.put(JSONKey.FieldName.DeviceSn, deviceWrapper.getDevice().getDeviceCard().getDeviceSn());
    	jsonMapHeader.put(JSONKey.FieldName.TimeStamp, DateUtil.getNow());
    }
    
    protected HashMap<String, Object> getDeviceJSONMap()
    {
    	if (jsonMapDevice.size() > 0)
    	{
    		return jsonMapDevice;
    	}
    	
    	DeviceCard card = deviceWrapper.getDevice().getDeviceCard();
    	jsonMapDevice.put(JSONKey.FieldName.OsVersion, card.getOsVersion());
    	jsonMapDevice.put(JSONKey.FieldName.AppVersion, card.getAppVersion());
    	jsonMapDevice.put(JSONKey.FieldName.IsJailed, card.isJailed());
    	jsonMapDevice.put(JSONKey.FieldName.Location, card.getLocation());
    	
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

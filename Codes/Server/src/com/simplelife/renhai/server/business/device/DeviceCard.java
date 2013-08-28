/**
 * DeviceCard.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.IDbOperation;
import com.simplelife.renhai.server.util.IJSONObject;


/**
 * 这是device类的描述
**/
public class DeviceCard implements IJSONObject, IDbOperation
{
    /** */
    public String deviceSn;
    
    /** */
    public Date registerTime;
    
    /** */
    public int serviceStatus;
    
    /** */
    public Date forbiddenExpiredDate;
    
    /** */
    public int profileId;
    
    /** */
    public String deviceModel;
    
    /** */
    public String osVersion;
    
    /** */
    public String appVersion;
    
    /** */
    public String location;
    
    /** */
    public boolean isJailed;
    
    /** */
    public Device Unnamed1;
    
    /** */
    public JSONObject toJSONObject()
    {
        return null;
    }
    
    /** */
    public boolean saveToDb()
    {
        return false;
    }
    
    /** */
    public boolean loadFromDb()
    {
        return false;
    }
}

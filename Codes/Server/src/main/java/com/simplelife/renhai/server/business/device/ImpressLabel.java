/**
 * ImpressLabel.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;


/** */
public class ImpressLabel extends AbstractLabel
{
    /** */
    protected int updateTime;
    
    /** */
    protected int assessCount;
    
    /** */
    public int getAssessCount()
    {
        return assessCount;
    
    }
    
    /** */
    public Date getUpdateTime()
    {
        return null;
    
    }
    
    /** */
    public JSONObject toJSONObject()
    {
        return null;
    }
}

/**
 * InterestLabel.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import com.alibaba.fastjson.JSONObject;


/** */
public class InterestLabel extends AbstractLabel
{
    /** */
    protected int order;
    
    /** */
    protected int validFlag;
    
    /** */
    public int getOrder()
    {
        return order;
    
    }
    
    /** */
    public int getValidFlag()
    {
        return validFlag;
    
    }
    
    /** */
    public JSONObject toJSONObject()
    {
        return null;
    }
}

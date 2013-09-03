/**
 * AbstractLabel.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.IJSONObject;


/** */
public abstract class AbstractLabel implements IJSONObject
{
    /** */
    protected int count;
    protected String name;
    
    public String getName()
    {
    	return name;
    }
    
    /** */
    public abstract JSONObject toJSONObject();
}

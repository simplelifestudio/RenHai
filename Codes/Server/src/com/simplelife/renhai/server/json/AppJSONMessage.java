/**
 * AppJSONMessage.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.util.IAppJSONMessage;


/** */
public abstract class AppJSONMessage implements IAppJSONMessage
{
    /** */
    public JSONObject jsonObject;
    
    /** */
    public void execute()
    {
    }
}

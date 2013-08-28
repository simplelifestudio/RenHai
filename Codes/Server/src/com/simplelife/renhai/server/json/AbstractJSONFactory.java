/**
 * AbstractJSONFactory.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.json;

import com.simplelife.renhai.server.util.IAppJSONMessage;
import com.simplelife.renhai.server.util.IServerJSONMessage;


/** */
public abstract class AbstractJSONFactory
{
    /** */
    public IAppJSONMessage createJSONRequest()
    {
        return null;
    
    }
    
    /** */
    public IServerJSONMessage CreateJSONResponse()
    {
        return null;
    
    }
}

/**
 * IServerJSONMessage.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;


/** */
public interface IServerJSONMessage
{
    /** */
    public void responseError();
    
    /** */
    public void responseNormalRequest();
}

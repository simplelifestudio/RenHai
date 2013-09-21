/**
 * IBaseConnectionOwner.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.nio.ByteBuffer;

import com.simplelife.renhai.server.json.AppJSONMessage;


/** */
public interface IBaseConnectionOwner
{
    /** */
    public void onClose(IBaseConnection connection);
    
    /** */
    public void onPing(IBaseConnection conection, ByteBuffer payload);
    
    /** */
    public void onJSONCommand(AppJSONMessage command);
    
    /** */
    public void onTimeOut(IBaseConnection conection);
    
    public IBaseConnection getConnection();
}

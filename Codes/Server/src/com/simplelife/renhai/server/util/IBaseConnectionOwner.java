/**
 * IBaseConnectionOwner.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;


/** */
public interface IBaseConnectionOwner
{
    /** */
    public void onClose(IBaseConnection connection);
    
    /** */
    public void onPing(IBaseConnection conection);
    
    /** */
    public void onJSONCommand(IAppJSONMessage command);
    
    /** */
    public void onTimeOut(IBaseConnection conection);
}

/**
 * IBaseConnection.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;


/** */
public interface IBaseConnection
{
    /** */
    public void onClose();
    
    /** */
    public void onOpen();
    
    /** */
    public void onPing();
    
    /** */
    public void onPong();
    
    /** */
    public void bind(IBaseConnectionOwner owner);
    
    /** */
    public void ping();
    
    /** */
    public void onTextMessage(String message);
    
    /** */
    public void sendMessage(String messge);
    
    /** */
    public void sendMessage(IServerJSONMessage message);
    
    /** */
    public void onTimeout();
    
    /** */
    public void close();
    
    public IBaseConnectionOwner getOwner();
}

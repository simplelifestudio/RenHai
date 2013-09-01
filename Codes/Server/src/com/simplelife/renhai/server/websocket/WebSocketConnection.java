/**
 * WebSocketConnection.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.websocket;

import java.nio.CharBuffer;

import com.simplelife.renhai.server.business.device.DeviceWrapper;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IServerJSONMessage;


/** */
public class WebSocketConnection implements IBaseConnection
{
    /** */
    public IBaseConnectionOwner Unnamed1;
    
    /** */
    public void WebSocketConnection()
    {
    
    }
    
    /** */
    private void writeTextMessage(CharBuffer buffer)
    {
    
    }
    
    /** */
    public void onClose()
    {
    }
    
    /** */
    public void onOpen()
    {
    }
    
    /** */
    public void onPing()
    {
    }
    
    /** */
    public void onPong()
    {
    }
    
    /** */
    public void bind(IBaseConnectionOwner owner)
    {
    }
    
    /** */
    public void ping()
    {
    }
    
    /** */
    public void onTextMessage(String message)
    {
    }
    
    /** */
    public void sendMessage(String messge)
    {
    }
    
    /** */
    public void sendMessage(IServerJSONMessage message)
    {
    }
    
    /** */
    public void onTimeout()
    {
    }

    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IBaseConnection#close()
     */
    @Override
    public void close()
    {
        // TODO Auto-generated method stub
        
    }
}

/**
 * MockWebSocketConnection.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import java.io.IOException;

import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IServerJSONMessage;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/** */
public class MockWebSocketConnection extends WebSocketConnection
{
    /** */
    private boolean disabled = false;
    
    
    /** */
    public void disableConnection()
    {
    	disabled = true;
    }
    
    public void enableConnection()
    {
    	disabled = false;
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
    public void onTextMessage()
    {
    }
    
    /**
     * @throws IOException  */
    @Override
    public void sendMessage(String messge) throws IOException
    {
    	if (disabled)
    	{
    		throw new IOException();
    	}
    }
    
    /**
     * @throws IOException  */
    @Override
    public void sendMessage(IServerJSONMessage message) throws IOException
    {
    	if (disabled)
    	{
    		throw new IOException();
    	}
    }
    
    /** */
    public void onTimeout()
    {
    }
    
    /** */
    public void close()
    {
    }
}

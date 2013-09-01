/**
 * MockWebSocketConnection.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;

import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IServerJSONMessage;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/** */
public class MockWebSocketConnection extends WebSocketConnection
{
    /** */
    public LocalMockApp Unnamed1;
    
    /** */
    public void disableConnection(IDeviceWrapper device)
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
    public void onTextMessage()
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
    
    /** */
    public void close()
    {
    }
}

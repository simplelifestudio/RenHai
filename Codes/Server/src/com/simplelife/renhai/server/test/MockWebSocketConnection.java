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

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/** */
public class MockWebSocketConnection extends WebSocketConnection
{
	private JSONObject lastSentMessage;
	
	public JSONObject getLastSentMessage()
	{
		return lastSentMessage;
	}
	
	public MockWebSocketConnection(String connectionId)
	{
		super(connectionId);
	}

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
    	getOwner().onClose(this);
    }
    
    /** */
    public void onOpen()
    {
    }
    
    /** */
    public void onPong()
    {
    }
    
    /** */
    public void onTextMessage()
    {
    }
    
    /** */
    @Override
    protected void sendMessage(String message) throws IOException
    {
    	System.out.print(message);
    }
    
    /** */
    @Override
    public void asyncSendMessage(ServerJSONMessage message) throws IOException
    {
    	if (disabled)
    	{
    		throw new IOException();
    	}
    	
    	lastSentMessage = message.toJSONObject();
    	sendMessage(lastSentMessage.toJSONString());
    }
    
    /**
     * @return  */
    @Override
    public AppJSONMessage syncSendMessage(String messageSn, String message) throws IOException
    {
    	if (disabled)
    	{
    		throw new IOException();
    	}
    	sendMessage(message);
    	return null;
    }
    
    /**
     * @return  */
    @Override
    public AppJSONMessage syncSendMessage(ServerJSONMessage message) throws IOException
    {
    	if (disabled)
    	{
    		throw new IOException();
    	}
    	lastSentMessage = message.toJSONObject();
    	sendMessage(lastSentMessage.toJSONString());
    	return null;
    }
    
    
    /** */
    public void onTimeout()
    {
    }
    
    /** */
    public void close(int status)
    {
    }
}

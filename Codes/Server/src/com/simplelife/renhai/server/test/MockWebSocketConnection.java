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
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/** */
public class MockWebSocketConnection extends WebSocketConnection
{
	private JSONObject lastSentMessage;
	private AbstractMockApp ownerMockApp;
	
	public void bindMockApp(AbstractMockApp mockApp)
	{
		this.ownerMockApp = mockApp;
	}
	
	public JSONObject getLastSentMessage()
	{
		return lastSentMessage;
	}
	
	public MockWebSocketConnection()
	{
		super(DateUtil.getNow());
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
    @Override
    public void onTextMessage(String message)
    {
    	System.out.print("Send message by mock WebSocket connection: \n");
    	System.out.print(message);
    	
    	super.onTextMessage(message);
    }
    
    /** */
    @Override
    protected void sendMessage(String message) throws IOException
    {
    	lastSentMessage = new JSONObject();
    	JSONObject obj = JSONObject.parseObject(message);
    	lastSentMessage.put(JSONKey.JsonEnvelope, obj);
    	this.ownerMockApp.onJSONCommand(lastSentMessage);
    	
    	System.out.print(message);
    }
    
    /* 
    @Override
    public void asyncSendMessage(ServerJSONMessage message) throws IOException
    {
    	if (disabled)
    	{
    		throw new IOException();
    	}
    	
    	lastSentMessage = new JSONObject();
    	lastSentMessage.put(JSONKey.JsonEnvelope, message.toJSONObject());
    	if (ownerMockApp != null)
    	{
    		this.ownerMockApp.onJSONCommand(lastSentMessage);
    	}
    	sendMessage(lastSentMessage.toJSONString());
    }
    */
    
    
    /*
     * 
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
    */
    
    /*
     *
    @Override
    public AppJSONMessage syncSendMessage(ServerJSONMessage message) throws IOException
    {
    	if (disabled)
    	{
    		throw new IOException();
    	}
    	
    	lastSentMessage = new JSONObject();
    	lastSentMessage.put(JSONKey.JsonEnvelope, message.toJSONObject());
    	this.ownerMockApp.onJSONCommand(lastSentMessage);
    	sendMessage(lastSentMessage.toJSONString());
    	return null;
    }
    */
    
    
    /** */
    public void onTimeout()
    {
    }
    
    public void clear()
    {
    	lastSentMessage = null;
    }
}

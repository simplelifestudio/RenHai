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
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.IMockConnection;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/** */
public class MockWebSocketConnection extends WebSocketConnection implements IMockConnection
{
	private JSONObject lastSentMessage;
	private IMockApp ownerMockApp;
	
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
    	logger.debug("Connection of {} is disabled", getConnectionId());
    	disabled = true;
    }
    
    public void enableConnection()
    {
    	logger.debug("Connection of {} is enabled", getConnectionId());
    	disabled = false;
    }
    
    /** */
    @Override
    public void onTextMessage(String message)
    {
    	//System.out.print("Send message by mock WebSocket connection: \n");
    	//System.out.print(message);
    	//System.out.print("\n");
    	
    	super.onTextMessage(message);
    }
    
    /** */
    @Override
    public void sendMessage(ServerJSONMessage message) throws IOException
    {
    	if (disabled)
    	{
    		logger.debug("Mock IOException due to connection is disabled");
    		throw new IOException();
    	}
    	
    	lastSentMessage = new JSONObject();
    	lastSentMessage.put(JSONKey.JsonEnvelope, message.toJSONObject());
    	
    	if (ownerMockApp != null)
    	{
    		this.ownerMockApp.onJSONCommand(lastSentMessage);
    	}
    }
    
    
    public void onTimeout()
    {
    }
    
    @Override
    public void close()
    {
    	
    }

	@Override
	public void bindMockApp(IMockApp mockApp)
	{
		this.ownerMockApp = mockApp;
	}
}

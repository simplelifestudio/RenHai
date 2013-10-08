/**
 * WebSocketClient.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.internal.runners.TestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.IMockConnection;
import com.simplelife.renhai.server.util.JSONKey;

/** */
public class MockWebSocketClient extends WebSocketClient implements IMockConnection 
{
	IMockApp mockApp;
	private JSONObject lastSentMessage;
	private boolean disabled = false;
	
	Logger logger = LoggerFactory.getLogger(MockWebSocketClient.class);
	
	public MockWebSocketClient(URI serverURI, Draft d)
	{
		super(serverURI, d);
	}
	
	public MockWebSocketClient(URI serverURI)
	{
		super(serverURI, new Draft_17());
	}


	@Override
	public void onClose(int arg0, String arg1, boolean arg2)
	{
		getConnection().close(arg0);
	}

	@Override
	public void onError(Exception arg0)
	{
		logger.debug("==================onError: {}", arg0.getMessage());
	}

	@Override
	public void onMessage(String arg0)
	{
		onTextMessage(arg0);
	}

	@Override
	public void onOpen(ServerHandshake arg0)
	{
		logger.debug("==================onOpen");
	}

	@Override
	public void bindMockApp(IMockApp mockApp)
	{
		this.mockApp = mockApp;
	}

	@Override
	public JSONObject getLastSentMessage()
	{
		return lastSentMessage;
	}

	@Override
	public void disableConnection()
	{
		this.disabled = true;
	}

	@Override
	public void enableConnection()
	{
		this.disabled = false;
	}


	@Override
	public void onTextMessage(String message)
	{
		logger.debug("Received message: " + message);
		JSONObject obj = JSONObject.parseObject(message);
		mockApp.onJSONCommand(obj);
	}


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
    	
    	getConnection().send(JSON.toJSONString(lastSentMessage, SerializerFeature.WriteMapNullValue));
	}


	@Override
	public void onTimeout()
	{
		logger.debug("==================onTimeout");
	}


	@Override
	public String getConnectionId()
	{
		return null;
	}


	@Override
	public void onPing(ByteBuffer pingData)
	{
		logger.debug("==================onPing");
	}
	
	public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) 
	{
		FrameBuilder builder = (FrameBuilder) frame;
		builder.setTransferemasked( true );
		getConnection().sendFrame( frame );
	}
}

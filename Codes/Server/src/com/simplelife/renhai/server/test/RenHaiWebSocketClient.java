/**
 * WebSocketClient.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.IMockConnection;
import com.simplelife.renhai.server.util.JSONKey;

/** */
public class RenHaiWebSocketClient extends WebSocketClient implements IMockConnection 
{
	IMockApp mockApp;
	private JSONObject lastSentMessage;
	private boolean disabled = false;
	private FramedataImpl1 pingFrameData;
	protected HashMap<String, MockSyncController> syncMap = new HashMap<String, MockSyncController>();
	
	Logger logger = LoggerFactory.getLogger(RenHaiWebSocketClient.class);
	
	public RenHaiWebSocketClient(URI serverURI, Draft d)
	{
		super(serverURI, d);
		pingFrameData = new FramedataImpl1(Opcode.PING);
		pingFrameData.setFin(true);
	}
	
	public RenHaiWebSocketClient(URI serverURI)
	{
		this(serverURI, new Draft_17());
	}

	
	
	@Override
	public void onClose(int arg0, String arg1, boolean arg2)
	{
		getConnection().close(arg0);
		if (mockApp != null)
		{
			mockApp.onClose();
		}
	}

	@Override
	public void onError(Exception arg0)
	{
		String temp =  "==================onError: " +  arg0.getMessage();
		if (mockApp != null)
		{
			temp += ", MockApp <" + mockApp.getDeviceSn() + ">";
		}
		logger.error(temp);
	}

	@Override
	public void onMessage(String arg0)
	{
		Thread.currentThread().setName("MockAppWebSocket" + DateUtil.getCurrentMiliseconds());
		onTextMessage(arg0);
	}

	@Override
	public void onOpen(ServerHandshake arg0)
	{
		Thread.currentThread().setName("WebSocketClient" + DateUtil.getCurrentMiliseconds());
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
		if (mockApp == null)
		{
			logger.error("Fatal error: mockApp == null");
			return;
		}
		
		logger.debug("MockWebSocket received message: " + message);
		JSONObject obj = JSONObject.parseObject(message);
		
		JSONObject env = obj.getJSONObject(JSONKey.JsonEnvelope);
		JSONObject header = env.getJSONObject(JSONKey.Header);
		String messageSn = header.getString(JSONKey.MessageSn);
		
		if (!syncMap.containsKey(messageSn))
    	{
    		mockApp.onJSONCommand(obj);
    	}
    	else
    	{
    		logger.debug("MockApp <{}> received response of synchronized request, messageSn: " + messageSn, mockApp.getDeviceSn());
    		signalForSyncSend(messageSn, obj);
    	}
	}

	public void signalForSyncSend(String messageSn, JSONObject serverMessage)
    {
		MockSyncController controller = syncMap.get(messageSn);
    	synchronized(controller)
    	{
			controller.lock.lock();
			controller.message = serverMessage;
			controller.condition.signal();
			controller.lock.unlock();
    	}
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
	public void ping()
	{
		getConnection().sendFrame(pingFrameData);
	}
	
	@Override
	public void onPing(ByteBuffer pingData)
	{
		logger.debug("onPing triggered");
	}
	
	public void onWebsocketMessageFragment( WebSocket conn, Framedata frame ) 
	{
		FrameBuilder builder = (FrameBuilder) frame;
		builder.setTransferemasked( true );
		getConnection().sendFrame( frame );
	}
	
	public void closeConnection()
	{
		super.close();
	}

	@Override
	public boolean isOpen()
	{
		if (getConnection() == null)
		{
			return false;
		}
		
		return getConnection().isOpen();
	}

	@Override
	public JSONObject syncSendToServer(JSONObject jsonObject)
	{
		if (mockApp == null)
		{
			logger.error("Fatal error: mockApp == null");
			return null;
		}
		
		if (disabled)
    	{
    		logger.debug("Mock IOException due to connection is disabled");
    		return null;
    	}
		
		String messageSn = jsonObject.getJSONObject(JSONKey.JsonEnvelope)
				.getJSONObject(JSONKey.Header)
				.getString(JSONKey.MessageSn);
		MockSyncController controller = new MockSyncController();
    	synchronized(syncMap)
    	{
    		logger.debug("Add {} to synchronized sending map", messageSn);
    		syncMap.put(messageSn, controller);
    	}
    	
    	lastSentMessage = jsonObject;
    	logger.debug("MockApp <{}> trying to get lock for synchronized sending", mockApp.getDeviceSn());
    	controller.lock.lock(); 
    	logger.debug("MockApp <{}> got lock", mockApp.getDeviceSn());

    	boolean exceptionOcurred = false;
    	try
    	{
    		logger.debug("Send synchronized message to device <{}>, MessageSn: " + messageSn, mockApp.getDeviceSn());
    		asyncSendToServer(jsonObject);
    		controller.condition.await(GlobalSetting.TimeOut.JSONMessageEcho, TimeUnit.SECONDS);
    	}
		catch (InterruptedException e)
		{
			logger.error(e.getMessage());
			exceptionOcurred = true;
		}
    	finally
    	{
    		controller.lock.unlock();
    		logger.debug("MockApp <{}> release lock for synchronized sending", mockApp.getDeviceSn());
    	}
    	
    	if (exceptionOcurred)
    	{
    		return null;
    	}
    	else
    	{
	    	if (controller.message == null)
	    	{
	    		logger.error("MockApp <{}> timeout for synchronized response from server, MessageSn: " + messageSn, mockApp.getDeviceSn());
	    		return null;
	    	}
	    	else
	    	{
	    		logger.debug("MockApp <{}> received synchronized message from server in time.", mockApp.getDeviceSn());
	    	}
    	}
    	return controller.message;
	}

	@Override
	public void asyncSendToServer(JSONObject jsonObject)
	{
		if (disabled)
    	{
    		logger.debug("Mock IOException due to connection is disabled");
    		return;
    	}
    	
    	lastSentMessage = jsonObject;
    	String message = JSON.toJSONString(lastSentMessage, SerializerFeature.WriteMapNullValue);
    	getConnection().send(message);
    	logger.debug("Sent message over WebSocket: \n{}", message);
	}
}

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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.IMockConnection;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/** */
public class MockWebSocketConnection extends WebSocketConnection implements IMockConnection
{
	private JSONObject lastSentMessage;
	private IMockApp mockApp;
	private HashMap<String, MockSyncController> syncMapToServer = new HashMap<String, MockSyncController>();
	
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
    }
    
    /** */
    @Override
    public void sendMessage(ServerJSONMessage message) throws IOException
    {
    	// This function is called by DeviceWrapper in Server
    	// So it's actually received message from Server
    	if (disabled)
    	{
    		logger.debug("Mock IOException due to connection is disabled");
    		throw new IOException();
    	}
    	
    	logger.debug("Received message: \n" + JSON.toJSONString(message.toJSONObject(), true));
		JSONObject obj = message.toJSONObject();
		JSONObject header = obj.getJSONObject(JSONKey.Header);
		String messageSn = header.getString(JSONKey.MessageSn);
		
		JSONObject env = new JSONObject();
		env.put(JSONKey.JsonEnvelope, obj);
		if (!syncMapToServer.containsKey(messageSn))
    	{
    		mockApp.onJSONCommand(env);
    	}
    	else
    	{
    		logger.debug("MockApp <{}> receive response of synchronized request", mockApp.getDeviceSn());
    		signalForSyncSend(messageSn, env);
    	}
    }
    
    public void signalForSyncSend(String messageSn, JSONObject serverMessage)
    {
		MockSyncController controller = syncMapToServer.get(messageSn);
    	synchronized(controller)
    	{
			controller.lock.lock();
			controller.message = serverMessage;
			controller.condition.signal();
			controller.lock.unlock();
    	}
    }
    
    public void onTimeout()
    {
    }
    
    @Override
    public void closeConnection()
    {
    	super.onClose(0);
    }

	@Override
	public void bindMockApp(IMockApp mockApp)
	{
		this.mockApp = mockApp;
	}

	@Override
	public boolean isOpen()
	{
		return true;
	}
	
	@Override
	public void ping()
	{
		ByteBuffer pingData = ByteBuffer.allocate(5);
		this.onPing(pingData);
	}

	@Override
	public JSONObject syncSendToServer(JSONObject jsonObject)
	{
		if (disabled)
    	{
    		logger.debug("Mock IOException due to connection is disabled");
    		return null;
    	}
		
		String messageSn = jsonObject.getJSONObject(JSONKey.JsonEnvelope)
				.getJSONObject(JSONKey.Header)
				.getString(JSONKey.MessageSn);
		MockSyncController controller = new MockSyncController();
    	synchronized(syncMapToServer)
    	{
    		logger.debug("Add {} to synchronized sending map", messageSn);
    		syncMapToServer.put(messageSn, controller);
    	}
    	
    	lastSentMessage = jsonObject;
    	controller.lock.lock(); 
	    	
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
    	}
    	
    	synchronized(syncMapToServer)
    	{
    		logger.debug("Remove {} from synchronized sending map", messageSn);
    		syncMapToServer.remove(messageSn);
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
		lastSentMessage = jsonObject;
		super.onTextMessage(JSON.toJSONString(jsonObject, SerializerFeature.WriteMapNullValue));
	}
}

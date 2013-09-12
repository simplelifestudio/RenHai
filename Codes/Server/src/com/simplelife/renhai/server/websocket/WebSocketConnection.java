/**
 * WebSocketConnection.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.InvalidRequest;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.json.TimeoutRequest;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class WebSocketConnection extends MessageInbound implements IBaseConnection
{
	private class SyncController
	{
		public final Lock lock = new ReentrantLock();
		public final Condition condition = lock.newCondition(); 
		public AppJSONMessage message;
	}
	
    /** */
    protected IBaseConnectionOwner connectionOwner;
    protected String remoteIPAddress;
    protected HashMap<String, SyncController> syncMap = new HashMap<String, SyncController>();
    protected String connectionId;
    
    
    /**
	 * @return the remoteIPAddress
	 */
	public String getRemoteIPAddress()
	{
		return remoteIPAddress;
	}

	/**
	 * @param remoteIPAddress the remoteIPAddress to set
	 */
	public void setRemoteIPAddress(String remoteIPAddress)
	{
		this.remoteIPAddress = remoteIPAddress;
	}

	/** */
    public WebSocketConnection(String connectionId)
    {
    	super();
    	setByteBufferMaxSize(Consts.ConnectionSetting.ByteBufferMaxSize.ordinal());
    	setByteBufferMaxSize(Consts.ConnectionSetting.ByteBufferMaxSize.ordinal());
    	this.connectionId = connectionId;
    }
    
    @Override
    public void close()
    {
        // TODO: value of status below
        super.onClose(0);
    }
    
    /** */
    public void bind(IBaseConnectionOwner owner)
    {
    	this.connectionOwner = owner;
    }
    
	@Override
	public IBaseConnectionOwner getOwner()
	{
		return connectionOwner;
	}
    
    /** */
    public void ping()
    {
    	Logger logger = WebSocketModule.instance.getLogger();
    	
        ByteBuffer pingData = ByteBuffer.allocate(5);
        try
        {
            getWsOutbound().ping(pingData);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }
   
    @Override
    protected void onBinaryMessage(ByteBuffer message) throws IOException 
    {
        //getWsOutbound().writeBinaryMessage(message);
    }

	@Override
	protected void onTextMessage(CharBuffer message) throws IOException
	{
		String strMessage = message.toString().trim();
		onTextMessage(strMessage);
	}
    
    @Override
    public void onTextMessage(String message)
    {
    	Logger logger = WebSocketModule.instance.getLogger();
    	logger.debug("Text message received.");
    	
    	JSONObject obj = JSONObject.parseObject(message);
    	AppJSONMessage appMessage;
		if (obj == null)
		{
			logger.error("Invalid JSON string which can't be converted into JSON object.");
			appMessage = new InvalidRequest(null);
			appMessage.setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
			appMessage.setErrorDescription("Invalid JSON string.");
		}
		else if (!obj.containsKey(JSONKey.JsonEnvelope))
		{
			logger.error("Invalid JSON string ");
			appMessage = new InvalidRequest(obj);
			appMessage.setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
			appMessage.setErrorDescription("Invalid JSON request: " + JSONKey.JsonEnvelope + " was not found.");
		}
		else
		{
			logger.debug("Enveloped JSON string.");
			message = obj.getString(JSONKey.JsonEnvelope);
	    	if (GlobalSetting.BusinessSetting.Encrypt)
	    	{
	    		logger.debug("Try to decrypt message");
	    		try
				{
					message = SecurityUtils.decryptByDESAndDecodeByBase64(message, GlobalSetting.BusinessSetting.EncryptKey);
				}
				catch (Exception e)
				{
					WebSocketModule.instance.getLogger().error(e.getMessage());
					e.printStackTrace();
				}
	    	}
	    	
	    	appMessage = JSONFactory.createAppJSONMessage(JSONObject.parseObject(message));
	    	
		}

    	logger.debug("Received message: {}", appMessage.getMessageId().name());
    	String messageSn = appMessage.getMessageSn();
    	if (!syncMap.containsKey(messageSn))
    	{
    		logger.debug("The received message is not expected by synchronizd sending.");
    		if (connectionOwner != null)
    		{
    			connectionOwner.onJSONCommand(appMessage);
    		}
    	}
    	else
    	{
    		logger.debug("Expected synchronizd message received.");
    		SyncController controller = syncMap.get(messageSn);
    		controller.lock.lock();
    		controller.message = appMessage;
    		controller.condition.signal();
    		controller.lock.unlock();
    	}
    }
    
    /** */
    @Override
    public void onTimeout()
    {
    	connectionOwner.onTimeOut(this);
    }
    
    @Override
    public void onOpen(WsOutbound outbound)
    {
    	WebSocketModule.instance.getLogger().debug("onOpen triggered");
        super.onOpen(outbound);
    }
    
    @Override
    public void onPing(ByteBuffer payload)
    {
    	WebSocketModule.instance.getLogger().debug("onPing triggered");
    	connectionOwner.onPing(this);
    	super.onPing(payload);
    }
    
    @Override
    public void onPong(ByteBuffer payload)
    {
    	WebSocketModule.instance.getLogger().debug("onPong triggered");
    	super.onPong(payload);
    }
    
    @Override
    public void onClose(int status)
    {
    	WebSocketModule.instance.getLogger().debug("onClose triggered");
    	connectionOwner.onClose(this);
    	super.onClose(status);
    }

    
    /** */
    protected void sendMessage(String message) throws IOException
    {
    	Logger logger = WebSocketModule.instance.getLogger();
    	logger.debug("Send message: " + message);
    	CharBuffer buffer = CharBuffer.allocate(message.length());
        buffer.put(message);
        buffer.flip();
        
        try
        {
            getWsOutbound().writeTextMessage(buffer);
        } 
        catch (IOException e)
        {
        	logger.error(e.getMessage());
        }
    }
    
    /** */
    public void asyncSendMessage(ServerJSONMessage message) throws IOException
    {
    	sendMessage(message.toString());
    }
    
    /** */
    protected AppJSONMessage syncSendMessage(String messageSn, String message) throws IOException
    {
    	Logger logger = WebSocketModule.instance.getLogger();
    	
    	if (!syncMap.containsKey(messageSn))
    	{
    		throw new IOException("messageSn <" + messageSn+"> is not saved in syncMap!");
    	}
    	
    	SyncController controller = syncMap.get(messageSn); 
    	controller.lock.lock(); 
    	
    	try
    	{
    		sendMessage(message);
    		controller.condition.await(GlobalSetting.TimeOut.JSONMessageEcho, TimeUnit.SECONDS);
    	}
    	catch(IOException e)
    	{
    		logger.error(e.getMessage());
    		throw e;
    	}
		catch (InterruptedException e)
		{
			logger.error(e.getMessage());
		}
    	finally
    	{
    		controller.lock.unlock();
    	}
    	
    	if (controller.message == null)
    	{
    		controller.message = new TimeoutRequest(null);
    	}
    	
    	return controller.message;
    }
    
    /** */
    public AppJSONMessage syncSendMessage(ServerJSONMessage message) throws IOException
    {
    	SyncController controller = new SyncController();
    	String messageSn = message.getMessageSn();
    	synchronized(syncMap)
    	{
    		syncMap.put(messageSn, controller);
    	}
    	
    	AppJSONMessage appMessage = syncSendMessage(messageSn, message.toString());

    	synchronized(syncMap)
    	{
    		syncMap.remove(messageSn);
    	}
    	return appMessage;
    }

	@Override
	public String getConnectionId()
	{
		return connectionId;
	}
}

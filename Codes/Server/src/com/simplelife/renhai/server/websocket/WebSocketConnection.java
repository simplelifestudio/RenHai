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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.InvalidRequest;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.json.TimeoutRequest;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IDeviceWrapper;
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
    protected IDeviceWrapper connectionOwner;
    protected String remoteIPAddress;
    protected HashMap<String, SyncController> syncMap = new HashMap<String, SyncController>();
    protected String connectionId;
    protected Logger logger = WebSocketModule.instance.getLogger();
    
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
    	setByteBufferMaxSize(Consts.ConnectionSetting.ByteBufferMaxSize.getValue());
    	setByteBufferMaxSize(Consts.ConnectionSetting.ByteBufferMaxSize.getValue());
    	this.connectionId = connectionId;
    }
    
    @Override
    public void close()
    {
        try
		{
			getWsOutbound().close(Consts.DeviceReleaseReason.CloseConnectionByServer.getValue(), null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
    /** */
    public void bind(IDeviceWrapper owner)
    {
    	this.connectionOwner = owner;
    }
    
	@Override
	public IDeviceWrapper getOwner()
	{
		return connectionOwner;
	}
    
    /** */
    public void ping()
    {
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
		try
		{
			String strMessage = message.toString().trim();
			onTextMessage(strMessage);
		}
		catch(Exception e)
		{
			AppJSONMessage appMessage;
			logger.error("Exception caught in WebSocketConnection.onTextMessage!");
			e.printStackTrace();
			appMessage = new InvalidRequest(null);
			appMessage.setErrorCode(Consts.GlobalErrorCode.DBException_1001);
			appMessage.setErrorDescription("Server internal error.");
		}
	}
    
    @Override
    public void onTextMessage(String message)
    {
//    	if (connectionOwner != null)
//    	{
//    		logger.debug("Received message from device <{}>: " + message, connectionOwner.getDeviceSn());
//    	}
//    	else
//    	{
//    		logger.debug("Received message : ", message);
//    	}
    	logger.debug("Received message: \n{}", message);
    	
    	JSONObject obj = null;
    	try
    	{
    		obj = JSONObject.parseObject(message);
    	}
    	catch(Exception e)
    	{
    	}
    	
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
			String temp = "Invalid JSON request: " + JSONKey.JsonEnvelope + " was not found.";
			logger.error(temp);
			appMessage = new InvalidRequest(obj);
			appMessage.setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
			appMessage.setErrorDescription(temp);
		}
		else
		{
			JSONObject messageObj = obj.getJSONObject(JSONKey.JsonEnvelope);
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
	    	
	    	appMessage = JSONFactory.createAppJSONMessage(messageObj);
	    	
		}

		//JSONObject obj = JSONObject.parseObject(temp);
		//JSONObject deviceCount = obj.getJSONObject("jsonEnvelope").getJSONObject("body").getJSONObject("deviceCount");
		//System.out.print(JSON.toJSONString(deviceCount, SerializerFeature.WriteMapNullValue) + "\n");
		
		//deviceCount = appMessage.getBody().getJSONObject("deviceCount");
		//System.out.print(JSON.toJSONString(deviceCount, SerializerFeature.WriteMapNullValue) + "\n");
		
    	logger.debug("Received message: {}", appMessage.getMessageId().name());
    	String messageSn = appMessage.getMessageSn();
    	if (!syncMap.containsKey(messageSn))
    	{
    		if (connectionOwner != null)
    		{
    			logger.debug("New request message from device <{}>", connectionOwner.getDeviceSn());
    			connectionOwner.onJSONCommand(appMessage);
    		}
    		else
    		{
    			logger.error("New message received on connection {} but its connectionOwner is null!", this.getConnectionId());
    		}
    	}
    	else
    	{
    		logger.debug("Response of synchronizd notification from device <{}>", connectionOwner.getDeviceSn());
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
    	WebSocketModule.instance.getLogger().debug("WebSocketConnection onOpen triggered");
        super.onOpen(outbound);
    }
    
    @Override
    public void onPing(ByteBuffer payload)
    {
    	if (connectionOwner == null)
    	{
    		logger.error("Fatal error in onPing as its connectionOwner is null");
    	}
    	//WebSocketModule.instance.getLogger().debug("onPing triggered");
    	connectionOwner.onPing(this, payload);
    	//super.onPing(payload);
    }
    
    public void pong(ByteBuffer payload)
    {
    	try
		{
    		WsOutbound ws = getWsOutbound();
    		if (ws != null)
    		{
    			ws.pong(payload);
    		}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
    @Override
    public void onPong(ByteBuffer payload)
    {
    	WebSocketModule.instance.getLogger().debug("WebSocketConnection onPong triggered");
    	super.onPong(payload);
    }
    
    @Override
    public void onClose(int status)
    {
    	if (Consts.DeviceReleaseReason.parseValue(status) == Consts.DeviceReleaseReason.CloseConnectionByServer)
    	{
    		return;
    	}
    	
    	WebSocketModule.instance.getLogger().debug("WebSocketConnection onClose triggered");
    	connectionOwner.onClose(this);
    	super.onClose(status);
    }

    
    /** */
    protected void sendMessage(ServerJSONMessage message) throws IOException
    {
    	JSONObject obj = new JSONObject();
    	obj.put(JSONKey.JsonEnvelope, message.toJSONObject());
    	
    	String strMessage = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
    	
//    	if (connectionOwner != null)
//    	{
//    		logger.debug("Send message to Device <{}> : " + message, this.connectionOwner.getDeviceSn());
//    	}
//    	else
//    	{
    		logger.debug("Send message: \n{}", message);
    	//}
    	
    	CharBuffer buffer = CharBuffer.allocate(strMessage.length());
        buffer.put(strMessage);
        buffer.flip();
        
        try
        {
            getWsOutbound().writeTextMessage(buffer);
        } 
        catch (IOException e)
        {
        	logger.error(e.getMessage());
        	throw(e);
        }
    }
    
    /** */
    public void asyncSendMessage(ServerJSONMessage message)
    {
    	try
		{
			sendMessage(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.error("Connection of device <{}> was broken and will be released.", connectionOwner.getDeviceSn());
			connectionOwner.onClose(this);
		}
    }
    
    /** */
    protected AppJSONMessage syncSendMessage(String messageSn, ServerJSONMessage message)
    {
    	if (!syncMap.containsKey(messageSn))
    	{
    		logger.error("messageSn <" + messageSn+"> is not saved in syncMap!");
    	}
    	
    	SyncController controller = syncMap.get(messageSn); 
    	controller.lock.lock(); 
    	
    	try
    	{
    		logger.debug("Send synchronized message to device <{}>", this.connectionOwner.getDeviceSn());
    		sendMessage(message);
    		controller.condition.await(GlobalSetting.TimeOut.JSONMessageEcho, TimeUnit.SECONDS);
    	}
		catch (InterruptedException e)
		{
			logger.error(e.getMessage());
		}
		catch (IOException e)
		{
			logger.error("Connection of device <{}> was broken and will be released.", connectionOwner.getDeviceSn());
			connectionOwner.onClose(this);
		}
    	finally
    	{
    		controller.lock.unlock();
    	}
    	
    	if (controller.message == null)
    	{
    		logger.debug("Timeout when waiting for synchronized response of device <{}>", connectionOwner.getDeviceSn());
    		controller.message = new TimeoutRequest(null);
    	}
    	else
    	{
    		logger.debug("Device <{}> replied synchronized message in time.", this.connectionOwner.getDeviceSn());
    	}
    	
    	return controller.message;
    }
    
    /** */
    public AppJSONMessage syncSendMessage(ServerJSONMessage message)
    {
    	SyncController controller = new SyncController();
    	String messageSn = message.getMessageSn();
    	synchronized(syncMap)
    	{
    		syncMap.put(messageSn, controller);
    	}
    	
    	AppJSONMessage appMessage = syncSendMessage(messageSn, message);

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

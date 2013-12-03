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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ConnectionErrorEvent;
import com.simplelife.renhai.server.json.InvalidRequest;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.json.TimeoutRequest;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
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
    protected ConcurrentHashMap<String, SyncController> syncMap = new ConcurrentHashMap<String, SyncController>();
    protected String connectionId;
    protected Logger logger = WebSocketModule.instance.getLogger();
    protected boolean isConnectionOpen = false;
    
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
    
    public void closeConnection()
    {
    	logger.debug("Close Websocket: " + getConnectionId());
        try
		{
        	WsOutbound ws = getWsOutbound(); 
        	if (ws != null && !ws.isClosed())
        	{
        		//ws.close(Consts.StatusChangeReason.WebsocketClosedByServer.getValue(), null);
        		String reason = "Closed by server";
        		ByteBuffer buffer = ByteBuffer.allocate(reason.length());
                buffer.put(reason.getBytes());
        		ws.close(0, buffer);
        	}
		}
		catch (IOException e)
		{
			FileLogger.printStackTrace(e);
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
			FileLogger.printStackTrace(e);
			appMessage = new InvalidRequest(null);
			((InvalidRequest) appMessage).setReceivedMessage(message.toString().trim());
			appMessage.setErrorCode(Consts.GlobalErrorCode.DBException_1001);
			appMessage.setErrorDescription("Server internal error.");
		}
	}
    
    @Override
    public void onTextMessage(String message)
    {
    	Thread.currentThread().setName("WebSocket" + DateUtil.getCurrentMiliseconds());
    	
    	if (connectionOwner == null)
		{
    		logger.error("Message received on connection {} but its device has been released, message ignored", this.getConnectionId());
    		return;
		}
    	
    	JSONObject obj = null;
    	try
    	{
    		obj = JSONObject.parseObject(message);
    	}
    	catch(Exception e)
    	{
    		logger.error("Failed to parse: \n{}", message);
    	}
    	
    	AppJSONMessage appMessage = null;
		if (obj == null)
		{
			logger.error("Invalid JSON string which can't be converted into JSON object.");
			appMessage = new InvalidRequest(null);
			((InvalidRequest) appMessage).setReceivedMessage(message);
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
			JSONObject messageObj = null;
	    	if (GlobalSetting.BusinessSetting.Encrypt != 0)
	    	{
	    		logger.debug("Try to decrypt message");
	    		try
				{
	    			String tmpStr = obj.getString(JSONKey.JsonEnvelope);
	    			tmpStr = SecurityUtils.decryptByDESAndDecodeByBase64(tmpStr, GlobalSetting.BusinessSetting.EncryptKey);
	    			//logger.debug("Message after decrypt:\n{}", tmpStr);
	    			messageObj = JSONObject.parseObject(tmpStr);
	    			logger.debug("Received message: \n{}", JSON.toJSONString(messageObj, true));
				}
				catch (Exception e)
				{
					logger.error(e.toString());
					FileLogger.printStackTrace(e);
					appMessage = new InvalidRequest(obj);
					appMessage.setErrorCode(Consts.GlobalErrorCode.InvalidJSONRequest_1100);
					appMessage.setErrorDescription("Invalid JSON message, exception occurred in decryption or creating JSON object");
				}
	    	}
	    	else
	    	{
	    		logger.debug("Received message: \n{}", JSON.toJSONString(obj, true));
	    		messageObj = obj.getJSONObject(JSONKey.JsonEnvelope);
	    	}
	    	
	    	if (appMessage == null)
	    	{
	    		appMessage = JSONFactory.createAppJSONMessage(messageObj);
	    	}
		}

		String messageSn = appMessage.getMessageSn();
    	//logger.debug("Received message: {}", appMessage.getMessageId().name());
    	if (!syncMap.containsKey(messageSn))
    	{
			logger.debug("New request message from device <{}> with MessageSn: " + messageSn, connectionOwner.getDeviceSn());
			connectionOwner.onJSONCommand(appMessage);
    	}
    	else
    	{
    		logger.debug("Response of synchronized notification from device <{}>", connectionOwner.getDeviceSn());
    		signalForSyncSend(messageSn, appMessage);
    	}
    }
    
    public void signalForSyncSend(String messageSn, AppJSONMessage appMessage)
    {
    	SyncController controller = syncMap.get(messageSn);
    	if (controller == null)
    	{
    		return;
    	}
		controller.lock.lock();
		controller.message = appMessage;
		controller.condition.signal();
		controller.lock.unlock();
    }
    
    /** */
    @Override
    public void onTimeout()
    {
    	connectionOwner.onTimeOut();
    }
    
    @Override
    public void onOpen(WsOutbound outbound)
    {
    	logger.debug("WebSocketConnection onOpen triggered");
        super.onOpen(outbound);
        isConnectionOpen = true;
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
			FileLogger.printStackTrace(e);
		}
    }
    
    @Override
    public void onPong(ByteBuffer payload)
    {
    	logger.debug("WebSocketConnection onPong triggered");
    	super.onPong(payload);
    }
    
    @Override
    public void onClose(int status)
    {
    	/*
    	if (status == Consts.DeviceLeaveReason.WebsocketClosedByServer.getValue())
    	{
    		return;
    	}
    	*/
    	
    	isConnectionOpen = false;
    	String temp = "WebSocketConnection onClose triggered, connection id: " + getConnectionId();
    	if (this.connectionOwner != null)
    	{
    		temp += ", deviceSn: " + connectionOwner.getDeviceSn(); 
    	}
    	
    	logger.debug(temp);
    	
    	// comment out releasing device related resource
    	// to avoid loop in procedure of device releasing
    	connectionOwner.onConnectionClose();
    	super.onClose(status);
    }

    
    /** */
    protected void sendMessage(ServerJSONMessage message) throws IOException
    {
    	if (getWsOutbound().isClosed())
    	{
    		logger.error("The websocket connection with id {} has been closed, sending of "+ message.getMessageId().name() +" is given up.", this.getConnectionId());
    		return;
    	}
    	
    	JSONObject obj = new JSONObject();
    	message.addToHeader(JSONKey.TimeStamp, DateUtil.getNow());
    	
    	if (GlobalSetting.BusinessSetting.Encrypt == 0)
    	{
    		obj.put(JSONKey.JsonEnvelope, message.toJSONObject());
    	}
    	else
    	{
    		String tmpStr = JSON.toJSONString(message.toJSONObject(), true);
    		try
			{
				tmpStr = SecurityUtils.encryptByDESAndEncodeByBase64(tmpStr, GlobalSetting.BusinessSetting.EncryptKey);
				obj.put(JSONKey.JsonEnvelope, tmpStr);
			}
			catch (Exception e)
			{
				FileLogger.printStackTrace(e);
			}
    	}
    	
    	message.addToHeader(JSONKey.TimeStamp, DateUtil.getNow());
    	String strMessage = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
   		logger.debug("Send message over WebSocket: \n{}", message.toReadableString());
   		
   		CharBuffer buffer = CharBuffer.allocate(strMessage.length());
        buffer.put(strMessage);
        buffer.flip();
        
        try
        {
            getWsOutbound().writeTextMessage(buffer);
        } 
        catch (IOException e)
        {
        	FileLogger.printStackTrace(e);
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
			FileLogger.printStackTrace(e);
			logger.error("Connection of device <{}> was broken and will be released.", connectionOwner.getDeviceSn());
			AppJSONMessage connectionError = new ConnectionErrorEvent(null);
			connectionOwner.onJSONCommand(connectionError);
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
	    	
    	boolean exceptionOcurred = false;
    	try
    	{
    		logger.debug("Send synchronized message to device <{}>, MessageSn: " + messageSn, this.connectionOwner.getDeviceSn());
    		sendMessage(message);
    		controller.condition.await(GlobalSetting.TimeOut.JSONMessageEcho, TimeUnit.SECONDS);
    	}
		catch (InterruptedException e)
		{
			FileLogger.printStackTrace(e);
			exceptionOcurred = true;
		}
		catch (IOException e)
		{
			FileLogger.printStackTrace(e);
			exceptionOcurred = true;
		}
    	finally
    	{
    		controller.lock.unlock();
    	}
    	
    	if (exceptionOcurred)
    	{
    		logger.debug("Exception occurred on Websocket connection");
    		controller.message = new ConnectionErrorEvent(null);
    	}
    	else
    	{
	    	if (controller.message == null)
	    	{
	    		logger.error("Timeout for synchronized response of device <{}>, MessageSn: " + messageSn, connectionOwner.getDeviceSn());
	    		controller.message = new TimeoutRequest(null);
	    	}
	    	else
	    	{
	    		logger.debug("Device <{}> replied synchronized message in time.", this.connectionOwner.getDeviceSn());
	    	}
    	}
    	return controller.message;
    }
    
    /** */
    public AppJSONMessage syncSendMessage(ServerJSONMessage message)
    {
    	SyncController controller = new SyncController();
    	String messageSn = message.getMessageSn();
    	logger.debug("Add {} to synchronized sending map", messageSn);
    	syncMap.put(messageSn, controller);
    	
    	AppJSONMessage appMessage = syncSendMessage(messageSn, message);

    	logger.debug("Remove {} from synchronized sending map", messageSn);
    	syncMap.remove(messageSn);
    	return appMessage;
    }

	@Override
	public String getConnectionId()
	{
		return connectionId;
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IBaseConnection#isOpen()
	 */
	@Override
	public boolean isOpen()
	{
		return isConnectionOpen;
	}
}

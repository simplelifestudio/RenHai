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

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.json.TimeoutRequest;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;


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
    private IBaseConnectionOwner owner;
    private String remoteIPAddress;
    private HashMap<String, SyncController> syncMap = new HashMap<String, SyncController>();
    
    
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
    public WebSocketConnection()
    {
    	super();
    	setByteBufferMaxSize(GlobalSetting.ConnectionSetting.ByteBufferMaxSize.ordinal());
    	setByteBufferMaxSize(GlobalSetting.ConnectionSetting.ByteBufferMaxSize.ordinal());
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
    	this.owner = owner;
    }
    
	@Override
	public IBaseConnectionOwner getOwner()
	{
		return owner;
	}
    
    /** */
    public void ping()
    {
        ByteBuffer pingData = ByteBuffer.allocate(5);
        try
        {
            getWsOutbound().ping(pingData);
        } catch (IOException e)
        {
            // TODO: 
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
    	
    	AppJSONMessage appMessage = JSONFactory.createAppJSONMessage(JSONObject.parseObject(message));
    	String messageSn = appMessage.getMessageSn();
    	
    	if (!syncMap.containsKey(messageSn))
    	{
    		owner.onJSONCommand(appMessage);
    	}
    	else
    	{
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
    	owner.onTimeOut(this);
    }
    
    @Override
    public void onOpen(WsOutbound outbound)
    {
        super.onOpen(outbound);
    }
    
    @Override
    public void onPing(ByteBuffer payload)
    {
    	owner.onPing(this);
    	super.onPong(payload);
    }
    
    @Override
    public void onPong(ByteBuffer payload)
    {
    	super.onPong(payload);
    }
    
    public void onClose(int status)
    {
    	owner.onClose(this);
    	super.onClose(status);
    }

    
    /** */
    protected void sendMessage(String messge) throws IOException
    {
    	CharBuffer buffer = CharBuffer.allocate(messge.length());
        buffer.put(messge);
        buffer.flip();
        
        try
        {
            getWsOutbound().writeTextMessage(buffer);
        } 
        catch (IOException e)
        {
            // TODO
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
    		// TODO
    		throw e;
    	}
		catch (InterruptedException e)
		{
			// TODO
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
}

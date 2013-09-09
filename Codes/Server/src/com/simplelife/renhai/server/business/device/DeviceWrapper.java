/**
 * DeviceWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IAppJSONMessage;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IJSONObject;
import com.simplelife.renhai.server.util.INode;
import com.simplelife.renhai.server.websocket.WebSocketConnection;


/** */
public class DeviceWrapper implements IDeviceWrapper, INode, IBaseConnectionOwner, IJSONObject
{
    /** */
    protected IBaseConnection webSocketConnection;
    
    /** */
    protected Date lastPingTime;
    
    /** */
    protected Date lastActivityTime;
    
    /** */
    protected IBusinessSession ownerBusinessSession;
    
    /** */
    protected Device device;
    
    protected String deviceSn;
    
    /** */
    protected Consts.DeviceServiceStatus serviceStatus;
    
    /** */
    protected Consts.DeviceBusinessStatus businessStatus;
    
    protected OnlineDevicePool ownerOnlinePool;
    
    /** */
    protected void updatePingTime()
    {
    	lastPingTime = DateUtil.getNowDate();
    }
            
    /** */
    public void onChatConfirm()
    {
    
    }
    
    /** */
    public DeviceWrapper(IBaseConnection connection)
    {
    	this.webSocketConnection = connection;
    }
    
    /** */
    public void changeBusinessStatus(Consts.DeviceBusinessStatus targetStatus)
    {
    	switch(targetStatus)
    	{
    		case Idle:
    			break;
    		case Init:
    			if (businessStatus == Consts.DeviceBusinessStatus.Idle)
    			{
    				ownerOnlinePool.synchronizeDevice(this);
    			}
    			break;
    		case SessionBound:
    			break;
    		case WaitMatch:
    			break;
    		default:
    			break;
    	}
    }
    
    /** */
    public void updateLastActivityTime()
    {
    	lastActivityTime = DateUtil.getNowDate();
    }
    
    /** */
    public IBusinessSession getOwnerBusinessSession()
    {
        return ownerBusinessSession;
    }
    
    /** */
    public void bindOnlineDevicePool(OnlineDevicePool pool)
    {
    	this.ownerOnlinePool = pool;
    }
    
    /** */
    public Device getDevice()
    {
        return device;
    }
    
    /** */
    public Consts.DeviceBusinessStatus getBusinessStatus()
    {
        return businessStatus;
    }
    
    /** */
    public Consts.DeviceServiceStatus getServiceStatus()
    {
        return serviceStatus;
    }
    
    /** */
    public boolean checkExistenceInDb()
    {
        return false;
    }
    
    /** */
    public Date getLastPingTime()
    {
        return lastPingTime;
    }
    
     
    /** */
    public void unbindBusinessSession()
    {
    }
    
    /** */
    public Date getLastActivityTime()
    {
        return lastActivityTime;
    }
    
    /** */
    public void setPreviousNode(INode node)
    {
    }
    
    /** */
    public void setNextNode(INode node)
    {
    }
    
    /** */
    public INode getPreviousNode()
    {
        return null;
    }
    
    /** */
    public INode getNextNode()
    {
        return null;
    }
    
    /** */
    public JSONObject toJSONObject()
    {
        return null;
    }

    @Override
    public void onClose(IBaseConnection connection)
    {
        ownerOnlinePool.releaseDevice(this);
    }


    @Override
    public void onJSONCommand(AppJSONMessage command)
    {
    	command.bindDeviceWrapper(this);
        (new Thread(command)).run();
    }

    @Override
    public void bindBusinessSession(IBusinessSession session)
    {
        this.ownerBusinessSession = session;
    }

    @Override
    public void onPing(IBaseConnection conection)
    {
        this.updatePingTime();
    }


    @Override
    public void onTimeOut(IBaseConnection conection)
    {
        this.ownerOnlinePool.releaseDevice(this);
    }


    @Override
    public void syncSendMessage(ServerJSONMessage message)
    {
    	Logger logger = BusinessModule.instance.getLogger();
        try
		{
        	if (webSocketConnection == null)
        	{
        		logger.error("webSocketConnection == null");
        		return;
        	}
			webSocketConnection.syncSendMessage(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }

    @Override
    public void asyncSendMessage(ServerJSONMessage message)
    {
    	Logger logger = BusinessModule.instance.getLogger();
        try
		{
        	if (webSocketConnection == null)
        	{
        		logger.error("webSocketConnection == null");
        		return;
        	}
			webSocketConnection.asyncSendMessage(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }

	@Override
	public IBaseConnection getConnection()
	{
		return webSocketConnection;
	}
	
	public void setDevice(Device device)
	{
		this.device = device;
		deviceSn = device.getDevicecard().getDeviceSn();
	}
	
	public String getDeviceSn()
	{
		return this.deviceSn;
	}
}


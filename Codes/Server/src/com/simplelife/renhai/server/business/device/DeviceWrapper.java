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

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.IBaseConnection;
import com.simplelife.renhai.server.util.IBaseConnectionOwner;
import com.simplelife.renhai.server.util.IBusinessSession;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.INode;


/** */
public class DeviceWrapper implements IDeviceWrapper, INode, IBaseConnectionOwner
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
    
    /** */
    protected Consts.ServiceStatus serviceStatus;
    
    /** */
    protected Consts.BusinessStatus businessStatus;
    
    protected OnlineDevicePool ownerOnlinePool;
    
    public void setServiceStatus(Consts.ServiceStatus serviceStatus)
    {
    	this.serviceStatus = serviceStatus;
    }
    
    public void setBusinessStatus(Consts.BusinessStatus businessStatus)
    {
    	this.businessStatus = businessStatus;
    }
    
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
    	connection.bind(this);
    }
    
    /** */
    public void changeBusinessStatus(Consts.BusinessStatus targetStatus)
    {
    	switch(targetStatus)
    	{
    		case Idle:
    			break;
    		case Init:
    			if (businessStatus == Consts.BusinessStatus.Idle)
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
    public Consts.BusinessStatus getBusinessStatus()
    {
        return businessStatus;
    }
    
    /** */
    public Consts.ServiceStatus getServiceStatus()
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
	}
	
	public String getDeviceSn()
	{
		return this.device.getDeviceSn();
	}

	@Override
	public void setLastActivityTime(Date date)
	{
		this.lastActivityTime = date;
	}

	@Override
	public void setLastPingTime(Date date)
	{
		this.lastPingTime = date;
	}
}


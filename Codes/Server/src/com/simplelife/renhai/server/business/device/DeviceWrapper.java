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
import java.nio.ByteBuffer;
import java.util.Date;

import org.slf4j.Logger;

import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessType;
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
    
    // Time of last ping from APP
    protected Date lastPingTime;
    
    // Time of last request (including AlohaRequest and AppDataSyncRequest) from APP
    protected Date lastActivityTime;
    
    // Owner business session of this device, be null if device is not in status of SessionBound
    protected IBusinessSession ownerBusinessSession;
    
    // Real device object enclosed in this DeviceWrapper
    protected Device device;
    
    // Service status of device, to indicate if device is out of service
    protected Consts.ServiceStatus serviceStatus;
    
    // Business session
    protected Consts.BusinessStatus businessStatus;
    
    protected Consts.BusinessType businessType;
    
    protected OnlineDevicePool ownerOnlinePool;
    
    protected boolean sessionBindConfirmed;
    
    protected boolean chatConfirmed;
    
    protected boolean connectionLost;
    
    protected boolean assessProvided;
    
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
    	Logger logger = BusinessModule.instance.getLogger();
    	switch(targetStatus)
    	{
    		case Idle:
    			break;
    		case Init:
    			switch(businessStatus)
    			{
    				case Idle:
    					ownerOnlinePool.synchronizeDevice(this);
    					break;
    				case SessionBound:
    					this.unbindBusinessSession();
    					break;
    				default:
    					logger.error("Abnormal business status change for device:" + device.getDeviceSn() + ", source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			break;
    		case SessionBound:
    			break;
    		case WaitMatch:
    			switch(businessStatus)
    			{
    				case Init:
    					break;
    				case SessionBound:
    					this.unbindBusinessSession();
    					break;
    				default:
    					logger.error("Abnormal business status change for device:" + device.getDeviceSn() + ", source status: " + businessStatus.name() + ", target status: " + targetStatus.name());
    					break;
    			}
    			break;
    		default:
    			logger.error("Abnormal target business status for device:" + device.getDeviceSn());
    			break;
    	}
    }
    
    /** */
    public IBusinessSession getOwnerBusinessSession()
    {
        return ownerBusinessSession;
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
    	ownerBusinessSession = null;
    	sessionBindConfirmed = false;
    	chatConfirmed = false;
    	connectionLost = false;
    	assessProvided = false;
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
    public void onPing(IBaseConnection connection, ByteBuffer payload)
    {
    	if (this.ownerOnlinePool == null)
    	{
    		return;
    	}
    	
    	// 只有没有被释放的连接才回应ping
        this.updatePingTime();
        connection.pong(payload);
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

	@Override
	public void onSessionBindConfirmed()
	{
		sessionBindConfirmed = true;
	}

	@Override
	public void onChatConfirmed()
	{
		chatConfirmed = true;
	}

	@Override
	public void onConnectionLost()
	{
		connectionLost = true;
	}

	@Override
	public void onAssessProvided()
	{
		assessProvided = true;
	}

	@Override
	public boolean isSessionBindConfirmed()
	{
		return sessionBindConfirmed;
	}

	@Override
	public boolean isChatConfirmed()
	{
		return chatConfirmed;
	}

	@Override
	public boolean isConnectionLost()
	{
		return connectionLost;
	}

	@Override
	public boolean isAssessProvided()
	{
		return assessProvided;
	}

	@Override
	public void setBusinessType(BusinessType businessType)
	{
		this.businessType = businessType;
	}

	@Override
	public BusinessType getBusinessType()
	{
		return businessType;
	}

	/** */
    public void bindOnlineDevicePool(OnlineDevicePool pool)
    {
    	this.ownerOnlinePool = pool;
    }
    
    
	@Override
	public void unbindOnlineDevicePool()
	{
		this.ownerOnlinePool = null;
	}

	@Override
	public OnlineDevicePool getOwnerOnlineDevicePool()
	{
		return ownerOnlinePool;
	}
}


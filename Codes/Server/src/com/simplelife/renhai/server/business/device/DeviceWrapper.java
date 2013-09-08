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

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.util.Consts;
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
    
    /** */
    protected Consts.DeviceServiceStatus serviceStatus;
    
    /** */
    protected Consts.DeviceBusinessStatus businessStatus;
    
    protected OnlineDevicePool pool;
    
    /** */
    protected void updatePingTime()
    {
    
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
    public void changeBusinessStatus()
    {
    
    }
    
    /** */
    public void updateLastActivityTime()
    {
    
    }
    
    /** */
    public IBusinessSession getOwnerBusinessSession()
    {
        return ownerBusinessSession;
    
    }
    
    /** */
    public void bindOnlineDevicePool(OnlineDevicePool pool)
    {
    	this.pool = pool;
    }
    
    /** */
    public Device getDevice()
    {
        return device;
    }
    
    /** */
    public Consts.DeviceBusinessStatus getBusinessStatus()
    {
        return null;
    }
    
    /** */
    public Consts.DeviceServiceStatus getServiceStatus()
    {
        return null;
    }
    
    /** */
    public boolean checkExistenceInDb()
    {
        return false;
    }
    
    /** */
    public Date getLastPingTime()
    {
        return null;
    }
    
     
    /** */
    public void unbindBusinessSession()
    {
    }
    
    /** */
    public Date getLastActivityTime()
    {
        return null;
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

    /* (non-Javadoc)
     * @see IBaseConnectionOwner#onClose(IBaseConnection)
     */
    @Override
    public void onClose(IBaseConnection connection)
    {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see IBaseConnectionOwner#onJSONCommand(IAppJSONMessage)
     */
    @Override
    public void onJSONCommand(AppJSONMessage command)
    {
    	command.bindDeviceWrapper(this);
        (new Thread(command)).run();
    }


    /* (non-Javadoc)
     * @see IDeviceWrapper#bindBusinessSession(IBusinessSession)
     */
    @Override
    public void bindBusinessSession(IBusinessSession session)
    {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IBaseConnectionOwner#onPing(com.simplelife.renhai.server.util.IBaseConnection)
     */
    @Override
    public void onPing(IBaseConnection conection)
    {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IBaseConnectionOwner#onTimeOut(com.simplelife.renhai.server.util.IBaseConnection)
     */
    @Override
    public void onTimeOut(IBaseConnection conection)
    {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IDeviceWrapper#syncSendMessage(com.simplelife.renhai.server.util.IJSONObject)
     */
    @Override
    public void syncSendMessage(ServerJSONMessage message)
    {
        try
		{
			webSocketConnection.syncSendMessage(message);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IDeviceWrapper#asyncSendMessage(com.simplelife.renhai.server.util.IJSONObject)
     */
    @Override
    public void asyncSendMessage(ServerJSONMessage message)
    {
        try
		{
			webSocketConnection.asyncSendMessage(message);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IBaseConnectionOwner#getConnection()
	 */
	@Override
	public IBaseConnection getConnection()
	{
		return webSocketConnection;
	}
}


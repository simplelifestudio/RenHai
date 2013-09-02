/**
 * DeviceWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.device;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.util.DeviceBusinessStatus;
import com.simplelife.renhai.server.util.DeviceServiceStatus;
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
    protected WebSocketConnection webSocketConnection;
    
    /** */
    protected Date lastPingTime;
    
    /** */
    protected Date lastActivityTime;
    
    /** */
    protected IBusinessSession ownerBusinessSession;
    
    /** */
    protected Device device;
    
    /** */
    protected DeviceServiceStatus serviceStatus;
    
    /** */
    protected DeviceBusinessStatus businessStatus;
    
   
    /** */
    public Device Unnamed3;
    
    /** */
    protected void updatePingTime()
    {
    
    }
    
    /** */
    public void onChatConfirm()
    {
    
    }
    
    /** */
    public void DeviceWrapper()
    {
    
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
    
    }
    
    /** */
    public Device getDevice()
    {
        return device;
    }
    
    /** */
    public DeviceBusinessStatus getBusinessStatus()
    {
        return null;
    }
    
    /** */
    public DeviceServiceStatus getServiceStatus()
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
    public void onJSONCommand(IAppJSONMessage command)
    {
        // TODO Auto-generated method stub
        
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
    public void syncSendMessage(IJSONObject message)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.simplelife.renhai.server.util.IDeviceWrapper#asyncSendMessage(com.simplelife.renhai.server.util.IJSONObject)
     */
    @Override
    public void asyncSendMessage(IJSONObject message)
    {
        // TODO Auto-generated method stub
        
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

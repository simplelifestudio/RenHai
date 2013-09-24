/**
 * IDeviceWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.nio.ByteBuffer;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.json.AppJSONMessage;
import com.simplelife.renhai.server.json.ServerJSONMessage;


/** */
public interface IDeviceWrapper
{
    /** */
    public Consts.BusinessStatus getBusinessStatus();
    
    /** */
    public Consts.ServiceStatus getServiceStatus();
    
    /** */
    public boolean checkExistenceInDb();
    
    /** */
    public Date getLastPingTime();
    
    /** */
    public void bindBusinessSession(IBusinessSession session);
    
    /** */
    public void unbindBusinessSession();
    
    /** */
    public void syncSendMessage(ServerJSONMessage message);
    
    /** */
    public void asyncSendMessage(ServerJSONMessage message);
    
    /** */
    public Date getLastActivityTime();
    
    public Device getDevice();
    
    public void setDevice(Device device);
    
    public IBusinessSession getOwnerBusinessSession();
    
    public String getDeviceSn();
    
    public void updateActivityTime();
    
    public void updatePingTime();
    
    public void setServiceStatus(Consts.ServiceStatus serviceStatus);
    
    public void changeBusinessStatus(Consts.BusinessStatus targetStatus);
    
    public void setBusinessType(Consts.BusinessType businessType);
    
    public Consts.BusinessType getBusinessType();
    
    public void bindOnlineDevicePool(OnlineDevicePool pool);
    
    public void unbindOnlineDevicePool();
    
    public OnlineDevicePool getOwnerOnlineDevicePool();
    
    /** */
    public void onClose(IBaseConnection connection);
    
    /** */
    public void onPing(IBaseConnection conection, ByteBuffer payload);
    
    /** */
    public void onJSONCommand(AppJSONMessage command);
    
    /** */
    public void onTimeOut(IBaseConnection conection);
    
    public IBaseConnection getConnection();
    
    public JSONObject toJSONObject();
}

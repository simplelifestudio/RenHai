/**
 * IDeviceWrapper.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import java.util.Date;

import com.simplelife.renhai.server.business.device.Device;
import com.simplelife.renhai.server.json.ServerJSONMessage;


/** */
public interface IDeviceWrapper
{
    /** */
    public Consts.DeviceBusinessStatus getBusinessStatus();
    
    /** */
    public Consts.DeviceServiceStatus getServiceStatus();
    
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
}

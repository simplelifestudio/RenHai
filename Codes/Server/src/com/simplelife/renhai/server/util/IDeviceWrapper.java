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


/** */
public interface IDeviceWrapper
{
    /** */
    public DeviceBusinessStatus getBusinessStatus();
    
    /** */
    public DeviceServiceStatus getServiceStatus();
    
    /** */
    public boolean checkExistenceInDb();
    
    /** */
    public Date getLastPingTime();
    
    /** */
    public void bindBusinessSession(IBusinessSession session);
    
    /** */
    public void unbindBusinessSession();
    
    /** */
    public void syncSendMessage(IJSONObject message);
    
    /** */
    public void asyncSendMessage(IJSONObject message);
    
    /** */
    public Date getLastActivityTime();
}

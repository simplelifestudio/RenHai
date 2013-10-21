/**
 * IBusinessPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;


/** */
public interface IBusinessPool
{
    /** */
    public void onDeviceEnter(IDeviceWrapper device);
    
    /** */
    public void onDeviceLeave(IDeviceWrapper device, Consts.StatusChangeReason reason);
    
    public void startChat(IDeviceWrapper device);
    
    public void endChat(IDeviceWrapper device);
    
    public Consts.BusinessType getBusinessType();
}

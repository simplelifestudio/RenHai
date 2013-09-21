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
    public boolean deviceEnter(IDeviceWrapper device);
    
    /** */
    public void deviceLeave(IDeviceWrapper device);
    
    public void startChat(IDeviceWrapper device);
    
    public void endChat(IDeviceWrapper device);
}

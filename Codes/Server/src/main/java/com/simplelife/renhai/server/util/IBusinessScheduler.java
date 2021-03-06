/**
 * IBusinessScheduler.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;

import com.simplelife.renhai.server.business.pool.AbstractBusinessDevicePool;


/** */
public interface IBusinessScheduler
{
    /** */
    public void startScheduler();
    
    /** */
    public void bind(AbstractBusinessDevicePool pool);
    
    /** */
    public void stopScheduler();
    
    /** */
    public void schedule();
}

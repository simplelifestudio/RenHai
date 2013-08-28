/**
 * AbstractBusinessScheduler.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import com.simplelife.renhai.server.util.IBusinessPool;
import com.simplelife.renhai.server.util.IBusinessScheduler;


/** */
public class AbstractBusinessScheduler implements IBusinessScheduler
{
    /** */
    private IBusinessPool ownerBusinessPool;
    
    /** */
    public void startScheduler()
    {
    }
    
    /** */
    public void bind(IBusinessPool pool)
    {
    }
    
    /** */
    public void stopScheduler()
    {
    }
    
    /** */
    public void schedule()
    {
    }
}

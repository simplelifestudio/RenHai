/**
 * BusinessSessionPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.session;

import java.util.Timer;

import com.simplelife.renhai.server.business.pool.AbstractPool;
import com.simplelife.renhai.server.util.IBusinessSession;


/** */
public class BusinessSessionPool extends AbstractPool
{
    /** */
    public Timer timer;
    
    /** */
    public IBusinessSession getBusinessSession()
    {
        return null;
    
    }
    
    /** */
    public void recycleBusinessSession(IBusinessSession session)
    {
    
    }
    
    /** */
    public void init()
    {
    
    }
    
    /** */
    public void checkWebRTCToken()
    {
    
    }
    
    /** */
    public boolean isPoolFull()
    {
        return false;
    }
    
    /** */
    public void updateCount()
    {
    }
    
    /** */
    public int getElementCount()
    {
        return 0;
    }
}

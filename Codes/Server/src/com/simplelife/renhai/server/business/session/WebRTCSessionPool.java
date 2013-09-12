/**
 * WebRTCSessionPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.session;

import java.util.LinkedList;
import java.util.Timer;

import com.simplelife.renhai.server.business.pool.AbstractPool;


/** */
public class WebRTCSessionPool extends AbstractPool
{
    /** */
    private LinkedList webRTCSessionLink;
    
    /** */
    protected Timer timer;
    
    /** */
    public WebRTCSession getWebRTCSession()
    {
        return null;
    
    }
    
    /** */
    public WebRTCSession getSession()
    {
        return null;
    
    }
    
    /** */
    public void init()
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
    
    /** */
    public boolean saveToDb()
    {
        return false;
    }
    
    /** */
    public boolean loadFromDb()
    {
        return false;
    }
    
	@Override
	public void clearPool()
	{
		webRTCSessionLink.clear();
	}
}

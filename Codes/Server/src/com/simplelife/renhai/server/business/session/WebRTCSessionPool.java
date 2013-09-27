/**
 * WebRTCSessionPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.simplelife.renhai.server.business.pool.AbstractPool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;


/** */
public class WebRTCSessionPool extends AbstractPool
{
    /** */
    private List<WebRTCSession> webRTCSessionList = new ArrayList<WebRTCSession>();
    
    /** */
    protected Timer timer;
    
    /** */
    public WebRTCSession getWebRTCSession()
    {
		if (webRTCSessionList.isEmpty())
		{
			return null;
		}
		return webRTCSessionList.remove(0);
    }

    
    /** */
    public void init()
    {
    	clearPool();
    	loadFromDb();
    }
    
    /** */
    public boolean isPoolFull()
    {
        return (webRTCSessionList.size() > capacity);
    }
    
    /** */
    public int getElementCount()
    {
        return webRTCSessionList.size();
    }
    
    /** */
    public boolean saveToDb()
    {
    	for (WebRTCSession session : webRTCSessionList)
    	{
    		DBModule.instance.cache(session);
    	}
        return true;
    }
    
    /** */
    public boolean loadFromDb()
    {
    	for (int i = 0; i < this.capacity; i++)
		{
    		webRTCSessionList.add(new WebRTCSession());
		}
    	
        return true;
    }
    
	@Override
	public void clearPool()
	{
		webRTCSessionList.clear();
	}
}

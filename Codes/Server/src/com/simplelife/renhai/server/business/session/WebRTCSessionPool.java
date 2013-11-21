/**
 * WebRTCSessionPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.session;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;

import com.simplelife.renhai.server.business.pool.AbstractPool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Webrtcsession;
import com.simplelife.renhai.server.db.WebrtcsessionMapper;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.OpentokUtil;


/** */
public class WebRTCSessionPool extends AbstractPool
{
    private ConcurrentLinkedQueue<Webrtcsession> webRTCSessionList = new ConcurrentLinkedQueue<>();
    protected Timer timer = new Timer();
    public static final WebRTCSessionPool instance = new WebRTCSessionPool();
    private Logger logger = DBModule.instance.getLogger();
    
    private WebRTCSessionPool()
    {
    	this.capacity = 100;
    	//this.capacity = GlobalSetting.BusinessSetting.OnlinePoolCapacity / 2;
    }
    
    public void recycleWetRTCSession(Webrtcsession session)
    {
    	webRTCSessionList.add(session);
    }
    
    public Webrtcsession getWebRTCSession()
    {
		if (webRTCSessionList.isEmpty())
		{
			return null;
		}
		return webRTCSessionList.remove();
    }

    public void startService()
    {
    	timer.scheduleAtFixedRate(new TokenCheckTask(), GlobalSetting.BusinessSetting.OpenTokTokenDuration, GlobalSetting.BusinessSetting.OpenTokTokenDuration);
    	loadFromDb();
    }
    
    public void stopService()
    {
    	timer.cancel();
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
    
    /*
    public boolean saveToDb()
    {
    	for (Webrtcsession session : webRTCSessionList)
    	{
    		DAOWrapper.cache(session);
    	}
        return true;
    }
    */

    
    /** */
    public boolean loadFromDb()
    {
    	logger.debug("Load WebRTC tokens from DB...");
    	SqlSession session = DAOWrapper.getSession();
		WebrtcsessionMapper mapper = session.getMapper(WebrtcsessionMapper.class);
		List<Webrtcsession> list = mapper.selectAll();
		for (Webrtcsession rtcSession : list)
		{
			webRTCSessionList.add(rtcSession);
		}
		
		
		if (list.size() < this.capacity)
		{
			int appendCount = capacity - list.size();
			for (int i = 0; i < appendCount; i++)
			{
				webRTCSessionList.add(createWebRTCSession());
			}
		}
		
		logger.debug("Finished loading WebRTC tokens from DB, start to update tokens if needed.");
		this.checkExpiredToken();
        return true;
    }
    
    public Webrtcsession createWebRTCSession()
    {
    	Webrtcsession session = new Webrtcsession();
    	session.setWebrtcsession(OpentokUtil.requestNewSession());
    	session.setRequestDate(System.currentTimeMillis());
    	session.updateToken();
    	return session;
    }
    
	@Override
	public void clearPool()
	{
		webRTCSessionList.clear();
	}
	
	public void checkExpiredToken()
	{
		logger.debug("Start to check expired WebRTC token");
		Iterator<Webrtcsession> it = webRTCSessionList.iterator();
		Webrtcsession session;
		while (it.hasNext())
		{
			session = it.next();
			if (session.getWebrtcsession() == null)
			{
				logger.error("Fatal error: Webrtcsession in webRTCSessionList has null session ID!");
				session.setWebrtcsession(OpentokUtil.requestNewSession());
			}
			
			Long now = System.currentTimeMillis();
			if (session.getToken() == null)
			{
				session.updateToken();
			}
			else if (session.getExpirationDate() < now - GlobalSetting.BusinessSetting.OpenTokTokenDuration)
			{
				session.updateToken();
			}
		}
	}

	
	
	private class TokenCheckTask extends TimerTask
	{
		@Override
		public void run()
		{
			WebRTCSessionPool.instance.checkExpiredToken();
		}
	}
}

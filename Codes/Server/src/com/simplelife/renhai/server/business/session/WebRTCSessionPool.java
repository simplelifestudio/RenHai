/**
 * WebRTCSessionPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.session;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;

import com.opentok.api.OpenTokSDK;
import com.simplelife.renhai.server.business.pool.AbstractPool;
import com.simplelife.renhai.server.db.DAOWrapper;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Webrtcaccount;
import com.simplelife.renhai.server.db.WebrtcaccountMapper;
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
    private int webRTCAccountId = 0;
    
    private WebRTCSessionPool()
    {
    	this.capacity = 100;
    	//this.capacity = GlobalSetting.BusinessSetting.OnlinePoolCapacity / 2;
    }
    
    public void recycleWetRTCSession(Webrtcsession session)
    {
    	if (session == null)
    	{
    		return;
    	}
    	
    	webRTCSessionList.add(session);
    	logger.debug("WebRTC session <{}> is recycled", session.getWebRtcSessionId());
    }
    
    public Webrtcsession getWebRTCSession()
    {
		if (webRTCSessionList.isEmpty())
		{
			logger.warn("All WebRTC session are used up");
			return null;
		}
		
		Webrtcsession session = webRTCSessionList.remove(); 
		logger.debug("WebRTC session <{}> is removed from pool per request by business session", session.getWebRtcSessionId());
		return session; 
    }

    public void startService()
    {
    	timer.scheduleAtFixedRate(new TokenCheckTask(), GlobalSetting.BusinessSetting.OpenTokTokenDuration, GlobalSetting.BusinessSetting.OpenTokTokenDuration);
    	this.webRTCAccountId = getAccountIdByDate();
    	loadFromDb();
    }
    
    private int getAccountIdByDate()
    {
    	SqlSession session = DAOWrapper.getSession();
    	WebrtcaccountMapper mapper = session.getMapper(WebrtcaccountMapper.class);
    	int accountNum = mapper.countAll();
    	
    	if (accountNum == 0)
    	{
    		logger.error("Fatal Error: WebRTC account is not configged in DB!");
    		return -1;
    	}
    	
    	Calendar cal = Calendar.getInstance();
    	int day = cal.get(Calendar.DAY_OF_YEAR);
    	
    	int accountId = day % accountNum + 1;
    	return accountId;
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
    	SqlSession session = DAOWrapper.getSession();
    	WebrtcaccountMapper mapper = session.getMapper(WebrtcaccountMapper.class);
    	Webrtcaccount account = mapper.selectByPrimaryKey(this.webRTCAccountId);
    	if (account == null)
    	{
    		logger.error("Fatal Error: WebRTC account with id {} can't be found in DB, default account will be used.");
    		account = new Webrtcaccount();
    		account.setAccountKey(GlobalSetting.BusinessSetting.OpenTokKey);
    		account.setAccountSecret(GlobalSetting.BusinessSetting.OpenTokSecret);
    	}
    	
    	OpenTokSDK sdk = new OpenTokSDK(account.getAccountKey(), account.getAccountSecret());
    	
    	logger.debug("Load WebRTC tokens from DB...");
    	WebrtcsessionMapper sessionMapper = session.getMapper(WebrtcsessionMapper.class);
		List<Webrtcsession> list = sessionMapper.selectAll(this.webRTCAccountId);
		for (Webrtcsession rtcSession : list)
		{
			webRTCSessionList.add(rtcSession);
		}
		
		if (list.size() < this.capacity)
		{
			int appendCount = capacity - list.size();
			for (int i = 0; i < appendCount; i++)
			{
				webRTCSessionList.add(createWebRTCSession(account, sdk));
			}
		}
		
		logger.debug("Finished loading WebRTC tokens from DB, start to update tokens if needed.");
		this.checkExpiredToken(sdk);
        return true;
    }
    
    public Webrtcsession createWebRTCSession(Webrtcaccount account, OpenTokSDK sdk)
    {
    	Webrtcsession session = new Webrtcsession();
    	session.setWebrtcsession(OpentokUtil.requestNewSession(sdk));
    	session.setRequestDate(System.currentTimeMillis());
    	session.setWebRTCAccountId(account.getWebRTCAccountId());
    	session.updateToken(sdk);
    	return session;
    }
    
	@Override
	public void clearPool()
	{
		webRTCSessionList.clear();
	}
	
	public void checkExpiredToken()
	{
		SqlSession session = DAOWrapper.getSession();
    	WebrtcaccountMapper mapper = session.getMapper(WebrtcaccountMapper.class);
    	Webrtcaccount account = mapper.selectByPrimaryKey(this.webRTCAccountId);
    	if (account == null)
    	{
    		logger.error("Fatal Error: WebRTC account with id {} can't be found in DB, default account will be used.");
    		account = new Webrtcaccount();
    		account.setAccountKey(GlobalSetting.BusinessSetting.OpenTokKey);
    		account.setAccountSecret(GlobalSetting.BusinessSetting.OpenTokSecret);
    	}
    	
    	OpenTokSDK sdk = new OpenTokSDK(account.getAccountKey(), account.getAccountSecret());
    	checkExpiredToken(sdk);
	}
	
	public void checkExpiredToken(OpenTokSDK sdk)
	{
		int accountId = this.getAccountIdByDate();
		if (accountId != this.webRTCAccountId)
		{
			logger.debug("Start to load WebRTC tokens of account <{}>", accountId);
			clearPool();
			loadFromDb();
			
			if (!this.webRTCSessionList.isEmpty())
			{
				webRTCAccountId = accountId;
			}
			else
			{
				logger.error("Fatal Error: failed to load WebRTC tokens of account <{}>", accountId);
			}
		}
		logger.debug("Start to check expired WebRTC token");
		Iterator<Webrtcsession> it = webRTCSessionList.iterator();
		Webrtcsession session;
		while (it.hasNext())
		{
			session = it.next();
			if (session.getWebrtcsession() == null)
			{
				logger.error("Fatal error: Webrtcsession in webRTCSessionList has null session ID!");
				session.setWebrtcsession(OpentokUtil.requestNewSession(sdk));
			}
			
			Long now = System.currentTimeMillis();
			if (session.getToken() == null)
			{
				session.updateToken(sdk);
			}
			else if (session.getExpirationDate() < now - GlobalSetting.BusinessSetting.OpenTokTokenDuration)
			{
				session.updateToken(sdk);
			}
		}
	}

	
	
	private class TokenCheckTask extends TimerTask
	{
		public TokenCheckTask()
		{
			Thread.currentThread().setName("TokenCheckTimer");
		}
		
		@Override
		public void run()
		{
			WebRTCSessionPool.instance.checkExpiredToken();
		}
	}
}

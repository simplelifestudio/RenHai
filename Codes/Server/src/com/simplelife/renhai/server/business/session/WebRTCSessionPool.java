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
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.OpentokUtil;


/** */
public class WebRTCSessionPool extends AbstractPool
{
    private ConcurrentLinkedQueue<Webrtcsession> webRTCSessionList = new ConcurrentLinkedQueue<>();
    protected Timer timer = new Timer();
    public static final WebRTCSessionPool instance = new WebRTCSessionPool();
    private Logger logger = DBModule.instance.getLogger();
    private Webrtcaccount account = null;
    private int accountNum = -1;
    
    private WebRTCSessionPool()
    {
    	//this.capacity = 100;
    	this.capacity = GlobalSetting.BusinessSetting.OnlinePoolCapacity / 2;
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
    	//timer.scheduleAtFixedRate(new TokenCheckTask(), GlobalSetting.BusinessSetting.OpenTokTokenDuration, GlobalSetting.BusinessSetting.OpenTokTokenDuration);
    	timer.scheduleAtFixedRate(new TokenCheckTask(), 10000, 10000);
    	checkExpiredToken();
    }
    
    private int getAccountIdByDate()
    {
    	if (accountNum == -1)
    	{
    		SqlSession session = DAOWrapper.instance.getSession();
    		WebrtcaccountMapper mapper = session.getMapper(WebrtcaccountMapper.class);
    		accountNum = mapper.countAll();
    		session.close();
    	}
    	
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
    public int getDeviceCount()
    {
        return webRTCSessionList.size();
    }
    
    /*
    public boolean saveToDb()
    {
    	for (Webrtcsession session : webRTCSessionList)
    	{
    		DAOWrapper.instance.cache(session);
    	}
        return true;
    }
    */
    
    /** */
    public boolean loadFromDb(Webrtcaccount account, OpenTokSDK sdk)
    {
    	SqlSession session = DAOWrapper.instance.getSession();
    	logger.debug("Load WebRTC tokens from DB...");
    	WebrtcsessionMapper sessionMapper = session.getMapper(WebrtcsessionMapper.class);
		List<Webrtcsession> list = sessionMapper.selectAll(account.getWebRTCAccountId());
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
		
		logger.debug("Finished loading {} WebRTC tokens from DB, start to update tokens if needed.", webRTCSessionList.size());
		session.close();
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
	
	private Webrtcaccount getAccount(int accountId)
	{
		SqlSession session = DAOWrapper.instance.getSession();
    	WebrtcaccountMapper mapper = session.getMapper(WebrtcaccountMapper.class);
    	Webrtcaccount account = mapper.selectByPrimaryKey(accountId);
    	session.close();
    	if (account == null)
    	{
    		logger.error("Fatal Error: WebRTC account with id {} can't be found in DB, default account will be used.");
    		account = new Webrtcaccount();
    		account.setAccountKey(GlobalSetting.BusinessSetting.OpenTokKey);
    		account.setAccountSecret(GlobalSetting.BusinessSetting.OpenTokSecret);
    	}
    	return account;
	}
	
	private OpenTokSDK getOpenTokSDK(Webrtcaccount account)
	{
		OpenTokSDK sdk = new OpenTokSDK(account.getAccountKey(), account.getAccountSecret());
    	return sdk;
	}
	
	public void checkExpiredToken()
	{
		logger.debug("Start to check expired token");
		int accountId = this.getAccountIdByDate();
		if (account == null)
		{
			// The first time
			account = getAccount(accountId);
			OpenTokSDK sdk = getOpenTokSDK(account);
			loadFromDb(account, sdk);
			return;
		}
		
		if (accountId != account.getWebRTCAccountId())
		{
			logger.debug("Clear tokens and load tokens of next account from DB");
			clearPool();
			
			Webrtcaccount tmpAccount = getAccount(accountId);
			OpenTokSDK sdk = getOpenTokSDK(tmpAccount);
			loadFromDb(tmpAccount, sdk);
			
			if (this.webRTCSessionList.isEmpty())
			{
				logger.error("Fatal Error: failed to load WebRTC tokens of account <{}>", accountId);
				return;
			}
			account = tmpAccount;
		}
		
		OpenTokSDK sdk = getOpenTokSDK(account);
		updateToken(sdk);
	}

	private void updateToken(OpenTokSDK sdk)
	{
		logger.debug("Start to update WebRTC tokens");
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
			
		}
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("TokenCheckTimer");
			try
			{
				WebRTCSessionPool.instance.checkExpiredToken();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
}

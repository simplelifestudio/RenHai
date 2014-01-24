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
    private Webrtcaccount curAccount = null;
    private int accountNum = -1;
    private int dayOfCurAccount = 0;
    private int curAccountId = 0;
    
    private WebRTCSessionPool()
    {
    	//this.capacity = 100;
    	this.capacity = GlobalSetting.BusinessSetting.WebRTCSessionPoolCapacity;
    }
    
    public Integer getWebRTCAccountKey()
    {
    	if (curAccount == null)
    	{
    		return null;
    	}
    	
    	return curAccount.getAccountKey();
    }
    
    public void recycleWetRTCSession(Webrtcsession session)
    {
    	if (session == null)
    	{
    		logger.error("The WebRTC session to be recycled is null");
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
    	timer.scheduleAtFixedRate(new TokenCheckTask(), GlobalSetting.BusinessSetting.AccountCheckInterval, GlobalSetting.BusinessSetting.AccountCheckInterval);
    	//timer.scheduleAtFixedRate(new TokenCheckTask(), 10000, 10000);
    	checkExpiredToken();
    }
    
    private int getAccountIdOfToday()
    {
    	if (accountNum == -1)
    	{
    		SqlSession session = DAOWrapper.instance.getSession();
    		WebrtcaccountMapper mapper = session.getMapper(WebrtcaccountMapper.class);
    		accountNum = mapper.countAll();
    		session.close();
    		logger.debug("Update number of WebRTC accounts in DB");
    	}
    	
    	if (accountNum == 0)
    	{
    		logger.error("Fatal Error: WebRTC account is not configged in DB!");
    		return -1;
    	}
    	
    	if (accountNum == 1)
    	{
    		logger.debug("There is only one WebRTC account");
    		return 1;
    	}

    	Calendar cal = Calendar.getInstance();
    	int day = cal.get(Calendar.DAY_OF_YEAR);
    	if (day != dayOfCurAccount)
    	{
    		// Range of curAccountId: 1 to accountNum
    		int tmpId = curAccountId;
    		curAccountId++;
    		if (curAccountId > accountNum)
        	{
    			curAccountId = 1;
        	}
    		
    		if (logger.isDebugEnabled())
    		{
    			logger.debug("WebRTC account ID was changed from " + tmpId + " to " + curAccountId + ", value of dayOfCurAccount is changed from "+ dayOfCurAccount +" to " + day);
    		}
        	dayOfCurAccount = day;
    	}

    	return curAccountId;
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
    public boolean loadFromDb(Webrtcaccount account)
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
			
			if (logger.isDebugEnabled())
			{
				logger.debug("Try to create {} tokens over internet, account details: ", appendCount);
				logger.debug("Account key: {}", account.getAccountKey());
				logger.debug("Account secret: {}", account.getAccountSecret());
				logger.debug("Login account: {}", account.getLoginAccount());
			}
			
			logger.debug("Before creating sessions from OpenTok, get sdk by account: {}", account.getLoginAccount());
			OpenTokSDK sdk = getOpenTokSDK(account);
			for (int i = 0; i < appendCount; i++)
			{
				webRTCSessionList.add(createWebRTCSession(account, sdk));
			}
			logger.error("After creating sessions from OpenTok, {} sessions created", appendCount);
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
    		logger.error("Fatal Error: WebRTC account with id {} can't be found in DB, default account will be used.", accountId);
    		//account = new Webrtcaccount();
    		//account.setAccountKey(GlobalSetting.BusinessSetting.OpenTokKey);
    		//account.setAccountSecret(GlobalSetting.BusinessSetting.OpenTokSecret);
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
		int idOfToday = this.getAccountIdOfToday();
		
		if (curAccount == null)
		{
			logger.debug("It's the first time of system running, try to load WebRTC account from DB");
			// The first time
			curAccount = getAccount(idOfToday);
			if (curAccount == null)
			{
				logger.error("Fatal error: failed to load WebRTC Token due to account is null by ID {}, it's the first load", idOfToday);
				return;
			}
			
			logger.debug("Before loadFromDb(curAccount), account: {}", curAccount.getLoginAccount());
			loadFromDb(curAccount);
			logger.debug("After loadFromDb(curAccount), account: {}", curAccount.getLoginAccount());
			return;
		}
		
		if (idOfToday != curAccount.getWebRTCAccountId())
		{
			logger.debug("Clear tokens and load tokens of next account from DB, account ID of today: {}", idOfToday);
			clearPool();
			
			Webrtcaccount tmpAccount = getAccount(idOfToday);
			if (tmpAccount == null)
			{
				logger.error("Fatal error: failed to load WebRTC Token due to account is null by ID {}", idOfToday);
				return;
			}
			
			logger.debug("Before loading WebRTC sessions from DB, account: {}", tmpAccount.getLoginAccount());
			loadFromDb(tmpAccount);
			logger.debug("After loading WebRTC sessions from DB, account: {}", tmpAccount.getLoginAccount());
			
			if (this.webRTCSessionList.isEmpty())
			{
				logger.error("Fatal Error: failed to load WebRTC tokens of account <{}>", idOfToday);
				return;
			}
			if (logger.isDebugEnabled())
			{
				logger.debug("WebRTC account was changed from " + curAccount.getLoginAccount() + " to " + tmpAccount.getLoginAccount());
			}
			curAccount = tmpAccount;
		}
		
		OpenTokSDK sdk = getOpenTokSDK(curAccount);
		logger.debug("Before updateToken(sdk), get SDK by account: {}", curAccount.getLoginAccount());
		updateToken(sdk);
		logger.debug("After updateToken(sdk), account: {}", curAccount.getLoginAccount());
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

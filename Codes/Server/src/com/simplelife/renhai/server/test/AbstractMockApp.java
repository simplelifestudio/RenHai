
/**
 * AbstractMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.DBModule;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.db.Impresscard;
import com.simplelife.renhai.server.db.Interestcard;
import com.simplelife.renhai.server.db.Profile;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.JSONKey;

/** */
public abstract class AbstractMockApp implements IMockApp
{
	Logger logger = LoggerFactory.getLogger(AbstractMockApp.class);
	
	private class PingTask extends TimerTask
	{
		private AbstractMockApp mockApp;
		public PingTask(AbstractMockApp mockApp)
		{
			this.mockApp = mockApp;
		}
		
		@Override
		public void run()
		{
			mockApp.ping();
		}
	}
	
	protected class AutoReplyTask extends Thread
	{
		private JSONObject message;
		private AbstractMockApp app;
		
		public AutoReplyTask(JSONObject message, AbstractMockApp app)
		{
			this.message = message;
			this.app = app;
		}
		
		@Override
		public void run()
		{
			logger.debug("Mock app trying to response BusinessSessionNotification");
			JSONObject body = message.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
			Consts.NotificationType operationType = Consts.NotificationType.parseValue(body.getIntValue(JSONKey.OperationType)); 
			app.sendNotificationResponse(operationType, "", "1");
		}
	}
	
    /** */
    protected int sessionId;
    protected Timer pingTimer = new Timer();
    
    /** */
    protected IDeviceWrapper deviceWrapper;
    protected String peerDeviceId;
    protected String businessSessionId;
    protected Consts.BusinessType businessType;
    protected JSONObject lastReceivedCommand;
    
    protected JSONObject jsonObject = new JSONObject();
    protected JSONObject header = new JSONObject();
    protected JSONObject body = new JSONObject();
	
	protected Lock lock = new ReentrantLock();
	protected Condition condition = lock.newCondition();
	
	protected String OSVersion = "iOS 6.1.2";
	protected String AppVersion = "0.1";
	protected String Location = "22.511962,113.380301";
	protected String DeviceModel = "iPhone6";
	
	protected boolean autoReply = true;
	
	public Device createNewDevice(String deviceSn)
	{
		// Create new impress card
		Impresscard impressCard = new Impresscard();
		impressCard.setChatLossCount(0);
		impressCard.setChatTotalCount(0);
		impressCard.setChatTotalDuration(0);
		
		// Create new interest card
		Interestcard interestCard = new Interestcard();
		
		// Create new profile
		Profile profile = new Profile();
		long now = System.currentTimeMillis();
		profile.setLastActivityTime(now);
		profile.setCreateTime(now);
		profile.setServiceStatus(Consts.ServiceStatus.Normal.name());
		
		// Bind profile with cards
		interestCard.setProfile(profile);
		impressCard.setProfile(profile);
		
		// Create new deviceCard
		Devicecard deviceCard = new Devicecard();
		deviceCard.setRegisterTime(now);
		deviceCard.setOsVersion(OSVersion);
		deviceCard.setAppVersion(AppVersion);
		deviceCard.setIsJailed(Consts.YesNo.No.toString());
		deviceCard.setLocation(Location);
		deviceCard.setDeviceModel(DeviceModel);
		
		// Create Device object and bind with cards
		Device device = new Device();
		device.setDevicecard(deviceCard);
		device.setProfile(profile);
		device.setDeviceSn(deviceSn);
		
		DBModule.instance.cache(device);
		return device;
	}
	
	public String getOSVersion()
	{
		return OSVersion;
	}

	public void setOSVersion(String oSVersion)
	{
		OSVersion = oSVersion;
	}

	public String getAppVersion()
	{
		return AppVersion;
	}

	public void setAppVersion(String appVersion)
	{
		AppVersion = appVersion;
	}

	public String getLocation()
	{
		return Location;
	}

	public void setLocation(String location)
	{
		Location = location;
	}

	public String getDeviceModel()
	{
		return DeviceModel;
	}

	public void setDeviceModel(String deviceModel)
	{
		DeviceModel = deviceModel;
	}


    
    public void clearLastReceivedCommand()
	{
		lastReceivedCommand = null;
	}
    
    public JSONObject getLastReceivedCommand()
    {
    	return lastReceivedCommand;
    }
    
    public boolean lastReceivedCommandIsError()
    {
    	JSONObject header = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope)
    			.getJSONObject(JSONKey.Header);
    	int messageId = header.getIntValue(JSONKey.MessageId);
    	return (messageId == Consts.MessageId.ServerErrorResponse.getValue());
    }
    
    
    public AbstractMockApp()
    {
    	startTimer();
    }

    public void startTimer()
    {
    	this.pingTimer.scheduleAtFixedRate(new PingTask(this), new Date(System.currentTimeMillis() + 5000), GlobalSetting.TimeOut.PingInterval);
    }
    
    public void stopTimer()
    {
    	this.pingTimer.cancel();
    }
    
    /**
     * Clear jsonMap and add default fields
     */
    protected void init()
    {
    	jsonObject.clear();
    	jsonObject.put(JSONKey.Header, header);
    	jsonObject.put(JSONKey.Body, body);
    	
    	header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.toString());
    	header.put(JSONKey.MessageSn, CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfMessageSn));
    	header.put(JSONKey.DeviceId, deviceWrapper.getDevice().getDeviceId());
    	header.put(JSONKey.DeviceSn, deviceWrapper.getDeviceSn());
    	header.put(JSONKey.TimeStamp, DateUtil.getNow());
    }
    
    public void bindDeviceWrapper(IDeviceWrapper deviceWrapper)
    {
    	this.deviceWrapper = deviceWrapper;
    }
    
    public IDeviceWrapper getDeviceWrapper()
    {
    	return deviceWrapper;
    }
    
    public void waitMessage()
    {
    	if (lastReceivedCommand != null)
    	{
    		return;
    	}
    	
    	lock.lock();
    	try
		{
			condition.await(10, TimeUnit.SECONDS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
    	lock.unlock();
    }
    
    public void startAutoReply()
    {
    	autoReply = true;
    }
    
    public void stopAutoReply()
    {
    	autoReply = false;
    }
}

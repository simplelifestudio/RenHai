
/**
 * AbstractMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.db.Devicecard;
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
    
    
	private String OSVersion = "iOS 6.1.2";
	private String AppVersion = "0.1";
	private String Location = "22.511962,113.380301";
	private String DeviceModel = "iPhone6";
	
	protected Lock lock = new ReentrantLock();
	protected Condition condition = lock.newCondition();
	
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
    
    public AbstractMockApp()
    {
    	startTimer();
    }

    public void startTimer()
    {
    	this.pingTimer.scheduleAtFixedRate(new PingTask(this), DateUtil.getNowDate(), GlobalSetting.TimeOut.PingInterval);
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
    	
    	Device device = deviceWrapper.getDevice();
    	device.setDeviceSn(CommonFunctions.getRandomString(20));
    	
    	Devicecard card = deviceWrapper.getDevice().getDevicecard();
		card.setOsVersion(OSVersion);
		card.setAppVersion(AppVersion);
		card.setIsJailed(Consts.YesNo.No.toString());
		card.setLocation(Location);
		card.setDeviceModel(DeviceModel);
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
}

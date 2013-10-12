/**
 * AbstractMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simplelife.renhai.server.business.BusinessModule;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.test.MockAppConsts.MockAppBusinessStatus;
import com.simplelife.renhai.server.test.MockAppConsts.MockAppRequest;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.IMockConnection;
import com.simplelife.renhai.server.util.JSONKey;

/** */
public class MockApp implements IMockApp, Runnable
{
	private class PingTask extends TimerTask
	{
		private MockApp mockApp;
		public PingTask(MockApp mockApp)
		{
			this.mockApp = mockApp;
		}
		
		@Override
		public void run()
		{
			try
			{
				Thread.currentThread().setName("Ping");
				mockApp.ping();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	
	protected class AutoReplyTask extends Thread
	{
		private MockAppConsts.MockAppRequest messageIdToBeSent;
		private JSONObject receivedMessage;
		private MockApp app;
		private int delay;
		private MockAppConsts.MockAppBusinessStatus nextStatus;
		
		public AutoReplyTask(
				MockAppConsts.MockAppRequest messageIdToBeSent,
				JSONObject receivedMessage, 
				MockApp app, 
				int delay, 
				MockAppConsts.MockAppBusinessStatus nextStatus)
		{
			this.messageIdToBeSent = messageIdToBeSent;
			this.receivedMessage = receivedMessage;
			this.app = app;
			this.delay = delay;
			this.nextStatus = nextStatus;
		}
		
		@Override
		public void run()
		{
			if (delay > 0)
			{
				try
				{
					logger.debug("Sleep " + delay + " ms, next status: " + nextStatus + ", message to be sent: " + messageIdToBeSent);
					AutoReplyTask.sleep(delay);
					logger.debug("Recover from sleep");
				}
				catch (InterruptedException e)
				{
					FileLogger.printStackTrace(e);
				}
			}

			if (nextStatus != null)
			{
				app.setBusinessStatus(nextStatus);
			}
			switch(messageIdToBeSent)
			{
				case AlohaRequest:
					app.sendAlohaRequest();
					break;
				case AppDataSyncRequest:
					app.syncDevice();
					break;
				case EnterPool:
					app.enterPool(BusinessType.Interest);
					break;
				case LeavePool:
					app.leavePool();
					break;
				case AgreeChat:
					app.chatConfirm(true);
					break;
				case RejectChat:
					app.chatConfirm(false);
					break;
				case EndChat:
					app.endChat();
					break;
				case AssessAndContinue:
					app.assessAndContinue("^#Happy#^,assessFromDevice" + deviceSn);
					break;
				case AssessAndQuit:
					app.assessAndContinue("^#SoSo#^,assessFromDevice" + deviceSn);
					break;
				case BusinessSessionNotificationResponse:
					JSONObject body = receivedMessage.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
					JSONObject header = receivedMessage.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header);
					Consts.NotificationType operationType = Consts.NotificationType.parseValue(body.getIntValue(JSONKey.OperationType));
					String messageSn = header.getString(JSONKey.MessageSn);
					
					app.sendNotificationResponse(messageSn, operationType, "", "1");
					break;
				case ServerDataSyncRequest:
					app.sendServerDataSyncRequest();
					break;
			}
		}
	}
	
	public final static String OSVersion = "iOS 6.1.2";
	public final static String AppVersion = "0.1";
	public final static String Location = "22.511962,113.380301";
	public final static String DeviceModel = "iPhone6";
	
	protected IMockConnection connection;
	protected Logger logger = LoggerFactory.getLogger(MockApp.class);

    protected int sessionId;
    protected Timer pingTimer = new Timer();
    protected String lastSentMessageSn;
    
    protected String peerDeviceId;
    protected String businessSessionId;
    protected Consts.BusinessType businessType;
    protected JSONObject lastReceivedCommand;
    
    protected JSONObject jsonObject = new JSONObject();
    protected JSONObject header = new JSONObject();
    protected JSONObject body = new JSONObject();
	
	protected Lock lock = new ReentrantLock();
	protected Condition condition = lock.newCondition();
	
	protected String deviceSn;
	protected int deviceId;
	protected String osVersion = MockApp.OSVersion;
	protected String appVersion = MockApp.AppVersion;
	protected String location = MockApp.Location;
	protected String deviceModel = MockApp.DeviceModel;
	
	protected JSONObject targetDeviceObject;
	
	protected boolean autoReplyInSlaveMode = true;
	protected String websocketLink = "ws://192.81.135.31/renhai/websocket";
	
	protected MockAppConsts.MockAppBehaviorMode behaviorMode = MockAppConsts.MockAppBehaviorMode.Slave; 
	protected MockAppConsts.MockAppBusinessStatus businessStatus = MockAppConsts.MockAppBusinessStatus.Invalid;
	
	protected int chatCount = 0;
	protected boolean useRealSocket;
	
	public MockAppConsts.MockAppBusinessStatus getBusinessStatus()
	{
		return businessStatus;
	}
	
	
	
	public void setWebsocketLink(String link)
	{
		websocketLink = link;
	}
	
	public String getWebsocketLink()
	{
		return websocketLink; 
	}
	
	public String getDeviceSn()
	{
		return deviceSn;
	}
	
	public String getOSVersion()
	{
		return osVersion;
	}

	public void setOSVersion(String oSVersion)
	{
		osVersion = oSVersion;
	}

	public String getAppVersion()
	{
		return AppVersion;
	}

	public void setAppVersion(String appVersion)
	{
		appVersion = appVersion;
	}

	public String getLocation()
	{
		return Location;
	}

	public void setLocation(String location)
	{
		location = location;
	}

	public String getDeviceModel()
	{
		return DeviceModel;
	}

	public void setDeviceModel(String deviceModel)
	{
		deviceModel = deviceModel;
	}

    public void clearLastReceivedCommand()
	{
		lastReceivedCommand = null;
	}
    
    public JSONObject getLastReceivedCommand()
    {
    	return lastReceivedCommand;
    }
    
    
    public boolean checkLastNotification(Consts.MessageId messageId, 
    		Consts.NotificationType notifyType)
    {
    	if (lastReceivedCommand == null)
    	{
    		return false;
    	}
    	
    	JSONObject header = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope)
    			.getJSONObject(JSONKey.Header);
    	JSONObject body = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope)
    			.getJSONObject(JSONKey.Body);
    	
    	if (messageId != null)
    	{
	    	int receivedMessageId = header.getIntValue(JSONKey.MessageId);
	    	if (receivedMessageId != messageId.getValue())
	    	{
	    		logger.error("MessageId verification of device <{}> failed, expected: " 
	    				+ messageId.name() + "("+ messageId.getValue() +"), received: " + receivedMessageId, deviceSn);
	    		
	    		return false;
	    	}
    	}
    	
    	if (notifyType != null)
    	{
	    	int receivedOperationType = body.getIntValue(JSONKey.OperationType);
	    	if (receivedOperationType != notifyType.getValue())
	    	{
	    		logger.error("OperationType verification of device <{}> failed, expected: " 
	    				+ notifyType.name() + "("+ notifyType.getValue() +"), received: " + receivedOperationType, deviceSn);
	    		
	    		return false;
	    	}
    	}
    	return true;
    }
    
    public boolean checkLastResponse(Consts.MessageId messageId, 
    		Consts.OperationType operationType)
    {
    	if (lastReceivedCommand == null)
    	{
    		return false;
    	}
    	
    	JSONObject header = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope)
    			.getJSONObject(JSONKey.Header);
    	JSONObject body = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope)
    			.getJSONObject(JSONKey.Body);
    	
    	if (messageId != null)
    	{
	    	int receivedMessageId = header.getIntValue(JSONKey.MessageId);
	    	if (receivedMessageId != messageId.getValue())
	    	{
	    		logger.error("MessageId verification of device <{}> failed, expected: " 
	    				+ messageId.name() + "("+ messageId.getValue() +"), received: " + receivedMessageId, deviceSn);
	    		return false;
	    	}
    	}
    	
		String receivedMessageSn = header.getString(JSONKey.MessageSn);
    	if (!receivedMessageSn.equals(lastSentMessageSn))
    	{
    		logger.error("MessageSn verification of device <{}> failed, expected: " 
    				+ lastSentMessageSn + ", received: " + receivedMessageSn, deviceSn);
    		return false;
    	}
    	
    	if (operationType != null)
    	{
	    	int receivedOperationType = body.getIntValue(JSONKey.OperationType);
	    	if (receivedOperationType != operationType.getValue())
	    	{
	    		logger.error("OperationType verification of device <{}> failed, expected: " 
	    				+ operationType.name() + "("+ operationType.getValue() +"), received: " + receivedOperationType, deviceSn);
	    		return false;
	    	}
    	}
    	return true;
    }
    
    public boolean lastReceivedCommandIsError()
    {
    	JSONObject header = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope)
    			.getJSONObject(JSONKey.Header);
    	int messageId = header.getIntValue(JSONKey.MessageId);
    	return (messageId == Consts.MessageId.ServerErrorResponse.getValue());
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
    protected void init(Consts.MessageId messageId)
    {
    	jsonObject.clear();
    	header.clear();
    	body.clear();
    	jsonObject.put(JSONKey.Header, header);
    	jsonObject.put(JSONKey.Body, body);
    	
    	header.put(JSONKey.MessageId, messageId.getValue());
    	header.put(JSONKey.MessageSn, CommonFunctions.getRandomString(GlobalSetting.BusinessSetting.LengthOfMessageSn));
    	header.put(JSONKey.DeviceId, deviceId);
    	header.put(JSONKey.DeviceSn, deviceSn);
    	header.put(JSONKey.TimeStamp, DateUtil.getNow());
    	
    	if (messageId == Consts.MessageId.AlohaRequest
    			|| messageId == Consts.MessageId.AppDataSyncRequest
    			|| messageId == Consts.MessageId.ServerDataSyncRequest
    			|| messageId == Consts.MessageId.BusinessSessionRequest)
    	{
    		header.put(JSONKey.MessageType, Consts.MessageType.AppRequest.getValue());
    	}
    	else
    	{
    		header.put(JSONKey.MessageType, Consts.MessageType.AppResponse.getValue());
    	}
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
			condition.await(15, TimeUnit.SECONDS);
		}
		catch (InterruptedException e)
		{
			FileLogger.printStackTrace(e);
		}
    	lock.unlock();
    }
    
    public void startAutoReply()
    {
    	autoReplyInSlaveMode = true;
    }
    
    public void stopAutoReply()
    {
    	autoReplyInSlaveMode = false;
    }
    
    public MockApp(String deviceSn)
	{
    	this.deviceSn = deviceSn;
    	this.behaviorMode = MockAppConsts.MockAppBehaviorMode.Slave;
    	useRealSocket = false;
    	this.connect(useRealSocket);
	}
    
    public MockApp(String deviceSn, String strBehaviorMode, boolean realSocket)
	{
    	this.deviceSn = deviceSn;
    	MockAppConsts.MockAppBehaviorMode tmpBehaviorMode = MockAppConsts.MockAppBehaviorMode.parseFromStringValue(strBehaviorMode);
    	
    	if (tmpBehaviorMode == MockAppConsts.MockAppBehaviorMode.Invalid)
    	{
    		logger.error("Fatal error that {} is invalid behavior mode, Normal mode will be configurated instead.", strBehaviorMode);
    		tmpBehaviorMode = MockAppConsts.MockAppBehaviorMode.NormalAndQuit;
    	}
    	this.behaviorMode = tmpBehaviorMode;
    	businessStatus = MockAppConsts.MockAppBusinessStatus.Init;
    	this.useRealSocket = realSocket;
    	this.connect(realSocket);
    	if (behaviorMode != MockAppConsts.MockAppBehaviorMode.Slave)
    	{
    		this.prepareSending(null);
    	}
	}
	
	public String getConnectionId()
	{
		return connection.getConnectionId();
	}
	
	public IMockConnection getConnection()
	{
		return connection;
	}
	
	/** */
	@Override
	public void sendAlohaRequest()
	{
		init(Consts.MessageId.AlohaRequest);
		// Add command type
		
		
		// Add command body
		body.put(JSONKey.Content, "Hello Server!");
		
		lastSentMessageSn = header.getString(JSONKey.MessageSn);
		sendRawJSONMessage(jsonObject, true);
	}
	
	/** */
	@Override
	public void sendAppDataSyncRequest(JSONObject queryObj, JSONObject updateObj)
	{
		init(Consts.MessageId.AppDataSyncRequest);
		
		// Add command body
		if (queryObj != null)
		{
			body.put(JSONKey.DataQuery, queryObj);
		}
		
		if (updateObj != null)
		{
			body.put(JSONKey.DataUpdate, updateObj);
		}
		
		lastSentMessageSn = header.getString(JSONKey.MessageSn);
		sendRawJSONMessage(jsonObject, true);
	}
	
	/** */
	@Override
	public void sendServerDataSyncRequest()
	{
		init(Consts.MessageId.ServerDataSyncRequest);
		
		// Add command body
		JSONObject deviceCountObj = new JSONObject();
		deviceCountObj.put(JSONKey.Online, null);
		deviceCountObj.put(JSONKey.Random, null);
		deviceCountObj.put(JSONKey.Interest, null);
		deviceCountObj.put(JSONKey.Chat, null);
		deviceCountObj.put(JSONKey.RandomChat, null);
		deviceCountObj.put(JSONKey.InterestChat, null);
		
		JSONObject deviceCapacityObj = new JSONObject();
		deviceCapacityObj.put(JSONKey.Online, null);
		deviceCapacityObj.put(JSONKey.Random, null);
		deviceCapacityObj.put(JSONKey.Interest, null);
		
		JSONObject interestObj = new JSONObject();
		interestObj.put(JSONKey.Current, 10);
		
		body.put(JSONKey.DeviceCount, deviceCountObj);
		body.put(JSONKey.DeviceCapacity, deviceCapacityObj);
		body.put(JSONKey.InterestLabelList, interestObj);
		
		lastSentMessageSn = header.getString(JSONKey.MessageSn);
		sendRawJSONMessage(jsonObject, true);
	}
	
	@Override
	public void sendRawJSONMessage(String jsonString, boolean syncSend)
	{
		if (!syncSend)
		{
			connection.onTextMessage(jsonString);
			return;
		}
		connection.onTextMessage(jsonString);
	}
	
	private void sendByRealWebsocket(String message)
	{
		try
		{
			int tryCount = 0;
			while (!connection.isOpen() && tryCount < 5)
			{
				Thread.sleep(1000);
				tryCount++;
			}
			
			if (!connection.isOpen())
			{
				logger.error("Fatal error that connection of device <{}> was not opened", deviceSn);
				return;
			}
			connection.sendMessage(message);
		}
		catch (IOException e)
		{
			FileLogger.printStackTrace(e);
		}
		catch (InterruptedException e)
		{
			FileLogger.printStackTrace(e);
		}
	}
	
	@Override
	public void sendRawJSONMessage(JSONObject jsonObject, boolean syncSend)
	{
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		
		String message = JSON.toJSONString(envelopeObj, SerializerFeature.WriteMapNullValue);
		logger.debug("MockApp <{}> send message: \n" + message, deviceSn);
		if (!syncSend)
		{
			if (useRealSocket)
			{
				sendByRealWebsocket(message);
			}
			else
			{
				connection.onTextMessage(message);
			}
			return;
		}
		
		lock.lock();
		clearLastReceivedCommand();
		
		if (useRealSocket)
		{
			sendByRealWebsocket(message);
		}
		else
		{
			connection.onTextMessage(message);
		}
		
		try
		{
			if (lastReceivedCommand == null)
			{
				logger.debug("MockApp <{}> sent message and await for response.", deviceSn);
				condition.await(10, TimeUnit.SECONDS);
				logger.debug("MockApp <{}> recovers from await.", deviceSn);
			}
		}
		catch (InterruptedException e)
		{
			FileLogger.printStackTrace(e);
		}
		lock.unlock();
	}
	
	/** */
	@Override
	public void sendNotificationResponse(
			String messageSn,
			Consts.NotificationType operationType, 
			String operationInfo,
			String operationValue)
	{
		init(Consts.MessageId.BusinessSessionNotificationResponse);
		
		// Add command header
		if (messageSn == null)
		{
			messageSn = CommonFunctions.getRandomString(16);
		}
		header.put(JSONKey.MessageSn, messageSn);
		
		// Add command body
		body.put(JSONKey.BusinessSessionId, CommonFunctions.getJSONValue(businessSessionId));
		body.put(JSONKey.BusinessType, Consts.BusinessType.Interest.getValue());
		body.put(JSONKey.OperationInfo, CommonFunctions.getJSONValue(operationInfo));
		body.put(JSONKey.OperationType, operationType.getValue());
		body.put(JSONKey.OperationValue, null);
		
		sendRawJSONMessage(jsonObject, false);
	}
	
	/** */
	@Override
	public void sendBusinessSessionRequest(Consts.OperationType operationType, 
			JSONObject operationInfoObj,
			String operationValue)
	{
		init(Consts.MessageId.BusinessSessionRequest);
		
		// Add command body
		body.put(JSONKey.BusinessSessionId, CommonFunctions.getJSONValue(businessSessionId));
		body.put(JSONKey.BusinessType, businessType.getValue());
		body.put(JSONKey.OperationType, operationType.getValue());
		
		body.put(JSONKey.OperationInfo, operationInfoObj);
		body.put(JSONKey.OperationValue, operationValue);
		
		lastSentMessageSn = header.getString(JSONKey.MessageSn);
		sendRawJSONMessage(jsonObject, true);
	}
	
	/** */
	@Override
	public void disconnect()
	{
		connection.close();
		//connection.onClose(0);
	}
	
	/** */
	@Override
	public void enterPool(Consts.BusinessType businessType)
	{
		this.businessType = businessType;
		sendBusinessSessionRequest(Consts.OperationType.EnterPool, null, businessType.toString());
	}
	
	@Override
	public void leavePool()
	{
		sendBusinessSessionRequest(Consts.OperationType.LeavePool, null, businessType.toString());
	}
	
	/** */
	@Override
	public void endChat()
	{
		sendBusinessSessionRequest(Consts.OperationType.EndChat, null, "");
	}
	
	/** */
	@Override
	public void chatConfirm(boolean agree)
	{
		if (agree)
		{
			sendBusinessSessionRequest(Consts.OperationType.AgreeChat, null, "");
		}
		else
		{
			sendBusinessSessionRequest(Consts.OperationType.RejectChat, null, "");
		}
	}
	
	/** */
	@Override
	public void ping()
	{
		if (connection == null)
		{
			return;
		}
		connection.ping();
	}
	
	/** */
	private void assess(String impressLabelList, boolean continueFlag)
	{
		if (impressLabelList == null || impressLabelList.length() == 0)
		{
			return;
		}
		
		JSONArray assessObj = targetDeviceObject.getJSONObject(JSONKey.Device)
				.getJSONObject(JSONKey.Profile)
				.getJSONObject(JSONKey.ImpressCard)
				.getJSONArray(JSONKey.AssessLabelList);
		
		JSONArray impressObj = targetDeviceObject.getJSONObject(JSONKey.Device)
				.getJSONObject(JSONKey.Profile)
				.getJSONObject(JSONKey.ImpressCard)
				.getJSONArray(JSONKey.ImpressLabelList);
		
		assessObj.clear();
		impressObj.clear();
		String[] labelArray =  impressLabelList.split(",");
		for (String label : labelArray)
		{
			if (Consts.SolidAssessLabel.isSolidAssessLabel(label))
			{
				assessObj.add(label);
			}
			else
			{
				impressObj.add(label);
			}
		}
		
		if (continueFlag)
		{
			sendBusinessSessionRequest(Consts.OperationType.AssessAndContinue, targetDeviceObject, "");
		}
		else
		{
			sendBusinessSessionRequest(Consts.OperationType.AssessAndQuit, targetDeviceObject, "");
		}
	}
	
	@Override
	public void onJSONCommand(JSONObject obj)
	{
		lastReceivedCommand = obj;
		lock.lock();
		condition.signal();
		lock.unlock();
		logger.debug("MockApp <{}> received command: \n" + JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue), deviceSn);
		
		int intMessageId = 0;
		JSONObject header = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header);
		JSONObject body = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
		if (header.containsKey(JSONKey.MessageId))
		{
			intMessageId = header.getIntValue(JSONKey.MessageId);
		}
		
		Consts.MessageId messageId = Consts.MessageId.parseValue(intMessageId);
		if (messageId == Consts.MessageId.AppDataSyncResponse)
		{
			deviceId = body.getJSONObject(JSONKey.DataQuery)
					.getJSONObject(JSONKey.Device)
					.getIntValue(JSONKey.DeviceId);
		}
		else if (messageId == Consts.MessageId.BusinessSessionNotification)
		{
			int messageType = body.getIntValue(JSONKey.OperationType);
			if (messageType == Consts.NotificationType.SessionBinded.getValue())
			{
				targetDeviceObject = JSON.parseObject(body.getString(JSONKey.OperationInfo));
			}
		}
		
		if (this.behaviorMode == MockAppConsts.MockAppBehaviorMode.Slave)
		{
			// Check if it's notification, and response if it is
			if (autoReplyInSlaveMode)
			{
				if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					AutoReplyTask task = new AutoReplyTask(
							MockAppRequest.BusinessSessionNotificationResponse, 
							lastReceivedCommand, 
							this,
							0,
							MockAppConsts.MockAppBusinessStatus.Init);
					task.start();
				}
			}
			return;
			
		}
		prepareSending(messageId);
	}
	
	private void replyNotification(JSONObject lastReceivedCommand, MockAppBusinessStatus nextStatus)
	{
		AutoReplyTask task = new AutoReplyTask(
				MockAppRequest.BusinessSessionNotificationResponse, 
				lastReceivedCommand, 
				this, 0,
				nextStatus);
		task.start();
	}
	
	private void prepareSending(Consts.MessageId messageId)
	{
		AutoReplyTask task = null;
		if (messageId != null)
		{
			logger.debug("Device<"+ deviceSn + "> received " + messageId.name() + " in status of " + businessStatus.name());
		}
		switch(businessStatus)
		{
			case Init:
				if (behaviorMode.ordinal() > MockAppConsts.MockAppBehaviorMode.NoAppSyncRequest.ordinal())
				{
					task = new AutoReplyTask(MockAppRequest.AppDataSyncRequest, null, this, 0, MockAppConsts.MockAppBusinessStatus.AppDataSyncReqSent);
					task.setName("AppDataSyncRequestThread");
				}
				break;
				
			case AppDataSyncReqSent:
				if (messageId == Consts.MessageId.AppDataSyncResponse)
				{
					if (behaviorMode.ordinal() > MockAppConsts.MockAppBehaviorMode.NoEnterPoolRequest.ordinal())
					{
						task = new AutoReplyTask(
								MockAppRequest.EnterPool, 
								lastReceivedCommand, 
								this, 
								0, 
								MockAppConsts.MockAppBusinessStatus.EnterPoolReqSent);
						task.setName("EnterPoolRequestThread");
					}
				}
				break;
				
			case EnterPoolReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					setBusinessStatus(MockAppConsts.MockAppBusinessStatus.EnterPoolResReceived);
				}
				else
				{
					logger.error("Received {} in status of EnterPoolReqSent", messageId.name());
				}
				break;
			
			case EnterPoolResReceived:
				if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					int receivedOperationType = body.getIntValue(JSONKey.OperationType);
			    	if (receivedOperationType != Consts.NotificationType.SessionBinded.getValue())
			    	{
			    		logger.error("Device <" + deviceSn + "> received {} in status of EnterPoolResReceived", messageId.name());
			    	}
			    	else
			    	{
			    		replyNotification(lastReceivedCommand, MockAppBusinessStatus.SessionBoundedReceived);
			    		if (behaviorMode == MockAppConsts.MockAppBehaviorMode.RejectChat)
						{
							AutoReplyTask chatConfirmTask = new AutoReplyTask(
									MockAppRequest.RejectChat, 
									null, 
									this, MockAppConsts.Setting.ChatConfirmDuration,
									MockAppConsts.MockAppBusinessStatus.Ended);
							chatConfirmTask.setName("AgreeChatThread");
							chatConfirmTask.start();
						}
						else if (behaviorMode == MockAppConsts.MockAppBehaviorMode.ConnectLossDuringChatConfirm)
						{
							setBusinessStatus(MockAppConsts.MockAppBusinessStatus.Ended);
						}
						else if (behaviorMode.ordinal() >= MockAppConsts.MockAppBehaviorMode.ConnectLossDuringChatConfirm.ordinal())
						{
							AutoReplyTask chatConfirmTask = new AutoReplyTask(
									MockAppRequest.AgreeChat, 
									null, 
									this, MockAppConsts.Setting.ChatConfirmDuration,
									MockAppConsts.MockAppBusinessStatus.AgreeChatReqSent);
							chatConfirmTask.setName("AgreeChatThread");
							chatConfirmTask.start();
						}
			    	}
				}
				else
				{
					logger.error("Device <" + deviceSn + "> received {} in status of EnterPoolResReceived", messageId.name());
				}
				break;
			case SessionBoundedReplied:
				if (messageId != Consts.MessageId.BusinessSessionNotification)
				{
					logger.error("Device <" + deviceSn + "> received {} in status of SessionBoundedReplied", messageId.name());
				}
				break;
			case AgreeChatReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					JSONObject body = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
					JSONObject header = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header);
					if (body.getInteger(JSONKey.OperationType) == Consts.NotificationType.OthersideRejected.getValue())
					{
						setBusinessStatus(MockAppConsts.MockAppBusinessStatus.EnterPoolResReceived);
					}
					else if (body.getInteger(JSONKey.OperationType) == Consts.NotificationType.OthersideAgreed.getValue())
					{
						if (behaviorMode ==  MockAppConsts.MockAppBehaviorMode.ConnectLossDuringChat)
						{
							try
							{
								Thread.sleep(MockAppConsts.Setting.VideoChatDuration);
							}
							catch (InterruptedException e)
							{
								FileLogger.printStackTrace(e);
							}
							setBusinessStatus(MockAppConsts.MockAppBusinessStatus.Ended);
						}
						else if (behaviorMode.ordinal() >= MockAppConsts.MockAppBehaviorMode.NoRequestOfAssess.ordinal())
						{
							task = new AutoReplyTask(
									MockAppRequest.EndChat, 
									null, 
									this, MockAppConsts.Setting.VideoChatDuration,
									MockAppConsts.MockAppBusinessStatus.EndChatReqSent);
							task.setName("EndChatRequestThread");
						}
					}
				}
				else if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					replyNotification(lastReceivedCommand, null);
				}
				else
				{
					logger.error("Received {} in status of AgreeChatReqSent", messageId.name());
				}
				break;
			
			case EndChatReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					if (behaviorMode.ordinal() >=  MockAppConsts.MockAppBehaviorMode.NoRequestOfAssess.ordinal())
					{
						if (behaviorMode ==  MockAppConsts.MockAppBehaviorMode.NormalAndContinue)
						{
							task = new AutoReplyTask(
									MockAppRequest.AssessAndContinue, 
									null, 
									this, MockAppConsts.Setting.AssessDuration,
									MockAppConsts.MockAppBusinessStatus.AssessReqSent);
						}
						else
						{
							task = new AutoReplyTask(
									MockAppRequest.AssessAndQuit, 
									null, 
									this, MockAppConsts.Setting.AssessDuration,
									MockAppConsts.MockAppBusinessStatus.AssessReqSent);
						}
						task.setName("AssessAndContinueThread");
					}
				}
				else if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					replyNotification(lastReceivedCommand, null);
				}
				else
				{
					logger.error("Device <" + deviceSn + "> received {} in status of AssessReqSent", messageId.name());
				}
				break;
				
			case AssessReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					if (behaviorMode ==  MockAppConsts.MockAppBehaviorMode.NormalAndContinue)
					{
						chatCount++;
						if (chatCount > MockAppConsts.Setting.MaxChatCount)
						{
							setBusinessStatus(MockAppConsts.MockAppBusinessStatus.Ended);
							return;
						}
						else
						{
							setBusinessStatus(MockAppConsts.MockAppBusinessStatus.EnterPoolResReceived);
						}
						
					}
					else if (behaviorMode ==  MockAppConsts.MockAppBehaviorMode.NormalAndQuit)
					{
						setBusinessStatus(MockAppConsts.MockAppBusinessStatus.Ended);
						return;
					}
				}
				else if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					replyNotification(lastReceivedCommand, null);
				}
				else
				{
					logger.error("Device <" + deviceSn + "> received {} in status of AssessReqSent", messageId.name());
				}
				break;
		}
		
		if (task != null)
		{
			task.start();
		}
	}
	
	/**
	 * Mock request of AppDataSyncRequest
	 */
	public void syncDevice()
	{
		JSONObject queryObj = new JSONObject();
		JSONObject updateObj = new JSONObject();
		JSONObject deviceObj = new JSONObject();
		JSONObject deviceCardObj = new JSONObject();
		JSONObject profileObj = new JSONObject();
		JSONObject interestCardObj = new JSONObject();
		
		queryObj.put(JSONKey.Device, null);
		
		updateObj.put(JSONKey.Device, deviceObj);
		
		deviceObj.put(JSONKey.DeviceCard, deviceCardObj);
		deviceObj.put(JSONKey.Profile, profileObj);
		
		profileObj.put(JSONKey.InterestCard, interestCardObj);
		
		deviceObj.put(JSONKey.DeviceSn, CommonFunctions.getJSONValue(deviceSn));
		
		deviceCardObj.put(JSONKey.OsVersion, CommonFunctions.getJSONValue(getOSVersion()));
		deviceCardObj.put(JSONKey.AppVersion, CommonFunctions.getJSONValue(getAppVersion()));
		deviceCardObj.put(JSONKey.IsJailed, Consts.YesNo.No.getValue());
		deviceCardObj.put(JSONKey.Location, CommonFunctions.getJSONValue(getLocation()));
		deviceCardObj.put(JSONKey.DeviceSn, CommonFunctions.getJSONValue(deviceSn));
		deviceCardObj.put(JSONKey.DeviceModel, CommonFunctions.getJSONValue(getDeviceModel()));
		
		JSONArray interestLabelArray = new JSONArray();
		interestCardObj.put(JSONKey.InterestLabelList, interestLabelArray);
		
		JSONObject interestLabelObj = new JSONObject();
		interestLabelObj.put(JSONKey.InterestLabelName, "“Ù¿÷");
		interestLabelObj.put(JSONKey.LabelOrder, System.currentTimeMillis() % 5);
		interestLabelArray.add(interestLabelObj);
		
		interestLabelObj = new JSONObject();
		interestLabelObj.put(JSONKey.InterestLabelName, "ø¥µÁ”∞");
		interestLabelObj.put(JSONKey.LabelOrder, System.currentTimeMillis() % 5);
		interestLabelArray.add(interestLabelObj);
		
		interestLabelObj = new JSONObject();
		interestLabelObj.put(JSONKey.InterestLabelName, "privateInterestOf " + deviceSn);
		interestLabelObj.put(JSONKey.LabelOrder, System.currentTimeMillis() % 5);
		interestLabelArray.add(interestLabelObj);
		
		sendAppDataSyncRequest(queryObj, updateObj);
	}

	@Override
	public void assessAndQuit(String impressLabelList)
	{
		assess(impressLabelList, false);
	}

	@Override
	public void assessAndContinue(String impressLabelList)
	{
		assess(impressLabelList, true);
	}

	@Override
	public void connect(boolean realSocket)
	{
		if (realSocket)
		{
			URI uri = URI.create(websocketLink);
			
			Draft d = new Draft_17();
			MockWebSocketClient conn = new MockWebSocketClient(uri);
			
			Thread t = new Thread(conn);
			t.start();
			connection = conn;
		}
		else
		{
			MockWebSocketConnection conn = new MockWebSocketConnection();
			connection = conn;
			OnlineDevicePool.instance.newDevice(conn);
		}
		connection.bindMockApp(this);
		
		startTimer();
	}
	
	public void setBusinessStatus(MockAppConsts.MockAppBusinessStatus status)
	{
		logger.debug("Device <{}> changes status to " + status.name(), deviceSn);
		businessStatus = status;
		if (status == MockAppConsts.MockAppBusinessStatus.Ended)
		{
			connection.close();
		}
	}



	@Override
	public void run()
	{
		logger.debug("MockApp <{}> started", deviceSn);
		while (this.businessStatus != MockAppConsts.MockAppBusinessStatus.Ended)
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		logger.debug("MockApp <{}> ended", deviceSn);
	}
}

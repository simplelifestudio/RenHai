/**
 * AbstractMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.scripting.xmltags.SetSqlNode;
import org.java_websocket.drafts.Draft_17;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.business.pool.AbstractMsgExecutorPool;
import com.simplelife.renhai.server.business.pool.InputMsgExecutorPool;
import com.simplelife.renhai.server.business.pool.MessageHandler;
import com.simplelife.renhai.server.business.pool.OnlineDevicePool;
import com.simplelife.renhai.server.json.JSONFactory;
import com.simplelife.renhai.server.json.ServerJSONMessage;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.test.MockAppConsts.MockAppBehaviorMode;
import com.simplelife.renhai.server.test.MockAppConsts.MockAppBusinessStatus;
import com.simplelife.renhai.server.test.MockAppConsts.MockAppRequest;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.BusinessType;
import com.simplelife.renhai.server.util.Consts.NotificationType;
import com.simplelife.renhai.server.util.DateUtil;
import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IDeviceWrapper;
import com.simplelife.renhai.server.util.IMockApp;
import com.simplelife.renhai.server.util.IMockConnection;
import com.simplelife.renhai.server.util.JSONKey;
import com.simplelife.renhai.server.websocket.SecurityUtils;

/** */
public class MockApp implements IMockApp, Runnable
{
	public final static AbstractMsgExecutorPool mockAppExecutePool = new AbstractMsgExecutorPool()
	{
		@Override
		public void startService()
		{
			executeThreadPool = Executors.newFixedThreadPool(1500);
		}
	};
	
	private class MonitorTask extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				sendServerDataSyncRequest();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	
	private class PingTask extends TimerTask
	{
		public PingTask()
		{
		}
		
		@Override
		public void run()
		{
			Thread.currentThread().setName("MockPing" + DateUtil.getCurrentMiliseconds());
			try
			{
				ping();
			}
			catch(Exception e)
			{
				FileLogger.printStackTrace(e);
			}
		}
	}
	
	protected class AutoReplyTask implements Runnable
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
			Thread.currentThread().setName(messageIdToBeSent.name() + DateUtil.getCurrentMiliseconds());
			if (delay > 0)
			{
				try
				{
					logger.debug("Sleep " + delay + " ms, next status: " + nextStatus + ", message to be sent: " + messageIdToBeSent);
					Thread.sleep(delay);
					
					if (app.getBusinessStatus() == MockAppBusinessStatus.Ended)
					{
						logger.debug("Recover from sleep but app is ended, sending of " + messageIdToBeSent.name() + " is cancelled");
						return;
					}
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
				case ChooseBusiness:
					app.chooseBusiness(BusinessType.Interest);
					break;
				case UnchooseBusiness:
					app.unchooseBusiness();
					break;
				case MatchStart:
					app.matchStart();
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
					app.assessAndContinue("^#Happy#^,By" + deviceSn + ",A-" + DateUtil.getCurrentMiliseconds());
					break;
				case AssessAndQuit:
					app.assessAndQuit("^#SoSo#^,By" + deviceSn + ",A-" + DateUtil.getCurrentMiliseconds());
					break;
				case BusinessSessionNotificationResponse:
					JSONObject body = receivedMessage.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
					JSONObject header = receivedMessage.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header);
					Consts.NotificationType operationType = Consts.NotificationType.parseValue(body.getIntValue(JSONKey.OperationType));
					String messageSn = header.getString(JSONKey.MessageSn);
					logger.debug("MockApp <{}> The messageSn in last command before response:" + messageSn, app.getDeviceSn());
					app.sendNotificationResponse(messageSn, operationType, "", "1");
					break;
				case ServerDataSyncRequest:
					app.sendServerDataSyncRequest();
					break;
				case SessionUnbind:
					app.sessionUnbind();
					break;
				case ChatMessage:
					app.chatMessage();
					break;
				default:
					logger.error("Unknown message id");
			}
		}
	}
	
	public final static String OSVersion = "MockOS";
	public final static String AppVersion = "0.1";
	public final static String Location = "22.511962,113.380301";
	public final static String DeviceModel = "MockDevice";
	
	protected IMockConnection connection;
	protected Logger logger = LoggerFactory.getLogger(MockApp.class);

    //protected int sessionId;
    protected Timer pingTimer = new Timer();
    protected Timer monitorTimer;
    
    protected String lastSentMessageSn;
    
    protected String peerDeviceId;
    protected String businessSessionId;
    protected Consts.BusinessType businessType;
    protected JSONObject lastReceivedCommand;
    
	protected String deviceSn;
	protected int deviceId;
	protected String osVersion = MockApp.OSVersion;
	protected String appVersion = MockApp.AppVersion;
	protected String location = MockApp.Location;
	protected String deviceModel = MockApp.DeviceModel;
	
	protected JSONObject targetDeviceObject;
	
	protected boolean autoReplyInManualMode = true;
	protected String websocketLink = "ws://192.81.135.31/renhai/websocket";
	
	protected MockAppConsts.MockAppBehaviorMode behaviorMode = MockAppConsts.MockAppBehaviorMode.Manual; 
	protected MockAppConsts.MockAppBusinessStatus businessStatus = MockAppConsts.MockAppBusinessStatus.Invalid;
	
	protected int chatCount = 0;
	protected int maxChatCount = 3;
	protected boolean isUsingRealSocket;
	
	private MockMessageHandler inputMessageHandler = new MockMessageHandler(this, MockApp.mockAppExecutePool);
	
	protected int maxOnlineDeviceCount = 0;
	
	protected String interestLabels;
	
	public boolean isUseRealSocket()
	{
		return isUsingRealSocket;
	}
	
	public void setBusinessType(Consts.BusinessType businessType)
	{
		this.businessType = businessType; 
	}
	
	public Consts.BusinessType getBusinessType()
	{
		return businessType;
	}
	
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
		this.osVersion = oSVersion;
	}

	public String getAppVersion()
	{
		return AppVersion;
	}

	public void setAppVersion(String appVersion)
	{
		this.appVersion = appVersion;
	}

	public String getLocation()
	{
		return Location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getDeviceModel()
	{
		return DeviceModel;
	}

	public void setDeviceModel(String deviceModel)
	{
		this.deviceModel = deviceModel;
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
	    		logger.error("MessageId verification of MockApp <{}> failed, expected: " 
	    				+ messageId.name() + "("+ messageId.getValue() +"), received: " + receivedMessageId, deviceSn);
	    		
	    		return false;
	    	}
    	}
    	
    	if (notifyType != null)
    	{
	    	int receivedOperationType = body.getIntValue(JSONKey.OperationType);
	    	if (receivedOperationType != notifyType.getValue())
	    	{
	    		logger.error("OperationType verification of MockApp <{}> failed, expected: " 
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
	    		logger.error("MessageId verification of MockApp <{}> failed, expected: " 
	    				+ messageId.name() + "("+ messageId.getValue() +"), received: " + receivedMessageId, deviceSn);
	    		return false;
	    	}
    	}
    	
		String receivedMessageSn = header.getString(JSONKey.MessageSn);
    	if (!receivedMessageSn.equals(lastSentMessageSn))
    	{
    		logger.error("MessageSn verification of MockApp <{}> failed, expected: " 
    				+ lastSentMessageSn + ", received: " + receivedMessageSn, deviceSn);
    		return false;
    	}
    	
    	if (operationType != null)
    	{
	    	int receivedOperationType = body.getIntValue(JSONKey.OperationType);
	    	if (receivedOperationType != operationType.getValue())
	    	{
	    		logger.error("OperationType verification of MockApp <{}> failed, expected: " 
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
    	this.pingTimer.scheduleAtFixedRate(new PingTask(), GlobalSetting.TimeOut.PingInterval, GlobalSetting.TimeOut.PingInterval);
    	
    	if (behaviorMode == MockAppBehaviorMode.Monitor)
    	{
    		monitorTimer = new Timer();
    		monitorTimer.scheduleAtFixedRate(new MonitorTask(), 1000, 1000);
    	}
    }
    
    public void stopTimer()
    {
    	if (behaviorMode == MockAppBehaviorMode.Monitor)
    	{
    		if (monitorTimer != null)
    		{
    			monitorTimer.cancel();
    		}
    	}
    	this.pingTimer.cancel();
    	logger.debug("Ping timer of MockApp <{}> was stopped", deviceSn);
    }
    
    /**
     * Clear jsonMap and add default fields
     */
    protected JSONObject createMessage(Consts.MessageId messageId)
    {
    	JSONObject jsonObject = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();
    	
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
    	return jsonObject;
    }
    
    public void waitMessage()
    {
    	if (lastReceivedCommand != null)
    	{
    		return;
    	}
    	
    	/*
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
    	*/
    }
    
    public void startAutoReply()
    {
    	autoReplyInManualMode = true;
    }
    
    public void stopAutoReply()
    {
    	autoReplyInManualMode = false;
    }
    
    public MockApp(String deviceSn)
	{
    	this(deviceSn, MockAppConsts.MockAppBehaviorMode.Manual.getValue());
	}
    
    public MockApp(String deviceSn, String strBehaviorMode)
	{
    	this(deviceSn, strBehaviorMode, null);
	}
    
    public MockApp(String deviceSn, String strBehaviorMode, String serverLink)
    {
    	this(deviceSn, strBehaviorMode, serverLink, null);
    }
    
    public MockApp(String deviceSn, String strBehaviorMode, String serverLink, String interestLabels)
    {
    	this(deviceSn, strBehaviorMode, serverLink, interestLabels, MockAppConsts.Setting.MaxChatCount);
    }
    
    public MockApp(String deviceSn, String strBehaviorMode, String serverLink, String interestLabels, int maxChatCount)
    {
    	this.interestLabels = interestLabels;
    	this.deviceSn = deviceSn;
    	this.maxChatCount = maxChatCount;

    	MockAppConsts.MockAppBehaviorMode tmpBehaviorMode = MockAppConsts.MockAppBehaviorMode.parseFromStringValue(strBehaviorMode);
    	
    	if (tmpBehaviorMode == MockAppConsts.MockAppBehaviorMode.Invalid)
    	{
    		logger.error("Fatal error that {} is invalid behavior mode, Normal mode will be configurated instead.", strBehaviorMode);
    		tmpBehaviorMode = MockAppConsts.MockAppBehaviorMode.NormalAndQuit;
    	}
    	
    	this.behaviorMode = tmpBehaviorMode;
    	this.setWebsocketLink(serverLink);
    	if (serverLink == null)
    	{
    		this.connect(false);
    	}
    	else
    	{
    		this.connect(true);
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
		JSONObject jsonObject = createMessage(Consts.MessageId.AlohaRequest);
		JSONObject body = jsonObject.getJSONObject(JSONKey.Body);
		JSONObject header = jsonObject.getJSONObject(JSONKey.Header);
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
		JSONObject jsonObject = createMessage(Consts.MessageId.AppDataSyncRequest);
		JSONObject body = jsonObject.getJSONObject(JSONKey.Body);
		JSONObject header = jsonObject.getJSONObject(JSONKey.Header);
		
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
		JSONObject jsonObject = createMessage(Consts.MessageId.ServerDataSyncRequest);
		JSONObject body = jsonObject.getJSONObject(JSONKey.Body);
		JSONObject header = jsonObject.getJSONObject(JSONKey.Header);
		
		// Add command body
		JSONObject deviceCountObj = new JSONObject();
		//deviceCountObj.put(JSONKey.Online, null);
		//deviceCountObj.put(JSONKey.Random, null);
		//deviceCountObj.put(JSONKey.Interest, null);
		//deviceCountObj.put(JSONKey.Chat, null);
		//deviceCountObj.put(JSONKey.RandomChat, null);
		//deviceCountObj.put(JSONKey.InterestChat, null);
		
		JSONObject deviceCapacityObj = new JSONObject();
		//deviceCapacityObj.put(JSONKey.Online, null);
		//deviceCapacityObj.put(JSONKey.Random, null);
		//deviceCapacityObj.put(JSONKey.Interest, null);
		
		JSONObject interestObj = new JSONObject();
		interestObj.put(JSONKey.Current, 10);
		
		body.put(JSONKey.DeviceCount, deviceCountObj);
		body.put(JSONKey.DeviceCapacity, deviceCapacityObj);
		body.put(JSONKey.InterestLabelList, interestObj);
		//body.put(JSONKey.DeviceSummary, null);
		//body.put(JSONKey.DeviceDetailedInfo, this.getDeviceSn());
		
		lastSentMessageSn = header.getString(JSONKey.MessageSn);
		sendRawJSONMessage(jsonObject, true);
	}
	
	@Override
	public void sendRawJSONMessage(JSONObject obj, boolean syncSend)
	{
		JSONObject envelopeObj = new JSONObject();
		
		String messageSn = obj.getJSONObject(JSONKey.Header)
				.getString(JSONKey.MessageSn);
		
		if (GlobalSetting.BusinessSetting.Encrypt == 0)
		{
			envelopeObj.put(JSONKey.JsonEnvelope, obj);
		}
		else
		{
			String message = JSON.toJSONString(obj);
			try
			{
				message = SecurityUtils.encryptByDESAndEncodeByBase64(message, GlobalSetting.BusinessSetting.EncryptKey);
			}
			catch (Exception e)
			{
				FileLogger.printStackTrace(e);
			}
			envelopeObj.put(JSONKey.JsonEnvelope, message);
		}
		logger.debug("MockApp <" + deviceSn + "> sends message: \n{}", JSON.toJSONString(envelopeObj, true));
		
		//String message = JSON.toJSONString(envelopeObj, SerializerFeature.WriteMapNullValue);
		
		if (syncSend)
		{
			JSONObject response = connection.syncSendToServer(envelopeObj, messageSn);
			
			if (response == null)
			{
				String tmpStr = envelopeObj.getString(JSONKey.JsonEnvelope);
				try
				{
					tmpStr = SecurityUtils.decryptByDESAndDecodeByBase64(tmpStr, GlobalSetting.BusinessSetting.EncryptKey);
				}
				catch (Exception e)
				{
					FileLogger.printStackTrace(e);
				}
				logger.error("MockApp <" + deviceSn + ">: server has no response for message\n{}", tmpStr);
				setBusinessStatus(MockAppBusinessStatus.Ended);
				return;
			}
			else
			{
				this.onJSONCommand(response);
			}
		}
		else
		{
			connection.asyncSendToServer(envelopeObj);
		}
	}
	
	/** */
	@Override
	public void sendNotificationResponse(
			String messageSn,
			Consts.NotificationType operationType, 
			String operationInfo,
			String operationValue)
	{
		JSONObject jsonObject = createMessage(Consts.MessageId.BusinessSessionNotificationResponse);
		JSONObject body = jsonObject.getJSONObject(JSONKey.Body);
		JSONObject header = jsonObject.getJSONObject(JSONKey.Header);
		
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
		
		if (operationType == NotificationType.OthersideLost)
		{
			endBusiness();
			//setBusinessStatus(MockAppBusinessStatus.Ended);
		}
	}
	
	/** */
	@Override
	public void sendBusinessSessionRequest(Consts.OperationType operationType, 
			JSONObject operationInfoObj,
			String operationValue)
	{
		JSONObject jsonObject = createMessage(Consts.MessageId.BusinessSessionRequest);
		JSONObject body = jsonObject.getJSONObject(JSONKey.Body);
		JSONObject header = jsonObject.getJSONObject(JSONKey.Header);
		
		// Add command body
		body.put(JSONKey.BusinessSessionId, CommonFunctions.getJSONValue(businessSessionId));
		
		if (businessType != null)
		{
			body.put(JSONKey.BusinessType, businessType.getValue());
		}
		else
		{
			body.put(JSONKey.BusinessType, null);
		}
		
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
		connection.closeConnection();
		//connection.onClose(0);
	}
	
	/** */
	@Override
	public void chooseBusiness(Consts.BusinessType businessType)
	{
		this.businessType = businessType;
		sendBusinessSessionRequest(Consts.OperationType.ChooseBusiness, null, businessType.toString());
	}
	
	@Override
	public void matchStart()
	{
		sendBusinessSessionRequest(Consts.OperationType.MatchStart, null, "");
	}
	
	@Override
	public void sessionUnbind()
	{
		sendBusinessSessionRequest(Consts.OperationType.SessionUnbind, null, "");
	}
	
	@Override
	public void unchooseBusiness()
	{
		if (businessType != null)
		{
			sendBusinessSessionRequest(Consts.OperationType.UnchooseBusiness, null, businessType.toString());
		}
		else
		{
			sendBusinessSessionRequest(Consts.OperationType.UnchooseBusiness, null, null);
		}
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
		logger.debug("Start ping of device <{}>", this.getDeviceSn());
		connection.ping();
	}
	
	@Override
	public void chatMessage()
	{
		JSONObject operationInfoObj = new JSONObject();
		String temp = "这是" + getDeviceSn() + "发来的消息：" + CommonFunctions.getRandomString(10);
		operationInfoObj.put(JSONKey.ChatMessage, temp);
		sendBusinessSessionRequest(Consts.OperationType.ChatMessage, operationInfoObj, "");
	}
	
	/** */
	private void assess(String impressLabelList, boolean continueFlag)
	{
		if (impressLabelList == null || impressLabelList.length() == 0)
		{
			return;
		}
		
		if (targetDeviceObject == null)
		{
			logger.error("Fatal error that targetDeviceObject is null when try to assess it");
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
			if (Consts.SolidImpressLabel.isSolidImpressLabel(label))
			{
				JSONObject obj = new JSONObject();
				obj.put(JSONKey.ImpressLabelName, label);
				assessObj.add(obj);
			}
			else
			{
				JSONObject obj = new JSONObject();
				obj.put(JSONKey.ImpressLabelName, label);
				impressObj.add(obj);
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
	public synchronized void onJSONCommand(JSONObject obj)
	{
		lastReceivedCommand = obj;
		this.inputMessageHandler.addMessage(obj);
	}
	
	public void execute(JSONObject obj)
	{
		if (obj == null)
		{
			return;
		}
		else
		{
			logger.debug("MockApp <"+ deviceSn +"> received command: \n{}", JSON.toJSONString(obj, true));
		}
		
		int intMessageId = 0;
		JSONObject header = null;
		JSONObject body = null;
		JSONObject envObj = null;
		envObj = obj.getJSONObject(JSONKey.JsonEnvelope);
		
		header = envObj.getJSONObject(JSONKey.Header);
		body = envObj.getJSONObject(JSONKey.Body);
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
		else if (messageId == Consts.MessageId.ServerDataSyncResponse)
		{
			if (behaviorMode == MockAppBehaviorMode.Monitor)
			{
				try
				{
					saveMonitorData(body);
				}
				catch(Exception e)
				{
					FileLogger.printStackTrace(e);
				}
			}
			return;
		}
		else if (messageId == Consts.MessageId.BusinessSessionNotification)
		{
			int messageType = body.getIntValue(JSONKey.OperationType);
			if (messageType == Consts.NotificationType.SessionBound.getValue())
			{
				targetDeviceObject = JSON.parseObject(body.getString(JSONKey.OperationInfo));
				businessSessionId = body.getString(JSONKey.BusinessSessionId);
			}
		}
		
		if (this.behaviorMode == MockAppConsts.MockAppBehaviorMode.Manual)
		{
			// Check if it's notification, and response if it is
			if (autoReplyInManualMode)
			{
				if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					AutoReplyTask task = new AutoReplyTask(
							MockAppRequest.BusinessSessionNotificationResponse, 
							obj, 
							this,
							500,
							null);
					MockApp.mockAppExecutePool.execute(task);
				}
			}
			return;
			
		}
		prepareSending(messageId, obj);
	}
	
	private void saveMonitorData(JSONObject body)
	{
		JSONObject deviceCountObj = body.getJSONObject(JSONKey.DeviceCount);
		int chat = deviceCountObj.getIntValue(JSONKey.Chat);
		int interest = deviceCountObj.getIntValue(JSONKey.Interest);
		int online = deviceCountObj.getIntValue(JSONKey.Online);
		
		if (maxOnlineDeviceCount < online)
		{
			maxOnlineDeviceCount = online;
		}
		
		try
		{
			FileWriter fw = new FileWriter("./monitor.txt", true);
			fw.write(DateUtil.getNow() + "\t" + online + "\t" + interest + "\t" + chat + "\t" + (interest - chat) + "\r\n");
			fw.close();
		}
		catch (IOException e)
		{
			FileLogger.printStackTrace(e);
		}
		
		if (online == 1 && maxOnlineDeviceCount > 1)
		{
			this.setBusinessStatus(MockAppBusinessStatus.Ended);
		}
	}
	
	private void replyNotification(JSONObject lastReceivedCommand, MockAppBusinessStatus nextStatus)
	{
		logger.debug("Received message before start task of reply: " + lastReceivedCommand.toJSONString());
		AutoReplyTask task = new AutoReplyTask(
				MockAppRequest.BusinessSessionNotificationResponse, 
				lastReceivedCommand, 
				this, 0,
				nextStatus);
		//task.setName("NotificationReply" + DateUtil.getCurrentMiliseconds());
		MockApp.mockAppExecutePool.execute(task);
	}
	
	private boolean endBusiness()
	{
		if (behaviorMode !=  MockAppConsts.MockAppBehaviorMode.NormalAndContinue)
		{
			setBusinessStatus(MockAppConsts.MockAppBusinessStatus.Ended);
			return true;
		}

		chatCount++;
		if (chatCount >= maxChatCount)
		{
			setBusinessStatus(MockAppConsts.MockAppBusinessStatus.Ended);
			return true;
		}
		else
		{
			setBusinessStatus(MockAppConsts.MockAppBusinessStatus.EnterPoolResReceived);
			return false;
		}
	}
	
	private int getRandomVideoChatDuration()
	{
		/*
		Random random = new Random();
		int result = random.nextInt(300);
		return result + 30;
		*/
		int index = Math.abs(this.businessSessionId.hashCode()) % MockAppConsts.chatDuration.length;
		return MockAppConsts.chatDuration[index];
	}
	
	private void prepareSending(Consts.MessageId messageId, JSONObject obj)
	{
		int intOperationType = 0;
		Consts.NotificationType receivedOperationType = null;
		
		if (obj != null)
		{
			intOperationType = obj.getJSONObject(JSONKey.JsonEnvelope)
					.getJSONObject(JSONKey.Body)
					.getIntValue(JSONKey.OperationType);
			receivedOperationType = Consts.NotificationType.parseValue(intOperationType);
		}
		
		AutoReplyTask task = null;
		if (messageId != null)
		{
			logger.debug("MockApp <"+ deviceSn + "> received " + messageId.name() + " in status of " + businessStatus.name());
		}
		
		if (messageId == Consts.MessageId.BusinessSessionNotification)
		{
			logger.debug("MockApp <"+ deviceSn + "> received notification: " + receivedOperationType.name());
			if (receivedOperationType == Consts.NotificationType.OthersideAgreed
					|| receivedOperationType == Consts.NotificationType.OthersideEndChat
					|| receivedOperationType == Consts.NotificationType.OthersideChatMessage)
			{
				replyNotification(obj, null);
				return;
			}
			else if (receivedOperationType == Consts.NotificationType.OthersideRejected
					|| receivedOperationType == Consts.NotificationType.OthersideLost)
			{
				//endBusiness();
				task = new AutoReplyTask(
						MockAppRequest.SessionUnbind, 
						obj, 
						this, 
						MockAppConsts.Setting.ChatConfirmDuration, 
						MockAppConsts.MockAppBusinessStatus.SessionUnbindReqSent);
				//task.setName("SessionUnbindReqSent" + DateUtil.getCurrentMiliseconds());
				replyNotification(obj, null);
				return;
			}
		}
		
		switch(businessStatus)
		{
			case Init:
				if (behaviorMode.ordinal() > MockAppConsts.MockAppBehaviorMode.NoAppSyncRequest.ordinal())
				{
					task = new AutoReplyTask(MockAppRequest.AppDataSyncRequest, null, this, 0, MockAppConsts.MockAppBusinessStatus.AppDataSyncReqSent);
					//task.setName("AppDataSyncRequest" + DateUtil.getCurrentMiliseconds());
				}
				break;
				
			case AppDataSyncReqSent:
				if (messageId == Consts.MessageId.AppDataSyncResponse)
				{
					if (behaviorMode.ordinal() > MockAppConsts.MockAppBehaviorMode.NoEnterPoolRequest.ordinal())
					{
						task = new AutoReplyTask(
								MockAppRequest.ChooseBusiness, 
								obj, 
								this, 
								300, 
								MockAppConsts.MockAppBusinessStatus.EnterPoolReqSent);
						//task.setName("EnterPoolRequest" + DateUtil.getCurrentMiliseconds());
					}
				}
				break;
				
			case EnterPoolReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					setBusinessStatus(MockAppConsts.MockAppBusinessStatus.EnterPoolResReceived);
					
					task = new AutoReplyTask(
							MockAppRequest.MatchStart, 
							obj, 
							this, 
							MockAppConsts.Setting.ChatConfirmDuration,
							MockAppConsts.MockAppBusinessStatus.MatchStartReqSent);
					//task.setName("MatchStart" + DateUtil.getCurrentMiliseconds());
				}
				else
				{
					logger.error("Received {} in status of EnterPoolReqSent", messageId.name());
				}
				break;
			case MatchStartReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					setBusinessStatus(MockAppConsts.MockAppBusinessStatus.MatchStartResReceived);
				}
				else
				{
					logger.error("Received {} in status of MatchStartReqSent", messageId.name());
				}
				break;
				
			case MatchStartResReceived:
				if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					if (receivedOperationType != Consts.NotificationType.SessionBound)
			    	{
			    		logger.error("MockApp <" + deviceSn + "> received {} in status of MatchStartResReceived", receivedOperationType.name());
			    	}
			    	else
			    	{
			    		setBusinessStatus(MockAppBusinessStatus.SessionBoundReceived);
			    		replyNotification(obj, MockAppBusinessStatus.SessionBoundReplied);
			    		if (behaviorMode == MockAppConsts.MockAppBehaviorMode.RejectChat)
						{
							AutoReplyTask chatConfirmTask = new AutoReplyTask(
									MockAppRequest.RejectChat,
									null, 
									this, MockAppConsts.Setting.ChatConfirmDuration,
									MockAppConsts.MockAppBusinessStatus.Ended);
							//chatConfirmTask.setName("AgreeChatThread" + DateUtil.getCurrentMiliseconds());
							//chatConfirmTask.start();
							MockApp.mockAppExecutePool.execute(chatConfirmTask);
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
							//chatConfirmTask.setName("AgreeChatThread");
							//chatConfirmTask.start();
							MockApp.mockAppExecutePool.execute(chatConfirmTask);
						}
			    	}
				}
				else
				{
					logger.error("MockApp <" + deviceSn + "> received {} in status of MatchStartResReceived", messageId.name());
				}
				break;

			case SessionBoundReplied:
				if (messageId != Consts.MessageId.BusinessSessionNotification)
				{
					logger.error("MockApp <" + deviceSn + "> received {} in status of SessionBoundReplied", messageId.name());
				}
				break;
				
			case AgreeChatReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					if (behaviorMode ==  MockAppConsts.MockAppBehaviorMode.ConnectLossDuringChat)
					{
						try
						{
							Thread.sleep(getRandomVideoChatDuration());
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
								MockAppRequest.ChatMessage, 
								null, 
								this,
								5000,
								MockAppConsts.MockAppBusinessStatus.ChatMessageReqSent);
					}
				}
				else if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					JSONObject body = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
					//lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header);
					if (body.getInteger(JSONKey.OperationType) == Consts.NotificationType.OthersideRejected.getValue())
					{
						//setBusinessStatus(MockAppConsts.MockAppBusinessStatus.EnterPoolResReceived);
						endBusiness();
					}
					replyNotification(obj, null);
				}
				else
				{
					logger.error("Received {} in status of AgreeChatReqSent", messageId.name());
				}
				break;
			
			case ChatMessageReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					int chatDuration = getRandomVideoChatDuration();
					task = new AutoReplyTask(
							MockAppRequest.EndChat, 
							null, 
							this,
							chatDuration,
							MockAppConsts.MockAppBusinessStatus.EndChatReqSent);
				}
				else if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					JSONObject body = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
					if (body.getInteger(JSONKey.OperationType) == Consts.NotificationType.OthersideRejected.getValue())
					{
						endBusiness();
					}
					replyNotification(obj, null);
				}
				else
				{
					logger.error("Received {} in status of ChatMessageReqSent", messageId.name());
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
						//task.setName("AssessAndQuit" + DateUtil.getCurrentMiliseconds());
					}
				}
				else if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					replyNotification(obj, null);
				}
				else
				{
					logger.error("MockApp <" + deviceSn + "> received {} in status of AssessReqSent", messageId.name());
				}
				break;
				
			case AssessReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					if (!endBusiness())
					{
						task = new AutoReplyTask(
								MockAppRequest.MatchStart, 
								obj, 
								this, 
								MockAppConsts.Setting.ChatConfirmDuration,
								MockAppConsts.MockAppBusinessStatus.MatchStartReqSent);
						//task.setName("MatchStart" + DateUtil.getCurrentMiliseconds());
					}
					
				}
				else if (messageId == Consts.MessageId.BusinessSessionNotification)
				{
					replyNotification(obj, null);
				}
				else
				{
					logger.error("MockApp <" + deviceSn + "> received {} in status of AssessReqSent", messageId.name());
				}
				break;
				
			case SessionUnbindReqSent:
				if (messageId == Consts.MessageId.BusinessSessionResponse)
				{
					endBusiness();
				}
				else
				{
					logger.error("MockApp <" + deviceSn + "> received {} in status of SessionUnbindReqSent", messageId.name());
				}
				break;
			default:
				break;
		}
		
		if (task != null)
		{
			MockApp.mockAppExecutePool.execute(task);
		}
	}
	
	public void syncDevice(List<String> interestLabels)
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
		
		for (String label : interestLabels)
		{
			JSONObject interestLabelObj = new JSONObject();
			interestLabelObj.put(JSONKey.InterestLabelName, label);
			interestLabelObj.put(JSONKey.LabelOrder, System.currentTimeMillis() % 5);
			interestLabelArray.add(interestLabelObj);
			
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				FileLogger.printStackTrace(e);
			}
		}
		
		sendAppDataSyncRequest(queryObj, updateObj);
	}
	
	/**
	 * Mock request of AppDataSyncRequest
	 */
	public void syncDevice()
	{
		List<String> labels = new ArrayList<String>();;
		if (interestLabels == null)
		{
			labels.add("音乐");
			labels.add("看电影");
			labels.add(this.getDeviceSn());
			
			if (System.currentTimeMillis() % 2 == 0)
			{
				labels.add(Consts.SolidInterestLabel.RenHai.getValue());
			}
		}
		else
		{
			String[] labelArray = interestLabels.split(",");
			for (int i = 0; i < labelArray.length; i++)
			{
				labels.add(labelArray[i]);
			}
		}
		syncDevice(labels);
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

	private boolean setupConnection(boolean useRealSocket)
	{
		if (!useRealSocket)
		{
			MockWebSocketConnection conn = new MockWebSocketConnection();
			connection = conn;
			OnlineDevicePool.instance.newDevice(conn);
			connection.bindMockApp(this);
			return true;
		}
		
		URI uri = URI.create(websocketLink);
		
		new Draft_17();
		RenHaiWebSocketClient conn = new RenHaiWebSocketClient(uri);
		
		Thread t = new Thread(conn);
		t.start();
		connection = conn;
		
		int count = 0;
    	while (count < 10)
    	{
    		if (!connection.isOpen())
    		{
    			try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
    		}
    		else
    		{
    			break;
    		}
    		count++;
    	}
    	
    	if (this.connection.isOpen())
    	{
    		connection.bindMockApp(this);
    		return true;
    	}
    	else
    	{
    		return false;
    	}
	}
	
	public void connect()
	{
		setBusinessStatus(MockAppBusinessStatus.Init);
		if (!setupConnection(isUsingRealSocket))
		{
			logger.error("Fatal error: MockApp <{}> failed to setup websocket connection with server", deviceSn);
			setBusinessStatus(MockAppBusinessStatus.Ended);
			return;
		}
		startTimer();
		if (behaviorMode != MockAppConsts.MockAppBehaviorMode.Manual)
    	{
    		this.prepareSending(null, null);
    	}
	}
	
	@Override
	public void connect(boolean realSocket)
	{
		isUsingRealSocket = realSocket;
		connect();
	}
	
	public void setBusinessStatus(MockAppConsts.MockAppBusinessStatus status)
	{
		logger.debug("MockApp <{}> will chang status to " + status.name(), deviceSn);
		businessStatus = status;
		if (status == MockAppConsts.MockAppBusinessStatus.Ended)
		{
			stopTimer();
			if (connection != null)
			{
				connection.closeConnection();
			}
		}
		logger.debug("MockApp <{}> changed status to " + status.name(), deviceSn);
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


	@Override
	public void sendRawJSONMessage(String jsonString, boolean syncSend)
	{
		JSONObject obj = JSON.parseObject(jsonString);
		//JSONObject envelopeObj = new JSONObject();
		//envelopeObj.put(JSONKey.JsonEnvelope, obj);
		
		//String message = JSON.toJSONString(envelopeObj, SerializerFeature.WriteMapNullValue);
		//logger.debug("MockApp <{}> sends message: \n" + message, deviceSn);
		this.sendRawJSONMessage(obj, syncSend);
	}
	
	public void onClose()
	{
		logger.debug("Quit MockApp <{}> due to WebSocket connection was closed by server", deviceSn);
		setBusinessStatus(MockAppBusinessStatus.Ended);
	}
}

/**
 * SlaveMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;


import java.nio.ByteBuffer;
import java.util.HashMap;

import org.junit.internal.runners.TestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class LocalMockApp extends AbstractMockApp
{
	Logger logger = LoggerFactory.getLogger(LocalMockApp.class);
	/** */
	protected MockWebSocketConnection connection;
	
	public LocalMockApp(MockWebSocketConnection connection)
	{
		this.connection = connection;
		connection.bindMockApp(this);
	}
	
	public String getConnectionId()
	{
		return connection.getConnectionId();
	}
	
	public MockWebSocketConnection getConnection()
	{
		return connection;
	}
	
	@Override
	public void assessAndQuit(String impressLabelList)
	{
		if (impressLabelList == null || impressLabelList.length() == 0)
		{
			return;
		}
		
		sendBusinessSessionRequest(Consts.OperationType.AssessAndQuit, "", impressLabelList);
	}
	
	/** */
	@Override
	public void sendAlohaRequest()
	{
		init();
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.AlohaRequest.toString());
		
		// Add command body
		body.put(JSONKey.Content, "Hello Server!");
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		connection.onTextMessage(envelopeObj.toJSONString());
	}
	
	/** */
	@Override
	public void sendAppDataSyncRequest(JSONObject queryObj, JSONObject updateObj)
	{
		init();
		
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		
		// Add command body
		if (queryObj != null)
		{
			body.put(JSONKey.DataQuery, queryObj);
		}
		
		if (updateObj != null)
		{
			body.put(JSONKey.DataUpdate, updateObj);
		}
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		connection.onTextMessage(envelopeObj.toJSONString());
	}
	
	/** */
	@Override
	public void sendServerDataSyncRequest()
	{
		init();
		
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.toString());
		
		// Add command body
		body.put(JSONKey.Online, "");
		body.put(JSONKey.Interest, "");
		body.put(JSONKey.Chat, "");
		body.put(JSONKey.Interest, "");
		body.put(JSONKey.RandomChat, "");
		body.put(JSONKey.InterestChat, "");
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		connection.onTextMessage(envelopeObj.toJSONString());
	}
	
	@Override
	public void sendRawJSONMessage(String jsonString)
	{
		connection.onTextMessage(jsonString);
	}
	
	@Override
	public void sendRawJSONMessage(JSONObject jsonObject)
	{
		connection.onTextMessage(jsonObject.toJSONString());
	}
	
	/** */
	@Override
	public void sendNotificationResponse(
			Consts.NotificationType notificationType, 
			String operationInfo,
			String operationValue)
	{
		init();
		
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.BusinessSessionNotificationResponse.toString());
		
		String messageSn = null;
		if (this.lastReceivedCommand != null)
		{
			if (lastReceivedCommand.containsKey(JSONKey.JsonEnvelope))
			{
				messageSn = lastReceivedCommand.getJSONObject(JSONKey.JsonEnvelope)
						.getJSONObject(JSONKey.Header)
						.getString(JSONKey.MessageSn);
			}
			else
			{
				logger.error("Invalid lastReceivedCommand: " + lastReceivedCommand.toJSONString());
			}
		}
		
		if (messageSn != null)
		{
			header.put(JSONKey.MessageSn, messageSn);
		}
		
		// Add command body
		body.put(JSONKey.BusinessSessionId, businessSessionId);
		body.put(JSONKey.BusinessType, Consts.BusinessType.Interest);
		body.put(JSONKey.OperationInfo, operationInfo);
		body.put(JSONKey.OperationType, notificationType);
		body.put(JSONKey.OperationValue, "");
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		connection.onTextMessage(envelopeObj.toJSONString());
	}
	
	/** */
	@Override
	public void sendBusinessSessionRequest(Consts.OperationType operationType, String operationInfo,
			String operationValue)
	{
		init();
		
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.BusinessSessionRequest.toString());
		
		// Add command body
		body.put(JSONKey.BusinessSessionId, businessSessionId);
		body.put(JSONKey.BusinessType, businessType.toString());
		body.put(JSONKey.OperationType, operationType.toString());
		body.put(JSONKey.OperationInfo, operationInfo);
		body.put(JSONKey.OperationValue, operationValue);
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		connection.onTextMessage(envelopeObj.toJSONString());
	}
	
	/** */
	@Override
	public void close()
	{
		connection.onClose();
	}
	
	/** */
	@Override
	public void enterPool(Consts.BusinessType businessType)
	{
		this.businessType = businessType;
		sendBusinessSessionRequest(Consts.OperationType.EnterPool, "", businessType.name());
	}
	
	/** */
	@Override
	public void endChat()
	{
		sendBusinessSessionRequest(Consts.OperationType.EndChat, "", "");
	}
	
	/** */
	@Override
	public void chatConfirm(boolean agree)
	{
		if (agree)
		{
			sendBusinessSessionRequest(Consts.OperationType.AgreeChat, "", "");
		}
		else
		{
			sendBusinessSessionRequest(Consts.OperationType.RejectChat, "", "");
		}
	}
	
	/** */
	@Override
	public void ping()
	{
		ByteBuffer pingData = ByteBuffer.allocate(5);
		connection.onPing(pingData);
	}
	
	/** */
	@Override
	public void assess(String impressLabelList)
	{
		if (impressLabelList == null || impressLabelList.length() == 0)
		{
			return;
		}
		
		sendBusinessSessionRequest(Consts.OperationType.AssessAndContinue, "", impressLabelList);
	}
	
	@Override
	public void onJSONCommand(JSONObject obj)
	{
		lastReceivedCommand = obj;
		logger.debug("App received command: \n{}", obj.toJSONString());
	}
	
	public void startPingTimer()
	{
		
	}
	
	/**
	 * Mock request of AppDataSyncRequest
	 */
	protected void syncDevice()
	{
		JSONObject updateObj = new JSONObject();
		JSONObject deviceObj = new JSONObject();
		JSONObject deviceCardObj = new JSONObject();
		JSONObject profileObj = new JSONObject();
		JSONObject interestCardObj = new JSONObject();
		
		updateObj.put(JSONKey.Device, deviceObj);
		
		deviceObj.put(JSONKey.DeviceCard, deviceCardObj);
		deviceObj.put(JSONKey.Profile, profileObj);
		
		profileObj.put(JSONKey.InterestCard, interestCardObj);
		
		deviceObj.put(JSONKey.DeviceSn, this.deviceWrapper.getDeviceSn());
		deviceCardObj.put(JSONKey.OsVersion, this.getOSVersion());
		deviceCardObj.put(JSONKey.AppVersion, this.getAppVersion());
		deviceCardObj.put(JSONKey.IsJailed, Consts.YesNo.No.toString());
		deviceCardObj.put(JSONKey.Location, this.getLocation());
		deviceCardObj.put(JSONKey.DeviceSn, this.getDeviceWrapper().getDeviceSn());
		deviceCardObj.put(JSONKey.DeviceModel, this.getDeviceModel());
		
		sendAppDataSyncRequest(null, updateObj);
	}

}

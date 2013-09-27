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
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simplelife.renhai.server.util.CommonFunctions;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;
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
	
	/** */
	@Override
	public void sendAlohaRequest()
	{
		init();
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.AlohaRequest.getValue());
		
		// Add command body
		body.put(JSONKey.Content, "Hello Server!");
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		sendRawJSONMessage(envelopeObj, true);
	}
	
	/** */
	@Override
	public void sendAppDataSyncRequest(JSONObject queryObj, JSONObject updateObj)
	{
		init();
		
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.getValue());
		
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
		sendRawJSONMessage(envelopeObj, true);
	}
	
	/** */
	@Override
	public void sendServerDataSyncRequest()
	{
		init();
		
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.ServerDataSyncRequest.getValue());
		
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
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		sendRawJSONMessage(envelopeObj, true);
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
	
	@Override
	public void sendRawJSONMessage(JSONObject jsonObject, boolean syncSend)
	{
		String message = JSON.toJSONString(jsonObject, SerializerFeature.WriteMapNullValue);
		logger.debug("Device <{}> send message: \n" + message, getDeviceWrapper().getDeviceSn());
		if (!syncSend)
		{
			connection.onTextMessage(message);
			return;
		}
		
		lock.lock();
		clearLastReceivedCommand();
		connection.onTextMessage(message);
		try
		{
			if (lastReceivedCommand == null)
			{
				logger.debug("Device <{}> sent message and await for response.", getDeviceWrapper().getDeviceSn());
				condition.await(10, TimeUnit.SECONDS);
				logger.debug("Device <{}> recovers from await.", getDeviceWrapper().getDeviceSn());
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		lock.unlock();
	}
	
	/** */
	@Override
	public void sendNotificationResponse(
			Consts.NotificationType operationType, 
			String operationInfo,
			String operationValue)
	{
		init();
		
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.BusinessSessionNotificationResponse.getValue());
		
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
		body.put(JSONKey.BusinessSessionId, CommonFunctions.getJSONValue(businessSessionId));
		body.put(JSONKey.BusinessType, Consts.BusinessType.Interest.getValue());
		body.put(JSONKey.OperationInfo, CommonFunctions.getJSONValue(operationInfo));
		body.put(JSONKey.OperationType, operationType.getValue());
		body.put(JSONKey.OperationValue, null);
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		sendRawJSONMessage(envelopeObj, false);
	}
	
	/** */
	@Override
	public void sendBusinessSessionRequest(Consts.OperationType operationType, String operationInfo,
			String operationValue)
	{
		init();
		
		// Add command type
		header.put(JSONKey.MessageId, Consts.MessageId.BusinessSessionRequest.getValue());
		
		// Add command body
		body.put(JSONKey.BusinessSessionId, CommonFunctions.getJSONValue(businessSessionId));
		body.put(JSONKey.BusinessType, businessType.getValue());
		body.put(JSONKey.OperationType, operationType.getValue());
		
		body.put(JSONKey.OperationInfo, CommonFunctions.getJSONValue(operationInfo));
		body.put(JSONKey.OperationValue, CommonFunctions.getJSONValue(operationValue));
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonObject);
		sendRawJSONMessage(envelopeObj, true);
	}
	
	/** */
	@Override
	public void close()
	{
		connection.onClose(0);
	}
	
	/** */
	@Override
	public void enterPool(Consts.BusinessType businessType)
	{
		this.businessType = businessType;
		sendBusinessSessionRequest(Consts.OperationType.EnterPool, "", businessType.toString());
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
		if (connection == null)
		{
			return;
		}
		
		ByteBuffer pingData = ByteBuffer.allocate(5);
		connection.onPing(pingData);
	}
	
	/** */
	private void assess(IDeviceWrapper targetDevice, String impressLabelList, boolean continueFlag)
	{
		if (impressLabelList == null || impressLabelList.length() == 0)
		{
			return;
		}
		
		JSONObject obj = targetDevice.toJSONObject();
		JSONArray assessObj = obj.getJSONObject(JSONKey.Device)
				.getJSONObject(JSONKey.Profile)
				.getJSONObject(JSONKey.ImpressCard)
				.getJSONArray(JSONKey.AssessLabelList);
		
		JSONArray impressObj = obj.getJSONObject(JSONKey.Device)
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
			sendBusinessSessionRequest(Consts.OperationType.AssessAndContinue, "", obj.toJSONString());
		}
		else
		{
			sendBusinessSessionRequest(Consts.OperationType.AssessAndQuit, "", obj.toJSONString());
		}
	}
	
	@Override
	public void onJSONCommand(JSONObject obj)
	{
		lastReceivedCommand = obj;
		lock.lock();
		condition.signal();
		lock.unlock();
		logger.debug("Device<{}> received command: \n" + obj.toJSONString(), getDeviceWrapper().getDeviceSn());
		
		// Check if it's notification, and respose if it is
		JSONObject header = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Header);
		if (header.containsKey(JSONKey.MessageId))
		{
			int messageId = header.getIntValue(JSONKey.MessageId);
			if (messageId == Consts.MessageId.BusinessSessionNotification.getValue())
			{
				logger.debug("Mock app received BusinessSessionNotification, trying to response");
				JSONObject body = obj.getJSONObject(JSONKey.JsonEnvelope).getJSONObject(JSONKey.Body);
				Consts.NotificationType operationType = Consts.NotificationType.parseValue(body.getIntValue(JSONKey.OperationType)); 
				this.sendNotificationResponse(operationType, "", "1");
			}
		}
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
		
		deviceObj.put(JSONKey.DeviceSn, CommonFunctions.getJSONValue(deviceWrapper.getDeviceSn()));
		deviceCardObj.put(JSONKey.OsVersion, CommonFunctions.getJSONValue(getOSVersion()));
		deviceCardObj.put(JSONKey.AppVersion, CommonFunctions.getJSONValue(getAppVersion()));
		deviceCardObj.put(JSONKey.IsJailed, Consts.YesNo.No.getValue());
		deviceCardObj.put(JSONKey.Location, CommonFunctions.getJSONValue(getLocation()));
		deviceCardObj.put(JSONKey.DeviceSn, CommonFunctions.getJSONValue(getDeviceWrapper().getDeviceSn()));
		deviceCardObj.put(JSONKey.DeviceModel, CommonFunctions.getJSONValue(getDeviceModel()));
		
		sendAppDataSyncRequest(null, updateObj);
	}

	@Override
	public void assessAndQuit(IDeviceWrapper targetDevice, String impressLabelList)
	{
		assess(targetDevice, impressLabelList, false);
	}

	@Override
	public void assessAndContinue(IDeviceWrapper targetDevice, String impressLabelList)
	{
		assess(targetDevice, impressLabelList, true);
	}

}

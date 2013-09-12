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

import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.db.Devicecard;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class LocalMockApp extends AbstractMockApp
{
	/** */
	protected MockWebSocketConnection connection;
	
	
	public LocalMockApp(MockWebSocketConnection connection)
	{
		this.connection = connection;
	}
	
	@Override
	public void assessAndQuit(String impressLabelList)
	{
		if (impressLabelList == null || impressLabelList.length() == 0)
		{
			return;
		}
		
		sendBusinessSessionRequest(Consts.OperationType.AssessAndQuit, impressLabelList);
	}
	
	/** */
	@Override
	public void sendAlohaRequest()
	{
		// Add command type
		jsonMapHeader.put(JSONKey.MessageId, Consts.MessageId.AlohaRequest.name());
		
		// Add command body
		jsonMapBody.put(JSONKey.Content, "Hello Server!");
		
		// Send
		connection.onTextMessage(JSONObject.toJSONString(jsonMap));
	}
	
	/** */
	@Override
	public void sendAppDataSyncRequest(HashMap<String, Object> queryMap, HashMap<String, Object> updateMap)
	{
		// Add command type
		jsonMapHeader.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.name());
		
		// Add command body
		if (queryMap != null)
		{
			jsonMapBody.put(JSONKey.DataQuery, queryMap);
		}
		
		if (updateMap != null)
		{
			jsonMapBody.put(JSONKey.DataUpdate, updateMap);
		}
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.JsonEnvelope, jsonMap);
		
		// Send
		connection.onTextMessage(envelopeObj.toJSONString());
	}
	
	/** */
	@Override
	public void sendServerDataSyncRequest()
	{
		// Add command type
		jsonMapHeader.put(JSONKey.MessageId, Consts.MessageId.AppDataSyncRequest.name());
		
		// Add command body
		jsonMapBody.put(JSONKey.Online, "");
		jsonMapBody.put(JSONKey.Interest, "");
		jsonMapBody.put(JSONKey.Chat, "");
		jsonMapBody.put(JSONKey.Interest, "");
		jsonMapBody.put(JSONKey.RandomChat, "");
		jsonMapBody.put(JSONKey.InterestChat, "");
		
		// Send
		connection.onTextMessage(JSONObject.toJSONString(jsonMap));
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
	public void sendNotificationResponse()
	{
		// Add command type
		jsonMapHeader.put(JSONKey.MessageId, Consts.MessageId.BusinessSessionNotificationResponse.name());
		
		// Add command body
		jsonMapBody.put(JSONKey.BusinessSessionId, businessSessionId);
		jsonMapBody.put(JSONKey.BusinessType, Consts.BusinessType.Interest);
		jsonMapBody.put(JSONKey.OperationType, Consts.OperationType.Received);
		jsonMapBody.put(JSONKey.OperationValue, "");
		
		// Send
		connection.onTextMessage(JSONObject.toJSONString(jsonMap));
	}
	
	/** */
	@Override
	public void sendBusinessSessionRequest(Consts.OperationType operationType, String operationValue)
	{
		// Add command type
		jsonMapHeader.put(JSONKey.MessageId, Consts.MessageId.BusinessSessionRequest.name());
		
		// Add command body
		jsonMapBody.put(JSONKey.BusinessSessionId, businessSessionId);
		jsonMapBody.put(JSONKey.BusinessType, businessType);
		jsonMapBody.put(JSONKey.OperationType, operationType.name());
		jsonMapBody.put(JSONKey.OperationValue, operationValue);
		
		// Send
		connection.onTextMessage(JSONObject.toJSONString(jsonMap));
	}
	
	/** */
	@Override
	public void close()
	{
		connection.onClose();
	}
	
	/** */
	@Override
	public void enterPool(Consts.BusinessType poolType)
	{
		sendBusinessSessionRequest(Consts.OperationType.EnterPool, poolType.name());
	}
	
	/** */
	@Override
	public void endChat()
	{
		sendBusinessSessionRequest(Consts.OperationType.EndChat, "");
	}
	
	/** */
	@Override
	public void chatConfirm(boolean agree)
	{
		if (agree)
		{
			sendBusinessSessionRequest(Consts.OperationType.AgreeChat, "");
		}
		else
		{
			sendBusinessSessionRequest(Consts.OperationType.RejectChat, "");
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
		
		sendBusinessSessionRequest(Consts.OperationType.AssessAndContinue, impressLabelList);
	}
	
	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IMockApp#updateInterestcard(java.lang.String)
	 */
	
	@Override
	public void updateInterestcard(HashMap<String, Object> interestLabels)
	{
		HashMap<String, Object> updateMap = new HashMap<String, Object>();
		updateMap.put(JSONKey.DeviceCard, getDeviceJSONMap());
		updateMap.put(JSONKey.InterestCard, interestLabels);
		sendAppDataSyncRequest(null, updateMap);
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IMockApp#onJSONCommand()
	 */
	@Override
	public void onJSONCommand(JSONObject obj)
	{
		lastReceivedCommand = obj;
		FileLogger.info(obj.toJSONString());
	}
	
	public void startPingTimer()
	{
		
	}
	
	/**
	 * Mock request of AppDataSyncRequest
	 */
	protected void syncDevice()
	{
		HashMap<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put(JSONKey.DeviceCard, new HashMap<String, Object>());
		
		HashMap<String, Object> impressCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.ImpressLabelList, "10");
		queryMap.put(JSONKey.ImpressCard, impressCardMap);
		
		HashMap<String, Object> interestCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.InterestLabelList, "5");
		queryMap.put(JSONKey.InterestCard, interestCardMap);
		
		HashMap<String, Object> updateMap = new HashMap<String, Object>();
		
		HashMap<String, Object> deviceCardMap = new HashMap<String, Object>();
		Devicecard card = new Devicecard();
		
		deviceCardMap.put(JSONKey.OsVersion, "iOS 6.1.2");
		deviceCardMap.put(JSONKey.AppVersion, "1.2");
		deviceCardMap.put(JSONKey.IsJailed, "No");
		deviceCardMap.put(JSONKey.Location, "22.511962,113.380301");
		deviceCardMap.put(JSONKey.DeviceSn, "AFLNWERJL3203598FDLGSLDF");
		deviceCardMap.put(JSONKey.DeviceModel, "iPhone5");
		updateMap.put(JSONKey.DeviceCard, deviceCardMap);
		
		sendAppDataSyncRequest(queryMap, updateMap);
	}
}

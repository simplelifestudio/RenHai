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
		jsonMapHeader.put(JSONKey.FieldName.MessageId, JSONKey.MessageId.AlohaRequest);
		
		// Add command body
		jsonMapBody.put(JSONKey.FieldName.Content, "Hello Server!");
		
		// Send
		connection.onTextMessage(JSONObject.toJSONString(jsonMap));
	}
	
	/** */
	@Override
	public void sendAppDataSyncRequest(HashMap<String, Object> queryMap, HashMap<String, Object> updateMap)
	{
		// Add command type
		jsonMapHeader.put(JSONKey.FieldName.MessageId, JSONKey.MessageId.AppDataSyncRequest);
		
		// Add command body
		if (queryMap != null)
		{
			jsonMapBody.put(JSONKey.FieldName.DataQuery, queryMap);
		}
		
		if (updateMap != null)
		{
			jsonMapBody.put(JSONKey.FieldName.DataUpdate, updateMap);
		}
		
		JSONObject envelopeObj = new JSONObject();
		envelopeObj.put(JSONKey.FieldName.JsonEnvelope, jsonMap);
		
		// Send
		connection.onTextMessage(envelopeObj.toJSONString());
	}
	
	/** */
	@Override
	public void sendServerDataSyncRequest()
	{
		// Add command type
		jsonMapHeader.put(JSONKey.FieldName.MessageId, JSONKey.MessageId.AppDataSyncRequest);
		
		// Add command body
		jsonMapBody.put(JSONKey.FieldName.OnlineDeviceCount, "");
		jsonMapBody.put(JSONKey.FieldName.InterestDeviceCount, "");
		jsonMapBody.put(JSONKey.FieldName.ChatDeviceCount, "");
		jsonMapBody.put(JSONKey.FieldName.InterestChatDeviceCount, "");
		jsonMapBody.put(JSONKey.FieldName.CurrentHotInterestLabels, "");
		jsonMapBody.put(JSONKey.FieldName.HistoryHotInterestLabels, "");
		
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
		jsonMapHeader.put(JSONKey.FieldName.MessageId, JSONKey.MessageId.BusinessSessionNotificationResponse);
		
		// Add command body
		jsonMapBody.put(JSONKey.FieldName.BusinessSessionId, businessSessionId);
		jsonMapBody.put(JSONKey.FieldName.BusinessType, Consts.BusinessType.Interest);
		jsonMapBody.put(JSONKey.FieldName.OperationType, Consts.OperationType.Received);
		jsonMapBody.put(JSONKey.FieldName.OperationValue, "");
		
		// Send
		connection.onTextMessage(JSONObject.toJSONString(jsonMap));
	}
	
	/** */
	@Override
	public void sendBusinessSessionRequest(Consts.OperationType operationType, String operationValue)
	{
		// Add command type
		jsonMapHeader.put(JSONKey.FieldName.MessageId, JSONKey.MessageId.BusinessSessionRequest);
		
		// Add command body
		jsonMapBody.put(JSONKey.FieldName.BusinessSessionId, businessSessionId);
		jsonMapBody.put(JSONKey.FieldName.BusinessType, businessType);
		jsonMapBody.put(JSONKey.FieldName.OperationType, operationType.name());
		jsonMapBody.put(JSONKey.FieldName.OperationValue, operationValue);
		
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
		
		sendBusinessSessionRequest(Consts.OperationType.Assess, impressLabelList);
	}
	
	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.util.IMockApp#updateInterestcard(java.lang.String)
	 */
	
	@Override
	public void updateInterestcard(HashMap<String, Object> interestLabels)
	{
		HashMap<String, Object> updateMap = new HashMap<String, Object>();
		updateMap.put(JSONKey.FieldName.Devicecard, getDeviceJSONMap());
		updateMap.put(JSONKey.FieldName.Interestcard, interestLabels);
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
		queryMap.put(JSONKey.FieldName.Devicecard, new HashMap<String, Object>());
		
		HashMap<String, Object> impressCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.FieldName.LabelListCount, "10");
		queryMap.put(JSONKey.FieldName.Impresscard, impressCardMap);
		
		HashMap<String, Object> interestCardMap = new HashMap<String, Object>();
		impressCardMap.put(JSONKey.FieldName.LabelListCount, "5");
		queryMap.put(JSONKey.FieldName.Interestcard, interestCardMap);
		
		HashMap<String, Object> updateMap = new HashMap<String, Object>();
		
		HashMap<String, Object> deviceCardMap = new HashMap<String, Object>();
		Devicecard card = new Devicecard();
		
		deviceCardMap.put(JSONKey.FieldName.OsVersion, "iOS 6.1.2");
		deviceCardMap.put(JSONKey.FieldName.AppVersion, "1.2");
		deviceCardMap.put(JSONKey.FieldName.IsJailed, "No");
		deviceCardMap.put(JSONKey.FieldName.Location, "22.511962,113.380301");
		deviceCardMap.put(JSONKey.FieldName.DeviceSn, "AFLNWERJL3203598FDLGSLDF");
		deviceCardMap.put(JSONKey.FieldName.DeviceModel, "iPhone5");
		updateMap.put(JSONKey.FieldName.Devicecard, deviceCardMap);
		
		sendAppDataSyncRequest(queryMap, updateMap);
	}
}

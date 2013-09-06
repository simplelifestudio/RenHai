/**
 * SlaveMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.test;


import java.util.HashMap;
import com.alibaba.fastjson.JSONObject;
import com.simplelife.renhai.server.log.FileLogger;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.JSONKey;


/** */
public class LocalMockApp extends AbstractMockApp
{
	/** */
	protected MockWebSocketConnection connection;
	
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
		
		// Send
		connection.onTextMessage(JSONObject.toJSONString(jsonMap));
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
		connection.onPing();
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
	 * @see com.simplelife.renhai.server.util.IMockApp#updateInterestCard(java.lang.String)
	 */
	
	@Override
	public void updateInterestCard(HashMap<String, Object> interestLabels)
	{
		HashMap<String, Object> updateMap = new HashMap<String, Object>();
		updateMap.put(JSONKey.FieldName.DeviceCard, getDeviceJSONMap());
		updateMap.put(JSONKey.FieldName.InterestCard, interestLabels);
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
}

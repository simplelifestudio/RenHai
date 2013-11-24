/**
 * IMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;


import com.alibaba.fastjson.JSONObject;


/** */
public interface IMockApp
{
	/** */
	public void sendAlohaRequest();
	
	/** */
	public void sendAppDataSyncRequest(JSONObject queryObj, JSONObject updateObj);
	
	/** */
	public void sendServerDataSyncRequest();
	
	/** */
	public void sendRawJSONMessage(String jsonString, boolean syncSend);
	
	public void sendRawJSONMessage(JSONObject jsonObject, boolean syncSend);
	
	/** */
	public void sendNotificationResponse(
			String messageSn,
			Consts.NotificationType notificationType, 
			String operationInfo, 
			String operationValue);
	
	/** */
	public void sendBusinessSessionRequest(
			Consts.OperationType operationType, 
			JSONObject operationInfoObj,
			String operationValue);
	
	public void connect(boolean realSocket);
	
	/** */
	public void disconnect();
	
	/** */
	public void chooseBusiness(Consts.BusinessType poolType);
	
	public void unchooseBusiness();
	
	/** */
	public void endChat();
	
	public void sessionUnbind();
	
	public void matchStart();
	
	/** */
	public void chatConfirm(boolean agree);
	
	/** */
	public void ping();
	
	/** */
	public void assessAndQuit(String impressLabelList);
	
	public void assessAndContinue(String impressLabelList);
	
	/**
	 * @param obj
	 */
	public void onJSONCommand(JSONObject obj);
	
	public void onClose();
	
	public String getDeviceSn();
}

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
			Consts.NotificationType notificationType, 
			String operationInfo, 
			String operationValue);
	
	/** */
	public void sendBusinessSessionRequest(Consts.OperationType operationType, String operationInfo, String operationValue);
	
	/** */
	public void close();
	
	/** */
	public void enterPool(Consts.BusinessType poolType);
	
	/** */
	public void endChat();
	
	/** */
	public void chatConfirm(boolean agree);
	
	/** */
	public void ping();
	
	/** */
	public void assess(String impressLabelList);
	
	public void assessAndQuit(String impressLabelList);
	
	/**
	 * @param obj
	 */
	public void onJSONCommand(JSONObject obj);
	
	public void bindDeviceWrapper(IDeviceWrapper device);
}

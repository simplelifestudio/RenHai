/**
 * IMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;


import java.util.HashMap;
import com.alibaba.fastjson.JSONObject;


/** */
public interface IMockApp
{
	/** */
	public void sendAlohaRequest();
	
	/** */
	public void sendAppDataSyncRequest(HashMap<String, Object> queryMap, HashMap<String, Object> updateMap);
	
	/** */
	public void sendServerDataSyncRequest();
	
	/** */
	public void sendRawJSONMessage(String jsonString);
	
	public void sendRawJSONMessage(JSONObject jsonObject);
	
	/** */
	public void sendNotificationResponse();
	
	/** */
	public void sendBusinessSessionRequest(Consts.OperationType operationType, String operationValue);
	
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
	
	public void updateInterestcard(HashMap<String, Object> interestLabels);

	/**
	 * @param obj
	 */
	public void onJSONCommand(JSONObject obj);
	
	public void bindDeviceWrapper(IDeviceWrapper device);
}

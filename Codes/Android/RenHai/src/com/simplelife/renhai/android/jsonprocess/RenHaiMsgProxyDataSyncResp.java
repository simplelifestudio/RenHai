/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgProxyDataSyncResp.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiInfo;

public class RenHaiMsgProxyDataSyncResp extends RenHaiMsg{
	
	// Message fields definitions
	public static String MSG_PROXYRESP_SERVER = "server";
	public static String MSG_PROXYRESP_ID     = "id";
	public static String MSG_PROXYRESP_STATUS = "status";
	public static String MSG_PROXYRESP_SERVSTAT  = "serviceStatus";
	public static String MSG_PROXYRESP_STATPERD  = "statusPeriod";
	public static String MSG_PROXYRESP_TIMEZONE  = "timeZone";
	public static String MSG_PROXYRESP_BEGINTIME = "beginTime";
	public static String MSG_PROXYRESP_ENDTIME   = "endTime";
	public static String MSG_PROXYRESP_ADDRESS   = "address";
	public static String MSG_PROXYRESP_PROTOCOL  = "protocol";
	public static String MSG_PROXYRESP_IP   = "ip";
	public static String MSG_PROXYRESP_PORT = "port";
	public static String MSG_PROXYRESP_PATH = "path";
	public static String MSG_PROXYRESP_BROADCAST = "broadcast";	
	
	public static int parseMsg(JSONObject inBody){
		JSONObject tServerInfo;
		JSONObject tStatus;		
		JSONObject tStatusPeriod;
		JSONObject tAddress;
		
		try {
			tServerInfo = inBody.getJSONObject(MSG_PROXYRESP_SERVER);
			if (tServerInfo.has(MSG_PROXYRESP_STATUS))
			{
				tStatus = tServerInfo.getJSONObject(MSG_PROXYRESP_STATUS);
				if(tStatus.has(MSG_PROXYRESP_SERVSTAT))			
				    RenHaiInfo.ServerAddr.storeServiceStatus(tStatus.getInt(MSG_PROXYRESP_SERVSTAT));
				
				if(tStatus.has(MSG_PROXYRESP_STATPERD))
				{
					tStatusPeriod = tStatus.getJSONObject(MSG_PROXYRESP_STATPERD);
					if(tStatus.has(MSG_PROXYRESP_TIMEZONE))
					    RenHaiInfo.ServerAddr.storeTimeZone(tStatusPeriod.getString(MSG_PROXYRESP_TIMEZONE));
					if(tStatus.has(MSG_PROXYRESP_BEGINTIME))
					    RenHaiInfo.ServerAddr.storeBeginTime(tStatusPeriod.getString(MSG_PROXYRESP_BEGINTIME));
					if(tStatus.has(MSG_PROXYRESP_ENDTIME))
					    RenHaiInfo.ServerAddr.storeEndTime(tStatusPeriod.getString(MSG_PROXYRESP_ENDTIME));
				}
				
				if(tServerInfo.has(MSG_PROXYRESP_ADDRESS))
				{
					tAddress = tServerInfo.getJSONObject(MSG_PROXYRESP_ADDRESS);
					if(tAddress.has(MSG_PROXYRESP_PROTOCOL))
					    RenHaiInfo.ServerAddr.storeProtocol(tAddress.getString(MSG_PROXYRESP_PROTOCOL));
					if(tAddress.has(MSG_PROXYRESP_IP))
					     RenHaiInfo.ServerAddr.storeServerIp(tAddress.getString(MSG_PROXYRESP_IP));
					if(tAddress.has(MSG_PROXYRESP_PORT))
					    RenHaiInfo.ServerAddr.storeServerPort(tAddress.getInt(MSG_PROXYRESP_PORT));
					if(tAddress.has(MSG_PROXYRESP_PATH))
					    RenHaiInfo.ServerAddr.storeServerPath(tAddress.getString(MSG_PROXYRESP_PATH));
				}
				
				if(tServerInfo.has(MSG_PROXYRESP_BROADCAST))
				    RenHaiInfo.ServerAddr.storeBroadcastInfo(tServerInfo.getString(MSG_PROXYRESP_BROADCAST));
			}

			return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;						
		} catch (JSONException e) {
			mlog.error("Failed to decode ProxySyncResq body", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
	}

}

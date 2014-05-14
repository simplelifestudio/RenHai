/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiAppDataSyncReq.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiMsgProxyDataSyncReq extends RenHaiMsg{
	
	// ProxyDataSyncRequest fields definition
	public static String MSG_PROXYREQ_APPVERSION = "appVersion";
	public static String MSG_PROXYREQ_VERSION   = "version";
	public static String MSG_PROXYREQ_BUILD     = "build";
	
	public static JSONObject constructMsg(){
		JSONObject tMsgContent = new JSONObject();
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
				            RenHaiDefinitions.RENHAI_MSGTYPE_PROXYREQUEST,
				            RenHaiDefinitions.RENHAI_MSGID_PROXYSYNCREQUEST);
		
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			JSONObject tAppVersion = new JSONObject();
			tAppVersion.put(MSG_PROXYREQ_VERSION, RenHaiDefinitions.RENHAI_APP_VERSION);
			tAppVersion.put(MSG_PROXYREQ_BUILD, RenHaiDefinitions.RENHAI_APP_BUILD);
			tMsgBodyContent.put(MSG_PROXYREQ_APPVERSION, tAppVersion);
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing ProxyDataSyncRequest: "+tMsgContent.toString());
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
		              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);
		} catch (JSONException e) {
			mlog.error("Failed to construct ProxyDataSyncRequest!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt ProxyDataSyncRequest!", e);
		}		
				
		return tMsg;
	}

}

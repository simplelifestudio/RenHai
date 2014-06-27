/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgBusinessSessionNotification.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiInfo;

public class RenHaiMsgBusinessSessionNotification extends RenHaiMsg {
	
	// Message fields definition
	public static String MSG_BUSINESSSESSIONNOTIF_SESSIONID      = "businessSessionId";
	public static String MSG_BUSINESSSESSIONNOTIF_BUSINESSTYPE   = "businessType";
	public static String MSG_BUSINESSSESSIONNOTIF_OPERATIONTYPE  = "operationType";
	public static String MSG_BUSINESSSESSIONNOTIF_OPERATIONINFO  = "operationInfo";
	public static String MSG_BUSINESSSESSIONNOTIF_OPERATIONVALUE = "operationValue";
	
	public static int parseMsg(Context _context, JSONObject inBody){		
		
		try 
		{

			
		}catch (JSONException e) {
			mlog.error("Failed to process RenHaiMsgBusinessSessionResp!", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;

	}

}

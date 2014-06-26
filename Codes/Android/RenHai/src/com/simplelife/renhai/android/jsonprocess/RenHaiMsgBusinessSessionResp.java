/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgBusinessSessionResp.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiInfo;

import android.content.Context;
import android.content.Intent;

public class RenHaiMsgBusinessSessionResp extends RenHaiMsg{
	// Message fields definition
	public static String MSG_BUSINESSSESSIONRESP_SESSIONID      = "businessSessionId";
	public static String MSG_BUSINESSSESSIONRESP_BUSINESSTYPE   = "businessType";
	public static String MSG_BUSINESSSESSIONRESP_OPERATIONTYPE  = "operationType";
	public static String MSG_BUSINESSSESSIONRESP_OPERATIONINFO  = "operationInfo";
	public static String MSG_BUSINESSSESSIONRESP_OPERATIONVALUE = "operationValue";
	
	public static int parseMsg(Context _context, JSONObject inBody){
		
		boolean tValueSuccessFlag = false;
		
		try 
		{
			if(inBody.has(MSG_BUSINESSSESSIONRESP_SESSIONID))
				RenHaiInfo.BusinessSession.setBusinessSessionId(inBody.getInt(MSG_BUSINESSSESSIONRESP_SESSIONID));
				
			if(inBody.has(MSG_BUSINESSSESSIONRESP_OPERATIONVALUE))
				tValueSuccessFlag = (inBody.getInt(MSG_BUSINESSSESSIONRESP_OPERATIONVALUE) == 1) ? true : false;
				                  
			if(tValueSuccessFlag == true)
			{
				Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
		        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
		        		         RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESPSUCC);
		        _context.sendBroadcast(tIntent);
			}else
			{
				Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
		        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
		        		         RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESPFAIL);
		        _context.sendBroadcast(tIntent);				
			}
			
		}catch (JSONException e) {
			mlog.error("Failed to process RenHaiMsgBusinessSessionResp!", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;

	}

}

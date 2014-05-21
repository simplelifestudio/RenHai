/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgServerDataSyncResp.java
 *  RenHai
 *
 *  Created by Chris Li on 14-5-21. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiInfo;

import android.content.Context;

public class RenHaiMsgServerDataSyncResp extends RenHaiMsg{
	
	public static String MSG_SVRSYNCRESP_DEVCOUNT = "deviceCount";
	public static String MSG_SVRSYNCRESP_ONLINE   = "online";
	public static String MSG_SVRSYNCRESP_RANDOM   = "random";
	public static String MSG_SVRSYNCRESP_INTEREST = "interest";
	public static String MSG_SVRSYNCRESP_CHAT     = "chat";
	public static String MSG_SVRSYNCRESP_RANDOMCHAT   = "randomChat";
	public static String MSG_SVRSYNCRESP_INTERESTCHAT = "interestChat";
	public static String MSG_SVRSYNCRESP_MGTDATA  = "managementData";	
	public static String MSG_SVRSYNCRESP_DEVCAPA = "deviceCapacity";	
	public static String MSG_SVRSYNCRESP_INTLABELLIST = "interestLabelList";
	public static String MSG_SVRSYNCRESP_CURRENT   = "current";
	public static String MSG_SVRSYNCRESP_HISTORY   = "history";
	public static String MSG_SVRSYNCRESP_STARTTIME = "startTime";
	public static String MSG_SVRSYNCRESP_ENDTIME   = "endTime";
	
	public static String MSG_SVRSYNCRESP_TOPIMPLABELS = "topImpressLabels";
	public static String MSG_SVRSYNCRESP_TOPINTLABELS = "topInterestLabels";
	
	public static int parseMsg(Context _context, JSONObject inBody){		
		JSONObject tDevCount = new JSONObject();
		JSONObject tDevCapa  = new JSONObject();
		JSONObject tIntLabelList = new JSONObject();
		JSONObject tHistory  = new JSONObject();
		
		try {
			if(inBody.has(MSG_SVRSYNCRESP_DEVCOUNT))
			{
				tDevCount = inBody.getJSONObject(MSG_SVRSYNCRESP_DEVCOUNT);
				if(tDevCount.has(MSG_SVRSYNCRESP_ONLINE))
					RenHaiInfo.ServerPoolStat.storeOnlineCount(tDevCount.getInt(MSG_SVRSYNCRESP_ONLINE));
				if(tDevCount.has(MSG_SVRSYNCRESP_RANDOM))
					RenHaiInfo.ServerPoolStat.storeRandomCount(tDevCount.getInt(MSG_SVRSYNCRESP_RANDOM));
				if(tDevCount.has(MSG_SVRSYNCRESP_INTEREST))
					RenHaiInfo.ServerPoolStat.storeInterestCount(tDevCount.getInt(MSG_SVRSYNCRESP_INTEREST));
				if(tDevCount.has(MSG_SVRSYNCRESP_CHAT))
					RenHaiInfo.ServerPoolStat.storeChatCount(tDevCount.getInt(MSG_SVRSYNCRESP_CHAT));
				if(tDevCount.has(MSG_SVRSYNCRESP_RANDOMCHAT))
					RenHaiInfo.ServerPoolStat.storeRandomChatCount(tDevCount.getInt(MSG_SVRSYNCRESP_RANDOMCHAT));
				if(tDevCount.has(MSG_SVRSYNCRESP_INTERESTCHAT))
					RenHaiInfo.ServerPoolStat.storeInterestChatCount(tDevCount.getInt(MSG_SVRSYNCRESP_INTERESTCHAT));
				if(tDevCount.has(MSG_SVRSYNCRESP_MGTDATA))
					RenHaiInfo.ServerPoolStat.storeManagementData(tDevCount.getInt(MSG_SVRSYNCRESP_MGTDATA));
			}
			
			if(inBody.has(MSG_SVRSYNCRESP_DEVCAPA))
			{
				tDevCapa = inBody.getJSONObject(MSG_SVRSYNCRESP_DEVCAPA);
				if(tDevCapa.has(MSG_SVRSYNCRESP_ONLINE))
					RenHaiInfo.ServerPoolStat.storeOnlineCapa(tDevCapa.getInt(MSG_SVRSYNCRESP_ONLINE));
				if(tDevCapa.has(MSG_SVRSYNCRESP_RANDOM))
					RenHaiInfo.ServerPoolStat.storeRandomCapa(tDevCapa.getInt(MSG_SVRSYNCRESP_RANDOM));
				if(tDevCapa.has(MSG_SVRSYNCRESP_INTEREST))
					RenHaiInfo.ServerPoolStat.storeInterestCapa(tDevCapa.getInt(MSG_SVRSYNCRESP_INTEREST));
			}
		} catch (JSONException e) {
			mlog.error("Failed to process ServerDataSyncResp!", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;
		
	}

}

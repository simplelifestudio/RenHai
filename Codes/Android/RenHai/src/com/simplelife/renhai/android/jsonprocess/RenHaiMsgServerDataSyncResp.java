/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgServerDataSyncResp.java
 *  RenHai
 *
 *  Created by Chris Li on 14-5-21. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.data.InterestLabelMap;
import com.simplelife.renhai.android.data.RenHaiInfo;

import android.content.Context;
import android.content.Intent;

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
	public static String MSG_SVRSYNCRESP_INLABELPROFCNT = "currentProfileCount";
	public static String MSG_SVRSYNCRESP_INLABELGLBID = "globalInterestLabelId";
	public static String MSG_SVRSYNCRESP_INLABELGLBMATCHCNT = "globalMatchCount";
	public static String MSG_SVRSYNCRESP_INLABELNAME = "interestLabelName";
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
				/* managementData is not for the User, ignore it here currently
				if(tDevCount.has(MSG_SVRSYNCRESP_MGTDATA))
					RenHaiInfo.ServerPoolStat.storeManagementData(tDevCount.getInt(MSG_SVRSYNCRESP_MGTDATA));*/
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
			
			if(inBody.has(MSG_SVRSYNCRESP_INTLABELLIST))
			{
				tIntLabelList = inBody.getJSONObject(MSG_SVRSYNCRESP_INTLABELLIST);
				if(tIntLabelList.has(MSG_SVRSYNCRESP_CURRENT))
				{
					JSONArray tCurrentLabel = tIntLabelList.getJSONArray(MSG_SVRSYNCRESP_CURRENT);					
					int tLabelSize = tCurrentLabel.length();
					if (tLabelSize > 0)
					{
						RenHaiInfo.InterestLabel.resetCurrHotLabelList();
						for(int i = 0; i < tLabelSize; i++)
						{
							JSONObject tIntLabelMapInList = tCurrentLabel.getJSONObject(i);
							InterestLabelMap tIntLabelMap = new InterestLabelMap();
							if(tIntLabelMapInList.has(MSG_SVRSYNCRESP_INLABELGLBID))
								tIntLabelMap.setGlobalIntLabelId(tIntLabelMapInList.getInt(MSG_SVRSYNCRESP_INLABELGLBID));
							if(tIntLabelMapInList.has(MSG_SVRSYNCRESP_INLABELPROFCNT))
								tIntLabelMap.setCurrentProfileCount(tIntLabelMapInList.getInt(MSG_SVRSYNCRESP_INLABELPROFCNT));
							if(tIntLabelMapInList.has(MSG_SVRSYNCRESP_INLABELGLBMATCHCNT))
								tIntLabelMap.setGlobalMatchCount(tIntLabelMapInList.getInt(MSG_SVRSYNCRESP_INLABELGLBMATCHCNT));
							if(tIntLabelMapInList.has(MSG_SVRSYNCRESP_INLABELNAME))
							{
								tIntLabelMap.setIntLabelName(tIntLabelMapInList.getString(MSG_SVRSYNCRESP_INLABELNAME));
							}else{
								// It is not a normal case, but you know...
								tIntLabelMap.setIntLabelName("Unknow");
							}
								
							RenHaiInfo.InterestLabel.putCurrHotLabel(tIntLabelMap);						
						}
					}
				}
			}
			// Set the data sync flag
			RenHaiInfo.setAppDataSyncronized();
			
	        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
	        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
	        		         RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_SERVERSYNCRESP);
	        _context.sendBroadcast(tIntent);
	        
		} catch (JSONException e) {
			mlog.error("Failed to process ServerDataSyncResp!", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;
		
	}

}

/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgAppDataSyncResp.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiInfo;

public class RenHaiMsgAppDataSyncResp extends RenHaiMsg{
	
	// AppDataSyncRequest fields definitions
	public static String MSG_APPSYNCRESP_QUERY = "dataQuery";
	public static String MSG_APPSYNCRESP_DEV   = "device";
	public static String MSG_APPSYNCRESP_DEVID = "deviceId";
	public static String MSG_APPSYNCRESP_DEVSN = "deviceSn";
	public static String MSG_APPSYNCRESP_DEVCARD = "deviceCard";
	
	public static String MSG_APPSYNCRESP_PROFILE = "profile";
	public static String MSG_APPSYNCRESP_PROFILEID  = "profileId";
	public static String MSG_APPSYNCRESP_SVRSTAT    = "serviceStatus";
	public static String MSG_APPSYNCRESP_UNBANDATE  = "unbanDate";
	public static String MSG_APPSYNCRESP_LSTACTTIME = "lastActivityTime";
	public static String MSG_APPSYNCRESP_CREATETIME = "createTime";
	public static String MSG_APPSYNCRESP_ACTIVE  = "active";
	
	public static String MSG_APPSYNCRESP_INTCARD = "interestCard";
	public static String MSG_APPSYNCRESP_INTCARDID  = "interestCardId";
	public static String MSG_APPSYNCRESP_INTLBLLIST = "interestLabelList";
	public static String MSG_APPSYNCRESP_GLBINTLABELID = "globalInterestLabelId";
	public static String MSG_APPSYNCRESP_INTLABELNAME  = "interestLabelName";
	public static String MSG_APPSYNCRESP_GLBMTCHCOUNT  = "globalMatchCount";
	public static String MSG_APPSYNCRESP_LABELORDER = "labelOrder";
	public static String MSG_APPSYNCRESP_MATCHCOUNT = "matchCount";
	public static String MSG_APPSYNCRESP_VALIDFLAG  = "validFlag";
	
	public static String MSG_APPSYNCRESP_IMPCARD = "impressCard";
	public static String MSG_APPSYNCRESP_IMPCARDID = "impressCardId";
	public static String MSG_APPSYNCRESP_CHATTCOUNT = "chatTotalCount";
	public static String MSG_APPSYNCRESP_CHATTDURA = "chatTotalDuration";
	public static String MSG_APPSYNCRESP_CHATLOSS = "chatLossCount";
	public static String MSG_APPSYNCRESP_ASSLABELLIST = "assessLabelList";
	public static String MSG_APPSYNCRESP_IMPLABELLIST = "impressLabelList";
	
	public static String MSG_APPSYNCRESP_DATAUPDATE = "dataUpdate";
	public static String MSG_APPSYNCRESP_DEVCARDID = "deviceCardId";
	public static String MSG_APPSYNCRESP_RGSTTIME = "registerTime";
	public static String MSG_APPSYNCRESP_DEVMODEL = "deviceModel";
	public static String MSG_APPSYNCRESP_OSVERSION = "osVersion";
	public static String MSG_APPSYNCRESP_APPVERSION = "appVersion";
	public static String MSG_APPSYNCRESP_LOCATION = "location";
	public static String MSG_APPSYNCRESP_ISJAILED = "isJailed";
	
	public static int parseMsg(Context _context, JSONObject inBody){

		JSONObject tDataQuery    = new JSONObject();
		JSONObject tDataUpdate   = new JSONObject();
		JSONObject tQueryDevice  = new JSONObject();
		JSONObject tQDDeviceCard = new JSONObject();
		JSONObject tQDProfile    = new JSONObject();
		JSONObject tQDPInterestCard = new JSONObject();
		JSONObject tQDPImpressCard  = new JSONObject();
		
		try 
		{
		    if(inBody.has(MSG_APPSYNCRESP_QUERY))
		    {
				tDataQuery = inBody.getJSONObject(MSG_APPSYNCRESP_QUERY);
				if(tDataQuery.has(MSG_APPSYNCRESP_DEV))
				{
					tQueryDevice = tDataQuery.getJSONObject(MSG_APPSYNCRESP_DEV);
					if(tQueryDevice.has(MSG_APPSYNCRESP_DEVCARD))
					{
						// deviceId and deviceSn fields have been checked while
						// processing the header, so leave them unprocessed here
						// unless we will need them later for other purpose
						tQDDeviceCard = tQueryDevice.getJSONObject(MSG_APPSYNCRESP_DEVCARD);
						if(tQDDeviceCard.has(MSG_APPSYNCRESP_DEVCARDID))
							RenHaiInfo.DeviceCard.storeDeviceCardId(tQDDeviceCard.getInt(MSG_APPSYNCRESP_DEVCARDID));						
						if(tQDDeviceCard.has(MSG_APPSYNCRESP_RGSTTIME))
							RenHaiInfo.DeviceCard.storeRegisterTime(tQDDeviceCard.getString(MSG_APPSYNCRESP_RGSTTIME));						
					}
					
					if(tQueryDevice.has(MSG_APPSYNCRESP_PROFILE))
					{
						tQDProfile = tQueryDevice.getJSONObject(MSG_APPSYNCRESP_PROFILE);
						if(tQDProfile.has(MSG_APPSYNCRESP_PROFILEID))
							RenHaiInfo.Profile.storeProfileId(tQDProfile.getInt(MSG_APPSYNCRESP_PROFILEID));
						if(tQDProfile.has(MSG_APPSYNCRESP_SVRSTAT))
							RenHaiInfo.Profile.storeServiceStatus(tQDProfile.getInt(MSG_APPSYNCRESP_SVRSTAT));
						if(tQDProfile.has(MSG_APPSYNCRESP_UNBANDATE))
							RenHaiInfo.Profile.storeUnbanDate(tQDProfile.getLong(MSG_APPSYNCRESP_UNBANDATE));
						/* TODO:lastActivityTime and active should not be processed here
						if(tQDProfile.has(MSG_APPSYNCRESP_LSTACTTIME))
							RenHaiInfo.Profile.storeLastActiveTime(tQDProfile.getLong(MSG_APPSYNCRESP_LSTACTTIME));*/
						if(tQDProfile.has(MSG_APPSYNCRESP_CREATETIME))
							RenHaiInfo.Profile.storeCreatetTime(tQDProfile.getString(MSG_APPSYNCRESP_CREATETIME));						
						if(tQDProfile.has(MSG_APPSYNCRESP_INTCARD))
						{
							tQDPInterestCard = tQDProfile.getJSONObject(MSG_APPSYNCRESP_INTCARD);
							if(tQDPInterestCard.has(MSG_APPSYNCRESP_INTCARDID))
								RenHaiInfo.Profile.storeInterestCardId(tQDPInterestCard.getInt(MSG_APPSYNCRESP_INTCARDID));
							// TODO:interestLabelList processing
						}
						if(tQDProfile.has(MSG_APPSYNCRESP_IMPCARD))
						{
							tQDPImpressCard = tQDProfile.getJSONObject(MSG_APPSYNCRESP_IMPCARD);
							if(tQDPImpressCard.has(MSG_APPSYNCRESP_IMPCARDID))
								RenHaiInfo.Profile.storeImpressCardId(tQDPImpressCard.getInt(MSG_APPSYNCRESP_IMPCARDID));
							if(tQDPImpressCard.has(MSG_APPSYNCRESP_CHATTCOUNT))
								RenHaiInfo.Profile.storeChatTotalCound(tQDPImpressCard.getInt(MSG_APPSYNCRESP_CHATTCOUNT));
							if(tQDPImpressCard.has(MSG_APPSYNCRESP_CHATTDURA))
								RenHaiInfo.Profile.storeChatTotalDuration(tQDPImpressCard.getInt(MSG_APPSYNCRESP_CHATTDURA));
							if(tQDPImpressCard.has(MSG_APPSYNCRESP_CHATLOSS))
								RenHaiInfo.Profile.storeChatLossCount(tQDPImpressCard.getInt(MSG_APPSYNCRESP_CHATLOSS));
							// TODO: assessLabelList and impressLabelList processing
						}
					}
					
				}
				
		        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
		        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
		        		         RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_APPSYNCRESP);
		        _context.sendBroadcast(tIntent);
		    }
		    
		    // TODO: dataUpdate field processing				
		} catch (JSONException e) {
			mlog.error("Failed to process AppDataSyncResp!", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;		
	}
}

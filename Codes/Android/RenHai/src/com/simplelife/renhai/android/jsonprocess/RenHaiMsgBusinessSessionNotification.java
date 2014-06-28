/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgBusinessSessionNotification.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.data.BusinessSessionInfo;
import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.data.RenHaiInfo;

public class RenHaiMsgBusinessSessionNotification extends RenHaiMsg {
	
	// Message fields definition
	public static String MSG_BUSINESSSESSIONNOTIF_SESSIONID      = "businessSessionId";
	public static String MSG_BUSINESSSESSIONNOTIF_BUSINESSTYPE   = "businessType";
	public static String MSG_BUSINESSSESSIONNOTIF_OPERATIONTYPE  = "operationType";
	public static String MSG_BUSINESSSESSIONNOTIF_OPERATIONINFO  = "operationInfo";
	public static String MSG_BUSINESSSESSIONNOTIF_OPERATIONVALUE = "operationValue";
	
	public static String MSG_BUSINESSSESSIONNOTIF_DEV   = "device";
	public static String MSG_BUSINESSSESSIONNOTIF_DEVID = "deviceId";
	public static String MSG_BUSINESSSESSIONNOTIF_DEVSN = "deviceSn";
	public static String MSG_BUSINESSSESSIONNOTIF_DEVCARD = "deviceCard";
	
	public static String MSG_BUSINESSSESSIONNOTIF_PROFILE = "profile";
	public static String MSG_BUSINESSSESSIONNOTIF_PROFILEID  = "profileId";
	public static String MSG_BUSINESSSESSIONNOTIF_SVRSTAT    = "serviceStatus";
	public static String MSG_BUSINESSSESSIONNOTIF_LSTACTTIME = "lastActivityTime";
	public static String MSG_BUSINESSSESSIONNOTIF_CREATETIME = "createTime";
	public static String MSG_BUSINESSSESSIONNOTIF_ACTIVE  = "active";
	
	public static String MSG_BUSINESSSESSIONNOTIF_INTCARD = "interestCard";
	public static String MSG_BUSINESSSESSIONNOTIF_INTCARDID  = "interestCardId";
	public static String MSG_BUSINESSSESSIONNOTIF_INTLBLLIST = "interestLabelList";
	public static String MSG_BUSINESSSESSIONNOTIF_GLBINTLABELID = "globalInterestLabelId";
	public static String MSG_BUSINESSSESSIONNOTIF_INTLABELNAME  = "interestLabelName";
	public static String MSG_BUSINESSSESSIONNOTIF_GLBMTCHCOUNT  = "globalMatchCount";
	//public static String MSG_BUSINESSSESSIONNOTIF_LABELORDER = "labelOrder";
	public static String MSG_BUSINESSSESSIONNOTIF_MATCHCOUNT = "matchCount";
	public static String MSG_BUSINESSSESSIONNOTIF_VALIDFLAG  = "validFlag";
	
	public static String MSG_BUSINESSSESSIONNOTIF_IMPCARD = "impressCard";
	public static String MSG_BUSINESSSESSIONNOTIF_IMPCARDID = "impressCardId";
	public static String MSG_BUSINESSSESSIONNOTIF_CHATTCOUNT = "chatTotalCount";
	public static String MSG_BUSINESSSESSIONNOTIF_CHATTDURA = "chatTotalDuration";
	public static String MSG_BUSINESSSESSIONNOTIF_CHATLOSS = "chatLossCount";
	public static String MSG_BUSINESSSESSIONNOTIF_ASSLABELLIST = "assessLabelList";
	public static String MSG_BUSINESSSESSIONNOTIF_IMPLABELLIST = "impressLabelList";
	public static String MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT = "assessCount";
	public static String MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT = "assessedCount";
	public static String MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID = "globalImpressLabelId";
	public static String MSG_BUSINESSSESSIONNOTIF_ASSLISTIMPLABELNAME = "impressLabelName";
	public static String MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME = "updateTime";
	
	public static String MSG_BUSINESSSESSIONNOTIF_DEVCARDID = "deviceCardId";
	public static String MSG_BUSINESSSESSIONNOTIF_RGSTTIME = "registerTime";
	public static String MSG_BUSINESSSESSIONNOTIF_DEVMODEL = "deviceModel";
	public static String MSG_BUSINESSSESSIONNOTIF_OSVERSION = "osVersion";
	public static String MSG_BUSINESSSESSIONNOTIF_APPVERSION = "appVersion";
	public static String MSG_BUSINESSSESSIONNOTIF_LOCATION = "location";
	public static String MSG_BUSINESSSESSIONNOTIF_ISJAILED = "isJailed";
	
	public static int parseMsg(Context _context, JSONObject inBody){		
		int tOperationType = 0;
		
		try 
		{
			if(inBody.has(MSG_BUSINESSSESSIONNOTIF_SESSIONID))
				BusinessSessionInfo.setBusinessSessionId(inBody.getInt(MSG_BUSINESSSESSIONNOTIF_SESSIONID));

			
			if(inBody.has(MSG_BUSINESSSESSIONNOTIF_BUSINESSTYPE))
			{
				int tBusinessType = inBody.getInt(MSG_BUSINESSSESSIONNOTIF_BUSINESSTYPE);
				if(tBusinessType != BusinessSessionInfo.getBusinessType())
				{
					mlog.warn("Receive unmatch business type in BusinessSessionNotification message!");
					return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
				}
			}
			
			if(inBody.has(MSG_BUSINESSSESSIONNOTIF_OPERATIONTYPE))
				tOperationType = inBody.getInt(MSG_BUSINESSSESSIONNOTIF_OPERATIONTYPE);
			
			if(tOperationType == RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_SESSIONBINDED)
			{
				if(inBody.has(MSG_BUSINESSSESSIONNOTIF_OPERATIONINFO))
				{
					// Process on receiving sessionBinded
					JSONObject tOperationInfo = inBody.getJSONObject(MSG_BUSINESSSESSIONNOTIF_OPERATIONINFO);				
					if(tOperationInfo.has(MSG_BUSINESSSESSIONNOTIF_DEV))
					{
						JSONObject tDevice = tOperationInfo.getJSONObject(MSG_BUSINESSSESSIONNOTIF_DEV);
						if(tDevice.has(MSG_BUSINESSSESSIONNOTIF_DEVID))
							PeerDeviceInfo.storeDeviceId(tDevice.getInt(MSG_BUSINESSSESSIONNOTIF_DEVID));
						if(tDevice.has(MSG_BUSINESSSESSIONNOTIF_DEVSN))
							PeerDeviceInfo.storeDeviceSn(tDevice.getString(MSG_BUSINESSSESSIONNOTIF_DEVSN));
						if(tDevice.has(MSG_BUSINESSSESSIONNOTIF_DEVCARD))
						{
							JSONObject tDevCard = tDevice.getJSONObject(MSG_BUSINESSSESSIONNOTIF_DEVCARD);
							if(tDevCard.has(MSG_BUSINESSSESSIONNOTIF_APPVERSION))
								PeerDeviceInfo.DeviceCard.storeAppVersion(tDevCard.getString(MSG_BUSINESSSESSIONNOTIF_APPVERSION));
							if(tDevCard.has(MSG_BUSINESSSESSIONNOTIF_DEVCARDID))
								PeerDeviceInfo.DeviceCard.storeDeviceCardId(tDevCard.getInt(MSG_BUSINESSSESSIONNOTIF_DEVCARDID));
							if(tDevCard.has(MSG_BUSINESSSESSIONNOTIF_DEVMODEL))
								PeerDeviceInfo.DeviceCard.storeDeviceModel(tDevCard.getString(MSG_BUSINESSSESSIONNOTIF_DEVMODEL));
							// Skip isJailed
							if(tDevCard.has(MSG_BUSINESSSESSIONNOTIF_LOCATION))
								PeerDeviceInfo.DeviceCard.storeLocation(tDevCard.getString(MSG_BUSINESSSESSIONNOTIF_LOCATION));
							if(tDevCard.has(MSG_BUSINESSSESSIONNOTIF_OSVERSION))
								PeerDeviceInfo.DeviceCard.storeOsVersion(tDevCard.getString(MSG_BUSINESSSESSIONNOTIF_OSVERSION));
							if(tDevCard.has(MSG_BUSINESSSESSIONNOTIF_RGSTTIME))
								PeerDeviceInfo.DeviceCard.storeRegisterTime(tDevCard.getString(MSG_BUSINESSSESSIONNOTIF_RGSTTIME));
						}
						if(tDevice.has(MSG_BUSINESSSESSIONNOTIF_PROFILE))
						{
							JSONObject tProfile = tDevice.getJSONObject(MSG_BUSINESSSESSIONNOTIF_PROFILE);
							if(tProfile.has(MSG_BUSINESSSESSIONNOTIF_ACTIVE))
								PeerDeviceInfo.Profile.storeActive(tProfile.getInt(MSG_BUSINESSSESSIONNOTIF_ACTIVE));
							if(tProfile.has(MSG_BUSINESSSESSIONNOTIF_CREATETIME))
								PeerDeviceInfo.Profile.storeCreatetTime(tProfile.getString(MSG_BUSINESSSESSIONNOTIF_CREATETIME));
							if(tProfile.has(MSG_BUSINESSSESSIONNOTIF_LSTACTTIME))
								PeerDeviceInfo.Profile.storeLastActiveTime(tProfile.getLong(MSG_BUSINESSSESSIONNOTIF_LSTACTTIME));
							if(tProfile.has(MSG_BUSINESSSESSIONNOTIF_PROFILEID))
								PeerDeviceInfo.Profile.storeProfileId(tProfile.getInt(MSG_BUSINESSSESSIONNOTIF_PROFILEID));
							if(tProfile.has(MSG_BUSINESSSESSIONNOTIF_SVRSTAT))
								PeerDeviceInfo.Profile.storeServiceStatus(tProfile.getInt(MSG_BUSINESSSESSIONNOTIF_SVRSTAT));
							if(tProfile.has(MSG_BUSINESSSESSIONNOTIF_IMPCARD))
							{
								JSONObject tImpCard = tProfile.getJSONObject(MSG_BUSINESSSESSIONNOTIF_IMPCARD);
								if(tImpCard.has(MSG_BUSINESSSESSIONNOTIF_IMPCARDID))
									PeerDeviceInfo.Profile.storeImpressCardId(tImpCard.getInt(MSG_BUSINESSSESSIONNOTIF_IMPCARDID));
								if(tImpCard.has(MSG_BUSINESSSESSIONNOTIF_CHATTCOUNT))
									PeerDeviceInfo.Profile.storeChatTotalCound(tImpCard.getInt(MSG_BUSINESSSESSIONNOTIF_CHATTCOUNT));
								if(tImpCard.has(MSG_BUSINESSSESSIONNOTIF_CHATTDURA))
									PeerDeviceInfo.Profile.storeChatTotalDuration(tImpCard.getInt(MSG_BUSINESSSESSIONNOTIF_CHATTDURA));
								if(tImpCard.has(MSG_BUSINESSSESSIONNOTIF_CHATLOSS))
									PeerDeviceInfo.Profile.storeChatLossCount(tImpCard.getInt(MSG_BUSINESSSESSIONNOTIF_CHATLOSS));
								if(tImpCard.has(MSG_BUSINESSSESSIONNOTIF_ASSLABELLIST))
								{
									JSONArray tAssesList = tImpCard.getJSONArray(MSG_BUSINESSSESSIONNOTIF_ASSLABELLIST);
									int tListSize = tAssesList.length();
									for(int i=0; i<tListSize; i++)
									{
										JSONObject tAssessLabel = tAssesList.getJSONObject(i);
										
									}
								}
							}
							
							
						}
					}
				}
			}

			


			
		}catch (JSONException e) {
			mlog.error("Failed to process RenHaiMsgBusinessSessionResp!", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;

	}

}

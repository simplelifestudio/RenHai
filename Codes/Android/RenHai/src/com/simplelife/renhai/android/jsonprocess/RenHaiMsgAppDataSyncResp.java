/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgAppDataSyncResp.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.data.ImpressLabelMap;
import com.simplelife.renhai.android.data.InterestLabelMap;
import com.simplelife.renhai.android.data.RenHaiInfo;

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
	public static String MSG_APPSYNCRESP_ASSLISTASSCNT = "assessCount";
	public static String MSG_APPSYNCRESP_ASSLISTASSEDCNT = "assessedCount";
	public static String MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID = "globalImpressLabelId";
	public static String MSG_APPSYNCRESP_ASSLISTIMPLABELNAME = "impressLabelName";
	public static String MSG_APPSYNCRESP_ASSLISTUPDATETIME = "updateTime";
	
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
							if(tQDPInterestCard.has(MSG_APPSYNCRESP_INTLBLLIST))
							{
								JSONArray tIntLabelList = tQDPInterestCard.getJSONArray(MSG_APPSYNCRESP_INTLBLLIST);
								RenHaiInfo.InterestLabel.resetMyIntLabelList();
								int tArraySize = tIntLabelList.length();
								for(int i=0; i<tArraySize; i++)
								{
									JSONObject tIntLabelMapObj = (JSONObject) tIntLabelList.get(i);
									InterestLabelMap tIntLabelMap = new InterestLabelMap();
									if(tIntLabelMapObj.has(MSG_APPSYNCRESP_GLBINTLABELID))
										tIntLabelMap.setGlobalIntLabelId(tIntLabelMapObj.getInt(MSG_APPSYNCRESP_GLBINTLABELID));
									if(tIntLabelMapObj.has(MSG_APPSYNCRESP_INTLABELNAME))
										tIntLabelMap.setIntLabelName(tIntLabelMapObj.getString(MSG_APPSYNCRESP_INTLABELNAME));
									if(tIntLabelMapObj.has(MSG_APPSYNCRESP_GLBMTCHCOUNT))
										tIntLabelMap.setGlobalMatchCount(tIntLabelMapObj.getInt(MSG_APPSYNCRESP_GLBMTCHCOUNT));
									if(tIntLabelMapObj.has(MSG_APPSYNCRESP_LABELORDER))
										tIntLabelMap.setLabelOrder(tIntLabelMapObj.getInt(MSG_APPSYNCRESP_LABELORDER));
									if(tIntLabelMapObj.has(MSG_APPSYNCRESP_MATCHCOUNT))
										tIntLabelMap.setMatchCount(tIntLabelMapObj.getInt(MSG_APPSYNCRESP_MATCHCOUNT));
									if(tIntLabelMapObj.has(MSG_APPSYNCRESP_VALIDFLAG))
									{
										int tValidFlag = tIntLabelMapObj.getInt(MSG_APPSYNCRESP_VALIDFLAG);
										tIntLabelMap.setValidFlag((tValidFlag == 1) ? true : false);
									}
									RenHaiInfo.InterestLabel.putMyIntLabel(tIntLabelMap);
								}
							}
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
							if(tQDPImpressCard.has(MSG_APPSYNCRESP_ASSLABELLIST))
							{
								JSONArray tAssesList = tQDPImpressCard.getJSONArray(MSG_APPSYNCRESP_ASSLABELLIST);
								int tListSize = tAssesList.length();
								for(int i=0; i<tListSize; i++)
								{
									JSONObject tAssessLabel = tAssesList.getJSONObject(i);
									if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTIMPLABELNAME))
									{
										String tLabelName = tAssessLabel.getString(MSG_APPSYNCRESP_ASSLISTIMPLABELNAME);
										if(tLabelName.equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_HAPPY))
										{
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTASSCNT))
												RenHaiInfo.AssessLabel.mHappyLabel.setAssessCount(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTASSCNT));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTASSEDCNT))
												RenHaiInfo.AssessLabel.mHappyLabel.setAssessedCount(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTASSEDCNT));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID))
												RenHaiInfo.AssessLabel.mHappyLabel.setGlobalImpLabelId(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTUPDATETIME))
												RenHaiInfo.AssessLabel.mHappyLabel.setUpdateTime(tAssessLabel.getString(MSG_APPSYNCRESP_ASSLISTUPDATETIME));
										}else if(tLabelName.equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_SOSO)){
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTASSCNT))
												RenHaiInfo.AssessLabel.mSoSoLabel.setAssessCount(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTASSCNT));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTASSEDCNT))
												RenHaiInfo.AssessLabel.mSoSoLabel.setAssessedCount(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTASSEDCNT));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID))
												RenHaiInfo.AssessLabel.mSoSoLabel.setGlobalImpLabelId(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTUPDATETIME))
												RenHaiInfo.AssessLabel.mSoSoLabel.setUpdateTime(tAssessLabel.getString(MSG_APPSYNCRESP_ASSLISTUPDATETIME));
										}else if(tLabelName.equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_DISGUSTING)){
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTASSCNT))
												RenHaiInfo.AssessLabel.mDigustingLabel.setAssessCount(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTASSCNT));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTASSEDCNT))
												RenHaiInfo.AssessLabel.mDigustingLabel.setAssessedCount(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTASSEDCNT));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID))
												RenHaiInfo.AssessLabel.mDigustingLabel.setGlobalImpLabelId(tAssessLabel.getInt(MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID));
											if(tAssessLabel.has(MSG_APPSYNCRESP_ASSLISTUPDATETIME))
												RenHaiInfo.AssessLabel.mDigustingLabel.setUpdateTime(tAssessLabel.getString(MSG_APPSYNCRESP_ASSLISTUPDATETIME));
										}
									}
								}
							}
							if(tQDPImpressCard.has(MSG_APPSYNCRESP_IMPLABELLIST))
							{
								JSONArray tLabelList = tQDPImpressCard.getJSONArray(MSG_APPSYNCRESP_IMPLABELLIST);
								int tImpListSize = tLabelList.length();
								RenHaiInfo.ImpressionLabel.resetMyImpLabels();
								for(int i=0; i<tImpListSize; i++)
								{
									JSONObject tImpLabelInMsg = tLabelList.getJSONObject(i);
									ImpressLabelMap tImpLabel = new ImpressLabelMap();
									if(tImpLabelInMsg.has(MSG_APPSYNCRESP_ASSLISTASSCNT))
										tImpLabel.setAssessCount(tImpLabelInMsg.getInt(MSG_APPSYNCRESP_ASSLISTASSCNT));
									if(tImpLabelInMsg.has(MSG_APPSYNCRESP_ASSLISTASSEDCNT))
										tImpLabel.setAssessedCount(tImpLabelInMsg.getInt(MSG_APPSYNCRESP_ASSLISTASSEDCNT));
									if(tImpLabelInMsg.has(MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID))
										tImpLabel.setGlobalImpLabelId(tImpLabelInMsg.getInt(MSG_APPSYNCRESP_ASSLISTGLBIMPLABELID));
									if(tImpLabelInMsg.has(MSG_APPSYNCRESP_ASSLISTUPDATETIME))
										tImpLabel.setUpdateTime(tImpLabelInMsg.getString(MSG_APPSYNCRESP_ASSLISTIMPLABELNAME));
									if(tImpLabelInMsg.has(MSG_APPSYNCRESP_ASSLISTIMPLABELNAME))
										tImpLabel.setImpLabelName(tImpLabelInMsg.getString(MSG_APPSYNCRESP_ASSLISTIMPLABELNAME));
									RenHaiInfo.ImpressionLabel.putMyImpLabelMap(tImpLabel);										
								}
							}
						}
					}
					
				}
		    }
		    
		    // TODO: dataUpdate field processing	
		    
			// Set the data sync flag
			RenHaiInfo.setAppDataSyncronized();
			
	        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
	        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
	        		         RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_APPSYNCRESP);
	        _context.sendBroadcast(tIntent);
	        
		} catch (JSONException e) {
			mlog.error("Failed to process AppDataSyncResp!", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;		
	}
}

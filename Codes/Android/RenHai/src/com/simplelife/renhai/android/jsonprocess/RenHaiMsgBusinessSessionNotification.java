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
import com.simplelife.renhai.android.data.ImpressLabelMap;
import com.simplelife.renhai.android.data.InterestLabelMap;
import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.data.WebRtcSession;

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
	public static String MSG_BUSINESSSESSIONNOTIF_LABELORDER = "labelOrder";
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
	
	public static String MSG_BUSINESSSESSIONNOTIF_MATCHEDCONDITION = "matchedCondition";
	public static String MSG_BUSINESSSESSIONNOTIF_WEBRTC = "webrtc";
	public static String MSG_BUSINESSSESSIONNOTIF_WEBRTCAPIKEY = "apiKey";
	public static String MSG_BUSINESSSESSIONNOTIF_WEBRTCSESSIONID = "sessionId";
	public static String MSG_BUSINESSSESSIONNOTIF_WEBRTCTOKEN = "token";
	
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
					PeerDeviceInfo.resetPeerDeviceInfo();
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
										if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTIMPLABELNAME))
										{
											String tLabelName = tAssessLabel.getString(MSG_BUSINESSSESSIONNOTIF_ASSLISTIMPLABELNAME);
											if(tLabelName.equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_HAPPY))
											{
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT))
													PeerDeviceInfo.AssessLabel.mHappyLabel.setAssessCount(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT))
													PeerDeviceInfo.AssessLabel.mHappyLabel.setAssessedCount(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID))
													PeerDeviceInfo.AssessLabel.mHappyLabel.setGlobalImpLabelId(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME))
													PeerDeviceInfo.AssessLabel.mHappyLabel.setUpdateTime(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME));
											}else if(tLabelName.equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_SOSO)){
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT))
													PeerDeviceInfo.AssessLabel.mSoSoLabel.setAssessCount(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT))
													PeerDeviceInfo.AssessLabel.mSoSoLabel.setAssessedCount(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID))
													PeerDeviceInfo.AssessLabel.mSoSoLabel.setGlobalImpLabelId(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME))
													PeerDeviceInfo.AssessLabel.mSoSoLabel.setUpdateTime(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME));
											}else if(tLabelName.equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_DISGUSTING)){
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT))
													PeerDeviceInfo.AssessLabel.mDigustingLabel.setAssessCount(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT))
													PeerDeviceInfo.AssessLabel.mDigustingLabel.setAssessedCount(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID))
													PeerDeviceInfo.AssessLabel.mDigustingLabel.setGlobalImpLabelId(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID));
												if(tAssessLabel.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME))
													PeerDeviceInfo.AssessLabel.mDigustingLabel.setUpdateTime(tAssessLabel.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME));
											}
										}
									}
								}
								if(tImpCard.has(MSG_BUSINESSSESSIONNOTIF_IMPLABELLIST))
								{
									JSONArray tLabelList = tImpCard.getJSONArray(MSG_BUSINESSSESSIONNOTIF_IMPLABELLIST);
									int tImpListSize = tLabelList.length();
									PeerDeviceInfo.ImpressionLabel.resetPeerImpLabels();
									for(int i=0; i<tImpListSize; i++)
									{
										JSONObject tImpLabelInMsg = tLabelList.getJSONObject(i);
										ImpressLabelMap tImpLabel = new ImpressLabelMap();
										if(tImpLabelInMsg.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT))
											tImpLabel.setAssessCount(tImpLabelInMsg.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSCNT));
										if(tImpLabelInMsg.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT))
											tImpLabel.setAssessedCount(tImpLabelInMsg.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTASSEDCNT));
										if(tImpLabelInMsg.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID))
											tImpLabel.setGlobalImpLabelId(tImpLabelInMsg.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTGLBIMPLABELID));
										if(tImpLabelInMsg.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME))
											tImpLabel.setUpdateTime(tImpLabelInMsg.getInt(MSG_BUSINESSSESSIONNOTIF_ASSLISTUPDATETIME));
										if(tImpLabelInMsg.has(MSG_BUSINESSSESSIONNOTIF_ASSLISTIMPLABELNAME))
											tImpLabel.setImpLabelName(tImpLabelInMsg.getString(MSG_BUSINESSSESSIONNOTIF_ASSLISTIMPLABELNAME));
										PeerDeviceInfo.ImpressionLabel.putPeerImpLabelMap(tImpLabel);										
									}
								}
							}
							if(tProfile.has(MSG_BUSINESSSESSIONNOTIF_INTCARD))
							{
								JSONObject tIntCard = tProfile.getJSONObject(MSG_BUSINESSSESSIONNOTIF_INTCARD);
								if(tIntCard.has(MSG_BUSINESSSESSIONNOTIF_INTCARDID))
									PeerDeviceInfo.Profile.storeInterestCardId(tIntCard.getInt(MSG_BUSINESSSESSIONNOTIF_INTCARDID));
								if(tIntCard.has(MSG_BUSINESSSESSIONNOTIF_INTLBLLIST))
								{
									JSONArray tIntLabelList = tIntCard.getJSONArray(MSG_BUSINESSSESSIONNOTIF_INTLBLLIST);
									int tIntListSize = tIntLabelList.length();
									PeerDeviceInfo.InterestLabel.resetPeerIntLabelList();
									for(int i=0; i<tIntListSize; i++)
									{
										JSONObject tIntLabelInList = tIntLabelList.getJSONObject(i);
										InterestLabelMap tIntLabel = new InterestLabelMap();
										if(tIntLabelInList.has(MSG_BUSINESSSESSIONNOTIF_GLBINTLABELID))
											tIntLabel.setGlobalIntLabelId(tIntLabelInList.getInt(MSG_BUSINESSSESSIONNOTIF_GLBINTLABELID));
										if(tIntLabelInList.has(MSG_BUSINESSSESSIONNOTIF_INTLABELNAME))
											tIntLabel.setIntLabelName(tIntLabelInList.getString(MSG_BUSINESSSESSIONNOTIF_INTLABELNAME));
										if(tIntLabelInList.has(MSG_BUSINESSSESSIONNOTIF_GLBMTCHCOUNT))
											tIntLabel.setGlobalMatchCount(tIntLabelInList.getInt(MSG_BUSINESSSESSIONNOTIF_GLBMTCHCOUNT));
										if(tIntLabelInList.has(MSG_BUSINESSSESSIONNOTIF_LABELORDER))
											tIntLabel.setLabelOrder(tIntLabelInList.getInt(MSG_BUSINESSSESSIONNOTIF_LABELORDER));
										if(tIntLabelInList.has(MSG_BUSINESSSESSIONNOTIF_MATCHCOUNT))
											tIntLabel.setMatchCount(tIntLabelInList.getInt(MSG_BUSINESSSESSIONNOTIF_MATCHCOUNT));
										if(tIntLabelInList.has(MSG_BUSINESSSESSIONNOTIF_VALIDFLAG))
										{
											int tValidFlag = tIntLabelInList.getInt(MSG_BUSINESSSESSIONNOTIF_VALIDFLAG);
											tIntLabel.setValidFlag((tValidFlag == 1) ? true : false);
										}
										PeerDeviceInfo.InterestLabel.putPeerIntLabel(tIntLabel);
									}
								}
							}							
						}
						if(tOperationInfo.has(MSG_BUSINESSSESSIONNOTIF_MATCHEDCONDITION))
						{
							JSONObject tMatchCond = tOperationInfo.getJSONObject(MSG_BUSINESSSESSIONNOTIF_MATCHEDCONDITION);
							if(tMatchCond.has(MSG_BUSINESSSESSIONNOTIF_GLBINTLABELID))
								BusinessSessionInfo.MatchedCondition.setGlbIntLabelId(tMatchCond.getInt(MSG_BUSINESSSESSIONNOTIF_GLBINTLABELID));
							if(tMatchCond.has(MSG_BUSINESSSESSIONNOTIF_GLBMTCHCOUNT))
								BusinessSessionInfo.MatchedCondition.setMatchCount(tMatchCond.getInt(MSG_BUSINESSSESSIONNOTIF_GLBMTCHCOUNT));
							if(tMatchCond.has(MSG_BUSINESSSESSIONNOTIF_INTLABELNAME))
								BusinessSessionInfo.MatchedCondition.setMatchLabelName(tMatchCond.getString(MSG_BUSINESSSESSIONNOTIF_INTLABELNAME));
						}
						if(tOperationInfo.has(MSG_BUSINESSSESSIONNOTIF_WEBRTC))
						{
							JSONObject tWebRtc = tOperationInfo.getJSONObject(MSG_BUSINESSSESSIONNOTIF_WEBRTC);
							if(tWebRtc.has(MSG_BUSINESSSESSIONNOTIF_WEBRTCAPIKEY))
								WebRtcSession.setApiKey(tWebRtc.getLong(MSG_BUSINESSSESSIONNOTIF_WEBRTCAPIKEY));
							if(tWebRtc.has(MSG_BUSINESSSESSIONNOTIF_WEBRTCSESSIONID))
								WebRtcSession.setSessionId(tWebRtc.getString(MSG_BUSINESSSESSIONNOTIF_WEBRTCSESSIONID));
							if(tWebRtc.has(MSG_BUSINESSSESSIONNOTIF_WEBRTCTOKEN))
								WebRtcSession.setToken(tWebRtc.getString(MSG_BUSINESSSESSIONNOTIF_WEBRTCTOKEN));
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

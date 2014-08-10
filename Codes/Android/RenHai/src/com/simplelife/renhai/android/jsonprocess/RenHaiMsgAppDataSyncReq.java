/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiAppDataSyncReq.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.data.InterestLabelMap;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiMsgAppDataSyncReq extends RenHaiMsg{
	
	// AppDataSyncRequest fields definitions
	public static String MSG_APPSYNCREQ_QUERY = "dataQuery";
	public static String MSG_APPSYNCREQ_DEV   = "device";
	public static String MSG_APPSYNCREQ_DEVID = "deviceId";
	public static String MSG_APPSYNCREQ_DEVSN = "deviceSn";
	public static String MSG_APPSYNCREQ_DEVCARD = "deviceCard";
	
	public static String MSG_APPSYNCREQ_PROFILE = "profile";
	public static String MSG_APPSYNCREQ_PROFILEID  = "profileId";
	public static String MSG_APPSYNCREQ_SVRSTAT    = "serviceStatus";
	public static String MSG_APPSYNCREQ_UNBANDATE  = "unbanDate";
	public static String MSG_APPSYNCREQ_LSTACTTIME = "lastActivityTime";
	public static String MSG_APPSYNCREQ_CREATETIME = "createTime";
	public static String MSG_APPSYNCREQ_ACTIVE  = "active";
	
	public static String MSG_APPSYNCREQ_INTCARD = "interestCard";
	public static String MSG_APPSYNCREQ_INTCARDID  = "interestCardId";
	public static String MSG_APPSYNCREQ_INTLBLLIST = "interestLabelList";
	public static String MSG_APPSYNCREQ_GLBINTLABELID = "globalInterestLabelId";
	public static String MSG_APPSYNCREQ_INTLABELNAME  = "interestLabelName";
	public static String MSG_APPSYNCREQ_GLBMTCHCOUNT  = "globalMatchCount";
	public static String MSG_APPSYNCREQ_LABELORDER = "labelOrder";
	public static String MSG_APPSYNCREQ_MATCHCOUNT = "matchCount";
	public static String MSG_APPSYNCREQ_VALIDFLAG  = "validFlag";
	
	public static String MSG_APPSYNCREQ_IMPCARD = "impressCard";
	public static String MSG_APPSYNCREQ_IMPCARDID = "impressCardId";
	public static String MSG_APPSYNCREQ_CHATTCOUNT = "chatTotalCount";
	public static String MSG_APPSYNCREQ_CHATTDURA = "chatTotalDuration";
	public static String MSG_APPSYNCREQ_CHATLOSS = "chatLossCount";
	public static String MSG_APPSYNCREQ_ASSLABELLIST = "assessLabelList";
	public static String MSG_APPSYNCREQ_IMPLABELLIST = "impressLabelList";
	
	public static String MSG_APPSYNCREQ_DATAUPDATE = "dataUpdate";
	public static String MSG_APPSYNCREQ_DEVCARDID = "deviceCardId";
	public static String MSG_APPSYNCREQ_RGSTTIME = "registerTime";
	public static String MSG_APPSYNCREQ_DEVMODEL = "deviceModel";
	public static String MSG_APPSYNCREQ_OSVERSION = "osVersion";
	public static String MSG_APPSYNCREQ_APPVERSION = "appVersion";
	public static String MSG_APPSYNCREQ_LOCATION = "location";
	public static String MSG_APPSYNCREQ_ISJAILED = "isJailed";
	
	public static JSONObject constructQueryMsg(){
		JSONObject tMsgContent = new JSONObject();	
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
	            RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST,
	            RenHaiDefinitions.RENHAI_MSGID_APPDATASYNCREQUEST);
		
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			// Construct the dataQuery field
			JSONObject tDataQuery  = new JSONObject();
			JSONObject tDevice     = new JSONObject();
			JSONObject tDeviceCard = new JSONObject();
			JSONObject tProfile    = new JSONObject();
			JSONObject tIntCard    = new JSONObject();
			JSONObject tImpCard    = new JSONObject();

			tDevice.put(MSG_APPSYNCREQ_DEVID, JSONNULL);
			tDeviceCard.put(MSG_APPSYNCREQ_DEVCARDID, JSONNULL);
			tDeviceCard.put(MSG_APPSYNCREQ_RGSTTIME, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_PROFILEID, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_SVRSTAT, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_UNBANDATE, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_LSTACTTIME, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_CREATETIME, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_ACTIVE, JSONNULL);
			
			tIntCard.put(MSG_APPSYNCREQ_INTCARDID, JSONNULL);
			tIntCard.put(MSG_APPSYNCREQ_INTLBLLIST, 9);			
			tProfile.put(MSG_APPSYNCREQ_INTCARD, tIntCard);
			tProfile.put(MSG_APPSYNCREQ_IMPCARD, JSONNULL);
			tDevice.put(MSG_APPSYNCREQ_DEVSN, JSONNULL);			
			tDevice.put(MSG_APPSYNCREQ_DEVCARD, tDeviceCard);
			tDevice.put(MSG_APPSYNCREQ_PROFILE, tProfile);
			tDataQuery.put(MSG_APPSYNCREQ_DEV, tDevice);
			tMsgBodyContent.put(MSG_APPSYNCREQ_QUERY, tDataQuery);
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing AppDataSyncRequest(Query): "+tMsgContent.toString());
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
						              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);						
						
		} catch (JSONException e) {
			mlog.error("Failed to construct query type AppDataSyncRequest!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt query type AppDataSyncRequest!", e);
		}				
		return tMsg;
	}
	
	public static JSONObject constructUpdateMsg(){
		
		JSONObject tMsgContent = new JSONObject();	
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
	            RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST,
	            RenHaiDefinitions.RENHAI_MSGID_APPDATASYNCREQUEST);
		
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			// Construct the dataUpdate field
			JSONObject tDataUpdate = new JSONObject();
			JSONObject tDevice2  = new JSONObject();
			JSONObject tDevCard  = new JSONObject();
			JSONObject tProfile2 = new JSONObject();
			JSONObject tIntCard2 = new JSONObject();
			JSONObject tImpCard2 = new JSONObject();
			
			int tMyIntLabelNum = RenHaiInfo.InterestLabel.getMyIntLabelNum(); 
			if( tMyIntLabelNum > 0)
			{
				tIntCard2.put(MSG_APPSYNCREQ_INTCARDID, RenHaiInfo.Profile.interestCardId);
				JSONArray tIntCardLabelList = new JSONArray();
				for(int i=0; i<tMyIntLabelNum; i++)
				{
					InterestLabelMap tIntLabelMap = RenHaiInfo.InterestLabel.getMyIntLabel(i);
					
					//if(true == tIntLabelMap.getNewlyCreatedFlag())
					//{
						JSONObject tIntCardLabelMap = new JSONObject();
						tIntCardLabelMap.put(MSG_APPSYNCREQ_GLBINTLABELID, JSONNULL);
						tIntCardLabelMap.put(MSG_APPSYNCREQ_GLBMTCHCOUNT, JSONNULL);
						tIntCardLabelMap.put(MSG_APPSYNCREQ_LABELORDER, tIntLabelMap.getLabelOrder());
						tIntCardLabelMap.put(MSG_APPSYNCREQ_MATCHCOUNT, JSONNULL);
						tIntCardLabelMap.put(MSG_APPSYNCREQ_VALIDFLAG, JSONNULL);
						tIntCardLabelMap.put(MSG_APPSYNCREQ_INTLABELNAME, tIntLabelMap.getIntLabelName());
						tIntCardLabelList.put(tIntCardLabelMap);
					//}
					/*else{
						tIntCardLabelMap.put(MSG_APPSYNCREQ_GLBINTLABELID, tIntLabelMap.getGlobalIntLabelId());
						tIntCardLabelMap.put(MSG_APPSYNCREQ_GLBMTCHCOUNT, tIntLabelMap.getGlobalMatchCount());
						tIntCardLabelMap.put(MSG_APPSYNCREQ_LABELORDER, tIntLabelMap.getLabelOrder());
						tIntCardLabelMap.put(MSG_APPSYNCREQ_MATCHCOUNT, tIntLabelMap.getMatchCount());
						tIntCardLabelMap.put(MSG_APPSYNCREQ_VALIDFLAG, tIntLabelMap.getValidFlag());
					}*/
					
				}
				tIntCard2.put(MSG_APPSYNCREQ_INTLBLLIST, tIntCardLabelList);
			}
			tDevCard.put(MSG_APPSYNCREQ_DEVMODEL, RenHaiInfo.DeviceCard.getDeviceModel());
			tDevCard.put(MSG_APPSYNCREQ_OSVERSION, RenHaiInfo.DeviceCard.getOsVersion());
			tDevCard.put(MSG_APPSYNCREQ_APPVERSION, RenHaiInfo.DeviceCard.getAppVersion());
			tDevCard.put(MSG_APPSYNCREQ_LOCATION, RenHaiInfo.DeviceCard.getLocation());
			tDevCard.put(MSG_APPSYNCREQ_ISJAILED, RenHaiInfo.DeviceCard.getJailedStatus());
			
			tProfile2.put(MSG_APPSYNCREQ_INTCARD, tIntCard2);
			tProfile2.put(MSG_APPSYNCREQ_IMPCARD, tImpCard2);
			tDevice2.put(MSG_APPSYNCREQ_DEVID, RenHaiInfo.getDeviceId());
			tDevice2.put(MSG_APPSYNCREQ_DEVSN, RenHaiInfo.getDeviceSn());
			tDevice2.put(MSG_APPSYNCREQ_DEVCARD, tDevCard);
			tDevice2.put(MSG_APPSYNCREQ_PROFILE, tProfile2);
			tDataUpdate.put(MSG_APPSYNCREQ_DEV, tDevice2);
			tMsgBodyContent.put(MSG_APPSYNCREQ_DATAUPDATE, tDataUpdate);

			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing AppDataSyncRequest(Update): "+tMsgContent.toString());
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
						              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);						
						
		} catch (JSONException e) {
			mlog.error("Failed to construct AppDataSyncRequest!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt AppDataSyncRequest!", e);
		}				
		return tMsg;
	}
	
	public static JSONObject constructMsg(){
		
		JSONObject tMsgContent = new JSONObject();	
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
	            RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST,
	            RenHaiDefinitions.RENHAI_MSGID_APPDATASYNCREQUEST);
						
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			// Construct the dataQuery field
			JSONObject tDataQuery  = new JSONObject();
			JSONObject tDevice     = new JSONObject();
			JSONObject tDeviceCard = new JSONObject();
			JSONObject tProfile    = new JSONObject();
			JSONObject tIntCard    = new JSONObject();
			JSONObject tImpCard    = new JSONObject();						
			
			tDevice.put(MSG_APPSYNCREQ_DEVID, JSONNULL);
			tDeviceCard.put(MSG_APPSYNCREQ_DEVCARDID, JSONNULL);
			tDeviceCard.put(MSG_APPSYNCREQ_RGSTTIME, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_PROFILEID, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_SVRSTAT, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_UNBANDATE, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_LSTACTTIME, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_CREATETIME, JSONNULL);
			tProfile.put(MSG_APPSYNCREQ_ACTIVE, JSONNULL);
			
			tIntCard.put(MSG_APPSYNCREQ_INTCARDID, JSONNULL);
			tIntCard.put(MSG_APPSYNCREQ_INTLBLLIST, 9);			
			tProfile.put(MSG_APPSYNCREQ_INTCARD, tIntCard);
			tProfile.put(MSG_APPSYNCREQ_IMPCARD, JSONNULL);
			tDevice.put(MSG_APPSYNCREQ_DEVSN, JSONNULL);			
			tDevice.put(MSG_APPSYNCREQ_DEVCARD, tDeviceCard);
			tDevice.put(MSG_APPSYNCREQ_PROFILE, tProfile);
			tDataQuery.put(MSG_APPSYNCREQ_DEV, tDevice);
			tMsgBodyContent.put(MSG_APPSYNCREQ_QUERY, tDataQuery);
			
			// Construct the dataUpdate field
			JSONObject tDataUpdate = new JSONObject();
			JSONObject tDevice2  = new JSONObject();
			JSONObject tDevCard  = new JSONObject();
			JSONObject tProfile2 = new JSONObject();
			JSONObject tIntCard2 = new JSONObject();
			JSONObject tImpCard2 = new JSONObject();								
			
			tDevCard.put(MSG_APPSYNCREQ_DEVMODEL, RenHaiInfo.DeviceCard.getDeviceModel());
			tDevCard.put(MSG_APPSYNCREQ_OSVERSION, RenHaiInfo.DeviceCard.getOsVersion());
			tDevCard.put(MSG_APPSYNCREQ_APPVERSION, RenHaiInfo.DeviceCard.getAppVersion());
			tDevCard.put(MSG_APPSYNCREQ_LOCATION, RenHaiInfo.DeviceCard.getLocation());
			tDevCard.put(MSG_APPSYNCREQ_ISJAILED, RenHaiInfo.DeviceCard.getJailedStatus());
			
			//tProfile2.put(MSG_APPSYNCREQ_INTCARD, tIntCard2);
			//tProfile2.put(MSG_APPSYNCREQ_IMPCARD, tImpCard2);
			tDevice2.put(MSG_APPSYNCREQ_DEVID, RenHaiInfo.getDeviceId());
			tDevice2.put(MSG_APPSYNCREQ_DEVSN, RenHaiInfo.getDeviceSn());
			tDevice2.put(MSG_APPSYNCREQ_DEVCARD, tDevCard);
			tDevice2.put(MSG_APPSYNCREQ_PROFILE, tProfile2);
			tDataUpdate.put(MSG_APPSYNCREQ_DEV, tDevice2);
			tMsgBodyContent.put(MSG_APPSYNCREQ_DATAUPDATE, tDataUpdate);

			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing AppDataSyncRequest: "+tMsgContent.toString());
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
						              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);						
						
		} catch (JSONException e) {
			mlog.error("Failed to construct AppDataSyncRequest!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt AppDataSyncRequest!", e);
		}				
		return tMsg;
	}

}

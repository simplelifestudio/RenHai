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
import com.simplelife.renhai.android.RenHaiInfo;
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
	
	public static JSONObject constructAppDataReqMsg(){
		
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
			if(true == RenHaiInfo.isAppDataSyncronized())
			{
				tDevice.put(MSG_APPSYNCREQ_DEVID, RenHaiInfo.getDeviceId());
				tProfile.put(MSG_APPSYNCREQ_PROFILEID, RenHaiInfo.Profile.getProfileId());
				tProfile.put(MSG_APPSYNCREQ_SVRSTAT, RenHaiInfo.Profile.getServiceStatus());
				tProfile.put(MSG_APPSYNCREQ_UNBANDATE, RenHaiInfo.Profile.getUnbanDate());
				tProfile.put(MSG_APPSYNCREQ_LSTACTTIME, RenHaiInfo.Profile.getLastActiveTime());
				tProfile.put(MSG_APPSYNCREQ_CREATETIME, RenHaiInfo.Profile.getCreateTime());
				tProfile.put(MSG_APPSYNCREQ_ACTIVE, RenHaiInfo.Profile.getActive());
				tIntCard.put(MSG_APPSYNCREQ_INTCARDID, RenHaiInfo.Profile.getInterestCardId());
				//tIntCard.put(MSG_APPSYNCREQ_INTLBLLIST, value);
				tImpCard.put(MSG_APPSYNCREQ_IMPCARDID, RenHaiInfo.Profile.getImpressCardId());
				tImpCard.put(MSG_APPSYNCREQ_CHATTCOUNT, RenHaiInfo.Profile.getChatTotalCount());
				tImpCard.put(MSG_APPSYNCREQ_CHATTDURA, RenHaiInfo.Profile.getChatTotalDuration());
				tImpCard.put(MSG_APPSYNCREQ_CHATLOSS, RenHaiInfo.Profile.getChatLossCount());
				//tImpCard.put(MSG_APPSYNCREQ_ASSLABELLIST, value);
				//tImpCard.put(MSG_APPSYNCREQ_IMPLABELLIST, value);
			}
			else{
				tDevice.put(MSG_APPSYNCREQ_DEVID, null);
				tProfile.put(MSG_APPSYNCREQ_PROFILEID, null);
				tProfile.put(MSG_APPSYNCREQ_SVRSTAT, null);
				tProfile.put(MSG_APPSYNCREQ_UNBANDATE, null);
				tProfile.put(MSG_APPSYNCREQ_LSTACTTIME, null);
				tProfile.put(MSG_APPSYNCREQ_CREATETIME, null);
				tProfile.put(MSG_APPSYNCREQ_ACTIVE, null);
				tIntCard.put(MSG_APPSYNCREQ_INTCARDID, null);
				tIntCard.put(MSG_APPSYNCREQ_INTLBLLIST, null);
				tImpCard.put(MSG_APPSYNCREQ_IMPCARDID, null);
				tImpCard.put(MSG_APPSYNCREQ_CHATTCOUNT, null);
				tImpCard.put(MSG_APPSYNCREQ_CHATTDURA, null);
				tImpCard.put(MSG_APPSYNCREQ_CHATLOSS, null);
				tImpCard.put(MSG_APPSYNCREQ_ASSLABELLIST, null);
				tImpCard.put(MSG_APPSYNCREQ_IMPLABELLIST, null);				
			}
			tProfile.put(MSG_APPSYNCREQ_INTCARD, tIntCard);
			tProfile.put(MSG_APPSYNCREQ_IMPCARD, tImpCard);
			tDevice.put(MSG_APPSYNCREQ_DEVSN, RenHaiInfo.getDeviceSn());			
			tDevice.put(MSG_APPSYNCREQ_DEVCARD, tDeviceCard);
			tDevice.put(MSG_APPSYNCREQ_PROFILE, tProfile);
			tDataQuery.put(MSG_APPSYNCREQ_DEV, tDevice);
			tMsgBodyContent.put(MSG_APPSYNCREQ_QUERY, tDataQuery);
			
			// Construct the dataUpdate field
			
			
			
			
			
			
			
			
			tMsgBodyContent.put("content","Hello Server!");
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing ProxyDataSyncRequest: "+tMsgContent.toString());
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
						              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);						
						
		} catch (JSONException e) {
			mlog.error("Failed to construct AlohaRequest!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt AlohaRequest!", e);
		}		
		
		return tMsg;
	}

}

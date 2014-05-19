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

import com.simplelife.renhai.android.RenHaiDefinitions;

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

		JSONObject tDataQuery = new JSONObject();
		JSONObject tDataUpdate = new JSONObject();
		//JSONObject tQuery
		
		if(inBody.has(MSG_APPSYNCRESP_QUERY))
			try {
				tDataQuery = inBody.getJSONObject(MSG_APPSYNCRESP_QUERY);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;
		
	}

}

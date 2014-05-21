/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgServerDataSyncReq.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiMsgServerDataSyncReq extends RenHaiMsg{
	
	public static String MSG_SVRSYNCREQ_DEVCOUNT = "deviceCount";
	public static String MSG_SVRSYNCREQ_ONLINE   = "online";
	public static String MSG_SVRSYNCREQ_RANDOM   = "random";
	public static String MSG_SVRSYNCREQ_INTEREST = "interest";
	public static String MSG_SVRSYNCREQ_CHAT     = "chat";
	public static String MSG_SVRSYNCREQ_RANDOMCHAT   = "randomChat";
	public static String MSG_SVRSYNCREQ_INTERESTCHAT = "interestChat";
	public static String MSG_SVRSYNCREQ_MGTDATA  = "managementData";
	
	public static String MSG_SVRSYNCREQ_DEVCAPA = "deviceCapacity";
	
	public static String MSG_SVRSYNCREQ_INTLABELLIST = "interestLabelList";
	public static String MSG_SVRSYNCREQ_CURRENT   = "current";
	public static String MSG_SVRSYNCREQ_HISTORY   = "history";
	public static String MSG_SVRSYNCREQ_STARTTIME = "startTime";
	public static String MSG_SVRSYNCREQ_ENDTIME   = "endTime";
	
	
	public static JSONObject constructMsg(){
		JSONObject tMsgContent = new JSONObject();	
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
	            RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST,
	            RenHaiDefinitions.RENHAI_MSGID_SERVERDATASYNCREQUEST);
		
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			JSONObject tDevCount = new JSONObject();
			JSONObject tDevCapa  = new JSONObject();
			JSONObject tIntLabelList = new JSONObject();
			JSONObject tHistory  = new JSONObject();
			
			tDevCount.put(MSG_SVRSYNCREQ_ONLINE, JSONNULL);
			tDevCount.put(MSG_SVRSYNCREQ_RANDOM, JSONNULL);
			tDevCount.put(MSG_SVRSYNCREQ_INTEREST, JSONNULL);
			tDevCount.put(MSG_SVRSYNCREQ_CHAT, JSONNULL);
			tDevCount.put(MSG_SVRSYNCREQ_RANDOMCHAT, JSONNULL);
			tDevCount.put(MSG_SVRSYNCREQ_INTERESTCHAT, JSONNULL);
			tDevCount.put(MSG_SVRSYNCREQ_MGTDATA, JSONNULL);
			tDevCapa.put(MSG_SVRSYNCREQ_ONLINE, JSONNULL);
			tDevCapa.put(MSG_SVRSYNCREQ_RANDOM, JSONNULL);
			tDevCapa.put(MSG_SVRSYNCREQ_INTEREST, JSONNULL);
			tIntLabelList.put(MSG_SVRSYNCREQ_CURRENT, 10);
			
			// TODO: the startTime and endTime in history object
			// are not utilized currently. Leave them uncapsulated
			tMsgBodyContent.put(MSG_SVRSYNCREQ_DEVCOUNT, tDevCount);
			tMsgBodyContent.put(MSG_SVRSYNCREQ_DEVCAPA, tDevCapa);
			tMsgBodyContent.put(MSG_SVRSYNCREQ_INTLABELLIST, tIntLabelList);
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			
			mlog.info("Constructing ServerDataSyncRequest: "+tMsgContent.toString());
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
						              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);			
		} catch (JSONException e) {
			mlog.error("Failed to construct ServerDataSyncRequest!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt ServerDataSyncRequest!", e);
		}		
		return tMsg;
	}

}

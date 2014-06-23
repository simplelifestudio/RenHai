/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgBusinessSessionReq.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiMsgBusinessSessionReq extends RenHaiMsg{
	
	// Message fields definition
	public static String MSG_BUSINESSSESSIONREQ_SESSIONID      = "businessSessionId";
	public static String MSG_BUSINESSSESSIONREQ_BUSINESSTYPE   = "businessType";
	public static String MSG_BUSINESSSESSIONREQ_OPERATIONTYPE  = "operationType";
	public static String MSG_BUSINESSSESSIONREQ_OPERATIONINFO  = "operationInfo";
	public static String MSG_BUSINESSSESSIONREQ_OPERATIONVALUE = "operationValue";
	
	public static JSONObject constructMsg(){
		JSONObject tMsgContent = new JSONObject();
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
				            RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST,
				            RenHaiDefinitions.RENHAI_MSGID_BUSINESSSESSIONREQUEST);
		
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_SESSIONID, JSONNULL);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_BUSINESSTYPE, "Interest");
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONTYPE, "EnterPool");
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONINFO, JSONNULL);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONVALUE, JSONNULL);
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing BusinessSessionRequest: "+tMsgContent.toString());
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
		              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);
		} catch (JSONException e) {
			mlog.error("Failed to construct BusinessSessionRequest!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt BusinessSessionRequest!", e);
		}		
				
		return tMsg;
	}

}

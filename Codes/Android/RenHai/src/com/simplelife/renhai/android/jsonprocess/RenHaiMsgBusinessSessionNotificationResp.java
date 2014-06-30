/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgBusinessSessionNotificationResp.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.data.BusinessSessionInfo;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiMsgBusinessSessionNotificationResp extends RenHaiMsg{
	
	// Message fields definition
	public static String MSG_BUSINESSSESSIONNOTIFRESP_SESSIONID      = "businessSessionId";
	public static String MSG_BUSINESSSESSIONNOTIFRESP_BUSINESSTYPE   = "businessType";
	public static String MSG_BUSINESSSESSIONNOTIFRESP_OPERATIONTYPE  = "operationType";
	public static String MSG_BUSINESSSESSIONNOTIFRESP_OPERATIONINFO  = "operationInfo";
	public static String MSG_BUSINESSSESSIONNOTIFRESP_OPERATIONVALUE = "operationValue";
	
	public static JSONObject constructMsg(int _businessType, int _operationType, int _operationValue){
		JSONObject tMsgContent = new JSONObject();
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
				            RenHaiDefinitions.RENHAI_MSGTYPE_APPRESPONSE,
				            RenHaiDefinitions.RENHAI_MSGID_BUSINESSSESSIONNOTIFICATIONRESPONSE);
		
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONNOTIFRESP_SESSIONID, BusinessSessionInfo.getBusinessSessionId());
			tMsgBodyContent.put(MSG_BUSINESSSESSIONNOTIFRESP_BUSINESSTYPE, _businessType);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONNOTIFRESP_OPERATIONTYPE, _operationType);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONNOTIFRESP_OPERATIONINFO, JSONNULL);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONNOTIFRESP_OPERATIONVALUE, _operationValue);
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing BusinessSessionNotificationResponse: "+tMsgContent.toString());
			
			RenHaiInfo.BusinessSession.setBusinessType(_businessType);
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
		              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);
		} catch (JSONException e) {
			mlog.error("Failed to construct BusinessSessionNotificationResponse!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt BusinessSessionNotificationResponse!", e);
		}		
				
		return tMsg;
	}

}

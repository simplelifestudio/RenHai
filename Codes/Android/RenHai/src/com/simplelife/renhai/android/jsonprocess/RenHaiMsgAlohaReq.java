/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgAlohaReq.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiMsgAlohaReq extends RenHaiMsg{
	
	public static JSONObject constructMsg(){
		
		JSONObject tMsgContent = new JSONObject();	
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
	            RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST,
	            RenHaiDefinitions.RENHAI_MSGID_ALOHAREQUEST);
						
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			
			tMsgBodyContent.put("content","Hello Server!");
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing AlohaRequest: "+tMsgContent.toString());
			
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

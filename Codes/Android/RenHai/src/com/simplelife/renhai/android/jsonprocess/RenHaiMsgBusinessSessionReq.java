/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgBusinessSessionReq.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiMsgBusinessSessionReq extends RenHaiMsg{
	
	// Message fields definition
	public static String MSG_BUSINESSSESSIONREQ_SESSIONID      = "businessSessionId";
	public static String MSG_BUSINESSSESSIONREQ_BUSINESSTYPE   = "businessType";
	public static String MSG_BUSINESSSESSIONREQ_OPERATIONTYPE  = "operationType";
	public static String MSG_BUSINESSSESSIONREQ_OPERATIONINFO  = "operationInfo";
	public static String MSG_BUSINESSSESSIONREQ_OPERATIONVALUE = "operationValue";
	public static String MSG_BUSINESSSESSIONREQ_CHATMSG = "chatMessage";
	public static String MSG_BUSINESSSESSIONREQ_DEVICE = "device";
	public static String MSG_BUSINESSSESSIONREQ_PROFILE = "profile";
	public static String MSG_BUSINESSSESSIONREQ_IMPCARD = "impressCard";
	public static String MSG_BUSINESSSESSIONREQ_ASSLABELLIST = "assessLabelList";
	public static String MSG_BUSINESSSESSIONREQ_IMPLABELLIST = "impressLabelList";
	public static String MSG_BUSINESSSESSIONREQ_ASSLISTASSCNT = "assessCount";
	public static String MSG_BUSINESSSESSIONREQ_ASSLISTASSEDCNT = "assessedCount";
	public static String MSG_BUSINESSSESSIONREQ_ASSLISTGLBIMPLABELID = "globalImpressLabelId";
	public static String MSG_BUSINESSSESSIONREQ_ASSLISTIMPLABELNAME = "impressLabelName";
	public static String MSG_BUSINESSSESSIONREQ_ASSLISTUPDATETIME = "updateTime";
	
	public static JSONObject constructMsg(int _businessType, int _operationType){
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
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_BUSINESSTYPE, _businessType);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONTYPE, _operationType);			
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONVALUE, JSONNULL);
			if(_operationType == RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_ASSESSANDCONTINUE)
			{
				JSONObject tOperationInfo = new JSONObject();
				JSONObject tDevice = new JSONObject();
				JSONObject tProfile = new JSONObject();
				JSONObject tImpCard = new JSONObject();
				JSONArray tAssessLabelList = new JSONArray();
				JSONArray tImpLabelList = new JSONArray();
				// Assessment construction
				JSONObject tAssessLabel = new JSONObject();
				if(PeerDeviceInfo.AssessResult.getAssessment().equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_HAPPY))
				{
					tAssessLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTIMPLABELNAME, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_HAPPY);
				}
				else if(PeerDeviceInfo.AssessResult.getAssessment().equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_SOSO))
				{
					tAssessLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTIMPLABELNAME, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_SOSO);
				}
				else if(PeerDeviceInfo.AssessResult.getAssessment().equals(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_DISGUSTING))
				{
					tAssessLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTIMPLABELNAME, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_DISGUSTING);
				}
				tAssessLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTGLBIMPLABELID, JSONNULL);
				tAssessLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTASSEDCNT, JSONNULL);
				tAssessLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTUPDATETIME, JSONNULL);
				tAssessLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTASSCNT, JSONNULL);
				tAssessLabelList.put(tAssessLabel);
				
				// Impression construction
				int tImpCount = PeerDeviceInfo.AssessResult.getPeerAssessImpLabelNum();
				for(int i=0; i<tImpCount; i++)
				{
					JSONObject tImpLabel = new JSONObject();
					tImpLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTIMPLABELNAME, 
							     PeerDeviceInfo.AssessResult.getAssessImpLabelMap(i).getImpLabelName());
					tImpLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTGLBIMPLABELID, JSONNULL);
					tImpLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTASSEDCNT, JSONNULL);
					tImpLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTUPDATETIME, JSONNULL);
					tImpLabel.put(MSG_BUSINESSSESSIONREQ_ASSLISTASSCNT, JSONNULL);
					tImpLabelList.put(tImpLabel);
				}
				
				tImpCard.put(MSG_BUSINESSSESSIONREQ_ASSLABELLIST, tAssessLabelList);
				tImpCard.put(MSG_BUSINESSSESSIONREQ_IMPLABELLIST, tImpLabelList);
				tProfile.put(MSG_BUSINESSSESSIONREQ_IMPCARD, tImpCard);
				tDevice.put(MSG_BUSINESSSESSIONREQ_PROFILE, tProfile);
				tOperationInfo.put(MSG_BUSINESSSESSIONREQ_DEVICE, tDevice);
			}else{
				tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONINFO, JSONNULL);
			}			
			
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing BusinessSessionRequest: "+tMsgContent.toString());
			
			RenHaiInfo.BusinessSession.setBusinessType(_businessType);
			
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
	
	public static JSONObject constructMsg(int _businessType, int _operationType, String _operationInfo){
		JSONObject tMsgContent = new JSONObject();
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tOperationInfo    = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
				            RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST,
				            RenHaiDefinitions.RENHAI_MSGID_BUSINESSSESSIONREQUEST);
		
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_SESSIONID, JSONNULL);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_BUSINESSTYPE, _businessType);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONTYPE, _operationType);
			tOperationInfo.put(MSG_BUSINESSSESSIONREQ_CHATMSG, _operationInfo);			
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONINFO, tOperationInfo);
			tMsgBodyContent.put(MSG_BUSINESSSESSIONREQ_OPERATIONVALUE, JSONNULL);
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing BusinessSessionRequest: "+tMsgContent.toString());
			
			RenHaiInfo.BusinessSession.setBusinessType(_businessType);
			
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

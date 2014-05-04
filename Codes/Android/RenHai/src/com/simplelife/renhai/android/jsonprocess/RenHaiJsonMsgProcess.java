/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiJsonMsgProcess.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-25. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiMainPageActivity;
import com.simplelife.renhai.android.timeprocess.RenHaiTimeProcess;
import com.simplelife.renhai.android.utils.RenHaiUtilSeqGenerator;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiJsonMsgProcess {
	
	private static final Logger mlog = Logger.getLogger(RenHaiJsonMsgProcess.class);
	private static String mDeviceSn = "";	
	
	// Message ID
	public static int AlohaRequestId = 100;
	
	// Message fields name
	public static String MSG_ENVELOPE = "jsonEnvelope";
	public static String MSG_HEADER   = "header";
	public static String MSG_BODY     = "body";
	public static String MSG_HEADER_TYPE  = "messageType";
	public static String MSG_HEADER_SN    = "messageSn";
	public static String MSG_HEADER_ID    = "messageId";
	public static String MSG_HEADER_DEVID = "deviceId";
	public static String MSG_HEADER_DEVSN = "deviceSn";
	public static String MSG_HEADER_TIME  = "timeStamp";
	
	// ProxyDataSyncRequest fields definition
	public static String MSG_PROXYREQ_APPVERSION = "appVersion";
	public static String MSG_PROXYRESP_VERSION   = "version";
	public static String MSG_PROXYRESP_BUILD     = "build";
	
	// Length of the random sequence
	public static int LENGTH_OF_MESSAGESN = 16;
	
	public static JSONObject constructMsgHeader(int _msgType, int _msgId){
		JSONObject tMsgHeader = new JSONObject();
		
		RenHaiTimeProcess tTimeHandle = RenHaiTimeProcess.getTimeProcessHandle();
		String tCurrentTime = tTimeHandle.getCurrentTime();
		
		try {
			tMsgHeader.put(MSG_HEADER_TYPE, _msgType);
			tMsgHeader.put(MSG_HEADER_SN, RenHaiUtilSeqGenerator.genRandomSeq(LENGTH_OF_MESSAGESN));
			tMsgHeader.put(MSG_HEADER_ID, _msgId);
			tMsgHeader.put(MSG_HEADER_DEVID, "");
			tMsgHeader.put(MSG_HEADER_DEVSN, mDeviceSn);
			tMsgHeader.put(MSG_HEADER_TIME, tCurrentTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tMsgHeader;

	}
	
	public static JSONObject constructProxyDataSyncRequestMsg(){
		JSONObject tMsgContent = new JSONObject();
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		tMsgHeaderContent = constructMsgHeader(
				            RenHaiDefinitions.RENHAI_MSGTYPE_PROXYREQUEST,
				            RenHaiDefinitions.RENHAI_MSGID_PROXYSYNCREQUEST);
		
		try {
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			JSONObject tAppVersion = new JSONObject();
			tAppVersion.put(MSG_PROXYRESP_VERSION, RenHaiDefinitions.RENHAI_APP_VERSION);
			tAppVersion.put(MSG_PROXYRESP_BUILD, 1234);
			tMsgBodyContent.put(MSG_PROXYREQ_APPVERSION, tAppVersion);
			tMsgContent.put(MSG_BODY, tMsgBodyContent);			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("RenHaiJsonMsgProcess",String.format("ProxyDataSyncRequestMsg: %s", tMsgContent.toString()));
		
		// Encrpt the message content and encode by base64
		try {
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
					              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
		} catch (Exception e) {
			// TODO Auto-generated catch block				
			e.printStackTrace();
		}
		
		Log.i("RenHaiJsonMsgProcess","MsgContent after encoding is"+tMessageAfterEncode);
		
		// Capsulate with the envelope
		try {
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return tMsg;
	}
	
	public static JSONObject constructAlohaRequestMsg(){
		
		JSONObject tMsgContent = new JSONObject();	
		JSONObject tMsgHeaderContent = new JSONObject();
		JSONObject tMsgBodyContent   = new JSONObject();
		JSONObject tMsg = new JSONObject();
		String tMessageAfterEncode = null;
		
		Log.i("RenHaiJsonMsgProcess","Start constructing AlohaReq Msg!");
		RenHaiTimeProcess tTimeHandle = RenHaiTimeProcess.getTimeProcessHandle();
		String tCurrentTime = tTimeHandle.getCurrentTime();
		Log.i("RenHaiJsonMsgProcess","Time is"+tCurrentTime);
				
		try {
			// Construct the message body except the envelope part
			tMsgHeaderContent.put(MSG_HEADER_TYPE, RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST);
			tMsgHeaderContent.put(MSG_HEADER_SN, RenHaiUtilSeqGenerator.genRandomSeq(LENGTH_OF_MESSAGESN));
			tMsgHeaderContent.put(MSG_HEADER_ID, AlohaRequestId);
			tMsgHeaderContent.put(MSG_HEADER_DEVID, "");
			tMsgHeaderContent.put(MSG_HEADER_DEVSN, mDeviceSn);
			tMsgHeaderContent.put(MSG_HEADER_TIME, tCurrentTime);
			tMsgContent.put(MSG_HEADER,tMsgHeaderContent);
			
			tMsgBodyContent.put("content","Hello Server!");
			tMsgContent.put(MSG_BODY, tMsgBodyContent);

			Log.i("RenHaiJsonMsgProcess","MsgContent is"+tMsgContent);
			
			// Encrpt the message content and encode by base64
			try {
				tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
						              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			} catch (Exception e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();
			}
			
			Log.i("RenHaiJsonMsgProcess","MsgContent after encoding is"+tMessageAfterEncode);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);						
						
		} catch (JSONException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return tMsg;
	}
	
	public static String decodeMsg(String inMsg){
		
		JSONObject tInMsg;
		String tMsgDecoded = null;
		String tMsgContentBeforeDecode = null;
		String tMsgContentAfterDecode  = null;
		
		try {
			tInMsg = new JSONObject(inMsg);
			tMsgContentBeforeDecode = (String)tInMsg.get(MSG_ENVELOPE);
			tMsgContentAfterDecode  = SecurityUtils.decryptByDESAndDecodeByBase64(tMsgContentBeforeDecode, 
					                  RenHaiDefinitions.RENHAI_ENCODE_KEY);
		} catch (JSONException e) {
			mlog.error("JSONMessage parse exception", e);
		} catch (Exception e) {
			mlog.error("JSONMessage decode exception", e);
		}

		Log.i("RenHaiJsonMsgProcess","Recevive msg is"+tMsgContentAfterDecode);
		
		
		
		
		
		
		return tMsgContentAfterDecode;
	}
	
	public static void decodeAlohaResponseMsg(String inMsg){
		
		JSONObject tInMsg;
		String tMsgContentBeforeDecode = null;
		String tMsgContentAfterDecode  = null;
		try {
			tInMsg = new JSONObject(inMsg);
			tMsgContentBeforeDecode = (String)tInMsg.get(MSG_ENVELOPE);												
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Decode the message content
		try {
			tMsgContentAfterDecode  = SecurityUtils.decryptByDESAndDecodeByBase64(tMsgContentBeforeDecode, RenHaiDefinitions.RENHAI_ENCODE_KEY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("RenHaiJsonMsgProcess","AlohaResponse msg is"+tMsgContentAfterDecode);
			
	}
	
	public static void storeDeviceSn(String inSn){
		mDeviceSn = inSn;
	}
	

}

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
	
	private final Logger mlog = Logger.getLogger(RenHaiMainPageActivity.class);
	private static String mDeviceSn = "";
	
	// Message Type
	public static int MSGTYPE_UNKNOW = 0;
	public static int MSGTYPE_APPREQUEST = 1;
	public static int MSGTYPE_APPRESPONSE = 2;
	public static int MSGTYPE_SERVERNOTIFICATION = 3;
	public static int MSGTYPE_SERVERRESPONSE = 4;
	public static int MSGTYPE_PROXYREQUEST = 5;
	
	// Message ID
	public static int AlohaRequestId = 100;
	
	// Message fields name
	public static String MSG_ENVELOPE = "jsonEnvelope";
	public static String MSG_HEADER   = "header";
	public static String MSG_BODY     = "body";
	public static String MSG_HEADER_TYPE = "messageType";
	public static String MSG_HEADER_SN   = "messageSn";
	public static String MSG_HEADER_ID   = "messageId";
	public static String MSG_HEADER_DEVID = "deviceId";
	public static String MSG_HEADER_DEVSN = "deviceSn";
	public static String MSG_HEADER_TIME  = "timeStamp";
	
	// Length of the random sequence
	public static int LENGTH_OF_MESSAGESN = 16;
	
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
			tMsgHeaderContent.put(MSG_HEADER_TYPE, MSGTYPE_APPREQUEST);
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

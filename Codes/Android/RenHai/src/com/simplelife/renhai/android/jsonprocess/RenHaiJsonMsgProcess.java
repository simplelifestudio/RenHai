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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiInfo;
import com.simplelife.renhai.android.timeprocess.RenHaiTimeProcess;
import com.simplelife.renhai.android.utils.RenHaiUtilSeqGenerator;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiJsonMsgProcess {
	
	private static final Logger mlog = Logger.getLogger(RenHaiJsonMsgProcess.class);
	private static String mRandomSeq = "";
	
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
	public static String MSG_PROXYREQ_VERSION   = "version";
	public static String MSG_PROXYREQ_BUILD     = "build";
	
	// ProxyDataSuncResponse fields definition
	public static String MSG_PROXYRESP_SERVER = "server";
	public static String MSG_PROXYRESP_ID     = "id";
	public static String MSG_PROXYRESP_STATUS = "status";
	public static String MSG_PROXYRESP_SERVSTAT  = "serviceStatus";
	public static String MSG_PROXYRESP_STATPERD  = "statusPeriod";
	public static String MSG_PROXYRESP_TIMEZONE  = "timeZone";
	public static String MSG_PROXYRESP_BEGINTIME = "beginTime";
	public static String MSG_PROXYRESP_ENDTIME   = "endTime";
	public static String MSG_PROXYRESP_ADDRESS   = "address";
	public static String MSG_PROXYRESP_PROTOCOL  = "protocol";
	public static String MSG_PROXYRESP_IP   = "ip";
	public static String MSG_PROXYRESP_PORT = "port";
	public static String MSG_PROXYRESP_PATH = "path";
	public static String MSG_PROXYRESP_BROADCAST = "broadcast";
	
	// Length of the random sequence
	public static int LENGTH_OF_MESSAGESN = 16;
	
	public static JSONObject constructMsgHeader(int _msgType, int _msgId){
		JSONObject tMsgHeader = new JSONObject();
		
		RenHaiTimeProcess tTimeHandle = RenHaiTimeProcess.getTimeProcessHandle();
		String tCurrentTime = tTimeHandle.getCurrentTime();
		
		// Generate the random sequence and store for further verification
		mRandomSeq = RenHaiUtilSeqGenerator.genRandomSeq(LENGTH_OF_MESSAGESN);
		
		try {
			tMsgHeader.put(MSG_HEADER_TYPE, _msgType);
			tMsgHeader.put(MSG_HEADER_SN, mRandomSeq);
			tMsgHeader.put(MSG_HEADER_ID, _msgId);
			tMsgHeader.put(MSG_HEADER_DEVID, RenHaiInfo.getDeviceId());
			tMsgHeader.put(MSG_HEADER_DEVSN, RenHaiInfo.getDeviceSn());
			tMsgHeader.put(MSG_HEADER_TIME, tCurrentTime);
		} catch (JSONException e) {
			mlog.error("Failed to construct the msg header!",e);
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
			tAppVersion.put(MSG_PROXYREQ_VERSION, RenHaiDefinitions.RENHAI_APP_VERSION);
			tAppVersion.put(MSG_PROXYREQ_BUILD, RenHaiDefinitions.RENHAI_APP_BUILD);
			tMsgBodyContent.put(MSG_PROXYREQ_APPVERSION, tAppVersion);
			tMsgContent.put(MSG_BODY, tMsgBodyContent);
			mlog.info("Constructing ProxyDataSyncRequest: "+tMsgContent.toString());
			
			// Encrpt the message content and encode by base64
			tMessageAfterEncode = SecurityUtils.encryptByDESAndEncodeByBase64(
		              tMsgContent.toString(), RenHaiDefinitions.RENHAI_ENCODE_KEY);
			
			// Capsulate with the envelope
			tMsg.put(MSG_ENVELOPE, tMessageAfterEncode);
		} catch (JSONException e) {
			mlog.error("Failed to construct ProxyDataSyncRequest!", e);
		} catch (Exception e) {
			mlog.error("Failed to encrypt ProxyDataSyncRequest!", e);
		}		
				
		return tMsg;
	}
	
	public static JSONObject constructAlohaRequestMsg(){
		
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
	
	public static int decodeMsg(Context _context, String inMsg){
		
		JSONObject tInMsg;
		JSONObject tMsgHeader;
		JSONObject tMsgBody;
		String tMsgDecoded = null;
		String tMsgContentBeforeDecode = null;
		String tMsgContentAfterDecode  = null;
		String tMsgSn = null;
		String tDeviceSn = null;
		String tTimestmp = null;
		int tMsgType = RenHaiDefinitions.RENHAI_MSGTYPE_UNKNOW;
		int tMsgId = 0;
		int tDeviceId = 0;
				
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

		Log.i("RenHaiJsonMsgProcess","Recevive msg is "+tMsgContentAfterDecode);
		
		try {
			JSONObject tMsgInJson = new JSONObject(tMsgContentAfterDecode);
			tMsgHeader = tMsgInJson.getJSONObject(MSG_HEADER);
			if (tMsgHeader.has(MSG_HEADER_TYPE))
			    tMsgType  = tMsgHeader.getInt(MSG_HEADER_TYPE);			
			
			if ( (tMsgType > RenHaiDefinitions.RENHAI_MSGTYPE_UNKNOW)
               && (tMsgType <= RenHaiDefinitions.RENHAI_MSGTYPE_PROXYRESPONSE))
			{
				if(tMsgHeader.has(MSG_HEADER_ID))
				    tMsgId    = tMsgHeader.getInt(MSG_HEADER_ID);
				if(tMsgHeader.has(MSG_HEADER_DEVID))
				{
					tDeviceId = tMsgHeader.getInt(MSG_HEADER_DEVID);
					RenHaiInfo.storeDeviceId(tDeviceId);
				}
				    
				if(tMsgHeader.has(MSG_HEADER_SN))
				{
				    tMsgSn = tMsgHeader.getString(MSG_HEADER_SN);
				    if(!tMsgSn.equals(mRandomSeq))
				    {
			            Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			            tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		             RenHaiDefinitions.RENHAI_NETWORK_MSS_UNMATCHMSGSN);
			            _context.sendBroadcast(tIntent);			        
			            return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
				    }
				}
				if(tMsgHeader.has(MSG_HEADER_DEVSN))
				{
					tDeviceSn = tMsgHeader.getString(MSG_HEADER_DEVSN);
					if(!tDeviceSn.equals(RenHaiInfo.getDeviceSn()))
					{
				        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
				        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
				        		         RenHaiDefinitions.RENHAI_NETWORK_MSS_UNMATCHDEVICESN);
				        _context.sendBroadcast(tIntent);			        
				        return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
					}
				}				    
				if(tMsgHeader.has(MSG_HEADER_TIME))
				{
					tTimestmp = tMsgHeader.getString(MSG_HEADER_TIME);
					if(!RenHaiInfo.isServerTimeOnFirstMsgRecord())
					{
						RenHaiInfo.storeServerTimeOnFirstMsg(tTimestmp);
					}
				}
				
				// Continue process the message body
				tMsgBody = tMsgInJson.getJSONObject(MSG_BODY);
				switch(tMsgId){
				    case RenHaiDefinitions.RENHAI_MSGID_PROXYSYNCRESPONSE:
				    {
				    	return processProxySyncResqMsgBody(tMsgBody);					
				    }
				    case RenHaiDefinitions.RENHAI_MSGID_ALOHARESPONSE:
				    {
				    	return processAlohaResponse(tMsgBody);
				    }
				    //TODO: add other entries here
				}
								
			}
			else
			{
				mlog.warn("Receive message from server with unknow type!");
			}
		} catch (JSONException e) {
			mlog.error("Failed to decode message", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;
	}
	
	public static int processProxySyncResqMsgBody(JSONObject inBody){
		JSONObject tServerInfo;
		JSONObject tStatus;		
		JSONObject tStatusPeriod;
		JSONObject tAddress;
		
		try {
			tServerInfo = inBody.getJSONObject(MSG_PROXYRESP_SERVER);
			if (tServerInfo.has(MSG_PROXYRESP_STATUS))
			{
				tStatus = tServerInfo.getJSONObject(MSG_PROXYRESP_STATUS);
				if(tStatus.has(MSG_PROXYRESP_SERVSTAT))			
				    RenHaiInfo.ServerAddr.storeServiceStatus(tStatus.getInt(MSG_PROXYRESP_SERVSTAT));
				
				if(tStatus.has(MSG_PROXYRESP_STATPERD))
				{
					tStatusPeriod = tStatus.getJSONObject(MSG_PROXYRESP_STATPERD);
					if(tStatus.has(MSG_PROXYRESP_TIMEZONE))
					    RenHaiInfo.ServerAddr.storeTimeZone(tStatusPeriod.getString(MSG_PROXYRESP_TIMEZONE));
					if(tStatus.has(MSG_PROXYRESP_BEGINTIME))
					    RenHaiInfo.ServerAddr.storeBeginTime(tStatusPeriod.getString(MSG_PROXYRESP_BEGINTIME));
					if(tStatus.has(MSG_PROXYRESP_ENDTIME))
					    RenHaiInfo.ServerAddr.storeEndTime(tStatusPeriod.getString(MSG_PROXYRESP_ENDTIME));
				}
				
				if(tServerInfo.has(MSG_PROXYRESP_ADDRESS))
				{
					tAddress = tServerInfo.getJSONObject(MSG_PROXYRESP_ADDRESS);
					if(tAddress.has(MSG_PROXYRESP_PROTOCOL))
					    RenHaiInfo.ServerAddr.storeProtocol(tAddress.getString(MSG_PROXYRESP_PROTOCOL));
					if(tAddress.has(MSG_PROXYRESP_IP))
					     RenHaiInfo.ServerAddr.storeServerIp(tAddress.getString(MSG_PROXYRESP_IP));
					if(tAddress.has(MSG_PROXYRESP_PORT))
					    RenHaiInfo.ServerAddr.storeServerPort(tAddress.getInt(MSG_PROXYRESP_PORT));
					if(tAddress.has(MSG_PROXYRESP_PATH))
					    RenHaiInfo.ServerAddr.storeServerPath(tAddress.getString(MSG_PROXYRESP_PATH));
				}
				
				if(tServerInfo.has(MSG_PROXYRESP_BROADCAST))
				    RenHaiInfo.ServerAddr.storeBroadcastInfo(tServerInfo.getString(MSG_PROXYRESP_BROADCAST));
			}

			return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;						
		} catch (JSONException e) {
			mlog.error("Failed to decode ProxySyncResq body", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
	}
	
	public static int processAlohaResponse(JSONObject inBody){
		
		// Not too much to do here, add if for further necessary
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;		
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
	
	

}

/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsg.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.data.BusinessSessionInfo;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.timeprocess.RenHaiTimeProcess;
import com.simplelife.renhai.android.utils.RenHaiUtilSeqGenerator;
import com.simplelife.renhai.android.utils.SecurityUtils;

public class RenHaiMsg {
	protected static final Logger mlog = Logger.getLogger(RenHaiMsg.class);
	protected static String mRandomSeq = "";
	protected static Object JSONNULL = JSONObject.NULL;
	
	public static String MSG_ENVELOPE = "jsonEnvelope";
	public static String MSG_HEADER   = "header";
	public static String MSG_BODY     = "body";
	public static String MSG_HEADER_TYPE  = "messageType";
	public static String MSG_HEADER_SN    = "messageSn";
	public static String MSG_HEADER_ID    = "messageId";
	public static String MSG_HEADER_DEVID = "deviceId";
	public static String MSG_HEADER_DEVSN = "deviceSn";
	public static String MSG_HEADER_TIME  = "timeStamp";
	
	// Length of the random sequence
	public static int LENGTH_OF_MESSAGESN = 16;
	
	public static JSONObject constructMsgHeader(int _msgType, int _msgId){
		JSONObject tMsgHeader = new JSONObject();
		String tMsgSn = "";
		
		RenHaiTimeProcess tTimeHandle = RenHaiTimeProcess.getTimeProcessHandle();
		String tCurrentTime = tTimeHandle.getCurrentTime();
		
		if (  (_msgType == RenHaiDefinitions.RENHAI_MSGTYPE_APPREQUEST)
		   || (_msgType == RenHaiDefinitions.RENHAI_MSGTYPE_PROXYREQUEST))
		{
			// Generate the random sequence and store for further verification
			mRandomSeq = RenHaiUtilSeqGenerator.genRandomSeq(LENGTH_OF_MESSAGESN);
			tMsgSn = mRandomSeq;
		}else{
			tMsgSn = BusinessSessionInfo.getMsgSn();
		}		
		
		try {
			tMsgHeader.put(MSG_HEADER_TYPE, _msgType);
			tMsgHeader.put(MSG_HEADER_SN, tMsgSn);
			tMsgHeader.put(MSG_HEADER_ID, _msgId);
			tMsgHeader.put(MSG_HEADER_DEVID, (true == RenHaiInfo.isAppDataSyncronized())
					                         ? RenHaiInfo.getDeviceId()
					                         : JSONNULL);
			tMsgHeader.put(MSG_HEADER_DEVSN, RenHaiInfo.getDeviceSn());
			tMsgHeader.put(MSG_HEADER_TIME, tCurrentTime);
		} catch (JSONException e) {
			mlog.error("Failed to construct the msg header!",e);
		}
		return tMsgHeader;

	}
	
	public static int decodeMsg(Context _context, String inMsg){
		
		JSONObject tInMsg;
		JSONObject tMsgHeader;
		JSONObject tMsgBody;
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

		mlog.info("Recevive msg is "+tMsgContentAfterDecode);
		
		try {
			JSONObject tMsgInJson = new JSONObject(tMsgContentAfterDecode);
			tMsgHeader = tMsgInJson.getJSONObject(MSG_HEADER);
			
			if(tMsgHeader.has(MSG_HEADER_DEVID))
			{
				// TODO: Do we need to verify the device id to make sure the msg is correct?
				tDeviceId = tMsgHeader.getInt(MSG_HEADER_DEVID);
				RenHaiInfo.storeDeviceId(tDeviceId);
			}
			
			// Verify device sn
			if(tMsgHeader.has(MSG_HEADER_DEVSN))
			{
				tDeviceSn = tMsgHeader.getString(MSG_HEADER_DEVSN);
				if(!tDeviceSn.equals(RenHaiInfo.getDeviceSn()))
				{
			        mlog.warn("Receive message with unmatch device SN, received device SN: "+tDeviceSn+", my SN: "+RenHaiInfo.getDeviceSn());
					Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		         RenHaiDefinitions.RENHAI_NETWORK_MSS_UNMATCHDEVICESN);
			        _context.sendBroadcast(tIntent);			        
			        return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
				}
			}
			
			if (tMsgHeader.has(MSG_HEADER_TYPE))
			    tMsgType  = tMsgHeader.getInt(MSG_HEADER_TYPE);				
					
			if(tMsgHeader.has(MSG_HEADER_ID))
			    tMsgId    = tMsgHeader.getInt(MSG_HEADER_ID);

			// TODO: find a way to process the time
			if(tMsgHeader.has(MSG_HEADER_TIME))
			{
				tTimestmp = tMsgHeader.getString(MSG_HEADER_TIME);
				if(!RenHaiInfo.isServerTimeOnFirstMsgRecord())
				{
					RenHaiInfo.storeServerTimeOnFirstMsg(tTimestmp);
				}
			}
			
			// Verify the message SN
			if(tMsgHeader.has(MSG_HEADER_SN))
			{
			    tMsgSn = tMsgHeader.getString(MSG_HEADER_SN);
			    
			    if (  (tMsgType == RenHaiDefinitions.RENHAI_MSGTYPE_SERVERRESPONSE)
			       || (tMsgType == RenHaiDefinitions.RENHAI_MSGTYPE_PROXYRESPONSE))
			    {
			    	if(!tMsgSn.equals(mRandomSeq))
				    {
			    		mlog.warn("Receive message with unmatch message SN, received message SN: "+tMsgSn+", my SN: "+mRandomSeq);
			    		Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			            tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		             RenHaiDefinitions.RENHAI_NETWORK_MSS_UNMATCHMSGSN);
			            _context.sendBroadcast(tIntent);			        
			            return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
				    }
			    }
			    else
			    {
			    	BusinessSessionInfo.setMsgSn(tMsgSn);
			    }
			    
			}
				
			// Continue process the message body
			tMsgBody = tMsgInJson.getJSONObject(MSG_BODY);
			switch(tMsgId){
			    case RenHaiDefinitions.RENHAI_MSGID_PROXYSYNCRESPONSE:
			    {
			    	return RenHaiMsgProxyDataSyncResp.parseMsg(tMsgBody);					
			    }
			    case RenHaiDefinitions.RENHAI_MSGID_ALOHARESPONSE:
			    {
			    	return RenHaiMsgAlohaResp.parseMsg(_context, tMsgBody);
			    }
			    case RenHaiDefinitions.RENHAI_MSGID_APPDATASYNCRESPONSE:
			    {
			    	return RenHaiMsgAppDataSyncResp.parseMsg(_context,tMsgBody);
			    }
			    case RenHaiDefinitions.RENHAI_MSGID_SERVERDATASYNCRESPONSE:
			    {
			    	return RenHaiMsgServerDataSyncResp.parseMsg(_context, tMsgBody);
			    }
			    case RenHaiDefinitions.RENHAI_MSGID_BUSINESSSESSIONRESPONSE:
			    {
			    	return RenHaiMsgBusinessSessionResp.parseMsg(_context, tMsgBody);
			    }
			    case RenHaiDefinitions.RENHAI_MSGID_BUSINESSSESSIONNOTIFICATION:
			    {
			    	return RenHaiMsgBusinessSessionNotification.parseMsg(_context, tMsgBody);
			    }
			    
			    //TODO: add other entries here
			}
								
		} catch (JSONException e) {
			mlog.error("Failed to decode message", e);
			return RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		}
		
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;
	}
	

}

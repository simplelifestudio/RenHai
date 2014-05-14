/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMsgAlohaResp.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.jsonprocess;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.simplelife.renhai.android.RenHaiDefinitions;

public class RenHaiMsgAlohaResp extends RenHaiMsg{
	
	public static int parseMsg(Context _context, JSONObject inBody){
		
		// Not too much to do here, add if for further necessary
        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
        		         RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_ALOHARESP);
        _context.sendBroadcast(tIntent);
		return RenHaiDefinitions.RENHAI_FUNC_STATUS_OK;		
	}

}

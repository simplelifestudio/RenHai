/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiHttpProcess.java
 *  RenHai
 *
 *  Created by Chris Li on 14-5-3. 
 */
package com.simplelife.renhai.android.networkprocess;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.jsonprocess.RenHaiJsonMsgProcess;

public class RenHaiHttpProcess {
	
	public static String sendProxyDataSyncRequest(Context _context) throws JSONException, ClientProtocolException, IOException {
        
		// Prepare network parameters
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    new BasicResponseHandler();
	    HttpPost postMethod = new HttpPost(RenHaiDefinitions.RENHAI_SERVER_PROXYADDR);
	    postMethod.addHeader("Content-Type", "application/json");
	    
	    // Create Aloha Message
		JSONObject tMsg = RenHaiJsonMsgProcess.constructProxyDataSyncRequestMsg();
		
	    // Send the request
		postMethod.setEntity(new StringEntity(tMsg.toString()));
		
		// Retrieve the response
		HttpResponse response = httpClient.execute(postMethod);
		
		int tStatusCode = response.getStatusLine().getStatusCode();
		
	    // Parse the response
		if (HttpStatus.SC_OK != tStatusCode)
		{
	        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
	        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
	        		         RenHaiDefinitions.RENHAI_NETWORK_HTTP_COMM_ERROR);
	        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_HTTPRESPSTATUS, tStatusCode);
	        _context.sendBroadcast(tIntent);
	        Log.i("RenHaiHttpProcess","http error code: " + tStatusCode);
	        return null;
		}
		else
		{
			String retSrc = EntityUtils.toString(response.getEntity());			
			String tMsgDecoded = RenHaiJsonMsgProcess.decodeMsg(retSrc);
			return tMsgDecoded;
		}				
	
	}

}

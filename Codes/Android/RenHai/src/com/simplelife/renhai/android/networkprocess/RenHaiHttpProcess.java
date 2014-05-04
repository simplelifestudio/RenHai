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

import android.util.Log;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.jsonprocess.RenHaiJsonMsgProcess;

public class RenHaiHttpProcess {
	
	public static String sendProxyDataSyncRequest() throws JSONException, ClientProtocolException, IOException {
        
		// Prepare network parameters
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    new BasicResponseHandler();
	    HttpPost postMethod = new HttpPost(RenHaiDefinitions.RENHAI_SERVER_PROXYADDR);
	    
	    // Create Aloha Message
		JSONObject tMsg = RenHaiJsonMsgProcess.constructProxyDataSyncRequestMsg();
		
	    // Send the request
		postMethod.setEntity(new StringEntity(tMsg.toString()));
		
		// Retrieve the response
		HttpResponse response = httpClient.execute(postMethod);
		
	    // Parse the response
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String retSrc = EntityUtils.toString(response.getEntity());
			Log.i("return :", retSrc);
			JSONObject result = new JSONObject( retSrc);  
			String token = (String) result.get("id");  
		    Log.i("response :", token);	
		    
		    return token;
		}
		else
		{
			return null;
		}
	
	}

}

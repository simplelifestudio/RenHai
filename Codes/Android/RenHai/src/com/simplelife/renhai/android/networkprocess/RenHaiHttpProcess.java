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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.simplelife.renhai.android.RenHaiDefinitions;

public class RenHaiHttpProcess {
	
	public static String sendProxyDataSyncRequest() throws JSONException, ClientProtocolException, IOException {
        
		// Prepare network parameters
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    new BasicResponseHandler();
	    HttpPost postMethod = new HttpPost(RenHaiDefinitions.RENHAI_SERVER_PROXYADDR);
	    
	    // Create Aloha Message
		JSONObject paramList = new JSONObject();
		
	
	}

}

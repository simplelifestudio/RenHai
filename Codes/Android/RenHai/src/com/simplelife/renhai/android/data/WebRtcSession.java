/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  WebRtcSession.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android.data;

public class WebRtcSession {
	public static String mApiKey;
	public static String mSessionId;
	public static String mToken;
	
	public static void setApiKey(String _key){
		mApiKey = _key;
	}
	
	public static void setSessionId(String _id){
		mSessionId = _id;
	}
	
	public static void setToken(String _token){
		mToken = _token;
	}
	
	public static String getApiKey(){
		return mApiKey;
	}
	
	public static String getSessionId(){
		return mSessionId;
	}
	
	public static String getToken(){
		return mToken;
	}
	

}

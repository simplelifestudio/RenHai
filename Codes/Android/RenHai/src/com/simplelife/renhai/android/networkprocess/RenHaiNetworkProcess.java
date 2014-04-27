/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiNetworkProcess.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-25. 
 */
package com.simplelife.renhai.android.networkprocess;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import android.util.Log;

import com.simplelife.renhai.android.RenHaiMainPageActivity;
import com.simplelife.renhai.android.jsonprocess.RenHaiJsonMsgProcess;
import com.simplelife.renhai.android.utils.WebSocketClient;

public class RenHaiNetworkProcess {
	
	private static RenHaiNetworkProcess mInstance = null;
	private WebSocketClient mWebsocketClient;
	private final Logger mlog = Logger.getLogger(RenHaiMainPageActivity.class);
	public static String TAG = "RenHaiNetworkProcess";	
	
	public RenHaiNetworkProcess(){
		mlog.info("Network process is starting!");
		
		List<BasicNameValuePair> extraHeaders = Arrays.asList(
			    new BasicNameValuePair("Cookie","session=abcd")
			);

		mWebsocketClient = new WebSocketClient(URI.create("ws://192.81.135.31:80/renhai/websocket"), new WebSocketClient.Listener() {
			    @Override
			    public void onConnect() {
			        Log.d(TAG, "Connected!");
			    }

			    @Override
			    public void onMessage(String message) {
			        Log.d(TAG, String.format("Got string message! %s", message));
			        RenHaiJsonMsgProcess.decodeAlohaResponseMsg(message);
			    }

			    @Override
			    public void onMessage(byte[] data) {
			        Log.d(TAG, String.format("Got binary message! %s", data.toString()));
			    }

			    @Override
			    public void onDisconnect(int code, String reason) {
			        Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
			    }

			    @Override
			    public void onError(Exception error) {
			        Log.e(TAG, "Error!", error);
			    }
			}, extraHeaders);

		mWebsocketClient.connect();
	}
	
	public void sendMessage(String inMsg){
		mWebsocketClient.send(inMsg);
	}
	
	public static RenHaiNetworkProcess getNetworkInstance(){
		if (null == mInstance)
			mInstance = new RenHaiNetworkProcess();
		return mInstance;
	}
	
	public static void initNetworkProcess(){
		if (null == mInstance)
			mInstance = new RenHaiNetworkProcess();		
	}

}

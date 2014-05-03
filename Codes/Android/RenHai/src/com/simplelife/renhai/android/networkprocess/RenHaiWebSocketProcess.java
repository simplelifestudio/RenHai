/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiWebSocketProcess.java
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

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiMainPageActivity;
import com.simplelife.renhai.android.RenHaiSplashActivity;
import com.simplelife.renhai.android.jsonprocess.RenHaiJsonMsgProcess;
import com.simplelife.renhai.android.utils.WebSocketClient;

public class RenHaiWebSocketProcess {
	
	private static RenHaiWebSocketProcess mInstance = null;
	private Context mContext;
	private WebSocketClient mWebsocketClient;
	private final Logger mlog = Logger.getLogger(RenHaiMainPageActivity.class);
	public static String TAG = "RenHaiNetworkProcess";	
	
	public RenHaiWebSocketProcess(Context _context){
		mlog.info("Network process is starting!");
		
		mContext = _context;
		
		List<BasicNameValuePair> extraHeaders = Arrays.asList(
			    new BasicNameValuePair("Cookie","session=abcd")
			);

		mWebsocketClient = new WebSocketClient(URI.create("ws://192.81.135.31:80/renhai/websocket"), new WebSocketClient.Listener() {
			    @Override
			    public void onConnect() {
			        Log.d(TAG, "Connected!");
			        
			        // Notify the foreground receiver
			        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		        RenHaiDefinitions.RENHAI_NETWORK_CREATE_SUCCESS);
			        mContext.sendBroadcast(tIntent);
			    }

			    @Override
			    public void onMessage(String message) {
			        Log.d(TAG, String.format("Got string message! %s", message));
			        // 1. Decode the whole message			        
			        RenHaiJsonMsgProcess.decodeMsg(message);
			        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		         RenHaiDefinitions.RENHAI_NETWORK_RECEIVE_MSG);
			        mContext.sendBroadcast(tIntent);
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
			        
			        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		         RenHaiDefinitions.RENHAI_NETWORK_CREATE_ERROR);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_SOCKETERROR, 
			        		         error.toString());
			        mContext.sendBroadcast(tIntent);
			    }
			}, extraHeaders);

		mWebsocketClient.connect();
	}
	
	public void sendMessage(String inMsg){
		mWebsocketClient.send(inMsg);
	}
	
	public static RenHaiWebSocketProcess getNetworkInstance(Context _context){
		if (null == mInstance)
			mInstance = new RenHaiWebSocketProcess(_context);
		return mInstance;
	}
	
	public static void initNetworkProcess(Context _context){
		if (null == mInstance)
			mInstance = new RenHaiWebSocketProcess(_context);		
	}

}

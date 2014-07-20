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
import android.util.Log;

import com.simplelife.renhai.android.RenHaiDefinitions;
import com.simplelife.renhai.android.RenHaiMainPageActivity;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsg;
import com.simplelife.renhai.android.utils.WebSocketClient;

public class RenHaiWebSocketProcess {
	
	private static RenHaiWebSocketProcess mInstance = null;
	private Context mContext;
	private WebSocketClient mWebsocketClient;
	private final Logger mlog = Logger.getLogger(RenHaiWebSocketProcess.class);
	public static String TAG = "RenHaiWebSocketProcess";	
	
	public RenHaiWebSocketProcess(Context _context){
		mlog.info("Network process is starting!");
		
		mContext = _context;
		
		List<BasicNameValuePair> extraHeaders = Arrays.asList(
			    new BasicNameValuePair("Cookie","session=abcd")
			);
		
		String tServerAddr = RenHaiInfo.ServerAddr.getProtocol()
				           + "://"
				           + RenHaiInfo.ServerAddr.getServerIp()
				           + ":"
				           + RenHaiInfo.ServerAddr.getServerPort()
				           + RenHaiInfo.ServerAddr.getServerPath();
		
		//ws://192.81.135.31:80/renhai/websocket
		mWebsocketClient = new WebSocketClient(URI.create(tServerAddr), new WebSocketClient.Listener() {
			    @Override
			    public void onConnect() {
			        Log.d(TAG, "Connected!");
			        
			        // Notify the foreground receiver
			        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		        RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_CREATE_SUCCESS);
			        mContext.sendBroadcast(tIntent);
			    }

			    @Override
			    public void onMessage(String message) {
			        Log.d(TAG, String.format("Got string message! %s", message));		        
			        RenHaiMsg.decodeMsg(mContext,message);
			    }

			    @Override
			    public void onMessage(byte[] data) {
			        Log.d(TAG, String.format("Got binary message! %s", data.toString()));
			    }

			    @Override
			    public void onDisconnect(int code, String reason) {
			        Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));			        
			        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		        RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_DISCONNECT);
			        mContext.sendBroadcast(tIntent);
			    }

			    @Override
			    public void onError(Exception error) {
			        Log.e(TAG, "Error!", error);
			        
			        Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
			        		         RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_CREATE_ERROR);
			        tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_SOCKETERROR, 
			        		         error.toString());
			        mContext.sendBroadcast(tIntent);
			    }
			}, extraHeaders);

		mWebsocketClient.connect();
	}
	
	public void ping(String inMsg){
		mWebsocketClient.ping(inMsg);
	}
	
	public void sendMessage(String inMsg){
		mWebsocketClient.send(inMsg);
	}
	
	public static RenHaiWebSocketProcess getNetworkInstance(Context _context){
		if (null == mInstance)
			mInstance = new RenHaiWebSocketProcess(_context);
		return mInstance;
	}
	
	public static boolean initWebSocketProcess(Context _context){
		if (null == mInstance)
		{
			mInstance = new RenHaiWebSocketProcess(_context);
			return false;
		}else{
			return true;
		}
					
	}
	
	public static void reInitWebSocket(Context _context){
		mInstance = null;
		
		mInstance = new RenHaiWebSocketProcess(_context);
	}

}

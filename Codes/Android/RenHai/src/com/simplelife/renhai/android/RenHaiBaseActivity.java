/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiBaseActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.RenHaiDefinitions.RenHaiAppState;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public abstract class RenHaiBaseActivity extends Activity {
	
	protected RenHaiAppState mAppState = RenHaiAppState.DISCONNECTED;
	protected final Logger mlog = Logger.getLogger(RenHaiBaseActivity.class);
	protected RenHaiWebSocketProcess mWebSocketHandle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	     // Register the broadcast receiver
	 	IntentFilter tFilter = new IntentFilter();
	 	tFilter.addAction(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
	 	registerReceiver(mBroadcastRcver, tFilter); 
	}
	
	@Override
    protected void onDestroy() {  
        super.onDestroy();  
        unregisterReceiver(mBroadcastRcver);  
    }
	
	///////////////////////////////////////////////////////////////////////
	// State management
	///////////////////////////////////////////////////////////////////////
	protected void setMyAppStatus(RenHaiAppState _state) {
		mAppState = _state;
	}
	
	protected RenHaiAppState getMyAppStatus() {
		return mAppState;
	}			
	
	///////////////////////////////////////////////////////////////////////
	// Network message Processing
	///////////////////////////////////////////////////////////////////////	
	protected void onHttpCommSuccess() {
	}
	
	protected void onHttpConnectFailed() {
	}
	
	protected void onWebSocketCreateError() {
	}
	
	protected void onWebSocketCreateSuccess() {
		if(getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		{
			mlog.info("App State transit to Connected from DisConnected.");
			setMyAppStatus(RenHaiAppState.CONNECTED);
		}else if((getMyAppStatus() == RenHaiAppState.CONNECTED)
				||(getMyAppStatus() == RenHaiAppState.APPDATASYNCED)){				
			mlog.info("WebSocket connected under state: "+getMyAppStatus());
		}else{
			mlog.error("WebSocket unexpected connected under state: "+getMyAppStatus());
		}
	}
	
	protected void onWebSocketDisconnect() {
		setMyAppStatus(RenHaiAppState.DISCONNECTED);
	}
	
	protected void onReceiveAlohaResp() {
	}
	
	protected void onReceiveAppSyncResp() {
		if(getMyAppStatus() == RenHaiAppState.CONNECTED)
		{	
			mlog.info("App State transit to AppDataSynced from Connected.");
			setMyAppStatus(RenHaiAppState.APPDATASYNCED);
		}else if((getMyAppStatus() == RenHaiAppState.DISCONNECTED)
				||(getMyAppStatus() == RenHaiAppState.APPDATASYNCED)){				
			// Ignore the state
		}else{
			mlog.error("Unexpected AppDataSyncResp received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveServerSyncResp() {
		if( (getMyAppStatus() == RenHaiAppState.CONNECTED)
		  ||(getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		  ||(getMyAppStatus() == RenHaiAppState.APPDATASYNCED))
		{
			// Ignore the state
		}else{
			mlog.error("Unexpected AppDataSyncResp received under state:"+getMyAppStatus());
		}
		
	}
	
	protected void onReceiveBSRespEnterPool() {
		if(getMyAppStatus() == RenHaiAppState.APPDATASYNCED)
		{	
			mlog.info("App State transit to BusinessChoosed from AppDataSynced.");
			setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		}else if((getMyAppStatus() == RenHaiAppState.DISCONNECTED)
				||(getMyAppStatus() == RenHaiAppState.CHATENDED)
				||(getMyAppStatus() == RenHaiAppState.MATCHSTARTED)){				
			// Ignore the state
		}else{
			mlog.error("Unexpected EnterPoolResp received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespLeavePool() {
		if( (getMyAppStatus() == RenHaiAppState.BUSINESSCHOOSED)
		  ||(getMyAppStatus() == RenHaiAppState.MATCHSTARTED)
		  ||(getMyAppStatus() == RenHaiAppState.SESSIONBOUNDACKED))
		{
			mlog.info("App State transit to AppDataSynced from "+getMyAppStatus());
			setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		}else if(getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected LeavePoolResp received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespAgreeChat() {
		// TODO:leave the object to realize
	}
	
	protected void onReceiveBSRespRejectChat() {
		if(getMyAppStatus() == RenHaiAppState.SESSIONBOUNDACKED)
		{	
			mlog.info("App State transit to BusinessSession from SessionBoundAcked.");
			setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		}else if(getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected RejectChatResp received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespEndChat() {
		if(getMyAppStatus() == RenHaiAppState.CHATALLAGREED)
		{	
			mlog.info("App State transit to ChatEnded from ChatAllAgreed.");
			setMyAppStatus(RenHaiAppState.CHATENDED);
		}else if(getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected EndChatResp received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespAssAndCont() {
		if(getMyAppStatus() == RenHaiAppState.CHATENDED)
		{	
			mlog.info("App State transit to BusinessChoosed from ChatEnded.");
			setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		}else if(getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected AssAndChatResp received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespAssAndQuit() {
		if(getMyAppStatus() == RenHaiAppState.CHATENDED)
		{	
			mlog.info("App State transit to AppDataSynced from ChatEnded.");
			setMyAppStatus(RenHaiAppState.APPDATASYNCED);
		}else if(getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected AssAndQuitResp received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespSessUnBind() {
		//TODO: leave the object to realize
	}
	
	protected void onReceiveBSRespMatchStart() {
		if(getMyAppStatus() == RenHaiAppState.BUSINESSCHOOSED)
		{	
			mlog.info("App State transit to MatchStarted from BusinessChoosed.");
			setMyAppStatus(RenHaiAppState.MATCHSTARTED);
		}else if((getMyAppStatus() == RenHaiAppState.DISCONNECTED)
				||(getMyAppStatus() == RenHaiAppState.CHATENDED)
				||(getMyAppStatus() == RenHaiAppState.APPDATASYNCED)
				||(getMyAppStatus() == RenHaiAppState.MATCHSTARTED)){				
			// Ignore the state
		}else{
			mlog.error("Unexpected MatchStartResp received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespChatMsg() {
	}
	
	protected void onReceiveBSRespFail() {
	}
	
	protected void onReceiveBNSessBinded() {
		if(getMyAppStatus() == RenHaiAppState.MATCHSTARTED)
		{	
			mlog.info("App State transit to MatchStarted from BusinessChoosed.");
			setMyAppStatus(RenHaiAppState.SESSIONBOUNDACKED);
		}else if((getMyAppStatus() == RenHaiAppState.DISCONNECTED)
				||(getMyAppStatus() == RenHaiAppState.CHATENDED)
				||(getMyAppStatus() == RenHaiAppState.APPDATASYNCED)
				||(getMyAppStatus() == RenHaiAppState.BUSINESSCHOOSED)){				
			// Ignore the state
		}else{
			mlog.error("Unexpected SessionBound received under state:"+getMyAppStatus());
		}
	}
	
	protected void onReceiveBNPeerRej() {
		//TODO:leave the object to realize
	}
	
	protected void onReceiveBNPeerAgree() {
	}
	
	protected void onReceiveBNPeerLost() {
	}

	protected void onReceiveBNPeerEndChat() {
	}
	
	protected void onReceiveBNPeerChatMsg() {
	}
	
	protected BroadcastReceiver mBroadcastRcver = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 

            String action = intent.getAction(); 
            if(action.equals(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG)){
            	int tMsgType = intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 0);
            	switch (tMsgType) {
            		// Http proxy communicate messages
	            	case RenHaiDefinitions.RENHAI_NETWORK_HTTP_COMM_SUCESS:
	            	{
	            		mlog.info("Proxy communicate success!");
	            		onHttpCommSuccess();
	            		break;
	            	}
	            	case RenHaiDefinitions.RENHAI_NETWORK_HTTP_COMM_ERROR:
	            	{
	            		mlog.error("Failed to communicate with server proxy, http status code: "
	            		          +intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_HTTPRESPSTATUS, 0));
	            		onHttpConnectFailed();	            		
	            		break;            		
	            	}
	            	// Websocket messages 
	            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_CREATE_SUCCESS:
	        	    {
	        	    	mlog.info("Websocket recreate success!");
	        	    	onWebSocketCreateSuccess();       	    	
	        	        break;
	        	    }
	            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_CREATE_ERROR:
	        	    {
	        	    	mlog.error("Websocket error, error info: "+
	        	                   intent.getStringExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_SOCKETERROR));
	        	    	onWebSocketCreateError();
	        	    	break;
	        	    }
	            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_DISCONNECT:
	            	{
	            		onWebSocketDisconnect();
	            		break;
	            	}
	        	    // Server responses
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_ALOHARESP:
	        	    {
	        	    	onReceiveAlohaResp();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_APPSYNCRESP:
	        	    {
	        	    	onReceiveAppSyncResp();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_SERVERSYNCRESP:
	        	    {
	        	    	onReceiveServerSyncResp();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_ENTERPOOL:
	        	    {
	        	    	onReceiveBSRespEnterPool();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_LEAVEPOOL:
	        	    {
	        	    	onReceiveBSRespLeavePool();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_AGREECHAT:
	        	    {
	        	    	onReceiveBSRespAgreeChat();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_REJCHAT:
	        	    {
	        	    	onReceiveBSRespRejectChat();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_ENDCHAT:
	        	    {
	        	    	onReceiveBSRespEndChat();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_ASSANDCON:
	        	    {
	        	    	onReceiveBSRespAssAndCont();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_ASSANDQUIT:
	        	    {
	        	    	onReceiveBSRespAssAndQuit();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_SESSIONUNBIND:
	        	    {
	        	    	onReceiveBSRespSessUnBind();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_MATCHSTART:
	        	    {
	        	    	onReceiveBSRespMatchStart();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_CHATMSG:
	        	    {
	        	    	onReceiveBSRespChatMsg();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESPFAIL:
	        	    {
	        	    	onReceiveBSRespFail();
	        	    	break;
	        	    }
	        	    // Server notifications
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_SESSIONBINDED:
	        	    {
	        	    	onReceiveBNSessBinded();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_PEERREJECT:
	        	    {
	        	    	onReceiveBNPeerRej();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_PEERAGREE:
	        	    {
	        	    	onReceiveBNPeerAgree();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_PEERLOST:
	        	    {
	        	    	onReceiveBNPeerLost();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_PEERENDCHAT:
	        	    {
	        	    	onReceiveBNPeerEndChat();
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_PEERCHATMSG:
	        	    {
	        	    	onReceiveBNPeerChatMsg();
	        	    	break;
	        	    }
	        	    
	        	    case RenHaiDefinitions.RENHAI_NETWORK_MSS_UNMATCHMSGSN:
	        	    {
	        	    	mlog.error("Receive message with unmatch msgsn!");
	        	    	break;
	        	    }
	        	    case RenHaiDefinitions.RENHAI_NETWORK_MSS_UNMATCHDEVICESN:
	        	    {
	        	    	mlog.error("Receive message with unmatch devicesn!");
	        	    	break;
	            	}
            	}
            	//determineToTransitState(tMsgType);
            }
        } 
    };

}

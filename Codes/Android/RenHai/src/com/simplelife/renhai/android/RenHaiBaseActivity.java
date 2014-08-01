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
import com.simplelife.renhai.android.data.AppStateMgr;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class RenHaiBaseActivity extends FragmentActivity {		
	protected final Logger mlog = Logger.getLogger(RenHaiBaseActivity.class);
	protected RenHaiWebSocketProcess mWebSocketHandle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        	     
	}
	
	@Override
	protected void onStart() {
    	super.onStart();  	
    }
	
	@Override
	protected void onResume() {
    	super.onResume();
    	// Register the broadcast receiver
	 	IntentFilter tFilter = new IntentFilter();
	 	tFilter.addAction(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
	 	registerReceiver(mBroadcastRcver, tFilter); 
    }
	
	@Override
	protected void onPause() {
    	super.onPause();
    	unregisterReceiver(mBroadcastRcver);
    }
	
	@Override
	protected void onStop() {
    	super.onStop();  	
    }
	
	@Override
    protected void onDestroy() {  
        super.onDestroy();  
         
    }			
	
	///////////////////////////////////////////////////////////////////////
	// Network message Processing
	///////////////////////////////////////////////////////////////////////	
	protected void onHttpCommSuccess() {
	}
	
	protected void onHttpConnectFailed() {
	}
	
	protected void onWebSocketCreateError() {
		mlog.info("App State transit to DisConnected from "+AppStateMgr.getMyAppStatus());
		AppStateMgr.setMyAppStatus(RenHaiAppState.DISCONNECTED);
	}
	
	protected void onWebSocketCreateSuccess() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		{
			mlog.info("App State transit to Connected from DisConnected.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.CONNECTED);
		}else if((AppStateMgr.getMyAppStatus() == RenHaiAppState.CONNECTED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.APPDATASYNCED)){				
			mlog.info("WebSocket connected under state: "+AppStateMgr.getMyAppStatus());
		}else{
			mlog.error("WebSocket unexpected connected under state: "+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onWebSocketDisconnect() {
		mlog.info("App State transit to DisConnected from "+AppStateMgr.getMyAppStatus());
		AppStateMgr.setMyAppStatus(RenHaiAppState.DISCONNECTED);
	}
	
	protected void onReceiveAlohaResp() {
	}
	
	protected void onReceiveAppSyncResp() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.CONNECTED)
		{	
			mlog.info("App State transit to AppDataSynced from Connected.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.APPDATASYNCED);
		}else if((AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.APPDATASYNCED)){				
			// Ignore the state
		}else{
			mlog.error("Unexpected AppDataSyncResp received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveServerSyncResp() {
		if( (AppStateMgr.getMyAppStatus() == RenHaiAppState.CONNECTED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.APPDATASYNCED))
		{
			// Ignore the state
		}else{
			mlog.error("Unexpected AppDataSyncResp received under state:"+AppStateMgr.getMyAppStatus());
		}
		
	}
	
	protected void onReceiveBSRespEnterPool() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.APPDATASYNCED)
		{	
			mlog.info("App State transit to BusinessChoosed from AppDataSynced.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		}else if((AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATENDED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.MATCHSTARTED)){				
			// Ignore the state
		}else{
			mlog.error("Unexpected EnterPoolResp received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespLeavePool() {
		if( (AppStateMgr.getMyAppStatus() == RenHaiAppState.BUSINESSCHOOSED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.MATCHSTARTED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.SESSIONBOUNDACKED))
		{
			mlog.info("App State transit to AppDataSynced from "+AppStateMgr.getMyAppStatus());
			AppStateMgr.setMyAppStatus(RenHaiAppState.APPDATASYNCED);
		}else if(AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected LeavePoolResp received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespAgreeChat() {
		// TODO:leave the object to realize
	}
	
	protected void onReceiveBSRespRejectChat() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.SESSIONBOUNDACKED)
		{	
			mlog.info("App State transit to BusinessSession from SessionBoundAcked.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		}else if(AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected RejectChatResp received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespEndChat() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATALLAGREED)
		{	
			mlog.info("App State transit to ChatEnded from ChatAllAgreed.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.CHATENDED);
		}else if(AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected EndChatResp received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespAssAndCont() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATENDED)
		{	
			mlog.info("App State transit to BusinessChoosed from ChatEnded.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		}else if(AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected AssAndChatResp received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespAssAndQuit() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATENDED)
		{	
			mlog.info("App State transit to AppDataSynced from ChatEnded.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.APPDATASYNCED);
		}else if(AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED){				
			// Ignore the state
		}else{
			mlog.error("Unexpected AssAndQuitResp received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespSessUnBind() {
		//TODO: leave the object to realize
	}
	
	protected void onReceiveBSRespMatchStart() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.BUSINESSCHOOSED)
		{	
			mlog.info("App State transit to MatchStarted from BusinessChoosed.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.MATCHSTARTED);
		}else if((AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATENDED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.APPDATASYNCED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.MATCHSTARTED)){				
			// Ignore the state
		}else{
			mlog.error("Unexpected MatchStartResp received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBSRespChatMsg() {
	}
	
	protected void onReceiveBSRespFail() {
	}
	
	protected void onReceiveBNSessBinded() {
		if(AppStateMgr.getMyAppStatus() == RenHaiAppState.MATCHSTARTED)
		{	
			mlog.info("App State transit to MatchStarted from BusinessChoosed.");
			AppStateMgr.setMyAppStatus(RenHaiAppState.SESSIONBOUNDACKED);
		}else if((AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATENDED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.APPDATASYNCED)
				||(AppStateMgr.getMyAppStatus() == RenHaiAppState.BUSINESSCHOOSED)){				
			// Ignore the state
		}else{
			mlog.error("Unexpected SessionBound received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBNPeerRej() {
		if( (AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.SESSIONBOUNDACKED))
		{	
			// Ignore
		}else{
			mlog.error("Unexpected OthersideRejectChat received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBNPeerAgree() {
		if( (AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.SESSIONBOUNDACKED))
		{	
			// Ignore
		}else{
			mlog.error("Unexpected OthersideAgreeChat received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBNPeerLost() {
		if( (AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.SESSIONBOUNDACKED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATALLAGREED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATENDED))
		{	
			// Ignore
		}else{
			mlog.error("Unexpected OthersideLost received under state:"+AppStateMgr.getMyAppStatus());
		}
	}

	protected void onReceiveBNPeerEndChat() {
		if( (AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATALLAGREED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATENDED))
		{	
			// Ignore
		}else{
			mlog.error("Unexpected OthersideEndChat received under state:"+AppStateMgr.getMyAppStatus());
		}
	}
	
	protected void onReceiveBNPeerChatMsg() {
		if( (AppStateMgr.getMyAppStatus() == RenHaiAppState.DISCONNECTED)
		  ||(AppStateMgr.getMyAppStatus() == RenHaiAppState.CHATALLAGREED))
		{	
			// Ignore
		}else{
			mlog.error("Unexpected OthersideChatMsg received under state:"+AppStateMgr.getMyAppStatus());
		}
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

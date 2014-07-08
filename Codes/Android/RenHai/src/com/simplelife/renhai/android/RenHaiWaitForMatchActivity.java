/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiWaitForMatchActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionNotificationResp;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgServerDataSyncReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class RenHaiWaitForMatchActivity extends Activity {
	
	private TextView mCounterText;
	private MyCount mCounter;	
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	private final Logger mlog = Logger.getLogger(RenHaiWaitForMatchActivity.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waitingformatchpage);
		
		mCounterText = (TextView) findViewById(R.id.waitingformatch_counter);
		mCounter = new MyCount(20000, 1000);
		mCounter.start();
		
		// Register the broadcast receiver
		IntentFilter tFilter = new IntentFilter();
		tFilter.addAction(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
		registerReceiver(mBroadcastRcverWaitForMatch, tFilter); 
		
		mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication());
		
		sendMatchStartMessage();
	}
	
	@Override
	public void onDestroy() {  
        super.onDestroy();  
        unregisterReceiver(mBroadcastRcverWaitForMatch);  
    }
	
	private void sendMatchStartMessage(){
		String tBusinessSessionReq = RenHaiMsgBusinessSessionReq.constructMsg(
    			RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
    			RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_MATCHSTART).toString();
    	mWebSocketHandle.sendMessage(tBusinessSessionReq);
	}
	
	
	 class MyCount extends CountDownTimer{
		 long mTimeToWait;
		   
	        public MyCount(long millisInFuture, long countDownInterval) {  
	            super(millisInFuture, countDownInterval);
	            mTimeToWait = millisInFuture;
	        }  
	        @Override  
	        public void onTick(long millisUntilFinished) {  
	        	mCounterText.setText("" + (mTimeToWait - millisUntilFinished) / 1000);  
	        }  
	        @Override  
	        public void onFinish() {  
	        	mCounterText.setText("Time Out!"); 
	        	//Intent tIntent = new Intent(RenHaiWaitForMatchActivity.this, RenHaiMatchingActivity.class);
	    		//startActivity(tIntent);
	    		finish();	        	 
	        }  
	 }
	 
    ///////////////////////////////////////////////////////////////////////
    // Network message process
    ///////////////////////////////////////////////////////////////////////    
    private BroadcastReceiver mBroadcastRcverWaitForMatch = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 

            String action = intent.getAction(); 
            if(action.equals(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG)){
            	int tMsgType = intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 0);
            	switch (tMsgType) {
            	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_MATCHSTART:
            	    {
            	    	mlog.info("Receive match start response!");
            	    	break;
            	    }
            	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_SESSIONBINDED:
            	    {
            	    	mlog.info("Receive session binded message!");
            	    	String tBusinessNotRespMsg = RenHaiMsgBusinessSessionNotificationResp
            	    			                      .constructMsg(RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
            	    			                    		        RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_SESSIONBINDED, 1).toString();
            	    	mWebSocketHandle.sendMessage(tBusinessNotRespMsg);
            	    	PeerDeviceInfo.resetPeerDeviceInfo();
            	    	Intent tIntent = new Intent(RenHaiWaitForMatchActivity.this, RenHaiMatchingActivity.class);
        	    		startActivity(tIntent);
        	    		finish();
            	    	break;
            	    }

            	}            	
            }
        } 
    };

}

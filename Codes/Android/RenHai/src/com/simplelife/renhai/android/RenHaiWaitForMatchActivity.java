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

import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionNotificationResp;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class RenHaiWaitForMatchActivity extends RenHaiBaseActivity {
	
	private TextView mCounterText;
	private MyCount mCounter;	
	private final Logger mlog = Logger.getLogger(RenHaiWaitForMatchActivity.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waitingformatchpage);
		
		mCounterText = (TextView) findViewById(R.id.waitingformatch_counter);
		mCounter = new MyCount(20000, 1000);
		mCounter.start();
		mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication()); 
		sendMatchStartMessage();
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
	@Override
	protected void onReceiveBSRespMatchStart() {
		super.onReceiveBSRespMatchStart();
	}
	
	@Override
	protected void onReceiveBNSessBinded() {
		super.onReceiveBNSessBinded();
		String tBusinessNotRespMsg = RenHaiMsgBusinessSessionNotificationResp
                .constructMsg(RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
              		          RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_SESSIONBINDED, 1).toString();
		mWebSocketHandle.sendMessage(tBusinessNotRespMsg);
		Intent tIntent = new Intent(RenHaiWaitForMatchActivity.this, RenHaiMatchingActivity.class);
		startActivity(tIntent);
		finish();
	}

}

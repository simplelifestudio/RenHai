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

import com.simplelife.renhai.android.RenHaiDefinitions.RenHaiAppState;
import com.simplelife.renhai.android.data.AppStateMgr;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionNotificationResp;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RenHaiWaitForMatchActivity extends RenHaiBaseActivity {
	
	private LinearLayout mBtnCancel;
	private LinearLayout mToBtnLayout;
	private TextView mCounterText;
	private TextView mCounterNote;
	private TextView mBtnRetry;
	private TextView mBtnBack;
	private TextView mTimeOutNote;
	private MyCount mCounter;
	private String mCallerName;
	private final Logger mlog = Logger.getLogger(RenHaiWaitForMatchActivity.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waitingformatchpage);
		
		mBtnCancel = (LinearLayout) findViewById(R.id.waitingformatch_btnlayout);
		mBtnCancel.setOnClickListener(mCancelBtnListener);
		
		mCounterNote = (TextView) findViewById(R.id.waitingformatch_counternote);
		mTimeOutNote = (TextView) findViewById(R.id.waitingformatch_timeoutnote);
		
		mToBtnLayout = (LinearLayout) findViewById(R.id.waitingformatch_tobtnlayout);
		mBtnRetry = (TextView) findViewById(R.id.waitingformatch_btnretry);
		mBtnBack  = (TextView) findViewById(R.id.waitingformatch_btnback);
		mBtnRetry.setOnClickListener(mRetryBtnListener);
		mBtnBack.setOnClickListener(mBackBtnListener);
		
		mCounterText = (TextView) findViewById(R.id.waitingformatch_counter);
		mCounter = new MyCount(22000, 1000);
		mCounter.start();
		mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication()); 
		
		// Retrieve the date info parameter
		Bundle bundle = getIntent().getExtras();
		mCallerName = bundle.getString("caller");
		if(mCallerName.equals("RenHaiStartVedioFragment"))
		{
			sendMatchStartMessage();
		}else if(mCallerName.equals("RenHaiMatchingActivity"))
		{
			// Do nothing here, because we are already under the MATCHSTARTED state
		}		
	}
	
	private View.OnClickListener mCancelBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String tBusinessSessionReq = RenHaiMsgBusinessSessionReq.constructMsg(
	    			RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
	    			RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_LEAVEPOOL).toString();
	    	mWebSocketHandle.sendMessage(tBusinessSessionReq);
	    	finish();
		}
	};
	
	private View.OnClickListener mRetryBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mTimeOutNote.setVisibility(View.GONE);
			mCounterText.setText(R.string.waitformatch_counterstart);
			mCounterText.setVisibility(View.VISIBLE);
        	mCounterNote.setVisibility(View.VISIBLE);
        	mBtnCancel.setVisibility(View.VISIBLE);
        	mToBtnLayout.setVisibility(View.GONE);
        	mCounter.start();
        	// We do not need to send any message here, cause the app
        	// is still inside the match pool        	
		}
	};
	
	private View.OnClickListener mBackBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String tBusinessSessionReq = RenHaiMsgBusinessSessionReq.constructMsg(
	    			RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
	    			RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_LEAVEPOOL).toString();
	    	mWebSocketHandle.sendMessage(tBusinessSessionReq);
	    	finish();
		}
	};
	
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
	        	mTimeOutNote.setVisibility(View.VISIBLE);
	        	mCounterText.setVisibility(View.GONE);
	        	mCounterNote.setVisibility(View.GONE);
	        	mBtnCancel.setVisibility(View.GONE);
	        	mToBtnLayout.setVisibility(View.VISIBLE);        	 
	        }  
	 }
	 
	 private void onWebSocketException() {
		 AlertDialog.Builder builder = new Builder(this);
	    	builder.setTitle(R.string.waitformatch_connectionlost_dialogtitle);

	    	builder.setPositiveButton(R.string.waitformatch_connectionlost_dialogposbtn, 
	    			                  new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					// Return to the mainpage, the network re-connection will be
					// fulfilled in the mainpage
					finish();
				}    		
	    	});

			builder.create().show();
	 }
	 
    ///////////////////////////////////////////////////////////////////////
    // Network message process
    ///////////////////////////////////////////////////////////////////////
	@Override
	protected void onWebSocketDisconnect() {
		super.onWebSocketDisconnect();
		onWebSocketException();
		
	}
	
	@Override
	protected void onWebSocketCreateError() {
		super.onWebSocketCreateError();
		onWebSocketException();
	}
	 
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
	
	@Override
	protected void onReceiveBSRespLeavePool() {
		super.onReceiveBSRespLeavePool();		
	}

}

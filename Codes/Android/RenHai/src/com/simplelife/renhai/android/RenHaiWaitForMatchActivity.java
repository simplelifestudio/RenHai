/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiWaitForMatchActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class RenHaiWaitForMatchActivity extends Activity {
	
	private TextView mCounterText;
	private MyCount mCounter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waitingformatchpage);
		
		mCounterText = (TextView) findViewById(R.id.waitingformatch_counter);
		mCounter = new MyCount(20000, 1000);
		mCounter.start();
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
	        }  
	    } 

}

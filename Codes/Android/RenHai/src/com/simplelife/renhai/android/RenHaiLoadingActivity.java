/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiLoadingActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.jsonprocess.RenHaiJsonMsgProcess;
import com.simplelife.renhai.android.networkprocess.RenHaiNetworkProcess;
import com.simplelife.renhai.android.ui.RenHaiProgressBar;
import com.simplelife.renhai.android.ui.RenHaiProgressBar.Mode;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class RenHaiLoadingActivity extends Activity{
	
	private final Logger mlog = Logger.getLogger(RenHaiSplashActivity.class);
	private static RenHaiProgressBar mProgressBar;

	private static Handler mMsgHandler = new Handler(){ 
	
        @Override  
        public void handleMessage(Message msg) {  
        	switch (msg.what) {
        	    case RenHaiDefinitions.RENHAI_MSGID_ALOHARESPONSE:
        	    	mProgressBar.animation_start(Mode.DETERMINATE);
        	}
        }

	};
	
	public static Handler getLoadingPageMsgHandler(){
		return mMsgHandler;
	}	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        
        mProgressBar = (RenHaiProgressBar)findViewById(R.id.loadingpage_bar);
        
        mProgressBar.animation_config(2, 20);
        int[] tColor = {Color.GREEN, Color.TRANSPARENT};
        mProgressBar.bar_config(20, 0, 20, Color.LTGRAY, tColor);
        
    	// 1.Communicate with the proxy to get the status of server
    	String tAlohaRequestMsg = RenHaiJsonMsgProcess.constructAlohaRequestMsg().toString();
    	RenHaiNetworkProcess tNetHandle = RenHaiNetworkProcess.getNetworkInstance();
    	tNetHandle.sendMessage(tAlohaRequestMsg);  
    }
        

}

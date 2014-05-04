/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiSplashActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android;

import com.simplelife.renhai.android.R;
import com.simplelife.renhai.android.jsonprocess.RenHaiJsonMsgProcess;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.timeprocess.RenHaiTimeProcess;
import com.simplelife.renhai.android.ui.RenHaiProgressBar;
import com.simplelife.renhai.android.ui.RenHaiProgressBar.Mode;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class RenHaiSplashActivity extends Activity {
	
	private SharedPreferences mSharedPrefs;
	
	private final static Logger mlog = Logger.getLogger(RenHaiSplashActivity.class);
	private static RenHaiProgressBar mProgressBar;
	private static TextView mProgressText;
	
	private static final int BACKGROUND_PROCESS_TYPE_INITIAL = 1;
	private static final int BACKGROUND_PROCESS_TYPE_SENDALOHA = 2;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View tStartView = View.inflate(this,R.layout.activity_splash,null);
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(tStartView);
		
        // Retrieve the first progress bar
		mProgressBar  = (RenHaiProgressBar) findViewById(R.id.splash_progressbar);	
		mProgressText = (TextView) findViewById(R.id.splash_progresstext);
		mProgressText.setText(R.string.mainpage_title_preparenetwork);
		mProgressText.setVisibility(View.INVISIBLE);
		
        // Configure the first progress bar
		mProgressBar.animation_config(2, 10);
        int[] tColor = {Color.WHITE, Color.TRANSPARENT};
        mProgressBar.bar_config(2, 0, 0, Color.TRANSPARENT, tColor);		
		
		// Configure fade in and fade out
		AlphaAnimation fadeShow = new AlphaAnimation(0.3f,1.0f);
		fadeShow.setDuration(2000);	
		
		// Register the broadcast receiver
		IntentFilter tFilter = new IntentFilter();
		tFilter.addAction(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
		registerReceiver(mBroadcastRcver, tFilter); 
		
		// Start a background task to process the network communication
        MessageSendingTask tMsgSender = new MessageSendingTask();
        tMsgSender.execute(BACKGROUND_PROCESS_TYPE_INITIAL);
        
        // Start the animation and wait for the communication result
        tStartView.startAnimation(fadeShow);
        
		// Stay for a moments and redirect
		fadeShow.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) {
				//redirectTo();
				// The background process has not finished yet, start the progress bar
				mProgressBar.animation_start(Mode.INDETERMINATE);
				mProgressText.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}			
		});        
	}

    private BroadcastReceiver mBroadcastRcver = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 

            String action = intent.getAction(); 
            if(action.equals(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG)){
            	int tMsgType = intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 0);
            	
            	switch (tMsgType) {
        	    case RenHaiDefinitions.RENHAI_NETWORK_CREATE_SUCCESS:
        	    {
        	    	mlog.info("Websocket create success!");
        	    	mProgressText.setText(R.string.mainpage_title_connectserver);
                	String tAlohaRequestMsg = RenHaiJsonMsgProcess.constructAlohaRequestMsg().toString();
                	RenHaiWebSocketProcess tNetHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication());
                	tNetHandle.sendMessage(tAlohaRequestMsg); 
        	        break;
        	    }
        	        
        	    case RenHaiDefinitions.RENHAI_NETWORK_CREATE_ERROR:
        	    {
        	    	mlog.info("Websocket error, error info: "+
        	                   intent.getStringExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_SOCKETERROR));
        	    	// Make a toast here to let the user to determine to enter the app offline or exit
        	    	break;
        	    }
        	    
        	    case RenHaiDefinitions.RENHAI_NETWORK_RECEIVE_MSG:
        	    {
        	    	mlog.info("Websocket receive message!");
        	    	mProgressText.setText(R.string.mainpage_title_syncserver);
        	    	processMessage();
        	    	break;
        	    }
        	}
            	
            }
        } 
    }; 
    
    @Override
    protected void onDestroy() {  
        super.onDestroy();  
        unregisterReceiver(mBroadcastRcver);  
    }  			
	
	private static void processMessage(){
		
	}
	
	private void redirectTo(){       
		Intent intent;
		/*
		if(isFirstStart())*/
		{
			updateFirstStartFlag(false);
			//intent = new Intent(this, RenHaiProtocalActivity.class);			
			intent = new Intent(this, RenHaiLoadingActivity.class);
		    Bundle bundle = new Bundle();
		    bundle.putString("caller", "RenHaiSplashActivity");
		    intent.putExtras(bundle);
		}
		/*else*/
		{

			//intent = new Intent(this, RenHaiMainPageActivity.class);			
		}		
				
		startActivity(intent);
		finish();
	}
	
    private boolean isFirstStart(){
    	
    	// Retrieve the seeds info status by date via the shared preference file
    	return mSharedPrefs.getBoolean("isfirststart",true);    	
    }
    
    private void updateFirstStartFlag(Boolean _inTag){
    	
    	SharedPreferences.Editor editor = mSharedPrefs.edit();
    	editor.putBoolean("isfirststart", _inTag);
    	editor.commit();    	
    } 
    
    public void configureLogger() {
        final LogConfigurator logConfigurator = new LogConfigurator();
                
        logConfigurator.setFileName(Environment.getExternalStorageDirectory() 
        		                   + File.separator
        		                   + RenHaiDefinitions.RENHAI_APP_FOLDER
        		                   + File.separator
        		                   + RenHaiDefinitions.RENHAI_LOG_FILENAME
        		                   + ".log");
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.configure();
    }
    
    protected void getDeviceSn(){
    	TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService( Context.TELEPHONY_SERVICE); 
    	String tDeviceSn = telephonyManager.getDeviceId();
    	RenHaiJsonMsgProcess.storeDeviceSn(tDeviceSn);    	
    }
    
    private class MessageSendingTask extends AsyncTask<Integer, Integer, String> {
        // Do the long-running work in here
        protected String doInBackground(Integer... inType) {
        	int tInType = inType[0];
        	String tReturnMsg = null;
        	
        	switch (tInType) {
        	    case BACKGROUND_PROCESS_TYPE_INITIAL:
        	    {
        			// 1.Configure the logger module
        			configureLogger();
        			mlog.info("Renhai is about to start!");
        			
        			// 2.Initialize the time process
        			RenHaiTimeProcess.initTimeProcess();
        			
        			// 3.Get the device sn
        			getDeviceSn();
        			
        			// 4.Initialize the websocket
        			RenHaiWebSocketProcess.initNetworkProcess(getApplication());
        			
        			break;
        	    }
        	}       	
        	
        	return tReturnMsg;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {
            
        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(Long result) {
            
        }
    }
    

}

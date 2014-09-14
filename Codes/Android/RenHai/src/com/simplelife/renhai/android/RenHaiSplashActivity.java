/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiSplashActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android;

import com.crashlytics.android.Crashlytics;
import com.simplelife.renhai.android.R;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgAppDataSyncReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgServerDataSyncReq;
import com.simplelife.renhai.android.networkprocess.RenHaiHttpProcess;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.timeprocess.RenHaiTimeProcess;
import com.simplelife.renhai.android.timer.RenHaiTimerHelper;
import com.simplelife.renhai.android.timer.RenHaiTimerProcessor;
import com.simplelife.renhai.android.ui.RenHaiProgressBar;
import com.simplelife.renhai.android.ui.RenHaiProgressBar.Mode;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class RenHaiSplashActivity extends RenHaiBaseActivity {
	
	private SharedPreferences mSharedPrefs;
	
	private final static Logger mlog = Logger.getLogger(RenHaiSplashActivity.class);
	private static RenHaiProgressBar mProgressBar;
	private static TextView mProgressText;
	
	private static final int BACKGROUND_PROCESS_TYPE_INITIAL = 1;
	private static final int BACKGROUND_PROCESS_TYPE_RECONNECT = 2;
	private static final int SPLASH_MSG_NETWORKCONNTIMEOUT = 2001;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		final View tStartView = View.inflate(this,R.layout.activity_splash,null);
		
		configureLogger();
		
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
		fadeShow.setDuration(2800);	
		
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
    	
	private void redirectTo(){       
		Intent intent;
		if(isFirstStart())
		{
			updateFirstStartFlag(false);
			intent = new Intent(this, RenHaiProtocalActivity.class);			
		    Bundle bundle = new Bundle();
		    bundle.putString("caller", "RenHaiSplashActivity");
		    intent.putExtras(bundle);
		}
		else
		{
			intent = new Intent(this, RenHaiMainPageActivity.class);			
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
    
    private void getDeviceInfo(){
    	// 1.Get and store the device IMEI
    	TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService( Context.TELEPHONY_SERVICE); 
    	String tDeviceSn = telephonyManager.getDeviceId();
    	RenHaiInfo.storeDeviceSn(tDeviceSn);
    	
    	// 2.Get and store the device model
    	//Build tBd = new Build();
    	String tModel = Build.MODEL;
    	RenHaiInfo.DeviceCard.storeDeviceModel(tModel);
    	
    	// 3.Get and store the os version
    	//String tOsVersion = telephonyManager.getDeviceSoftwareVersion();
    	String tOsVersion = "Android" + android.os.Build.VERSION.RELEASE;
    	RenHaiInfo.DeviceCard.storeOsVersion(tOsVersion);
    	
    	// 4.Store the app version
    	//RenHaiInfo.DeviceCard.storeAppVersion(RenHaiDefinitions.RENHAI_APP_VERSION);
    	PackageManager packageManager = getPackageManager();
    	try {
			String tVersion = packageManager.getPackageInfo(getPackageName(), 0).versionName;
			RenHaiInfo.DeviceCard.storeAppVersion(tVersion);
		} catch (NameNotFoundException e) {
			mlog.error("Failed to get the app version!", e);
		}
    	
    	// 5.Get and store the location
    	String tLocation = telephonyManager.getCellLocation().toString();
    	RenHaiInfo.DeviceCard.storeLocation(tLocation);
    	
    }
    
    private void initNetwork(){    	
		int tProxyResponse = RenHaiDefinitions.RENHAI_FUNC_STATUS_ERROR;
		boolean tHttpFailedFlag = false;
		
    	// Communicate with the server proxy
    	try {
    		tProxyResponse = RenHaiHttpProcess.sendProxyDataSyncRequest(getApplication());
		} catch (ClientProtocolException e) {
			tHttpFailedFlag = true;
			mlog.error("Server proxy protocal exception!", e);			
		} catch (JSONException e) {
			tHttpFailedFlag = true;
			mlog.error("Server proxy JSON message exception!", e);
		} catch (IOException e) {
			tHttpFailedFlag = true;
			mlog.error("Server proxy IO exception!", e);
		}
    	
    	if((tProxyResponse != RenHaiDefinitions.RENHAI_FUNC_STATUS_OK)
    			||(tHttpFailedFlag == true))
    	{
            Intent tIntent = new Intent(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
            tIntent.putExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 
            		         RenHaiDefinitions.RENHAI_NETWORK_HTTP_COMM_ERROR);
            sendBroadcast(tIntent);    		
    	}
    	
    	if (tProxyResponse == RenHaiDefinitions.RENHAI_FUNC_STATUS_OK)
    	{
    		// Initialize the websocket
    		RenHaiWebSocketProcess.reInitWebSocket(getApplication());
    	}  	
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
        			mlog.info("Renhai is about to start!");
        			
        			// 2.Initialize the time process
        			RenHaiTimeProcess.initTimeProcess();
        			
        			// 3.Get the device info
        			getDeviceInfo();
        			
        			// 4.Initialize the network instances
        			initNetwork();
      			
        			break;
        	    }
        	    case BACKGROUND_PROCESS_TYPE_RECONNECT:
        	    {
        	    	initNetwork();
        	    }
        	}       	
        	
        	return tReturnMsg;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {
            
        }

        // This is called when doInBackground() is finished
        @SuppressWarnings("unused")
		protected void onPostExecute(Long result) {
            
        }
    }
    
	///////////////////////////////////////////////////////////////////////
	// Network message Processing
	/////////////////////////////////////////////////////////////////////// 	
    @Override
    protected void onHttpCommSuccess() {
    	super.onHttpCommSuccess();
		mProgressText.setText(R.string.mainpage_title_connectserver);
	}
	
    @Override
	protected void onHttpConnectFailed() {
    	super.onHttpConnectFailed();
    	mNetConnTimer.stopTimer();
    	AlertDialog.Builder builder = new Builder(this);
    	builder.setTitle(R.string.splash_httpfailed_dialogtitle);
    	builder.setMessage(R.string.splash_httpfailed_dialogmsg);
    	/*
    	builder.setPositiveButton(R.string.splash_httpfailed_dialogposbtn, 
    			                  new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				initNetwork();				
			}
    		
    	});*/
    	builder.setNegativeButton(R.string.splash_httpfailed_dialognegbtn, 
    			                  new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        finish();
		    }
		});

		builder.create().show();    	
    }
	
    @Override
	protected void onWebSocketCreateSuccess() {
    	super.onWebSocketCreateSuccess();
    	mProgressText.setText(R.string.mainpage_title_syncserver);
    	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication());  
    	String tAppDataSyncReqMsg = RenHaiMsgAppDataSyncReq.constructMsg().toString();
    	mWebSocketHandle.sendMessage(tAppDataSyncReqMsg);
    	mNetConnTimer.startTimer();
	}
	
    @Override
	protected void onWebSocketCreateError() {
    	super.onWebSocketCreateError();
		// TODO:Make a toast here to let the user to determine to enter the app offline or exit
	}	
	
    @Override
	protected void onReceiveAppSyncResp() {
    	super.onReceiveAppSyncResp();
		mNetConnTimer.stopTimer();
    	mProgressText.setText(R.string.mainpage_title_updateinfo);
    	String tServerDataSyncReqMsg = RenHaiMsgServerDataSyncReq.constructMsg().toString();
    	mWebSocketHandle.sendMessage(tServerDataSyncReqMsg);
    	mNetConnTimer.startTimer();
	}
	
    @Override
	protected void onReceiveServerSyncResp() {
    	super.onReceiveServerSyncResp();
		mNetConnTimer.stopTimer();
    	redirectTo();
	}
    
    ///////////////////////////////////////////////////////////////////////
    // Private message Processing
    ///////////////////////////////////////////////////////////////////////
	private Handler handler = new Handler(){  		  
        @Override  
        public void handleMessage(Message msg) {          	
        	switch (msg.what) {
        	    case SPLASH_MSG_NETWORKCONNTIMEOUT:
        	    {
        	    	onNetworkConnectionTimeOut();       	    	
        	    	break;
        	    }       	
        	}
        }
	};
 
    ///////////////////////////////////////////////////////////////////////
    // Exception Processing
    ///////////////////////////////////////////////////////////////////////        
    private void onNetworkConnectionTimeOut(){
    	AlertDialog.Builder builder = new Builder(this);
    	builder.setTitle(R.string.splash_conntimeout_dialogtitle);

    	builder.setPositiveButton(R.string.splash_conntimeout_dialogposbtn, 
    			                  new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				// Start another background task to process the network communication
		        MessageSendingTask tMsgSender = new MessageSendingTask();
		        tMsgSender.execute(BACKGROUND_PROCESS_TYPE_RECONNECT);				
			}
    		
    	});
    	builder.setNegativeButton(R.string.splash_conntimeout_dialognegbtn, 
    			                  new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        finish();
		    }
		});

		builder.create().show();    	
    }        
    
    ///////////////////////////////////////////////////////////////////////
    // Timer Callbacks
    ///////////////////////////////////////////////////////////////////////
    RenHaiTimerHelper mNetConnTimer = new RenHaiTimerHelper(10000, new RenHaiTimerProcessor() {
        @Override
        public void onTimeOut() {
        	Message t_MsgListData = new Message();
        	t_MsgListData.what = SPLASH_MSG_NETWORKCONNTIMEOUT;
        	handler.sendMessage(t_MsgListData);	       	
        }
        
    });    
    
}

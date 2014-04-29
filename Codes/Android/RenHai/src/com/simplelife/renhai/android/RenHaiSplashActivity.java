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
import com.simplelife.renhai.android.networkprocess.RenHaiNetworkProcess;
import com.simplelife.renhai.android.timeprocess.RenHaiTimeProcess;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;


public class RenHaiSplashActivity extends Activity {
	
	private SharedPreferences mSharedPrefs;
	
	private final Logger mlog = Logger.getLogger(RenHaiSplashActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View startView = View.inflate(this,R.layout.activity_splash,null);
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(startView);
		//getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);		 
		
		// Fade in and fade out
		AlphaAnimation fadeShow = new AlphaAnimation(0.3f,1.0f);
		fadeShow.setDuration(2000);
		startView.startAnimation(fadeShow);	
		
		// 1.Configure the logger module
		configureLogger();
		mlog.info("Renhai is about to start!");
		
		// 2.Initialize the time process
		RenHaiTimeProcess.initTimeProcess();
		
		// 3.Initialize the websocket
		RenHaiNetworkProcess.initNetworkProcess();
						
		// Stay for a moments and redirect
		fadeShow.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}			
		});
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
    
    public static void configureLogger() {
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

}

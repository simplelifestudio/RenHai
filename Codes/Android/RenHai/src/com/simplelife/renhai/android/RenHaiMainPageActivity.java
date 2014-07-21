/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMainPageActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-20. 
 */
package com.simplelife.renhai.android;

import java.net.URL;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgAlohaReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgAppDataSyncReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgServerDataSyncReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.timer.RenHaiTimerHelper;
import com.simplelife.renhai.android.timer.RenHaiTimerProcessor;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RenHaiMainPageActivity extends RenHaiBaseActivity implements ActionBar.TabListener{
	
	private final int MAINPAGE_MSG_WEBRECONNECTING = 3000;
	private final int MAINPAGE_MSG_WEBRECONNECTED  = 3001;

	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	static ViewPager mViewPager;
	ActionBar mActionBar;
	TextView  mActionBarTitle;
	View mHomeIcon;
	boolean mWebSocketLost = false;
	private final Logger mlog = Logger.getLogger(RenHaiMainPageActivity.class);
	private RelativeLayout mServerStatLayout;
	private TextView mServerStatInfo;
	private Button mBtnDismissInfo;
	
	// Define the view pages
	public enum ViewPages{
		PAGE_STARTCHAT,
		PAGE_MYTOPICS,
		PAGE_MYIMPRESSIONS
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_mainpage_viewpager);
        //setProgressBarIndeterminateVisibility(true);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        mActionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        mActionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        mServerStatLayout = (RelativeLayout)findViewById(R.id.mainpage_serverstatinfolayout);
        mServerStatInfo = (TextView)findViewById(R.id.mainpage_serverstatinfo);
        mBtnDismissInfo = (Button)findViewById(R.id.mainpage_dismissserverinfo);
        
        String tServerInfoToShow = "";
        if(0 == RenHaiInfo.ServerAddr.getServiceStatus())
        {
        	tServerInfoToShow = getString(R.string.mainpage_statinfopart1)
        			          + RenHaiInfo.ServerAddr.getTimeZone() + " "
        			          + RenHaiInfo.ServerAddr.getBeginTime() + " to "
        			          + RenHaiInfo.ServerAddr.getEndTime()
        			          + getString(R.string.mainpage_statinfopart1);
        }else{
        	tServerInfoToShow = RenHaiInfo.ServerAddr.getBroadcastInfo();
        }
        mServerStatLayout.setVisibility(View.VISIBLE);
        mServerStatInfo.setText(tServerInfoToShow);
        mBtnDismissInfo.setOnClickListener(mBtnDismissInfoListener);        

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.mainpage_viewpager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
            	mActionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
        	mActionBar.addTab(
        			mActionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        if(true == RenHaiInfo.InterestLabel.isPersonalIntLabelsNotDefined())
        {
        	mViewPager.setCurrentItem(1);
        }
    }	
    
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }
    
    public static void disableSlide(){
    	mViewPager.requestDisallowInterceptTouchEvent(true);
    }
    
    public static void enableSlide(){
    	mViewPager.requestDisallowInterceptTouchEvent(false);
    }
    
    private View.OnClickListener mBtnDismissInfoListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mServerStatLayout.setVisibility(View.GONE);			
		}
	};

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public RenHaiStartVedioFragment mRenHaiStartVedioFragment = null;
        public RenHaiMyTopicsFragment mRenHaiMyTopicsFragment = null;
        public RenHaiMyImpressionsFragment mRenHaiMyImpressionsFragment = null;
        
    	public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                	if (null == mRenHaiStartVedioFragment)
                		mRenHaiStartVedioFragment = new RenHaiStartVedioFragment();
                	return mRenHaiStartVedioFragment;
                	
                case 1:
                	if (null == mRenHaiMyTopicsFragment)
                		mRenHaiMyTopicsFragment = new RenHaiMyTopicsFragment();
                	return mRenHaiMyTopicsFragment;
                
                case 2:
                	if (null == mRenHaiMyImpressionsFragment)
                		mRenHaiMyImpressionsFragment = new RenHaiMyImpressionsFragment();
                	return mRenHaiMyImpressionsFragment;                	

                default:
                	if (null == mRenHaiStartVedioFragment)
                		mRenHaiStartVedioFragment = new RenHaiStartVedioFragment();
                	return mRenHaiStartVedioFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	String tTitle = null;
        	if (position == 0)
        		tTitle = getString(R.string.viewpager_section_start);
        	else if(position == 1)
        		tTitle = getString(R.string.viewpager_section_topics);
        	else if(position == 2)
        		tTitle =  getString(R.string.viewpager_section_impressions);
        	else
        		tTitle = "Section "+(position+1);
        	
        	CharSequence tReturn = tTitle;
        	
            return tReturn;

        }
    }
    
    private void enableActionBarNote(){
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
        		ActionBar.LayoutParams.MATCH_PARENT,
        		ActionBar.LayoutParams.MATCH_PARENT,
        		Gravity.CENTER);
        View viewTitleBar = getLayoutInflater().inflate(R.layout.activity_mainpage_titlebar, null);
        mActionBar.setCustomView(viewTitleBar, lp);
        //getActionBar().setDisplayShowHomeEnabled(false);
        //getActionBar().setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        //mActionBar.setDisplayShowCustomEnabled(false);
        mActionBarTitle = (TextView) mActionBar.getCustomView().findViewById(R.id.mainpage_title);
        
        mHomeIcon = findViewById(android.R.id.home); 
		((View) mHomeIcon.getParent()).setVisibility(View.GONE); 
        //setProgressBarIndeterminateVisibility(true);
    }
    
    private void disableActionBarNote(){
    	mActionBar.setDisplayShowCustomEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        setProgressBarIndeterminateVisibility(false);
        ((View) mHomeIcon.getParent()).setVisibility(View.VISIBLE);
    }
    
    private void showActionBarNote(int _id){
    	enableActionBarNote();
    	mActionBarTitle.setText(_id);
    	mActionBarTitle.setTextSize(16);
    }
    
	private void onReInitWebSocketDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getString(R.string.mainpage_connlostdialogtitle));
		//builder.setMessage(getString(R.string.mainpage_connlostdialogmsg));		
		builder.setPositiveButton(R.string.mainpage_connlostdialogposbtn, new DialogInterface.OnClickListener() {
		    @Override
	        public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();    	
		            new Thread() {  						
			            @Override
			            public void run() {
			            	//RenHaiWebSocketProcess.reInitWebSocket(getApplication());
			            	mWebSocketHandle.reConnectWebSocket(getApplication());
			            	Message t_MsgListData = new Message();
			            	t_MsgListData.what = MAINPAGE_MSG_WEBRECONNECTING;
			            	handler.sendMessage(t_MsgListData);			            	
			            }				
		            }.start();
		    }
		});

		builder.setNegativeButton(R.string.mainpage_connlostdialognegbtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});

		builder.create().show();
	}    
    
	private Handler handler = new Handler(){  		  
        @Override  
        public void handleMessage(Message msg) {          	
        	switch (msg.what) {
        	    case MAINPAGE_MSG_WEBRECONNECTING:
        	    {        	    	
        	    	mActionBarTitle.setText(R.string.mainpage_title_connectserver);
	            	setProgressBarIndeterminateVisibility(true);
        	    	break;
        	    }
        	    case MAINPAGE_MSG_WEBRECONNECTED:
        	    {
        	    	disableActionBarNote();
        	    	break;
        	    }
        	
        	}
        }
	};
    
    private BroadcastReceiver mBroadcastRcver = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 

            String action = intent.getAction(); 
            if(action.equals(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG)){
            	int tMsgType = intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 0);
            	switch (tMsgType) {
            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_CREATE_ERROR:
        	    {
        	    	mlog.error("Websocket error, error info: "+
        	                   intent.getStringExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_SOCKETERROR));
        	    	mWebSocketLost = true;
        	    	//mPingTimer.stopTimer();
        	    	showActionBarNote(R.string.mainpage_title_connectionlost);
        	    	onReInitWebSocketDialog();
        	    	break;
        	    }
            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_CREATE_SUCCESS:
        	    {
        	    	mlog.info("Websocket recreate success!");
        	    	if(mWebSocketLost == true)
        	    	{
        	    		// Re-get the websocket handle in case the handle is changed
            	    	mActionBarTitle.setText(R.string.mainpage_title_syncserver);
            	    	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication());
            	    	String tAppDataSyncReqMsg = RenHaiMsgAppDataSyncReq.constructQueryMsg().toString();
            	    	mWebSocketHandle.sendMessage(tAppDataSyncReqMsg);
        	    	}        	    	
        	        break;
        	    }
            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_APPSYNCRESP:
        	    {
        	    	if(mWebSocketLost == true)
        	    	{
        	    		mActionBarTitle.setText(R.string.mainpage_title_updateinfo);
            	    	String tServerDataSyncReqMsg = RenHaiMsgServerDataSyncReq.constructMsg().toString();
            	    	mWebSocketHandle.sendMessage(tServerDataSyncReqMsg);
        	    	}       	    	
        	    	break;
        	    }
            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_SERVERSYNCRESP:
        	    {
        	    	if(mWebSocketLost == true)
        	    	{
        	    		mActionBarTitle.setText(R.string.mainpage_title);
        	    		setProgressBarIndeterminateVisibility(false);
            	    	mShowConnectedTimer.startTimer();
            	    	mWebSocketLost = false;
            	    	//mPingTimer.startRepeatTimer();
        	    	}        	    	
        	    	//disableActionBarNote();
        	    	break;
        	    }
            	}            	
            }
        } 
    };
    
    ///////////////////////////////////////////////////////////////////////
    // Timer Callbacks
    ///////////////////////////////////////////////////////////////////////
    RenHaiTimerHelper mShowConnectedTimer = new RenHaiTimerHelper(2000, new RenHaiTimerProcessor() {
        @Override
        public void onTimeOut() {
        	Message t_MsgListData = new Message();
        	t_MsgListData.what = MAINPAGE_MSG_WEBRECONNECTED;
        	handler.sendMessage(t_MsgListData);	        	      	
        }
        
    });
    
    RenHaiTimerHelper mPingTimer = new RenHaiTimerHelper(1000, 4000, new RenHaiTimerProcessor() {
        @Override
        public void onTimeOut() {
        	mWebSocketHandle.ping("");        	      	
        }
        
    });
    
    
}

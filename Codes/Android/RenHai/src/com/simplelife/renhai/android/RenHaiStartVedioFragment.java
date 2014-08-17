/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiStartVedioFragment.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-20. 
 */
package com.simplelife.renhai.android;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgServerDataSyncReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.timer.RenHaiTimerHelper;
import com.simplelife.renhai.android.timer.RenHaiTimerProcessor;
import com.simplelife.renhai.android.ui.RenHaiCircleButton;
import com.simplelife.renhai.android.ui.RenHaiCircleButton.OnRadialViewValueChanged;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RenHaiStartVedioFragment extends Fragment {
	
	private final int STARTVEDIO_MSG_UPDATEPROGRESS = 5000;
	private final int STARTVEDIO_MSG_TIMETOUPDATE   = 5001;
	private final int STARTVEDIO_MSG_READYTOENTERPOOL = 5002;
	private RenHaiCircleButton mCircleButton;
	private TextView mOnlineCount;
	private TextView mChatCount;
	protected boolean mIsReadyToMoveOn = false;
	protected boolean mHasMovedOn = false;
	private ImageView mGuideImage;
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	private SharedPreferences mSharedPrefs;
	
	private final Logger mlog = Logger.getLogger(RenHaiStartVedioFragment.class);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	mSharedPrefs = getActivity().getSharedPreferences(RenHaiDefinitions.RENHAI_SHAREDPREF_FIRSTSTART, 0);
    	View rootView = inflater.inflate(R.layout.fragment_startvedio, container, false);
    	mCircleButton = (RenHaiCircleButton)rootView.findViewById(R.id.startvedio_button);  	
    	mOnlineCount  = (TextView)rootView.findViewById(R.id.startvedio_onlinecount);    	
    	mChatCount    = (TextView)rootView.findViewById(R.id.startvedio_chatcount);
   	
    	mGuideImage  = (ImageView)rootView.findViewById(R.id.startvedio_guide);
    	
    	if(isFirstTimeAssess())
    	{
    		mGuideImage.setVisibility(View.VISIBLE);
    		mGuideImage.setOnClickListener(new View.OnClickListener() { 
                public void onClick(View v) { 
               	 mGuideImage.setVisibility(View.GONE); 
                } 
            });
    		updateFirstTimeAssFlag(false);
    	}
  	
    	mCircleButton.setOnRadialViewValueChanged(new OnRadialViewValueChanged() {
			@Override
			public void onValueChanged(int value) {
				//WindowManager.LayoutParams lp = getWindow().getAttributes();
				//lp.screenBrightness = value / 100.0f;
				//getWindow().setAttributes(lp);
				//Log.i("RenHaiStartVedioFragment", "onValueChanged value = " + value);
			}
		});    	
    	
    	return rootView;    
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	mIsReadyToMoveOn = false;
    	mHasMovedOn = false;
    	
    	mCircleButton.resetView();    	
    	mCircleButton.autoShiningRing();
    	mCircleButton.setClickable(true);
    	mCircleButton.setOnClickListener(mStartBtnListener);
    	
    	onUpdateView();
    	
    	// Retrieve the network instance
     	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
     	
     	// Start the time to update the data periodically
     	mUpdateTimer.startRepeatTimer();
    	
    }
    
    @Override
	public void onStop() {
    	super.onStop();
    	mUpdateTimer.stopTimer();  	
    }
    
    public void onUpdateView(){
    	mOnlineCount.setText(formatCount(RenHaiInfo.ServerPoolStat.getOnlineCount()));
    	mChatCount.setText(formatCount(RenHaiInfo.ServerPoolStat.getChatCount()));
    }
    
    public void onDetermineToDirect() {
    	if((mIsReadyToMoveOn == true)&&(mHasMovedOn == false))
    	{
    		mlog.info("Ready to move to the waiting page, loop2!");
    		mHasMovedOn = true;
    		Intent tIntent = new Intent(getActivity(), RenHaiWaitForMatchActivity.class);
    		Bundle bundle = new Bundle();
    	    bundle.putString("caller", "RenHaiStartVedioFragment");
    	    tIntent.putExtras(bundle);
    		startActivity(tIntent);    		
    	}else
    	{
    		mIsReadyToMoveOn = true;
    	}
    }
    
    private String formatCount(int _count){
    	DecimalFormat df = new DecimalFormat("00000");
        String formatStr = df.format(_count);
        return formatStr;
    }
    
	private Handler handler = new Handler(){  		  
        @Override  
        public void handleMessage(Message msg) {          	
        	switch (msg.what) {
        	    case STARTVEDIO_MSG_UPDATEPROGRESS:
        	    {
        	    	mCircleButton.setSecondaryText(getString(R.string.startvedio_btnpreparing));
        	    	break;
        	    }
        	    case STARTVEDIO_MSG_TIMETOUPDATE:
        	    {        	    	 
        	    	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
        	    	String tServerDataSyncReqMsg = RenHaiMsgServerDataSyncReq.constructMsg().toString();
        	    	mWebSocketHandle.sendMessage(tServerDataSyncReqMsg);
        	    	break;
        	    }
        	    case STARTVEDIO_MSG_READYTOENTERPOOL:
        	    {
        	    	mUpdateTimer.stopTimer();
        	    	onDetermineToDirect();
        	    	//mCircleButton.setSecondaryText(getString(R.string.startvedio_btnmatching));
        	    	mlog.info("Ready to move to the waiting page, before loop1!");
        	    	break;
        	    }
        	    
        	
        	}
        }
	};
    
    private View.OnClickListener mStartBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mCircleButton.setShowPercentText(true);
			mCircleButton.setClickable(false);
			new Thread() {  						
	            @Override
	            public void run() {
	            	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
	            	String tBusinessSessionReq = RenHaiMsgBusinessSessionReq.constructMsg(
	            			RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
	            			RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_ENTERPOOL).toString();
	            	mWebSocketHandle.sendMessage(tBusinessSessionReq);
	            	
	            	Message t_MsgListData = new Message();
	            	t_MsgListData.what = STARTVEDIO_MSG_UPDATEPROGRESS;
	            	handler.sendMessage(t_MsgListData);		
	            	
	            	int tProgress = 0;
	            	while(tProgress <= 100){
	            		tProgress += 3;
	            		mCircleButton.setProgress(tProgress);
						
						try {
							Thread.sleep(60);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
	            	
	            	Message t_MsgListData1 = new Message();
	            	t_MsgListData1.what = STARTVEDIO_MSG_READYTOENTERPOOL;
	            	handler.sendMessage(t_MsgListData1);	            	
	            	            	
	            }				
            }.start();
			
		}
	};	
	
    ///////////////////////////////////////////////////////////////////////
    // Timer Callbacks
    ///////////////////////////////////////////////////////////////////////
    RenHaiTimerHelper mUpdateTimer = new RenHaiTimerHelper(25000, 25000, new RenHaiTimerProcessor() {
        @Override
        public void onTimeOut() {
        	Message t_MsgListData = new Message();
        	t_MsgListData.what = STARTVEDIO_MSG_TIMETOUPDATE;
        	handler.sendMessage(t_MsgListData);	       	
        }
        
    });
    
    
    private boolean isFirstTimeAssess() {   	
    	// Retrieve the seeds info status by date via the shared preference file
    	return mSharedPrefs.getBoolean(RenHaiDefinitions.RENHAI_SHAREDPREF_FIRSTSTART_STARTVIDEO,true);    	
    }
    
    private void updateFirstTimeAssFlag(Boolean _inTag){
    	
    	SharedPreferences.Editor editor = mSharedPrefs.edit();
    	editor.putBoolean(RenHaiDefinitions.RENHAI_SHAREDPREF_FIRSTSTART_STARTVIDEO, _inTag);
    	editor.commit();    	
    }

}

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

import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgServerDataSyncReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.timer.RenHaiTimerHelper;
import com.simplelife.renhai.android.timer.RenHaiTimerProcessor;
import com.simplelife.renhai.android.ui.RenHaiCircleButton;
import com.simplelife.renhai.android.ui.RenHaiCircleButton.OnRadialViewValueChanged;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RenHaiStartVedioFragment extends Fragment {
	
	private final int STARTVEDIO_MSG_UPDATEPROGRESS = 5000;
	private final int STARTVEDIO_MSG_TIMETOUPDATE   = 5001;
	RenHaiCircleButton mCircleButton;
	TextView mOnlineCount;
	TextView mChatCount;
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	
	private final Logger mlog = Logger.getLogger(RenHaiStartVedioFragment.class);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View rootView = inflater.inflate(R.layout.fragment_startvedio, container, false);
    	mCircleButton = (RenHaiCircleButton)rootView.findViewById(R.id.startvedio_button);
    	mCircleButton.autoShiningRing();
    	mCircleButton.setOnClickListener(mStartBtnListener);
    	
    	mOnlineCount = (TextView)rootView.findViewById(R.id.startvedio_onlinecount);    	
    	mChatCount   = (TextView)rootView.findViewById(R.id.startvedio_chatcount);
    	initView();
    	
    	mCircleButton.setOnRadialViewValueChanged(new OnRadialViewValueChanged() {
			@Override
			public void onValueChanged(int value) {
				//WindowManager.LayoutParams lp = getWindow().getAttributes();
				//lp.screenBrightness = value / 100.0f;
				//getWindow().setAttributes(lp);
				Log.i("RenHaiStartVedioFragment", "onValueChanged value = " + value);
			}
		});
		
		/*
    	if((int) (getWindow().getAttributes().screenBrightness * 100) < 0)
			mCircleButton.setCurrentValue(50);
		else
			mCircleButton.setCurrentValue((int) (getWindow().getAttributes().screenBrightness * 100));*/
    	
        // Register the broadcast receiver
     	IntentFilter tFilter = new IntentFilter();
     	tFilter.addAction(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
     	getActivity().registerReceiver(mBroadcastRcverStartVideo, tFilter);
     	
     	// Retrieve the network instance
     	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
     	
     	// Start the time to update the data periodically
     	mUpdateTimer.startTimer();
    	
    	return rootView;    
    }
    
    @Override
	public void onDestroy() {  
        super.onDestroy();  
        getActivity().unregisterReceiver(mBroadcastRcverStartVideo);  
    } 
    
    private void initView(){
    	mOnlineCount.setText(formatCount(RenHaiInfo.ServerPoolStat.getOnlineCount()));
    	mChatCount.setText(formatCount(RenHaiInfo.ServerPoolStat.getChatCount()));
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
        	    	mCircleButton.setSecondaryText(getString(R.string.startvedio_btnmatching));
        	    	int tValue = msg.getData().getInt("progress");
        	    	//mCircleButton.setCurrentValue(tValue);
        	    	break;
        	    }
        	    case STARTVEDIO_MSG_TIMETOUPDATE:
        	    {        	    	 
        	    	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
        	    	String tServerDataSyncReqMsg = RenHaiMsgServerDataSyncReq.constructMsg().toString();
        	    	mWebSocketHandle.sendMessage(tServerDataSyncReqMsg);
        	    	break;
        	    }
        	
        	}
        }
	};
	
    private BroadcastReceiver mBroadcastRcverStartVideo = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 

            String action = intent.getAction(); 
            if(action.equals(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG)){
            	int tMsgType = intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 0);
            	switch (tMsgType) {
            	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_SERVERSYNCRESP:
        	        {
        	        	initView();
        	        	mUpdateTimer.startTimer();
        	    	    break;
        	        }  
            	}            	
            }
        } 
    };
    
    private View.OnClickListener mStartBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mCircleButton.setShowPercentText(true);
			new Thread() {  						
	            @Override
	            public void run() {
	            	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
	            	String tBusinessSessionReq = RenHaiMsgBusinessSessionReq.constructMsg(RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
	            			RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_ENTERPOOL).toString();
	            	mWebSocketHandle.sendMessage(tBusinessSessionReq);
	            	
	            	int tProgress = 0;
	            	while(tProgress <= 100){
	            		tProgress += 3;
	            		mCircleButton.setProgress(tProgress);
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
	            	Message t_MsgListData = new Message();
	            	t_MsgListData.what = STARTVEDIO_MSG_UPDATEPROGRESS;
	            	handler.sendMessage(t_MsgListData);		            	
	            }				
            }.start();
			
		}
	};
	
    ///////////////////////////////////////////////////////////////////////
    // Timer Callbacks
    ///////////////////////////////////////////////////////////////////////
    RenHaiTimerHelper mUpdateTimer = new RenHaiTimerHelper(25000, new RenHaiTimerProcessor() {
        @Override
        public void onTimeOut() {
        	Message t_MsgListData = new Message();
        	t_MsgListData.what = STARTVEDIO_MSG_TIMETOUPDATE;
        	handler.sendMessage(t_MsgListData);	       	
        }
        
    });

}

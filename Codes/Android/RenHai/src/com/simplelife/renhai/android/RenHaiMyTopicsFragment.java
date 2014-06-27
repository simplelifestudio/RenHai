/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMyTopicsFragment.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-20. 
 */
package com.simplelife.renhai.android;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.jsonprocess.RenHaiMsgAppDataSyncReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgServerDataSyncReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.structure.InterestLabelMap;
import com.simplelife.renhai.android.timer.RenHaiTimerHelper;
import com.simplelife.renhai.android.timer.RenHaiTimerProcessor;
import com.simplelife.renhai.android.ui.RenHaiDraggableGridView;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RenHaiMyTopicsFragment extends Fragment {
	
	private final int MYTOPIC_MSG_OKTODEFINEINTEREST = 2000;
	private final int MYTOPIC_MSG_INTERESTDEFINED = 2001;
	private final int MYTOPIC_MSG_INTERESTCHANGED = 2002;
	private final int MYTOPIC_MSG_INTERESTDEFINEDBYMANUAL = 2003;
	private final int MYTOPIC_MSG_TIMETOUPDATE = 2004;
		
	RenHaiDraggableGridView mMyInterestsGrid;
	RenHaiDraggableGridView mGlbInterestsGrid;
	Button mCreateInt;
	Button mFreshBtn;
	TextView mGlbIntEmpty;
	ProgressBar mProgressBar;
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	
	private final Logger mlog = Logger.getLogger(RenHaiMyTopicsFragment.class);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View rootView = inflater.inflate(R.layout.fragment_mytopic, container, false);
    	mMyInterestsGrid  = (RenHaiDraggableGridView)(rootView.findViewById(R.id.mytopics_myinterests));
    	mGlbInterestsGrid = (RenHaiDraggableGridView)(rootView.findViewById(R.id.mytopics_globalinterests));
    	mCreateInt = (Button)rootView.findViewById(R.id.mytopics_create);
    	mFreshBtn  = (Button)rootView.findViewById(R.id.mytopics_refresh);
    	mCreateInt.setOnClickListener(mCreateNewLabelListener);   	
    	mFreshBtn.setOnClickListener(mFreshGlbIntLabelListener);
    	
    	mGlbIntEmpty = (TextView)rootView.findViewById(R.id.mytopics_glbintempty);
    	mProgressBar = (ProgressBar)rootView.findViewById(R.id.mytopics_progressbar);
    	mProgressBar.setVisibility(View.INVISIBLE);
    	
    	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
    	
    	if(true == RenHaiInfo.InterestLabel.isPersonalIntLabelsNotDefined())
    	{
    		// The personal interest is not defined
    		onDefinePersonalInterestDialog();
    	}    	
    	
    	// Update the personal interests label grid
    	onUpdateMyInterestGrid(true);
    	
    	// Update the global interest label grid
    	onUpdateGlobalInterestGrid();
    	
		/*
    	for(int i=0; i < RenHaiInfo.InterestLabel.getCurrHotLabelNum(); i++)
    	{
    		ImageView tIntLabel = new ImageView(getActivity());
    		tIntLabel.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getCurrHotIntLabel(i)));
    		/*TextView tIntLabel = new TextView(getActivity());
    		tIntLabel.setText(RenHaiInfo.InterestLabel.getInterest(i));
    		tIntLabel.setBackgroundColor(getResources().getColor(R.color.maingreen));
    		tIntLabel.setTextColor(getResources().getColor(R.color.white));
    		tIntLabel.setTextSize(18);
    		//tIntLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    		mMyInterestsGrid.addView(tIntLabel);
    	}*/
    	
    	setListeners();
    	
		// Register the broadcast receiver
		IntentFilter tFilter = new IntentFilter();
		tFilter.addAction(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
		getActivity().registerReceiver(mBroadcastRcverMyTopic, tFilter); 
	
    	return rootView;
    
    }
    
    @Override
	public void onDestroy() {  
        super.onDestroy();  
        getActivity().unregisterReceiver(mBroadcastRcverMyTopic);  
    } 
    
    private void setListeners(){
    	mMyInterestsGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				
				onEditOrDeleteTheIntLabel(arg2);				
			}
		});
    }
    
	private View.OnClickListener mCreateNewLabelListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			onDefinePersonalInterestTextBuilder(false);			
		}
	};
	
	private View.OnClickListener mFreshGlbIntLabelListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mProgressBar.setVisibility(View.VISIBLE);
			mGlbIntEmpty.setVisibility(View.INVISIBLE);
			new Thread() {  						
	            @Override
	            public void run() {
	            	String tServerDataSyncReqMsg = RenHaiMsgServerDataSyncReq.constructMsg().toString();
	            	
	            	// Try to retrieve the handle everytime when we want to send a message rather than
	            	// init the websocket handle on the start if the fragment. Because the websocket 
	            	// connect might be lost under some cases, and after the websocket is re-initialized,
	            	// the handle is changed anyway. The old pipe is broken, so we have to re-get.	            	
	            	RenHaiWebSocketProcess tWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
	            	tWebSocketHandle.sendMessage(tServerDataSyncReqMsg);
	            }				
            }.start();			
		}
	};
    
    private void onUpdateMyInterestGrid(boolean _ifFirstEntry){
    	mMyInterestsGrid.removeAllViews();
    	for(int i=0; i < RenHaiInfo.InterestLabel.getMyIntLabelNum(); i++)
    	{
    		ImageView tIntLabel = new ImageView(getActivity());
    		tIntLabel.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getMyIntLabel(i).getIntLabelName()));
    		mMyInterestsGrid.addView(tIntLabel);
    	}
    	
    	// Use the resetTimer rather than the startTimer in case of the 
    	// frequently operation of adding interest labels
    	if(_ifFirstEntry != true)
    		mUpdateTimer.resetTimer();
    }
    
    private void onUpdateGlobalInterestGrid(){
    	if(RenHaiInfo.InterestLabel.getCurrHotLabelNum() > 0)
    	{
    		mGlbIntEmpty.setVisibility(View.INVISIBLE);
    		mGlbInterestsGrid.removeAllViews();
    		for(int i=0; i < RenHaiInfo.InterestLabel.getCurrHotLabelNum(); i++)
    		{
    			ImageView tGlbIntLabel = new ImageView(getActivity());
        		tGlbIntLabel.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getCurrHotIntLabel(i).getIntLabelName()));
        		mGlbInterestsGrid.addView(tGlbIntLabel);
    		} 
    		mGlbInterestsGrid.setVisibility(View.VISIBLE);
    	}
    	else{
    		mGlbIntEmpty.setVisibility(View.VISIBLE);
    		mGlbInterestsGrid.setVisibility(View.INVISIBLE);
    	}
    }
    
	private Handler handler = new Handler(){  		  
        @Override  
        public void handleMessage(Message msg) {          	
        	switch (msg.what) {
        	    case MYTOPIC_MSG_OKTODEFINEINTEREST:
        	    {
        	    	onDefinePersonalInterestTextBuilder(true);       	    	
        	    	break;
        	    }
        	    case MYTOPIC_MSG_INTERESTDEFINED:
        	    {
        	    	onUpdateMyInterestGrid(false);
        	    	mMyInterestsGrid.refreshDrawableState();
        	    	onContinueToDefinePersonalInterestDialog();
        	    	break;
        	    }
        	    case MYTOPIC_MSG_INTERESTCHANGED:
        	    {
        	    	onUpdateMyInterestGrid(false);
        	    	mMyInterestsGrid.refreshDrawableState();
        	    	break;
        	    }
        	    case MYTOPIC_MSG_INTERESTDEFINEDBYMANUAL:
        	    {
        	    	onUpdateMyInterestGrid(false);
        	    	mMyInterestsGrid.refreshDrawableState();
        	    	break;
        	    }
        	    case MYTOPIC_MSG_TIMETOUPDATE:
        	    {
        	    	String tAppDataSyncReqMsg = RenHaiMsgAppDataSyncReq.constructMsg().toString();
        	    	mWebSocketHandle.sendMessage(tAppDataSyncReqMsg);
        	    	mUpdateTimer.stopTimer();
        	    	break;
        	    }
        	
        	}
        }
	};
	    
	private void onDefinePersonalInterestDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(getString(R.string.mytopics_intdialogtitle));
		builder.setMessage(getString(R.string.mytopics_intdialogmsg));		
		builder.setPositiveButton(R.string.mytopics_intdialogposbtn, new DialogInterface.OnClickListener() {
		    @Override
	        public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();    	
		            new Thread() {  						
			            @Override
			            public void run() {
			            	Message t_MsgListData = new Message();
			            	t_MsgListData.what = MYTOPIC_MSG_OKTODEFINEINTEREST;
			            	handler.sendMessage(t_MsgListData);	
			            }				
		            }.start();
		    }
		});

		builder.setNegativeButton(R.string.mytopics_intdialognegbtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});

		builder.create().show();
	}
	
	private void onDefinePersonalInterestTextBuilder(final boolean _isForFirstTime) {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(getString(R.string.mytopics_inttxtbuildtitle));		
		final EditText tEditText = new EditText(getActivity());
		builder.setView(tEditText);
		builder.setPositiveButton(R.string.mytopics_inttxtbuildposbtn, new DialogInterface.OnClickListener() {
		    @Override
	        public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();    	
		            new Thread() {  						
			            @Override
			            public void run() {
			            	String tInput = tEditText.getText().toString();
			            	InterestLabelMap tIntLabelMap = new InterestLabelMap();
			            	tIntLabelMap.setIntLabelName(tInput);
			            	RenHaiInfo.InterestLabel.putMyIntLabel(tIntLabelMap);
			            	Message t_MsgListData = new Message();
			            	if (true == _isForFirstTime)
			            		t_MsgListData.what = MYTOPIC_MSG_INTERESTDEFINED;
			            	else
			            		t_MsgListData.what = MYTOPIC_MSG_INTERESTDEFINEDBYMANUAL;
					        									
					        handler.sendMessage(t_MsgListData);				            					        
			            }				
		            }.start();
		    }
		});

		builder.setNegativeButton(R.string.mytopics_intdialognegbtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});

		builder.create().show();		
	}
	
	private void onContinueToDefinePersonalInterestDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(getString(R.string.mytopics_intcontinuedialogtitle));
		builder.setMessage(getString(R.string.mytopics_intcontinuedialogmsg));		
		builder.setPositiveButton(R.string.mytopics_intcontinuedialogposbtn, new DialogInterface.OnClickListener() {
		    @Override
	        public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();    	
		            new Thread() {  						
			            @Override
			            public void run() {
			            	Message t_MsgListData = new Message();
					        t_MsgListData.what = MYTOPIC_MSG_OKTODEFINEINTEREST;									
					        handler.sendMessage(t_MsgListData);	
			            }				
		            }.start();
		    }
		});

		builder.setNegativeButton(R.string.mytopics_intcontinuedialognegbtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});

		builder.create().show();
	}
    
	private void onEditOrDeleteTheIntLabel(final int _index) {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(getString(R.string.mytopics_inteditordelbuildtitle));		
		final EditText tEditText = new EditText(getActivity());
		tEditText.setText(RenHaiInfo.InterestLabel.getMyIntLabel(_index).getIntLabelName());
		builder.setView(tEditText);
		builder.setPositiveButton(R.string.mytopics_inteditordelbuildposbtn, new DialogInterface.OnClickListener() {
		    @Override
	        public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();    	
		            new Thread() {  						
			            @Override
			            public void run() {
			            	String tInput = tEditText.getText().toString();
			            	// Reset the label map and set the label name
			            	RenHaiInfo.InterestLabel.getMyIntLabel(_index).reset();
			            	RenHaiInfo.InterestLabel.getMyIntLabel(_index).setIntLabelName(tInput);
			            	Message t_MsgListData = new Message();
					        t_MsgListData.what = MYTOPIC_MSG_INTERESTCHANGED;									
					        handler.sendMessage(t_MsgListData);			            					        
			            }				
		            }.start();
		    }
		});

		builder.setNegativeButton(R.string.mytopics_inteditordelbuildnegbtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	mMyInterestsGrid.removeViewAt(_index);
		    	RenHaiInfo.InterestLabel.deleteMyIntLabel(_index);
		    	mUpdateTimer.resetTimer();
		        dialog.dismiss();
		    }
		});
		
		builder.setNeutralButton(R.string.mytopics_inteditordelbuildneubtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});

		builder.create().show();		
	}
	
    private Bitmap getThumb(String s)
	{
		Bitmap bmp = Bitmap.createBitmap(150, 75, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bmp);
	    Paint paint = new Paint();
	    
	    paint.setColor(getResources().getColor(R.color.maingreen));
	    paint.setTextSize(24);
	    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	    canvas.drawRect(new Rect(0, 0, 150, 75), paint);
	    paint.setColor(Color.WHITE);
	    paint.setTextAlign(Paint.Align.CENTER);
	    canvas.drawText(s, 75, 40, paint);
	    
		return bmp;
	}
    
    ///////////////////////////////////////////////////////////////////////
    // Timer Callbacks
    ///////////////////////////////////////////////////////////////////////
    RenHaiTimerHelper mUpdateTimer = new RenHaiTimerHelper(10000, new RenHaiTimerProcessor() {
        @Override
        public void onTimeOut() {
        	Message t_MsgListData = new Message();
        	t_MsgListData.what = MYTOPIC_MSG_TIMETOUPDATE;
        	handler.sendMessage(t_MsgListData);	       	
        }
        
    });
    
    ///////////////////////////////////////////////////////////////////////
    // Network message process
    ///////////////////////////////////////////////////////////////////////    
    private BroadcastReceiver mBroadcastRcverMyTopic = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 

            String action = intent.getAction(); 
            if(action.equals(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG)){
            	int tMsgType = intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 0);
            	switch (tMsgType) {
            	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_CREATE_ERROR:
            	    {
            	    	mGlbIntEmpty.setVisibility(View.VISIBLE);
            	    	mProgressBar.setVisibility(View.INVISIBLE);
            	    	break;
            	    }
            	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_APPSYNCRESP:
        	        {       	        	
        	    	    break;
        	        }
            	    case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_SERVERSYNCRESP:
            	    {
            	    	onUpdateGlobalInterestGrid();
            	    	mProgressBar.setVisibility(View.INVISIBLE);
            	    	break;
            	    }
            	}            	
            }
        } 
    };
    
    
}

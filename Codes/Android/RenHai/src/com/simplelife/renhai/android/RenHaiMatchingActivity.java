/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMatchingActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android;

import java.math.BigDecimal;
import java.text.NumberFormat;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.data.BusinessSessionInfo;
import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.utils.TimerConverter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class RenHaiMatchingActivity extends Activity{
	
	private GridView mAssessGridView;
	private GridView mImpGridView;
	private AssessAdapter mAssessAdapter;
	private ImpressionAdapter mImpAdapter;
	ActionBar mActionBar;
	TextView  mActionBarTitle;
	View mHomeIcon;
	
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	private final Logger mlog = Logger.getLogger(RenHaiMatchingActivity.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matching);
		
		mAssessGridView = (GridView)findViewById(R.id.matching_assgridview);
		mImpGridView = (GridView)findViewById(R.id.matching_impgridview);
		
		mAssessAdapter = new AssessAdapter(this);
		mAssessGridView.setAdapter(mAssessAdapter);
		
		enableActionBarNote();
		
		// Register the broadcast receiver
		IntentFilter tFilter = new IntentFilter();
		tFilter.addAction(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
		registerReceiver(mBroadcastRcverMatching, tFilter); 
		
		mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication());

	}
	
	@Override
	public void onDestroy() {  
        super.onDestroy();  
        unregisterReceiver(mBroadcastRcverMatching);  
    }
	
	private void enableActionBarNote(){
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
        		ActionBar.LayoutParams.MATCH_PARENT,
        		ActionBar.LayoutParams.MATCH_PARENT,
        		Gravity.CENTER);
        View viewTitleBar = getLayoutInflater().inflate(R.layout.activity_mainpage_titlebar, null);
        mActionBar = getActionBar();
        mActionBar.setCustomView(viewTitleBar, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBarTitle = (TextView) mActionBar.getCustomView().findViewById(R.id.mainpage_title);        
        mHomeIcon = findViewById(android.R.id.home); 
		((View) mHomeIcon.getParent()).setVisibility(View.GONE); 
		mActionBarTitle.setText(getString(R.string.matching_matchcondition) + BusinessSessionInfo.MatchedCondition.getMatchLabelName());
    	mActionBarTitle.setTextSize(16);
        //setProgressBarIndeterminateVisibility(true);
    }
	
    ///////////////////////////////////////////////////////////////////////
    // GridView adapters
    ///////////////////////////////////////////////////////////////////////  	
    private class AssessAdapter extends BaseAdapter {
    	
    	private Context mContext;
    	
    	public AssessAdapter(Context _context){
    		mContext = _context;
    	}

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(null == convertView)
			{
				String tTextToShow = null;
				TextView tTextView = new TextView(mContext);
				tTextView.setMaxLines(3);
				tTextView.setSingleLine(false);
				tTextView.setEllipsize(TruncateAt.MARQUEE);
				tTextView.setTextSize(16);
				tTextView.setGravity(Gravity.CENTER);
				tTextView.setTextColor(mContext.getResources().getColor(R.color.white));
				tTextView.setBackgroundColor(mContext.getResources().getColor(R.color.maingreen));
				tTextView.setPadding(0, 10, 0, 10);
				switch (position){
				    case 0:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_happylabel) 
				    			    + "\n"
				    			    + PeerDeviceInfo.AssessLabel.mHappyLabel.getAssessCount();				    	
				    	break;				    	
				    }
				    case 1:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_sosolabel) 
				    			    + "\n"
				    			    + PeerDeviceInfo.AssessLabel.mSoSoLabel.getAssessCount();			    	
				    	break;				    	
				    }
				    case 2:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_disgustlabel) 
				    			    + "\n"
				    			    + PeerDeviceInfo.AssessLabel.mDigustingLabel.getAssessCount();
				    	
				    	break;				    	
				    }
				    case 3:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_chatcount) 
				    			    + "\n"
				    			    + PeerDeviceInfo.Profile.getChatTotalCount();				    	
				    	break;				    	
				    }
				    case 4:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_chattotalduration) 
				    			    + "\n"
				    			    + TimerConverter.secondsToHMS(PeerDeviceInfo.Profile.getChatTotalDuration());				    	
				    	break;				    	
				    }
				    case 5:
				    {
				    	String tLossRate = "";
				    	if(PeerDeviceInfo.Profile.getChatTotalCount() > 0)
				    	{
				    		double tLossCount  = (double)PeerDeviceInfo.Profile.getChatLossCount();
				    		double tTotalCount = (double)PeerDeviceInfo.Profile.getChatTotalCount();
				    		BigDecimal tValue = new BigDecimal(tLossCount / tTotalCount);
				    		NumberFormat tPercent = NumberFormat.getPercentInstance();
				    		tPercent.setMinimumFractionDigits(2);
				    		tPercent.setMaximumFractionDigits(2);
				    		tLossRate = tPercent.format(tValue);
				    	}
				    	else
				    	{
				    		tLossRate = "0.00%";
				    	}
				    	
				    	tTextToShow = mContext.getString(R.string.myimpression_chatlosscount) 
				    			    + "\n"
				    			    + tLossRate;
				    	break;				    	
				    }
				    default:
				    {
				    	break;
				    }
				}
				
				tTextView.setText(tTextToShow);

				/*
				String tText = "Test\nTest"; 
				SpannableString builder = new SpannableString(tText); 
				builder.setSpan(new AbsoluteSizeSpan(18), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				builder.setSpan(new AbsoluteSizeSpan(14), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				tTextView.setText(builder);*/
				return tTextView;
			}
			else{
				return convertView;
			}
		} 
    	
    }
    
    private class ImpressionAdapter extends BaseAdapter {
    	
    	private Context mContext;
    	
    	public ImpressionAdapter(Context _context){
    		mContext = _context;
    	}

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(null == convertView)
			{
				String tTextToShow = null;
				TextView tTextView = new TextView(mContext);
				tTextView.setMaxLines(3);
				tTextView.setSingleLine(false);
				tTextView.setEllipsize(TruncateAt.MARQUEE);
				tTextView.setTextSize(16);
				tTextView.setGravity(Gravity.CENTER);
				tTextView.setTextColor(mContext.getResources().getColor(R.color.white));
				tTextView.setBackgroundColor(mContext.getResources().getColor(R.color.maingreen));
				tTextView.setPadding(0, 10, 0, 10);
				switch (position){
				    case 0:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_happylabel) 
				    			    + "\n"
				    			    + PeerDeviceInfo.AssessLabel.mHappyLabel.getAssessCount();				    	
				    	break;				    	
				    }
				    case 1:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_sosolabel) 
				    			    + "\n"
				    			    + PeerDeviceInfo.AssessLabel.mSoSoLabel.getAssessCount();			    	
				    	break;				    	
				    }
				    case 2:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_disgustlabel) 
				    			    + "\n"
				    			    + PeerDeviceInfo.AssessLabel.mDigustingLabel.getAssessCount();
				    	
				    	break;				    	
				    }
				    case 3:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_chatcount) 
				    			    + "\n"
				    			    + PeerDeviceInfo.Profile.getChatTotalCount();				    	
				    	break;				    	
				    }
				    case 4:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_chattotalduration) 
				    			    + "\n"
				    			    + TimerConverter.secondsToHMS(PeerDeviceInfo.Profile.getChatTotalDuration());				    	
				    	break;				    	
				    }
				    case 5:
				    {
				    	String tLossRate = "";
				    	if(PeerDeviceInfo.Profile.getChatTotalCount() > 0)
				    	{
				    		double tLossCount  = (double)PeerDeviceInfo.Profile.getChatLossCount();
				    		double tTotalCount = (double)PeerDeviceInfo.Profile.getChatTotalCount();
				    		BigDecimal tValue = new BigDecimal(tLossCount / tTotalCount);
				    		NumberFormat tPercent = NumberFormat.getPercentInstance();
				    		tPercent.setMinimumFractionDigits(2);
				    		tPercent.setMaximumFractionDigits(2);
				    		tLossRate = tPercent.format(tValue);
				    	}
				    	else
				    	{
				    		tLossRate = "0.00%";
				    	}
				    	
				    	tTextToShow = mContext.getString(R.string.myimpression_chatlosscount) 
				    			    + "\n"
				    			    + tLossRate;
				    	break;				    	
				    }
				    default:
				    {
				    	break;
				    }
				}
				
				tTextView.setText(tTextToShow);

				/*
				String tText = "Test\nTest"; 
				SpannableString builder = new SpannableString(tText); 
				builder.setSpan(new AbsoluteSizeSpan(18), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				builder.setSpan(new AbsoluteSizeSpan(14), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				tTextView.setText(builder);*/
				return tTextView;
			}
			else{
				return convertView;
			}
		} 
    	
    }
	
    ///////////////////////////////////////////////////////////////////////
    // Network message process
    ///////////////////////////////////////////////////////////////////////    
    private BroadcastReceiver mBroadcastRcverMatching = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 

            String action = intent.getAction(); 
            if(action.equals(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG)){
            	int tMsgType = intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 0);
            	switch (tMsgType) {

            	}            	
            }
        } 
    };

}

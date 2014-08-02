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

import com.simplelife.renhai.android.RenHaiDefinitions.RenHaiAppState;
import com.simplelife.renhai.android.data.AppStateMgr;
import com.simplelife.renhai.android.data.BusinessSessionInfo;
import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionNotificationResp;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.utils.TimerConverter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RenHaiMatchingActivity extends RenHaiBaseActivity{
	
	private GridView mAssessGridView;
	private GridView mImpGridView;
	private AssessAdapter mAssessAdapter;
	private ImpressionAdapter mImpAdapter;
	private TextView mCounterText;
	private MyCount mCounter;
	private TextView mMatchingBtnYes;
	private TextView mMatchingBtnNo;
	private TextView mPeerStatText;
	private LinearLayout mBackLayout;
	private TextView mMatchingBtnBack;
	private boolean mEitherSideAgreed = false;
	private boolean mMovedToVideoPage = false;
	ActionBar mActionBar;
	TextView  mActionBarTitle;
	View mHomeIcon;

	private final Logger mlog = Logger.getLogger(RenHaiMatchingActivity.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matching);
		
		mAssessGridView = (GridView)findViewById(R.id.matching_assgridview);
		mImpGridView    = (GridView)findViewById(R.id.matching_impgridview);
		
		mAssessAdapter = new AssessAdapter(this);
		mAssessGridView.setAdapter(mAssessAdapter);
		
		mImpAdapter = new ImpressionAdapter(this);
		mImpGridView.setAdapter(mImpAdapter);
		
		mCounterText = (TextView)findViewById(R.id.matching_counter);
		mCounter = new MyCount(22000, 1000);
		
		mMatchingBtnYes = (TextView)findViewById(R.id.matching_btnaccept);
		mMatchingBtnNo  = (TextView)findViewById(R.id.matching_btnrefuse);		
		mPeerStatText   = (TextView)findViewById(R.id.matching_peerstat);		
		mBackLayout     = (LinearLayout)findViewById(R.id.matching_backlayout);
		mMatchingBtnBack = (TextView)findViewById(R.id.matching_btnback);
		mBackLayout.setVisibility(View.GONE);
	
		enableActionBarNote();
		
		mCounter.start();
	}
	
	@Override
	protected void onResume() {
    	super.onResume();
    	mMatchingBtnYes.setOnClickListener(mMatchingBtnYesListener);
		mMatchingBtnNo.setOnClickListener(mMatchingBtnNoListener);
		mMatchingBtnBack.setOnClickListener(mMatchingBtnBackListener);
		mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication());
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		sendBusinessSessionReqMessage(RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_LEAVEPOOL);
	}
	
	private View.OnClickListener mMatchingBtnYesListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			sendBusinessSessionReqMessage(RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_AGREECHAT);
			
			// Hide the buttons
			mMatchingBtnYes.setVisibility(View.INVISIBLE);
			mMatchingBtnNo.setVisibility(View.INVISIBLE);			
		}
	};
	
	private View.OnClickListener mMatchingBtnNoListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			sendBusinessSessionReqMessage(RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_REJECTCHAT);
			// Hide the buttons
		    mMatchingBtnYes.setVisibility(View.INVISIBLE);
			mMatchingBtnNo.setVisibility(View.INVISIBLE);			
		}
	};
	
	private View.OnClickListener mMatchingBtnBackListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			sendBusinessSessionReqMessage(RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_SESSIONUNBIND);			
			//finish();		
		}
	};
	
	private void determingToMoveToVideoPage(){
		if((mEitherSideAgreed == true)&&(mMovedToVideoPage == false))
    	{
			mMovedToVideoPage = true;
			mlog.info("App State transit to CHATALLAGREED from "+AppStateMgr.getMyAppStatus());
			AppStateMgr.setMyAppStatus(RenHaiAppState.CHATALLAGREED);
    		Intent tIntent = new Intent(this, RenHaiVideoTalkActivity.class);
    		startActivity(tIntent);
    		finish();
    	}else
    	{
    		mEitherSideAgreed = true;
    	}
	}
	
	private void sendBusinessSessionReqMessage(int _operationType){
		String tBusinessSessionReq = RenHaiMsgBusinessSessionReq.constructMsg(
    			RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, _operationType).toString();
    	mWebSocketHandle.sendMessage(tBusinessSessionReq);
	}
	
	private void sendBusinessSessionNotificationRespMessage(int _operationType, int _operationValue){
		String tBusinessSessionNotResp = RenHaiMsgBusinessSessionNotificationResp.constructMsg(
    			RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, _operationType, _operationValue).toString();
    	mWebSocketHandle.sendMessage(tBusinessSessionNotResp);
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
		mActionBarTitle.setText( getString(R.string.matching_matchconditionpart1) 
				               + BusinessSessionInfo.MatchedCondition.getMatchLabelName()
				               + getString(R.string.matching_matchconditionpart2));
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
				    			    + PeerDeviceInfo.AssessLabel.mHappyLabel.getAssessedCount();				    	
				    	break;				    	
				    }
				    case 1:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_sosolabel) 
				    			    + "\n"
				    			    + PeerDeviceInfo.AssessLabel.mSoSoLabel.getAssessedCount();			    	
				    	break;				    	
				    }
				    case 2:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_disgustlabel) 
				    			    + "\n"
				    			    + PeerDeviceInfo.AssessLabel.mDigustingLabel.getAssessedCount();
				    	
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
				    			    + TimerConverter.msToHms(PeerDeviceInfo.Profile.getChatTotalDuration());				    	
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
			int tImpLabelNum = PeerDeviceInfo.ImpressionLabel.getPeerImpLabelNum();
			return (tImpLabelNum > 6) ? 6 : tImpLabelNum;
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
				tTextToShow = PeerDeviceInfo.ImpressionLabel.getPeerImpLabelMap(position).getImpLabelName()
						    + "\n"
						    + PeerDeviceInfo.ImpressionLabel.getPeerImpLabelMap(position).getAssessedCount();
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
    // Count down timer process
    ///////////////////////////////////////////////////////////////////////  
    class MyCount extends CountDownTimer{
		 long mTimeToWait;
		   
	        public MyCount(long millisInFuture, long countDownInterval) {  
	            super(millisInFuture, countDownInterval);
	            mTimeToWait = millisInFuture;
	        }  
	        @Override  
	        public void onTick(long millisUntilFinished) {  
	        	mCounterText.setText("" + millisUntilFinished / 1000);  
	        }  
	        @Override  
	        public void onFinish() {  
	        	mCounterText.setText(R.string.matching_counttimeout); 
	        	mCounterText.setTextSize(18);
	        	sendBusinessSessionReqMessage(RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_LEAVEPOOL);
	        	mBackLayout.setVisibility(View.VISIBLE);      	 
	        }  
	 }
    
    private void redirectToWaitPage() {
    	Intent tIntent = new Intent(this, RenHaiWaitForMatchActivity.class);
    	Bundle bundle = new Bundle();
	    bundle.putString("caller", "RenHaiMatchingActivity");
	    tIntent.putExtras(bundle);
		startActivity(tIntent);
		finish();
    }
	
    ///////////////////////////////////////////////////////////////////////
    // Network message process
    ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onReceiveBSRespAgreeChat() {
		super.onReceiveBSRespAgreeChat();
		determingToMoveToVideoPage();
	}
    
    @Override
    protected void onReceiveBSRespRejectChat() {
    	super.onReceiveBSRespRejectChat();
    	mlog.info("App State transit to BusinessChoosed from "+AppStateMgr.getMyAppStatus());
		AppStateMgr.setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		redirectToWaitPage();		
    }
    
    @Override
    protected void onReceiveBNPeerAgree() {
    	super.onReceiveBNPeerAgree();
    	mPeerStatText.setText(R.string.matching_peerstattextagree);
		sendBusinessSessionNotificationRespMessage(RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_OTHERSIDEAGREED,1);
    	determingToMoveToVideoPage();
	}
    
    @Override
    protected void onReceiveBNPeerRej() {
		super.onReceiveBNPeerRej();
		mPeerStatText.setText(R.string.matching_peerstattextreject);
		mCounter.cancel();
		sendBusinessSessionNotificationRespMessage(RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_OTHERSIDEREJECTED,1);
		mBackLayout.setVisibility(View.VISIBLE);
	}
    
    @Override
    protected void onReceiveBNPeerLost() {
    	super.onReceiveBNPeerLost();
    	mPeerStatText.setText(R.string.matching_peerstattextlost);
		mCounter.cancel();
		sendBusinessSessionNotificationRespMessage(RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_OTHERSIDELOST,1);
		mBackLayout.setVisibility(View.VISIBLE);
	}
    
    @Override
    protected void onReceiveBSRespSessUnBind() {
    	mlog.info("App State transit to BusinessChoosed from "+AppStateMgr.getMyAppStatus());
		AppStateMgr.setMyAppStatus(RenHaiAppState.BUSINESSCHOOSED);
		redirectToWaitPage();
	}

}

/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMatchingActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android;

import java.util.ArrayList;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.data.WebRtcSession;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionNotificationResp;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RenHaiVideoTalkActivity extends Activity implements
Session.SessionListener, Publisher.PublisherListener,
Subscriber.VideoListener {
	
	public static final String SESSION_ID = "1_MX40NDg0NTgxMn5-U2F0IEp1biAyMSAwNTo1MTo0NyBQRFQgMjAxNH4wLjQ0MjQ4OTY4fn4";
	// Replace with a generated token (from the dashboard or using an OpenTok server SDK)
	public static final String TOKEN = "T1==cGFydG5lcl9pZD00NDg0NTgxMiZzaWc9NjMyZGEyMDkzYmRmNDI2OGNlYzI3N2YxNTEyYWVmYTQ2NGE2YzUxMjpyb2xlPXN1YnNjcmliZXImc2Vzc2lvbl9pZD0xX01YNDBORGcwTlRneE1uNS1VMkYwSUVwMWJpQXlNU0F3TlRvMU1UbzBOeUJRUkZRZ01qQXhOSDR3TGpRME1qUTRPVFk0Zm40JmNyZWF0ZV90aW1lPTE0MDMzNTUxMzkmbm9uY2U9MC40MjQ1NzAxMzg0ODczMTk4NCZleHBpcmVfdGltZT0xNDA1OTQ2ODIw"; 
	// Replace with your OpenTok API key
	public static final String API_KEY= "44845812";
	
	// Subscribe to a stream published by this client. Set to false to subscribe
    // to other clients' streams only.
    public static final boolean SUBSCRIBE_TO_SELF = true;

	private static final String LOGTAG = "RenHai-Video";
	private Session mSession;
	private Publisher mPublisher;
	private Subscriber mSubscriber;
	private ArrayList<Stream> mStreams;
	protected Handler mHandler = new Handler();
	
	private RelativeLayout mPublisherViewContainer;
	private RelativeLayout mSubscriberViewContainer;
	private LinearLayout mBtnLayout;
	private LinearLayout mMsgLayout;
	private LinearLayout mShowMsgLayout;
	private TextView mBtnWriteMsg;
	private TextView mBtnHangOff;
	private TextView mPeerStatus;
	private TextView mBtnSendMsg;
	private TextView mShowMsg;
	private EditText mEditMsg;
	
	// Spinning wheel for loading subscriber view
	private ProgressBar mLoadingSub;
	
	private boolean resumeHasRun = false;
	
	private boolean isLayoutCalledout = false;
	
	private NotificationCompat.Builder mNotifyBuilder;
	NotificationManager mNotificationManager;
	private int notificationId;
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOGTAG, "ONCREATE");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_videotalk);
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mPublisherViewContainer = (RelativeLayout) findViewById(R.id.video_publisherview);
		mSubscriberViewContainer = (RelativeLayout) findViewById(R.id.video_subscriberview);
		mLoadingSub  = (ProgressBar) findViewById(R.id.loadingSpinner);
		mBtnLayout   = (LinearLayout) findViewById(R.id.video_touchlayout);
		mMsgLayout   = (LinearLayout) findViewById(R.id.video_writemsglayout);
		mShowMsgLayout = (LinearLayout) findViewById(R.id.video_showmsglayout);
        mBtnWriteMsg = (TextView) findViewById(R.id.video_btnwritemsg);
        mBtnHangOff  = (TextView) findViewById(R.id.video_btnhangup);
        mPeerStatus  = (TextView) findViewById(R.id.peerstatus);
        mBtnSendMsg  = (TextView) findViewById(R.id.video_btnsendmsg);
        mShowMsg     = (TextView) findViewById(R.id.video_showmsg);
        mEditMsg     = (EditText) findViewById(R.id.video_editmsg);
        
        mPublisherViewContainer.setOnClickListener(mPublisherListener);
        mBtnWriteMsg.setOnClickListener(mBtnWriteMsgListener);
        mBtnHangOff.setOnClickListener(mBtnHangOffListener);
        mBtnSendMsg.setOnClickListener(mBtnSendMsgListener);
        mBtnLayout.setVisibility(View.GONE);
        mMsgLayout.setVisibility(View.GONE);
        mShowMsgLayout.setVisibility(View.GONE);
        mLoadingSub.setVisibility(View.VISIBLE);
		
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication());
		
		// Register the broadcast receiver
		IntentFilter tFilter = new IntentFilter();
		tFilter.addAction(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG);
		registerReceiver(mBroadcastRcverVideo, tFilter); 
		
		mStreams = new ArrayList<Stream>();
		sessionConnect();
	}
	
	private View.OnClickListener mPublisherListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if(isLayoutCalledout == false)
			{
				mBtnLayout.setVisibility(View.VISIBLE);	
				isLayoutCalledout = true;
			}else{
				mBtnLayout.setVisibility(View.GONE);
				mMsgLayout.setVisibility(View.GONE);
				isLayoutCalledout = false;
			}
			mShowMsgLayout.setVisibility(View.GONE);
				
		}
	};
	
	private View.OnClickListener mBtnWriteMsgListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mBtnLayout.setVisibility(View.GONE);
			mMsgLayout.setVisibility(View.VISIBLE);			
		}
	};
	
	private View.OnClickListener mBtnHangOffListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			sendBusinessSessionReqMessage(RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_ENDCHAT);
			mBtnLayout.setVisibility(View.GONE);
			//mShowMsgLayout.setVisibility(View.VISIBLE);
    		//mShowMsg.setText("Hello, this is a test!");
		}
	};
	
	private View.OnClickListener mBtnSendMsgListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String tBusinessSessionReq = RenHaiMsgBusinessSessionReq.constructMsg(
	    			RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
	    			RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_CHATMESSAGE,
	    			mEditMsg.getText().toString()).toString();
	    	mWebSocketHandle.sendMessage(tBusinessSessionReq);
			mMsgLayout.setVisibility(View.GONE);			
		}
	};
	
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		    onBackPressed();
		    return true;
		default:
		    return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	
		// Remove publisher & subscriber views because we want to reuse them
		if (mSubscriber != null) {
		    mSubscriberViewContainer.removeView(mSubscriber.getView());
		}
		reloadInterface();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mSession != null) {
		    mSession.onPause();
		
		    if (mSubscriber != null) {
		        mSubscriberViewContainer.removeView(mSubscriber.getView());
		    }
		}
		
		mNotifyBuilder = new NotificationCompat.Builder(this)
		        .setContentTitle(this.getTitle())
		        .setContentText(getResources().getString(R.string.video_notification))
		        .setSmallIcon(R.drawable.ic_launcher).setOngoing(true);
		
		Intent notificationIntent = new Intent(this, RenHaiVideoTalkActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(this, 0,
		        notificationIntent, 0);
		
		mNotifyBuilder.setContentIntent(intent);
		notificationId = (int) System.currentTimeMillis();
		mNotificationManager.notify(notificationId, mNotifyBuilder.build());
	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (!resumeHasRun) {
		    resumeHasRun = true;
		    return;
		} else {
		    if (mSession != null) {
		        mSession.onResume();
		    }
		}
		mNotificationManager.cancel(notificationId);
		
		reloadInterface();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		if (isFinishing()) {
		    mNotificationManager.cancel(notificationId);
		    if (mSession != null) {
		        mSession.disconnect();
		    }
		}
	}
	
	@Override
	public void onDestroy() {  
        super.onDestroy();  
        unregisterReceiver(mBroadcastRcverVideo);  
    }
	
	@Override
	public void onBackPressed() {
		if (mSession != null) {
		    mSession.disconnect();
		}
		super.onBackPressed();
	}
	
	public void reloadInterface() {
	mHandler.postDelayed(new Runnable() {
	    @Override
	    public void run() {
	        if (mSubscriber != null) {
	            attachSubscriberView(mSubscriber);
	        }
	    }
	}, 500);
	}
	
	private void sessionConnect() {
		if (mSession == null) {
			/*
		    mSession = new Session(RenHaiVideoTalkActivity.this,
		            WebRtcSession.getApiKey(), WebRtcSession.getSessionId());
		    mSession.setSessionListener(this);
		    mSession.connect(WebRtcSession.getToken());*/
		    mSession = new Session(RenHaiVideoTalkActivity.this,
		            API_KEY, SESSION_ID);
		    mSession.setSessionListener(this);
		    mSession.connect(TOKEN);
		}
	}
	
	@Override
	public void onConnected(Session session) {
		Log.i(LOGTAG, "Connected to the session.");
		if (mPublisher == null) {
		    mPublisher = new Publisher(RenHaiVideoTalkActivity.this, "publisher");
		    mPublisher.setPublisherListener(this);
		    attachPublisherView(mPublisher);
		    mSession.publish(mPublisher);
		    mLoadingSub.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onDisconnected(Session session) {
		Log.i(LOGTAG, "Disconnected from the session.");
		if (mPublisher != null) {
		    mPublisherViewContainer.removeView(mPublisher.getView());
		}
		
		if (mSubscriber != null) {
		    mSubscriberViewContainer.removeView(mSubscriber.getView());
		}
		
		mPublisher = null;
		mSubscriber = null;
		mStreams.clear();
		mSession = null;
	}
	
	private void subscribeToStream(Stream stream) {
		mSubscriber = new Subscriber(RenHaiVideoTalkActivity.this, stream);
		mSubscriber.setVideoListener(this);
		mSession.subscribe(mSubscriber);
		// start loading spinning
		//mLoadingSub.setVisibility(View.VISIBLE);
	}
	
	private void unsubscribeFromStream(Stream stream) {
		mStreams.remove(stream);
		if (mSubscriber.getStream().getStreamId().equals(stream.getStreamId())) {
		    mSubscriberViewContainer.removeView(mSubscriber.getView());
		    mSubscriber = null;
		    if (!mStreams.isEmpty()) {
		        subscribeToStream(mStreams.get(0));
		    }
		}
	}
	
	private void attachSubscriberView(Subscriber subscriber) {
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
		        getResources().getDisplayMetrics().widthPixels, getResources()
		                .getDisplayMetrics().heightPixels);
		Log.i(LOGTAG, "attaching subscriber, width: " + getResources().getDisplayMetrics().widthPixels + " height: "+getResources()
                .getDisplayMetrics().heightPixels);
		mSubscriberViewContainer.addView(mSubscriber.getView(), layoutParams);
		subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
		        BaseVideoRenderer.STYLE_VIDEO_FILL);
	}
	
	private void attachPublisherView(Publisher publisher) {
		mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
		        BaseVideoRenderer.STYLE_VIDEO_FILL);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
		        320, 240);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
		        RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
		        RelativeLayout.TRUE);
		layoutParams.bottomMargin = dpToPx(8);
		layoutParams.rightMargin = dpToPx(8);
		mPublisherViewContainer.addView(mPublisher.getView(), layoutParams);
	}
	
	@Override
	public void onError(Session session, OpentokError exception) {
		Log.i(LOGTAG, "Session exception: " + exception.getMessage());
	}
	
	@Override
	public void onStreamReceived(Session session, Stream stream) {
		Log.i(LOGTAG, "onStreamReceived");
		if (!SUBSCRIBE_TO_SELF) {
		    mStreams.add(stream);
		    if (mSubscriber == null) {
		        subscribeToStream(stream);
		    }
		}
	}
	
	@Override
	public void onStreamDropped(Session session, Stream stream) {
		Log.i(LOGTAG, "onStreamDropped");
		if (!SUBSCRIBE_TO_SELF) {
		    if (mSubscriber != null) {
		        unsubscribeFromStream(stream);
		    }
		}
	}
	
	@Override
	public void onStreamCreated(PublisherKit publisher, Stream stream) {
		Log.i(LOGTAG, "onStreamCreated");
		if (SUBSCRIBE_TO_SELF) {
		    mStreams.add(stream);
		    if (mSubscriber == null) {
		        subscribeToStream(stream);
		    }
		}
	}
	
	@Override
	public void onStreamDestroyed(PublisherKit publisher, Stream stream) {
		if ((SUBSCRIBE_TO_SELF && mSubscriber != null)) {
		    unsubscribeFromStream(stream);
		}
	}
	
	@Override
	public void onError(PublisherKit publisher, OpentokError exception) {
		Log.i(LOGTAG, "Publisher exception: " + exception.getMessage());
	}
	
	@Override
	public void onVideoDisabled(SubscriberKit subscriber) {
		Log.i(LOGTAG,
		        "Video quality changed. It is disabled for the subscriber.");
	}
	
	@Override
	public void onVideoDataReceived(SubscriberKit subscriber) {
		Log.i(LOGTAG, "First frame received");
		
		// stop loading spinning
		mLoadingSub.setVisibility(View.GONE);
		attachSubscriberView(mSubscriber);
	}
	
	/**
	* Converts dp to real pixels, according to the screen density.
	* 
	* @param dp
	*            A number of density-independent pixels.
	* @return The equivalent number of real pixels.
	*/
	private int dpToPx(int dp) {
	double screenDensity = this.getResources().getDisplayMetrics().density;
	return (int) (screenDensity * (double) dp);
	}
	
    ///////////////////////////////////////////////////////////////////////
    // Network message process
    ///////////////////////////////////////////////////////////////////////    
    private BroadcastReceiver mBroadcastRcverVideo = new BroadcastReceiver() { 
        @Override 
        public void onReceive(Context context, Intent intent) { 

            String action = intent.getAction(); 
            if(action.equals(RenHaiDefinitions.RENHAI_BROADCAST_WEBSOCKETMSG)){
            	int tMsgType = intent.getIntExtra(RenHaiDefinitions.RENHAI_BROADCASTMSG_DEF, 0);
            	switch (tMsgType) {
            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONRESP_ENDCHAT:
            	{
            		// TODO: Add processing to disconnect the opentok
            		finish();
            		break;
            	}
            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_PEERENDCHAT:
            	{
            		// TODO: Add processing to disconnect the opentok
            		sendBusinessSessionNotificationRespMessage(RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_OTHERSIDEENDCHAT,1);
            		mPeerStatus.setVisibility(View.VISIBLE);
            		mPeerStatus.setText(R.string.video_peerhangoff);
            		finish();
            		break;
            	}
            	case RenHaiDefinitions.RENHAI_NETWORK_WEBSOCKET_RECEIVE_BUSINESSSESSIONNOT_PEERCHATMSG:
            	{
            		sendBusinessSessionNotificationRespMessage(RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_OTHERSIDECHATMESSAGE,1);
            		mShowMsgLayout.setVisibility(View.VISIBLE);
            		mShowMsg.setText(PeerDeviceInfo.getChatMsg());
            		break;
            	}

            	}            	
            }
        } 
    };

}

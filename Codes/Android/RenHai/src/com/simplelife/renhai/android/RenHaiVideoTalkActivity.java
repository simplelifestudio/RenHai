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

import org.apache.log4j.Logger;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.simplelife.renhai.android.RenHaiDefinitions.RenHaiAppState;
import com.simplelife.renhai.android.data.AppStateMgr;
import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.data.WebRtcSession;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgAlohaReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionNotificationResp;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.timer.RenHaiTimerHelper;
import com.simplelife.renhai.android.timer.RenHaiTimerProcessor;
import com.simplelife.renhai.android.video.PublisherControlFragment;
import com.simplelife.renhai.android.video.PublisherStatusFragment;
import com.simplelife.renhai.android.video.SubscriberControlFragment;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class RenHaiVideoTalkActivity extends RenHaiBaseActivity implements Session.SessionListener,
		Session.PublisherListener, Session.ArchiveListener,
		Session.StreamPropertiesListener, Publisher.PublisherListener,
		Subscriber.VideoListener, Subscriber.SubscriberListener,
		SubscriberControlFragment.SubscriberCallbacks,
		PublisherControlFragment.PublisherCallbacks,
		OnTouchListener {

	// For Test purpose
	public static final String SESSION_ID = "2_MX40NDkyNzkxMn5-U3VuIEF1ZyAwMyAyMzoyOTozMSBQRFQgMjAxNH4wLjk1NjgyMzZ-fg";
	// Replace with a generated token (from the dashboard or using an OpenTok server SDK)
	public static final String TOKEN = "T1==cGFydG5lcl9pZD00NDkyNzkxMiZzaWc9ZjA3NmZjNjJmNWIyMDBiNjFkZWVjZDA0YjRmMDZiYzU1NGY5YTk3NDpyb2xlPXB1Ymxpc2hlciZzZXNzaW9uX2lkPTJfTVg0ME5Ea3lOemt4TW41LVUzVnVJRUYxWnlBd015QXlNem95T1Rvek1TQlFSRlFnTWpBeE5INHdMamsxTmpneU16Wi1mZyZjcmVhdGVfdGltZT0xNDA3MTMzOTkzJm5vbmNlPTAuMjMzOTE0ODcxOTkxODEzOTMmZXhwaXJlX3RpbWU9MTQwOTcyNTcyOQ=="; 
	// Replace with your OpenTok API key
	public static final String API_KEY= "44927912";
	// Subscribe to a stream published by this client. Set to false to subscribe
	// to other clients' streams only.
	public static final boolean SUBSCRIBE_TO_SELF = false;
	
	private static final int ANIMATION_DURATION = 3000;
	
	private Session mSession;
	protected Publisher mPublisher;
	protected Subscriber mSubscriber;
	private ArrayList<Stream> mStreams = new ArrayList<Stream>();
	protected Handler mHandler = new Handler();
	
	private boolean mSubscriberVideoOnly = false;
	private boolean archiving = false;
	private boolean resumeHasRun = false;
	private boolean mSelfStreamHide = false;
	
	// View related variables
	private RelativeLayout mPublisherViewContainer;
	private RelativeLayout mPublishViewFakeContainer;
	private RelativeLayout mSubscriberViewContainer;
	private RelativeLayout mSubscriberAudioOnlyView;
	private LinearLayout   mHangUpLayout;
	private TextView       mBtnHangUp;
	
	// Fragments
	private SubscriberControlFragment mSubscriberFragment;
	private PublisherControlFragment mPublisherFragment;
	private PublisherStatusFragment mPublisherStatusFragment;
	private FragmentTransaction mFragmentTransaction;
	
	// Spinning wheel for loading subscriber view
	private ProgressBar mLoadingSub;
	private ProgressBar mPublisherLoadingSub;
	private TextView mSubLoadingNote;
	private TextView mPubLoadingNote;
	
	private NotificationCompat.Builder mNotifyBuilder;
	NotificationManager mNotificationManager;
	private int notificationId;
	private final Logger mlog = Logger.getLogger(RenHaiVideoTalkActivity.class);
	private int lastX;  
	private int lastY; 
	private int screenWidth;  
	private int screenHeight;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		loadInterface();
		
		if (savedInstanceState == null) {
		    mFragmentTransaction = getFragmentManager().beginTransaction();
		    initSubscriberFragment();
		    initPublisherFragment();
		    initPublisherStatusFragment();
		    mFragmentTransaction.commitAllowingStateLoss();
		}
		
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getApplication());
		mAlohaTimer.startRepeatTimer();
		
		sessionConnect();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_videotalk, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_settings:
		    if (mSubscriber != null) {
		        onViewClick.onClick(null);
		    }
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
		
		    if (mSubscriberFragment != null) {
		        getFragmentManager().beginTransaction()
		                .remove(mSubscriberFragment).commit();
		
		        initSubscriberFragment();
		    }
		}
		if (mPublisher != null) {
		    mPublisherViewContainer.removeView(mPublisher.getView());
		
		    if (mPublisherFragment != null) {
		        getFragmentManager().beginTransaction()
		                .remove(mPublisherFragment).commit();
		
		        initPublisherFragment();
		    }
		
		    if (mPublisherStatusFragment != null) {
		        getFragmentManager().beginTransaction()
		                .remove(mPublisherStatusFragment).commit();
		
		        initPublisherStatusFragment();
		    }
		}		
		
		loadInterface();
	}
	
	public void loadInterface() {
		setContentView(R.layout.activity_videotalk);
		
		mLoadingSub = (ProgressBar) findViewById(R.id.loadingSpinner);
		mSubLoadingNote = (TextView) findViewById(R.id.video_subloadingnote);
		mPublisherLoadingSub = (ProgressBar) findViewById(R.id.loadingSpinner_publisher);
		mLoadingSub.setVisibility(View.VISIBLE);
		mSubLoadingNote.setVisibility(View.VISIBLE);
		mPublisherLoadingSub.setVisibility(View.VISIBLE);
		
		mPublisherViewContainer   = (RelativeLayout) findViewById(R.id.publisherView_full);
		mPublishViewFakeContainer = (RelativeLayout) findViewById(R.id.publisherView);
		mSubscriberViewContainer  = (RelativeLayout) findViewById(R.id.subscriberView);
		mSubscriberAudioOnlyView  = (RelativeLayout) findViewById(R.id.audioOnlyView);
		mHangUpLayout = (LinearLayout) findViewById(R.id.video_hangup);
		mBtnHangUp    = (TextView) findViewById(R.id.video_btnhangup);
		mBtnHangUp.setOnClickListener(mBtnHangUpListener);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();  
        screenWidth = dm.widthPixels;  
        screenHeight = dm.heightPixels - 50;  
		//mPublisherViewContainer.setOnTouchListener(this);
		
		// Attach running video views
		if (mPublisher != null) {
		    attachPublisherView(mPublisher);
		}
		
		// show subscriber status
		mHandler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        if (mSubscriber != null) {
		            attachSubscriberView(mSubscriber);
		
		            if (mSubscriberVideoOnly) {
		                mSubscriber.getView().setVisibility(View.GONE);
		                setAudioOnlyView(true);
		            }
		        }
		    }
		}, 0);
		
		loadFragments();
	}
	
	public void loadFragments() {
		// show subscriber status
		mHandler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        if (mSubscriber != null) {
		            mSubscriberFragment.showSubscriberWidget(true);
		            mSubscriberFragment.initSubscriberUI();
		        }
		    }
		}, 0);
		
		// show publisher status
		mHandler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        if (mPublisher != null) {
		            mPublisherFragment.showPublisherWidget(true);
		            mPublisherFragment.initPublisherUI();
		
		            if (archiving) {
		                mPublisherStatusFragment.updateArchivingUI(true);
		                setPubViewMargins();
		            }
		        }
		    }
		}, 0);
	}
	
	public void initSubscriberFragment() {
		mSubscriberFragment = new SubscriberControlFragment();
		getFragmentManager().beginTransaction()
		        .add(R.id.fragment_sub_container, mSubscriberFragment).commit();
	}
	
	public void initPublisherFragment() {
		mPublisherFragment = new PublisherControlFragment();
		getFragmentManager().beginTransaction()
		        .add(R.id.fragment_pub_container, mPublisherFragment).commit();
	}
	
	public void initPublisherStatusFragment() {
		mPublisherStatusFragment = new PublisherStatusFragment();
		getFragmentManager()
		        .beginTransaction()
		        .add(R.id.fragment_pub_status_container,
		                mPublisherStatusFragment).commit();
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
		
		mlog.info("VideoTalk onResume called!");
		
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
		
		loadFragments();
	}
	
	private void sessionConnect() {
		if (mSession == null) {
			if(SUBSCRIBE_TO_SELF)
			{
				mSession = new Session(this, API_KEY, SESSION_ID);				
			}else{
				mSession = new Session(this, WebRtcSession.getApiKey(), WebRtcSession.getSessionId());
			}
			
		    mSession.setSessionListener(this);
		    mSession.setArchiveListener(this);
		    mSession.setStreamPropertiesListener(this);
		    mSession.setPublisherListener(this);
		    if(SUBSCRIBE_TO_SELF)
			{
		    	mSession.connect(TOKEN);				
			}else{
				mSession.connect(WebRtcSession.getToken());
			}
		}
	}
	
	private void attachPublisherView(Publisher publisher) {
		/*
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
		mPublisherViewContainer.addView(publisher.getView(), layoutParams);
		mPublisherViewContainer.setDrawingCacheEnabled(true);*/
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				dpToPx(160),dpToPx(160));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		layoutParams.setMargins(0, 0, dpToPx(20), dpToPx(20));
		mPublisherViewContainer.addView(publisher.getView(), layoutParams);
		mPublisherViewContainer.setDrawingCacheEnabled(true);
		mPublishViewFakeContainer.setVisibility(View.GONE);
		publisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
		        BaseVideoRenderer.STYLE_VIDEO_FILL);
		publisher.getView().setOnClickListener(onViewClick);
		publisher.getView().setOnTouchListener(this);
		mPublisherLoadingSub.setVisibility(View.GONE);
	}
	
	@Override
	public void onMuteSubscriber() {
		if (mSubscriber != null) {
		    mSubscriber.setSubscribeToAudio(!mSubscriber.getSubscribeToAudio());
		}
	}
	
	@Override
	public void onReplyMsg() {
		if (mPublisherFragment != null) {
			mSubscriberFragment.showSubscriberWidget(false);			
			mPublisherFragment.showPublisherWidget(true);
			mPublisherFragment.editAndSendMsg();
		}
	}
	
	@Override
	public void onMutePublisher() {
		if (mPublisher != null) {
		    mPublisher.setPublishAudio(!mPublisher.getPublishAudio());
		}
	}
	
	@Override
	public void onSwapCamera() {
		if (mPublisher != null) {
		    mPublisher.swapCamera();
		}
	}
	
	@Override
	public void onShowOrHideMyCam() {
		if (mPublisher != null) {
		    if(mSelfStreamHide == false)
		    {
		    	mPublisher.getView().setVisibility(View.INVISIBLE);
		    	mSelfStreamHide = true;
		    }else{
		    	mPublisher.getView().setVisibility(View.VISIBLE);
		    	mSelfStreamHide = false;
		    }
			
		}
		
	} 
	
	@Override
	public void onEndCall() {
		sendBusinessSessionReqMessage(RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_ENDCHAT);
		//mLoadingSub.setVisibility(View.VISIBLE);
		mSubLoadingNote.setVisibility(View.VISIBLE);
		mSubLoadingNote.setText(R.string.video_endcallnote);
		//mBtnLayout.setVisibility(View.GONE);
		
		if (mSession != null) {
		    mSession.disconnect();
		}
		//finish();
	}
	
	private void attachSubscriberView(Subscriber subscriber) {
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
		mSubscriberViewContainer.addView(subscriber.getView(), layoutParams);
		subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
		        BaseVideoRenderer.STYLE_VIDEO_FILL);
		subscriber.getView().setOnClickListener(onViewClick);
	}
	
	private void subscribeToStream(Stream stream) {
		mSubscriber = new Subscriber(this, stream);
		mSubscriber.setSubscriberListener(this);
		mSubscriber.setVideoListener(this);
		mSession.subscribe(mSubscriber);
		// start loading spinning
		//mLoadingSub.setVisibility(View.VISIBLE);	
	}
	
	private void unsubscriberFromStream(Stream stream) {
		mStreams.remove(stream);
		if (mSubscriber.getStream().getStreamId().equals(stream.getStreamId())) {
		    mSubscriberViewContainer.removeView(mSubscriber.getView());
		    mSubscriber = null;
		    if (!mStreams.isEmpty()) {
		        subscribeToStream(mStreams.get(0));
		    }
		}
	}
	
	private void setAudioOnlyView(boolean audioOnlyEnabled) {
		mSubscriberVideoOnly = audioOnlyEnabled;
		
		if (audioOnlyEnabled) {
		    mSubscriber.getView().setVisibility(View.GONE);
		    mSubscriberAudioOnlyView.setVisibility(View.VISIBLE);
		    mSubscriberAudioOnlyView.setOnClickListener(onViewClick);
		
		    // Audio only text for subscriber
		    TextView subStatusText = (TextView) findViewById(R.id.subscriberName);
		    subStatusText.setText(R.string.video_audioOnly);
		    AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
		    aa.setDuration(ANIMATION_DURATION);
		    subStatusText.startAnimation(aa);
		} else {
		    if (!mSubscriberVideoOnly) {
		        mSubscriber.getView().setVisibility(View.VISIBLE);
		        mSubscriberAudioOnlyView.setVisibility(View.GONE);
		    }
		}
	}
	
	private OnClickListener onViewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
		
		    if (mPublisher != null) {
		        // check visibility of bars
		        mPublisherFragment.publisherClick();
		        if (archiving) {
		            mPublisherStatusFragment.publisherClick();
		        }
		        setPubViewMargins();
		    }
		    if (mSubscriber != null) {
		        mSubscriberFragment.subscriberClick();
		    }
		}
	};
	
	public Publisher getmPublisher() {
		return mPublisher;
	}
	
	public Subscriber getmSubscriber() {
		return mSubscriber;
	}
	
	public Handler getmHandler() {
		return mHandler;
	}
	
	@Override
	public void onConnected(Session session) {
		mlog.info("Connected to the session.");
		if (mPublisher == null) {
		    mPublisher = new Publisher(this, getString(R.string.video_publisherinfo));
		    mPublisher.setPublisherListener(this);
		    attachPublisherView(mPublisher);
		    mSession.publish(mPublisher);
		}
	}
	
	@Override
	public void onDisconnected(Session session) {
		mlog.info("Disconnected to the session.");
	}
	
	@Override
	public void onStreamReceived(Session session, Stream stream) {
	
		if (!SUBSCRIBE_TO_SELF) {
		    mStreams.add(stream);
		    if (mSubscriber == null) {
		        subscribeToStream(stream);
		    }
		}
	}
	
	@Override
	public void onStreamDropped(Session session, Stream stream) {
		mStreams.remove(stream);
		if (!SUBSCRIBE_TO_SELF) {
		    if (mSubscriber != null
		            && mSubscriber.getStream().getStreamId()
		                    .equals(stream.getStreamId())) {
		        mSubscriberViewContainer.removeView(mSubscriber.getView());
		        mSubscriber = null;
		        findViewById(R.id.avatar).setVisibility(View.GONE);
		        findViewById(R.id.speakerActive).setVisibility(View.GONE);
		        findViewById(R.id.noVideo).setVisibility(View.GONE);
		        mSubscriberVideoOnly = false;
		        if (!mStreams.isEmpty()) {
		            subscribeToStream(mStreams.get(0));
		        }
		    }
		}
	}
	
	@Override
	public void onStreamCreated(PublisherKit publisher, Stream stream) {
	
		if (SUBSCRIBE_TO_SELF) {
		    mStreams.add(stream);
		    if (mSubscriber == null) {
		        subscribeToStream(stream);
		    }
		}
	}
	
	@Override
	public void onStreamDestroyed(PublisherKit publisher, Stream stream) {	
		if (SUBSCRIBE_TO_SELF) {
			if (mSubscriber != null) {
			    unsubscriberFromStream(stream);
			}
		}
	}
	
	@Override
	public void onError(Session session, OpentokError exception) {
		Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
		mlog.error(exception.getMessage());
	}
	
	@Override
	public void onPublisherAdded(Session session, PublisherKit publisher) {
		mlog.info("The publisher starts streaming");
		
		// initializing publisher fragments UI
		mPublisherFragment.showPublisherWidget(true);
		mPublisherFragment.initPublisherUI();
		mPublisherStatusFragment.showPubStatusWidget(true);
		mPublisherStatusFragment.initPubStatusUI();
	}
		
	public void setPubViewMargins() {
		RelativeLayout.LayoutParams pubLayoutParams = (LayoutParams) mPublisherViewContainer
		        .getLayoutParams();
		int bottomMargin = 0;
		boolean controlBarVisible = mPublisherFragment
		        .isMPublisherWidgetVisible();
		boolean statusBarVisible = mPublisherStatusFragment
		        .isMPubStatusWidgetVisible();
		RelativeLayout.LayoutParams pubControlLayoutParams = (LayoutParams) mPublisherFragment
		        .getMPublisherContainer().getLayoutParams();
		RelativeLayout.LayoutParams pubStatusLayoutParams = (LayoutParams) mPublisherStatusFragment
		        .getMPubStatusContainer().getLayoutParams();
		
		// setting margins for publisher view on portrait orientation
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		    if (statusBarVisible && archiving) {
		        // height of publisher control bar + height of publisher status
		        // bar + 20 px
		
		        bottomMargin = pubControlLayoutParams.height
		                + pubStatusLayoutParams.height + dpToPx(20);
		    } else {
		        if (controlBarVisible) {
		            // height of publisher control bar + 20 px
		            bottomMargin = pubControlLayoutParams.height + dpToPx(20);
		        } else {
		            bottomMargin = dpToPx(20);
		        }
		    }
		}
		
		// setting margins for publisher view on landscape orientation
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    if (statusBarVisible && archiving) {
		        bottomMargin = pubStatusLayoutParams.height + dpToPx(20);
		    } else {
		        bottomMargin = dpToPx(20);
		    }
		}
		
		pubLayoutParams.bottomMargin = bottomMargin;
		pubLayoutParams.leftMargin = dpToPx(20);
		
		mPublisherViewContainer.setLayoutParams(pubLayoutParams);
		
		if (mSubscriber != null) {
		    if (mSubscriberVideoOnly) {
		        RelativeLayout.LayoutParams subLayoutParams = (LayoutParams) mSubscriberAudioOnlyView
		                .getLayoutParams();
		        int subBottomMargin = 0;
		        subBottomMargin = pubLayoutParams.bottomMargin;
		        subLayoutParams.bottomMargin = subBottomMargin;
		        mSubscriberAudioOnlyView.setLayoutParams(subLayoutParams);
		    }
		}
	}
	
	@Override
	public void onPublisherRemoved(Session session, PublisherKit publisher) {
		mlog.info("The publisher stops streaming");
	}
	
	@Override
	public void onError(PublisherKit publisher, OpentokError exception) {
		mlog.info("Publisher exception: " + exception.getMessage());
	}
	
	@Override
	public void onConnected(SubscriberKit subscriber) {
		mSubscriberFragment.showSubscriberWidget(true);
		mSubscriberFragment.initSubscriberUI();
	}
	
	@Override
	public void onDisconnected(SubscriberKit subscriber) {
		mlog.info("Subscriber disconnected.");
	}
	
	@Override
	public void onVideoDataReceived(SubscriberKit subscriber) {
		mlog.info("First frame received");
		
		// stop loading spinning
		mLoadingSub.setVisibility(View.GONE);
		mSubLoadingNote.setVisibility(View.GONE);
		attachSubscriberView(mSubscriber);
	}
	
	@Override
	public void onError(SubscriberKit subscriber, OpentokError exception) {
		mlog.info("Subscriber exception: " + exception.getMessage());
	}
	
	@Override
	public void onVideoDisabled(SubscriberKit subscriber) {
		mlog.info("Video quality changed. It is disabled for the subscriber.");
		if (mSubscriber == subscriber) {
		    setAudioOnlyView(true);
		}
	}
	
	@Override
	public void onStreamHasAudioChanged(Session session, Stream stream,
	    boolean audioEnabled) {
		mlog.info("Stream audio changed");
	}
	
	@Override
	public void onStreamHasVideoChanged(Session session, Stream stream,
	    boolean videoEnabled) {
		mlog.info("Stream video changed");
		
		if (mSubscriber != null
		        && (mSubscriber.getStream().getStreamId() == stream
		                .getStreamId())) {
		    if (videoEnabled) {
		        setAudioOnlyView(true);
		    } else {
		        setAudioOnlyView(false);
		    }
		}
	}
	
	@Override
	public void onStreamVideoDimensionsChanged(Session session, Stream stream,
	    int width, int height) {
		mlog.info("Stream video dimensions changed");
	}
	
	@Override
	public void onArchiveStarted(Session session, String id, String name) {
		mlog.info("Archiving starts");
		mPublisherFragment.showPublisherWidget(false);
		
		archiving = true;
		mPublisherStatusFragment.updateArchivingUI(true);
		mPublisherFragment.showPublisherWidget(true);
		mPublisherFragment.initPublisherUI();
		setPubViewMargins();
		
		if (mSubscriber != null) {
		    mSubscriberFragment.showSubscriberWidget(true);
		}
	}
	
	@Override
	public void onArchiveStopped(Session session, String id) {
		mlog.info("Archiving stops");
		archiving = false;
		
		mPublisherStatusFragment.updateArchivingUI(false);
		setPubViewMargins();
	}
	
	/**
	* Converts dp to real pixels, according to the screen density.
	* 
	* @param dp
	*            A number of density-independent pixels.
	* @return The equivalent number of real pixels.
	*/
	public int dpToPx(int dp) {
		double screenDensity = getResources().getDisplayMetrics().density;
		return (int) (screenDensity * (double) dp);
	}
	
	
	private void onWebSocketException() {
		 AlertDialog.Builder builder = new Builder(this);
    	builder.setTitle(R.string.video_connectionlost_dialogtitle);

    	builder.setPositiveButton(R.string.video_connectionlost_dialogposbtn, 
    			                  new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				// Return to the mainpage, the network re-connection will be
				// fulfilled in the mainpage
				finish();
			}    		
    	});

		builder.create().show();
	 }
	
    ///////////////////////////////////////////////////////////////////////
    // Network message process
    ///////////////////////////////////////////////////////////////////////
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
	protected void onWebSocketDisconnect() {
		super.onWebSocketDisconnect();
		onWebSocketException();		
	}
	
	@Override
	protected void onWebSocketCreateError() {
		super.onWebSocketCreateError();
		onWebSocketException();
	}
	
	@Override
	protected void onReceiveBSRespEndChat() {
		super.onReceiveBSRespEndChat();
		mlog.info("App State transit to ChatEnded from "+AppStateMgr.getMyAppStatus());
		AppStateMgr.setMyAppStatus(RenHaiAppState.CHATENDED);
		directToAssessPage();
	}
	
	@Override
	protected void onReceiveBNPeerEndChat() {
		super.onReceiveBNPeerEndChat();
		sendBusinessSessionNotificationRespMessage(RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_OTHERSIDEENDCHAT,1);
		mLoadingSub.setVisibility(View.GONE);
		mSubLoadingNote.setVisibility(View.VISIBLE);
		mSubLoadingNote.setText(R.string.video_peerhangoff);
		mHangUpLayout.setVisibility(View.VISIBLE);	
	}
	
	@Override
	protected void onReceiveBNPeerChatMsg() {
		super.onReceiveBNPeerChatMsg();
		sendBusinessSessionNotificationRespMessage(RenHaiDefinitions.RENHAI_SERVERNOTIF_TYPE_OTHERSIDECHATMESSAGE,1);
		mSubscriberFragment.showReceivedMsg(PeerDeviceInfo.getChatMsg()); 
	}
	
	@Override
	protected void onReceiveAlohaResp() {
		super.onReceiveAlohaResp();
	}
    
    private void directToAssessPage(){
    	mAlohaTimer.stopTimer();
    	Intent intent = new Intent(RenHaiVideoTalkActivity.this, RenHaiAssessActivity.class);
	    startActivity(intent);
		finish();
    }
    
	///////////////////////////////////////////////////////////////////////
	// Timer Callbacks
	///////////////////////////////////////////////////////////////////////
	RenHaiTimerHelper mAlohaTimer = new RenHaiTimerHelper(0, 40000, new RenHaiTimerProcessor() {
		@Override
		public void onTimeOut() {
			mlog.info("Sending Aloha Request!");
			String tAlohaRequestMsg = RenHaiMsgAlohaReq.constructMsg().toString();
        	mWebSocketHandle.sendMessage(tAlohaRequestMsg);	       	
		}
	
	});
	
	///////////////////////////////////////////////////////////////////////
	// On touch listner
	///////////////////////////////////////////////////////////////////////
	private View.OnClickListener mBtnHangUpListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			sendBusinessSessionReqMessage(RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_ENDCHAT);
			mHangUpLayout.setVisibility(View.GONE);	
		}
	};
	
	 @Override  
	    public boolean onTouch(View v, MotionEvent event) {  
	        // TODO Auto-generated method stub  
	        int action=event.getAction();    
	        switch(action){  
	        case MotionEvent.ACTION_DOWN:
	            lastX = (int) event.getRawX();  
	            lastY = (int) event.getRawY();  
	            break;  
	            /** 
	             * layout(l,t,r,b) 
	             * l  Left position, relative to parent  
	            t  Top position, relative to parent  
	            r  Right position, relative to parent  
	            b  Bottom position, relative to parent   
	             * */  
	        case MotionEvent.ACTION_MOVE:
	            int dx =(int)event.getRawX() - lastX;  
	            int dy =(int)event.getRawY() - lastY;  
	          
	            int left = v.getLeft() + dx;  
	            int top = v.getTop() + dy;  
	            int right = v.getRight() + dx;  
	            int bottom = v.getBottom() + dy;                      
	            if(left < 0){  
	                left = 0;  
	                right = left + v.getWidth();  
	            }                     
	            if(right > screenWidth){  
	                right = screenWidth;  
	                left = right - v.getWidth();  
	            }                     
	            if(top < 0){  
	                top = 0;  
	                bottom = top + v.getHeight();  
	            }                     
	            if(bottom > screenHeight){  
	                bottom = screenHeight;  
	                top = bottom - v.getHeight();  
	            }                     
	            v.layout(left, top, right, bottom);    
	            lastX = (int) event.getRawX();  
	            lastY = (int) event.getRawY();                    
	            break;  
	        case MotionEvent.ACTION_UP: 
	            break;                
	        }  
	        return false;     
	    }	
	
}


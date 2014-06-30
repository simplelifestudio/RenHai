/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMatchingActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android;

import com.opentok.android.OpentokException;
import com.opentok.android.Publisher;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.simplelife.renhai.android.data.WebRtcSession;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class RenHaiVideoTalkActivity extends Activity implements Publisher.Listener, Subscriber.Listener, Session.Listener{
	
	// automatically connect during Activity.onCreate
    private static final boolean AUTO_CONNECT = true;
    // automatically publish during Session.Listener.onSessionConnected
    private static final boolean AUTO_PUBLISH = true;
    // automatically subscribe during Session.Listener.onSessionReceivedStream IFF stream is our own
    private static final boolean SUBSCRIBE_TO_SELF = false;
    
	private RelativeLayout mSubscriberViewContainer;
	private RelativeLayout mPublisherViewContainer;
	private Publisher mPublisher;
    private Subscriber mSubscriber;
    private Session mSession;
    private WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_videotalk);
        
        mPublisherViewContainer  = (RelativeLayout) findViewById(R.id.video_publisherview);
        mSubscriberViewContainer = (RelativeLayout) findViewById(R.id.video_subscriberview);

        // Disable screen dimming
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Full Wake Lock");

        if (AUTO_CONNECT) {
            sessionConnect();
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();

        if (mSession != null) {
        	mSession.disconnect();
        }

        if (mWakeLock.isHeld()) {
        	mWakeLock.release();
        }
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mWakeLock.isHeld()) {
        	mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock.isHeld()) {
        	mWakeLock.release();
        }
    }
    
    private void sessionConnect() {
        mSession = Session.newInstance(RenHaiVideoTalkActivity.this, WebRtcSession.getSessionId(), RenHaiVideoTalkActivity.this);
        mSession.connect(WebRtcSession.getToken());
    }
    
	@Override
	public void onSessionConnected() {
		// TODO Auto-generated method stub
        // Session is ready to publish.
        if (AUTO_PUBLISH) {
            //Create Publisher instance.
            mPublisher = Publisher.newInstance(RenHaiVideoTalkActivity.this);
            mPublisher.setName("My First Publisher");
            mPublisher.setListener(RenHaiVideoTalkActivity.this);

            RelativeLayout.LayoutParams publisherViewParams =
                    new RelativeLayout.LayoutParams(mPublisher.getView().getLayoutParams().width,
                    		mPublisher.getView().getLayoutParams().height);
            publisherViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            publisherViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            publisherViewParams.bottomMargin = dpToPx(8);
            publisherViewParams.rightMargin = dpToPx(8);
            mPublisherViewContainer.setLayoutParams(publisherViewParams);
            mPublisherViewContainer.addView(mPublisher.getView());
            mSession.publish(mPublisher);
        }
		
	}

	@Override
	public void onSessionDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSessionDroppedStream(Stream _stream) {
		// TODO Auto-generated method stub
        Log.i("RenHaiVideoTalkActivity", String.format("stream dropped", _stream.toString()));
        
        boolean isMyStream = mSession.getConnection().equals(_stream.getConnection());
        if ((SUBSCRIBE_TO_SELF && isMyStream) || (!SUBSCRIBE_TO_SELF && !isMyStream)) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
		
	}

	@Override
	public void onSessionException(OpentokException arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSessionReceivedStream(Stream _stream) {
		// TODO Auto-generated method stub
		Log.i("RenHaiVideoTalkActivity", "session received stream");

        boolean isMyStream = mSession.getConnection().equals(_stream.getConnection());
        //If this incoming stream is our own Publisher stream and subscriberToSelf is true let's look in the mirror.
        if ((SUBSCRIBE_TO_SELF && isMyStream) || (!SUBSCRIBE_TO_SELF && !isMyStream)) {
            mSubscriber = Subscriber.newInstance(RenHaiVideoTalkActivity.this, _stream);
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,
                            getResources().getDisplayMetrics().heightPixels);
            View subscriberView = mSubscriber.getView();
            subscriberView.setLayoutParams(params);
            mSubscriberViewContainer.addView(mSubscriber.getView());
            mSubscriber.setListener(RenHaiVideoTalkActivity.this);
            mSession.subscribe(mSubscriber);
        }		
	}

	@Override
	public void onSubscriberConnected(Subscriber arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscriberException(Subscriber arg0, OpentokException arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPublisherChangedCamera(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPublisherException(OpentokException arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPublisherStreamingStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPublisherStreamingStopped() {
		// TODO Auto-generated method stub
		
	}
	
    public Publisher getPublisher() {
        return mPublisher;
    }

    public Subscriber getSubscriber() {
        return mSubscriber;
    }

    public Session getSession() {
        return mSession;
    }

    public RelativeLayout getPublisherView() {
        return mPublisherViewContainer;
    }

    public RelativeLayout getSubscriberView() {
        return mSubscriberViewContainer;
    }
	
	/**
     * Converts dp to real pixels, according to the screen density.
     * @param dp A number of density-independent pixels.
     * @return The equivalent number of real pixels.
     */
    private int dpToPx(int dp) {
        double screenDensity = this.getResources().getDisplayMetrics().density;
        return (int) (screenDensity * (double) dp);
    }

}

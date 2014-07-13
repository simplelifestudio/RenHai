package com.simplelife.renhai.android.video;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.R;
import com.simplelife.renhai.android.RenHaiMatchingActivity;
import com.simplelife.renhai.android.RenHaiVideoTalkActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SubscriberControlFragment extends Fragment implements
		View.OnClickListener {

	private final Logger mlog = Logger.getLogger(SubscriberControlFragment.class);

	private boolean mSubscriberWidgetVisible = false;
	private ImageButton mSubscriberMute;
	private TextView mSubscriberName;
	private RelativeLayout mSubContainer;
	private RelativeLayout mSubInfoContainer;
	private RelativeLayout mRcvMsgContainer;
	private TextView mShowMsg;
	private Button mReplyMsg;

	// Animation constants
	private static final int ANIMATION_DURATION = 500;
	private static final int SUBSCRIBER_CONTROLS_DURATION = 7000;

	private SubscriberCallbacks mCallbacks = sOpenTokCallbacks;
	private RenHaiVideoTalkActivity videoActivity;

	public interface SubscriberCallbacks {
		public void onMuteSubscriber();

		void onReplyMsg();
	}

	private static SubscriberCallbacks sOpenTokCallbacks = new SubscriberCallbacks() {

		@Override
		public void onMuteSubscriber() {
			return;
		}
		
		@Override
		public void onReplyMsg() {
			return;
		}

	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mlog.info("On attach Subscriber control fragment");
		videoActivity = (RenHaiVideoTalkActivity) activity;
		if (!(activity instanceof SubscriberCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callback");
		}

		mCallbacks = (SubscriberCallbacks) activity;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_video_sub_control,
				container, false);

		mSubContainer = (RelativeLayout) videoActivity
				.findViewById(R.id.fragment_sub_container);

		showSubscriberWidget(mSubscriberWidgetVisible, false);

		mSubscriberMute = (ImageButton) rootView
				.findViewById(R.id.muteSubscriber);
		mSubscriberMute.setOnClickListener(this);

		mSubscriberName = (TextView) rootView.findViewById(R.id.subscriberName);
		
		mSubInfoContainer = (RelativeLayout) rootView.findViewById(R.id.subscriberWidget);
		mRcvMsgContainer = (RelativeLayout) rootView.findViewById(R.id.video_rcvmsgwidget);
		mShowMsg  = (TextView) rootView.findViewById(R.id.video_recvmsg);
		mReplyMsg = (Button) rootView.findViewById(R.id.video_replymsg);
		mReplyMsg.setOnClickListener(this);

		if (videoActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container
					.getLayoutParams();

			DisplayMetrics metrics = new DisplayMetrics();
			videoActivity.getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);

			params.width = metrics.widthPixels - videoActivity.dpToPx(48);
			container.setLayoutParams(params);
		}

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();

		mlog.info("On detach Subscriber control fragment");
		mCallbacks = sOpenTokCallbacks;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.muteSubscriber:
			muteSubscriber();
			break;
			
		case R.id.video_replymsg:
			replyMsg();
			break;
		}
	}

	private Runnable mSubscriberWidgetTimerTask = new Runnable() {
		@Override
		public void run() {
			showSubscriberWidget(false);
		}
	};

	public void showSubscriberWidget(boolean show) {
		showSubscriberWidget(show, true);
	}
	
	public void showReceivedMsg(String _content) {
		mShowMsg.setText(_content);
		mSubInfoContainer.setVisibility(View.GONE);
		mRcvMsgContainer.setVisibility(View.VISIBLE);
		showSubscriberWidget(true);
	}

	private void showSubscriberWidget(boolean show, boolean animate) {
		mSubContainer.clearAnimation();
		mSubscriberWidgetVisible = show;
		float dest = show ? 1.0f : 0.0f;
		AlphaAnimation aa = new AlphaAnimation(1.0f - dest, dest);
		aa.setDuration(animate ? ANIMATION_DURATION : 1);
		aa.setFillAfter(true);
		mSubContainer.startAnimation(aa);

		if (show) {
			mSubContainer.setVisibility(View.VISIBLE);
		} else {
			mSubContainer.setVisibility(View.GONE);
		}

	}

	public void subscriberClick() {
		mSubInfoContainer.setVisibility(View.VISIBLE);
		mRcvMsgContainer.setVisibility(View.GONE);
		if (!mSubscriberWidgetVisible) {
			showSubscriberWidget(true);
		} else {
			showSubscriberWidget(false);
		}

		initSubscriberUI();
	}

	public void muteSubscriber() {
		mCallbacks.onMuteSubscriber();

		mSubscriberMute.setImageResource(videoActivity.getmSubscriber()
				.getSubscribeToAudio() ? R.drawable.unmute_sub
				: R.drawable.mute_sub);
	}
	
	public void replyMsg() {
		mCallbacks.onReplyMsg();
	}

	public void initSubscriberUI() {
		videoActivity.getmHandler().removeCallbacks(
				mSubscriberWidgetTimerTask);
		videoActivity.getmHandler().postDelayed(mSubscriberWidgetTimerTask,
				SUBSCRIBER_CONTROLS_DURATION);
		mSubscriberName.setText(videoActivity.getmSubscriber().getStream()
				.getName());
		mSubscriberMute.setImageResource(videoActivity.getmSubscriber()
				.getSubscribeToAudio() ? R.drawable.unmute_sub
				: R.drawable.mute_sub);
	}

}

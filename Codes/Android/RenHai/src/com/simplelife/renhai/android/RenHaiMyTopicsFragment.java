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

import com.simplelife.renhai.android.data.InterestLabelMap;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgAppDataSyncReq;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgServerDataSyncReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.timer.RenHaiTimerHelper;
import com.simplelife.renhai.android.timer.RenHaiTimerProcessor;
import com.simplelife.renhai.android.ui.RenHaiDraggableGridView;
import com.simplelife.renhai.android.ui.RenHaiDraggableGridViewListener;
import com.simplelife.renhai.android.ui.RenHaiImageView;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RenHaiMyTopicsFragment extends Fragment {
	
	private final int MYTOPIC_MSG_OKTODEFINEINTEREST = 2000;
	private final int MYTOPIC_MSG_INTERESTDEFINED = 2001;
	private final int MYTOPIC_MSG_INTERESTCHANGED = 2002;
	private final int MYTOPIC_MSG_INTERESTDEFINEDBYMANUAL = 2003;
	private final int MYTOPIC_MSG_TIMETOUPDATE = 2004;
	private final int MYTOPIC_MSG_INVALIDLABEL = 2005;
	private final int MYTOPIC_MSG_LABELREDEFINED = 2006;
		
	private RenHaiDraggableGridView mMyInterestsGrid;
	private RenHaiDraggableGridView mGlbInterestsGrid;
	private Button mCreateInt;
	private Button mFreshBtn;
	private TextView mGlbIntEmpty;
	private ProgressBar mProgressBar;
	private ImageView mGuideImage;
	private ImageView mGuideImage2;
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	boolean isMove = false;
	private SharedPreferences mSharedPrefs;
		
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
    	mCreateInt    = (Button)rootView.findViewById(R.id.mytopics_create);
    	mFreshBtn     = (Button)rootView.findViewById(R.id.mytopics_refresh);
    	mGlbIntEmpty  = (TextView)rootView.findViewById(R.id.mytopics_glbintempty);
    	mProgressBar  = (ProgressBar)rootView.findViewById(R.id.mytopics_progressbar);
    	mGuideImage   = (ImageView)rootView.findViewById(R.id.mytopics_guide);
    	mGuideImage2  = (ImageView)rootView.findViewById(R.id.mytopics_guide2);
    	
    	mSharedPrefs = getActivity().getSharedPreferences(RenHaiDefinitions.RENHAI_SHAREDPREF_FIRSTSTART, 0);
	
    	return rootView;   
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	mWebSocketHandle = RenHaiWebSocketProcess.getNetworkInstance(getActivity().getApplication());
    	
    	mCreateInt.setOnClickListener(mCreateNewLabelListener);   	
    	mFreshBtn.setOnClickListener(mFreshGlbIntLabelListener);
    	mProgressBar.setVisibility(View.INVISIBLE);
    	
    	if(true == RenHaiInfo.InterestLabel.isPersonalIntLabelsNotDefined())
    	{
    		// The personal interest is not defined
    		onDefinePersonalInterestDialog();
    	}    	
    	
    	// Update the personal interests label grid
    	onUpdateMyInterestGrid(true);
    	
    	// Update the global interest label grid
    	onUpdateGlobalInterestGrid();
    	
    	setListeners();
    }
    
    private void setListeners(){
    	mMyInterestsGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				
				onEditOrDeleteTheIntLabel(arg2);				
			}
		});
    	
    	mMyInterestsGrid.setOnRearrangeListener(new RenHaiDraggableGridViewListener() {

			@Override
			public void onRearrange(int oldIndex, int newIndex) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onRearrangeAll() {
				onReorderLabels();				
			}
    		
    	});
    	
    	mGlbInterestsGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				
				if(arg1 != null)
				{
					final RenHaiImageView tMoveView = new RenHaiImageView(getActivity());
					String tLabelText = ((RenHaiImageView)arg1).getText();
					if(true == isLabelAlreayDefined(tLabelText))
						return;					
					
					tMoveView.setImageBitmap(getThumb(tLabelText));
					tMoveView.setText(tLabelText);
					
					final int[] startLocation = new int[2];
					arg1.getLocationInWindow(startLocation);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							try {
								int[] endLocation = new int[2];
								mMyInterestsGrid.getLocationInWindow(endLocation);
								endLocation[0] += mMyInterestsGrid.getCoorFromIndex(mMyInterestsGrid.getChildCount()).x;
								endLocation[1] += mMyInterestsGrid.getCoorFromIndex(mMyInterestsGrid.getChildCount()).y;
								MoveAnim(tMoveView, startLocation , endLocation);							
							} catch (Exception localException) {
							}
						}
					}, 50L);
				}
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
			sendServerDataSyncReq();				
		}
	};
    
	private void MoveAnim(final View moveView, int[] startLocation,int[] endLocation) {
		int[] initLocation = new int[2];
		moveView.getLocationInWindow(initLocation);
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
		TranslateAnimation moveAnimation = new TranslateAnimation(
				startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(300L);
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);
		moveAnimationSet.addAnimation(moveAnimation);
		mMoveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				isMove = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				moveViewGroup.removeView(mMoveView);
				addInterestLabel(((RenHaiImageView)moveView).getText());
				mMyInterestsGrid.addView((RenHaiImageView)moveView);
				mMyInterestsGrid.refreshDrawableState();
				mUpdateTimer.resetTimer();
				isMove = false;
			}
		});
	}
	
	private ViewGroup getMoveViewGroup() {
		ViewGroup moveViewGroup = (ViewGroup) getActivity().getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(getActivity());
		moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}
	
	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}
	
    private void onUpdateMyInterestGrid(boolean _ifFirstEntry){
    	mMyInterestsGrid.removeAllViews();   	
    	for(int i=0; i < RenHaiInfo.InterestLabel.getMyIntLabelNum(); i++)
    	{
    		RenHaiImageView tIntLabel = new RenHaiImageView(getActivity());
    		tIntLabel.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getMyIntLabel(i).getIntLabelName()));
    		tIntLabel.setText(RenHaiInfo.InterestLabel.getMyIntLabel(i).getIntLabelName());
    		mMyInterestsGrid.addView(tIntLabel);
    	}
    	
    	if(isFirstTimeToShowMyIntLabel() && (RenHaiInfo.InterestLabel.getMyIntLabelNum()>0))
    	{
    		mGuideImage.setVisibility(View.VISIBLE);
    		mGuideImage.setOnClickListener(new View.OnClickListener() { 
                public void onClick(View v) { 
               	 mGuideImage.setVisibility(View.GONE); 
                } 
            });
    		updateFirstTimeShowMyIntFlag(false);
    	}
    	
    	// Use the resetTimer rather than the startTimer in case of the 
    	// frequently operation of adding interest labels
    	if(_ifFirstEntry != true)
    		mUpdateTimer.resetTimer();
    }
    
    public void onUpdateGlobalInterestGrid() {  	
    	/*
    	RenHaiImageView tGlbIntLabel = new RenHaiImageView(getActivity());
		tGlbIntLabel.setImageBitmap(getThumb(getString(R.string.mytopics_test)));
		tGlbIntLabel.setText(getString(R.string.mytopics_test));
		mGlbInterestsGrid.addView(tGlbIntLabel);*/
    	
    	if(RenHaiInfo.InterestLabel.getCurrHotLabelNum() > 0)
    	{
    		mGlbIntEmpty.setVisibility(View.INVISIBLE);
    		mGlbInterestsGrid.removeAllViews();
    		for(int i=0; i < RenHaiInfo.InterestLabel.getCurrHotLabelNum(); i++)
    		{
    			RenHaiImageView tGlbIntLabel = new RenHaiImageView(getActivity());
        		tGlbIntLabel.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getCurrHotIntLabel(i).getIntLabelName()));
        		tGlbIntLabel.setText(RenHaiInfo.InterestLabel.getCurrHotIntLabel(i).getIntLabelName());
        		mGlbInterestsGrid.addView(tGlbIntLabel);
    		} 
    		mGlbInterestsGrid.setVisibility(View.VISIBLE);
    		
    		if(isFirstTimeToShowGlbIntLabel())
    		{
    			mGuideImage2.setVisibility(View.VISIBLE);
    			mGuideImage2.setOnClickListener(new View.OnClickListener() { 
    	            public void onClick(View v) { 
    	            	mGuideImage2.setVisibility(View.GONE); 
    	            } 
    	        }); 
    			updateFirstTimeShowGlbIntFlag(false);
    		}

    	}
    	else{    		
    		mGlbIntEmpty.setVisibility(View.VISIBLE);
    		mGlbInterestsGrid.setVisibility(View.INVISIBLE);
    	}
    	mProgressBar.setVisibility(View.INVISIBLE);
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
        	    	String tAppDataSyncReqMsg = RenHaiMsgAppDataSyncReq.constructUpdateMsg().toString();
        	    	mWebSocketHandle.sendMessage(tAppDataSyncReqMsg);
        	    	mUpdateTimer.stopTimer();
        	    	break;
        	    }
        	    case MYTOPIC_MSG_INVALIDLABEL:
        	    {
        	    	onInValidIntLabelDefinedDialog();
        	    	break;
        	    }
        	    case MYTOPIC_MSG_LABELREDEFINED:
        	    {
        	    	onInterestLabelDoubleAddDialog();
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
			            	if((false == isLabelAlreayDefined(tInput)) && (true == isLabelValid(tInput)))
			            	{
			            		InterestLabelMap tIntLabelMap = new InterestLabelMap();
				            	tIntLabelMap.setIntLabelName(tInput);
				            	tIntLabelMap.setLabelOrder(RenHaiInfo.InterestLabel.getMyIntLabelNum()+1);
				            	RenHaiInfo.InterestLabel.putMyIntLabel(tIntLabelMap);
				            	Message t_MsgListData = new Message();
				            	if (true == _isForFirstTime)
				            		t_MsgListData.what = MYTOPIC_MSG_INTERESTDEFINED;
				            	else
				            		t_MsgListData.what = MYTOPIC_MSG_INTERESTDEFINEDBYMANUAL;
						        									
						        handler.sendMessage(t_MsgListData);	
			            	}
		            				            					        
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
	
	private void onInterestLabelDoubleAddDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(getString(R.string.mytopics_intlbreddialogtitle));
		builder.setMessage(getString(R.string.mytopics_intlbreddialogmsg));		
		builder.setPositiveButton(R.string.mytopics_intlbreddialogposbtn, new DialogInterface.OnClickListener() {
		    @Override
	        public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();    	
		    }
		});

		builder.create().show();
	}
	
	private void onInValidIntLabelDefinedDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(getString(R.string.mytopics_invalidlabdialogtitle));
		builder.setMessage(getString(R.string.mytopics_invalidlabdialogmsg));		
		builder.setPositiveButton(R.string.mytopics_invalidlabdialogposbtn, new DialogInterface.OnClickListener() {
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
	
	public void sendServerDataSyncReq() {
		String tServerDataSyncReqMsg = RenHaiMsgServerDataSyncReq.constructMsg().toString();    	
		mWebSocketHandle.sendMessage(tServerDataSyncReqMsg);
	}
    
	public void addInterestLabel(String _label) {
		InterestLabelMap tIntLabelMap = new InterestLabelMap();
    	tIntLabelMap.setIntLabelName(_label);
    	tIntLabelMap.setLabelOrder(RenHaiInfo.InterestLabel.getMyIntLabelNum()+1);
    	RenHaiInfo.InterestLabel.putMyIntLabel(tIntLabelMap);
	}
	
	public boolean isLabelAlreayDefined(String _label) {
		int tLabelNum = mMyInterestsGrid.getChildCount();

		for(int viewIdx=0; viewIdx<tLabelNum; viewIdx++)
		{
			String tLabelName = ((RenHaiImageView)mMyInterestsGrid.getChildAt(viewIdx)).getText();
			if(tLabelName.equals(_label))
			{
				Message t_MsgListData = new Message();
		        t_MsgListData.what = MYTOPIC_MSG_LABELREDEFINED;									
		        handler.sendMessage(t_MsgListData);	
				return true;
			}
		}
		return false;
	}
	
	public boolean isLabelValid(String _label) {
		if(_label.equals(""))
		{
			Message t_MsgListData = new Message();
	        t_MsgListData.what = MYTOPIC_MSG_INVALIDLABEL;									
	        handler.sendMessage(t_MsgListData);	
			return false;
		}			
		//TODO: Add conditions here
		return true;
	}
    ///////////////////////////////////////////////////////////////////////
    // Timer Callbacks
    ///////////////////////////////////////////////////////////////////////
    RenHaiTimerHelper mUpdateTimer = new RenHaiTimerHelper(5000, new RenHaiTimerProcessor() {
        @Override
        public void onTimeOut() {
        	Message t_MsgListData = new Message();
        	t_MsgListData.what = MYTOPIC_MSG_TIMETOUPDATE;
        	handler.sendMessage(t_MsgListData);	       	
        }        
    });

	///////////////////////////////////////////////////////////////////////
	// Interface Callbacks
	///////////////////////////////////////////////////////////////////////
	public void onReorderLabels() {				
		int tLabelNum = mMyInterestsGrid.getChildCount();

		for(int viewIdx=0; viewIdx<tLabelNum; viewIdx++)
		{
			String tLabelName = ((RenHaiImageView)mMyInterestsGrid.getChildAt(viewIdx)).getText();
			for(int mapIdx=0; mapIdx<tLabelNum; mapIdx++)
			{
				InterestLabelMap tLabelMap = RenHaiInfo.InterestLabel.getMyIntLabel(mapIdx);
				if((tLabelMap.getIntLabelName().equals(tLabelName))
						&&(viewIdx != mapIdx))
				{
					InterestLabelMap tLabelMapOrig = RenHaiInfo.InterestLabel.getMyIntLabel(viewIdx);
					RenHaiInfo.InterestLabel.replaceMyIntLabel(viewIdx, tLabelMap);
					RenHaiInfo.InterestLabel.replaceMyIntLabel(mapIdx, tLabelMapOrig);
					tLabelMap.setLabelOrder(viewIdx+1);
					tLabelMapOrig.setLabelOrder(mapIdx+1);
					break;
				}
			}
		}
		mUpdateTimer.resetTimer();		
	}        
    
	///////////////////////////////////////////////////////////////////////
	// First time running
	///////////////////////////////////////////////////////////////////////
	private boolean isFirstTimeToShowMyIntLabel() {   	
    	// Retrieve the seeds info status by date via the shared preference file
    	return mSharedPrefs.getBoolean(RenHaiDefinitions.RENHAI_SHAREDPREF_FIRSTSTART_MYTOPIC1,true);    	
    }
	
	private boolean isFirstTimeToShowGlbIntLabel() {   	
    	// Retrieve the seeds info status by date via the shared preference file
    	return mSharedPrefs.getBoolean(RenHaiDefinitions.RENHAI_SHAREDPREF_FIRSTSTART_MYTOPIC2,true);    	
    }
    
    private void updateFirstTimeShowMyIntFlag(Boolean _inTag){
    	
    	SharedPreferences.Editor editor = mSharedPrefs.edit();
    	editor.putBoolean(RenHaiDefinitions.RENHAI_SHAREDPREF_FIRSTSTART_MYTOPIC1, _inTag);
    	editor.commit();    	
    }
    
private void updateFirstTimeShowGlbIntFlag(Boolean _inTag){
    	
    	SharedPreferences.Editor editor = mSharedPrefs.edit();
    	editor.putBoolean(RenHaiDefinitions.RENHAI_SHAREDPREF_FIRSTSTART_MYTOPIC2, _inTag);
    	editor.commit();    	
    }
}

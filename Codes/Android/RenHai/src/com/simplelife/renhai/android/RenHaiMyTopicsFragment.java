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

import com.simplelife.renhai.android.ui.RenHaiDraggableGridView;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RenHaiMyTopicsFragment extends Fragment {
	
	private final int MYTOPIC_MSG_OKTODEFINEINTEREST = 2000;
	private final int MYTOPIC_MSG_INTERESTDEFINED = 2001;
		
	RenHaiDraggableGridView mMyInterestsGrid;
	RenHaiDraggableGridView mGlbInterestsGrid;
	private final Logger mlog = Logger.getLogger(RenHaiMyTopicsFragment.class);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View rootView = inflater.inflate(R.layout.fragment_mytopic, container, false);
    	mMyInterestsGrid = (RenHaiDraggableGridView)(rootView.findViewById(R.id.mytopics_myinterests));
    	mGlbInterestsGrid = (RenHaiDraggableGridView)(rootView.findViewById(R.id.mytopics_globalinterests));
    	
    	if(true == RenHaiInfo.InterestLabel.isPersonalIntLabelsNotDefined())
    	{
    		// The personal interest is not defined
    		onDefinePersonalInterestDialog();
    	}
    	
    	// Only for test purpose
    	//RenHaiInfo.InterestLabel.initTestInterests(getActivity());
    	
    	onUpdateMyInterestGrid();
    	
    	if(RenHaiInfo.InterestLabel.getCurrHotLabelNum() > 0)
    	{
    		for(int i=0; i < RenHaiInfo.InterestLabel.getCurrHotLabelNum(); i++)
    		{
    			ImageView tGlbIntLabel = new ImageView(getActivity());
        		tGlbIntLabel.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getCurrHotIntLabel(i)));
        		mGlbInterestsGrid.addView(tGlbIntLabel);
    		}    		
    	}
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
	
    	return rootView;
    
    }
    
    private void setListeners(){
    	mMyInterestsGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mMyInterestsGrid.removeViewAt(arg2);
			}
		});
    }
    
    private void onUpdateMyInterestGrid(){
    	mMyInterestsGrid.removeAllViews();
    	for(int i=0; i < RenHaiInfo.InterestLabel.getMyIntLabelNum(); i++)
    	{
    		ImageView tIntLabel = new ImageView(getActivity());
    		tIntLabel.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getMyIntLabel(i)));
    		mMyInterestsGrid.addView(tIntLabel);
    	}
    }
    
	private Handler handler = new Handler(){  		  
        @Override  
        public void handleMessage(Message msg) {          	
        	switch (msg.what) {
        	    case MYTOPIC_MSG_OKTODEFINEINTEREST:
        	    {
        	    	onDefinePersonalInterestTextBuilder();       	    	
        	    	break;
        	    }
        	    case MYTOPIC_MSG_INTERESTDEFINED:
        	    {
        	    	onUpdateMyInterestGrid();
        	    	mMyInterestsGrid.refreshDrawableState();
        	    	onContinueToDefinePersonalInterestDialog();
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
	
	private void onDefinePersonalInterestTextBuilder() {
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
			            	RenHaiInfo.InterestLabel.putMyIntLabel(tInput);
			            	Message t_MsgListData = new Message();
					        t_MsgListData.what = MYTOPIC_MSG_INTERESTDEFINED;									
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
    
    
}

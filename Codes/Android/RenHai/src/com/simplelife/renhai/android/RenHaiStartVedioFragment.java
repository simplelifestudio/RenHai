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

import com.simplelife.renhai.android.ui.RenHaiCircleButton;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RenHaiStartVedioFragment extends Fragment {
	RenHaiCircleButton mCircleButton;
	TextView mOnlineCount;
	TextView mChatCount;
	
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
    	mOnlineCount.setText(formatCount(RenHaiInfo.ServerPoolStat.getOnlineCount()));
    	
    	mChatCount = (TextView)rootView.findViewById(R.id.startvedio_chatcount);
    	mChatCount.setText(formatCount(RenHaiInfo.ServerPoolStat.getChatCount()));
    	
    	/*
    	mCircleButton.setOnRadialViewValueChanged(new OnRadialViewValueChanged() {
			@Override
			public void onValueChanged(int value) {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.screenBrightness = value / 100.0f;
				//getWindow().setAttributes(lp);
			}
		});
		
		if((int) (getWindow().getAttributes().screenBrightness * 100) < 0)
			mCircleButton.setCurrentValue(50);
		else
			mCircleButton.setCurrentValue((int) (getWindow().getAttributes().screenBrightness * 100));*/
    	
    	return rootView;    
    }
    
    private String formatCount(int _count){
    	DecimalFormat df = new DecimalFormat("00000");
        String formatStr = df.format(_count);
        return formatStr;
    }
    
    private View.OnClickListener mStartBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mCircleButton.setShowPercentText(true);
			for(int i=0; i<99999999; i++)
			{
				
			}
			mCircleButton.setCurrentValue(30);
			mCircleButton.updatePercent();
			for(int i=0; i<99999999; i++)
			{
				
			}
			mCircleButton.setCurrentValue(60);
			mCircleButton.updatePercent();
			for(int i=0; i<99999999; i++)
			{
				
			}
			mCircleButton.setCurrentValue(90);
			mCircleButton.updatePercent();	
			
			Toast.makeText(getActivity(), "Button clicked", Toast.LENGTH_SHORT).show();
			
		}
	};

}

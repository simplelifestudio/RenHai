/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiStartVedioFragment.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-20. 
 */
package com.simplelife.renhai.android;

import com.simplelife.renhai.android.ui.RenHaiCircleButton;
import com.simplelife.renhai.android.ui.RenHaiCircleButton.OnRadialViewValueChanged;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class RenHaiStartVedioFragment extends Fragment {
	RenHaiCircleButton mCircleButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View rootView = inflater.inflate(R.layout.fragment_startvedio, container, false);
    	mCircleButton = (RenHaiCircleButton)rootView.findViewById(R.id.startvedio_button);
    	
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

}

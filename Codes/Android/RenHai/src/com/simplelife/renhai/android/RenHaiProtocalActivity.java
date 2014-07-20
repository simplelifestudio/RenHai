/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiProtocalActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class RenHaiProtocalActivity extends Activity {
	
	private TextView mProtocalBtnYes;
	private TextView mProtocalBtnNo;
	private TextView mActionBarTitle;
	private View mHomeIcon;
	private ActionBar mActionBar;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protocal);
		
		// Set up the action bar.
        mActionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        mActionBar.setHomeButtonEnabled(false);
		
		mProtocalBtnYes = (TextView)findViewById(R.id.protocal_btnyes);
		mProtocalBtnNo  = (TextView)findViewById(R.id.protocal_btnno);
		
		mProtocalBtnYes.setOnClickListener(mProtocalBtnYesListener);
		mProtocalBtnNo.setOnClickListener(mProtocalBtnNoListener);
		
		enableActionBarNote();
	}
	
	private View.OnClickListener mProtocalBtnYesListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    Intent intent = new Intent(RenHaiProtocalActivity.this, RenHaiIntroduceActivity.class);
		    startActivity(intent);
			finish();
			
		}
	};
	
	private View.OnClickListener mProtocalBtnNoListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
			
		}
	};
	
	private void enableActionBarNote(){
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
        		ActionBar.LayoutParams.MATCH_PARENT,
        		ActionBar.LayoutParams.MATCH_PARENT,
        		Gravity.CENTER);
        View viewTitleBar = getLayoutInflater().inflate(R.layout.framelayout_titlebar, null);
        mActionBar.setCustomView(viewTitleBar, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBarTitle = (TextView) findViewById(R.id.frame_titlebar_text);
        
        mHomeIcon = findViewById(android.R.id.home); 
		((View) mHomeIcon.getParent()).setVisibility(View.GONE); 
		mActionBarTitle.setText(R.string.protocal_title);
    }

}

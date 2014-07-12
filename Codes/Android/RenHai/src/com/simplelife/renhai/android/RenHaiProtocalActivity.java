/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiProtocalActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RenHaiProtocalActivity extends Activity {
	
	private TextView mProtocalBtnYes;
	private TextView mProtocalBtnNo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protocal);
		
		mProtocalBtnYes = (TextView)findViewById(R.id.protocal_btnyes);
		mProtocalBtnNo  = (TextView)findViewById(R.id.protocal_btnno);
		
		mProtocalBtnYes.setOnClickListener(mProtocalBtnYesListener);
		mProtocalBtnNo.setOnClickListener(mProtocalBtnNoListener);
	}
	
	private View.OnClickListener mProtocalBtnYesListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    Intent intent = new Intent(RenHaiProtocalActivity.this, RenHaiMainPageActivity.class);
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

}

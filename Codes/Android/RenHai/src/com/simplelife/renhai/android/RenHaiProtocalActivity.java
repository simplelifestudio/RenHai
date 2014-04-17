package com.simplelife.renhai.android;

import android.app.Activity;
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

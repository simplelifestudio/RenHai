/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiAssessActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android;

import java.math.BigDecimal;
import java.text.NumberFormat;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.utils.TimerConverter;

public class RenHaiAssessActivity extends Activity{
	private GridView mAssessGridView;
	private AssessAdapter mAssessAdapter;
	
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	private final Logger mlog = Logger.getLogger(RenHaiMatchingActivity.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assess);

		mAssessGridView = (GridView)findViewById(R.id.assess_gridview1);
		mAssessAdapter = new AssessAdapter(this);
		mAssessGridView.setAdapter(mAssessAdapter);
		setAssessGridViewListeners();
				
	}
	
	private void setAssessGridViewListeners(){
		mAssessGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int _index, long arg3) {
				Log.i("AssessActivity", "Item clicked, index is "+_index);
				mAssessAdapter.setCheckBoxes(_index);
								
			}
		});
    }
	
    ///////////////////////////////////////////////////////////////////////
    // GridView adapters
    ///////////////////////////////////////////////////////////////////////  	
    private class AssessAdapter extends BaseAdapter {
    	
    	private Context mContext;
    	private CheckBox mCheckBox0;
    	private CheckBox mCheckBox1;
    	private CheckBox mCheckBox2;
    	
    	public AssessAdapter(Context _context){
    		mContext = _context;
    	}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void setCheckBoxes(int _itemChecked){
			if(_itemChecked == 0)
			{
				mCheckBox1.setChecked(false);
				mCheckBox2.setChecked(false);
			}else if(_itemChecked == 1)
			{
				mCheckBox0.setChecked(false);
				mCheckBox2.setChecked(false);
			}else if(_itemChecked == 2)
			{
				mCheckBox0.setChecked(false);
				mCheckBox1.setChecked(false);
			}
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(null == convertView)
			{
				/*
				CheckBox tRadioButton = new CheckBox(mContext);
				tRadioButton.setTextColor(mContext.getResources().getColor(R.color.white));
				tRadioButton.setBackgroundColor(mContext.getResources().getColor(R.color.maingreen));
				tRadioButton.setHeight(96);
				tRadioButton.setPadding(0, 10, 0, 10);*/
				
				switch (position){
				    case 0:
				    {
				    	mCheckBox0 = new CheckBox(mContext);
				    	mCheckBox0.setTextColor(mContext.getResources().getColor(R.color.white));
				    	mCheckBox0.setBackgroundColor(mContext.getResources().getColor(R.color.maingreen));
				    	mCheckBox0.setHeight(96);
				    	mCheckBox0.setPadding(0, 10, 0, 10);
				    	mCheckBox0.setText(R.string.myimpression_happylabel);
				    	return mCheckBox0;			    	
				    }
				    case 1:
				    {
				    	mCheckBox1 = new CheckBox(mContext);
				    	mCheckBox1.setTextColor(mContext.getResources().getColor(R.color.white));
				    	mCheckBox1.setBackgroundColor(mContext.getResources().getColor(R.color.maingreen));
				    	mCheckBox1.setHeight(96);
				    	mCheckBox1.setPadding(0, 10, 0, 10);
				    	mCheckBox1.setText(R.string.myimpression_sosolabel);			    	
				    	return mCheckBox1;				    	
				    }
				    case 2:
				    {
				    	mCheckBox2 = new CheckBox(mContext);
				    	mCheckBox2.setTextColor(mContext.getResources().getColor(R.color.white));
				    	mCheckBox2.setBackgroundColor(mContext.getResources().getColor(R.color.maingreen));
				    	mCheckBox2.setHeight(96);
				    	mCheckBox2.setPadding(0, 10, 0, 10);
				    	mCheckBox2.setText(R.string.myimpression_sosolabel);
				    	mCheckBox2.setText(R.string.myimpression_disgustlabel);		
				    	return mCheckBox2;				    	
				    }
				    default:
				    {
				    	return convertView;
				    }
				}
			}
			else{
				return convertView;
			}
		} 
    	
    }

}

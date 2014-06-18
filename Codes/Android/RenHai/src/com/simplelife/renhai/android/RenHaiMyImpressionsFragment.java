/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMyImpressionsFragment.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-20. 
 */
package com.simplelife.renhai.android;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.ui.RenHaiDraggableGridView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class RenHaiMyImpressionsFragment extends Fragment {
	
	private final Logger mlog = Logger.getLogger(RenHaiMyImpressionsFragment.class);
	
	private GridView mGridView;
	private MyAdapter mAdapter;	
	TextView mGlbImpEmpty;
	RenHaiDraggableGridView mImpLabelsGrid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View rootView = inflater.inflate(R.layout.fragment_myimpression, container, false);
    	mGridView = (GridView)rootView.findViewById(R.id.myimpression_gridview);
    	mGlbImpEmpty = (TextView)rootView.findViewById(R.id.myimpression_impempty);
    	mImpLabelsGrid = (RenHaiDraggableGridView)rootView.findViewById(R.id.myimpression_implabels);
    	
    	mAdapter = new MyAdapter(this.getActivity());
    	mGridView.setAdapter(mAdapter);
    	mImpLabelsGrid.setVisibility(View.INVISIBLE);
    	
    	return rootView;
    }
    
    private class MyAdapter extends BaseAdapter {
    	
    	private Context mContext;
    	
    	public MyAdapter(Context _context){
    		mContext = _context;
    	}

		@Override
		public int getCount() {
			// We have 6 items totally
			return 6;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(null == convertView)
			{
				TextView tTextView = new TextView(mContext);
				tTextView.setText("Test");
				tTextView.setTextSize(20);
				tTextView.setTextColor(mContext.getResources().getColor(R.color.black));
				tTextView.setBackgroundColor(mContext.getResources().getColor(R.color.maingreen));
				return tTextView;
			}
			else{
				return convertView;
			}
		} 
    	
    }
}

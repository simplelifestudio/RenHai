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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RenHaiMyTopicsFragment extends Fragment {
	
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
    	
    	// Only for test purpose
    	RenHaiInfo.InterestLabel.initTestInterests(getActivity());
    	if(null == mMyInterestsGrid)
    	{
    		mlog.error("mMyInterestsGrid is null!");
    	}else
    	{
    		ImageView tIntLabel1 = new ImageView(getActivity());
        	tIntLabel1.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getInterest(0)));
    		mMyInterestsGrid.addView(tIntLabel1);
    		ImageView tIntLabel2 = new ImageView(getActivity());
    		tIntLabel2.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getInterest(1)));
    		mMyInterestsGrid.addView(tIntLabel2);
    		ImageView tIntLabel3 = new ImageView(getActivity());
    		tIntLabel3.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getInterest(2)));
    		mMyInterestsGrid.addView(tIntLabel3);
    		ImageView tIntLabel4 = new ImageView(getActivity());
    		tIntLabel4.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getInterest(3)));
    		mMyInterestsGrid.addView(tIntLabel4);
    		ImageView tIntLabel5 = new ImageView(getActivity());
    		tIntLabel5.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getInterest(4)));
    		mMyInterestsGrid.addView(tIntLabel5);
    		ImageView tIntLabel6 = new ImageView(getActivity());
    		tIntLabel6.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getInterest(5)));
    		mMyInterestsGrid.addView(tIntLabel6);
    	}
    	
    	
		/*
    	for(int i=0; i < RenHaiInfo.InterestLabel.getCurrHotLabelNum(); i++)
    	{
    		ImageView tIntLabel = new ImageView(getActivity());
    		tIntLabel.setImageBitmap(getThumb(RenHaiInfo.InterestLabel.getInterest(i)));
    		mMyInterestsGrid.addView(tIntLabel);
    	}*/
	
    	return rootView;
    
    }
    
    private Bitmap getThumb(String s)
	{
		Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bmp);
	    Paint paint = new Paint();
	    
	    paint.setColor(getResources().getColor(R.color.maingreen));
	    paint.setTextSize(24);
	    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	    canvas.drawRect(new Rect(0, 0, 150, 150), paint);
	    paint.setColor(Color.WHITE);
	    paint.setTextAlign(Paint.Align.CENTER);
	    canvas.drawText(s, 75, 75, paint);
	    
		return bmp;
	}
}

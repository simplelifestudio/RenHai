/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiIntroduceActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-7-11. 
 */
package com.simplelife.renhai.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class RenHaiIntroduceActivity extends Activity {
	
	private ViewPager mViewPager;
	
	private ImageView mPage0;
	private ImageView mPage1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_main);        
        mViewPager = (ViewPager)findViewById(R.id.intro_viewpager);        
        
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());        
        mPage0 = (ImageView)findViewById(R.id.intro_page0);
        mPage1 = (ImageView)findViewById(R.id.intro_page1);
        
		// Retrieve the date info parameter
		//Bundle bundle = getIntent().getExtras();
		//mCallerName = bundle.getString("caller");
               
        LayoutInflater mLi = LayoutInflater.from(this);
        
        View view0 = mLi.inflate(R.layout.activity_introduce_page0, null);
        View view1 = mLi.inflate(R.layout.activity_introduce_page1, null);
        
        //mStartButton = (Button)findViewById(R.id.whats_new_start_btn);
        //mStartButton.setOnClickListener(myStartBtnListener);

        final ArrayList<View> views = new ArrayList<View>();
        views.add(view0);
        views.add(view1);   
                
        final ArrayList<String> titles = new ArrayList<String>();
        titles.add("tab0");
        titles.add("tab1");
        
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(views,titles);
		mViewPager.setAdapter(mPagerAdapter);
    }    
	
	public void onStartClick(View v) {
		Intent intent;
		intent = new Intent(RenHaiIntroduceActivity.this, RenHaiMainPageActivity.class);
		startActivity(intent);
		finish();			
	}

    public class MyOnPageChangeListener implements OnPageChangeListener {
    	    	
		public void onPageSelected(int page) {
			
			switch (page) {
			case 0:				
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.indicator_page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.indicator_page));
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.indicator_page_now));
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.indicator_page));
				break;
			}
		}
		
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		
		public void onPageScrollStateChanged(int arg0) {
		}
	}
    
    public class MyPagerAdapter extends PagerAdapter{
    	
    	private ArrayList<View> views;
    	@SuppressWarnings("unused")
		private ArrayList<String> titles;    	
    	
    	public MyPagerAdapter(ArrayList<View> views,ArrayList<String> titles){    		
    		this.views = views;
    		this.titles = titles;
    	}
    	
    	@Override
    	public int getCount() {
    		return this.views.size();
    	}

    	@Override
    	public boolean isViewFromObject(View arg0, Object arg1) {
    		return arg0 == arg1;
    	}
    	
    	public void destroyItem(View container, int position, Object object) {
    		((ViewPager)container).removeView(views.get(position));
    	}
    	
    	public Object instantiateItem(View container, int position) {
    		((ViewPager)container).addView(views.get(position));
    		return views.get(position);
    	}
    }

}

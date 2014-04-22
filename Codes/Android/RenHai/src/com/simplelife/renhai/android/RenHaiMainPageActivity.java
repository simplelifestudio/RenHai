/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMainPageActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-20. 
 */
package com.simplelife.renhai.android;

import org.apache.log4j.Logger;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class RenHaiMainPageActivity extends FragmentActivity implements ActionBar.TabListener{

	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	ViewPager mViewPager;
	private final Logger mlog = Logger.getLogger(RenHaiMainPageActivity.class);
	
	// Define the view pages
	public enum ViewPages{
		PAGE_STARTCHAT,
		PAGE_MYTOPICS,
		PAGE_MYIMPRESSIONS
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage_viewpager);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar tActionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        tActionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        tActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.mainpage_viewpager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
            	tActionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
        	tActionBar.addTab(
        			tActionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public RenHaiStartVedioFragment mRenHaiStartVedioFragment = null;
        public RenHaiMyTopicsFragment mRenHaiMyTopicsFragment = null;
        public RenHaiMyImpressionsFragment mRenHaiMyImpressionsFragment = null;
        
    	public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                	if (null == mRenHaiStartVedioFragment)
                		mRenHaiStartVedioFragment = new RenHaiStartVedioFragment();
                	return mRenHaiStartVedioFragment;
                	
                case 1:
                	if (null == mRenHaiMyTopicsFragment)
                		mRenHaiMyTopicsFragment = new RenHaiMyTopicsFragment();
                	return mRenHaiMyTopicsFragment;
                
                case 2:
                	if (null == mRenHaiMyImpressionsFragment)
                		mRenHaiMyImpressionsFragment = new RenHaiMyImpressionsFragment();
                	return mRenHaiMyImpressionsFragment;                	

                default:
                	if (null == mRenHaiStartVedioFragment)
                		mRenHaiStartVedioFragment = new RenHaiStartVedioFragment();
                	return mRenHaiStartVedioFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);

        }
    }
    
}

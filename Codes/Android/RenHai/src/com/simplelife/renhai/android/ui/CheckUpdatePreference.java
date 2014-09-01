package com.simplelife.renhai.android.ui;

import com.simplelife.renhai.android.R;
import com.simplelife.renhai.android.utils.AppVersionMgr;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckUpdatePreference extends Preference {
	
	public TextView mTitle;
	public TextView mVersion;
	public LinearLayout mFrame;
	public Context mContext;
	public RenHaiBadgeView mNewBadge;

	public CheckUpdatePreference(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
	}
	
	public CheckUpdatePreference(Context context, AttributeSet attrs) {   
		super(context, attrs);
		mContext = context;
	} 
	
	public CheckUpdatePreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		// TODO Auto-generated constructor stub
	}
	
	/*
	@Override    
    protected View onCreateView(ViewGroup parent) {
	    final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(
	             Context.LAYOUT_INFLATER_SERVICE);
	    Log.i("CheckUpdatePreference", "onCreateView ing!");
	    
	    final View layout = layoutInflater.inflate(R.layout.config_checkupdate, parent, false);
	    //mFrame   = (LinearLayout) viewGroup.findViewById(R.id.checkupdate_frame);	    
	     
	    return layout;
	}*/
	
	@Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mVersion = (TextView) view.findViewById(R.id.config_updateversion);
        mTitle   = (TextView) view.findViewById(android.R.id.title);
        if(mVersion != null)
        {
        	mVersion.setText(AppVersionMgr.getVersion(mContext));
        }
        
        if(mTitle != null)
        {
        	mTitle.setText(mContext.getString(R.string.config_update) + "            ");
        }
        mNewBadge = new RenHaiBadgeView(mContext, mTitle);
        mNewBadge.setText("New");
        mNewBadge.setTextColor(Color.WHITE);
        mNewBadge.setBadgeBackgroundColor(Color.RED);
        mNewBadge.setTextSize(12);
        //mNewBadge.toggle();
        /*
        mNewBadge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNewBadge.toggle();
			}
		});*/
        
	}
	
	public void setNewTag() {
		mNewBadge.toggle();
	}

}

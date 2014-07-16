/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiMyImpressionsFragment.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-20. 
 */
package com.simplelife.renhai.android;

import java.math.BigDecimal;
import java.text.NumberFormat;

import org.apache.log4j.Logger;

import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.data.RenHaiInfo;
import com.simplelife.renhai.android.ui.RenHaiDraggableGridView;
import com.simplelife.renhai.android.utils.TimerConverter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class RenHaiMyImpressionsFragment extends Fragment {
	
	private final Logger mlog = Logger.getLogger(RenHaiMyImpressionsFragment.class);
	
	private GridView mAssessGridView;
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
    	mAssessGridView = (GridView)rootView.findViewById(R.id.myimpression_gridview);
    	mGlbImpEmpty = (TextView)rootView.findViewById(R.id.myimpression_impempty);
    	mImpLabelsGrid = (RenHaiDraggableGridView)rootView.findViewById(R.id.myimpression_implabels);
    	
    	mAdapter = new MyAdapter(this.getActivity());
    	mAssessGridView.setAdapter(mAdapter);
    	//mImpLabelsGrid.setVisibility(View.INVISIBLE);
    	
    	onUpdateImpressionGridView();
    	
    	return rootView;
    }
    
    private void onUpdateImpressionGridView(){
    	mImpLabelsGrid.removeAllViews();
    	int tImpLabelNum = RenHaiInfo.ImpressionLabel.getMyImpLabelNum();    	
    	if(tImpLabelNum > 0)
    	{
    		for(int i=0; i < tImpLabelNum; i++)
        	{
        		ImageView tIntLabel = new ImageView(getActivity());
        		tIntLabel.setImageBitmap(getThumb( RenHaiInfo.ImpressionLabel.getMyImpLabelMap(i).getImpLabelName(),
        				     				       ""+RenHaiInfo.ImpressionLabel.getMyImpLabelMap(i).getAssessedCount()));
        		mImpLabelsGrid.addView(tIntLabel);
        	}
    		mGlbImpEmpty.setVisibility(View.GONE);
    	}else{
    		mImpLabelsGrid.setVisibility(View.GONE);
    		mGlbImpEmpty.setVisibility(View.VISIBLE);
    	}
    	
    }
    
    private Bitmap getThumb(String s)
	{
		Bitmap bmp = Bitmap.createBitmap(150, 75, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bmp);
	    Paint paint = new Paint();
	    
	    paint.setColor(getResources().getColor(R.color.maingreen));
	    paint.setTextSize(24);
	    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	    canvas.drawRect(new Rect(0, 0, 150, 75), paint);
	    paint.setColor(Color.WHITE);
	    paint.setTextAlign(Paint.Align.CENTER);
	    canvas.drawText(s, 75, 40, paint);
	    
	    
		return bmp;
	}
    
    private Bitmap getThumb(String s1, String s2)
	{
		Bitmap bmp = Bitmap.createBitmap(150, 75, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bmp);
	    Paint paint = new Paint();
	    
	    paint.setColor(getResources().getColor(R.color.maingreen));
	    paint.setTextSize(24);
	    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	    canvas.drawRect(new Rect(0, 0, 150, 75), paint);
	    paint.setColor(Color.WHITE);
	    paint.setTextAlign(Paint.Align.CENTER);
	    canvas.drawText(s1, 75, 35, paint);
	    canvas.drawText(s2, 75, 60, paint);
	    
		return bmp;
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
				String tTextToShow = null;
				TextView tTextView = new TextView(mContext);
				tTextView.setMaxLines(3);
				tTextView.setSingleLine(false);
				tTextView.setEllipsize(TruncateAt.MARQUEE);
				tTextView.setTextSize(16);
				tTextView.setGravity(Gravity.CENTER);
				tTextView.setTextColor(mContext.getResources().getColor(R.color.white));
				tTextView.setBackgroundColor(mContext.getResources().getColor(R.color.maingreen));
				tTextView.setPadding(0, 10, 0, 10);
				switch (position){
				    case 0:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_happylabel) 
				    			    + "\n"
				    			    + RenHaiInfo.AssessLabel.mHappyLabel.getAssessedCount();				    	
				    	break;				    	
				    }
				    case 1:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_sosolabel) 
				    			    + "\n"
				    			    + RenHaiInfo.AssessLabel.mSoSoLabel.getAssessedCount();			    	
				    	break;				    	
				    }
				    case 2:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_disgustlabel) 
				    			    + "\n"
				    			    + RenHaiInfo.AssessLabel.mDigustingLabel.getAssessedCount();
				    	
				    	break;				    	
				    }
				    case 3:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_chatcount) 
				    			    + "\n"
				    			    + RenHaiInfo.Profile.getChatTotalCount();				    	
				    	break;				    	
				    }
				    case 4:
				    {
				    	tTextToShow = mContext.getString(R.string.myimpression_chattotalduration) 
				    			    + "\n"
				    			    + TimerConverter.msToHms(RenHaiInfo.Profile.getChatTotalDuration());				    	
				    	break;				    	
				    }
				    case 5:
				    {
				    	String tLossRate = "";
				    	if(RenHaiInfo.Profile.getChatTotalCount() > 0)
				    	{
				    		double tLossCount  = (double)RenHaiInfo.Profile.getChatLossCount();
				    		double tTotalCount = (double)RenHaiInfo.Profile.getChatTotalCount();
				    		BigDecimal tValue = new BigDecimal(tLossCount / tTotalCount);
				    		NumberFormat tPercent = NumberFormat.getPercentInstance();
				    		tPercent.setMinimumFractionDigits(2);
				    		tPercent.setMaximumFractionDigits(2);
				    		tLossRate = tPercent.format(tValue);
				    	}
				    	else
				    	{
				    		tLossRate = "0.00%";
				    	}
				    	
				    	tTextToShow = mContext.getString(R.string.myimpression_chatlosscount) 
				    			    + "\n"
				    			    + tLossRate;
				    	break;				    	
				    }
				    default:
				    {
				    	break;
				    }
				}
				
				tTextView.setText(tTextToShow);

				/*
				String tText = "Test\nTest"; 
				SpannableString builder = new SpannableString(tText); 
				builder.setSpan(new AbsoluteSizeSpan(18), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				builder.setSpan(new AbsoluteSizeSpan(14), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				tTextView.setText(builder);*/
				return tTextView;
			}
			else{
				return convertView;
			}
		} 
    	
    }
}

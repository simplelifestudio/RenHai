/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiAssessActivity.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.simplelife.renhai.android.data.ImpressLabelMap;
import com.simplelife.renhai.android.data.PeerDeviceInfo;
import com.simplelife.renhai.android.jsonprocess.RenHaiMsgBusinessSessionReq;
import com.simplelife.renhai.android.networkprocess.RenHaiWebSocketProcess;
import com.simplelife.renhai.android.ui.RenHaiDraggableGridView;

public class RenHaiAssessActivity extends Activity{
	private GridView mAssessGridView;
	private AssessAdapter mAssessAdapter;
	private RadioGroup mAssessRdGroup;
	private RadioButton mRdBtnHappy;
	private RadioButton mRdBtnSoso;
	private RadioButton mRdBtnDisgust;
	private RenHaiDraggableGridView mImpressionGrid;
	private Button mCreateImp;
	private TextView mAssessBtnYes;
	private TextView mAssessBtnNo;
	
	private final int ASSESS_MSG_IMPLABELDEFINED = 6000;
	private final int ASSESS_MSG_IMPLABELMODIFIED = 6001;
	
	private RenHaiWebSocketProcess mWebSocketHandle = null;
	private final Logger mlog = Logger.getLogger(RenHaiAssessActivity.class);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assess);
		
		mAssessRdGroup = (RadioGroup)findViewById(R.id.assess_radiogroup);
		mRdBtnHappy    = (RadioButton)findViewById(R.id.assess_btnhappy);
		mRdBtnSoso     = (RadioButton)findViewById(R.id.assess_btnsoso);
		mRdBtnDisgust  = (RadioButton)findViewById(R.id.assess_btndisgust);		
		mAssessRdGroup.setOnCheckedChangeListener(mRadioGroupListener);
		
		mImpressionGrid = (RenHaiDraggableGridView)findViewById(R.id.assess_implabels);
		mCreateImp = (Button)findViewById(R.id.assess_create);
		mCreateImp.setOnClickListener(mCreateNewImpLabelListener);
		
		mAssessBtnYes = (TextView)findViewById(R.id.assess_btnyes);
		mAssessBtnNo  = (TextView)findViewById(R.id.assess_btnno);
		mAssessBtnYes.setOnClickListener(mAssessBtnYesListener);
		mAssessBtnNo.setOnClickListener(mAssessBtnNoListener);
		
		setGridListeners();

		/*
		mAssessGridView = (GridView)findViewById(R.id.assess_gridview1);
		mAssessAdapter = new AssessAdapter(this);
		mAssessGridView.setAdapter(mAssessAdapter);
		setAssessGridViewListeners();*/
				
	}
	
    ///////////////////////////////////////////////////////////////////////
    // Operation listeners
    /////////////////////////////////////////////////////////////////////// 
	private RadioGroup.OnCheckedChangeListener mRadioGroupListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int checkedId) {
			// TODO Auto-generated method stub
			if(checkedId == mRdBtnHappy.getId()){
            	PeerDeviceInfo.AssessResult.setAssessment(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_HAPPY);
            }
            if(checkedId==mRdBtnSoso.getId()){
            	PeerDeviceInfo.AssessResult.setAssessment(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_SOSO);                
            }
            if(checkedId==mRdBtnDisgust.getId()){
            	PeerDeviceInfo.AssessResult.setAssessment(RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_DISGUSTING);
            }
			
		}
		
	};	

	private View.OnClickListener mCreateNewImpLabelListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			onDefineImpLabelTextBuilder();			
		}
	};
	
	private void setGridListeners(){
		mImpressionGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				
				onEditOrDeleteTheImpLabel(arg2);				
			}
		});
    }
	
	private View.OnClickListener mAssessBtnYesListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			String tBusinessSessionReq = RenHaiMsgBusinessSessionReq.constructMsg(
	    			RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST, 
	    			RenHaiDefinitions.RENHAI_USEROPERATION_TYPE_ASSESSANDCONTINUE).toString();
	    	mWebSocketHandle.sendMessage(tBusinessSessionReq);
		    /*
			Intent intent = new Intent(RenHaiProtocalActivity.this, RenHaiAssessActivity.class);
		    startActivity(intent);
			finish();
			*/
		}
	};
	
	private View.OnClickListener mAssessBtnNoListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
			
		}
	};
	
	///////////////////////////////////////////////////////////////////////
	// Message processing
	/////////////////////////////////////////////////////////////////////// 	
	private void onUpdateImpLabelsGrid(){
		mImpressionGrid.removeAllViews();
    	for(int i=0; i < PeerDeviceInfo.AssessResult.getPeerAssessImpLabelNum(); i++)
    	{
    		ImageView tImpLabel = new ImageView(this);
    		tImpLabel.setImageBitmap(getThumb(PeerDeviceInfo.AssessResult.getAssessImpLabelMap(i).getImpLabelName()));
    		mImpressionGrid.addView(tImpLabel);
    	}

    }
	
	private void onDefineImpLabelTextBuilder() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getString(R.string.assess_addimplabeltitle));		
		final EditText tEditText = new EditText(this);
		builder.setView(tEditText);
		builder.setPositiveButton(R.string.assess_addimplabelposbtn, new DialogInterface.OnClickListener() {
		    @Override
	        public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();    	
		            new Thread() {  						
			            @Override
			            public void run() {
			            	String tInput = tEditText.getText().toString();
			            	ImpressLabelMap tImpLabelMap = new ImpressLabelMap();
			            	tImpLabelMap.setImpLabelName(tInput);
			            	PeerDeviceInfo.AssessResult.putAssessImpLabelMap(tImpLabelMap);
			            	Message t_MsgListData = new Message();
			            	t_MsgListData.what = ASSESS_MSG_IMPLABELDEFINED;					        								
					        handler.sendMessage(t_MsgListData);				            					        
			            }				
		            }.start();
		    }
		});

		builder.setNegativeButton(R.string.assess_addimplabeldnegbtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});

		builder.create().show();		
	}
	
    
	private void onEditOrDeleteTheImpLabel(final int _index) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getString(R.string.assess_editimplabeltitle));		
		final EditText tEditText = new EditText(this);
		tEditText.setText(PeerDeviceInfo.AssessResult.getAssessImpLabelMap(_index).getImpLabelName());
		builder.setView(tEditText);
		builder.setPositiveButton(R.string.assess_editimplabelposbtn, new DialogInterface.OnClickListener() {
		    @Override
	        public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();    	
		            new Thread() {  						
			            @Override
			            public void run() {
			            	String tInput = tEditText.getText().toString();
			            	PeerDeviceInfo.AssessResult.getAssessImpLabelMap(_index).reset();
			            	PeerDeviceInfo.AssessResult.getAssessImpLabelMap(_index).setImpLabelName(tInput);
			            	Message t_MsgListData = new Message();
					        t_MsgListData.what = ASSESS_MSG_IMPLABELMODIFIED;									
					        handler.sendMessage(t_MsgListData);			            					        
			            }				
		            }.start();
		    }
		});

		builder.setNegativeButton(R.string.assess_editimplabeldnegbtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	mImpressionGrid.removeViewAt(_index);
		    	PeerDeviceInfo.AssessResult.deleteImpLabel(_index);
		        dialog.dismiss();
		    }
		});
		
		builder.setNeutralButton(R.string.assess_editimplabelneubtn, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});

		builder.create().show();		
	}
	
    ///////////////////////////////////////////////////////////////////////
    // Inner message hander
    /////////////////////////////////////////////////////////////////////// 
	private Handler handler = new Handler(){  		  
        @Override  
        public void handleMessage(Message msg) {          	
        	switch (msg.what) {
        	    case ASSESS_MSG_IMPLABELDEFINED:
        	    {
        	    	onUpdateImpLabelsGrid();
        	    	mImpressionGrid.refreshDrawableState();
        	    	break;
        	    }
        	    case ASSESS_MSG_IMPLABELMODIFIED:
        	    {
        	    	onUpdateImpLabelsGrid();
        	    	mImpressionGrid.refreshDrawableState();
        	    	break;
        	    }
        	
        	}
        }
	};
	
	
	private void setAssessGridViewListeners(){
		mAssessGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int _index, long arg3) {
				Log.i("AssessActivity", "Item clicked, index is "+_index);
				mAssessAdapter.setCheckBoxes(_index);
								
			}
		});
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
				    	mCheckBox0.setOnClickListener(mCheckBox0Listener);
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
				    	mCheckBox1.setOnClickListener(mCheckBox1Listener);
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
				    	mCheckBox2.setOnClickListener(mCheckBox2Listener);
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
		private View.OnClickListener mCheckBox0Listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCheckBox1.setChecked(false);
				mCheckBox2.setChecked(false);
			}
		};
		private View.OnClickListener mCheckBox1Listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCheckBox0.setChecked(false);
				mCheckBox2.setChecked(false);
			}
		};
		private View.OnClickListener mCheckBox2Listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCheckBox0.setChecked(false);
				mCheckBox1.setChecked(false);
			}
		};
    }

}

package com.simplelife.renhai.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RenHaiFeedBackActivity extends Activity {	
	
	private ProgressDialog mProgressDialog = null;
	private EditText mContent;
	private EditText mContactway;

	@Override
    public void onCreate(Bundle savedInstanceState) {
   	    super.onCreate(savedInstanceState);
   	    setContentView(R.layout.activity_feedback);

   	    mContent = (EditText) findViewById(R.id.feedback_content);
   	    mContactway = (EditText) findViewById(R.id.feedback_contact);
   	    
   	    // Set a title for this page
		ActionBar tActionBar = getActionBar();
		tActionBar.setTitle(R.string.config_feedback_title);
		tActionBar.setDisplayHomeAsUpEnabled(true);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		//mContent.setText("");
		//mContactway.setText("");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		
		getLayoutInflater().setFactory(new Factory()
        {

			@Override
			public View onCreateView(String _name, Context _context,
					AttributeSet _attrs) {
				// TODO Auto-generated method stub
				if (  _name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")
		           || _name.equalsIgnoreCase("com.android.internal.view.menu.ActionMenuItemView"))
				{
					try
					{
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView(_name, null, _attrs);
						if(view instanceof TextView){
			                 ((TextView)view).setTextSize(14);
			            }
			            return view;
					}catch (InflateException e){
		              	e.printStackTrace();
		            } catch (ClassNotFoundException e){
		              	e.printStackTrace();
		            }
				}
				return null;
			}
		
        });
		inflater.inflate(R.menu.menu_feedback, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
		switch (item.getItemId()) {
			case android.R.id.home:
			{
				finish();
	            return true;
			}
			case R.id.feedback_send:
			{
				String tFeedbackText = mContent.getText().toString();
				String tContactWay   = mContactway.getText().toString();
				final String tTextToSend = tFeedbackText
						                 + "\n\n"
						                 + tContactWay;
				/*
				mProgressDialog = ProgressDialog.show(RenHaiFeedBackActivity.this, 
						getString(R.string.config_feedback_title), 
				        getString(R.string.config_feedback_sendnote), true, false);
		        mProgressDialog.setCanceledOnTouchOutside(true);
		        */
		        new Thread() {				
					@Override
					public void run() {
						Intent data=new Intent(Intent.ACTION_SENDTO);
						data.setData(Uri.parse("mailto:simplelife.chris@gmail.com"));
						data.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.config_feedback_title));
						data.putExtra(Intent.EXTRA_TEXT, tTextToSend);
						startActivity(data); 

					}
				}.start();
			}
		}
		
        return super.onOptionsItemSelected(item);
    }
}

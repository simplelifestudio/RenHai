package com.simplelife.renhai.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_feedback, menu);
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

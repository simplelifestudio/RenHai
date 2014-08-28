package com.simplelife.renhai.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class RenHaiFeedBackActivity extends Activity {	
	
	private ProgressDialog mProgressDialog = null; 

	@Override
    public void onCreate(Bundle savedInstanceState) {
   	    super.onCreate(savedInstanceState);
   	    setContentView(R.layout.activity_feedback);

   	    
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
				mProgressDialog = ProgressDialog.show(RenHaiFeedBackActivity.this, 
						getString(R.string.config_feedback_title), 
				        getString(R.string.config_feedback_sendnote), true, false);
		        mProgressDialog.setCanceledOnTouchOutside(true);
		        
		        new Thread() {				
					@Override
					public void run() {
						Intent data=new Intent(Intent.ACTION_SENDTO);
						data.setData(Uri.parse("mailto:simplelife.chris@gmail.com"));
						data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
						data.putExtra(Intent.EXTRA_TEXT, "这是内容");
						startActivity(data); 
					}
				}.start();
			}
		}
		
        return super.onOptionsItemSelected(item);
    }
}

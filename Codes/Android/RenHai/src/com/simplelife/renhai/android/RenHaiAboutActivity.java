package com.simplelife.renhai.android;

import com.simplelife.renhai.android.utils.AppVersionMgr;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class RenHaiAboutActivity extends Activity {
	
	private TextView mVersion;

	@Override
    public void onCreate(Bundle savedInstanceState) {
   	    super.onCreate(savedInstanceState);
   	    setContentView(R.layout.activity_about);
   	    mVersion = (TextView) findViewById(R.id.about_version);
   	    
   	    mVersion.setText(AppVersionMgr.getVersion(this));
   	    
   	    // Set a title for this page
		ActionBar tActionBar = getActionBar();
		tActionBar.setTitle(R.string.config_aboutpage_title);
		tActionBar.setDisplayHomeAsUpEnabled(true);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

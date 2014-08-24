package com.simplelife.renhai.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
   	    
   	    mVersion.setText(getVersion());
   	    
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
	
	public String getVersion() {
	     try {
	         PackageManager manager = this.getPackageManager();
	         PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	         String version = info.versionName;
	         return this.getString(R.string.config_aboutpage_version) + version;
	     } catch (Exception e) {
	         e.printStackTrace();
	         return this.getString(R.string.config_aboutpage_versionerror);
	     }
	 }

}

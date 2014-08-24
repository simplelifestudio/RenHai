package com.simplelife.renhai.android;

import com.simplelife.renhai.android.ui.RenHaiBadgeView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class RenHaiConfigActivity extends Activity {
	
	private static RenHaiBadgeView badge1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		getFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content, new RenHaiPreferenceFragment()).commit();
		
		// Set a title for this page
		ActionBar tActionBar = getActionBar();
		tActionBar.setTitle(R.string.config_pagetitle);
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
	
	public static class RenHaiPreferenceFragment extends PreferenceFragment 
    implements OnPreferenceChangeListener, OnPreferenceClickListener{
		
		private SharedPreferences mSharedPrefs;
		private Preference mPrefAbout;
		private Preference mPrefUpdate;
		
		private TextView mVersion;

		@Override  
        public void onCreate(Bundle savedInstanceState) {    
            super.onCreate(savedInstanceState);  
            addPreferencesFromResource(R.xml.renhai_preferences);
            
            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            
            mPrefAbout = (Preference)findPreference("config_about");
            mPrefUpdate = (Preference)findPreference("config_update");           
            
            mPrefAbout.setOnPreferenceClickListener(this);
            mPrefUpdate.setOnPreferenceClickListener(this);
            
            mVersion = (TextView) getActivity().findViewById(R.id.config_updateversion);
            //int tLayout = mPrefUpdate.getLayoutResource();
            //mVersion.setText(getVersion());
		}
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
			if(mPrefAbout == preference)
        	{
				onShowAboutPage();
        	}else if(mPrefUpdate == preference)
        	{
        		
        	}
			return false;
		}

		@Override
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			// TODO Auto-generated method stub
			return false;
		}
		
		private void onShowAboutPage() {
			Intent tIntent = new Intent(getActivity(), RenHaiAboutActivity.class);
    		startActivity(tIntent);  
		}
		
		public String getVersion() {
		     try {
		         PackageManager manager = getActivity().getPackageManager();
		         PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
		         String version = info.versionName;
		         return this.getString(R.string.config_aboutpage_version) + version;
		     } catch (Exception e) {
		         e.printStackTrace();
		         return this.getString(R.string.config_aboutpage_versionerror);
		     }
		 }
	}
	
	

}

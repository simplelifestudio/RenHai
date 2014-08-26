package com.simplelife.renhai.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.simplelife.renhai.android.R;

public class AppVersionMgr {
	
	public static String getVersion(Context _context) {
	     try {
	         PackageManager manager = _context.getPackageManager();
	         PackageInfo info = manager.getPackageInfo(_context.getPackageName(), 0);
	         String version = info.versionName;
	         return _context.getString(R.string.config_aboutpage_version) + " " + "V" + version;
	     } catch (Exception e) {
	         e.printStackTrace();
	         return _context.getString(R.string.config_aboutpage_versionerror);
	     }
	 }
}

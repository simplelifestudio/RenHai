/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiApplication.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-20. 
 */
package com.simplelife.renhai.android.ui;

import android.app.Application;
import android.view.WindowManager;

public class RenHaiApplication extends Application{
	private WindowManager.LayoutParams mWindowLayoutParam = new WindowManager.LayoutParams();
	
	public WindowManager.LayoutParams getLayoutParams() {
		return mWindowLayoutParam;
	}
}

/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiAppState.java
 *  RenHai
 *
 *  Created by Chris Li on 14-7-11. 
 */
package com.simplelife.renhai.android.data;

import com.simplelife.renhai.android.RenHaiDefinitions.RenHaiAppState;

public class AppStateMgr {
	
	public static RenHaiAppState mAppState = RenHaiAppState.DISCONNECTED;
	
	///////////////////////////////////////////////////////////////////////
	// State management
	///////////////////////////////////////////////////////////////////////
	public static void setMyAppStatus(RenHaiAppState _state) {
		mAppState = _state;
	}
	
	public static RenHaiAppState getMyAppStatus() {
		return mAppState;
	}	

}

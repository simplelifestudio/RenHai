/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiTimeProcess.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-25. 
 */
package com.simplelife.renhai.android.timeprocess;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class RenHaiTimeProcess {
	
	private static RenHaiTimeProcess mInstance = null;
	private final Logger mlog = Logger.getLogger(RenHaiTimeProcess.class);
	
	public RenHaiTimeProcess(){
		mlog.info("TimeProcess is starting!");
				
	}
	
	public static RenHaiTimeProcess getTimeProcessHandle(){
		if(null == mInstance)
			mInstance = new RenHaiTimeProcess();
		return mInstance;
	}
	
	public static void initTimeProcess(){
		if(null == mInstance)
			mInstance = new RenHaiTimeProcess();
	}
	
	public String getCurrentTime(){
		SimpleDateFormat  sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");     
	    String tDate = sDateFormat.format(new java.util.Date()); 
	    
	    return tDate;
	}

}

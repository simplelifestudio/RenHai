/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  TimerConverter.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-19. 
 */
package com.simplelife.renhai.android.utils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimerConverter {
	
	public static String secondsToHMS(int _second) {  
        int h = 0;  
        int m = 0;  
        int s = 0;  
        int temp = _second % 3600;  
        if (_second > 3600) {  
            h = _second / 3600;  
            if (temp != 0) {  
                if (temp > 60) {  
                    m = temp / 60;  
                    if (temp % 60 != 0) {  
                        s = temp % 60;  
                    }  
                } else {  
                    s = temp;  
                }  
            }  
        } else {  
            m = _second / 60;  
            if (_second % 60 != 0) {  
                s = _second % 60;  
            }  
        }  
        return h + ":" + m + ":" + s;  
    } 
	
	public static String msToHms(int _ms) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		String hms = formatter.format(_ms); 
		
		return hms;
	}
	
	public static  String msToHms2(int milliSecondTime) {		  
	  int hour = milliSecondTime /(60*60*1000);
	  int minute = (milliSecondTime - hour*60*60*1000)/(60*1000);
	  int seconds = (milliSecondTime - hour*60*60*1000 - minute*60*1000)/1000;
	  
	  if(seconds >= 60 )
	  {
	   seconds = seconds % 60;
	      minute+=seconds/60;
	  }
	  if(minute >= 60)
	  {
	    minute = minute % 60;
	    hour  += minute/60;
	  }
	  
	  String sh = "";
	  String sm ="";
	  String ss = "";
	  if(hour <10) {
	     sh = "0" + String.valueOf(hour);
	  }else {
	     sh = String.valueOf(hour);
	  }
	  if(minute <10) {
	     sm = "0" + String.valueOf(minute);
	  }else {
	     sm = String.valueOf(minute);
	  }
	  if(seconds <10) {
	     ss = "0" + String.valueOf(seconds);
	  }else {
	     ss = String.valueOf(seconds);
	  }
	  
	  return sh +":"+sm+":"+ ss;
	 }

}

/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  TimerConverter.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-19. 
 */
package com.simplelife.renhai.android.utils;

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

}

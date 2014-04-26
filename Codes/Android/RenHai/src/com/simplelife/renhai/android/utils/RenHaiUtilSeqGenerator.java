/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiUtilSeqGenerator.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-25. 
 */
package com.simplelife.renhai.android.utils;

import java.util.Random;

public class RenHaiUtilSeqGenerator {

	public static String genRandomSeq(int length)     
	{     
	    String val = "";     
	             
	    Random random = new Random();     
	    for(int i = 0; i < length; i++)     
	    {     
	        // Determine character or digit
	    	String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";    
	                 
	        if("char".equalsIgnoreCase(charOrNum))      
	        {     
	            int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;      
	            val += (char) (choice + random.nextInt(26));     
	        }     
	        else if("num".equalsIgnoreCase(charOrNum))      
	        {     
	            val += String.valueOf(random.nextInt(10));     
	        }     
	    }     
	             
	    return val;     
	}   
}

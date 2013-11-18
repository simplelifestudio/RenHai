/**
 * TestFunctions.java
 * 
 * History:
 *     2013-11-18: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.proxyserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 */
public class TestFunctions
{
	private static void testDateFormat()
	{
		TimeZone zone = TimeZone.getTimeZone("GMT+0800"); 
		//String dateStr = "2013-11-10 12:55:01 +0800";
		String dateStr = "2013-11-10 14:55:01";
		
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(zone);
		
		Date date = null;
		try
		{
			date = sdf.parse(dateStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if (date != null)
		{
			System.out.println(date.toString());
			System.out.println(sdf.getTimeZone().toString());
			System.out.println(zone.getDisplayName());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		testDateFormat();
	}
	
}

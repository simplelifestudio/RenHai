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

/**
 * 
 */
public class TestFunctions
{
	private static void testDateFormat()
	{
		String dateStr = "2013-11-10 12:55:01 +0800";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
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

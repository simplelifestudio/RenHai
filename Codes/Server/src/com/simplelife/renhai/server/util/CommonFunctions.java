/**
 * CommonFunctions.java
 * 
 * History:
 *     2013-9-8: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

import java.util.Random;

/**
 * 
 */
public class CommonFunctions
{
	private static char[] baseData = 
		{
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
			'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
			'Y', 'Z'
		};
	private static int baseDataLength = baseData.length;
	
	public static boolean IsNumric(String value)
	{
		return value.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	public static String getRandomString(int length)
	{
		char[] data = new char[length];
	    Random random = new Random();
	    for (int i = 0; i < length; i++)
	    {
	        data[i] = baseData[random.nextInt() % baseDataLength]; 
	    }
	    return new String(data);
	}
	
	
}

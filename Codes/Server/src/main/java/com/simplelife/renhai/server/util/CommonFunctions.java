/**
 * CommonFunctions.java
 * 
 * History:
 *     2013-9-8: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;

import java.io.File;
import java.util.Random;

/**
 * 
 */
public class CommonFunctions
{
	private static char[] baseData = 
		{
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
			'U', 'V', 'W', 'X', 'Y', 'Z'
		};
	private static int baseDataLength = baseData.length;
	
	public static boolean IsNumric(String value)
	{
		if (value == null || value.length() == 0)
		{
			return false;
		}
		return value.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	public static String getRandomString(int length)
	{
		char[] data = new char[length];
	    Random random = new Random();
	    for (int i = 0; i < length; i++)
	    {
	        data[i] = baseData[random.nextInt(baseDataLength)]; 
	    	//data[i] = baseData[i];
	    }
	    return new String(data);
	}
	
	public static String getJSONValue(String value)
    {
    	if (value == null || value.trim().length() == 0)
    	{
    		return null;
    	}
    	return value;
    }

	/**
	 * Add absolute path which Tomcat locates
	 * 
	 * @param relativePath
	 *            : relate path
	 * @return Full path
	 */
	public static String getWebAppPath(String relativePath)
	{
		File file = new File(".");
		String webappsRoot = file.getAbsolutePath().replace('\\', '/');
		webappsRoot = webappsRoot.replaceAll("/bin/.", "/webapps");
		
		String absPath;
		if (webappsRoot == null || webappsRoot.length() == 0)
		{
			absPath = "";
		}
		else
		{
			absPath = webappsRoot;
		}
		
		absPath += "/renhai";
		
		if (relativePath == null || relativePath.length() == 0)
		{
			return absPath + "/";
		}
		
		if (relativePath.charAt(0) != '/')
		{
			absPath += "/";
		}
		absPath += relativePath;
		return absPath;
	}
}

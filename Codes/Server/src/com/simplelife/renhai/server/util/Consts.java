/**
 * Consts.java
 * 
 * History:
 *     2013-9-3: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.util;


/**
 * 
 */
public class Consts
{
	public enum DeviceBusinessStatus
	{
		Init, Idle, WaitMatch, SessionBound
	}
	
	public enum DeviceServiceStatus
	{
		Normal, Forbidden
	}
	
	public enum BusinessSessionStatus
	{
		Idle, ChatConfirm, VideoChat, Assess
	}
	
	public static class BusinessType
	{
		public static final int Random = 0;
		public static final int Interest = 1;
	}
	
	public enum DBExistResult
	{
		Existent, NonExistent, ErrorOccurred
	}
}

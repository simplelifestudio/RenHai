/**
 * GlobalSetting.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;


/** */
public class GlobalSetting
{
	public static class TimeOut
	{
		public static int JSONMessageEcho = 9;
		public static int OnlineDeviceConnection = 30;
		public static int ChatConfirm = 15;
		public static int Assess = 60;
		public static int CheckExpiredToken = 3600;
		
		public static int SaveDataToDB = 30;
		public static int DeviceInIdel = 180;
	}
	
	public static class DBSetting
	{
		public static int MaxRecordCountForFlush = 100;
	}
}

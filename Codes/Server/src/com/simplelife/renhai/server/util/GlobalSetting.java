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
		public static int OnlineDeviceConnection = 30 * 1000;
		public static int ChatConfirm = 15;
		public static int Assess = 60;
		public static int CheckExpiredToken = 3600;
		
		public static int SaveDataToDB = 30;
		public static int DeviceInIdel = 180 * 1000;
	}
	
	public static class DBSetting
	{
		public static int MaxRecordCountForFlush = 100;
		public static boolean CacheEnabled = false;
	}
	
	public static class BusinessSetting
	{
		public static int OnlinePoolCapacity = 10000;
		public static int RandomBusinessPoolCapacity = 10000;
		public static int InterestBusinessPoolCapacity = 0;
		public static int DefaultLoadedImpressLabel = 10;
		
		public static boolean Encrypt = false;
		public static String EncryptKey = "20120801";
	}
}

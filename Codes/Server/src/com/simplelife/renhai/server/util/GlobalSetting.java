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
	public static void restoreToDefault()
	{
		TimeOut.JSONMessageEcho = 9;
		TimeOut.ChatConfirm = 15;
		TimeOut.Assess = 60;
		TimeOut.CheckExpiredToken = 3600;
				
		TimeOut.SaveDataToDB = 30;
		TimeOut.DeviceInIdle = 180 * 1000;
		TimeOut.OnlineDeviceConnection = 30 * 1000;
		TimeOut.PingInterval = 5 * 1000;
		
		DBSetting.MaxRecordCountForFlush = 100;
		DBSetting.CacheEnabled = false;
		
		BusinessSetting.OnlinePoolCapacity = 10000;
		BusinessSetting.RandomBusinessPoolCapacity = 10000;
		BusinessSetting.InterestBusinessPoolCapacity = 0;
		BusinessSetting.DefaultImpressLabelCount = 10;
		BusinessSetting.HotInterestLabelCount = 10;

		BusinessSetting.Encrypt = false;
		BusinessSetting.EncryptKey = "20120801";
		BusinessSetting.LengthOfSessionId = 16;
		BusinessSetting.LengthOfMessageSn = 16;
	}
	
	public static class TimeOut
	{
		public static int JSONMessageEcho = 9;
		public static int ChatConfirm = 15;
		public static int Assess = 60;
		public static int CheckExpiredToken = 3600;
		
		public static int SaveDataToDB = 30;
		public static int DeviceInIdle = 180*1000;
		
		public static int OnlineDeviceConnection = 5 * 1000;
		public static int PingInterval = 2 * 1000;
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
		public static int DefaultImpressLabelCount = 10;
		public static int HotInterestLabelCount = 10;
		
		public static boolean Encrypt = false;
		public static String EncryptKey = "20120801";
		public static int LengthOfSessionId = 16;
		public static int LengthOfMessageSn = 16;
	} 
}

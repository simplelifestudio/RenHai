/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiInfo.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android;

public class RenHaiInfo {
	
	public static int deviceId;		
	public static String deviceSn;
	
	public static class ServerAddr{
		public static boolean serviceStatus = false;
		public static String timeZone  = "GMT+0800";
		public static String beginTime = "2013-01-01 10:00:00";
		public static String endTime   = "2013-01-01 10:00:00";
		public static String protocal  = "ws";
		public static String serverIp  = "192.81.135.31";
		public static int serverPort   = 80;
		public static String path = "/renhai/websocket";
		public static String broadcastInfo = "";
		
	}
		
	public static class DeviceCard{		
		public static int deviceCardId;
		public static long registerTime;
		public static String deviceModel;
		public static String osVersion;
		public static String appVersion;
		public static String location;
		public static boolean isJailed;		
	}
	
	public static class Profile{
		public static int profileId;
		public static boolean serviceStatus;
		public static long unbanDate;
		public static long lastActivityTime;
		public static long createTime;
		public static boolean active;
		public static int interestCardId;
		public static int impressCardId;
		public static int chatTotalCount;
		public static int chatTotalDuration;
		public static int chatLossCount;
	}
	
	public class InterestLabelMap{
		public int globalInterestLabelId;
		public String interestLabelName;
		public int globalMatchCount;
		public int labelOrder;
		public boolean validFlag; 
	}
	
	public class ImpressLabelMap{
		public int globalImpressLabelId;
		public String impressLabelName;
		public int assessedCount;
		public int updateTime;
		public int assessCount; 
	}
	


}

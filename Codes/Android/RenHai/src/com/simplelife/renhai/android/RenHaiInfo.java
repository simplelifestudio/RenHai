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
	
	public static int deviceId = 0;		
	public static String deviceSn = "";
	public static String serverTimeOnFirstMsg = "";
	public static boolean isServerTimeRecord = false;
	
	public static void storeDeviceId(int _devId){
		deviceId = _devId;
	}
	
	public static int getDeviceId(){
		return deviceId;
	}
	
	public static void storeDeviceSn(String _devSn){
		deviceSn = _devSn;
	}
	
	public static String getDeviceSn(){
		return deviceSn;
	}
	
	public static void storeServerTimeOnFirstMsg(String _ServerTime){
		serverTimeOnFirstMsg = _ServerTime;
		isServerTimeRecord = true;
	}
	
	public static String getServerTimeOnFirstMsg(){
		return serverTimeOnFirstMsg;
	}
	
	public static boolean isServerTimeOnFirstMsgRecord(){
		return isServerTimeRecord;
	}
	
	public static class ServerAddr{
		public static int serviceStatus = 0;
		public static String timeZone  = "GMT+0800";
		public static String beginTime = "2013-01-01 10:00:00";
		public static String endTime   = "2013-01-01 10:00:00";
		public static String protocal  = "ws";
		public static String serverIp  = "192.81.135.31";
		public static int serverPort   = 80;
		public static String path = "/renhai/websocket";
		public static String broadcastInfo = "";
		
		public static void storeServiceStatus(int _status){
			serviceStatus = _status;
		}
		
		public static void storeTimeZone(String _timezone){
			timeZone = _timezone;
		}
		
		public static void storeBeginTime(String _beginTime){
			beginTime = _beginTime;
		}
		
		public static void storeEndTime(String _endTime){
			endTime = _endTime;
		}
		
		public static void storeProtocol(String _protocol){
			protocal = _protocol;
		}
		
		public static void storeServerIp(String _serverIp){
			serverIp = _serverIp;
		}
		
		public static void storeServerPort(int _port){
			serverPort = _port;
		}
		
		public static void storeServerPath(String _path){
			path = _path;
		}
		
		public static void storeBroadcastInfo(String _broadcast){
			broadcastInfo = _broadcast;
		}
		
		public static int getServiceStatus(){
			return serviceStatus;
		}
		
		public static String getTimeZone(){
			return timeZone;
		}
		
		public static String getBeginTime(){
			return beginTime;
		}
		
		public static String getEndTime(){
			return endTime;
		}
		
		public static String getProtocol(){
			return protocal;
		}
		
		public static String getServerIp(){
			return serverIp;
		}
		
		public static int getServerPort(){
			return serverPort;
		}
		
		public static String getServerPath(){
			return path;
		}
		
		public static String getBroadcastInfo(){
			return broadcastInfo;
		}		
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

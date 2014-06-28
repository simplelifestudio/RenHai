/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  PeerDeviceInfo.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android.data;

public class PeerDeviceInfo {
	public static int deviceId = 0;		
	public static String deviceSn = "";
	
	public static void storeDeviceId(int _devId){
		if((_devId != 0)&&(_devId != deviceId))
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
	
	public static class DeviceCard {
		public static int deviceCardId;
		public static String registerTime;
		public static String deviceModel;
		public static String osVersion;
		public static String appVersion;
		public static String location;
		
		// "Jail" is not an option for android, while "root"
		// is another thing. This field is not mandatory for
		// android devices, hence is set to false by default
		public static int isJailed = 0;
		
		public static void storeDeviceCardId(int _devId){
			deviceCardId = _devId;
		}
		
		public static void storeRegisterTime(String _regTime){
			registerTime = _regTime;
		}
		
		public static void storeDeviceModel(String _model){
			deviceModel = _model;
		}
		
		public static void storeOsVersion(String _osVersion){
			osVersion = _osVersion;
		}
		
		public static void storeAppVersion(String _appVersion){
			appVersion = _appVersion;
		}
		
		public static void storeLocation(String _location){
			location = _location;
		}
		
		public static void storeJailedStatus(int _jailStat){
			isJailed = _jailStat;
		}
		
		public static int getDeviceCardId(){
			return deviceCardId;
		}
		
		public static String getRegisterTime(){
			return registerTime;
		}
		
		public static String getDeviceModel(){
			return deviceModel;
		}
		
		public static String getOsVersion(){
			return osVersion;
		}
		
		public static String getAppVersion(){
			return appVersion;
		}
		
		public static String getLocation(){
			return location;
		}
		
		public static int getJailedStatus(){
			return isJailed;
		}
	}
	
	// ===============================================================================
	// Information of user device profile
	// ===============================================================================	
	public static class Profile{
		public static int profileId = 0;
		public static int serviceStatus = 0;
		public static long unbanDate = 0;
		public static long lastActivityTime = 0;
		public static String createTime = "2013-01-01 10:00:00";
		public static int active = 1;
		public static int interestCardId;
		public static int impressCardId;
		public static int chatTotalCount = 0;
		public static int chatTotalDuration = 0;
		public static int chatLossCount = 0;
		
		public static void storeProfileId(int _id){
			profileId = _id;
		}
		
		public static void storeServiceStatus(int _status){
			serviceStatus = _status;
		}
		
		public static void storeUnbanDate(long _unbanDate){
			unbanDate = _unbanDate;
		}
		
		public static void storeLastActiveTime(long _lastActiveTime){
			lastActivityTime = _lastActiveTime;
		}
		
		public static void storeCreatetTime(String _createTime){
			createTime = _createTime;
		}
		
		public static void storeActive(int _active){
			active = _active;
		}
		
		public static void storeInterestCardId(int _intCardId){
			interestCardId = _intCardId;
		}
		
		public static void storeImpressCardId(int _impCardId){
			impressCardId = _impCardId;
		}
		
		public static void storeChatTotalCound(int _chatCount){
			chatTotalCount = _chatCount;
		}
		
		public static void storeChatTotalDuration(int _chatDuration){
			chatTotalDuration = _chatDuration;
		}
		
		public static void storeChatLossCount(int _chatLoss){
			chatLossCount = _chatLoss;
		}
		
		public static int getProfileId(){
			return profileId;
		}
		
		public static int getServiceStatus(){
			return serviceStatus;
		}
		
		public static long getUnbanDate(){
			return unbanDate;
		}
		
		public static long getLastActiveTime(){
			return lastActivityTime;
		}
		
		public static String getCreateTime(){
			return createTime;
		}
		
		public static int getActive(){
			return active;
		}
		
		public static int getInterestCardId(){
			return interestCardId;
		}
		
		public static int getImpressCardId(){
			return impressCardId;
		}
		
		public static int getChatTotalCount(){
			return chatTotalCount;
		}
		
		public static int getChatTotalDuration(){
			return chatTotalDuration;
		}
		
		public static int getChatLossCount(){
			return chatLossCount;
		}
		
	}
}

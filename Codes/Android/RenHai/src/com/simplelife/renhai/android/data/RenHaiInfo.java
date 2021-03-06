/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  RenHaiInfo.java
 *  RenHai
 *
 *  Created by Chris Li on 14-4-11. 
 */
package com.simplelife.renhai.android.data;

import java.util.ArrayList;

import com.simplelife.renhai.android.RenHaiDefinitions;

public class RenHaiInfo {
	
	public static int deviceId = 0;		
	public static String deviceSn = "";
	public static String serverTimeOnFirstMsg = "";
	public static boolean isServerTimeRecord = false;
	public static boolean isAppDateSynced    = false;
	
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
	
	public static void setAppDataSyncronized(){
		isAppDateSynced = true;
	}
	
	public static boolean isAppDataSyncronized(){
		return isAppDateSynced;
	}
	
	// ===============================================================================
	// Information of server address associated
	// ===============================================================================	
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

	// ===============================================================================
	// Information of server pool states 
	// ===============================================================================	
	public static class ServerPoolStat{
		public static int onlineCount = 0;
		public static int randomCount = 0;
		public static int interestCount = 0;
		public static int chatCount = 0;
		public static int randomChatCount = 0;
		public static int interestChatCount = 0;
		public static int managementData = 0;
		
		public static int onlineCapa = 0;
		public static int randomCapa = 0;
		public static int interestCapa = 0;
		
		public static void storeOnlineCount(int _onlineCount){
			onlineCount = _onlineCount;
		}
		
		public static void storeRandomCount(int _randomCount){
			randomCount = _randomCount;
		}
		
		public static void storeInterestCount(int _interestCount){
			interestCount = _interestCount;
		}
		
		public static void storeChatCount(int _chatCount){
			chatCount = _chatCount;
		}
		
		public static void storeRandomChatCount(int _randomChatCount){
			randomChatCount = _randomChatCount;
		}
		
		public static void storeInterestChatCount(int _interestChatCount){
			interestChatCount = _interestChatCount;
		}
		
		public static void storeManagementData(int _mgtData){
			managementData = _mgtData;
		}
		
		public static void storeOnlineCapa(int _onlineCapa){
			onlineCapa = _onlineCapa;
		}
		
		public static void storeRandomCapa(int _randomCapa){
			randomCapa = _randomCapa;
		}
		
		public static void storeInterestCapa(int _interestCapa){
			interestCapa = _interestCapa;
		}
		
		public static int getOnlineCount(){
			return onlineCount;
		}
		
		public static int getRandomCount(){
			return randomCount;
		}
		
		public static int getInterestCount(){
			return interestCount;
		}
		
		public static int getChatCount(){
			return chatCount;
		}
		
		public static int getRandomChatCount(){
			return randomChatCount;
		}
		
		public static int getInterestChatCount(){
			return interestChatCount;
		}
		
		public static int getManagementData(){
			return managementData;
		}
		
		public static int getOnlineCapa(){
			return onlineCapa;
		}
		
		public static int getRandomCapa(){
			return randomCapa;
		}
		
		public static int getInterestCapa(){
			return interestCapa;
		}
	}

	// ===============================================================================
	// Information of user device card
	// ===============================================================================	
	public static class DeviceCard{		
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
	
	// ===============================================================================
	// Information of user interest labels and impression labels
	// ===============================================================================		
	public static class InterestLabel{
		public static ArrayList<InterestLabelMap> mCurrHotIntLabels = new ArrayList<InterestLabelMap>();
		public static ArrayList<InterestLabelMap> mMyIntLabels = new ArrayList<InterestLabelMap>();
		
		public static void resetCurrHotLabelList(){
			mCurrHotIntLabels.clear();
		}
		
		public static int getCurrHotLabelNum(){
			return mCurrHotIntLabels.size();
		}
		
		public static void putCurrHotLabel(InterestLabelMap _label){
			mCurrHotIntLabels.add(_label);
		}
		
		public static InterestLabelMap getCurrHotIntLabel(int _index){
			return mCurrHotIntLabels.get(_index);			
		}		
		
		public static void resetMyIntLabelList(){
			mMyIntLabels.clear();
		}
		
		public static int getMyIntLabelNum(){
			return mMyIntLabels.size();
		}
		
		public static InterestLabelMap getMyIntLabel(int _index){
			//if (null != mMyIntLabels.get(_index))
				return mMyIntLabels.get(_index);
		}
		
		public static void putMyIntLabel(InterestLabelMap _label){
			mMyIntLabels.add(_label);
		}
		
		public static void replaceMyIntLabel(int _index, InterestLabelMap _newLabel){					
			mMyIntLabels.set(_index, _newLabel);
		}
		
		public static void deleteMyIntLabel(int _index){					
			mMyIntLabels.remove(_index);
			int tLabelNum = mMyIntLabels.size();
			for(int i=0; i<tLabelNum; i++)
			{
				mMyIntLabels.get(i).setLabelOrder(i+1);
			}
		}
		
		public static boolean isPersonalIntLabelsNotDefined(){
			return (mMyIntLabels.size() <= 0) ? true : false;
		}
		
	}
	
	public static class ImpressionLabel{
		public static ArrayList<ImpressLabelMap> mMyImpressionLabels = new  ArrayList<ImpressLabelMap>();
		
		public static void resetMyImpLabels(){
			mMyImpressionLabels.clear();
		}
		
		public static int getMyImpLabelNum(){
			return mMyImpressionLabels.size();
		}
		
		public static void putMyImpLabelMap(ImpressLabelMap _impLabel){
			mMyImpressionLabels.add(_impLabel);			
		}
		
		public static ImpressLabelMap getMyImpLabelMap(int _index){
			return mMyImpressionLabels.get(_index);
		}		
	}
	
	public static class AssessLabel {
		public static ImpressLabelMap mHappyLabel = new ImpressLabelMap(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_HAPPY,0,0,"");
		public static ImpressLabelMap mSoSoLabel = new ImpressLabelMap(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_SOSO,0,0,"");
		public static ImpressLabelMap mDigustingLabel = new ImpressLabelMap(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_DISGUSTING,0,0,"");
	}
	
	// ===============================================================================
	// Information of business session related
	// ===============================================================================
	public static class BusinessSession{
		public static int mBusinessSessionId;
		public static int mBusinessType = RenHaiDefinitions.RENHAI_BUSINESS_TYPE_INTEREST;
		
		public static void setBusinessSessionId(int _id){
			mBusinessSessionId = _id;
		}
		
		public static int getBusinessSessionId(){
			return mBusinessSessionId;
		}
		
		public static void setBusinessType(int _type){
			mBusinessType = _type;
		}
		
		public static int getBusinessType(){
			return mBusinessType;
		}
	}

}

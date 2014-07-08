/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  PeerDeviceInfo.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android.data;

import java.util.ArrayList;

import com.simplelife.renhai.android.RenHaiDefinitions;

public class PeerDeviceInfo {
	public static int deviceId = 0;		
	public static String deviceSn = "";
	public static String chatMsg = "";
	
	public static void resetPeerDeviceInfo(){
		deviceId = 0;
		deviceSn = "";
		chatMsg  = "";
		DeviceCard.resetDeviceCardInfo();
		Profile.resetProfileInfo();
		InterestLabel.resetPeerIntLabelList();
		ImpressionLabel.resetPeerImpLabels();
		AssessLabel.resetAssessLabels();
		AssessResult.resetAssessImpLabels();
		
	}
	
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
	
	public static void storeChatMsg(String _msg){
		chatMsg = _msg;
	}
	
	public static String getChatMsg(){
		return chatMsg;
	}
	
	public static class DeviceCard {
		public static int deviceCardId = 0;
		public static String registerTime = "";
		public static String deviceModel = "";
		public static String osVersion = "";
		public static String appVersion = "";
		public static String location = "";
		
		// "Jail" is not an option for android, while "root"
		// is another thing. This field is not mandatory for
		// android devices, hence is set to false by default
		public static int isJailed = 0;
		
		public static void resetDeviceCardInfo(){
			deviceCardId = 0;
			registerTime = "";
			deviceModel = "";
			osVersion = "";
			appVersion = "";
			location = "";
		}
		
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
		public static String lastActivityTime = "";
		public static String createTime = "2013-01-01 10:00:00";
		public static int active = 1;
		public static int interestCardId = 0;
		public static int impressCardId = 0;
		public static int chatTotalCount = 0;
		public static int chatTotalDuration = 0;
		public static int chatLossCount = 0;
		
		public static void resetProfileInfo(){
			profileId = 0;
			serviceStatus = 0;
			unbanDate = 0;
			lastActivityTime = "";
			createTime = "2013-01-01 10:00:00";
			active = 1;
			interestCardId = 0;
			impressCardId = 0;
			chatTotalCount = 0;
			chatTotalDuration = 0;
			chatLossCount = 0;
		}
		
		public static void storeProfileId(int _id){
			profileId = _id;
		}
		
		public static void storeServiceStatus(int _status){
			serviceStatus = _status;
		}
		
		public static void storeUnbanDate(long _unbanDate){
			unbanDate = _unbanDate;
		}
		
		public static void storeLastActiveTime(String _lastActiveTime){
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
		
		public static String getLastActiveTime(){
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
		public static ArrayList<InterestLabelMap> mPeerIntLabels = new ArrayList<InterestLabelMap>();		
		
		public static void resetPeerIntLabelList(){
			mPeerIntLabels.clear();
		}
		
		public static int getPeerIntLabelNum(){
			return mPeerIntLabels.size();
		}
		
		public static InterestLabelMap getPeerIntLabel(int _index){
			//if (null != mMyIntLabels.get(_index))
				return mPeerIntLabels.get(_index);
		}
		
		public static void putPeerIntLabel(InterestLabelMap _label){
			mPeerIntLabels.add(_label);
		}
		
		public static void replacePeerIntLabel(int _index, InterestLabelMap _newLabel){					
			mPeerIntLabels.set(_index, _newLabel);
		}
		
		public static void deletePeerIntLabel(int _index){					
			mPeerIntLabels.remove(_index);
		}
		
		public static boolean isPersonalIntLabelsNotDefined(){
			return (mPeerIntLabels.size() <= 0) ? true : false;
		}		
	}
	
	public static class ImpressionLabel{
		public static ArrayList<ImpressLabelMap> mPeerImpressionLabels = new  ArrayList<ImpressLabelMap>();
		
		public static void resetPeerImpLabels(){
			mPeerImpressionLabels.clear();
		}
		
		public static int getPeerImpLabelNum(){
			return mPeerImpressionLabels.size();
		}
		
		public static void putPeerImpLabelMap(ImpressLabelMap _impLabel){
			mPeerImpressionLabels.add(_impLabel);			
		}
		
		public static ImpressLabelMap getPeerImpLabelMap(int _index){
			return mPeerImpressionLabels.get(_index);
		}		
	}

	// ===============================================================================
	// Information of assess labels
	// ===============================================================================	
	public static class AssessLabel {
		public static ImpressLabelMap mHappyLabel = new ImpressLabelMap(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_HAPPY,0,0,"");
		public static ImpressLabelMap mSoSoLabel = new ImpressLabelMap(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_SOSO,0,0,"");
		public static ImpressLabelMap mDigustingLabel = new ImpressLabelMap(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_DISGUSTING,0,0,"");
		
		public static void resetAssessLabels(){
			mHappyLabel.reset(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_HAPPY,0,0,"");
			mSoSoLabel.reset(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_SOSO,0,0,"");
			mDigustingLabel.reset(0, RenHaiDefinitions.RENHAI_IMPRESSIONLABEL_ASSESS_DISGUSTING,0,0,"");
		}
	}
	
	// ===============================================================================
	// Assess result after the chat
	// ===============================================================================
	public static class AssessResult {
		public static String mAssessment;		
        public static ArrayList<ImpressLabelMap> mAssessImpLabels = new  ArrayList<ImpressLabelMap>();
		
		public static void resetAssessImpLabels(){
			mAssessImpLabels.clear();
		}
		
		public static int getPeerAssessLabelNum(){
			return mAssessImpLabels.size();
		}
		
		public static void putAssessImpLabelMap(ImpressLabelMap _impLabel){
			mAssessImpLabels.add(_impLabel);			
		}
		
		public static ImpressLabelMap getAssessImpLabelMap(int _index){
			return mAssessImpLabels.get(_index);
		}
		
		public static void deleteImpLabel(int _index){					
			mAssessImpLabels.remove(_index);
		}
		
		public static void setAssessment(String _assess){
			mAssessment = _assess;
		}
		
		public static String getAssessment(){
			return mAssessment;
		}
	}
}

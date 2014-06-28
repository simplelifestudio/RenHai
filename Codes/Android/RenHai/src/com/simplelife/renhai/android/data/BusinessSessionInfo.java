/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  BusinessSessionInfo.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android.data;

import com.simplelife.renhai.android.RenHaiDefinitions;

public class BusinessSessionInfo {
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
	
	public static class MatchedCondition {
		public static int mGlbIntLabelId;
		public static int mMatchCount;
		public static String mLabelName;
		
		public static void setGlbIntLabelId(int _id){
			mGlbIntLabelId = _id;
		}
		
		public static void setMatchCount(int _count){
			mMatchCount = _count;
		}
		
		public static void setMatchLabelName(String _name){
			mLabelName = _name;
		}
		
		public static int getGlbIntLabelId(){
			return mGlbIntLabelId;
		}
		
		public static int getMatchCount(){
			return mMatchCount;
		}
		
		public static String setMatchLabelName(){
			return mLabelName;
		}
		
	}

}

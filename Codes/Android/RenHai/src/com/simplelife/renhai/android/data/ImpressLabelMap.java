/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  ImpressLabelMap.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android.data;

public class ImpressLabelMap{
	public int globalImpressLabelId;
	public String impressLabelName;
	public int assessedCount;
	public int updateTime;
	public int assessCount;
	
	public ImpressLabelMap(){
		globalImpressLabelId = 0;
		impressLabelName = null;
		assessedCount = 0;
		updateTime    = 0;
		assessCount   = 0;
	}
	
	public void setGlobalImpLabelId(int _id){
		globalImpressLabelId = _id;
	}
	
	public void setImpLabelName(String _name){
		impressLabelName = _name;
	}
	
	public void setAssessedCount(int _count){
		assessedCount = _count;
	}
	
	public void setUpdateTime(int _time){
		updateTime = _time;
	}
	
	public void setAssessCount(int _count){
		assessCount = _count;
	}
	
	public int getGlobalImpLabelId(){
		return globalImpressLabelId;
	}
	
	public String getImpLabelName(){
		return impressLabelName;
	}
	
	public int getAssessedCount(){
		return assessedCount;
	}
	
	public int getUpdateTime(){
		return updateTime;
	}
	
	public int setAssessCount(){
		return assessCount;
	}
}

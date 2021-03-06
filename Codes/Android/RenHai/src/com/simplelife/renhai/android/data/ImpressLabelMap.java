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
	public String updateTime;
	public int assessCount;
	
	public ImpressLabelMap(){
		globalImpressLabelId = 0;
		impressLabelName = null;
		assessedCount = 0;
		updateTime    = "";
		assessCount   = 0;
	}
	
	public ImpressLabelMap(int _glbid, String _name, int _assessCnt, int _assessedCnt, String _updateTime){
		globalImpressLabelId = _glbid;
		impressLabelName = _name;
		assessedCount = _assessedCnt;
		updateTime    = _updateTime;
		assessCount   = _assessCnt;
	}
	
	public void reset(int _glbid, String _name, int _assessCnt, int _assessedCnt, String _updateTime){
		globalImpressLabelId = _glbid;
		impressLabelName = _name;
		assessedCount = _assessedCnt;
		updateTime    = _updateTime;
		assessCount   = _assessCnt;
	}
	
	public void reset(){
		globalImpressLabelId = 0;
		impressLabelName = null;
		assessedCount = 0;
		updateTime    = "";
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
	
	public void setUpdateTime(String _time){
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
	
	public String getUpdateTime(){
		return updateTime;
	}
	
	public int getAssessCount(){
		return assessCount;
	}
}

/*
 *  Copyright (C) 2014 SimpleLife Studio All Rights Reserved
 *  
 *  InterestLabelMap.java
 *  RenHai
 *
 *  Created by Chris Li on 14-6-11. 
 */
package com.simplelife.renhai.android.structure;

public class InterestLabelMap{
	public int globalInterestLabelId;
	public String interestLabelName;
	public int globalMatchCount;
	public int labelOrder;
	public int matchCount;
	public int currentProfileCount;
	public boolean validFlag;
	public boolean isNewlyCreated;
	
	public InterestLabelMap(){
		globalInterestLabelId = 0;
		interestLabelName  = null;
		globalMatchCount = 0;
		matchCount = 0;
		labelOrder = 0;
		validFlag  = false;
		isNewlyCreated = true;
	}
	
	public void reset(){
		globalInterestLabelId = 0;
		interestLabelName  = null;
		globalMatchCount = 0;
		matchCount = 0;
		labelOrder = 0;
		validFlag  = false;
		isNewlyCreated = true;
	}
	
	public void setGlobalIntLabelId(int _id){
		globalInterestLabelId = _id;
	}
	
	public void setIntLabelName(String _labelName){
		interestLabelName = _labelName;
	}
	
	public void setGlobalMatchCount(int _glbMatchCount){
		globalMatchCount = _glbMatchCount;
	}
	
	public void setCurrentProfileCount(int _count){
		currentProfileCount = _count;
	}
	
	public void setMatchCount(int _matchCount){
		matchCount = _matchCount;
	}
	
	public void setLabelOrder(int _labelOrder){
		labelOrder = _labelOrder;
	}
	
	public void setValidFlag(boolean _flag){
		validFlag = _flag;
	}
	
	public void setNewlyCreatedFlag(boolean _flag){
		isNewlyCreated = _flag;
	}
	
	public int getGlobalIntLabelId(){
		return globalInterestLabelId;
	}
	
	public String getIntLabelName(){
		return interestLabelName;
	}
	
	public int getGlobalMatchCount(){
		return globalMatchCount;
	}
	
	public int getMatchCount(){
		return matchCount;
	}
	
	public int getCurrentProfileCount(){
		return currentProfileCount;
	}
	
	public int getLabelOrder(){
		return labelOrder;
	}
	
	public boolean getValidFlag(){
		return validFlag;
	}
	
	public boolean getNewlyCreatedFlag(){
		return isNewlyCreated;
	}
	
}

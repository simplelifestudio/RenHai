/**
 * InterestBusinessDevicePool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import java.util.HashMap;
import java.util.LinkedList;

import com.simplelife.renhai.server.business.device.AbstractLabel;
import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class InterestBusinessDevicePool extends AbstractBusinessDevicePool
{
    /** */
    private HashMap<String, LinkedList> interestLabelMap;
    
    
    public InterestBusinessDevicePool()
    {
    	businessType = Consts.BusinessType.Interest;
    }
    
    /** */
    public LinkedList<AbstractLabel> getHotInterestLabel(int count)
    {
    	LinkedList<AbstractLabel> labelList = new LinkedList<AbstractLabel>();
    	if (count <= 0)
    	{
    		return labelList;
    	}
    	
    	// TODO: 
        return labelList;
    }
    
    /** */
    public LinkedList<AbstractLabel> getHistoryHotInterestLabel(int count)
    {
    	// TODO:
    	LinkedList<AbstractLabel> labelList = new LinkedList<AbstractLabel>();
    	if (count <= 0)
    	{
    		return labelList;
    	}
    	
    	// TODO: 
        return labelList;
    }
    
    /** */
    public boolean onDeviceEnter(IDeviceWrapper device)
    {
        return false;
    }
    
    /** */
    public void onDeviceLeave(IDeviceWrapper device)
    {
    }

	@Override
	public void clearPool()
	{
		deviceMap.clear();
		interestLabelMap.clear();
	}
}

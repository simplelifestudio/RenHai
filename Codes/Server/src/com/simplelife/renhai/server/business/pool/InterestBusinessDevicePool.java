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
import com.simplelife.renhai.server.util.IDeviceWrapper;


/** */
public class InterestBusinessDevicePool extends AbstractBusinessDevicePool
{
    /** */
    private HashMap<String, LinkedList> interestLabelMap;
    
    /** */
    public LinkedList<AbstractLabel> getHotInterestLabel(Object count)
    {
        return null;
    
    }
    
    /** */
    public LinkedList<AbstractLabel> getHistoryHotInterestLabel(int count)
    {
        return null;
    
    }
    
    /** */
    public boolean isPoolFull()
    {
        return false;
    }
    
    /** */
    public void updateCount()
    {
    }
    
    /** */
    public int getElementCount()
    {
        return 0;
    }
    
    /** */
    public boolean deviceEnter(IDeviceWrapper device)
    {
        return false;
    }
    
    /** */
    public void deviceLeave(IDeviceWrapper device)
    {
    }
}

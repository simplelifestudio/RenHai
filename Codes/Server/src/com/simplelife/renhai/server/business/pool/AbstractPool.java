/**
 * AbstractPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

import com.simplelife.renhai.server.util.GlobalSetting;
import com.simplelife.renhai.server.util.IPool;


/** */
public abstract class AbstractPool implements IPool
{
    /** */
    protected int capacity;
    
    /** */
    public int getCapacity()
    {
        return capacity;
    }
    
    /** */
    public void setCapacity(int newCapacity)
    {
    	this.capacity = newCapacity;
    }
    
    /** */
    public abstract boolean isPoolFull();
    
    /** */
    public abstract int getElementCount();
}

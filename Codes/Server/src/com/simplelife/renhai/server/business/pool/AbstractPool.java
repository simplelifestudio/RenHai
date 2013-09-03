/**
 * AbstractPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.business.pool;

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
}

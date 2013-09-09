/**
 * IPool.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;


/** */
public interface IPool
{
    /** */
    public boolean isPoolFull();

    /** */
    public int getCapacity();
    
    /** */
    public int getElementCount();
    
    public void clearPool();
}

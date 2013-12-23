/**
 * IModule.java
 * 
 * History:
 *     2013-8-28: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */



package com.simplelife.renhai.server.util;


/** */
public interface IModule
{
    /** */
    public void initModule();
    
    /** */
    public void startService();
    
    /** */
    public void processService();
    
    /** */
    public void pauseService();
    
    /** */
    public void stopService();
    
    /** */
    public void releaseModule();
    
    /** */
    public void serviceWithIndividualThread();
    
    /** */
    public void serviceWithCallingThread();
}

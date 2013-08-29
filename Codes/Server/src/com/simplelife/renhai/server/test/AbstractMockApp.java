
/**
 * AbstractMockApp.java
 * 
 * History:
 *     2013-8-29: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */


package com.simplelife.renhai.server.test;

import java.util.HashMap;

import com.simplelife.renhai.server.business.device.Device;
import com.simplelife.renhai.server.util.IMockApp;

/** */
public abstract class AbstractMockApp implements IMockApp
{
    /** */
    protected int sessionId;
    
    /** */
    protected Device device;
    
    protected int peerDeviceId;
    
    HashMap<String, Object> jsonMap;
    HashMap<String, Object> jsonMapHeader;
    HashMap<String, Object> jsonMapBody;
}

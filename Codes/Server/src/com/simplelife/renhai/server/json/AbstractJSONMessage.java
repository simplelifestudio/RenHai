/**
 * AbstractJSONMessage.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public abstract class AbstractJSONMessage
{
	protected IDeviceWrapper deviceWrapper;
	
    public abstract Consts.MessageType getMessageType();

    public abstract String getMessageSn();
    
    public abstract Consts.MessageId getMessageId();
}

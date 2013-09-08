/**
 * BroadcastNotification.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;

/**
 * 
 */
public class BroadcastNotification extends ServerJSONMessage
{
	public BroadcastNotification()
	{
		super(null);
		setMessageType(Consts.MessageType.ServerNotification);
	}
	
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.BroadcastNotification;
	}
}


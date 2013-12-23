/**
 * BusinessSessionNotification.java
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
public class BusinessSessionNotification extends ServerJSONMessage
{
	public BusinessSessionNotification(AppJSONMessage request)
	{
		super(request);
		setMessageType(Consts.MessageType.ServerNotification);
		setMessageId(Consts.MessageId.BusinessSessionNotification);
	}
	
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.BusinessSessionNotification;
	}
	
	@Override
	public boolean isSyncMessage()
    {
    	return true;
    }
}

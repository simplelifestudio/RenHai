/**
 * AlohaResponse.java
 * 
 * History:
 *     2013-9-5: Tomas Chen, initial version
 * 
 * Copyright (c) 2013 SimpleLife Studio. All rights reserved.
 */

package com.simplelife.renhai.server.json;

import com.simplelife.renhai.server.util.Consts;
import com.simplelife.renhai.server.util.Consts.MessageId;
import com.simplelife.renhai.server.util.IDeviceWrapper;

/**
 * 
 */
public class AlohaResponse extends ServerJSONMessage
{
	public AlohaResponse(AppJSONMessage request)
	{
		super(request);
		setMessageType(Consts.MessageType.ServerResponse);
	}

	/* (non-Javadoc)
	 * @see com.simplelife.renhai.server.json.ServerJSONMessage#getMessageId()
	 */
	@Override
	public MessageId getMessageId()
	{
		return Consts.MessageId.AlohaResponse;
	}
}
